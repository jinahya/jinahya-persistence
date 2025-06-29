package com.github.jinahya.persistence.mapped;

import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UserWithGeneratedIdentity_Test extends __MappedEntityTest<UserWithGeneratedIdentity, Long> {

    UserWithGeneratedIdentity_Test() {
        super(UserWithGeneratedIdentity.class, Long.class);
    }

    @Nested
    protected class ToStringTest extends __MappedEntityTest<UserWithGeneratedIdentity, Long>.ToStringTest {

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
    }

    @Nested
    protected class EqualsTest extends __MappedEntityTest<UserWithGeneratedIdentity, Long>.EqualsTest {

        @DisplayName("equals/hashCode")
        @Test
        @Override
        protected void _verify_equals() {
            super._verify_equals();
        }

        @Override
        protected SingleTypeEqualsVerifierApi<UserWithGeneratedIdentity> equalsVerifier() {
            return super.equalsVerifier()
                    .suppress(Warning.SURROGATE_KEY)
//                    .suppress(Warning.JPA_GETTER)
                    ;
        }
    }
}