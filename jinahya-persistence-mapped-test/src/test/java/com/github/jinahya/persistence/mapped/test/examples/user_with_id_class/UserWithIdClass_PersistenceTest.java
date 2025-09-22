package com.github.jinahya.persistence.mapped.test.examples.user_with_id_class;

import com.github.jinahya.persistence.mapped.test.__MappedEntity_PersistenceTest;

class UserWithIdClass_PersistenceTest extends __MappedEntity_PersistenceTest<UserWithIdClass, IdForUserWithIdClass> {

    UserWithIdClass_PersistenceTest() {
        super(UserWithIdClass.class, IdForUserWithIdClass.class);
    }
}
