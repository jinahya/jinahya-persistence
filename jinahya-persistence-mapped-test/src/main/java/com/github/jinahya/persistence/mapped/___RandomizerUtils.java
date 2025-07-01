package com.github.jinahya.persistence.mapped;

import com.github.jinahya.persistence.mapped.util.__JavaLangReflectUtils;
import com.github.jinahya.persistence.mapped.util.__JavaLangUtils;

import java.util.Objects;
import java.util.Optional;

@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class ___RandomizerUtils {

    static Optional<Class<?>> getRandomizerClassOf(final Class<?> type) {
        return Optional.ofNullable(
                __JavaLangUtils.forAnyPostfixes(type, ___Randomizer.class, "Randomizer", "_Randomizer")
        );
    }

    @SuppressWarnings({
            "unchecked"
    })
    static <T> Optional<___Randomizer<T>> newRandomizerInstanceOf(final Class<T> type) {
        return getRandomizerClassOf(type)
                .map(__JavaLangReflectUtils::newInstanceOf)
                .map(i -> (___Randomizer<T>) i)
                ;
    }

    public static <T> Optional<T> newRandomizedInstanceOf(final Class<T> type) {
        Objects.requireNonNull(type, "type is null");
        return newRandomizerInstanceOf(type)
                .map(___Randomizer::get);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private ___RandomizerUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
