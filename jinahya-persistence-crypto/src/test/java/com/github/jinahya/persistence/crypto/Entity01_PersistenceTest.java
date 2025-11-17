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
        _PersistenceCryptoListener.class,
        _PersistenceCryptoService.class,
        _PersistenceCryptoManager.class,
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
        super.__persistEntityInstance(persisted);
        {
            assertThat(persisted.b1Enc).isNotNull().isNotEmpty();
        }
        {
            assertThat(persisted.b2Enc).isNotNull().isNotEmpty();
        }
        {
            assertThat(persisted.s1Enc).isNotNull().isNotEmpty();
        }
        {
            assertThat(persisted.s2Enc).isNotNull().isNotEmpty();
        }
        {
            final var found = applyEntityManager(em -> em.find(entityClass, persisted.getId()));
            log.debug("found: {}", found);
            assertThat(found).isNotNull();
        }
    }

    //    @Transactional
    @Test
    void __() {
        applyEntityManagerInTransactionAndRollback(em -> {
            final var randomized =
                    __MappedEntity_RandomizerUtils.newRandomizedInstanceOf(entityClass).orElseThrow();
            final var b1 = randomized.b1;
            final var b2 = randomized.b2;
            final var s1 = randomized.s1;
            final var s2 = randomized.s2;
            em.persist(randomized);
            em.flush();
            em.detach(randomized);
            em.clear();
            log.debug("persisted: {}", randomized);
            final var found = em.find(entityClass, randomized.getId());
            log.debug("found: {}", found);
            assertThat(found.b1).isEqualTo(b1);
            assertThat(found.b2).isEqualTo(b2);
            assertThat(found.s1).isEqualTo(s1);
            assertThat(found.s2).isEqualTo(s2);
            return null;
        });
    }
}
