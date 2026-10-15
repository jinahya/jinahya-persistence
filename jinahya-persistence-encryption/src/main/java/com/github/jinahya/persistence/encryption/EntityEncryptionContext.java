package com.github.jinahya.persistence.encryption;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

/**
 * The context of one entity instance, for one encryption, or one decryption, of its encrypted attributes.
 * <p>
 * Created by this module for each operation — the encryption of an instance before it is written, or the decryption of
 * it after it is read, its embeddables included — and handed to the {@link EntityEncryptionService}: first to
 * {@link EntityEncryptionService#init(EntityEncryptionContext) init}, once, then to
 * {@link EntityEncryptionService#encrypt(EntityEncryptionContext, byte[]) encrypt}, or to
 * {@link EntityEncryptionService#decrypt(EntityEncryptionContext, byte[]) decrypt}, once for each attribute.
 * <p>
 * A context is confined to the thread of its operation, and is never reused: what the service prepares in {@code init}
 * — a key, a {@link javax.crypto.Cipher} — it keeps here, as the {@link #getAttachment() attachment}, without any
 * synchronization. The service must not keep the context, which holds the entity instance, beyond the call it was
 * handed to.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see EntityEncryptionService
 */
public interface EntityEncryptionContext {

    /**
     * Returns the entity instance being encrypted, or decrypted.
     * <p>
     * On the decrypt path, its encrypted attributes are still encrypted.
     *
     * @return the entity instance; never {@code null}.
     */
    @Nonnull
    @NotNull
    Object getEntityInstance();

    /**
     * Returns what the encryption service attached to this context.
     *
     * @return the attachment; {@code null} until one is {@link #setAttachment(Object) set}.
     */
    @org.jspecify.annotations.Nullable
    @Nullable
    Object getAttachment();

    /**
     * Attaches the specified object to this context, replacing any attached before.
     *
     * @param attachment the object to attach; may be {@code null}.
     */
    void setAttachment(@org.jspecify.annotations.Nullable @Nullable Object attachment);
}
