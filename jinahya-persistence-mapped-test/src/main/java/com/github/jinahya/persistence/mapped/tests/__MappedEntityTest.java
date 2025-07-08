package com.github.jinahya.persistence.mapped.tests;

import com.github.jinahya.persistence.mapped.__MappedEntity;
import jakarta.persistence.Transient;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.beans.Introspector;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThatCode;

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
        extends ___MappedTest<ENTITY> {

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
        super(entityClass);
        this.entityClass = Objects.requireNonNull(entityClass, "entityClass is null");
        this.idClass = Objects.requireNonNull(idClass, "idClass is null");
    }

    // ------------------------------------------------------------------------------------------------- equals/hashCode

    /**
     * Verifies the {@link #entityClass} with an equals verifier from {@link #getEqualsVerifier()}.
     *
     * @see #getEqualsVerifier()
     * @see SingleTypeEqualsVerifierApi#verify()
     */
    @DisplayName("equals/hashCode")
    @Test
    protected void _verify_equals() {
        final var equalsVerifier = getEqualsVerifier();
        equalsVerifier.verify();
    }

    /**
     * Creates a new instance of {@link EqualsVerifier} for the {@link #entityClass}.
     *
     * @return a new instance of {@link EqualsVerifier} for the {@link #entityClass}
     * @see EqualsVerifier#forClass(Class)
     * @see #_verify_equals()
     */
    protected SingleTypeEqualsVerifierApi<ENTITY> getEqualsVerifier() {
        return EqualsVerifier.forClass(entityClass)
                ;
    }

    // -----------------------------------------------------------------------------------------------------------------
    protected void __accessors(final ENTITY entityInstance) {
        try {
            final var info = Introspector.getBeanInfo(entityClass);
            for (final var descriptor : info.getPropertyDescriptors()) {
                final var reader = descriptor.getReadMethod();
                if (reader == null) {
                    continue;
                }
                if (!reader.canAccess(entityInstance)) {
                    reader.setAccessible(true);
                }
                if (reader.isAnnotationPresent(Transient.class)) {
                    continue;
                }
                final var value = reader.invoke(entityInstance);
                final var writer = descriptor.getWriteMethod();
                if (writer == null) {
                    continue;
                }
                if (!writer.canAccess(entityInstance)) {
                    writer.setAccessible(true);
                }
                assertThatCode(() -> writer.invoke(entityInstance, value))
                        .as("%s(%s)", writer.getName(), value)
                        .doesNotThrowAnyException();
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    protected void accessors__newEntityInstance() {
        __accessors(newTypeInstance());
    }

    @Test
    protected void accessors__newRandomizedEntityInstance() {
        newRandomizedTypeInstance().ifPresent(this::__accessors);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The entity class to test.
     */
    protected final Class<ENTITY> entityClass;

    /**
     * The id class of the {@link #entityClass}.
     */
    protected final Class<ID> idClass;
}
