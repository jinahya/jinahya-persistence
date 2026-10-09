package com.github.jinahya.persistence.crypto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * An interface for the cryptographic half of this module.
 * <p>
 * An implementation decides which key an entity instance is encrypted with — that is what the encryption identifier
 * selects — and performs the encryption and decryption themselves; everything else, including finding the attributes
 * and turning their values into bytes, is done by {@link AbstractEntityEncryptionService}.
 * <p>
 * <strong>An implementation has to be thread-safe.</strong> A service holds a single instance, and calls it from the
 * entity life cycle callbacks of every persistence context, so concurrent transactions call all three methods of one
 * instance concurrently. Beware of the attractive mistake: {@link javax.crypto.Cipher}, {@link java.security.MessageDigest}
 * and {@link java.security.Signature} are <em>not</em> thread-safe, and one hoisted into a field produces garbage, or a
 * {@link javax.crypto.BadPaddingException}, non-deterministically and only under load. Obtain one per call, or confine
 * one per thread.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see AbstractEntityEncryptionService
 * @see EncryptedEntity
 */
@SuppressWarnings({
        "java:S114" // Interface names should comply with a naming convention
})
public interface EntityEncryptionManager {

    /**
     * Returns an identifier for the specified entity instance.
     *
     * @param entityInstance the entity instance.
     * @return an identifier for the {@code entityInstance}.
     * @apiNote Called concurrently on one instance; an implementation has to be thread-safe.
     */
    @NotBlank
    String getEncryptionIdentifier(@Valid @NotNull Object entityInstance);

    // -----------------------------------------------------------------------------------------------------------------
    /**
     * Encrypts the specified decrypted bytes with the specified identifier provided via
     * {@link #getEncryptionIdentifier(Object)}.
     *
     * @param encryptionIdentifier an identifier for the entity instance.
     * @param decryptedBytes       the decrypted bytes to encrypt.
     * @return an encryption result to be decrypted via {@link #decrypt(String, byte[])} method.
     * @apiNote Called concurrently on one instance; an implementation has to be thread-safe.
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
     * @apiNote Called concurrently on one instance; an implementation has to be thread-safe.
     */
    @NotNull
    byte[] decrypt(@NotBlank String encryptionIdentifier, @NotNull byte[] encryptedBytes);
}
