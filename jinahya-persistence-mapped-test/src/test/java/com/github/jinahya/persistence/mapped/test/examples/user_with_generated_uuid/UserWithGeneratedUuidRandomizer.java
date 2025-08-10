package com.github.jinahya.persistence.mapped.test.examples.user_with_generated_uuid;

import com.github.jinahya.persistence.mapped.test.__MappedEntityWithGeneratedUuidRandomizer;

class UserWithGeneratedUuidRandomizer extends __MappedEntityWithGeneratedUuidRandomizer<UserWithGeneratedUuid> {

    UserWithGeneratedUuidRandomizer() {
        super(UserWithGeneratedUuid.class, "id");
    }
}