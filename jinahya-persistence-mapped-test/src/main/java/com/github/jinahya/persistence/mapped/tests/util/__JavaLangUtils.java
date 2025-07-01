package com.github.jinahya.persistence.mapped.tests.util;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.Arrays;
import java.util.Objects;

public final class __JavaLangUtils {

    @Nullable
    public static Class<?> forAnyPostfixes(@Nonnull final Class<?> type, final Class<?> supertype,
                                           @Nonnull final String... postfixes) {
        Objects.requireNonNull(type, "type is null");
        if (Objects.requireNonNull(postfixes, "postfixes is null").length == 0) {
            throw new IllegalArgumentException("postfixes is empty");
        }
        final String typeName = type.getName();
        return Arrays.stream(postfixes)
                .filter(Objects::nonNull)
                .map(String::strip)
                .filter(v -> !v.isBlank())
                .<String>map((String postfix) -> {
                    final var name = typeName + postfix;
                    return name;
                })
                .map((String className) -> {
                    try {
                        return Class.forName(className);
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
    private __JavaLangUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
