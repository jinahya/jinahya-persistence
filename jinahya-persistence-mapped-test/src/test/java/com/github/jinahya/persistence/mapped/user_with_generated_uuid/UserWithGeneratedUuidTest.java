package com.github.jinahya.persistence.mapped.user_with_generated_uuid;

import com.github.jinahya.persistence.mapped.__MappedEntityWithGeneratedUuidTest;
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserWithGeneratedUuidTest extends __MappedEntityWithGeneratedUuidTest<UserWithGeneratedUuid> {

    UserWithGeneratedUuidTest() {
        super(UserWithGeneratedUuid.class);
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
    protected SingleTypeEqualsVerifierApi<UserWithGeneratedUuid> getEqualsVerifier() {
        return super.getEqualsVerifier()
                ;
    }
}