package com.github.jinahya.persistence.crypto;

import com.github.jinahya.persistence.mapped.test.__MappedEntity_Persister;
import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityManager;

class EntityWithEmbedded01_Persister extends __MappedEntity_Persister<EntityWithEmbedded01, Long> {

    EntityWithEmbedded01_Persister() {
        super(EntityWithEmbedded01.class, Long.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public void persist(final @Nonnull EntityManager entityManager, final @Nonnull EntityWithEmbedded01 entityInstance) {
        super.persist(entityManager, entityInstance);
    }
}
