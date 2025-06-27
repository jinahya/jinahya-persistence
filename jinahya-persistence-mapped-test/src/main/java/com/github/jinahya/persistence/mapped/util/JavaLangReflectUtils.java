package com.github.jinahya.persistence.mapped.util;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.lang.invoke.MethodHandles;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class JavaLangReflectUtils {

    private static final Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    @SuppressWarnings({
            "java:S112", // Generic exceptions should never be thrown
            "java:S3011" // Reflection should not be used to increase accessibility of classes, methods, or fields
    })
    @Nullable
    public static <T> T newInstanceOf(@Nonnull final Class<T> type) {
        Objects.requireNonNull(type, "type is null");
        try {
            final var constructor = type.getDeclaredConstructor();
            if (!constructor.canAccess(null)) {
                constructor.setAccessible(true);
            }
            return constructor.newInstance();
        } catch (final ReflectiveOperationException roe) {
            logger.log(Level.SEVERE, roe, () -> "failed to initialize a new instance of " + type);
            return null;
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    private JavaLangReflectUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
