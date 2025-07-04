package com.github.jinahya.persistence.mapped.tests;

import com.github.jinahya.persistence.mapped.___Mapped;
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
public final class ___MappedEntityRandomizerUtils {

    /**
     * Returns a randomized instance of the specified entity class.
     *
     * @param typeClass the entity class to be randomized.
     * @param <T>       entity type parameter
     * @return an optional of the randomized entity; {@link Optional#empty() empty} when no randomizer found.
     * @see ___RandomizerUtils#newRandomizedInstanceOf(Class)
     */
    @Nonnull
    @SuppressWarnings({
            "java:S119" // Type parameter names should comply with a naming convention
    })
    public static <T extends ___Mapped<T>> Optional<T> newRandomizedInstanceOf(@Nonnull final Class<T> typeClass) {
        Objects.requireNonNull(typeClass, "typeClass is null");
        return ___RandomizerUtils.newRandomizedInstanceOf(typeClass);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private ___MappedEntityRandomizerUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
