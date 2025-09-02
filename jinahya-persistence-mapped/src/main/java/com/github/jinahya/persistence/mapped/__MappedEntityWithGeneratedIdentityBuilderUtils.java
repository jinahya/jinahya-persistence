package com.github.jinahya.persistence.mapped;

import jakarta.annotation.Nonnull;

import java.util.Objects;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public final class __MappedEntityWithGeneratedIdentityBuilderUtils {

    public static <BUILDER extends __MappedEntityWithGeneratedIdentityBuilder<BUILDER, ?>> BUILDER reset(
            @Nonnull final BUILDER instance) {
        Objects.requireNonNull(instance, "instance is null");
        return __BuilderUtils.reset(instance);
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    private __MappedEntityWithGeneratedIdentityBuilderUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
