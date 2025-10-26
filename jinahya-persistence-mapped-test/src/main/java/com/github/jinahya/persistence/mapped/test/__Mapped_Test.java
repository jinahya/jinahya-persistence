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

import com.github.jinahya.persistence.mapped.__Mapped;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.mockito.Mockito.spy;

/**
 * An abstract class for testing a specific subclass of {@link __Mapped} class.
 *
 * @param <MAPPED> target type parameter
 * @see __Mapped_Randomizer
 */
@SuppressWarnings({
        "java:S100", // Method names should comply with a naming convention
        "java:S101", // Class names should comply with a naming convention
        "java:S119", // Type parameter names should comply with a naming convention
        "java:S5960" // Assertions should not be used in production code
})
public abstract class __Mapped_Test<MAPPED extends __Mapped> {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance to test the specified mapped class.
     *
     * @param mappedClass the mapped class to test.
     * @see #mappedClass
     */
    protected __Mapped_Test(@Nonnull final Class<MAPPED> mappedClass) {
        super();
        this.mappedClass = Objects.requireNonNull(mappedClass, "mappedClass is null");
    }

    // -------------------------------------------------------------------------------------------------------- toString

    /**
     * Verifies that the result of {@link Object#toString() toString()} is not blank.
     *
     * @param mappedInstance the instance to test.
     * @see #toString_NotBlank_newMappedInstance()
     * @see #toString_NotBlank_newRandomizedMappedInstance()
     */
    private void toString_NotBlank_(@Nonnull final MAPPED mappedInstance) {
        Objects.requireNonNull(mappedInstance, "mappedInstance is null");
        final var string = mappedInstance.toString();
        assertThat(string)
                .as("%s.toString()", mappedInstance)
                .isNotBlank();
        logger.log(Level.DEBUG, "instance: {0}", mappedInstance);
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
     * {@link #newMappedInstance()} is not blank.
     *
     * @see __Disable_ToString_Test
     * @see #newMappedInstance()
     * @see #toString_NotBlank_(__Mapped)
     */
    @DisplayName("<instantiated>.toString()!blank")
    @Test
    final void toString_NotBlank_newMappedInstance() {
        assumeToStringTestNotDisabled();
        toString_NotBlank_(newMappedInstance());
    }

    /**
     * Verifies that the result of {@link Object#toString() toString()} of an instance from the
     * {@link #newRandomizedMappedInstance()} is not blank.
     *
     * @see __Disable_ToString_Test
     * @see #newRandomizedMappedInstance()
     * @see #toString_NotBlank_(__Mapped)
     */
    @DisplayName("<randomized>.toString()!blank")
    @Test
    final void toString_NotBlank_newRandomizedMappedInstance() {
        assumeToStringTestNotDisabled();
        final var randomized = newRandomizedMappedInstance().orElse(null);
        assumeThat(randomized)
                .as("new randomized mapped instance")
                .isNotNull();
        toString_NotBlank_(randomized);
    }

    // ------------------------------------------------------------------------------------------------- equals/hashCode

    /**
     * Verifies the {@link #equals(Object)} method (and {@link #hashCode()} method) of the {@link #mappedClass} using an
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
            final var verifier = equals_Verify_(EqualsVerifier.forClass(mappedClass));
            verifier.verify();
            return;
        }
        final var equalsVerifierReference = new AtomicReference<>(EqualsVerifier.forClass(mappedClass));
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
                    equalsVerifierReference.set((SingleTypeEqualsVerifierApi<MAPPED>) result);
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
     * @apiNote the {@code equals_Verify_(SingleTypeEqualsVerifierApi)} method of {@code __Mapped_Test} class
     *         simply returns the {@code equalsVerifier}.
     */
    @Nonnull
    protected SingleTypeEqualsVerifierApi<MAPPED> equals_Verify_(
            @Nonnull final SingleTypeEqualsVerifierApi<MAPPED> equalsVerifier) {
        return equalsVerifier
                ;
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Tests standard accessors with the specified instance.
     *
     * @param mappedInstance the instance to test.
     */
    @SuppressWarnings({
            "java:S3011" // Reflection should not be used to increase accessibility of classes, methods, or fields
    })
    private void propertyAccessors_DoesNotThrow_(@Nonnull final MAPPED mappedInstance) {
        Objects.requireNonNull(mappedInstance, "mappedInstance is null");
        try {
            final var info = Introspector.getBeanInfo(mappedClass);
            for (final var descriptor : info.getPropertyDescriptors()) {
                final var reader = descriptor.getReadMethod();
                if (reader == null
                    || reader.isAnnotationPresent(Transient.class)
                    || reader.isAnnotationPresent(__Disable_PropertyAccessor_Test.class)) {
                    continue;
                }
                if (!reader.canAccess(mappedInstance)) {
                    try {
                        reader.setAccessible(true);
                    } catch (final InaccessibleObjectException ioe) {
                        logger.log(Level.WARNING, "failed to set accessible for " + reader, ioe);
                    }
                }
                final var value = reader.invoke(mappedInstance);
                final var writer = descriptor.getWriteMethod();
                if (writer == null || writer.isAnnotationPresent(Transient.class)) {
                    continue;
                }
                if (!writer.canAccess(mappedInstance)) {
                    try {
                        writer.setAccessible(true);
                    } catch (final InaccessibleObjectException ioe) {
                        logger.log(Level.WARNING, "failed to set accessible for " + writer, ioe);
                    }
                }
                assertThatCode(() -> writer.invoke(mappedInstance, value))
                        .as("%s.%s(%s)", mappedInstance, writer.getName(), value)
                        .doesNotThrowAnyException();
            }
        } catch (final Exception e) {
            throw new RuntimeException("failed to test accessors of " + mappedInstance, e);
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
     * Tests standard accessors with a new instance of {@link #mappedClass}.
     *
     * @see __Disable_PropertyAccessors_Test
     * @see __Disable_PropertyAccessor_Test
     * @see #newMappedInstance()
     */
    @DisplayName("newMappedInstance().accessors_DoesNotThrow_()")
    @Test
    final void propertyAccessors_DoesNotThrow_newMappedInstance() {
        assumePropertyAccessorsTestNotDisabled();
        propertyAccessors_DoesNotThrow_(newMappedInstance());
    }

    /**
     * Tests standard accessors with a new randomized instance of {@link #mappedClass}.
     *
     * @see __Disable_PropertyAccessors_Test
     * @see __Disable_PropertyAccessor_Test
     * @see #newRandomizedMappedInstance()
     */
    @DisplayName("newRandomizedMappedInstance().accessors_DoesNotThrow_()")
    @Test
    final void propertyAccessors_DoesNotThrow_newRandomizedMappedInstance() {
        assumePropertyAccessorsTestNotDisabled();
        newRandomizedMappedInstance().ifPresent(this::propertyAccessors_DoesNotThrow_);
    }

    // ----------------------------------------------------------------------------------------------------- mappedClass

    /**
     * Returns a new instance of {@link #mappedClass}.
     *
     * @return a new instance of {@link #mappedClass}.
     */
    @Nonnull
    protected final MAPPED newMappedInstance() {
        return ___InstantiatorUtils.newInstantiatedInstanceOf(mappedClass)
                .orElseGet(() -> ReflectionUtils.newInstance(mappedClass))
                ;
    }

    /**
     * Returns a new spy object of {@link #newMappedInstance()}.
     *
     * @return a new spy object of {@link #newMappedInstance()}.
     */
    @Nonnull
    protected final MAPPED newMappedInstanceSpy() {
        return spy(newMappedInstance());
    }

    /**
     * Returns a new randomized instance of {@link #mappedClass}.
     *
     * @return a new randomized instance of {@link #mappedClass}.
     */
    @Nonnull
    protected final Optional<MAPPED> newRandomizedMappedInstance() {
        return __Mapped_RandomizerUtils.newRandomizedInstanceOf(mappedClass);
    }

    /**
     * Returns a new spy object of a new randomized instance of {@link #mappedClass}.
     *
     * @return a new spy object of a new randomized instance of {@link #mappedClass}; {@link Optional#empty() empty}
     *         when no randomizer found.
     */
    @Nonnull
    protected final Optional<MAPPED> newRandomizedMappedInstanceSpy() {
        return newRandomizedMappedInstance().map(Mockito::spy);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The class of {@link MAPPED} to test.
     */
    protected final Class<MAPPED> mappedClass;
}
