package com.github.jinahya.persistence.mapped.tests.util;

import jakarta.annotation.Nonnull;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

public final class __JakartaValidationTestUtils {

    public static <T> Set<ConstraintViolation<T>> validate(@Nonnull final Validator validator, @Nonnull final T object,
                                                           @Nonnull final Class<?>... groups) {
        Objects.requireNonNull(validator, "validator is null");
        Objects.requireNonNull(object, "object is null");
        Objects.requireNonNull(groups, "groups is null");
        return validator.validate(object, groups);
    }

    public static <T> T requireValid(@Nonnull final Validator validator, @Nonnull final T object,
                                     @Nonnull final Class<?>... groups) {
        Objects.requireNonNull(validator, "validator is null");
        Objects.requireNonNull(object, "object is null");
        Objects.requireNonNull(groups, "groups is null");
        final var violations = validate(validator, object, groups);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        return object;
    }

    // -----------------------------------------------------------------------------------------------------------------
    public static <R> R applyValidationFactory(final Function<? super ValidatorFactory, ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            return function.apply(factory);
        }
    }

    public static <R> R applyValidator(final Function<? super Validator, ? extends R> function) {
        Objects.requireNonNull(function, "function is null");
        return applyValidationFactory(f -> function.apply(f.getValidator()));
    }

    public static <T> Set<ConstraintViolation<T>> validate(final T object, final Class<?>... groups) {
        return applyValidator(v -> validate(v, object, groups));
    }

    public static <T> T requireValid(final T object, final Class<?>... groups) {
        Objects.requireNonNull(object, "object is null");
        Objects.requireNonNull(groups, "groups is null");
        return applyValidator(v -> requireValid(v, object, groups));
    }

    // -----------------------------------------------------------------------------------------------------------------
    private __JakartaValidationTestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
