package com.github.jinahya.persistence.entity.test;

/*-
 * #%L
 * jinahya-persistence-entity-test
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

import com.github.jinahya.persistence.mapped.test.__Disable_EqualsVerifier_Test;
import com.github.jinahya.persistence.mapped.test.__Disable_PropertyAccessor_Test;
import com.github.jinahya.persistence.mapped.test.__Disable_PropertyAccessors_Test;
import com.github.jinahya.persistence.mapped.test.__Disable_ToString_Test;
import com.github.jinahya.persistence.mapped.test.___InstantiatorUtils;
import com.github.jinahya.persistence.mapped.test.___RandomizerUtils;
import jakarta.annotation.Nonnull;
import jakarta.persistence.Transient;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.AnnotationUtils;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.Mockito;

import java.beans.Introspector;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InaccessibleObjectException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.mockito.Mockito.spy;

/**
 * A class for testing a specific entity class.
 *
 * @param <T> entity type parameter
 */
@SuppressWarnings({
        "java:S100", // Method names should comply with a naming convention
        "java:S101", // Class names should comply with a naming convention
        "java:S5960" // Assertions should not be used in production code
})
public abstract class ___Entity_Test<T> {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    protected ___Entity_Test(@Nonnull final Class<T> entityClass) {
        super();
        this.entityClass = Objects.requireNonNull(entityClass, "entityClass is null");
    }

    // -------------------------------------------------------------------------------------------------------- toString

    /**
     * Verifies that the result of {@link Object#toString() toString()} is not blank.
     *
     * @param entityInstance the instance to test.
     * @see #toString_NotBlank_newEntityInstance()
     * @see #toString_NotBlank_newRandomizedEntityInstance()
     */
    private void toString_NotBlank_(@Nonnull final T entityInstance) {
        Objects.requireNonNull(entityInstance, "entityInstance is null");
        final var string = entityInstance.toString();
        assertThat(string)
                .as("%s.toString()", entityInstance)
                .isNotBlank();
        logger.log(System.Logger.Level.DEBUG, "instance: {0}", entityInstance);
    }

    private void assumeToStringTestNotDisabled() {
        final var clazz = getClass();
        final var disabled = AnnotationUtils.findAnnotation(clazz, __Disable_ToString_Test.class);
        assumeThat(disabled)
                .as("%s on %s", __Disable_ToString_Test.class, clazz)
                .isEmpty();
    }

    /**
     * Verifies that the result of {@link Object#toString() toString()} of an instance from the
     * {@link #newEntityInstance()} is not blank.
     *
     * @see __Disable_ToString_Test
     * @see #newEntityInstance()
     * @see #toString_NotBlank_(Object)
     */
    @DisplayName("<instantiated>.toString()!blank")
    @Test
    final void toString_NotBlank_newEntityInstance() {
        assumeToStringTestNotDisabled();
        toString_NotBlank_(newEntityInstance());
    }

    /**
     * Verifies that the result of {@link Object#toString() toString()} of an instance from the
     * {@link #newRandomizedEntityInstance()} is not blank.
     *
     * @see __Disable_ToString_Test
     * @see #newRandomizedEntityInstance()
     * @see #toString_NotBlank_(Object)
     */
    @DisplayName("<randomized>.toString()!blank")
    @Test
    final void toString_NotBlank_newRandomizedEntityInstance() {
        assumeToStringTestNotDisabled();
        final var randomized = newRandomizedEntityInstance().orElse(null);
        assumeThat(randomized)
                .as("new randomized entity instance")
                .isNotNull();
        toString_NotBlank_(randomized);
    }

    // ------------------------------------------------------------------------------------------------- equals/hashCode

    /**
     * Verifies the {@link #equals(Object)} method (and {@link #hashCode()} method) of the {@link #entityClass} using an
     * equals-verifier.
     *
     * @see __Disable_EqualsVerifier_Test
     * @see #equals_Verify_(SingleTypeEqualsVerifierApi)
     * @see #equals_Verify_(SingleTypeEqualsVerifierApi)
     */
    @DisplayName("equals/hashCode")
    @Test
    final void equals_Verify_() {
        {
            final var clazz = getClass();
            final var disabled = AnnotationUtils.findAnnotation(clazz, __Disable_EqualsVerifier_Test.class);
            assumeThat(disabled)
                    .as("%s on %s", __Disable_EqualsVerifier_Test.class, clazz)
                    .isEmpty();
        }
        if (true) {
            final var verifier = equals_Verify_(EqualsVerifier.forClass(entityClass));
            verifier.verify();
            return;
        }
        final var equalsVerifierReference = new AtomicReference<>(EqualsVerifier.forClass(entityClass));
        ReflectionUtils.findMethods(
                getClass(),
                m -> {
                    final var parameterTypes = m.getParameterTypes();
                    if (parameterTypes.length != 1) {
                        return false;
                    }
                    if (parameterTypes[0] != SingleTypeEqualsVerifierApi.class) {
                        return false;
                    }
                    return true;
                },
                ReflectionUtils.HierarchyTraversalMode.BOTTOM_UP
        ).forEach(m -> {
            final var equalsVerifier = equalsVerifierReference.get();
            logger.log(System.Logger.Level.DEBUG, "invoking {0}({1})", m, equalsVerifier);
            if (!m.canAccess(this)) {
                m.setAccessible(true);
            }
            try {
                final var result = m.invoke(this, equalsVerifier);
                if (result != null && !(result instanceof SingleTypeEqualsVerifierApi<?>)) {
                    throw new RuntimeException("unacceptable result: " + result + " from " + m);
                }
                if (result != null) {
                    equalsVerifierReference.set((SingleTypeEqualsVerifierApi<T>) result);
                }
            } catch (final Exception e) {
                throw new RuntimeException(
                        "failed to invoke " + m + " on " + this + " with " + equalsVerifier,
                        e
                );
            }
        });
        final var equalsVerifier = equalsVerifierReference.get();
        equalsVerifier.verify();
    }

    /**
     * Configures specified equals verifier.
     *
     * @param equalsVerifier the equals verifier to configure.
     * @return given {@code equalsVerifier}, or a new equals verifier if required.
     * @apiNote the {@code equals_Verify_(SingleTypeEqualsVerifierApi)} method of {@code __Entity_Test} class
     *         simply returns the {@code equalsVerifier}.
     */
    @Nonnull
    protected SingleTypeEqualsVerifierApi<T> equals_Verify_(
            @Nonnull final SingleTypeEqualsVerifierApi<T> equalsVerifier) {
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
    private void propertyAccessors_DoesNotThrow_(@Nonnull final T entityInstance) {
        Objects.requireNonNull(entityInstance, "entityInstance is null");
        try {
            final var info = Introspector.getBeanInfo(entityClass);
            for (final var descriptor : info.getPropertyDescriptors()) {
                final var reader = descriptor.getReadMethod();
                if (reader == null
                    || reader.isAnnotationPresent(Transient.class)
                    || reader.isAnnotationPresent(__Disable_PropertyAccessor_Test.class)) {
                    continue;
                }
                if (!reader.canAccess(entityInstance)) {
                    try {
                        reader.setAccessible(true);
                    } catch (final InaccessibleObjectException ioe) {
                        logger.log(System.Logger.Level.WARNING, "failed to set accessible for " + reader, ioe);
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
                        logger.log(System.Logger.Level.WARNING, "failed to set accessible for " + writer, ioe);
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

    private void assumePropertyAccessorsTestNotDisabled() {
        final var clazz = getClass();
        final var disabled = AnnotationUtils.findAnnotation(clazz, __Disable_PropertyAccessors_Test.class);
        assumeThat(disabled)
                .as("%s on %s", __Disable_PropertyAccessors_Test.class, clazz)
                .isEmpty();
    }

    /**
     * Tests standard accessors with a new instance of {@link #entityClass}.
     *
     * @see __Disable_PropertyAccessors_Test
     * @see __Disable_PropertyAccessor_Test
     * @see #newEntityInstance()
     */
    @DisplayName("<instantiated>.accessors_DoesNotThrow_()")
    @Test
    final void propertyAccessors_DoesNotThrow_newEntityInstance() {
        assumePropertyAccessorsTestNotDisabled();
        propertyAccessors_DoesNotThrow_(newEntityInstance());
    }

    /**
     * Tests standard accessors with a new randomized instance of {@link #entityClass}.
     *
     * @see __Disable_PropertyAccessors_Test
     * @see __Disable_PropertyAccessor_Test
     * @see #newRandomizedEntityInstance()
     */
    @DisplayName("<randomized>.accessors_DoesNotThrow_()")
    @Test
    final void propertyAccessors_DoesNotThrow_newRandomizedEntityInstance() {
        assumePropertyAccessorsTestNotDisabled();
        final var randomized = newRandomizedEntityInstance();
        assumeThat(randomized).isNotEmpty();
        propertyAccessors_DoesNotThrow_(randomized.get());
    }

    // ----------------------------------------------------------------------------------------------------- entityClass

    /**
     * Returns a new instance of {@link #entityClass}.
     *
     * @return a new instance of {@link #entityClass}.
     */
    @Nonnull
    protected final T newEntityInstance() {
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
    protected final T newEntityInstanceSpy() {
        return spy(newEntityInstance());
    }

    /**
     * Returns a new randomized instance of {@link #entityClass}.
     *
     * @return a new randomized instance of {@link #entityClass}.
     */
    @Nonnull
    protected final Optional<T> newRandomizedEntityInstance() {
        return ___RandomizerUtils.newRandomizedInstanceOf(entityClass);
    }

    /**
     * Returns a new spy object of a new randomized instance of {@link #entityClass}.
     *
     * @return a new spy object of a new randomized instance of {@link #entityClass}; {@link Optional#empty() empty}
     *         when no randomizer found.
     */
    @Nonnull
    protected final Optional<T> newRandomizedEntityInstanceSpy() {
        return newRandomizedEntityInstance().map(Mockito::spy);
    }

    // -----------------------------------------------------------------------------------------------------------------
    protected final Class<T> entityClass;
}
