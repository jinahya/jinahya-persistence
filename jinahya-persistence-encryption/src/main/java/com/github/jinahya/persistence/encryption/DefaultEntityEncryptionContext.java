package com.github.jinahya.persistence.encryption;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * The context of one entity instance, for one encryption, or one decryption, of it; its attachments a plain map.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @implNote Created for each operation, and confined to its thread, so the map is not synchronized. The
 *         {@link EntityEncryptor encryptor} initializes the encryption service with it on the first attribute which
 *         needs the service, and never when none does.
 */
final class DefaultEntityEncryptionContext implements EntityEncryptionContext {

    /**
     * Creates a new instance for the specified entity instance.
     *
     * @param entityInstance the entity instance; must not be {@code null}.
     */
    DefaultEntityEncryptionContext(final Object entityInstance) {
        super();
        this.entityInstance = Objects.requireNonNull(entityInstance, "entityInstance is null");
    }

    @Override
    public Object getEntityInstance() {
        return entityInstance;
    }

    @Override
    public @org.jspecify.annotations.Nullable Object getAttachment(final String name) {
        Objects.requireNonNull(name, "name is null");
        return attachments == null ? null : attachments.get(name);
    }

    @Override
    public @org.jspecify.annotations.Nullable Object setAttachment(
            final String name, final @org.jspecify.annotations.Nullable Object attachment) {
        Objects.requireNonNull(name, "name is null");
        if (attachment == null) {
            return attachments == null ? null : attachments.remove(name);
        }
        if (attachments == null) {
            attachments = new HashMap<>();
        }
        return attachments.put(name, attachment);
    }

    private final Object entityInstance;

    // created on the first attachment
    private @org.jspecify.annotations.Nullable Map<String, Object> attachments;

    /**
     * Whether the encryption service has been initialized with this context; the encryptor's, never a service's.
     */
    boolean initialized;
}
