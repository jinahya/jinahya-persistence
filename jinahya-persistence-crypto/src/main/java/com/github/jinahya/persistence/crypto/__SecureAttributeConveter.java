package com.github.jinahya.persistence.crypto;

import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;
import jakarta.persistence.AttributeConverter;
import org.jspecify.annotations.Nullable;

import java.lang.invoke.MethodHandles;
import java.util.Optional;

/**
 * An abstract {@link AttributeConverter} which encrypts an attribute value on its way to the database, and decrypts it
 * on the way back.
 * <p>
 * Where {@link __EncryptionService} works on a whole entity instance, and needs a second attribute to hold the
 * ciphertext, a converter works on a single attribute and needs no second column. The
 * {@link __EncryptionService encryptionService} is injected, and falls back to a
 * {@link jakarta.enterprise.inject.spi.CDI#current() CDI lookup} when this converter is instantiated by the persistence
 * provider rather than by the container.
 *
 * @param <X> entity attribute type parameter
 * @param <Y> database column type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @implNote The class name is misspelled — {@code Conveter} — and is kept as is for source compatibility.
 * @see __EncryptionService
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __SecureAttributeConveter<X, Y> implements AttributeConverter<X, Y> {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    /**
     * A converter for a {@code byte[]} attribute.
     *
     * @implSpec Not implemented yet; the conversion methods throw an {@link UnsupportedOperationException}.
     */
    public static class OfBytes extends __SecureAttributeConveter<byte[], byte[]> {

        /**
         * Creates a new instance.
         *
         * @implSpec An attribute converter class has to have a public no-arg constructor.
         */
        public OfBytes() {
            super();
        }

        @Override
        public byte @Nullable [] convertToDatabaseColumn(final byte @Nullable [] attribute) {
            throw new UnsupportedOperationException("not implemented yet");
        }

        @Override
        public byte @Nullable [] convertToEntityAttribute(final byte @Nullable [] dbData) {
            throw new UnsupportedOperationException("not implemented yet");
        }
    }

    /**
     * A converter for a {@link String} attribute.
     *
     * @implSpec Not implemented yet; the conversion methods throw an {@link UnsupportedOperationException}.
     */
    public static class OfString extends __SecureAttributeConveter<String, String> {

        /**
         * Creates a new instance.
         *
         * @implSpec An attribute converter class has to have a public no-arg constructor.
         */
        public OfString() {
            super();
        }

        @Override
        public @Nullable String convertToDatabaseColumn(final @Nullable String attribute) {
            throw new UnsupportedOperationException("not implemented yet");
        }

        @Override
        public @Nullable String convertToEntityAttribute(final @Nullable String dbData) {
            throw new UnsupportedOperationException("not implemented yet");
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance.
     */
    protected __SecureAttributeConveter() {
        super();
    }

    // ----------------------------------------------------------------------------------------------- encryptionService

    /**
     * Returns the encryption service this converter delegates to.
     *
     * @return the injected encryption service; one looked up from the current CDI container when this converter was not
     *         created by the container.
     */
    protected __EncryptionService getEncryptionService() {
        return Optional.ofNullable(encryptionService)
                .orElseGet(() -> CDI.current().select(__EncryptionService.class).get())
                ;
    }

    /**
     * Encrypts the specified entity instance, through {@link #getEncryptionService() the encryption service}.
     *
     * @param entityInstance the entity instance to encrypt; instances of a class which is not annotated with
     *                       {@link __EncryptedEntity @__EncryptedEntity} are silently skipped.
     */
    protected void encrypt(final Object entityInstance) {
        logger.log(System.Logger.Level.DEBUG, "encrypt({0})", describe(entityInstance));
        final var annotation = entityInstance.getClass().getAnnotation(__EncryptedEntity.class);
        if (annotation == null) {
            return;
        }
        final var encryptionService = getEncryptionService();
        assert encryptionService != null;
        encryptionService.encrypt(entityInstance);
        logger.log(System.Logger.Level.DEBUG, "encrypted: {0}", describe(entityInstance));
    }

    /**
     * Decrypts the specified entity instance, through {@link #getEncryptionService() the encryption service}.
     *
     * @param entityInstance the entity instance to decrypt; instances of a class which is not annotated with
     *                       {@link __EncryptedEntity @__EncryptedEntity} are silently skipped.
     */
    protected void decrypt(final Object entityInstance) {
        logger.log(System.Logger.Level.DEBUG, "decrypt({0})", describe(entityInstance));
        final var annotation = entityInstance.getClass().getAnnotation(__EncryptedEntity.class);
        if (annotation == null) {
            return;
        }
        final var encryptionService = getEncryptionService();
        assert encryptionService != null;
        encryptionService.decrypt(entityInstance);
        logger.log(System.Logger.Level.DEBUG, "decrypted: {0}", describe(entityInstance));
    }

    /**
     * Returns a description of the specified entity instance which cannot carry an attribute value.
     *
     * @param entityInstance the entity instance to describe.
     * @return the instance's class name and identity hash.
     */
    private static String describe(final Object entityInstance) {
        return entityInstance.getClass().getName() + '@'
               + Integer.toHexString(System.identityHashCode(entityInstance));
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Inject
    private __EncryptionService encryptionService;
}
