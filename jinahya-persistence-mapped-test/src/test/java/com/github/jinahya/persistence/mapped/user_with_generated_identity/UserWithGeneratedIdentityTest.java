package com.github.jinahya.persistence.mapped.user_with_generated_identity;

import com.github.jinahya.persistence.mapped.__MappedEntityWithGeneratedIdentityTest;
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserWithGeneratedIdentityTest extends __MappedEntityWithGeneratedIdentityTest<UserWithGeneratedIdentity> {

    UserWithGeneratedIdentityTest() {
        super(UserWithGeneratedIdentity.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Test
    @Override
    protected void _NotBlank_NewInstance() {
        super._NotBlank_NewInstance();
    }

    @Test
    @Override
    protected void _NotBlank_NewRandomizedInstance() {
        super._NotBlank_NewRandomizedInstance();
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("equals/hashCode")
    @Test
    @Override
    protected void _verify_equals() {
        super._verify_equals();
    }

    @Override
    protected SingleTypeEqualsVerifierApi<UserWithGeneratedIdentity> getEqualsVerifier() {
        return super.getEqualsVerifier();
    }
}