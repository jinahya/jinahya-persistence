package com.github.jinahya.persistence.mapped.test.examples.user_with_embedded_id;

import com.github.jinahya.persistence.mapped.test.__MappedEntityPersistenceIT;

class UserWithEmbeddedIdPersistenceIT
        extends __MappedEntityPersistenceIT<UserWithEmbeddedId, IdForUserWithEmbeddedId> {

    UserWithEmbeddedIdPersistenceIT() {
        super(UserWithEmbeddedId.class, IdForUserWithEmbeddedId.class);
    }
}
