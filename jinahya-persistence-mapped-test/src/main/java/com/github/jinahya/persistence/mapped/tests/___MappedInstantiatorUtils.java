package com.github.jinahya.persistence.mapped.tests;

import com.github.jinahya.persistence.mapped.___Mapped;
import jakarta.annotation.Nonnull;

import java.util.Objects;
import java.util.Optional;

@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class ___MappedInstantiatorUtils {

    /**
     * Returns an initialized instance of the specified entity class.
     *
     * @param typeClass the entity class to be initialized.
     * @param <T>       entity type parameter
     * @return an optional of the initialized entity; {@link Optional#empty() empty} when no initializer found.
     */
    @Nonnull
    public static <T extends ___Mapped<T>> Optional<T> newInitializedInstanceOf(@Nonnull final Class<T> typeClass) {
        Objects.requireNonNull(typeClass, "typeClass is null");
        return ___InstantiatorUtils.newInstanceOf(typeClass);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private ___MappedInstantiatorUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
