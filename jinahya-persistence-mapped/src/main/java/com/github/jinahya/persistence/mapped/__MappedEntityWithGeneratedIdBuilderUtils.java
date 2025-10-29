package com.github.jinahya.persistence.mapped;

import jakarta.annotation.Nonnull;

import java.util.Objects;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public final class __MappedEntityWithGeneratedIdBuilderUtils {

    public static <BUILDER extends __MappedEntityWithGeneratedIdBuilder<BUILDER, ?>> BUILDER reset(
            @Nonnull final BUILDER instance) {
        Objects.requireNonNull(instance, "instance is null");
        return ___BuilderUtils.reset(instance);
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    private __MappedEntityWithGeneratedIdBuilderUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
