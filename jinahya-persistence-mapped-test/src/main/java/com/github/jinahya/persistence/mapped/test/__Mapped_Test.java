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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.mockito.Mockito.spy;

/**
 * A class for testing a specific subclass of {@link __Mapped} class.
 *
 * @param <MAPPED> target type parameter
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
     * Verifies that the result of {@link Object#toString() mappedInstance.toString()} is not blank.
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
    }

    private void assumeToStringTestNotDisabled() {
        final var clazz = getClass();
        final var disabled = AnnotationUtils.findAnnotation(clazz, __Disable_ToString_Test.class);
        assumeThat(disabled)
                .as("%s on %s", __Disable_ToString_Test.class, clazz)
                .isEmpty();
    }

    /**
     * Invokes {@link #toString_NotBlank_(__Mapped)} method with a new instance of {@link #mappedClass}.
     *
     * @see #newMappedInstance()
     * @see #toString_NotBlank_(__Mapped)
     */
    @DisplayName("newMappedInstance().toString()!blank")
    @Test
    void toString_NotBlank_newMappedInstance() {
        assumeToStringTestNotDisabled();
        toString_NotBlank_(newMappedInstance());
    }

    /**
     * Invokes {@link #toString_NotBlank_(__Mapped)} method with a new randomized instance of {@link #mappedClass}.
     *
     * @see #newRandomizedMappedInstance()
     * @see #toString_NotBlank_(__Mapped)
     */
    @DisplayName("newRandomizedMappedInstance().toString()!blank")
    @Test
    void toString_NotBlank_newRandomizedMappedInstance() {
        assumeToStringTestNotDisabled();
        newRandomizedMappedInstance().ifPresent(this::toString_NotBlank_);
    }

    // ------------------------------------------------------------------------------------------------- equals/hashCode

    /**
     * Verifies the {@link #equals(Object)} method (and {@link #hashCode()} method) of the {@link #mappedClass} using an
     * equals-verifier configured with {@link #equals_Verify_(SingleTypeEqualsVerifierApi)} method.
     *
     * @implNote This method is not annotated with the {@link Test} annotation. Override this method, and put
     *         {@link Test} to verify the {@link #equals(Object)} method (and {@link #hashCode()} method) of the
     *         {@link #mappedClass}
     * @see #equals_Verify_(SingleTypeEqualsVerifierApi)
     */
    @DisplayName("equals/hashCode")
    @Test
    final void equals_Verify_() {
        {
            final var clazz = getClass();
            final var disabled = AnnotationUtils.findAnnotation(clazz, __Disable_Equals_Test.class);
            assumeThat(disabled)
                    .as("%s on %s", __Disable_Equals_Test.class, clazz)
                    .isEmpty();
        }
        equals_Verify_(EqualsVerifier.forClass(mappedClass)).verify();
    }

    /**
     * Configures specified equals verifier.
     *
     * @param equalsVerifier the equals verifier to configure.
     * @return given {@code equalsVerifier}.
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
                if (reader == null || reader.isAnnotationPresent(Transient.class)) {
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
     * @see #newMappedInstance()
     * @see #propertyAccessors_DoesNotThrow_(__Mapped)
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
     * @see #newRandomizedMappedInstance()
     * @see #propertyAccessors_DoesNotThrow_(__Mapped)
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
