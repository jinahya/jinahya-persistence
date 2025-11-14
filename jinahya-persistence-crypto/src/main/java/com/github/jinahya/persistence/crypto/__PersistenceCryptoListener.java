package com.github.jinahya.persistence.crypto;

import jakarta.annotation.Nonnull;
import jakarta.inject.Inject;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;

public abstract class __PersistenceCryptoListener {

    // -----------------------------------------------------------------------------------------------------------------
    @PrePersist
    protected void onPrePersist(final @Nonnull Object entityInstance) {
        service.encrypt(entityInstance);
    }

    @PostPersist
    protected void onPostPersist(final @Nonnull Object entityInstance) {
        // empty
    }

    // -----------------------------------------------------------------------------------------------------------------
    @PreRemove
    protected void onPreRemove(final @Nonnull Object entityInstance) {
        // empty
    }

    @PostRemove
    protected void onPostRemove(final @Nonnull Object entityInstance) {
        // empty
    }

    // -----------------------------------------------------------------------------------------------------------------
    @PreUpdate
    protected void onPreUpdate(final @Nonnull Object entityInstance) {
        service.encrypt(entityInstance);
    }

    @PostUpdate
    protected void onPostUpdate(final @Nonnull Object entityInstance) {
        // empty
    }

    // -----------------------------------------------------------------------------------------------------------------
    @PostLoad
    protected void onPostLoad(final @Nonnull Object entityInstance) {
        service.decrypt(entityInstance);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Inject
    private __PersistenceCryptoService service;
}
