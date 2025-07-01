package com.github.jinahya.persistence.mapped.user_with_generated_uuid;

import com.github.jinahya.persistence.mapped.__MappedEntityWithGeneratedUuidTest;
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