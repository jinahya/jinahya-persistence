package com.github.jinahya.persistence.encryption;

import jakarta.persistence.EntityManagerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;

/**
 * An encryption service for tests: AES-GCM with a fresh key per value, carried inside the ciphertext. Not secure, by
 * design; it exercises the mechanism, not the cryptography.
 */
public class EntityEncryptionServiceImpl implements EntityEncryptionService {

    // -----------------------------------------------------------------------------------------------------------------
    private static final String ALGORITHM = "AES";

    private static final String MODE = "GCM";

    private static final String PADDING = "NoPadding";

    private static final String TRANSFORMATION = ALGORITHM + '/' + MODE + '/' + PADDING;

    // -----------------------------------------------------------------------------------------------------------------
    private static final int IV_SIZE = 96; // 12 bytes

    static final int IV_BYTES = IV_SIZE >> 3;

    // -----------------------------------------------------------------------------------------------------------------
    private static final int KEY_SIZE = 256;

    static final int KEY_BYTES = KEY_SIZE >> 3;

    // -----------------------------------------------------------------------------------------------------------------
    private static final int AAD_SIZE = 128; // 16 bytes

    static final int AAD_BYTES = AAD_SIZE >> 3;

    // -----------------------------------------------------------------------------------------------------------------
    private static final int TAG_SIZE = 128; // 16 bytes

    static final int TAG_BYTES = TAG_SIZE >>> 3; // 16 bytes

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance for the specified persistence unit.
     *
     * @param entityManagerFactory the entity manager factory of the persistence unit.
     */
    EntityEncryptionServiceImpl(final EntityManagerFactory entityManagerFactory) {
        super();
        this.entityManagerFactory = entityManagerFactory;
    }

    /**
     * Creates a new instance with no persistence unit, for exercising the cryptography alone.
     */
    EntityEncryptionServiceImpl() {
        this(null);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null) {
            throw new IllegalStateException("no entity manager factory");
        }
        return entityManagerFactory;
    }

    @Override
    public String getEncryptionIdentifier(final Object entityInstance) {
        return "irrelevant";
    }

    @Override
    public byte[] encrypt(final String encryptionIdentifier, final byte[] decryptedBytes) {
        final var iv = new byte[IV_BYTES];
        try {
            SecureRandom.getInstanceStrong().nextBytes(iv);
        } catch (final NoSuchAlgorithmException nsae) {
            throw new RuntimeException(nsae);
        }
        final var key = new byte[KEY_BYTES];
        try {
            SecureRandom.getInstanceStrong().nextBytes(key);
        } catch (final NoSuchAlgorithmException nsae) {
            throw new RuntimeException(nsae);
        }
        final var keySpec = new SecretKeySpec(key, ALGORITHM);
        try {
            final var cipher = Cipher.getInstance(TRANSFORMATION);
            final var parameterSpec = new GCMParameterSpec(TAG_SIZE, iv);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, parameterSpec);
            final var aad = new byte[AAD_BYTES];
            ThreadLocalRandom.current().nextBytes(aad);
            cipher.updateAAD(aad);
            final var finalized = cipher.doFinal(decryptedBytes);
            final var buffer = ByteBuffer.allocate(iv.length + key.length + aad.length + finalized.length);
            buffer.put(iv);
            buffer.put(key);
            buffer.put(aad);
            buffer.put(finalized);
            return buffer.array();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] decrypt(final String encryptionIdentifier, final byte[] encryptedBytes) {
        final byte[] iv;
        final byte[] key;
        final byte[] aad;
        final byte[] input;
        {
            final var buffer = ByteBuffer.wrap(encryptedBytes);
            iv = new byte[IV_BYTES];
            buffer.get(iv);
            key = new byte[KEY_BYTES];
            buffer.get(key);
            aad = new byte[AAD_BYTES];
            buffer.get(aad);
            input = new byte[buffer.remaining()];
            buffer.get(input);
        }
        final var keySpec = new SecretKeySpec(key, ALGORITHM);
        try {
            final var cipher = Cipher.getInstance(TRANSFORMATION);
            final var parameterSpec = new GCMParameterSpec(TAG_SIZE, iv);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, parameterSpec);
            cipher.updateAAD(aad);
            return cipher.doFinal(input);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final EntityManagerFactory entityManagerFactory;
}
