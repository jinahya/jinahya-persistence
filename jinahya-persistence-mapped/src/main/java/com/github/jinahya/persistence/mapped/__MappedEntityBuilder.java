package com.github.jinahya.persistence.mapped;

import jakarta.annotation.Nonnull;

import java.util.Objects;
import java.util.function.Function;

/**
 * An abstract class for building subclasses of {@link __MappedEntity} class.
 *
 * @param <SELF>   self type parameter
 * @param <ENTITY> entity type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "unchecked",
        "java:S100", // Method names should comply with a naming convention
        "java:S101", // Class names should comply with a naming convention
        "java:S116", // Field names should comply with a naming convention
        "java:S117", // Local variable and method parameter names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public abstract class __MappedEntityBuilder<
        SELF extends __MappedEntityBuilder<SELF, ENTITY>,
        ENTITY extends __MappedEntity<?>
        > {

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected __MappedEntityBuilder() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Builds this builder into an instance of {@link ENTITY}.
     *
     * @return an instance of {@link ENTITY}.
     */
    @Nonnull
    public abstract ENTITY build();

    /**
     * Builds this builder into an instance of {@link ENTITY} using specified function.
     *
     * @param function the function to build an instance of {@link ENTITY} applied to this builder.
     * @return an instance of {@link ENTITY} built by specified function applied to this builder.
     */
    @Nonnull
    protected final ENTITY build(@Nonnull final Function<? super SELF, ? extends ENTITY> function) {
        Objects.requireNonNull(function, "function is null");
        return function.apply((SELF) this);
    }

    /**
     * Builds this builder into an instance of {@link ENTITY} using specified entity class.
     *
     * @param entityClass the entity class.
     * @return an instance of {@link ENTITY} built by specified entity class.
     * @implNote The entity class must have a constructor which takes this builder as a parameter.
     */
    @Nonnull
    @SuppressWarnings({
            "java:S3011" // Reflection should not be used to increase accessibility of classes, methods, or fields
    })
    protected final ENTITY build(@Nonnull final Class<ENTITY> entityClass) {
        Objects.requireNonNull(entityClass, "entityClass is null");
        return build(b -> {
            try {
                final var constructor = entityClass.getDeclaredConstructor(getClass());
                if (!constructor.canAccess(null)) {
                    constructor.setAccessible(true);
                }
                return constructor.newInstance(b);
            } catch (final ReflectiveOperationException roe) {
                throw new RuntimeException(
                        "failed to instantiate " + entityClass + " with " + this,
                        roe
                );
            }
        });
    }
}
