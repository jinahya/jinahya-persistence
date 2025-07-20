package com.github.jinahya.persistence.mapped.test.examples.user_with_embedded_id;

import com.github.jinahya.persistence.mapped.test.__MappedEntityPersister;
import jakarta.persistence.EntityManager;

class UserWithEmbeddedIdPersister extends __MappedEntityPersister<UserWithEmbeddedId, IdForUserWithEmbeddedId> {

    UserWithEmbeddedIdPersister() {
        super(UserWithEmbeddedId.class, IdForUserWithEmbeddedId.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public void persist(final EntityManager entityManager, final UserWithEmbeddedId entityInstance) {
        super.persist(entityManager, entityInstance);
    }
}
