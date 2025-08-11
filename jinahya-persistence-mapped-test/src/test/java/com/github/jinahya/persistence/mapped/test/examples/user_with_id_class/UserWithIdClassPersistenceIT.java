package com.github.jinahya.persistence.mapped.test.examples.user_with_id_class;

import com.github.jinahya.persistence.mapped.test.__MappedEntityPersistenceIT;

class UserWithIdClassPersistenceIT extends __MappedEntityPersistenceIT<UserWithIdClass, IdForUserWithIdClass> {

    UserWithIdClassPersistenceIT() {
        super(UserWithIdClass.class, IdForUserWithIdClass.class);
    }
}
