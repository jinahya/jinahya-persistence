package com.github.jinahya.persistence.mapped.test.examples.user_with_generated_identity;

import com.github.jinahya.persistence.mapped.test.__MappedEntityWithGeneratedIdentity_Test;
import jakarta.annotation.Nonnull;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;

class UserWithGeneratedIdentityTest extends __MappedEntityWithGeneratedIdentity_Test<UserWithGeneratedIdentity> {

    UserWithGeneratedIdentityTest() {
        super(UserWithGeneratedIdentity.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    @Override
    protected SingleTypeEqualsVerifierApi<UserWithGeneratedIdentity> equals_Verify_(
            @Nonnull final SingleTypeEqualsVerifierApi<UserWithGeneratedIdentity> equalsVerifier) {
        return super.equals_Verify_(equalsVerifier)
//                .suppress(Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY)
                .suppress(Warning.SURROGATE_KEY)
                .suppress(Warning.INHERITED_DIRECTLY_FROM_OBJECT)
                .suppress(Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY)
                ;
    }
}
