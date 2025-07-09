package com.github.jinahya.persistence.mapped.tests;

import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityManager;

import java.util.Objects;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S3011" // Reflection should not be used to increase accessibility of classes, methods, or fields
})
public abstract class ___Persister<T> {

    protected ___Persister(@Nonnull final Class<T> type) {
        super();
        this.type = Objects.requireNonNull(type, "type is null");
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Persists specified value using specified value manager.
     *
     * @param value         the value to persist.
     * @param entityManager the value manager to use.
     */
    public abstract void persist(final T value, final EntityManager entityManager);

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The class of {@link T}.
     */
    protected final Class<T> type;
}
