package com.github.jinahya.persistence.mapped.tests;

import jakarta.persistence.EntityManager;

class Entity1Persister extends __MappedEntityPersister<Entity1, Long> {

    Entity1Persister() {
        super(Entity1.class, Long.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public Entity1 persist(final EntityManager entityManager, final Entity1 entityInstance) {
        return super.persist(entityManager, entityInstance);
    }

}
