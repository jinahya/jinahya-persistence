package com.github.jinahya.persistence.crypto;

import com.github.jinahya.persistence.mapped.test.__MappedEntity_PersistenceTest;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.Shutdown;
import jakarta.enterprise.event.Startup;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.jboss.weld.junit5.auto.AddBeanClasses;

@AddBeanClasses({
//        _PersistenceCryptoListener.class,
//        __PersistenceProducer.class,
//        __PersistenceCryptoProducer.class
//        _EncryptionListener.class,
//        _EncryptionService.class,
//        _EncryptionManager.class,
        _EncryptionProducer.class
})
//@ExtendWith(WeldJunit5AutoExtension.class)
@Slf4j
class EntityWithEmbedded01_PersistenceTest extends __MappedEntity_PersistenceTest<EntityWithEmbedded01, Long> {

    // -----------------------------------------------------------------------------------------------------------------
    EntityWithEmbedded01_PersistenceTest() {
        super(EntityWithEmbedded01.class, Long.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @PostConstruct
    protected void doOnPostConstruct() {
        super.doOnPostConstruct();
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

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    protected void __persistEntityInstance(final EntityManager entityManager, final EntityWithEmbedded01 persisted) {
        super.__persistEntityInstance(entityManager, persisted);
    }

    @Override
    protected void __persistEntityInstance(final EntityWithEmbedded01 persisted) {
        log.debug("persisted: {}", persisted);
    }
}
