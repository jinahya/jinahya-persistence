package com.github.jinahya.persistence.mapped.util;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.Arrays;
import java.util.Objects;

public final class JavaLangUtils {

    @Nullable
    public static Class<?> forAnyPostfixes(@Nonnull final Class<?> type, final Class<?> supertype,
                                           @Nonnull final String... postfixes) {
        assert type != null;
        assert postfixes != null;
        return Arrays.stream(postfixes)
                .filter(Objects::nonNull)
                .map(String::strip)
                .filter(v -> !v.isBlank())
                .map(p -> type.getName() + p)
                .map(n -> {
                    try {
                        return Class.forName(n);
                    } catch (final ClassNotFoundException cnfe) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .filter(c -> supertype == null || supertype.isAssignableFrom(c))
                .findAny()
                .orElse(null);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private JavaLangUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
