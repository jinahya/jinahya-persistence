package com.github.jinahya.persistence.mapped.user_with_string_id;

import com.github.jinahya.persistence.mapped.__MappedEntityTest;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;

class UserWithStringIdTest extends __MappedEntityTest<UserWithStringId, String> {

    UserWithStringIdTest() {
        super(UserWithStringId.class, String.class);
    }

    @Override
    protected SingleTypeEqualsVerifierApi<UserWithStringId> getEqualsVerifier() {
        return super.getEqualsVerifier()
                .suppress(Warning.SURROGATE_KEY)
                ;
    }
}