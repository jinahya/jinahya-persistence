package com.github.jinahya.persistence.mapped.tests;

import com.github.jinahya.persistence.mapped.__MappedEntity;
import jakarta.annotation.Nonnull;

import java.util.Objects;
import java.util.Optional;

@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class __MappedEntityInstantiatorUtils {

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
        return ___InstantiatorUtils.newInitializedInstanceOf(entityClass);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private __MappedEntityInstantiatorUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
