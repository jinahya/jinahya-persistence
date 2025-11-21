package com.github.jinahya.persistence.crypto;

import jakarta.annotation.Nonnull;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.event.Observes;
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
import java.lang.invoke.MethodHandles;
import java.util.Arrays;

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
        logger.log(System.Logger.Level.DEBUG, "onPostConstruct()");
    }

    // https://stackoverflow.com/a/72628439/330457
    protected void onStartup(@Observes final Startup startup) {
        logger.log(System.Logger.Level.DEBUG, "onStartup({0})", startup);
    }

    @PreDestroy
    protected void onPreDestroy() {
        logger.log(System.Logger.Level.DEBUG, "onPreDestroy()");
    }

    // https://stackoverflow.com/a/72628439/330457
    protected void onShutdown(@Observes final Shutdown shutdown) {
        logger.log(System.Logger.Level.DEBUG, "onShutdown({0})", shutdown);
    }

    // --------------------------------------------------------------------------------------------------------- PERSIST
    @PrePersist
    protected void onPrePersist(final @Nonnull Object entityInstance) {
        logger.log(System.Logger.Level.DEBUG, "onPrePersist({0})", entityInstance);
        encrypt(entityInstance);
    }

    @PostPersist
    protected void onPostPersist(final @Nonnull Object entityInstance) {
        logger.log(System.Logger.Level.DEBUG, "onPostPersist({0})", entityInstance);
    }

    // ---------------------------------------------------------------------------------------------------------- REMOVE
    @PreRemove
    protected void onPreRemove(final @Nonnull Object entityInstance) {
        logger.log(System.Logger.Level.DEBUG, "onPreRemove({0})", entityInstance);
    }

    @PostRemove
    protected void onPostRemove(final @Nonnull Object entityInstance) {
        logger.log(System.Logger.Level.DEBUG, "onPostRemove({0})", entityInstance);
    }

    // ---------------------------------------------------------------------------------------------------------- UPDATE
    @PreUpdate
    protected void onPreUpdate(final @Nonnull Object entityInstance) {
        logger.log(System.Logger.Level.DEBUG, "onPreUpdate({0})", entityInstance);
        encrypt(entityInstance);
    }

    @PostUpdate
    protected void onPostUpdate(final @Nonnull Object entityInstance) {
        logger.log(System.Logger.Level.DEBUG, "onPostUpdate({0})", entityInstance);
    }

    // ------------------------------------------------------------------------------------------------------------ LOAD
    @PostLoad
    protected void onPostLoad(final @Nonnull Object entityInstance) {
        logger.log(System.Logger.Level.DEBUG, "onPostLoad({0})", entityInstance);
        decrypt(entityInstance);
    }

    // ----------------------------------------------------------------------------------------------- encryptionService
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

    protected void encrypt(final Object entityInstance) {
        logger.log(System.Logger.Level.DEBUG, "encrypt({0})", entityInstance);
        final var annotation = entityInstance.getClass().getAnnotation(__EncryptedEntity.class);
        if (annotation == null) {
            return;
        }
        final var encryptionService = getEncryptionService();
        logger.log(System.Logger.Level.DEBUG, "encryptionService: {0}", encryptionService);
        assert encryptionService != null;
        encryptionService.encrypt(entityInstance);
        logger.log(System.Logger.Level.DEBUG, "encrypted: {0}", entityInstance);
    }

    protected void decrypt(final Object entityInstance) {
        logger.log(System.Logger.Level.DEBUG, "decrypt({0})", entityInstance);
        final var annotation = entityInstance.getClass().getAnnotation(__EncryptedEntity.class);
        if (annotation == null) {
            return;
        }
        final var encryptionService = getEncryptionService();
        logger.log(System.Logger.Level.DEBUG, "encryptionService: {0}", encryptionService);
        assert encryptionService != null;
        encryptionService.decrypt(entityInstance);
        logger.log(System.Logger.Level.DEBUG, "decrypted: {0}", entityInstance);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private volatile __EncryptionService encryptionService;
}
