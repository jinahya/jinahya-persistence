package com.github.jinahya.persistence.mapped.tests;

import lombok.extern.slf4j.Slf4j;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.WeldJunit5AutoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@AddBeanClasses({
        _PersistenceProducer.class
})
@ExtendWith(WeldJunit5AutoExtension.class)
@Slf4j
class Entity1PersistenceTest extends _MappedEntityPersistenceTest<Entity1, Long> {

    Entity1PersistenceTest() {
        super(Entity1.class, Long.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Test
    @Override
    protected void persistEntityInstance() {
        super.persistEntityInstance();
    }

    @Override
    protected void persistingEntityInstance(final Entity1 entityInstance) {
        super.persistingEntityInstance(entityInstance);
    }

    @Override
    protected void persistedEntityInstance(Entity1 entityInstance) {
        super.persistedEntityInstance(entityInstance);
    }
}
