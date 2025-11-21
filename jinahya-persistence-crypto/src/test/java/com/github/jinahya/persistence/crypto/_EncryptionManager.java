package com.github.jinahya.persistence.crypto;

import jakarta.annotation.Nonnull;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.Shutdown;
import jakarta.enterprise.event.Startup;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.lang.invoke.MethodHandles;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

@ApplicationScoped
public class _EncryptionManager implements __EncryptionManager {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // -----------------------------------------------------------------------------------------------------------------
    private static final String ALGORITHM = "AES";

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";

    private static final int IV_SIZE = 96; // 12 bytes

    private static final int IV_BYTES = IV_SIZE >> 3;

    private static final int TAG_SIZE = 128; // 16 bytes

    // -----------------------------------------------------------------------------------------------------------------
    protected _EncryptionManager() {
        super();
    }

    // -----------------------------------------------------------------------------------------------------------------
    @PostConstruct
    protected void onPostConstruct() {
        logger.log(System.Logger.Level.DEBUG, "onPostConstruct()");
    }

    // https://stackoverflow.com/a/72628439/330457
    protected void onStartup(@Observes final Startup startup) {
        logger.log(System.Logger.Level.DEBUG, "onStartup({0})", startup);
    }

    @PreDestroy
    protected void onPreDestroy() {
        logger.log(System.Logger.Level.DEBUG, "onPreDestroy()");
    }

    // https://stackoverflow.com/a/72628439/330457
    protected void onShutdown(@Observes final Shutdown shutdown) {
        logger.log(System.Logger.Level.DEBUG, "onShutdown({0})", shutdown);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public String getEncryptionIdentifier(final @Nonnull Object entityInstance) {
        return "irrelevant";
    }

    @Override
    public @Nonnull byte[] encrypt(final @Nonnull String encryptionIdentifier, final @Nonnull byte[] decryptedBytes) {
        final var iv = new byte[IV_BYTES];
        try {
            SecureRandom.getInstanceStrong().nextBytes(iv);
        } catch (final NoSuchAlgorithmException nsae) {
            throw new RuntimeException(nsae);
        }
        final var key = Arrays.copyOf(encryptionIdentifier.getBytes(), 32);
        final var keySpec = new SecretKeySpec(key, ALGORITHM);
        try {
            final var cipher = Cipher.getInstance(TRANSFORMATION);
            final var parameterSpec = new GCMParameterSpec(TAG_SIZE, iv);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, parameterSpec);
            final var finalized = cipher.doFinal(decryptedBytes);
            final var result = new byte[iv.length + finalized.length];
            System.arraycopy(iv, 0, result, 0, iv.length);
            System.arraycopy(finalized, 0, result, iv.length, finalized.length);
            return result;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @Nonnull byte[] decrypt(final @Nonnull String encryptionIdentifier, final @Nonnull byte[] encryptedBytes) {
        final var iv = Arrays.copyOf(encryptedBytes, IV_BYTES);
        final var key = Arrays.copyOf(encryptionIdentifier.getBytes(), 32);
        final var keySpec = new SecretKeySpec(key, ALGORITHM);
        try {
            final var cipher = Cipher.getInstance(TRANSFORMATION);
            final var parameterSpec = new GCMParameterSpec(TAG_SIZE, iv);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, parameterSpec);
            final var input = Arrays.copyOfRange(encryptedBytes, iv.length, encryptedBytes.length);
            return cipher.doFinal(input);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
