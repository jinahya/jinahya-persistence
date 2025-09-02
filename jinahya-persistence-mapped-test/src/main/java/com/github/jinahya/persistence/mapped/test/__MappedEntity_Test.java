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
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.Mockito;

import java.beans.Introspector;
import java.lang.System.Logger.Level;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InaccessibleObjectException;
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
public abstract class __MappedEntity_Test<ENTITY extends __MappedEntity<ID>, ID> extends __Mapped_Test<ENTITY> {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance to test the specified entity class.
     *
     * @param entityClass the entity class to test.
     * @param idClass     the id class of the {@code entityClass}.
     * @see #entityClass
     * @see #idClass
     */
    protected __MappedEntity_Test(@Nonnull final Class<ENTITY> entityClass, @Nonnull final Class<ID> idClass) {
        super(entityClass);
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
    @DisplayName("newEntityInstance().toString()!blank")
//    @Test
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
    @DisplayName("newRandomizedEntityInstance().toString()!blank")
//    @Test
    protected void toString_NotBlank_newRandomizedEntityInstance() {
        newRandomizedEntityInstance().ifPresent(this::toString_NotBlank_);
    }

    // ------------------------------------------------------------------------------------------------- equals/hashCode

    /**
     * Verifies the {@link #equals(Object)} method (and {@link #hashCode()} method) of the {@link #entityClass} using an
     * equals-verifier created via {@link #createEqualsVerifier()}, and configured with
     * {@link #configureEqualsVerifier(SingleTypeEqualsVerifierApi)} method.
     *
     * @implNote This method is not annotated with the {@link Test} annotation. Override this method, and put
     *         {@link Test} to verify the {@link #equals(Object)} method (and {@link #hashCode()} method) of the
     *         {@link #entityClass}
     * @see #createEqualsVerifier()
     * @see #configureEqualsVerifier(SingleTypeEqualsVerifierApi)
     */
    @DisplayName("equals/hashCode")
    protected void equals_Verify_() {
        final var equalsVerifier = Objects.requireNonNull(createEqualsVerifier(), "null equalsVerifier created");
        configureEqualsVerifier(equalsVerifier);
        equalsVerifier.verify();
    }

    /**
     * Creates a new equals verifier for {@link #entityClass}.
     *
     * @return a new equals verifier for {@link #entityClass}.
     */
    @Nonnull
    protected SingleTypeEqualsVerifierApi<ENTITY> createEqualsVerifier() {
        return EqualsVerifier.forClass(entityClass);
    }

    /**
     * Configures specified equals verifier.
     *
     * @param equalsVerifier the equals verifier to configure.
     * @return given {@code equalsVerifier}.
     * @see #equals_Verify_()
     */
    @Nonnull
    protected SingleTypeEqualsVerifierApi<ENTITY> configureEqualsVerifier(
            @Nonnull final SingleTypeEqualsVerifierApi<ENTITY> equalsVerifier) {
        return equalsVerifier
                ;
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Tests standard accessors with the specified instance.
     *
     * @param entityInstance the instance to test.
     */
    @SuppressWarnings({
            "java:S3011" // Reflection should not be used to increase accessibility of classes, methods, or fields
    })
    protected void accessors_DoesNotThrow_(@Nonnull final ENTITY entityInstance) {
        Objects.requireNonNull(entityInstance, "entityInstance is null");
        try {
            final var info = Introspector.getBeanInfo(entityClass);
            for (final var descriptor : info.getPropertyDescriptors()) {
                final var reader = descriptor.getReadMethod();
                if (reader == null || reader.isAnnotationPresent(Transient.class)) {
                    continue;
                }
                if (!reader.canAccess(entityInstance)) {
                    try {
                        reader.setAccessible(true);
                    } catch (final InaccessibleObjectException ioe) {
                        logger.log(Level.WARNING, "failed to set accessible for " + reader, ioe);
                    }
                }
                final var value = reader.invoke(entityInstance);
                final var writer = descriptor.getWriteMethod();
                if (writer == null || writer.isAnnotationPresent(Transient.class)) {
                    continue;
                }
                if (!writer.canAccess(entityInstance)) {
                    try {
                        writer.setAccessible(true);
                    } catch (final InaccessibleObjectException ioe) {
                        logger.log(Level.WARNING, "failed to set accessible for " + writer, ioe);
                    }
                }
                assertThatCode(() -> writer.invoke(entityInstance, value))
                        .as("%s.%s(%s)", entityInstance, writer.getName(), value)
                        .doesNotThrowAnyException();
            }
        } catch (final Exception e) {
            throw new RuntimeException("failed to test accessors of " + entityInstance, e);
        }
    }

    /**
     * Tests standard accessors with a new instance of {@link #entityClass}.
     *
     * @see #newEntityInstance()
     * @see #accessors_DoesNotThrow_(__MappedEntity)
     */
    @DisplayName("newEntityInstance().accessors_DoesNotThrow_()")
//    @Test
    protected void accessors_DoesNotThrow_newEntityInstance() {
        accessors_DoesNotThrow_(newEntityInstance());
    }

    /**
     * Tests standard accessors with a new randomized instance of {@link #entityClass}.
     *
     * @see #newRandomizedEntityInstance()
     * @see #accessors_DoesNotThrow_(__MappedEntity)
     */
    @DisplayName("newRandomizedEntityInstance().accessors_DoesNotThrow_()")
//    @Test
    protected void accessors_DoesNotThrow_newRandomizedEntityInstance() {
        newRandomizedEntityInstance().ifPresent(this::accessors_DoesNotThrow_);
    }

    // ----------------------------------------------------------------------------------------------------- entityClass

    /**
     * Returns a new instance of {@link #entityClass}.
     *
     * @return a new instance of {@link #entityClass}.
     */
    @Nonnull
    protected ENTITY newEntityInstance() {
        return ___InstantiatorUtils.newInstantiatedInstanceOf(entityClass)
                .orElseGet(() -> ReflectionUtils.newInstance(entityClass))
                ;
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

    /**
     * Returns a new randomized instance of {@link #entityClass}.
     *
     * @return a new randomized instance of {@link #entityClass}.
     */
    @Nonnull
    protected Optional<ENTITY> newRandomizedEntityInstance() {
        return __MappedEntity_RandomizerUtils.newRandomizedInstanceOf(entityClass);
    }

    /**
     * Returns a new spy object of a new randomized instance of {@link #entityClass}.
     *
     * @return a new spy object of a new randomized instance of {@link #entityClass}; {@link Optional#empty() empty}
     *         when no randomizer found.
     */
    @Nonnull
    protected Optional<ENTITY> newRandomizedEntityInstanceSpy() {
        return newRandomizedEntityInstance().map(Mockito::spy);
    }

    // --------------------------------------------------------------------------------------------------------- idClass

    /**
     * Returns a new instance of {@link #idClass}.
     *
     * @return a new instance of {@link #idClass}.
     */
    @Nonnull
    protected ID newIdInstance() {
        return ___InstantiatorUtils.newInstantiatedInstanceOf(idClass)
                .orElseGet(() -> ReflectionUtils.newInstance(idClass))
                ;
    }

    /**
     * Returns a spy object of a new instance of {@link #newIdInstance()}.
     *
     * @return a spy object of a new instance of {@link #newIdInstance()}.
     */
    @Nonnull
    protected ID newIdInstanceSpy() {
        return Mockito.spy(newIdInstance());
    }

    /**
     * Returns a new randomized instance of {@link #idClass}.
     *
     * @return a new randomized instance of {@link #idClass}; {@link Optional#empty() empty} when no randomizer found.
     */
    @Nonnull
    protected Optional<ID> newRandomizedIdInstance() {
        return ___RandomizerUtils.newRandomizedInstanceOf(idClass);
    }

    /**
     * Returns a spy object of a new randomized instance of {@link #idClass}.
     *
     * @return a spy object of a new randomized instance of {@link #idClass}; {@link Optional#empty() empty} when no
     *         randomizer found.
     */
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
