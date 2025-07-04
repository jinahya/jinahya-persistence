package com.github.jinahya.persistence.mapped;

import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public abstract class ___Mapped<SELF extends ___Mapped<SELF>> {

    // ------------------------------------------------------------------------------------------ STATIC_FACTORY_METHODS

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected ___Mapped() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object
}
