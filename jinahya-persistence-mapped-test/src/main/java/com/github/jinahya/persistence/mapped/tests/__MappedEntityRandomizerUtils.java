package com.github.jinahya.persistence.mapped.tests;

import com.github.jinahya.persistence.mapped.__MappedEntity;
import jakarta.annotation.Nonnull;

import java.util.Objects;
import java.util.Optional;

@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class __MappedEntityRandomizerUtils {

    /**
     * Returns a randomized instance of the specified entity class.
     *
     * @param entityClass the entity class to be randomized.
     * @param <T>         entity type parameter
     * @return an optional of the randomized entity; {@link Optional#empty() empty} when no randomizer found.
     */
    @Nonnull
    public static <T extends __MappedEntity<T, ?>>
    Optional<T> newRandomizedInstanceOf(@Nonnull final Class<T> entityClass) {
        Objects.requireNonNull(entityClass, "entityClass is null");
        return ___RandomizerUtils.newRandomizedInstanceOf(entityClass);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private __MappedEntityRandomizerUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
