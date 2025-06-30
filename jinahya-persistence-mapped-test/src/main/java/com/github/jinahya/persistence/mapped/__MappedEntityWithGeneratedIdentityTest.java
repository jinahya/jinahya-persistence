package com.github.jinahya.persistence.mapped;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public abstract class __MappedEntityWithGeneratedIdentityTest<
        ENTITY extends __MappedEntityWithGeneratedIdentity<ENTITY>
        >
        extends __MappedEntityTest<ENTITY, Long> {

    protected __MappedEntityWithGeneratedIdentityTest(final Class<ENTITY> entityClass) {
        super(entityClass, Long.class);
    }

    /**
     * A nested test class for testing {@link Object#toString()} method of {@link #entityClass}.
     */
    @DisplayName("toString()")
    @Nested
    protected class ToStringTest extends __MappedEntityTest<ENTITY, Long>.ToStringTest {

        @DisplayName("newEntityInstance().")
        @Test
        protected void _NotBlank_NewInstance() {
            // --------------------------------------------------------------------------------------------------- given
            final var entityInstance = newEntityInstance();
            // ---------------------------------------------------------------------------------------------------- when
            final var string = entityInstance.toString();
            // ---------------------------------------------------------------------------------------------------- then
            assertThat(string).isNotBlank();
        }

        @DisplayName("newRandomizedEntityInstance().")
        @Test
        protected void _NotBlank_NewRandomizedInstance() {
            // --------------------------------------------------------------------------------------------------- given
            final var entityInstance = newRandomizedEntityInstance();
            // ---------------------------------------------------------------------------------------------------- when
            final var string = entityInstance.toString();
            // ---------------------------------------------------------------------------------------------------- then
            assertThat(string).isNotBlank();
        }
    }

    /**
     * A nested test class for testing {@link Object#equals(Object)} method of {@link #entityClass}.
     */
    @DisplayName("equals()")
    @Nested
    protected class EqualsTest extends __MappedEntityTest<ENTITY, Long>.EqualsTest {

        @DisplayName("equals/hashCode")
        @Test
        protected void _verify_equals() {
            equalsVerifier().verify();
        }

        protected SingleTypeEqualsVerifierApi<ENTITY> equalsVerifier() {
            return EqualsVerifier.forClass(entityClass);
        }
    }
}
