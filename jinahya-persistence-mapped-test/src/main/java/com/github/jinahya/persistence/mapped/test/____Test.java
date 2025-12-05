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
import java.lang.System.Logger.Level;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InaccessibleObjectException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.mockito.Mockito.spy;

/**
 * An abstract class for testing a specific class.
 *
 * @param <T> target type parameter
 * @see ___Randomizer
 */
@SuppressWarnings({
        "java:S100", // Method names should comply with a naming convention
        "java:S101", // Class names should comply with a naming convention
        "java:S119", // Type parameter names should comply with a naming convention
        "java:S5960" // Assertions should not be used in production code
})
public abstract class ____Test<T> {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance to test the specified target class.
     *
     * @param targetClass the target class to test.
     * @see #targetClass
     */
    protected ____Test(final @Nonnull Class<T> targetClass) {
        super();
        this.targetClass = Objects.requireNonNull(targetClass, "targetClass is null");
    }

    // -------------------------------------------------------------------------------------------------------- toString

    /**
     * Verifies that the result of {@link Object#toString() toString()} is not blank.
     *
     * @param targetInstance the instance to test.
     * @see #toString_NotBlank_newInstantiatedTargetInstance()
     * @see #toString_NotBlank_newRandomizedTargetInstance()
     */
    private void toString_NotBlank_(final @Nonnull T targetInstance) {
        Objects.requireNonNull(targetInstance, "targetInstance is null");
        final var string = targetInstance.toString();
        assertThat(string)
                .as("%s.toString()", targetInstance)
                .isNotBlank();
        logger.log(Level.DEBUG, "instance: {0}", targetInstance);
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
     * {@link #newInstantiatedTargetInstance()} is not blank.
     *
     * @see __Disable_ToString_Test
     * @see #newInstantiatedTargetInstance()
     */
    @DisplayName("<instantiated>.toString()!blank")
    @Test
    final void toString_NotBlank_newInstantiatedTargetInstance() {
        assumeToStringTestNotDisabled();
        toString_NotBlank_(newInstantiatedTargetInstance());
    }

    /**
     * Verifies that the result of {@link Object#toString() toString()} of an instance from the
     * {@link #newRandomizedTargetInstance()} is not blank.
     *
     * @see __Disable_ToString_Test
     * @see #newRandomizedTargetInstance()
     */
    @DisplayName("<randomized>.toString()!blank")
    @Test
    final void toString_NotBlank_newRandomizedTargetInstance() {
        assumeToStringTestNotDisabled();
        final var randomized = newRandomizedTargetInstance().orElse(null);
        assumeThat(randomized)
                .as("new randomized target instance")
                .isNotNull();
        toString_NotBlank_(randomized);
    }

    // ------------------------------------------------------------------------------------------------- equals/hashCode

    /**
     * Verifies the {@link #equals(Object)} method (and {@link #hashCode()} method) of the {@link #targetClass} using an
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
            final var verifier = equals_Verify_(EqualsVerifier.forClass(targetClass));
            verifier.verify();
            return;
        }
        final var equalsVerifierReference = new AtomicReference<>(EqualsVerifier.forClass(targetClass));
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
            logger.log(Level.DEBUG, "invoking {0}({1})", m, equalsVerifier);
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
     * @apiNote the {@code equals_Verify_(SingleTypeEqualsVerifierApi)} method of {@code __Object_Test} class
     *         simply returns the {@code equalsVerifier}.
     */
    @Nonnull
    protected SingleTypeEqualsVerifierApi<T> equals_Verify_(
            final @Nonnull SingleTypeEqualsVerifierApi<T> equalsVerifier) {
        return equalsVerifier
                ;
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Tests standard accessors with the specified instance.
     *
     * @param targetInstance the instance to test.
     */
    @SuppressWarnings({
            "java:S3011" // Reflection should not be used to increase accessibility of classes, methods, or fields
    })
    private void propertyAccessors_DoesNotThrow_(final @Nonnull T targetInstance) {
        Objects.requireNonNull(targetInstance, "targetInstance is null");
        try {
            final var info = Introspector.getBeanInfo(targetClass);
            for (final var descriptor : info.getPropertyDescriptors()) {
                final var reader = descriptor.getReadMethod();
                if (reader == null
                    || reader.isAnnotationPresent(Transient.class)
                    || reader.isAnnotationPresent(__Disable_PropertyAccessor_Test.class)) {
                    continue;
                }
                if (!reader.canAccess(targetInstance)) {
                    try {
                        reader.setAccessible(true);
                    } catch (final InaccessibleObjectException ioe) {
                        logger.log(Level.WARNING, "failed to set accessible for " + reader, ioe);
                    }
                }
                final var value = reader.invoke(targetInstance);
                final var writer = descriptor.getWriteMethod();
                if (writer == null || writer.isAnnotationPresent(Transient.class)) {
                    continue;
                }
                if (!writer.canAccess(targetInstance)) {
                    try {
                        writer.setAccessible(true);
                    } catch (final InaccessibleObjectException ioe) {
                        logger.log(Level.WARNING, "failed to set accessible for " + writer, ioe);
                    }
                }
                assertThatCode(() -> writer.invoke(targetInstance, value))
                        .as("%s.%s(%s)", targetInstance, writer.getName(), value)
                        .doesNotThrowAnyException();
            }
        } catch (final Exception e) {
            throw new RuntimeException("failed to test accessors of " + targetInstance, e);
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
     * Tests standard accessors with a new instance of {@link #targetClass}.
     *
     * @see __Disable_PropertyAccessors_Test
     * @see __Disable_PropertyAccessor_Test
     * @see #newInstantiatedTargetInstance()
     */
    @DisplayName("<instantiated>.accessors_DoesNotThrow_()")
    @Test
    final void propertyAccessors_DoesNotThrow_newInstantiatedTargetInstance() {
        assumePropertyAccessorsTestNotDisabled();
        propertyAccessors_DoesNotThrow_(newInstantiatedTargetInstance());
    }

    /**
     * Tests standard accessors with a new randomized instance of {@link #targetClass}.
     *
     * @see __Disable_PropertyAccessors_Test
     * @see __Disable_PropertyAccessor_Test
     * @see #newRandomizedTargetInstance()
     */
    @DisplayName("<randomized>.accessors_DoesNotThrow_()")
    @Test
    final void propertyAccessors_DoesNotThrow_newRandomizedTargetInstance() {
        assumePropertyAccessorsTestNotDisabled();
        final var randomized = newRandomizedTargetInstance();
        assumeThat(randomized).isNotEmpty();
        propertyAccessors_DoesNotThrow_(randomized.get());
    }

    // ----------------------------------------------------------------------------------------------------- targetClass

    /**
     * Returns a new instance of {@link #targetClass}.
     *
     * @return a new instance of {@link #targetClass}.
     */
    @Nonnull
    protected final T newInstantiatedTargetInstance() {
        return ___InstantiatorUtils.newInstantiatedInstanceOf(targetClass)
                .orElseGet(() -> ReflectionUtils.newInstance(targetClass))
                ;
    }

    /**
     * Returns a new spy object of {@link #newInstantiatedTargetInstance()}.
     *
     * @param processor a processor to apply to {@link #newInstantiatedTargetInstance()} before returning a spy object
     * @return a new spy object of {@link #newInstantiatedTargetInstance()}.
     */
    @Nonnull
    protected final T newInstantiatedTargetInstanceSpy(final UnaryOperator<T> processor) {
        Objects.requireNonNull(processor, "processor is null");
        return spy(processor.apply(newInstantiatedTargetInstance()));
    }

    /**
     * Returns a new spy object of {@link #newInstantiatedTargetInstance()}.
     *
     * @return a new spy object of {@link #newInstantiatedTargetInstance()}.
     */
    @Nonnull
    protected final T newInstantiatedTargetInstanceSpy() {
        return newInstantiatedTargetInstanceSpy(UnaryOperator.identity());
    }

    /**
     * Returns a new randomized instance of {@link #targetClass}.
     *
     * @return a new randomized instance of {@link #targetClass}.
     */
    @Nonnull
    protected final Optional<T> newRandomizedTargetInstance() {
        return ___RandomizerUtils.newRandomizedInstanceOf(targetClass);
    }

    /**
     * Returns a new spy object of a new randomized instance of {@link #targetClass}.
     *
     * @param processor a processor to apply to a new randomized instance of {@link #targetClass} before returning a
     *                  spy
     * @return a new spy object of a new randomized instance of {@link #targetClass}; {@link Optional#empty() empty}
     *         when no randomizer found.
     */
    @Nonnull
    protected final Optional<T> newRandomizedTargetInstanceSpy(final UnaryOperator<T> processor) {
        Objects.requireNonNull(processor, "processor is null");
        return newRandomizedTargetInstance().map(processor).map(Mockito::spy);
    }

    /**
     * Returns a new spy object of a new randomized instance of {@link #targetClass}.
     *
     * @return a new spy object of a new randomized instance of {@link #targetClass}; {@link Optional#empty() empty}
     *         when no randomizer found.
     */
    @Nonnull
    protected final Optional<T> newRandomizedTargetInstanceSpy() {
        return newRandomizedTargetInstanceSpy(UnaryOperator.identity());
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The class of {@link T} to test.
     */
    protected final Class<T> targetClass;
}
