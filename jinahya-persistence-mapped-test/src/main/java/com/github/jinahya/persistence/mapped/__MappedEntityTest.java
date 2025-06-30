package com.github.jinahya.persistence.mapped;

import jakarta.persistence.Transient;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.beans.Introspector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assumptions.assumeThat;

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

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

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

    // -------------------------------------------------------------------------------------------------------- toString
    @DisplayName("newEntityInstance().")
    @Test
    protected void _NotBlank_NewInstance() {
        // ------------------------------------------------------------------------------------------------------- given
        final var entityInstance = newEntityInstance();
        // -------------------------------------------------------------------------------------------------------- when
        final var string = entityInstance.toString();
        // -------------------------------------------------------------------------------------------------------- then
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

    // ------------------------------------------------------------------------------------------------- equals/hashCode
    @DisplayName("equals/hashCode")
    @Test
    protected void _verify_equals() {
        final var equalsVerifier = getEqualsVerifier();
        equalsVerifier.verify();
    }

    protected SingleTypeEqualsVerifierApi<ENTITY> getEqualsVerifier() {
        return EqualsVerifier.forClass(entityClass);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("getId__()<ID>")
    @Nested
    protected class GetId__Test {

        @DisplayName("newEntityInstance().")
        @Test
        void _DoesNotThrow_New() {
            final var entityInstance = newEntityInstance();
            assertThatCode(entityInstance::getId__).doesNotThrowAnyException();
        }

        @DisplayName("newRandomizedEntityInstance().")
        @Test
        void _DoesNotThrow_NewRandomized() {
            final var entityInstanceOptional = newRandomizedEntityInstance();
            assumeThat(entityInstanceOptional).isPresent();
            @SuppressWarnings({
                    "java:S3655" // Optional value should only be accessed after calling isPresent()
            })
            final var entityInstance = entityInstanceOptional.get();
            assertThatCode(entityInstance::getId__).doesNotThrowAnyException();
        }
    }

    @DisplayName("setId__(<ID>)")
    @Nested
    protected class SetId__Test {

        @DisplayName("newEntityInstance().")
        @Test
        void _DoesNotThrow_New() {
            final var entityInstance = newEntityInstance();
            assertThatCode(() -> entityInstance.setId__(null)).doesNotThrowAnyException();
            assertThatCode(() -> entityInstance.setId__(newIdInstance())).doesNotThrowAnyException();
            assertThatCode(
                    () -> entityInstance.setId__(newRandomizedIdInstance().orElse(null))
            ).doesNotThrowAnyException();
        }

        @DisplayName("newRandomizedEntityInstance().")
        @Test
        void _DoesNotThrow_NewRandomized() {
            final var entityInstanceOptional = newRandomizedEntityInstance();
            assumeThat(entityInstanceOptional).isPresent();
            @SuppressWarnings({
                    "java:S3655" // Optional value should only be accessed after calling isPresent()
            })
            final var entityInstance = entityInstanceOptional.get();
            assertThatCode(() -> entityInstance.setId__(null)).doesNotThrowAnyException();
            assertThatCode(() -> entityInstance.setId__(newIdInstance())).doesNotThrowAnyException();
            assertThatCode(
                    () -> entityInstance.setId__(newRandomizedIdInstance().orElse(null))
            ).doesNotThrowAnyException();
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("setXxx(getXxx())")
    @Test
    protected void __accessors() {
        __MappedEntityTest_Utils.getEntityInstanceStream(entityClass).forEach(instance -> {
            try {
                final var info = Introspector.getBeanInfo(entityClass);
                for (final var descriptor : info.getPropertyDescriptors()) {
                    final var reader = descriptor.getReadMethod();
                    if (reader == null) {
                        continue;
                    }
                    if (!reader.canAccess(instance)) {
                        reader.setAccessible(true);
                    }
                    if (reader.isAnnotationPresent(Transient.class)) {
                        continue;
                    }
                    final var value = reader.invoke(instance);
                    final var writer = descriptor.getWriteMethod();
                    if (writer == null) {
                        continue;
                    }
                    if (!writer.canAccess(instance)) {
                        writer.setAccessible(true);
                    }
                    assertThatCode(() -> writer.invoke(instance, value))
                            .as("%s(%s)", writer.getName(), value)
                            .doesNotThrowAnyException();
                }
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}