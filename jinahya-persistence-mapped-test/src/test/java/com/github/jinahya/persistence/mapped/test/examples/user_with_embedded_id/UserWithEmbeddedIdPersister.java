package com.github.jinahya.persistence.mapped.test.examples.user_with_embedded_id;

import com.github.jinahya.persistence.mapped.test.__MappedEntityPersister;
import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityManager;

class UserWithEmbeddedIdPersister extends __MappedEntityPersister<UserWithEmbeddedId, IdForUserWithEmbeddedId> {

    UserWithEmbeddedIdPersister() {
        super(UserWithEmbeddedId.class, IdForUserWithEmbeddedId.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public void persist(@Nonnull final EntityManager entityManager, @Nonnull final UserWithEmbeddedId entityInstance) {
        super.persist(entityManager, entityInstance);
    }
}
