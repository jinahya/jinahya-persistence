package com.github.jinahya.persistence.more;

import jakarta.persistence.Embeddable;
import jakarta.persistence.MappedSuperclass;

import java.io.Serial;

@Embeddable
@MappedSuperclass
public abstract class __MappedCmyk<SELF extends __MappedCmyk<SELF>> extends ___MappedColor<SELF> {

    @Serial
    private static final long serialVersionUID = -5905278228337798453L;

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    protected __MappedCmyk() {
        super();
    }
}