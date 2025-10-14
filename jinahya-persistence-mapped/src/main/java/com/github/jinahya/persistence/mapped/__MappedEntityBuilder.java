package com.github.jinahya.persistence.mapped;

import jakarta.annotation.Nonnull;

/**
 * An abstract builder class for building a specific subclass of {@link __MappedEntity} class.
 *
 * @param <SELF>   self type parameter
 * @param <TARGET> entity type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119", // Type parameter names should comply with a naming convention
})
public abstract class __MappedEntityBuilder<
        SELF extends __MappedEntityBuilder<SELF, TARGET>,
        TARGET extends __MappedEntity<?>
        >
        extends __MappedBuilder<SELF, TARGET> {

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance with the specified entity class.
     *
     * @param targetClass the entity class.
     */
    protected __MappedEntityBuilder(@Nonnull final Class<TARGET> targetClass) {
        super(targetClass);
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object
}
