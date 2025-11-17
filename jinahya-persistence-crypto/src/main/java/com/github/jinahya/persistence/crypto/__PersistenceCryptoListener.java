package com.github.jinahya.persistence.crypto;

import jakarta.annotation.Nonnull;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.Shutdown;
import jakarta.enterprise.event.Startup;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;

import java.lang.invoke.MethodHandles;
import java.util.Optional;

@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __PersistenceCryptoListener {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // -----------------------------------------------------------------------------------------------------------------
    protected __PersistenceCryptoListener() {
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

    // -----------------------------------------------------------------------------------------------------------------
    @PrePersist
    protected void onPrePersist(final @Nonnull Object entityInstance) {
        logger.log(System.Logger.Level.DEBUG, "onPrePersist({0})", entityInstance);
        final var cryptoService = getCryptoService();
        logger.log(System.Logger.Level.DEBUG, "cryptoService: {0}", cryptoService);
        assert cryptoService != null;
        cryptoService.encrypt(entityInstance);
        logger.log(System.Logger.Level.DEBUG, "encrypted entity instance: {0}", entityInstance);
    }

    @PostPersist
    protected void onPostPersist(final @Nonnull Object entityInstance) {
        logger.log(System.Logger.Level.DEBUG, "onPostPersist({0})", entityInstance);
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
        logger.log(System.Logger.Level.DEBUG, "onPreUpdate({0})", entityInstance);
        final var cryptoService = getCryptoService();
        logger.log(System.Logger.Level.DEBUG, "cryptoService: {0}", cryptoService);
        assert cryptoService != null;
        cryptoService.encrypt(entityInstance);
        logger.log(System.Logger.Level.DEBUG, "encrypted entity instance: {0}", entityInstance);
    }

    @PostUpdate
    protected void onPostUpdate(final @Nonnull Object entityInstance) {
        logger.log(System.Logger.Level.DEBUG, "onPostUpdate({0})", entityInstance);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @PostLoad
    protected void onPostLoad(final @Nonnull Object entityInstance) {
        logger.log(System.Logger.Level.DEBUG, "onPostLoad({0})", entityInstance);
        final var cryptoService = getCryptoService();
        logger.log(System.Logger.Level.DEBUG, "cryptoService: {0}", cryptoService);
        assert cryptoService != null;
        cryptoService.decrypt(entityInstance);
        logger.log(System.Logger.Level.DEBUG, "decrypted entity instance: {0}", entityInstance);
    }

    // --------------------------------------------------------------------------------------------------- cryptoService
    protected __PersistenceCryptoService getCryptoService() {
        return Optional.ofNullable(cryptoService).orElseGet(() -> {
            return CDI.current().select(__PersistenceCryptoService.class).get();
        });
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Inject
    private __PersistenceCryptoService cryptoService;
}
