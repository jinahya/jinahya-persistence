package com.github.jinahya.persistence.mapped.test.examples.user_with_generated_identity;

import com.github.jinahya.persistence.mapped.test.__MappedEntityWithGeneratedIdentityRandomizer;

class UserWithGeneratedIdentityRandomizer
        extends __MappedEntityWithGeneratedIdentityRandomizer<UserWithGeneratedIdentity> {

    UserWithGeneratedIdentityRandomizer() {
        super(UserWithGeneratedIdentity.class, "id");
    }
}