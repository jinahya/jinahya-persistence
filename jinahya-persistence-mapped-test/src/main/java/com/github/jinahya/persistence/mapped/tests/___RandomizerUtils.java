package com.github.jinahya.persistence.mapped.tests;

import com.github.jinahya.persistence.mapped.tests.util.__JavaLangReflectUtils;
import com.github.jinahya.persistence.mapped.tests.util.__JavaLangUtils;

import java.util.Objects;
import java.util.Optional;

/**
 * Utilities for {@link ___Randomizer}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ___Randomizer
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class ___RandomizerUtils {

    static Optional<Class<?>> getRandomizerClassOf(final Class<?> type) {
        return Optional.ofNullable(
                        __JavaLangUtils.forAnyPostfixes(type, ___Randomizer.class, "Randomizer", "_Randomizer")
                )
//                .map(ic -> {
//                    final var typeClass = __JavaLangReflectUtils.getActualTypeParameter(ic, ___Randomizer.class, 0);
//                    assertThat(typeClass)
//                            .as("type class of the randomizer class: %s", ic)
//                            .isNotNull()
//                            .isAssignableTo(type);
//                    return ic;
//                })
                ;
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
