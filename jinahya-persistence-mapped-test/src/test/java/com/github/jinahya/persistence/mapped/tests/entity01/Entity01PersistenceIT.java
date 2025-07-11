package com.github.jinahya.persistence.mapped.tests.entity01;

import com.github.jinahya.persistence.mapped.tests._MappedEntityPersistenceIT;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class Entity01PersistenceIT extends _MappedEntityPersistenceIT<Entity01, Long> {

    Entity01PersistenceIT() {
        super(Entity01.class, Long.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Test
    @Override
    protected void persistEntityInstance() {
        super.persistEntityInstance();
    }

    @Override
    protected void persistingEntityInstance(final Entity01 entityInstance) {
        log.debug("persisting {}", entityInstance);
        super.persistingEntityInstance(entityInstance);
    }

    @Override
    protected void persistedEntityInstance(final Entity01 entityInstance) {
        log.debug("persisted {}", entityInstance);
        super.persistedEntityInstance(entityInstance);
    }
}
