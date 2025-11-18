package com.github.jinahya.persistence.crypto;

import jakarta.annotation.Nonnull;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.event.Shutdown;
import jakarta.enterprise.event.Startup;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;

public class _EncryptionListener extends __EncryptionListener {

    protected _EncryptionListener() {
        super();
    }

    // -----------------------------------------------------------------------------------------------------------------
    @PostConstruct
    @Override
    protected void onPostConstruct() {
        super.onPostConstruct();
    }

    @Override
    protected void onStartup(final Startup startup) {
        super.onStartup(startup);
    }

    @PreDestroy
    @Override
    protected void onPreDestroy() {
        super.onPreDestroy();
    }

    @Override
    protected void onShutdown(final Shutdown shutdown) {
        super.onShutdown(shutdown);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @PrePersist
    @Override
    protected void onPrePersist(final @Nonnull Object entityInstance) {
        super.onPrePersist(entityInstance);
    }

    @PostPersist
    @Override
    protected void onPostPersist(@Nonnull Object entityInstance) {
        super.onPostPersist(entityInstance);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @PreUpdate
    @Override
    protected void onPreUpdate(@Nonnull Object entityInstance) {
        super.onPreUpdate(entityInstance);
    }

    @PostUpdate
    @Override
    protected void onPostUpdate(final @Nonnull Object entityInstance) {
        super.onPostUpdate(entityInstance);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @PreRemove
    @Override
    protected void onPreRemove(@Nonnull Object entityInstance) {
        super.onPreRemove(entityInstance);
    }

    @PostRemove
    @Override
    protected void onPostRemove(@Nonnull Object entityInstance) {
        super.onPostRemove(entityInstance);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @PostLoad
    @Override
    protected void onPostLoad(@Nonnull Object entityInstance) {
        super.onPostLoad(entityInstance);
    }
}
