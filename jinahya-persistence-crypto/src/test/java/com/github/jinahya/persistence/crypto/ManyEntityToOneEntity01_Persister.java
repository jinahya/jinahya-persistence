package com.github.jinahya.persistence.crypto;

import com.github.jinahya.persistence.mapped.test.__MappedEntity_Persister;
import com.github.jinahya.persistence.mapped.test.__MappedEntity_PersisterUtils;
import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityManager;

class ManyEntityToOneEntity01_Persister extends __MappedEntity_Persister<ManyEntityToOneEntity01, Long> {

    ManyEntityToOneEntity01_Persister() {
        super(ManyEntityToOneEntity01.class, Long.class);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public void persist(@Nonnull EntityManager entityManager,
                        @Nonnull final ManyEntityToOneEntity01 entityInstance) {
        entityInstance.setEntity01(
                __MappedEntity_PersisterUtils.newPersistedInstanceOf(entityManager, Entity01.class)
        );
        super.persist(entityManager, entityInstance);
    }
}
