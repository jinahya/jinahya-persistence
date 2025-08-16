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

    @Override
    protected SingleTypeEqualsVerifierApi<UserWithGeneratedUuid> equalsVerifier() {
        return super.equalsVerifier();
    }

    @Nonnull
    @Override
    protected SingleTypeEqualsVerifierApi<UserWithGeneratedUuid> equalsVerifier(
            @Nonnull final SingleTypeEqualsVerifierApi<UserWithGeneratedUuid> equalsVerifier) {
        return super.equalsVerifier(equalsVerifier)
                .suppress(Warning.SURROGATE_KEY)
                .suppress(Warning.IDENTICAL_COPY_FOR_VERSIONED_ENTITY)
                ;
    }
}
