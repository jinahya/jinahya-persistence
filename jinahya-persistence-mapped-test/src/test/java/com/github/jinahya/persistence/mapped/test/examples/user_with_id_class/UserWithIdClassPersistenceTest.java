package com.github.jinahya.persistence.mapped.test.examples.user_with_id_class;

import com.github.jinahya.persistence.mapped.test.__MappedEntity_PersistenceTest;

class UserWithIdClassPersistenceTest extends __MappedEntity_PersistenceTest<UserWithIdClass, IdForUserWithIdClass> {

    UserWithIdClassPersistenceTest() {
        super(UserWithIdClass.class, IdForUserWithIdClass.class);
    }
}
