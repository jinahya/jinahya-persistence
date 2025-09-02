package com.github.jinahya.persistence.mapped.test.examples.user_with_generated_identity;

import com.github.jinahya.persistence.mapped.test.__MappedEntityWithGeneratedIdentity_Persister;

class UserWithGeneratedIdentityPersister
        extends __MappedEntityWithGeneratedIdentity_Persister<UserWithGeneratedIdentity> {

    UserWithGeneratedIdentityPersister() {
        super(UserWithGeneratedIdentity.class);
    }
}
