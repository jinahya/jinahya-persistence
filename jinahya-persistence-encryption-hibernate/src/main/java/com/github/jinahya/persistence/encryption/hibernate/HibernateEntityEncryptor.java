package com.github.jinahya.persistence.encryption.hibernate;

import com.github.jinahya.persistence.encryption.EntityEncryptionEngine;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.engine.spi.Status;
import org.hibernate.event.spi.FlushEntityEvent;
import org.hibernate.event.spi.PostLoadEvent;
import org.hibernate.event.spi.PostLoadEventListener;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;
import org.hibernate.event.spi.PreUpsertEvent;
import org.hibernate.event.spi.PreUpsertEventListener;
import org.hibernate.metamodel.RepresentationMode;
import org.hibernate.persister.entity.EntityPersister;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

/**
 * Encrypts and decrypts entities through Hibernate ORM's own write and read paths.
 * <ul>
 *   <li>Before an {@code INSERT}, the ciphertexts are computed into the entity, and copied into the state the row is
 *       written from. The plaintexts are left in that state: their columns are non-insertable, and the state becomes
 *       the entity's snapshot — for an {@code IDENTITY} insert, only once the post-insert events have run.</li>
 *   <li>When a dirty entity is flushed, the ciphertexts which changed are computed into the entity, and the state the
 *       {@code UPDATE} is built from gets a copy of each plaintext attribute, with its plaintexts cleared.</li>
 *   <li>After a load or an {@code UPDATE}, the snapshot the entity is dirty-checked against — the entry's loaded
 *       state — is set to copies of what the entity holds, its decrypted plaintexts included, so that neither a read
 *       nor a cleared plaintext makes the entity dirty.</li>
 *   <li>A {@code StatelessSession} is refused any write of an encrypted entity: it neither flushes nor fires load
 *       events, so its updates would carry the plaintext in the clear, and what it reads is never decrypted.</li>
 * </ul>
 * The entity's plaintexts are never touched.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class HibernateEntityEncryptor
        implements PreInsertEventListener, PreUpdateEventListener, PreUpsertEventListener, PostUpdateEventListener,
                   PostLoadEventListener {

    /**
     * The indices, in an entity's state, of the attributes this engine rewrites.
     *
     * @param plaintexts  the attributes holding a plaintext, directly or inside an embeddable, by index.
     * @param ciphertexts the attributes holding a ciphertext directly, by index.
     * @param indices     the index of every attribute in either, by name.
     */
    private record Indices(Map<Integer, String> plaintexts, Map<Integer, String> ciphertexts,
                           Map<String, Integer> indices) {

        private static final Indices NONE = new Indices(Map.of(), Map.of(), Map.of());

        boolean isEmpty() {
            return plaintexts.isEmpty() && ciphertexts.isEmpty();
        }

        IntStream all() {
            return IntStream.concat(plaintexts.keySet().stream().mapToInt(Integer::intValue),
                                    ciphertexts.keySet().stream().mapToInt(Integer::intValue));
        }
    }

    private static final Runnable NOTHING = () -> {
    };

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance.
     *
     * @param encryptor the encryptor to encrypt and decrypt with.
     */
    HibernateEntityEncryptor(final EntityEncryptionEngine encryptor) {
        super();
        this.encryptor = Objects.requireNonNull(encryptor, "encryptor is null");
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns the indices of the specified persister's encrypted attributes.
     *
     * @param persister the persister.
     * @return the indices; {@link Indices#NONE} when the entity is not encrypted.
     */
    private Indices indices(final EntityPersister persister) {
        return indices.computeIfAbsent(persister.getEntityName(), k -> {
            if (persister.getRepresentationStrategy().getMode() != RepresentationMode.POJO) {
                return Indices.NONE;
            }
            final var mappedClass = persister.getMappedClass();
            if (!encryptor.hasEncryptedAttributes(mappedClass)) {
                return Indices.NONE;
            }
            final var names = Arrays.asList(persister.getPropertyNames());
            final var plaintexts = new HashMap<Integer, String>();
            final var ciphertexts = new HashMap<Integer, String>();
            final var byName = new HashMap<String, Integer>();
            for (final var name : encryptor.getPlaintextAttributeNames(mappedClass)) {
                final var index = indexOf(names, name, persister);
                plaintexts.put(index, name);
                byName.put(name, index);
            }
            for (final var name : encryptor.getCiphertextAttributeNames(mappedClass)) {
                final var index = indexOf(names, name, persister);
                ciphertexts.put(index, name);
                byName.put(name, index);
            }
            return new Indices(Map.copyOf(plaintexts), Map.copyOf(ciphertexts), Map.copyOf(byName));
        });
    }

    private static int indexOf(final java.util.List<String> names, final String name,
                               final EntityPersister persister) {
        final var index = names.indexOf(name);
        if (index < 0) {
            throw new IllegalStateException("no property named '" + name + "' in " + persister.getEntityName());
        }
        return index;
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns an action which puts back the plaintext attributes the specified entity holds now.
     *
     * @param persister the persister of the {@code entity}.
     * @param entity    the entity.
     * @param indices   the indices of the {@code entity}'s encrypted attributes.
     * @return an action which restores the plaintext attributes.
     */
    private static Runnable capturePlaintexts(final EntityPersister persister, final Object entity,
                                              final Indices indices) {
        final var captured = new HashMap<Integer, Object>();
        for (final var index : indices.plaintexts().keySet()) {
            captured.put(index, persister.getValue(entity, index));
        }
        return () -> captured.forEach((index, value) -> {
            if (persister.getValue(entity, index) != value) {
                persister.setValue(entity, index, value);
            }
        });
    }

    /**
     * Encrypts the specified event's dirty entity, and writes the ciphertexts, and the cleared plaintexts, into the
     * state its {@code UPDATE} is built from, marking those which differ from the snapshot dirty.
     * <p>
     * That state is final by now, after the {@code @PreUpdate} callbacks and the interceptor: the encrypted
     * attributes are taken from it into the entity first, so that what an interceptor changed is what is encrypted.
     * Hibernate ORM may copy the state back into the entity, the cleared plaintexts included; the action returned
     * puts the plaintexts back as they are now.
     *
     * @param event the event.
     * @return an action which restores the entity's plaintexts; does nothing for an entity which is not encrypted.
     */
    Runnable encryptState(final FlushEntityEvent event) {
        final var entry = event.getEntityEntry();
        if (entry.getStatus() == Status.DELETED) {
            return NOTHING;
        }
        final var persister = entry.getPersister();
        final var indices = indices(persister);
        if (indices.isEmpty()) {
            return NOTHING;
        }
        final var entity = event.getEntity();
        final Object[] values = event.getPropertyValues();
        // the state is final: the @PreUpdate callbacks copied into it, and an interceptor may have changed it since
        indices.all().forEach(i -> {
            if (persister.getValue(entity, i) != values[i]) {
                persister.setValue(entity, i, values[i]);
            }
        });
        final var restoration = capturePlaintexts(persister, entity, indices);
        final Object[] loaded = entry.getLoadedState();
        encryptor.encrypt(entity, loaded == null ? null : name -> {
            final var index = indices.indices().get(name);
            return index == null ? null : loaded[index];
        });
        writeState(persister, entity, values, indices, true, event.getSession());
        if (loaded == null) {
            // no snapshot to check against; every column is written
            return restoration;
        }
        final var types = persister.getPropertyTypes();
        final var dirty = indices.all()
                .filter(i -> types[i].isDirty(loaded[i], values[i], event.getSession()))
                .toArray();
        if (dirty.length == 0) {
            return restoration;
        }
        final var previous = event.getDirtyProperties();
        event.setDirtyProperties(
                previous == null
                        ? dirty
                        : IntStream.concat(Arrays.stream(previous), Arrays.stream(dirty)).distinct().sorted().toArray()
        );
        return restoration;
    }

    /**
     * Writes, into the specified state, a copy of each encrypted attribute of the specified entity, each plaintext the
     * mapping nulls cleared when {@code clear} is {@code true}.
     */
    private void writeState(final EntityPersister persister, final Object entity,
                            final @org.jspecify.annotations.Nullable Object[] state, final Indices indices,
                            final boolean clear, final SharedSessionContractImplementor session) {
        final var types = persister.getPropertyTypes();
        final var factory = session.getFactory();
        final var mappedClass = persister.getMappedClass();
        indices.plaintexts().forEach((i, name) -> {
            final var copy = types[i].deepCopy(persister.getValue(entity, i), factory);
            state[i] = clear ? encryptor.clearPlaintexts(mappedClass, name, copy) : copy;
        });
        indices.ciphertexts().forEach((i, name) -> state[i] = types[i].deepCopy(persister.getValue(entity, i),
                                                                                factory));
    }

    /**
     * Sets the snapshot of the specified entity, for each encrypted attribute, to a copy of what the entity holds.
     */
    private void resetSnapshot(final EntityPersister persister, final Object entity, final Indices indices,
                               final SharedSessionContractImplementor session) {
        final var entry = session.getPersistenceContextInternal().getEntry(entity);
        final @org.jspecify.annotations.Nullable Object @org.jspecify.annotations.Nullable [] loaded =
                entry == null ? null : entry.getLoadedState();
        if (loaded == null) {
            return;
        }
        final var types = persister.getPropertyTypes();
        final var factory = session.getFactory();
        indices.all().forEach(i -> loaded[i] = types[i].deepCopy(persister.getValue(entity, i), factory));
    }

    /**
     * Refuses a write, by a session which is not an event source, of an entity which is encrypted.
     *
     * @param session   the session writing.
     * @param persister the persister of the entity written.
     * @param indices   the indices of the entity's encrypted attributes.
     * @throws IllegalStateException when the {@code session} is not an event source — a {@code StatelessSession} —
     *                               and the entity is encrypted.
     */
    private static void refuseStateless(final SharedSessionContractImplementor session,
                                        final EntityPersister persister, final Indices indices) {
        if (!indices.isEmpty() && !session.isEventSource()) {
            throw new IllegalStateException(
                    "a StatelessSession can't write " + persister.getEntityName() + ", which has encrypted attributes:"
                    + " it neither flushes nor fires load events, so an update would store the plaintext in the clear,"
                    + " and what it reads is never decrypted; use a Session (an EntityManager) instead");
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public boolean onPreInsert(final PreInsertEvent event) {
        final var persister = event.getPersister();
        final var indices = indices(persister);
        refuseStateless(event.getSession(), persister, indices);
        if (!indices.isEmpty()) {
            final var entity = event.getEntity();
            encryptor.encrypt(entity, null);
            writeState(persister, entity, event.getState(), indices, false, event.getSession());
        }
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @implNote A session's update is encrypted while it is flushed; only a {@code StatelessSession}'s, which is
     *         never flushed, reaches here unencrypted, and is refused.
     */
    @Override
    public boolean onPreUpdate(final PreUpdateEvent event) {
        refuseStateless(event.getSession(), event.getPersister(), indices(event.getPersister()));
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @implNote Only a {@code StatelessSession} upserts; it is refused.
     */
    @Override
    public boolean onPreUpsert(final PreUpsertEvent event) {
        refuseStateless(event.getSession(), event.getPersister(), indices(event.getPersister()));
        return false;
    }

    @Override
    public void onPostUpdate(final PostUpdateEvent event) {
        final var persister = event.getPersister();
        final var indices = indices(persister);
        if (!indices.isEmpty()) {
            resetSnapshot(persister, event.getEntity(), indices, event.getSession());
        }
    }

    @Override
    public void onPostLoad(final PostLoadEvent event) {
        final var persister = event.getPersister();
        final var indices = indices(persister);
        if (!indices.isEmpty()) {
            final var entity = event.getEntity();
            encryptor.decrypt(entity);
            resetSnapshot(persister, entity, indices, event.getSession());
        }
    }

    @Override
    public boolean requiresPostCommitHandling(final EntityPersister persister) {
        return false;
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final EntityEncryptionEngine encryptor;

    // by entity name
    private final Map<String, Indices> indices = new ConcurrentHashMap<>();
}
