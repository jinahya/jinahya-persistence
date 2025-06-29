package com.github.jinahya.persistence.mapped.user_with_generated_identity;

import com.github.jinahya.persistence.mapped.__MappedEntityTest;
import com.github.jinahya.persistence.mapped.__MappedEntityWithGeneratedIdentityTest;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class User_Test extends __MappedEntityWithGeneratedIdentityTest<User> {

    User_Test() {
        super(User.class);
    }

    @Nested
    protected class ToStringTest extends __MappedEntityTest<User, Long>.ToStringTest {

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
    protected class EqualsTest extends __MappedEntityWithGeneratedIdentityTest<User>.EqualsTest {

        @DisplayName("equals/hashCode")
        @Test
        @Override
        protected void _verify_equals() {
            super._verify_equals();
        }

        @Override
        protected SingleTypeEqualsVerifierApi<User> equalsVerifier() {
            return super.equalsVerifier()
                    .suppress(Warning.SURROGATE_KEY)
//                    .suppress(Warning.JPA_GETTER)
                    ;
        }
    }
}