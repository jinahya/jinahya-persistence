package com.github.jinahya.persistence.mapped;

import jakarta.annotation.Nonnull;

import java.util.Objects;
import java.util.function.Function;

/**
 * An abstract builder class for building a specific target type.
 *
 * @param <SELF> self type parameter
 * @param <T>    target type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "unchecked",
        "java:S101", // Class names should comply with a naming convention
        "java:S112", // Generic exceptions should never be thrown
        "java:S119", // Type parameter names should comply with a naming convention
        "java:S3011" // Reflection should not be used to increase accessibility of classes, methods, or fields
})
public abstract class __Builder<
        SELF extends __Builder<SELF, T>,
        T
        > {

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance for building an instance of {@link T}.
     *
     * @param targetClass the class of {@link T} to build.
     */
    protected __Builder(@Nonnull final Class<T> targetClass) {
        super();
        this.targetClass = Objects.requireNonNull(targetClass, "targetClass is null");
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Builds this builder into an instance of {@link T} using specified function.
     *
     * @param function the function to build an instance of {@link T} applied to this builder.
     * @return an instance of {@link T} built by specified function applied to this builder.
     */
    @Nonnull
    public T build(@Nonnull final Function<? super SELF, ? extends T> function) {
        Objects.requireNonNull(function, "function is null");
        return function.apply((SELF) this);
    }

    /**
     * Builds this builder into an instance of {@link T}.
     *
     * @return an instance of {@link T} built from this builder.
     */
    @Nonnull
    public T build() {
        return build(
                b -> {
                    try {
                        final var constructor = targetClass.getDeclaredConstructor(b.getClass());
                        if (!constructor.canAccess(null)) {
                            constructor.setAccessible(true);
                        }
                        return constructor.newInstance(b);
                    } catch (final ReflectiveOperationException roe) {
                        throw new RuntimeException(
                                "failed to instantiate " + targetClass + " with " + this,
                                roe
                        );
                    }
                }
        );
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The type to build.
     */
    protected final Class<T> targetClass;
}
