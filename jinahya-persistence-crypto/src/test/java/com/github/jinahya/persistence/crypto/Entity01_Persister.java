package com.github.jinahya.persistence.crypto;

import com.github.jinahya.persistence.mapped.test.__MappedEntity_Persister;
import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityManager;

class Entity01_Persister extends __MappedEntity_Persister<Entity01, Long> {

    Entity01_Persister() {
        super(Entity01.class, Long.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public void persist(final @Nonnull EntityManager entityManager, final @Nonnull Entity01 entityInstance) {
        super.persist(entityManager, entityInstance);
    }
}
