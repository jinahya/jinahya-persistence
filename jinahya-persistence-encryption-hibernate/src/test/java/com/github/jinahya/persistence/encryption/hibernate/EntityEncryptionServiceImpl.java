package com.github.jinahya.persistence.encryption.hibernate;

import com.github.jinahya.persistence.encryption.EntityEncryptionService;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * An encryption service for tests, named in {@code META-INF/services/}: AES-GCM under a fixed key, with a random IV;
 * it counts its calls. Not secure, by design.
 */
public class EntityEncryptionServiceImpl implements EntityEncryptionService {

    private static final SecretKeySpec KEY = new SecretKeySpec(new byte[32], "AES");

    private static final int IV_BYTES = 12;

    private static final SecureRandom RANDOM = new SecureRandom();

    static final AtomicInteger ENCRYPTED = new AtomicInteger();

    static final AtomicInteger DECRYPTED = new AtomicInteger();

    public static byte[] encryptBytes(final byte[] decryptedBytes) {
        final var iv = new byte[IV_BYTES];
        RANDOM.nextBytes(iv);
        try {
            final var cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, KEY, new GCMParameterSpec(128, iv));
            final var encrypted = cipher.doFinal(decryptedBytes);
            return ByteBuffer.allocate(IV_BYTES + encrypted.length).put(iv).put(encrypted).array();
        } catch (final GeneralSecurityException gse) {
            throw new RuntimeException(gse);
        }
    }

    public static byte[] decryptBytes(final byte[] encryptedBytes) {
        try {
            final var cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, KEY, new GCMParameterSpec(128, encryptedBytes, 0, IV_BYTES));
            return cipher.doFinal(encryptedBytes, IV_BYTES, encryptedBytes.length - IV_BYTES);
        } catch (final GeneralSecurityException gse) {
            throw new RuntimeException(gse);
        }
    }

    public EntityEncryptionServiceImpl() {
        super();
    }

    @Override
    public byte[] encrypt(final byte[] decryptedBytes) {
        ENCRYPTED.incrementAndGet();
        return encryptBytes(decryptedBytes);
    }

    @Override
    public byte[] decrypt(final byte[] encryptedBytes) {
        DECRYPTED.incrementAndGet();
        return decryptBytes(encryptedBytes);
    }
}
