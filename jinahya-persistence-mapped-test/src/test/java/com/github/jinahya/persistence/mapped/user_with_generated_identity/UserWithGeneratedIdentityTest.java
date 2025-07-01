package com.github.jinahya.persistence.mapped.user_with_generated_identity;

import com.github.jinahya.persistence.mapped.__MappedEntityWithGeneratedIdentityTest;
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;

class UserWithGeneratedIdentityTest extends __MappedEntityWithGeneratedIdentityTest<UserWithGeneratedIdentity> {

    UserWithGeneratedIdentityTest() {
        super(UserWithGeneratedIdentity.class);
    }

    @Override
    protected SingleTypeEqualsVerifierApi<UserWithGeneratedIdentity> getEqualsVerifier() {
        return super.getEqualsVerifier();
    }
}