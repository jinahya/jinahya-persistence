package com.github.jinahya.persistence.mapped.tests;

import jakarta.annotation.Nonnull;

import java.util.Objects;

/**
 * An abstract class for initializing a new instance of a specific class.
 *
 * @param <T> class type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ___InstantiatorUtils
 * @see ___Randomizer
 */
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S3011" // Reflection should not be used to increase accessibility of classes, methods, or fields
})
public abstract class ___Instantiator<T> {

    /**
     * Creates a new instance for initializing the specified class.
     *
     * @param type the class to be initialized.
     * @see #type
     */
    protected ___Instantiator(@Nonnull final Class<T> type) {
        super();
        this.type = Objects.requireNonNull(type, "type is null");
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Initializes a new instance of {@link #type}.
     *
     * @return a new instance of {@link #type}.
     */
    @Nonnull
    @SuppressWarnings({
            "java:S112" // Generic exceptions should never be thrown
    })
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

    /**
     * The class to be initialized.
     */
    private final Class<T> type;
}
