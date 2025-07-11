package com.github.jinahya.persistence.mapped.tests.entity02;

import com.github.jinahya.persistence.mapped.tests.__MappedEntityPersister;
import jakarta.persistence.EntityManager;

class Entity02Persister extends __MappedEntityPersister<Entity02, Long> {

    Entity02Persister() {
        super(Entity02.class, Long.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public Entity02 persist(final EntityManager entityManager, final Entity02 entityInstance) {
        return super.persist(entityManager, entityInstance);
    }
}
