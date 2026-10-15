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
 * — a key, a {@link javax.crypto.Cipher} — it keeps here, as {@link #setAttachment(String, Object) attachments},
 * without any synchronization. Their names, and their values, are entirely the service's; this module never reads them.
 * The service must not keep the context, which holds the entity instance, beyond the call it was handed to.
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
     * Returns the object attached to this context under the specified name.
     *
     * @param name the name the object was attached under; never {@code null}.
     * @return the object attached under the {@code name}; {@code null} when none is.
     * @see #setAttachment(String, Object)
     */
    @org.jspecify.annotations.Nullable
    @Nullable
    Object getAttachment(@Nonnull @NotNull String name);

    /**
     * Attaches the specified object to this context under the specified name, replacing any attached under it before;
     * or, when the object is {@code null}, removes any attached under the name.
     *
     * @param name       the name to attach the {@code attachment} under; never {@code null}.
     * @param attachment the object to attach; {@code null} removes any attached under the {@code name}.
     * @return the object previously attached under the {@code name}; {@code null} when none was.
     * @see #getAttachment(String)
     */
    @org.jspecify.annotations.Nullable
    @Nullable
    Object setAttachment(@Nonnull @NotNull String name,
                         @org.jspecify.annotations.Nullable @Nullable Object attachment);

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns what the encryption service attached to this context.
     *
     * @return the attachment; {@code null} until one is {@link #setAttachment(Object) set}.
     * @deprecated Use {@link #getAttachment(String)}.
     */
    @Deprecated
    @org.jspecify.annotations.Nullable
    @Nullable
    default Object getAttachment() {
        return getAttachment(EntityEncryptionContextConstants.DEFAULT_NAME);
    }

    /**
     * Attaches the specified object to this context, replacing any attached before; or, when the object is
     * {@code null}, removes any attached before.
     *
     * @param attachment the object to attach; {@code null} removes any attached before.
     * @return the object previously attached; {@code null} when none was.
     * @deprecated Use {@link #setAttachment(String, Object)}.
     */
    @Deprecated
    @org.jspecify.annotations.Nullable
    @Nullable
    default Object setAttachment(@org.jspecify.annotations.Nullable @Nullable Object attachment) {
        return setAttachment(EntityEncryptionContextConstants.DEFAULT_NAME, attachment);
    }
}
