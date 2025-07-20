package com.github.jinahya.persistence.mapped.test.examples.user_with_embedded_id;

import com.github.jinahya.persistence.mapped.test.__MappedEntityPersistenceIT;

class UserPersistenceIT extends __MappedEntityPersistenceIT<User, UserId> {

    UserPersistenceIT() {
        super(User.class, UserId.class);
    }
}
