package com.github.jinahya.persistence.encryption;

/**
 * A context for calling an encryption service directly, outside the module.
 */
class SimpleEncryptionContext implements EntityEncryptionContext {

    SimpleEncryptionContext(final Object entityInstance) {
        super();
        this.entityInstance = entityInstance;
    }

    SimpleEncryptionContext() {
        this(new Object());
    }

    @Override
    public Object getEntityInstance() {
        return entityInstance;
    }

    @Override
    public Object getAttachment() {
        return attachment;
    }

    @Override
    public void setAttachment(final Object attachment) {
        this.attachment = attachment;
    }

    private final Object entityInstance;

    private Object attachment;
}
