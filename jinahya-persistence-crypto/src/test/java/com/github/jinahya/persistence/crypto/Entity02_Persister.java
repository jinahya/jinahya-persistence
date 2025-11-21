package com.github.jinahya.persistence.crypto;

import com.github.jinahya.persistence.mapped.test.__MappedEntity_Persister;
import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityManager;

class Entity02_Persister extends __MappedEntity_Persister<Entity02, Long> {

    Entity02_Persister() {
        super(Entity02.class, Long.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public void persist(final @Nonnull EntityManager entityManager, final @Nonnull Entity02 entityInstance) {
        super.persist(entityManager, entityInstance);
    }
}
