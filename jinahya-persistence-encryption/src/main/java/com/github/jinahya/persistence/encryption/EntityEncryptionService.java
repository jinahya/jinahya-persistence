package com.github.jinahya.persistence.encryption;

import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityManagerFactory;
import jakarta.validation.constraints.NotNull;

/**
 * The service an application provides for encrypting its entities: which persistence unit, which key, and the
 * cryptography itself.
 * <p>
 * This is the only type an application implements. Provided as a bean — CDI, or Spring — it is injected into
 * {@link EntityEncryptionListener}, which does everything else: finding the encrypted attributes, turning their values
 * into bytes, and moving the results between the plaintext and the ciphertext attributes. An implementation never sees
 * attributes or their types; it sees an {@link EntityEncryptionContext entity context}, and bytes.
 * <p>
 * An implementation chooses its own dependencies, qualified as it likes: the {@link EntityManagerFactory} of the
 * persistence unit whose entities it encrypts, a key management client, and so on.
 * <p>
 * <strong>One context, one entity.</strong> Each encryption of an entity instance, and each decryption of one, gets a
 * context of its own: the service is {@link #init(EntityEncryptionContext) initialized} with it once, then
 * {@link #encrypt(EntityEncryptionContext, byte[]) encrypts}, or {@link #decrypt(EntityEncryptionContext, byte[])
 * decrypts}, once for each attribute of that instance which has a value. What is prepared once for the instance — the
 * key, a {@link javax.crypto.Cipher} — goes into the context's {@link EntityEncryptionContext#getAttachment()
 * attachment}, and is used for every attribute. An instance with nothing to encrypt, or to decrypt, never reaches this
 * service.
 * <p>
 * <strong>An implementation has to be thread-safe.</strong> The listener holds a single instance, and calls it from
 * the entity life cycle callbacks of every persistence context, so concurrent transactions call every method of one
 * instance concurrently. A context, though, is confined to one thread: {@link javax.crypto.Cipher},
 * {@link java.security.MessageDigest} and {@link java.security.Signature}, which are <em>not</em> thread-safe, belong
 * in a context's attachment, never in a field of the service, where they produce garbage, or a
 * {@link javax.crypto.BadPaddingException}, non-deterministically and only under load.
 * <p>
 * <strong>{@code null} is {@code null}.</strong> An absent value is never handed to this service: a {@code null}
 * plaintext is stored as a {@code null} ciphertext, and a {@code null} ciphertext read as a {@code null} plaintext,
 * without calling {@link #encrypt(EntityEncryptionContext, byte[])} or
 * {@link #decrypt(EntityEncryptionContext, byte[])}. So every argument the listener passes is non-{@code null}, and
 * every result has to be non-{@code null}: the factory, and the bytes, a {@code null} for a value which was present
 * meaning nothing. The listener checks what it receives, and rejects a {@code null} result.
 * <p>
 * The signatures say the same with Jakarta Annotations, Bean Validation constraints and, through the package, JSpecify.
 * A Bean Validation constraint is checked only where a container validates the call — a CDI or Spring bean under method
 * validation — and a constraint on a parameter is inherited: an implementation may not add one of its own, or change
 * it, without a {@code ConstraintDeclarationException}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see EntityEncryptionContext
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

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Initializes this service with the specified context, before the first attribute of its entity instance is
     * encrypted, or decrypted: selects the keys, prepares the cipher suite, and keeps what it prepared as the context's
     * {@link EntityEncryptionContext#setAttachment(Object) attachment}.
     * <p>
     * Called once for each context, and only when an attribute of its instance is to be encrypted, or decrypted.
     * <p>
     * <strong>Nothing about the keys is stored.</strong> Whatever selects them is derived again, independently, on every
     * encryption and every decryption, for as long as the row exists. So it has to be derivable <em>identically for the
     * whole lifetime of the row</em>, from state of the {@link EntityEncryptionContext#getEntityInstance() instance}
     * which is:
     * <ol>
     *   <li><em>present on both paths</em> — on the decrypt path the instance comes from the database with its
     *       encrypted attributes still encrypted, so the selection cannot depend on any of them; and</li>
     *   <li><em>immutable for the life of the row</em> — anything which changes selects other keys at the next read:
     *       the {@link jakarta.persistence.Version version}, a tenant's <em>current</em> key id, a rotation counter, a
     *       key version read from configuration. Rows written before the change then become undecryptable, or, worse,
     *       decrypt with the wrong key into a plausible wrong value.</li>
     * </ol>
     * A safe derivation reads an unencrypted, never-updated attribute, such as a tenant or owner column. Beware the
     * entity identifier: with {@link jakarta.persistence.GenerationType#IDENTITY IDENTITY} generation it is assigned
     * by the {@code INSERT}, so it is still {@code null} when the instance is encrypted. Key rotation belongs in the
     * ciphertext this service returns (a key version in its own frame), never in what the instance holds.
     *
     * @param context the context of the entity instance about to be encrypted, or decrypted; never {@code null}.
     * @apiNote Called concurrently on one instance, each call with a context of its own; an implementation has to
     *         be thread-safe.
     */
    void init(@Nonnull @NotNull EntityEncryptionContext context);

    /**
     * Encrypts the specified bytes, the encoding of one attribute's value, with what was prepared for the specified
     * context.
     * <p>
     * Called only for a value which is present: a {@code null} plaintext never reaches this method. A cipher suite
     * prepared once for the context serves every attribute, but each encryption needs what the suite requires to be
     * fresh: an AES-GCM nonce, for one, must never repeat under the same key.
     *
     * @param context        the context this service was {@link #init(EntityEncryptionContext) initialized} with;
     *                       never {@code null}.
     * @param decryptedBytes the decrypted bytes to encrypt; never {@code null}, possibly empty. The encoding of the
     *                       value; an array of the listener's own, which this method may keep.
     * @return an encryption result to be decrypted via {@link #decrypt(EntityEncryptionContext, byte[])} method; never
     *         {@code null}.
     * @apiNote Called concurrently on one instance, each call with a context of its own; an implementation has to
     *         be thread-safe.
     */
    @Nonnull
    @NotNull
    byte[] encrypt(@Nonnull @NotNull EntityEncryptionContext context, @Nonnull @NotNull byte[] decryptedBytes);

    /**
     * Decrypts the specified bytes, the stored ciphertext of one attribute's value, with what was prepared for the
     * specified context.
     * <p>
     * Called only for a value which is present: a {@code null} ciphertext never reaches this method.
     *
     * @param context        the context this service was {@link #init(EntityEncryptionContext) initialized} with;
     *                       never {@code null}.
     * @param encryptedBytes an array of bytes resulted via {@link #encrypt(EntityEncryptionContext, byte[])} method;
     *                       never {@code null}. A copy of the stored ciphertext, which this method may keep.
     * @return an array of bytes decrypted from the {@code encryptedBytes}; never {@code null}.
     * @apiNote Called concurrently on one instance, each call with a context of its own; an implementation has to
     *         be thread-safe.
     */
    @Nonnull
    @NotNull
    byte[] decrypt(@Nonnull @NotNull EntityEncryptionContext context, @Nonnull @NotNull byte[] encryptedBytes);
}
