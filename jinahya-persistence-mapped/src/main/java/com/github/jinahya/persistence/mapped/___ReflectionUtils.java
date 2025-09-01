package com.github.jinahya.persistence.mapped;

import jakarta.annotation.Nonnull;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.function.Consumer;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S3011" // Reflection should not be used to increase accessibility of classes, methods, or fields
})
final class ___ReflectionUtils {

    static <T> void acceptFields(@Nonnull final Class<T> clazz,
                                 @Nonnull final Consumer<? super Field> consumer) {
        Objects.requireNonNull(clazz, "clazz is null");
        Objects.requireNonNull(consumer, "consumer is null");
        for (Class<?> c = clazz; c != null && c != Object.class; c = c.getSuperclass()) {
            for (final var field : c.getDeclaredFields()) {
                consumer.accept(field);
            }
        }
    }

    static <T> T reset(@Nonnull final T instance) {
        Objects.requireNonNull(instance, "instance is null");
        acceptFields(
                instance.getClass(),
                f -> {
                    if (!f.canAccess(instance)) {
                        f.setAccessible(true);
                    }
                    final var type = f.getType();
                    try {
                        if (type.isPrimitive()) {
                            if (type == boolean.class) {
                                f.setBoolean(instance, false);
                            } else if (type == char.class) {
                                f.setChar(instance, '\0');
                            } else {
                                f.set(instance, 0);
                            }
                        } else {
                            f.set(instance, null);
                        }
                    } catch (final IllegalAccessException iae) {
                        throw new RuntimeException("failed to reset " + f + " on " + instance, iae);
                    }
                }
        );
        return instance;
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    private ___ReflectionUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
