package com.github.jinahya.persistence.mapped;

import jakarta.annotation.Nonnull;

import java.util.Objects;
import java.util.Optional;

@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class __MappedEntityInitializerUtils {

    /**
     * Returns an initialized instance of the specified entity class.
     *
     * @param entityClass the entity class to be initialized.
     * @param <T>         entity type parameter
     * @return an optional of the initialized entity; {@link Optional#empty() empty} when no initializer found.
     */
    @Nonnull
    public static <T extends __MappedEntity<T, ?>> Optional<T> newInitializedInstanceOf(
            @Nonnull final Class<T> entityClass) {
        Objects.requireNonNull(entityClass, "entityClass is null");
        return ___InitializerUtils.newInitializedInstanceOf(entityClass);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private __MappedEntityInitializerUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
