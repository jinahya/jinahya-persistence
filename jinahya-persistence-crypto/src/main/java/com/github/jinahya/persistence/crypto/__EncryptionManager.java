package com.github.jinahya.persistence.crypto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@SuppressWarnings({
        "java:S114" // Interface names should comply with a naming convention
})
public interface __EncryptionManager {

    /**
     * Returns an identifier for the specified entity instance.
     *
     * @param entityInstance the entity instance.
     * @return an identifier for the {@code entityInstance}.
     */
    @NotBlank
    String getEncryptionIdentifier(@Valid @NotNull Object entityInstance);

    /**
     * Encrypts the specified decrypted bytes with the specified identifier provided via
     * {@link #getEncryptionIdentifier(Object)}.
     *
     * @param encryptionIdentifier an identifier for the entity instance.
     * @param decryptedBytes       the decrypted bytes to encrypt.
     * @return an encryption result to be decrypted via {@link #decrypt(String, byte[])} method.
     */
    @NotNull
    byte[] encrypt(@NotBlank String encryptionIdentifier, @NotNull byte[] decryptedBytes);

    /**
     * Decrypts the specified encrypted bytes with the specified identifier provided via
     * {@link #getEncryptionIdentifier(Object)}.
     *
     * @param encryptionIdentifier an identifier for the entity instance.
     * @param encryptedBytes       an array of bytes resulted via {@link #encrypt(String, byte[])} method.
     * @return an array of bytes decrypted from the {@code encryptedBytes}.
     */
    @NotNull
    byte[] decrypt(@NotBlank String encryptionIdentifier, @NotNull byte[] encryptedBytes);
}
