package com.github.jinahya.persistence.crypto;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.Shutdown;
import jakarta.enterprise.event.Startup;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManagerFactory;

@_EncryptionServiceQualifier
public class _EncryptionService extends __EncryptionService {

    @Inject
    protected _EncryptionService(final EntityManagerFactory entityManagerFactory,
                                 final __EncryptionManager encryptionManager) {
        super(entityManagerFactory, encryptionManager);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @PostConstruct
    protected void onPostConstruct() {
        super.onPostConstruct();
    }

    // https://stackoverflow.com/a/72628439/330457
    protected void onStartup(@Observes final Startup startup) {
        super.onStartup(startup);
    }

    @PreDestroy
    protected void onPreDestroy() {
        super.onPreDestroy();
    }

    // https://stackoverflow.com/a/72628439/330457
    protected void onShutdown(@Observes final Shutdown shutdown) {
        super.onShutdown(shutdown);
    }
}
