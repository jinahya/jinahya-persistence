package com.github.jinahya.persistence.mapped.tests.entity01;

import com.github.jinahya.persistence.mapped.tests.__MappedEntityPersister;
import jakarta.persistence.EntityManager;

class Entity01Persister extends __MappedEntityPersister<Entity01, Long> {

    Entity01Persister() {
        super(Entity01.class, Long.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public Entity01 persist(final EntityManager entityManager, final Entity01 entityInstance) {
        return super.persist(entityManager, entityInstance);
    }
}
