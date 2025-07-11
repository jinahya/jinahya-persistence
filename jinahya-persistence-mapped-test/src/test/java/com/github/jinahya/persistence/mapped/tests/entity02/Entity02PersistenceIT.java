package com.github.jinahya.persistence.mapped.tests.entity02;

import com.github.jinahya.persistence.mapped.tests._MappedEntityPersistenceIT;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class Entity02PersistenceIT extends _MappedEntityPersistenceIT<Entity02, Long> {

    Entity02PersistenceIT() {
        super(Entity02.class, Long.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Test
    @Override
    protected void persistEntityInstance() {
        super.persistEntityInstance();
    }

    @Override
    protected void persistingEntityInstance(final Entity02 entityInstance) {
        log.debug("persisting {}", entityInstance);
        super.persistingEntityInstance(entityInstance);
    }

    @Override
    protected void persistedEntityInstance(final Entity02 entityInstance) {
        log.debug("persisted {}", entityInstance);
        super.persistedEntityInstance(entityInstance);
    }
}
