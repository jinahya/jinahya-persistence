package com.github.jinahya.persistence.mapped.test.examples.user_with_embedded_id;

import com.github.jinahya.persistence.mapped.test.__MappedEntityPersister;
import jakarta.persistence.EntityManager;

class UserPersister extends __MappedEntityPersister<User, IdForUser> {

    UserPersister() {
        super(User.class, IdForUser.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public void persist(final EntityManager entityManager, final User entityInstance) {
        super.persist(entityManager, entityInstance);
    }
}
