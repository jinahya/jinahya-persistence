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
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

@ApplicationScoped
public class _EncryptionManager implements __EncryptionManager {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // -----------------------------------------------------------------------------------------------------------------
    private static final String ALGORITHM = "AES";

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";

    // -----------------------------------------------------------------------------------------------------------------
    private static final int IV_SIZE = 96; // 12 bytes

    private static final int IV_BYTES = IV_SIZE >> 3;

    // -----------------------------------------------------------------------------------------------------------------
    private static final int KEY_SIZE = 128;

    private static final int KEY_BYTES = KEY_SIZE >> 3;

    // -----------------------------------------------------------------------------------------------------------------
    private static final int AAD_SIZE = 128; // 16 bytes

    private static final int AAD_BYTES = AAD_SIZE >> 3;

    // -----------------------------------------------------------------------------------------------------------------
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
        final var key = new byte[KEY_BYTES];
        ThreadLocalRandom.current().nextBytes(key);
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
    public @Nonnull byte[] decrypt(final @Nonnull String encryptionIdentifier, final @Nonnull byte[] encryptedBytes) {
        final var buffer = ByteBuffer.wrap(encryptedBytes);
        final var iv = new byte[IV_BYTES];
        buffer.get(iv);
        final var key = new byte[KEY_BYTES];
        buffer.get(key);
        final var aad = new byte[AAD_BYTES];
        buffer.get(aad);
        final var keySpec = new SecretKeySpec(key, ALGORITHM);
        try {
            final var cipher = Cipher.getInstance(TRANSFORMATION);
            final var parameterSpec = new GCMParameterSpec(TAG_SIZE, iv);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, parameterSpec);
            cipher.updateAAD(aad);
            final var input = new byte[buffer.remaining()];
            buffer.get(input);
            return cipher.doFinal(input);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
