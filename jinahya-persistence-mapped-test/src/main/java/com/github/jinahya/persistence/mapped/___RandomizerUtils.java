package com.github.jinahya.persistence.mapped;

import com.github.jinahya.persistence.mapped.util.JavaLangReflectUtils;
import com.github.jinahya.persistence.mapped.util.JavaLangUtils;

import java.util.Objects;
import java.util.Optional;

public final class ___RandomizerUtils<T> {

    public static <T> Optional<T> newRandomizedInstanceOf(final Class<T> type) {
        Objects.requireNonNull(type, "type is null");
        return Optional.ofNullable(
                        JavaLangUtils.forAnyPostfixes(type, ___Randomizer.class, "Randomizer", "_Randomizer")
                )
                .map(JavaLangReflectUtils::newInstanceOf)
                .map(type::cast);
    }

    // -----------------------------------------------------------------------------------------------------------------
    ___RandomizerUtils() {
        super();
    }
}
