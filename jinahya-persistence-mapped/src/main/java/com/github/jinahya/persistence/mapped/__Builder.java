package com.github.jinahya.persistence.mapped;

import jakarta.annotation.Nonnull;

import java.util.Objects;
import java.util.function.Function;

/**
 * An abstract class for building a specific target type.
 *
 * @param <SELF>   self type parameter
 * @param <TARGET> target type parameter
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
        SELF extends __Builder<SELF, TARGET>,
        TARGET
        > {

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance for building instances of the specified target class.
     *
     * @param targetClass the class of {@link TARGET} to build.
     */
    protected __Builder(@Nonnull final Class<TARGET> targetClass) {
        super();
        this.targetClass = Objects.requireNonNull(targetClass, "targetClass is null");
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Builds this builder into an instance of {@link #targetClass} using specified function.
     *
     * @param function the function to be applied to this builder.
     * @return an instance of {@link #targetClass} built by specified function applied to this builder.
     */
    @Nonnull
    public TARGET build(@Nonnull final Function<? super SELF, ? extends TARGET> function) {
        Objects.requireNonNull(function, "function is null");
        return function.apply((SELF) this);
    }

    /**
     * Builds this builder into an instance of {@link #targetClass}.
     *
     * @return an instance of {@link #targetClass} built from this builder.
     */
    @Nonnull
    public TARGET build() {
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
                                "failed to build a new instance of " + targetClass + " with " + this,
                                roe
                        );
                    }
                }
        );
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The target type to build.
     */
    protected final Class<TARGET> targetClass;
}
