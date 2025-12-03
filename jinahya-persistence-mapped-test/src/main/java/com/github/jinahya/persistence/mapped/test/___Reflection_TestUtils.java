package com.github.jinahya.persistence.mapped.test;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.lang.reflect.Method;
import java.util.Arrays;

class ___Reflection_TestUtils {

    static @Nullable Method getPropertyGetter(final @Nonnull Class<?> clazz, final @Nonnull String name,
                                              final @Nonnull Class<?> type) {
        final var methodName = "get" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
        for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
            final var found = Arrays.stream(c.getDeclaredMethods())
                    .filter(m -> {
                        return m.getName().equals(methodName)
                               && m.getParameterCount() == 0
                               && type.isAssignableFrom(m.getReturnType());
                    })
                    .findFirst();
            if (found.isPresent()) {
                return found.get();
            }
        }
        return null;
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    private ___Reflection_TestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
