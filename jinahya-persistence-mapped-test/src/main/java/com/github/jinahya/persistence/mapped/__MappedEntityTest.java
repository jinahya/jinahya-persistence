package com.github.jinahya.persistence.mapped;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A class for testing subclass of {@link __MappedEntity}.
 *
 * @param <ENTITY> entity type parameter
 * @param <ID>     id type parameter
 */
@SuppressWarnings({
        "java:S100", // Method names should comply with a naming convention
        "java:S101", // Class names should comply with a naming convention
        "java:S119", // Type parameter names should comply with a naming convention
        "java:S5960" // Assertions should not be used in production code
})
public abstract class __MappedEntityTest<ENTITY extends __MappedEntity<ENTITY, ID>, ID>
        extends ___MappedEntityTest<ENTITY, ID> {

    /**
     * Creates a new instance to test the specified entity class.
     *
     * @param entityClass the entity class to test.
     * @param idClass     the id class of the {@code entityClass}.
     * @see #entityClass
     * @see #idClass
     */
    protected __MappedEntityTest(final Class<ENTITY> entityClass, final Class<ID> idClass) {
        super(entityClass, idClass);
    }

    /**
     * A nested test class for testing {@link Object#toString()} method of {@link #entityClass}.
     */
    @DisplayName("toString()")
    @Nested
    protected class ToStringTest {

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
    protected class EqualsTest {

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