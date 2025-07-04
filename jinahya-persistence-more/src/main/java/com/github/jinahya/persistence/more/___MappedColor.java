package com.github.jinahya.persistence.more;

import jakarta.persistence.MappedSuperclass;

import java.io.Serial;
import java.io.Serializable;

@MappedSuperclass
public abstract class ___MappedColor<SELF extends ___MappedColor<SELF>> implements Serializable {

    @Serial
    private static final long serialVersionUID = 7382750834467647159L;

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    protected ___MappedColor() {
        super();
    }
}
