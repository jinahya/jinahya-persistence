package com.github.jinahya.persistence.crypto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public interface __PersistenceCryptoManager {

    /**
     * Returns an identifier for the specified entity instance.
     *
     * @param entityInstance the entity instance.
     * @return an identifier for the {@code entityInstance}.
     */
    @NotBlank
    String getCryptoIdentifier(@Valid @NotNull Object entityInstance);

    /**
     * Encrypts the specified decrypted bytes with the specified identifier provided via
     * {@link #getCryptoIdentifier(Object)}.
     *
     * @param cryptoIdentifier an identifier for the entity instance.
     * @param decryptedBytes   the decrypted bytes to encrypt.
     * @return an array of encrypted bytes of the {@code decryptedBytes}.
     */
    @NotNull
    byte[] encrypt(@NotBlank String cryptoIdentifier, @NotNull byte[] decryptedBytes);

    /**
     * Decrypts the specified encrypted bytes with the specified identifier provided via
     * {@link #getCryptoIdentifier(Object)}.
     *
     * @param cryptoIdentifier an identifier for the entity instance.
     * @param encryptedBytes   the encrypted bytes to decrypt.
     * @return an array of decrypted bytes of the {@code encryptedBytes}.
     */
    @NotNull
    byte[] decrypt(@NotBlank String cryptoIdentifier, @NotNull byte[] encryptedBytes);
}
