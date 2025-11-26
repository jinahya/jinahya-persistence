package com.github.jinahya.persistence.crypto;

import com.github.jinahya.persistence.mapped.test.__MappedEntity_PersistenceTest;
import com.github.jinahya.persistence.mapped.test.__MappedEntity_RandomizerUtils;
import com.github.jinahya.persistence.metamodel.JinahyaAttributeUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.Shutdown;
import jakarta.enterprise.event.Startup;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

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
    protected void __persistEntityInstance(final EntityManager entityManager, final Entity01 persisted) {
        super.__persistEntityInstance(entityManager, persisted);
    }

    @Override
    protected void __persistEntityInstance(final Entity01 persisted) {
    }

    //    @Transactional
    @Test
    void __() {
        applyEntityManagerInTransactionAndRollback(em -> {
            final var randomized =
                    __MappedEntity_RandomizerUtils.newRandomizedInstanceOf(entityClass).orElseThrow();
            log.debug("randomized: {}", randomized);
            final var attributes = List.of(
                    "byte1", "short1", "int1", "long1", "char1", "float1", "double1",
                    "string1", "uuid1",
                    "bigInteger1", "bigDecimal1",
                    "localDate1", "localTime1", "localDateTime1", "offsetTime1", "offsetDateTime1", "instant1", "year1"
//                    ,
//                    "javaUtilDate1", "calendar1",
//                    "javaSqlDate1", "javaSqlTime1"
//                    ,
//                    "javaSqlTimestamp1"
            );
            final var attributesAndValues1 =
                    attributes.stream()
                            .map(n -> entityType().getAttribute(n))
                            .collect(Collectors.toMap(Function.identity(),
                                                      a -> JinahyaAttributeUtils.getAttributeValue(randomized, a)));
            em.persist(randomized);
            em.flush();
            log.debug("persisted: {}", randomized);
            em.detach(randomized);
            em.clear();
            // ---------------------------------------------------------------------------------------------------------
            log.debug("----------------------------------------------------------------------------------------------");
            final var found = em.find(entityClass, randomized.getId());
            log.debug("found: {}", found);
            final var attributesAndValues2 =
                    attributes.stream()
                            .map(n -> entityType().getAttribute(n))
                            .collect(Collectors.toMap(Function.identity(),
                                                      a -> JinahyaAttributeUtils.getAttributeValue(found, a)));
            for (final var entry2 : attributesAndValues2.entrySet()) {
//                assertThat(attributesAndValues2).containsEntry(entry2.getKey(), entry2.getValue());
                assertThat(attributesAndValues1.get(entry2.getKey()))
                        .as("attribute: %s", entry2.getKey())
                        .isEqualTo(entry2.getValue());
            }
            return null;
        });
    }
}
