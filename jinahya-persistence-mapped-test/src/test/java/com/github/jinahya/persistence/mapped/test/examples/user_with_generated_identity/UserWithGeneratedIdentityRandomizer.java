package com.github.jinahya.persistence.mapped.test.examples.user_with_generated_identity;

import com.github.jinahya.persistence.mapped.test.__MappedEntityWithGeneratedIdentity_Randomizer;

class UserWithGeneratedIdentityRandomizer
        extends __MappedEntityWithGeneratedIdentity_Randomizer<UserWithGeneratedIdentity> {

    UserWithGeneratedIdentityRandomizer() {
        super(UserWithGeneratedIdentity.class, "id");
    }
}
