package com.github.jinahya.persistence.mapped.test;

/*-
 * #%L
 * jinahya-persistence-mapped-test
 * %%
 * Copyright (C) 2024 - 2025 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.github.jinahya.persistence.mapped.__MappedEntity;
import jakarta.annotation.Nonnull;
import jakarta.persistence.Transient;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.beans.Introspector;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * A class for testing a specific subclass of {@link __MappedEntity}.
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
public abstract class __MappedEntityTest<ENTITY extends __MappedEntity<ID>, ID> {

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
        super();
        this.entityClass = Objects.requireNonNull(entityClass, "entityClass is null");
        this.idClass = Objects.requireNonNull(idClass, "idClass is null");
    }

    // -------------------------------------------------------------------------------------------------------- toString

    /**
     * Verifies that the result of {@link Object#toString() entityInstance.toString()} is not blank.
     *
     * @param entityInstance the instance to test.
     * @see #toString_NotBlank_newEntityInstance()
     * @see #toString_NotBlank_newRandomizedEntityInstance()
     */
    protected void toString_NotBlank_(@Nonnull final ENTITY entityInstance) {
        Objects.requireNonNull(entityInstance, "entityInstance is null");
        final var string = entityInstance.toString();
        assertThat(string)
                .as("%s.toString()", entityInstance)
                .isNotBlank();
    }

    /**
     * Invokes {@link #toString_NotBlank_(__MappedEntity)} method with a new instance of {@link #entityClass}.
     *
     * @see #newEntityInstance()
     * @see #toString_NotBlank_(__MappedEntity)
     */
    @DisplayName("toString()!blank <- newEntityInstance()")
    @Test
    protected void toString_NotBlank_newEntityInstance() {
        toString_NotBlank_(newEntityInstance());
    }

    /**
     * Invokes {@link #toString_NotBlank_(__MappedEntity)} method with a new randomized instance of
     * {@link #entityClass}.
     *
     * @see #newRandomizedEntityInstance()
     * @see #toString_NotBlank_(__MappedEntity)
     */
    @DisplayName("toString()!blank <- newRandomizedEntityInstance()")
    @Test
    protected void toString_NotBlank_newRandomizedEntityInstance() {
        newRandomizedEntityInstance().ifPresent(this::toString_NotBlank_);
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
    protected void equals_verify() {
        final var equalsVerifier = getEqualsVerifier();
        equalsVerifier.verify();
    }

    /**
     * Creates a new instance of {@link EqualsVerifier} for the {@link #entityClass}.
     *
     * @return a new instance of {@link EqualsVerifier} for the {@link #entityClass}
     * @see EqualsVerifier#forClass(Class)
     * @see #equals_verify()
     */
    @Nonnull
    protected SingleTypeEqualsVerifierApi<ENTITY> getEqualsVerifier() {
        return EqualsVerifier.forClass(entityClass)
//                .withRedefinedSuperclass()
                ;
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Tests standard accessors with the specified instance.
     *
     * @param entityInstance the instance to test.
     */
    protected void accessors__(@Nonnull final ENTITY entityInstance) {
        Objects.requireNonNull(entityInstance, "entityInstance is null");
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
            throw new RuntimeException("failed to test accessors for " + entityInstance, e);
        }
    }

    /**
     * Tests standard accessors with a new instance of {@link #entityClass}.
     *
     * @see #newEntityInstance()
     * @see #accessors__(__MappedEntity)
     */
    @Test
    protected void accessors__newEntityInstance() {
        accessors__(newEntityInstance());
    }

    /**
     * Tests standard accessors with a new randomized instance of {@link #entityClass}.
     *
     * @see #newRandomizedEntityInstance()
     * @see #accessors__(__MappedEntity)
     */
    @Test
    protected void accessors__newRandomizedEntityInstance() {
        newRandomizedEntityInstance().ifPresent(this::accessors__);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns a new instance of {@link #entityClass}.
     *
     * @return a new instance of {@link #entityClass}.
     */
    @Nonnull
    protected ENTITY newEntityInstance() {
        return ___InstantiatorUtils.newInstantiatedInstanceOf(entityClass)
                .orElseGet(() -> ___JavaLangReflectTestUtils.newInstanceOf(entityClass));
    }

    /**
     * Returns a new spy object of {@link #newEntityInstance()}.
     *
     * @return a new spy object of {@link #newEntityInstance()}.
     */
    @Nonnull
    protected ENTITY newEntityInstanceSpy() {
        return Mockito.spy(newEntityInstance());
    }

    @Nonnull
    protected Optional<ENTITY> newRandomizedEntityInstance() {
        return __MappedEntityRandomizerUtils.newRandomizedInstanceOf(entityClass);
    }

    @Nonnull
    protected Optional<ENTITY> newRandomizedEntityInstanceSpy() {
        return newRandomizedEntityInstance().map(Mockito::spy);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    protected ID newIdInstance() {
        return ___InstantiatorUtils.newInstantiatedInstanceOf(idClass)
                .orElseGet(() -> ___JavaLangReflectTestUtils.newInstanceOf(idClass));
    }

    @Nonnull
    protected ID newIdInstanceSpy() {
        return Mockito.spy(newIdInstance());
    }

    @Nonnull
    protected Optional<ID> newRandomizedIdInstance() {
        return ___RandomizerUtils.newRandomizedInstanceOf(idClass);
    }

    @Nonnull
    protected Optional<ID> newRandomizedIdInstanceSpy() {
        return newRandomizedIdInstance().map(Mockito::spy);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The class of {@link ENTITY}.
     */
    protected final Class<ENTITY> entityClass;

    /**
     * The class of {@link ID}.
     */
    protected final Class<ID> idClass;
}
