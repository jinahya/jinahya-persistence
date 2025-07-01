package com.github.jinahya.persistence.mapped;

import com.github.jinahya.persistence.mapped.util.__JavaLangReflectUtils;
import com.github.jinahya.persistence.mapped.util.__JavaLangUtils;

import java.util.Objects;
import java.util.Optional;

@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class ___InitializerUtils {

    // -----------------------------------------------------------------------------------------------------------------
    static <T> Optional<Class<?>> getInitializerClassOf(final Class<T> type) {
        return Optional.ofNullable(
                __JavaLangUtils.forAnyPostfixes(type, ___Initializer.class, "Initializer", "_Initializer")
        );
    }

    @SuppressWarnings({
            "unchecked"
    })
    static <T> Optional<___Initializer<T>> newInitializerInstanceOf(final Class<T> type) {
        return getInitializerClassOf(type)
                .map(__JavaLangReflectUtils::newInstanceOf)
                .map(i -> (___Initializer<T>) i);
    }

    public static <T> Optional<T> newInitializedInstanceOf(final Class<T> type) {
        Objects.requireNonNull(type, "type is null");
        return newInitializerInstanceOf(type)
                .map(___Initializer::get);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private ___InitializerUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
