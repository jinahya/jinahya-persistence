package com.github.jinahya.persistence.more;

import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.MappedSuperclass;

import java.io.Serial;

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@MappedSuperclass
public abstract class __MappedSingleTableColor<SELF extends __MappedSingleTableColor<SELF>>
        extends ___MappedColor<SELF> {

    @Serial
    private static final long serialVersionUID = -5132360269083578954L;

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected __MappedSingleTableColor() {
        super();
    }
}
