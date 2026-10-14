package com.github.jinahya.persistence.encryption.hibernate;

import com.github.jinahya.persistence.encryption.EntityEncryptionService;
import jakarta.persistence.EntityManagerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * An encryption service for tests: AES-GCM under a fixed key, with a random IV, so that encrypting the same value twice
 * yields different bytes; it counts its calls.
 */
class CountingEncryptionService implements EntityEncryptionService {

    private static final SecretKeySpec KEY = new SecretKeySpec(new byte[32], "AES");

    private static final int IV_BYTES = 12;

    @Override
    public EntityManagerFactory getEntityManagerFactory() {
        throw new UnsupportedOperationException("the integration reads the session factory being integrated");
    }

    @Override
    public String getEncryptionIdentifier(final Object entityContext) {
        return "test";
    }

    @Override
    public byte[] encrypt(final String encryptionIdentifier, final byte[] decryptedBytes) {
        encrypted.incrementAndGet();
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

    @Override
    public byte[] decrypt(final String encryptionIdentifier, final byte[] encryptedBytes) {
        decrypted.incrementAndGet();
        try {
            final var cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, KEY, new GCMParameterSpec(128, encryptedBytes, 0, IV_BYTES));
            return cipher.doFinal(encryptedBytes, IV_BYTES, encryptedBytes.length - IV_BYTES);
        } catch (final GeneralSecurityException gse) {
            throw new RuntimeException(gse);
        }
    }

    private static final SecureRandom RANDOM = new SecureRandom();

    final AtomicInteger encrypted = new AtomicInteger();

    final AtomicInteger decrypted = new AtomicInteger();
}
