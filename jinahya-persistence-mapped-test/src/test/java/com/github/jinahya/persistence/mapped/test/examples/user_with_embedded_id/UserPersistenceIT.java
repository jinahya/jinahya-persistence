package com.github.jinahya.persistence.mapped.test.examples.user_with_embedded_id;

import com.github.jinahya.persistence.mapped.test.__MappedEntityPersistenceIT;

class UserPersistenceIT extends __MappedEntityPersistenceIT<User, IdForUser> {

    UserPersistenceIT() {
        super(User.class, IdForUser.class);
    }
}
