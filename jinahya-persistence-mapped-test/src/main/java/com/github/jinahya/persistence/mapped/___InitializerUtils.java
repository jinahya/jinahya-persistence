package com.github.jinahya.persistence.mapped;

import com.github.jinahya.persistence.mapped.util.JavaLangUtils;

import java.util.Objects;
import java.util.Optional;

@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class ___InitializerUtils {

    // -----------------------------------------------------------------------------------------------------------------
    public static <T> Optional<T> newInitializedInstanceOf(final Class<T> type) {
        Objects.requireNonNull(type, "type is null");
        return Optional.ofNullable(
                        JavaLangUtils.forAnyPostfixes(type, ___Initializer.class, "Initializer", "_Initializer")
                )
                .map(type::cast);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private ___InitializerUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
