package com.github.jinahya.persistence.crypto;


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
 * <p>
 * Every argument the service passes is non-{@code null}, and the identifier non-blank; an implementation returns a
 * non-blank identifier, and non-{@code null} bytes. The service checks what it receives. (These are not declared as Bean
 * Validation constraints: those would only fire under CDI method-validation interception, and an implementation could
 * not redeclare its own without a {@code ConstraintDeclarationException}.)
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
     * Returns an identifier for the specified entity instance; the identifier the encryption keys are selected by.
     * <p>
     * <strong>The identifier is never stored.</strong> It is derived again, independently, on every encrypt and on every
     * decrypt, for as long as the row exists. So it has to be derivable <em>identically for the whole lifetime of the
     * row</em>, from state which is:
     * <ol>
     *   <li><em>present on both paths</em> — on the decrypt path the instance comes from the database with its
     *       encrypted attributes still encrypted, so the identifier cannot depend on any of them; and</li>
     *   <li><em>immutable for the life of the row</em> — anything which changes yields another identifier at the next
     *       read: the {@link jakarta.persistence.Version version}, a tenant's <em>current</em> key id, a rotation
     *       counter, a key version read from configuration. Rows written before the change then become undecryptable,
     *       or, worse, decrypt with the wrong key into a plausible wrong value.</li>
     * </ol>
     * A safe derivation reads an unencrypted, never-updated attribute, such as a tenant or owner column. Beware the
     * entity identifier: with {@link jakarta.persistence.GenerationType#IDENTITY IDENTITY} generation it is assigned
     * by the {@code INSERT}, so it is still {@code null} when the instance is encrypted on
     * {@link jakarta.persistence.PrePersist @PrePersist}. Key rotation belongs in the ciphertext the manager returns
     * (a key version in its own frame), never in this identifier.
     *
     * @param entityInstance the entity instance.
     * @return an identifier for the {@code entityInstance}, the same for the whole lifetime of its row.
     * @apiNote Called concurrently on one instance; an implementation has to be thread-safe.
     */
    String getEncryptionIdentifier(Object entityInstance);

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
    byte[] encrypt(String encryptionIdentifier, byte[] decryptedBytes);

    /**
     * Decrypts the specified encrypted bytes with the specified identifier provided via
     * {@link #getEncryptionIdentifier(Object)}.
     *
     * @param encryptionIdentifier an identifier for the entity instance.
     * @param encryptedBytes       an array of bytes resulted via {@link #encrypt(String, byte[])} method.
     * @return an array of bytes decrypted from the {@code encryptedBytes}.
     * @apiNote Called concurrently on one instance; an implementation has to be thread-safe.
     */
    byte[] decrypt(String encryptionIdentifier, byte[] encryptedBytes);
}
