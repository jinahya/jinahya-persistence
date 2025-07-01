package com.github.jinahya.persistence.mapped.tests.test.user_with_generated_identity;

import com.github.jinahya.persistence.mapped.tests.__MappedEntityWithGeneratedIdentityTest;
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