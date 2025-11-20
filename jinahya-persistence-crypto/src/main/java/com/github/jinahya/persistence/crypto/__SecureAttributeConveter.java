package com.github.jinahya.persistence.crypto;

import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;
import jakarta.persistence.AttributeConverter;

import java.lang.invoke.MethodHandles;
import java.util.Optional;

public abstract class __SecureAttributeConveter<X, Y> implements AttributeConverter<X, Y> {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    public static class OfBytes extends __SecureAttributeConveter<byte[], byte[]> {

        protected OfBytes() {
            super();
        }

        @Override
        public byte[] convertToDatabaseColumn(final byte[] attribute) {
            if (attribute == null) {
                return null;
            }
            return new byte[0];
        }

        @Override
        public byte[] convertToEntityAttribute(final byte[] dbData) {
            return new byte[0];
        }
    }

    public static class OfString extends __SecureAttributeConveter<String, String> {

        protected OfString() {
            super();
        }

        @Override
        public String convertToDatabaseColumn(String attribute) {
            return "";
        }

        @Override
        public String convertToEntityAttribute(String dbData) {
            return "";
        }

        private final AttributeConverter<byte[], byte[]> ofBytes = new OfBytes();
    }

    // -----------------------------------------------------------------------------------------------------------------
    protected __SecureAttributeConveter() {
        super();
    }

    // ----------------------------------------------------------------------------------------------- encryptionService
    protected __EncryptionService getEncryptionService() {
        return Optional.ofNullable(encryptionService)
                .orElseGet(() -> CDI.current().select(__EncryptionService.class).get())
                ;
    }

    protected void encrypt(final Object entityInstance) {
        logger.log(System.Logger.Level.DEBUG, "encrypt({0})", entityInstance);
        final var annotation = entityInstance.getClass().getAnnotation(__EncryptedEntity.class);
        if (annotation == null) {
            return;
        }
        final var encryptionService = getEncryptionService();
        logger.log(System.Logger.Level.DEBUG, "encryptionService: {0}", encryptionService);
        assert encryptionService != null;
        encryptionService.encrypt(entityInstance);
        logger.log(System.Logger.Level.DEBUG, "encrypted: {0}", entityInstance);
    }

    protected void decrypt(final Object entityInstance) {
        logger.log(System.Logger.Level.DEBUG, "decrypt({0})", entityInstance);
        final var annotation = entityInstance.getClass().getAnnotation(__EncryptedEntity.class);
        if (annotation == null) {
            return;
        }
        final var encryptionService = getEncryptionService();
        logger.log(System.Logger.Level.DEBUG, "encryptionService: {0}", encryptionService);
        assert encryptionService != null;
        encryptionService.decrypt(entityInstance);
        logger.log(System.Logger.Level.DEBUG, "decrypted: {0}", entityInstance);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Inject
    private __EncryptionService encryptionService;
}
