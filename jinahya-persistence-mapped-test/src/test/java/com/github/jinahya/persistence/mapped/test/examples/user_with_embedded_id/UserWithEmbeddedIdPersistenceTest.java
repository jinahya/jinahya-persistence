package com.github.jinahya.persistence.mapped.test.examples.user_with_embedded_id;

import com.github.jinahya.persistence.mapped.test.__MappedEntityPersistenceTest;

class UserWithEmbeddedIdPersistenceTest
        extends __MappedEntityPersistenceTest<UserWithEmbeddedId, IdForUserWithEmbeddedId> {

    UserWithEmbeddedIdPersistenceTest() {
        super(UserWithEmbeddedId.class, IdForUserWithEmbeddedId.class);
    }
}
