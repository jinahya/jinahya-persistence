package com.github.jinahya.persistence.more;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.io.Serial;

@DiscriminatorValue(__SingleTableRgba.DISCRIMINATOR_VALUE)
@Entity
class __SingleTableRgba extends __SingleTableColor<__SingleTableRgba> {

    @Serial
    private static final long serialVersionUID = 6264342671001204962L;

    // -----------------------------------------------------------------------------------------------------------------
    static final String DISCRIMINATOR_VALUE = "rgba";

    // ----------------------------------------------------------------------------------------------------- CONSTRUCTOR
    protected __SingleTableRgba() {
        super();
    }
}
