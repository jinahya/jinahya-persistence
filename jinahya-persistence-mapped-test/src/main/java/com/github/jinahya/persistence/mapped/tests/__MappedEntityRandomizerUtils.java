package com.github.jinahya.persistence.mapped.tests;

import com.github.jinahya.persistence.mapped.__MappedEntity;
import jakarta.annotation.Nonnull;

import java.util.Objects;
import java.util.Optional;

/**
 * Utilities for {@link __MappedEntityRandomizer}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class __MappedEntityRandomizerUtils {

    /**
     * Returns a randomized instance of the specified entity class.
     *
     * @param entityClass the entity class to be randomized.
     * @param <ENTITY>    entity type parameter
     * @return an optional of the randomized entity; {@link Optional#empty() empty} when no randomizer found.
     * @see ___RandomizerUtils#newRandomizedInstanceOf(Class)
     */
    @Nonnull
    @SuppressWarnings({
            "java:S119" // Type parameter names should comply with a naming convention
    })
    public static <ENTITY extends __MappedEntity<ENTITY, ?>>
    Optional<ENTITY> newRandomizedInstanceOf(@Nonnull final Class<ENTITY> entityClass) {
        Objects.requireNonNull(entityClass, "entityClass is null");
        return ___RandomizerUtils.newRandomizedInstanceOf(entityClass);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private __MappedEntityRandomizerUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
