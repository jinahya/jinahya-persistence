package com.github.jinahya.persistence.mapped.test.examples.user_with_id_class;

import com.github.jinahya.persistence.mapped.test.__MappedEntity_Persister;
import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityManager;

class UserWithIdClass_Persister extends __MappedEntity_Persister<UserWithIdClass, IdForUserWithIdClass> {

    UserWithIdClass_Persister() {
        super(UserWithIdClass.class, IdForUserWithIdClass.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public void persist(final @Nonnull EntityManager entityManager, final @Nonnull UserWithIdClass entityInstance) {
        super.persist(entityManager, entityInstance);
    }
}
