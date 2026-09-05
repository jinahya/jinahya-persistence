package com.github.jinahya.persistence.test;

import jakarta.annotation.Nonnull;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * An abstract class for initializing a new instance of a specific class.
 *
 * @param <T> class type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __InstantiatorUtils
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __Instantiator<T> implements Supplier<T> {

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance for instantiating the specified class.
     *
     * @param targetClass the class to be instantiated.
     * @see #targetClass
     */
    protected __Instantiator(final @Nonnull Class<T> targetClass) {
        super();
        this.targetClass = Objects.requireNonNull(targetClass, "targetClass is null");
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Instantiates {@link #targetClass}, using its no-argument constructor.
     *
     * @return a new instance of {@link #targetClass}.
     * @see ___Utils#newInstance(Class)
     */
    @Nonnull
    @Override
    public T get() {
        return ___Utils.newInstance(targetClass);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The target type to instantiate.
     */
    protected final Class<T> targetClass;
}
