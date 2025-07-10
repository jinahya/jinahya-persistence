package com.github.jinahya.persistence.mapped.tests.test.user_with_string_id;

import com.github.jinahya.persistence.mapped.tests.__MappedEntityTest;
import jakarta.annotation.Nonnull;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;

class UserWithStringIdTest extends __MappedEntityTest<UserWithStringId, String> {

    UserWithStringIdTest() {
        super(UserWithStringId.class, String.class);
    }

    @Nonnull
    @Override
    protected SingleTypeEqualsVerifierApi<UserWithStringId> getEqualsVerifier() {
        return super.getEqualsVerifier()
                .suppress(Warning.SURROGATE_KEY)
                ;
    }
}
