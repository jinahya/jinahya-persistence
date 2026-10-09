package com.github.jinahya.persistence.crypto;

import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

/**
 * A hand-written Mode B listener for the prototype (#82): encrypting leaves the plaintext alone, and decrypting leaves
 * the ciphertext alone; nothing mapped is ever nulled.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public class _ModeBPrototypeListener {

    static volatile EntityEncryptionManager MANAGER;

    /**
     * Creates a new instance.
     */
    public _ModeBPrototypeListener() {
        super();
    }

    @PrePersist
    @PreUpdate
    void encrypt(final Object instance) {
        final var entity = (_GuardedModeBEntity) instance;
        if (entity.nameEnc__ != null) {
            return; // unchanged since it was decrypted, or already encrypted: never re-encrypted needlessly
        }
        if (entity.name == null) {
            return; // cleared: the ciphertext stays null
        }
        entity.nameEnc__ = MANAGER.encrypt(
                MANAGER.getEncryptionIdentifier(entity),
                EntityEncryptionServiceUtils.frame(EntityEncryptionServiceUtils.Codec.STRING_,
                                                   EntityEncryptionServiceUtils.string_(entity.name)));
    }

    @PostLoad
    void decrypt(final Object instance) {
        final var entity = (_GuardedModeBEntity) instance;
        if (entity.nameEnc__ == null) {
            entity.name = null;
            return;
        }
        entity.name = EntityEncryptionServiceUtils.string_(EntityEncryptionServiceUtils.unframe(
                EntityEncryptionServiceUtils.Codec.STRING_,
                MANAGER.decrypt(MANAGER.getEncryptionIdentifier(entity), entity.nameEnc__.clone())));
    }
}
