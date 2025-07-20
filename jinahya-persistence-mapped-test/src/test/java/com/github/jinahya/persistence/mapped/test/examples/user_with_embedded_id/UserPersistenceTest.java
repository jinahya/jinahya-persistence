package com.github.jinahya.persistence.mapped.test.examples.user_with_embedded_id;

import com.github.jinahya.persistence.mapped.test.__MappedEntityPersistenceTest;

class UserPersistenceTest extends __MappedEntityPersistenceTest<User, IdForUser> {

    UserPersistenceTest() {
        super(User.class, IdForUser.class);
    }
}
