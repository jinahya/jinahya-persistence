package com.github.jinahya.persistence.mapped.test.examples.user_with_generated_identity;

import com.github.jinahya.persistence.mapped.test.__MappedEntityWithGeneratedIdentityPersister;

class UserWithGeneratedIdentityPersister
        extends __MappedEntityWithGeneratedIdentityPersister<UserWithGeneratedIdentity> {

    UserWithGeneratedIdentityPersister() {
        super(UserWithGeneratedIdentity.class);
    }
}