package com.github.jinahya.persistence.crypto;

import com.github.jinahya.persistence.mapped.test.__MappedEntity_PersistenceTest;
import com.github.jinahya.persistence.mapped.test.__MappedEntity_RandomizerUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.Shutdown;
import jakarta.enterprise.event.Startup;
import lombok.extern.slf4j.Slf4j;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@AddBeanClasses({
//        _PersistenceCryptoListener.class,
//        __PersistenceProducer.class,
//        __PersistenceCryptoProducer.class
        _EncryptionListener.class,
        _EncryptionService.class,
        _EncryptionManager.class,
})
//@ExtendWith(WeldJunit5AutoExtension.class)
@Slf4j
class Entity01_PersistenceTest extends __MappedEntity_PersistenceTest<Entity01, Long> {

    // -----------------------------------------------------------------------------------------------------------------
    Entity01_PersistenceTest() {
        super(Entity01.class, Long.class);
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
    protected void __persistEntityInstance(final Entity01 persisted) {
    }

    //    @Transactional
    @Test
    void __() {
        applyEntityManagerInTransactionAndRollback(em -> {
            final var randomized =
                    __MappedEntity_RandomizerUtils.newRandomizedInstanceOf(entityClass).orElseThrow();
            final var byte1 = randomized.byte1;
            final var short1 = randomized.short1;
            final var int1 = randomized.int1;
            em.persist(randomized);
            em.flush();
            {
                assertThat(randomized.byte1).isNull();
                assertThat(randomized.short1).isNull();
                assertThat(randomized.int1).isNull();
            }
            em.detach(randomized);
            em.clear();
            log.debug("persisted: {}", randomized);
            final var found = em.find(entityClass, randomized.getId());
            log.debug("found: {}", found);
            {
                assertThat(found.byte1).isEqualTo(byte1);
                assertThat(found.short1).isEqualTo(short1);
                assertThat(found.int1).isEqualTo(int1);
            }
            return null;
        });
    }
}
