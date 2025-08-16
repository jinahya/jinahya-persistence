package com.github.jinahya.persistence.mapped.test.examples.user_with_generated_identity;

import com.github.jinahya.persistence.mapped.test.__MappedEntityWithGeneratedIdentityTest;
import jakarta.annotation.Nonnull;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;
import org.junit.jupiter.api.Test;

class UserWithGeneratedIdentityTest extends __MappedEntityWithGeneratedIdentityTest<UserWithGeneratedIdentity> {

    UserWithGeneratedIdentityTest() {
        super(UserWithGeneratedIdentity.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Test
    @Override
    protected void equals_Verify_() {
        super.equals_Verify_();
    }

    @Nonnull
    @Override
    protected SingleTypeEqualsVerifierApi<UserWithGeneratedIdentity> equals_Verify_(
            @Nonnull final SingleTypeEqualsVerifierApi<UserWithGeneratedIdentity> equalsVerifier) {
        return super.equals_Verify_(equalsVerifier)
                .suppress(Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY)
                .suppress(Warning.SURROGATE_KEY)
                ;
    }
}
