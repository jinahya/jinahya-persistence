package com.github.jinahya.persistence.more;

import com.github.jinahya.persistence.mapped.test.__Mapped_Test;
import jakarta.annotation.Nonnull;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;

class Rgba_Test extends __Mapped_Test<Rgba> {

    Rgba_Test() {
        super(Rgba.class);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Nonnull
    @Override
    protected SingleTypeEqualsVerifierApi<Rgba> equals_Verify_(
            @Nonnull final SingleTypeEqualsVerifierApi<Rgba> equalsVerifier) {
        return EqualsVerifier.simple().forClass(mappedClass)
//        return super.equals_Verify_(equalsVerifier)
                ;
    }
}
