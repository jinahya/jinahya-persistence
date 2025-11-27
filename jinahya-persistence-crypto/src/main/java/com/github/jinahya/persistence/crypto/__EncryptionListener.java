package com.github.jinahya.persistence.crypto;

import jakarta.annotation.Nonnull;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.Shutdown;
import jakarta.enterprise.event.Startup;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;

import java.lang.invoke.MethodHandles;

@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __EncryptionListener {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // -----------------------------------------------------------------------------------------------------------------
    protected __EncryptionListener() {
        super();
    }

    // -----------------------------------------------------------------------------------------------------------------
    @PostConstruct
    protected void onPostConstruct() {
        logger.log(System.Logger.Level.TRACE, "onPostConstruct()");
    }

    // https://stackoverflow.com/a/72628439/330457
    protected void onStartup(@Observes final Startup startup) {
        logger.log(System.Logger.Level.TRACE, "onStartup({0})", startup);
    }

    @PreDestroy
    protected void onPreDestroy() {
        logger.log(System.Logger.Level.TRACE, "onPreDestroy()");
    }

    // https://stackoverflow.com/a/72628439/330457
    protected void onShutdown(@Observes final Shutdown shutdown) {
        logger.log(System.Logger.Level.TRACE, "onShutdown({0})", shutdown);
    }

    // --------------------------------------------------------------------------------------------------------- PERSIST
    @PrePersist
    protected void onPrePersist(final @Nonnull Object entityInstance) {
        logger.log(System.Logger.Level.TRACE, "onPrePersist({0})", entityInstance);
    }

    @PostPersist
    protected void onPostPersist(final @Nonnull Object entityInstance) {
        logger.log(System.Logger.Level.TRACE, "onPostPersist({0})", entityInstance);
    }

    // ---------------------------------------------------------------------------------------------------------- REMOVE
    @PreRemove
    protected void onPreRemove(final @Nonnull Object entityInstance) {
        logger.log(System.Logger.Level.TRACE, "onPreRemove({0})", entityInstance);
    }

    @PostRemove
    protected void onPostRemove(final @Nonnull Object entityInstance) {
        logger.log(System.Logger.Level.TRACE, "onPostRemove({0})", entityInstance);
    }

    // ---------------------------------------------------------------------------------------------------------- UPDATE
    @PreUpdate
    protected void onPreUpdate(final @Nonnull Object entityInstance) {
        logger.log(System.Logger.Level.TRACE, "onPreUpdate({0})", entityInstance);
    }

    @PostUpdate
    protected void onPostUpdate(final @Nonnull Object entityInstance) {
        logger.log(System.Logger.Level.TRACE, "onPostUpdate({0})", entityInstance);
    }

    // ------------------------------------------------------------------------------------------------------------ LOAD
    @PostLoad
    protected void onPostLoad(final @Nonnull Object entityInstance) {
        logger.log(System.Logger.Level.TRACE, "onPostLoad({0})", entityInstance);
    }

    // ----------------------------------------------------------------------------------------------- encryptionService
    protected abstract __EncryptionService getEncryptionService();

    protected void encrypt(final Object entityInstance) {
        logger.log(System.Logger.Level.TRACE, "encrypt({0})", entityInstance);
        final var annotation = entityInstance.getClass().getAnnotation(__EncryptedEntity.class);
        if (annotation == null) {
            logger.log(System.Logger.Level.TRACE, "skipping encrypting {0}, not annotated with {1}",
                       entityInstance, __EncryptedEntity.class);
            return;
        }
        final var encryptionService = getEncryptionService();
        logger.log(System.Logger.Level.TRACE, "encryptionService: {0}", encryptionService);
        assert encryptionService != null;
        encryptionService.encrypt(entityInstance);
        logger.log(System.Logger.Level.TRACE, "encrypted: {0}", entityInstance);
    }

    protected void decrypt(final Object entityInstance) {
        logger.log(System.Logger.Level.TRACE, "decrypt({0})", entityInstance);
        final var annotation = entityInstance.getClass().getAnnotation(__EncryptedEntity.class);
        if (annotation == null) {
            logger.log(System.Logger.Level.TRACE, "skipping decrypting {0}, not annotated with {1}",
                       entityInstance, __EncryptedEntity.class);
            return;
        }
        final var encryptionService = getEncryptionService();
        logger.log(System.Logger.Level.TRACE, "encryptionService: {0}", encryptionService);
        assert encryptionService != null;
        encryptionService.decrypt(entityInstance);
        logger.log(System.Logger.Level.TRACE, "decrypted: {0}", entityInstance);
    }
}
