package com.github.jinahya.persistence.mapped.test.examples.user_with_generated_uuid;

import com.github.jinahya.persistence.mapped.test.__Disable_PersistEntityInstance_Test;
import com.github.jinahya.persistence.mapped.test.__MappedEntityWithGeneratedUuid_PersistenceIT;

@__Disable_PersistEntityInstance_Test
class UserWithGeneratedUuidPersistenceIT
        extends __MappedEntityWithGeneratedUuid_PersistenceIT<UserWithGeneratedUuid> {

    UserWithGeneratedUuidPersistenceIT() {
        super(UserWithGeneratedUuid.class);
    }
}
