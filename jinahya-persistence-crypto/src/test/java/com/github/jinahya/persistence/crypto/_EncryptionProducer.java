package com.github.jinahya.persistence.crypto;

import com.github.jinahya.persistence.mapped.test.__PersistenceProducer;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class _EncryptionProducer {

    // -----------------------------------------------------------------------------------------------------------------
    protected _EncryptionProducer() {
        super();
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Produces
    public __EncryptionManager produceEncryptionManager() {
        final var encryptionManager = new _EncryptionManager();
        log.debug("producing {}", encryptionManager);
        return encryptionManager;
    }

    public void disposeEncryptionManager(final @Disposes __EncryptionManager encryptionManager) {
        log.debug("disposing {}", encryptionManager);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @_EncryptionServiceQualifier
    @Produces
    public __EncryptionService produceEncryptionService(
            @__PersistenceProducer.__testPU final EntityManagerFactory entityManagerFactory,
            final __EncryptionManager encryptionManager) {
        final var encryptionService = new _EncryptionService(entityManagerFactory, encryptionManager);
        log.debug("producing {}, initialized with {}, {}", encryptionService, entityManagerFactory, encryptionManager);
        return encryptionService;
    }

    public void dispostEncryptionService(
            final @_EncryptionServiceQualifier @Disposes __EncryptionService encryptionService) {
        log.debug("disposing: {}", encryptionService);
    }

    // -----------------------------------------------------------------------------------------------------------------
//    @Produces
//    public __EncryptionListener productEncryptionListener(
//            @_EncryptionServiceQualifier final __EncryptionService encryptionService) {
//        final var encryptionListener = new _EncryptionListener(encryptionService);
//        log.debug("producing encryption listener: {}, initialized with {}", encryptionListener, encryptionService);
//        return encryptionListener;
//    }
//
//    public void disposeCryptoListener(final @Disposes __EncryptionListener encryptionListener) {
//        log.debug("disposing: {}", encryptionListener);
//    }
}
