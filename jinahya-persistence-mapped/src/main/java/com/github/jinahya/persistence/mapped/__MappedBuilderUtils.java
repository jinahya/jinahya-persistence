package com.github.jinahya.persistence.mapped;

import jakarta.annotation.Nonnull;

import java.util.Objects;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public final class __MappedBuilderUtils {

    public static <BUILDER extends __MappedBuilder<BUILDER, ?>> BUILDER reset(final @Nonnull BUILDER instance) {
        Objects.requireNonNull(instance, "instance is null");
        return ___BuilderUtils.reset(instance);
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    private __MappedBuilderUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
