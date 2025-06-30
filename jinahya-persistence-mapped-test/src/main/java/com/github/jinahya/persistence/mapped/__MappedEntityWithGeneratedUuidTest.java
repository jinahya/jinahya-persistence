package com.github.jinahya.persistence.mapped;

import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public abstract class __MappedEntityWithGeneratedUuidTest<ENTITY extends __MappedEntityWithGeneratedUuid<ENTITY>>
        extends __MappedEntityTest<ENTITY, UUID> {

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    protected __MappedEntityWithGeneratedUuidTest(final Class<ENTITY> entityClass) {
        super(entityClass, UUID.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nested
    protected class ToStringTest extends __MappedEntityTest<ENTITY, UUID>.__ToStringTest {

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
    protected class EqualsTest extends __MappedEntityTest<ENTITY, UUID>.__EqualsTest {

        @DisplayName("equals/hashCode")
        @Test
        @Override
        protected void _verify_equals() {
            super._verify_equals();
        }

        @Override
        protected SingleTypeEqualsVerifierApi<ENTITY> getEqualsVerifier() {
            return super.getEqualsVerifier()
                    .suppress(Warning.SURROGATE_KEY)
                    .suppress(Warning.JPA_GETTER) // https://github.com/jqno/equalsverifier/issues/1092 // TODO: check!
                    ;
        }
    }
}
