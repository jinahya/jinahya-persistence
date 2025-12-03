package com.github.jinahya.persistence.mapped.test.examples.embeddable01;

import com.github.jinahya.persistence.mapped.test.__MappedEntity_PersistenceTest;
import com.github.jinahya.persistence.mapped.test.___JakartaPersistence_TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

@Slf4j
class Embedding01_PersistenceTest extends __MappedEntity_PersistenceTest<Embedding01, Long> {

    Embedding01_PersistenceTest() {
        super(Embedding01.class, Long.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Test
    void __() {
        final var entityColumnNames = applyEntityManagerFactory(
                emf -> ___JakartaPersistence_TestUtils.addAllEntityColumNames(
                        emf,
                        entityClass,
                        new ArrayList<>()
                )
        );
        log.debug("entity column names: {}", entityColumnNames);
    }
}
