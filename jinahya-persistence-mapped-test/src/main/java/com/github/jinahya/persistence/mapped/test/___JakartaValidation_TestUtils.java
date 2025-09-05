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
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * Utilities for the <a href="https://beanvalidation.org/">Jakarta Validation</a>.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://beanvalidation.org/">Jakarta Validation</a>
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
final class ___JakartaValidation_TestUtils {

    private static <T> T requireNonNullObject(final T object) {
        return Objects.requireNonNull(object, "object is null");
    }

    private static Class<?>[] requireNonNullGroups(final Class<?>... groups) {
        return Objects.requireNonNull(groups, "groups is null");
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Validates the specified object using the specified validator and groups.
     *
     * @param validator the validator.
     * @param object    the object to validate.
     * @param groups    the validation groups.
     * @param <T>       object type parameter
     * @return a set of constraint violations of the {@code object}; {@code empty} if the {@code object} is valid.
     */
    @Nonnull
    static <T> Set<ConstraintViolation<T>> validate(@Nonnull final Validator validator, @Nonnull final T object,
                                                    @Nonnull final Class<?>... groups) {
        Objects.requireNonNull(validator, "validator is null");
        return validator.validate(
                requireNonNullObject(object),
                requireNonNullGroups(groups)
        );
    }

    /**
     * Verifies that the specified object is valid using the specified validator and groups.
     *
     * @param validator the validator.
     * @param object    the object to validate.
     * @param groups    the validation groups.
     * @param <T>       object type parameter
     * @return given {@code object}.
     * @throws ConstraintViolationException if the {@code object} is invalid.
     */
    static <T> T requireValid(@Nonnull final Validator validator, @Nonnull final T object,
                              @Nonnull final Class<?>... groups) {
        final var violations = validate(validator, object, groups);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        return object;
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Applies the specified function to the default validator factory.
     *
     * @param function the function.
     * @param <R>      result type parameter
     * @return the result of the {@code function}.
     */
    static <R> R applyValidationFactory(
            @Nonnull final Function<? super ValidatorFactory, ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            return function.apply(factory);
        }
    }

    /**
     * Applies the specified function to the default validator.
     *
     * @param function the function.
     * @param <R>      result type parameter
     * @return the result of the {@code function}.
     */
    static <R> R applyValidator(@Nonnull final Function<? super Validator, ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return applyValidationFactory(f -> function.apply(f.getValidator()));
    }

    /**
     * Validates the specified object using the default validator and specified groups.
     *
     * @param object the object to validate.
     * @param groups the validation groups.
     * @param <T>    object type parameter
     * @return a set of constraint violations of the {@code object}; {@code empty} if the {@code object} is valid.
     * @see #validate(Validator, Object, Class...)
     */
    static <T> Set<ConstraintViolation<T>> validate(final T object, final Class<?>... groups) {
        requireNonNullObject(object);
        requireNonNullGroups(groups);
        return applyValidator(v -> validate(v, object, groups));
    }

    /**
     * Verifies that the specified object is valid using the default validator and specified validation groups.
     *
     * @param object the object to validate.
     * @param groups the validation groups.
     * @param <T>    object type parameter
     * @return given {@code object}.
     * @throws ConstraintViolationException if the {@code object} is invalid.
     * @see #requireValid(Validator, Object, Class...)
     */
    @Nonnull
    static <T> T requireValid(@Nonnull final T object, @Nonnull final Class<?>... groups) {
        requireNonNullObject(object);
        requireNonNullGroups(groups);
        return applyValidator(v -> requireValid(v, object, groups));
    }

    // -----------------------------------------------------------------------------------------------------------------
    private ___JakartaValidation_TestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
