package com.github.jinahya.persistence.mapped.tests.test.user_with_generated_uuid;

import com.github.jinahya.persistence.mapped.tests.__MappedEntityWithGeneratedUuidTest;
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;

class UserWithGeneratedUuidTest extends __MappedEntityWithGeneratedUuidTest<UserWithGeneratedUuid> {

    UserWithGeneratedUuidTest() {
        super(UserWithGeneratedUuid.class);
    }

    @Override
    protected SingleTypeEqualsVerifierApi<UserWithGeneratedUuid> getEqualsVerifier() {
        return super.getEqualsVerifier()
                ;
    }
}