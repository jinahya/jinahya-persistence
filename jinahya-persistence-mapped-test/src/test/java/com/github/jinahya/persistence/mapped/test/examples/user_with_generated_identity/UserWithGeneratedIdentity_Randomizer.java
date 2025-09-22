package com.github.jinahya.persistence.mapped.test.examples.user_with_generated_identity;

import com.github.jinahya.persistence.mapped.test.__MappedEntityWithGeneratedIdentity_Randomizer;

class UserWithGeneratedIdentity_Randomizer
        extends __MappedEntityWithGeneratedIdentity_Randomizer<UserWithGeneratedIdentity> {

    UserWithGeneratedIdentity_Randomizer() {
        super(UserWithGeneratedIdentity.class, "id");
    }
}
