package com.github.jinahya.persistence.mapped.test.examples.user_with_generated_uuid;

import com.github.jinahya.persistence.mapped.test.__MappedEntityWithGeneratedUuid_Randomizer;

class UserWithGeneratedUuid_Randomizer extends __MappedEntityWithGeneratedUuid_Randomizer<UserWithGeneratedUuid> {

    UserWithGeneratedUuid_Randomizer() {
        super(UserWithGeneratedUuid.class, "id");
    }
}
