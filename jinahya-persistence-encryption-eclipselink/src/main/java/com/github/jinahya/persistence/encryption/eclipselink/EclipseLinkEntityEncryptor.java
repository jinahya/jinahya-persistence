package com.github.jinahya.persistence.encryption.eclipselink;

import com.github.jinahya.persistence.encryption.EntityEncryptor;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.mappings.AggregateObjectMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Encrypts and decrypts entities through EclipseLink's own write and read paths: descriptor events of the unit of
 * work.
 * <ul>
 *   <li>Before an {@code INSERT} — when the unit of work calculates its changes — the ciphertexts are computed into the
 *       entity. The plaintext columns are non-insertable, so the row never carries them.</li>
 *   <li>Before an {@code UPDATE} of an entity which has changes, the ciphertexts which changed against the backup clone
 *       are computed into the entity; EclipseLink then calculates the changes again. When the row is built, each
 *       plaintext column the mapping nulls is set to {@code null} in it.</li>
 *   <li>After an entity is cloned into the unit of work, or refreshed — directly, or by a merge from the shared cache
 *       — it is decrypted, and its backup clone, if already taken, is given copies of the decrypted plaintexts, so
 *       that a read never makes the entity changed.</li>
 * </ul>
 * The entity's plaintexts are never touched; nor is an object outside a unit of work, such as one in the shared cache.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class EclipseLinkEntityEncryptor extends DescriptorEventAdapter {

    /**
     * What this engine needs of a descriptor.
     *
     * @param plaintexts the mappings of the attributes holding a plaintext, directly or inside an aggregate.
     * @param nulled     the fields of the plaintexts the mapping nulls.
     */
    private record Encryption(List<DatabaseMapping> plaintexts, List<DatabaseField> nulled) {

        private static final Encryption NONE = new Encryption(List.of(), List.of());

        boolean isNone() {
            return plaintexts.isEmpty();
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance.
     *
     * @param encryptor the encryptor to encrypt and decrypt with.
     */
    EclipseLinkEntityEncryptor(final EntityEncryptor encryptor) {
        super();
        this.encryptor = Objects.requireNonNull(encryptor, "encryptor is null");
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns what this engine needs of the specified descriptor, resolving it on first use, once the descriptor is
     * initialized.
     *
     * @param descriptor the descriptor.
     * @return the encryption of the {@code descriptor}; {@link Encryption#NONE} when it has nothing encrypted.
     * @throws IllegalStateException when the descriptor tracks attribute changes.
     */
    private Encryption encryption(final ClassDescriptor descriptor) {
        return encryptions.computeIfAbsent(descriptor, d -> {
            final var javaClass = d.getJavaClass();
            if (!encryptor.hasEncryptedAttributes(javaClass)) {
                return Encryption.NONE;
            }
            if (d.getObjectChangePolicy().isAttributeChangeTrackingPolicy()) {
                throw new IllegalStateException(
                        "attribute change tracking is not supported; disable it, with"
                        + " eclipselink.weaving.changetracking=false, for " + javaClass.getName());
            }
            final var plaintexts = new ArrayList<DatabaseMapping>();
            for (final var name : encryptor.getPlaintextAttributeNames(javaClass)) {
                plaintexts.add(mapping(d, name));
            }
            final var nulled = new ArrayList<DatabaseField>();
            for (final var path : encryptor.getNulledPlaintextPaths(javaClass)) {
                var owner = d;
                for (final var name : path.subList(0, path.size() - 1)) {
                    if (!(mapping(owner, name) instanceof AggregateObjectMapping aggregate)) {
                        throw new IllegalStateException("not an aggregate: " + name + " of " + owner.getJavaClassName());
                    }
                    // a descriptor cloned for this very mapping, its fields translated by the overrides in scope
                    owner = aggregate.getReferenceDescriptor();
                }
                nulled.add(mapping(owner, path.get(path.size() - 1)).getField());
            }
            return new Encryption(List.copyOf(plaintexts), List.copyOf(nulled));
        });
    }

    private static DatabaseMapping mapping(final ClassDescriptor descriptor, final String attributeName) {
        final var mapping = descriptor.getObjectBuilder().getMappingForAttributeName(attributeName);
        if (mapping == null) {
            throw new IllegalStateException(
                    "no mapping for '" + attributeName + "' in " + descriptor.getJavaClassName());
        }
        return mapping;
    }

    /**
     * Copies each plaintext attribute of the specified working clone into its backup clone, if it has one yet.
     */
    private static void resetBackup(final Object clone, final UnitOfWorkImpl unitOfWork, final Encryption encryption) {
        final var backup = unitOfWork.getCloneMapping().get(clone);
        if (backup == null || backup == clone) {
            // no backup yet: it is taken from the clone, decrypted, afterwards
            return;
        }
        for (final var mapping : encryption.plaintexts()) {
            mapping.buildBackupClone(clone, backup, unitOfWork);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public void preInsert(final DescriptorEvent event) {
        if (!encryption(event.getDescriptor()).isNone()) {
            encryptor.encrypt(event.getObject(), null);
        }
    }

    @Override
    public void preUpdateWithChanges(final DescriptorEvent event) {
        final var descriptor = event.getDescriptor();
        if (encryption(descriptor).isNone() || !(event.getSession() instanceof UnitOfWorkImpl unitOfWork)) {
            return;
        }
        final var entity = event.getObject();
        final var backup = unitOfWork.getCloneMapping().get(entity);
        encryptor.encrypt(entity, backup == null || backup == entity
                ? null
                : name -> mapping(descriptor, name).getAttributeValueFromObject(backup));
    }

    @Override
    public void aboutToUpdate(final DescriptorEvent event) {
        final var encryption = encryption(event.getDescriptor());
        if (encryption.isNone()) {
            return;
        }
        // the row is what is written; the entity keeps its plaintexts. A legacy row is migrated by any UPDATE
        final var row = (AbstractRecord) event.getRecord();
        for (final var field : encryption.nulled()) {
            row.put(field, null);
        }
    }

    @Override
    public void postClone(final DescriptorEvent event) {
        decrypt(event);
    }

    @Override
    public void postRefresh(final DescriptorEvent event) {
        decrypt(event);
    }

    /**
     * Decrypts a working clone a refresh, under the shared cache, merged the refreshed original into.
     *
     * @param event the event.
     * @implNote The same event follows the merge of a detached instance into its working clone, which carries
     *         the application's plaintexts, not the row's; and the merge of a working clone's changes into the shared
     *         cache, whose object stays as the row is. Only a merge from the shared cache's own original is decrypted.
     */
    @Override
    public void postMerge(final DescriptorEvent event) {
        final var descriptor = event.getDescriptor();
        final var encryption = encryption(descriptor);
        if (encryption.isNone() || !(event.getSession() instanceof UnitOfWorkImpl unitOfWork)) {
            return;
        }
        final var clone = event.getObject();
        final var source = event.getOriginalObject();
        if (source == null || source == clone || !unitOfWork.getCloneMapping().containsKey(clone)) {
            return;
        }
        final var parent = unitOfWork.getParentIdentityMapSession(descriptor, false, false);
        final var primaryKey = descriptor.getObjectBuilder().extractPrimaryKeyFromObject(source, unitOfWork);
        if (primaryKey == null || parent.getIdentityMapAccessorInstance()
                                          .getFromIdentityMap(primaryKey, descriptor.getJavaClass(), descriptor)
                                  != source) {
            return;
        }
        encryptor.decrypt(clone);
        resetBackup(clone, unitOfWork, encryption);
    }

    private void decrypt(final DescriptorEvent event) {
        final var encryption = encryption(event.getDescriptor());
        if (encryption.isNone() || !(event.getSession() instanceof UnitOfWorkImpl unitOfWork)) {
            // an object outside a unit of work, such as the shared cache's, keeps what the row holds
            return;
        }
        final var entity = event.getObject();
        encryptor.decrypt(entity);
        resetBackup(entity, unitOfWork, encryption);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final EntityEncryptor encryptor;

    private final Map<ClassDescriptor, Encryption> encryptions = new ConcurrentHashMap<>();
}
