package com.github.jinahya.persistence.crypto;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManagerFactory;
import jakarta.validation.constraints.NotNull;

/**
 * The service an application provides for encrypting its entities: which persistence unit, which key, and the
 * cryptography itself.
 * <p>
 * This is the only type an application implements. Provided as a bean — CDI, or Spring — it is injected into
 * {@link EntityEncryptionListener}, which does everything else: finding the encrypted attributes, turning their values
 * into bytes, and moving the results between the plaintext and the ciphertext attributes. An implementation never sees
 * attributes or their types; it sees an entity instance, to derive an identifier from, and bytes.
 * <p>
 * An implementation chooses its own dependencies, qualified as it likes: the {@link EntityManagerFactory} of the
 * persistence unit whose entities it encrypts, a key management client, and so on.
 * <p>
 * <strong>An implementation has to be thread-safe.</strong> The listener holds a single instance, and calls it from
 * the entity life cycle callbacks of every persistence context, so concurrent transactions call every method of one
 * instance concurrently. Beware of the attractive mistake: {@link javax.crypto.Cipher},
 * {@link java.security.MessageDigest} and {@link java.security.Signature} are <em>not</em> thread-safe, and one hoisted
 * into a field produces garbage, or a {@link javax.crypto.BadPaddingException}, non-deterministically and only under
 * load. Obtain one per call, or confine one per thread.
 * <p>
 * <strong>{@code null} is {@code null}.</strong> An absent value is never handed to this service: a {@code null}
 * plaintext is stored as a {@code null} ciphertext, and a {@code null} ciphertext read as a {@code null} plaintext,
 * without calling {@link #encrypt(String, byte[])} or {@link #decrypt(String, byte[])}. So every argument the listener
 * passes is non-{@code null} — except the identifier, which is whatever {@link #getEncryptionIdentifier(Object)}
 * returned — and every result has to be non-{@code null}: the factory, and the bytes, a {@code null} for a value which
 * was present meaning nothing. The listener checks what it receives, and rejects a {@code null} result.
 * <p>
 * The signatures say the same with Jakarta Annotations, Bean Validation constraints and, through the package, JSpecify.
 * A Bean Validation constraint is checked only where a container validates the call — a CDI or Spring bean under method
 * validation — and a constraint on a parameter is inherited: an implementation may not add one of its own, or change
 * it, without a {@code ConstraintDeclarationException}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see EntityEncryptionListener
 * @see EncryptedAttribute
 */
@SuppressWarnings({
        "java:S114" // Interface names should comply with a naming convention
})
public interface EntityEncryptionService {

    /**
     * Returns the entity manager factory of the persistence unit whose entities this service encrypts; its metamodel is
     * what the encrypted attributes are found in.
     * <p>
     * <strong>Resolve it lazily.</strong> The persistence provider creates entity listeners while it is still building
     * the factory, so an implementation which receives the factory in its constructor, or in an injected field, can
     * close a cycle on the factory being built. Hold a {@code jakarta.inject.Provider} (or Spring's
     * {@code ObjectFactory}) instead, and resolve it here. The listener calls this method on its first callback, never
     * when it is created.
     *
     * @return the entity manager factory; never {@code null}, and the same one on every call.
     * @apiNote Called concurrently on one instance; an implementation has to be thread-safe.
     */
    @Nonnull
    @NotNull
    EntityManagerFactory getEntityManagerFactory();

    /**
     * Returns an identifier for the specified entity instance; the identifier the encryption keys are selected by.
     * <p>
     * <strong>The identifier is never stored.</strong> It is derived again, independently, on every encrypt and on
     * every decrypt, for as long as the row exists. So it has to be derivable <em>identically for the whole lifetime of
     * the row</em>, from state which is:
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
     * {@link jakarta.persistence.PrePersist @PrePersist}. Key rotation belongs in the ciphertext this service returns
     * (a key version in its own frame), never in this identifier.
     * <p>
     * <strong>The identifier is opaque to the listener.</strong> Whatever this method returns, {@code null} and blank
     * included, is passed, as it is, to {@link #encrypt(String, byte[])} and {@link #decrypt(String, byte[])}, whose
     * meaning for it — a default key, a legacy key, … — is entirely this service's. The rule above applies to it all
     * the same: a row written with {@code null} is read with {@code null}.
     *
     * @param entityInstance the entity instance; never {@code null}.
     * @return an identifier for the {@code entityInstance}, the same for the whole lifetime of its row; may be
     *         {@code null}.
     * @apiNote Called concurrently on one instance; an implementation has to be thread-safe.
     */
    @org.jspecify.annotations.Nullable
    @Nullable
    String getEncryptionIdentifier(@Nonnull @NotNull Object entityInstance);

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Encrypts the specified decrypted bytes with the specified identifier provided via
     * {@link #getEncryptionIdentifier(Object)}.
     * <p>
     * Called only for a value which is present: a {@code null} plaintext never reaches this method.
     *
     * @param encryptionIdentifier the identifier {@link #getEncryptionIdentifier(Object)} returned; may be
     *                             {@code null}.
     * @param decryptedBytes       the decrypted bytes to encrypt; never {@code null}. The encoding of the value, framed
     *                             with a header, so never empty either; an array of the listener's own, which this
     *                             method may keep.
     * @return an encryption result to be decrypted via {@link #decrypt(String, byte[])} method; never {@code null}.
     * @apiNote Called concurrently on one instance; an implementation has to be thread-safe.
     */
    @Nonnull
    @NotNull
    byte[] encrypt(@Nullable @org.jspecify.annotations.Nullable String encryptionIdentifier,
                   @Nonnull @NotNull byte[] decryptedBytes);

    /**
     * Decrypts the specified encrypted bytes with the specified identifier provided via
     * {@link #getEncryptionIdentifier(Object)}.
     * <p>
     * Called only for a value which is present: a {@code null} ciphertext never reaches this method.
     *
     * @param encryptionIdentifier the identifier {@link #getEncryptionIdentifier(Object)} returned; may be
     *                             {@code null}.
     * @param encryptedBytes       an array of bytes resulted via {@link #encrypt(String, byte[])} method; never
     *                             {@code null}. A copy of the stored ciphertext, which this method may keep.
     * @return an array of bytes decrypted from the {@code encryptedBytes}; never {@code null}.
     * @apiNote Called concurrently on one instance; an implementation has to be thread-safe.
     */
    @Nonnull
    @NotNull
    byte[] decrypt(@Nullable @org.jspecify.annotations.Nullable String encryptionIdentifier,
                   @Nonnull @NotNull byte[] encryptedBytes);
}
