package com.github.jinahya.persistence.crypto;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotNull;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;

public class _EncryptionManager implements __EncryptionManager {

    private static final String ALGORITHM = "AES";

    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";

    private static final String IV_STRING = "0123456789abcdef";

    protected _EncryptionManager() {
        super();
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public String getCryptoIdentifier(final @Nonnull Object entityInstance) {
        return "irrelevant";
    }

    @Override
    public @NotNull byte[] encrypt(final @Nonnull String cryptoIdentifier, final @Nonnull byte[] decryptedBytes) {
        final var ivParameterSpec = new IvParameterSpec(IV_STRING.getBytes());
        final var keySpec = new SecretKeySpec(Arrays.copyOf(cryptoIdentifier.getBytes(), 16), ALGORITHM);
        try {
            final var cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParameterSpec);
            return cipher.doFinal(decryptedBytes);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @NotNull byte[] decrypt(final @Nonnull String cryptoIdentifier, final @Nonnull byte[] encryptedBytes) {
        final var ivParameterSpec = new IvParameterSpec(IV_STRING.getBytes());
        final var keySpec = new SecretKeySpec(Arrays.copyOf(cryptoIdentifier.getBytes(), 16), ALGORITHM);
        try {
            final var cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParameterSpec);
            return cipher.doFinal(encryptedBytes);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
