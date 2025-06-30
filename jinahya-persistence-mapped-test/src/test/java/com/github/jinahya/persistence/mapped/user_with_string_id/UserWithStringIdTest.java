package com.github.jinahya.persistence.mapped.user_with_string_id;

import com.github.jinahya.persistence.mapped.__MappedEntityTest;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserWithStringIdTest extends __MappedEntityTest<UserWithStringId, String> {

    UserWithStringIdTest() {
        super(UserWithStringId.class, String.class);
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
    protected SingleTypeEqualsVerifierApi<UserWithStringId> getEqualsVerifier() {
        return super.getEqualsVerifier()
                .suppress(Warning.SURROGATE_KEY)
                ;
    }
}