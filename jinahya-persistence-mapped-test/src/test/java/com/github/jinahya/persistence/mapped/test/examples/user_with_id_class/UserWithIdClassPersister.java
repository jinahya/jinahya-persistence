package com.github.jinahya.persistence.mapped.test.examples.user_with_id_class;

import com.github.jinahya.persistence.mapped.test.__MappedEntityPersister;
import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityManager;

class UserWithIdClassPersister extends __MappedEntityPersister<UserWithIdClass, IdForUserWithIdClass> {

    UserWithIdClassPersister() {
        super(UserWithIdClass.class, IdForUserWithIdClass.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public void persist(@Nonnull final EntityManager entityManager, @Nonnull final UserWithIdClass entityInstance) {
        super.persist(entityManager, entityInstance);
    }
}
