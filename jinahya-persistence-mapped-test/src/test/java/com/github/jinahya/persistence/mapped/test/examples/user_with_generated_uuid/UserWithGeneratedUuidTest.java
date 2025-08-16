package com.github.jinahya.persistence.mapped.test.examples.user_with_generated_uuid;

import com.github.jinahya.persistence.mapped.test.__MappedEntityWithGeneratedUuidTest;
import jakarta.annotation.Nonnull;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;
import org.junit.jupiter.api.Test;

class UserWithGeneratedUuidTest extends __MappedEntityWithGeneratedUuidTest<UserWithGeneratedUuid> {

    UserWithGeneratedUuidTest() {
        super(UserWithGeneratedUuid.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Test
    @Override
    protected void equals_Verify_() {
        super.equals_Verify_();
    }

    @Nonnull
    @Override
    protected SingleTypeEqualsVerifierApi<UserWithGeneratedUuid> equals_Verify_(
            @Nonnull final SingleTypeEqualsVerifierApi<UserWithGeneratedUuid> equalsVerifier) {
        return super.equals_Verify_(equalsVerifier)
                .suppress(Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY)
                ;
    }
}
