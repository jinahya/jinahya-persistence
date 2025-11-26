package com.github.jinahya.persistence.crypto;

import jakarta.annotation.Nonnull;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.event.Shutdown;
import jakarta.enterprise.event.Startup;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;

import java.lang.annotation.Annotation;
import java.util.Arrays;

public class _EncryptionListener extends __EncryptionListener {

    /**
     * .
     * <blockquote>The entity listener class must have a public no-arg constructor.</blockquote>
     *
     * @see <a
     *         href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#entity-listeners">3.6.1.
     *         Entity Listeners</a> (Jakarta Persistence 3.2 Specification Document)
     */
    public _EncryptionListener() {
        super();
    }

    // -----------------------------------------------------------------------------------------------------------------
    protected __EncryptionService getEncryptionService() {
        __EncryptionService result = encryptionService;
        if (result == null) {
            final var qualifier = Arrays.stream(getClass().getAnnotations())
                    .filter(a -> a instanceof __EncryptionServiceQualifier)
                    .findFirst()
                    .orElse(null);
            final var qualifiers = qualifier == null ? new Annotation[0] : new Annotation[]{qualifier};
            result = encryptionService = CDI.current().select(__EncryptionService.class, qualifiers).get();
        }
        return result;
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
        encrypt(entityInstance);
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
        encrypt(entityInstance);
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
        decrypt(entityInstance);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private volatile __EncryptionService encryptionService;
}
