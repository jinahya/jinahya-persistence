package com.github.jinahya.persistence.mapped;

import jakarta.annotation.Nonnull;

/**
 * An abstract builder class for building a specific subclass of {@link __Mapped} class.
 *
 * @param <SELF>   self type parameter
 * @param <MAPPED> a type of {@link __Mapped} to build
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119", // Type parameter names should comply with a naming convention
})
public abstract class __MappedBuilder<
        SELF extends __MappedBuilder<SELF, MAPPED>,
        MAPPED extends __Mapped
        >
        extends __Builder<SELF, MAPPED> {

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance with the specified entity class.
     *
     * @param mappedClass the entity class.
     */
    protected __MappedBuilder(@Nonnull final Class<MAPPED> mappedClass) {
        super(mappedClass);
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object
}
