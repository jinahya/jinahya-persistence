package com.github.jinahya.persistence.mapped.test.examples.user_with_embedded_id;

import com.github.jinahya.persistence.mapped.test.__MappedEntityPersistenceTest;

class UserPersistenceTest extends __MappedEntityPersistenceTest<User, UserId> {

    UserPersistenceTest() {
        super(User.class, UserId.class);
    }
}
