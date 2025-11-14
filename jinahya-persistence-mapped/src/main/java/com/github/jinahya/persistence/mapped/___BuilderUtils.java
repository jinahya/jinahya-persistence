package com.github.jinahya.persistence.mapped;

import jakarta.annotation.Nonnull;

import java.util.Objects;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public final class ___BuilderUtils {

    public static <BUILDER extends ___Builder<BUILDER, ?>> BUILDER reset(final @Nonnull BUILDER instance) {
        Objects.requireNonNull(instance, "instance is null");
        return ___ReflectionUtils.reset(instance);
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    private ___BuilderUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
