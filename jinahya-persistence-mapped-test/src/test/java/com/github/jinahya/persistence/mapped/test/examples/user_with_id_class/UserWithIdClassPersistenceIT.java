package com.github.jinahya.persistence.mapped.test.examples.user_with_id_class;

import com.github.jinahya.persistence.mapped.test.__Disable_PersistEntityInstance_Test;
import com.github.jinahya.persistence.mapped.test.__MappedEntity_PersistenceIT;

@__Disable_PersistEntityInstance_Test
class UserWithIdClassPersistenceIT extends __MappedEntity_PersistenceIT<UserWithIdClass, IdForUserWithIdClass> {

    UserWithIdClassPersistenceIT() {
        super(UserWithIdClass.class, IdForUserWithIdClass.class);
    }
}
