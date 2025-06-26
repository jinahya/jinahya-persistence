package com.github.jinahya.persistence.mapped;

import jakarta.annotation.Nonnull;

import java.util.Objects;

abstract class ___Initializer<T> {

    ___Initializer(final Class<T> type) {
        super();
        this.type = Objects.requireNonNull(type, "type is null");
    }

    /**
     * Initializes a new instance of {@link #type}.
     *
     * @return a new instance of {@link #type}.
     */
    @Nonnull
    public T get() {
        try {
            final var constructor = type.getDeclaredConstructor();
            if (!constructor.canAccess(null)) {
                constructor.setAccessible(true);
            }
            return constructor.newInstance();
        } catch (final ReflectiveOperationException roe) {
            throw new RuntimeException("failed to initialize a new instance of " + type, roe);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    protected final Class<T> type;
}
