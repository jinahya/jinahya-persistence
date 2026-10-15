package com.github.jinahya.persistence.encryption;

import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * The engine a persistence provider integration encrypts and decrypts entities with; the service provider interface of
 * this module, for integrations only.
 * <p>
 * An integration hooks into the provider's own write and read paths, instead of the entity life cycle callbacks, so
 * that a plaintext is never nulled in the entity, and a read never writes. It encrypts into the entity against the
 * provider's snapshot ({@link #encrypt(Object, Snapshot)}), writes
 * {@link #clearPlaintexts(Class, String, Object) cleared} copies of the
 * {@link #getPlaintextAttributeNames(Class) plaintext attributes} to the row, and {@link #decrypt(Object) decrypts}
 * after a load. An application never uses this type: it maps {@link EncryptedAttribute encrypted attributes}, and
 * provides an {@link EntityEncryptionService}.
 * <p>
 * An engine is thread-safe, and is meant to live exactly as long as its entity manager factory.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see EntityEncryptionService
 */
public interface EntityEncryptionEngine {

    /**
     * The name of the persistence unit property a persistence provider integration reads its
     * {@link EntityEncryptionService encryption service} from. The value is an {@link EntityEncryptionService}
     * instance, its {@link Class}, or the name of its class; a class has to have a public no-arg constructor.
     *
     * @see #getEncryptionService(Object)
     */
    String PROPERTY_ENCRYPTION_SERVICE = "jinahya.persistence.encryption.service";

    /**
     * Returns the encryption service the specified value of {@link #PROPERTY_ENCRYPTION_SERVICE} stands for.
     *
     * @param value the value of the property: an {@link EntityEncryptionService}, its {@link Class}, or the name of its
     *              class.
     * @return the encryption service; a new instance when the {@code value} is a class, or a class name.
     * @throws IllegalArgumentException when the {@code value} is {@code null}, or none of the above.
     */
    static EntityEncryptionService getEncryptionService(final @org.jspecify.annotations.Nullable Object value) {
        if (value instanceof EntityEncryptionService service) {
            return service;
        }
        final Class<?> type;
        if (value instanceof Class<?> c) {
            type = c;
        } else if (value instanceof String name && !name.isBlank()) {
            try {
                type = Class.forName(name.strip(), true, Thread.currentThread().getContextClassLoader());
            } catch (final ClassNotFoundException cnfe) {
                throw new IllegalArgumentException("no class named " + name + " for " + PROPERTY_ENCRYPTION_SERVICE,
                                                   cnfe);
            }
        } else {
            throw new IllegalArgumentException(
                    PROPERTY_ENCRYPTION_SERVICE + " has to be an EntityEncryptionService, its class, or the name of its"
                    + " class; found " + (value == null ? "nothing" : value.getClass().getName()));
        }
        if (!EntityEncryptionService.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException(type.getName() + " is not an EntityEncryptionService");
        }
        try {
            return (EntityEncryptionService) type.getConstructor().newInstance();
        } catch (final ReflectiveOperationException roe) {
            throw new IllegalArgumentException("failed to instantiate " + type.getName(), roe);
        }
    }

    /**
     * Returns a new engine working through the specified encryption service, which also names the persistence unit: its
     * {@link EntityEncryptionService#getEntityManagerFactory() entity manager factory} is asked for on first use.
     *
     * @param encryptionService the encryption service; must not be {@code null}.
     * @return a new engine.
     */
    static EntityEncryptionEngine of(final EntityEncryptionService encryptionService) {
        return new EntityEncryptor(Objects.requireNonNull(encryptionService, "encryptionService is null"));
    }

    /**
     * Returns a new engine for the specified entity manager factory, working through the specified encryption service,
     * whose own {@link EntityEncryptionService#getEntityManagerFactory() entity manager factory} is then never asked
     * for.
     *
     * @param entityManagerFactory the entity manager factory whose entities are encrypted; must not be {@code null}.
     * @param encryptionService    the encryption service; must not be {@code null}.
     * @return a new engine.
     */
    static EntityEncryptionEngine of(final EntityManagerFactory entityManagerFactory,
                                     final EntityEncryptionService encryptionService) {
        return new EntityEncryptor(entityManagerFactory, encryptionService);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The values an entity instance's attributes had when it was last read or written, as the persistence provider
     * keeps them for checking the instance for changes.
     *
     * @see #encrypt(Object, Snapshot)
     */
    @FunctionalInterface
    interface Snapshot {

        /**
         * Returns the value the specified attribute had.
         *
         * @param attributeName the name of an attribute of the entity class; for an embedded attribute, the value is an
         *                      instance of its embeddable.
         * @return the value; may be {@code null}.
         */
        @org.jspecify.annotations.Nullable
        Object get(String attributeName);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Encrypts, in place, each plaintext of the specified entity instance which differs from the specified snapshot,
     * storing its ciphertext; a plaintext is never cleared.
     * <p>
     * A plaintext is encrypted again only when it encodes differently from the snapshot's, or when the ciphertext the
     * instance holds is not the snapshot's, which includes a legacy row, whose ciphertext is {@code null}. A
     * {@code null} plaintext always yields a {@code null} ciphertext.
     *
     * @param entity   the entity instance to encrypt, in place; an instance of a class which has no encrypted attribute
     *                 passes through untouched.
     * @param snapshot the values the instance's attributes had when it was last read or written, by the name of an
     *                 attribute of the entity class; an embedded attribute maps to an instance of its embeddable, read
     *                 the same way. {@code null} when there is none, as before an {@code INSERT}: every plaintext is
     *                 then encrypted.
     * @throws RuntimeException when the mapping is invalid, or when a plaintext cannot be encoded.
     */
    void encrypt(Object entity, @org.jspecify.annotations.Nullable Snapshot snapshot);

    /**
     * Decrypts, in place, the encrypted attributes of the specified entity instance.
     *
     * @param entity the entity instance to decrypt, in place; an instance of a class which has no encrypted attribute
     *               passes through untouched.
     * @throws RuntimeException when the mapping is invalid, or when a value cannot be reconstructed from its bytes.
     */
    void decrypt(Object entity);

    /**
     * Returns whether the specified entity class has an {@link EncryptedAttribute encrypted attribute}, directly or
     * inside an embeddable.
     *
     * @param entityClass the entity class.
     * @return {@code true} when instances of the {@code entityClass} are encrypted.
     * @throws RuntimeException when the mapping of the {@code entityClass} is invalid.
     */
    boolean hasEncryptedAttributes(Class<?> entityClass);

    /**
     * Returns the names of the attributes of the specified entity class which hold a plaintext: a plaintext attribute
     * of the class itself, or an embedded attribute whose embeddable holds one, at any depth.
     *
     * @param entityClass the entity class.
     * @return an unmodifiable set of attribute names; empty when the {@code entityClass} is not encrypted.
     */
    Set<String> getPlaintextAttributeNames(Class<?> entityClass);

    /**
     * Returns the names of the attributes of the specified entity class itself which hold a ciphertext. Those inside an
     * embeddable are reached through its {@link #getPlaintextAttributeNames(Class) embedded attribute}.
     *
     * @param entityClass the entity class.
     * @return an unmodifiable set of attribute names; empty when the {@code entityClass} has none of its own.
     */
    Set<String> getCiphertextAttributeNames(Class<?> entityClass);

    /**
     * Returns the paths, from the specified entity class, of the plaintexts which are nulled: not primitive, and their
     * columns mapped nullable. A path is the names of the embedded attributes leading to the plaintext, if any,
     * followed by the plaintext's own name.
     *
     * @param entityClass the entity class.
     * @return an unmodifiable list of unmodifiable paths; empty when the {@code entityClass} has none.
     */
    List<List<String>> getNulledPlaintextPaths(Class<?> entityClass);

    /**
     * Returns the value to write for the specified attribute of the specified entity class: {@code null} for a
     * plaintext which is nulled — not primitive, and its column mapped nullable — and, for an embedded attribute, the
     * specified value with each such plaintext inside it, at any depth, set to {@code null}. Any other value is
     * returned as it is.
     *
     * @param entityClass   the entity class.
     * @param attributeName the name of an attribute of the {@code entityClass}.
     * @param value         the value of the attribute; for an embedded attribute, a <em>copy</em> of the instance's,
     *                      which is modified.
     * @return the value to write.
     */
    @org.jspecify.annotations.Nullable
    Object clearPlaintexts(Class<?> entityClass, String attributeName,
                           @org.jspecify.annotations.Nullable Object value);
}
