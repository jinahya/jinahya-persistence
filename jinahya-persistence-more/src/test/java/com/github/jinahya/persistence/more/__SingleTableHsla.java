package com.github.jinahya.persistence.more;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.io.Serial;

@DiscriminatorValue(__SingleTableHsla.DISCRIMINATOR_VALUE)
@Entity
class __SingleTableHsla extends __SingleTableColor<__SingleTableHsla> {

    @Serial
    private static final long serialVersionUID = 6264342671001204962L;

    // -----------------------------------------------------------------------------------------------------------------
    static final String DISCRIMINATOR_VALUE = "hsla";

    // ----------------------------------------------------------------------------------------------------- CONSTRUCTOR
    protected __SingleTableHsla() {
        super();
    }
}
