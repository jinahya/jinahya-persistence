package com.github.jinahya.persistence.mapped.test.examples.user_with_id_class;

import com.github.jinahya.persistence.mapped.test.__MappedEntityTest;
import jakarta.annotation.Nonnull;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;

class UserWithIdClassTest extends __MappedEntityTest<UserWithIdClass, IdForUserWithIdClass> {

    UserWithIdClassTest() {
        super(UserWithIdClass.class, IdForUserWithIdClass.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    @Override
    protected SingleTypeEqualsVerifierApi<UserWithIdClass> configureEqualsVerifier(
            @Nonnull SingleTypeEqualsVerifierApi<UserWithIdClass> equalsVerifier) {
        return super.configureEqualsVerifier(equalsVerifier)
                .suppress(Warning.SURROGATE_KEY)
                ;
    }
}
