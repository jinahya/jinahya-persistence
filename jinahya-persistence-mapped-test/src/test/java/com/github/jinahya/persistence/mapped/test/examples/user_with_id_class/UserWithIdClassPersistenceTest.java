package com.github.jinahya.persistence.mapped.test.examples.user_with_id_class;

import com.github.jinahya.persistence.mapped.test.__MappedEntityPersistenceTest;

class UserWithIdClassPersistenceTest extends __MappedEntityPersistenceTest<UserWithIdClass, IdForUserWithIdClass> {

    UserWithIdClassPersistenceTest() {
        super(UserWithIdClass.class, IdForUserWithIdClass.class);
    }
}
