package com.github.jinahya.persistence.mapped.test.examples.user_with_generated_uuid;

import com.github.jinahya.persistence.mapped.test.__MappedEntityWithGeneratedUuid_Randomizer;

class UserWithGeneratedUuidRandomizer extends __MappedEntityWithGeneratedUuid_Randomizer<UserWithGeneratedUuid> {

    UserWithGeneratedUuidRandomizer() {
        super(UserWithGeneratedUuid.class, "id");
    }
}
