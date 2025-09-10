package com.github.jinahya.persistence.more;

import com.github.jinahya.persistence.mapped.test.__Mapped_Test;
import jakarta.annotation.Nonnull;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;

class Rgb_Test extends __Mapped_Test<Rgb> {

    Rgb_Test() {
        super(Rgb.class);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Nonnull
    @Override
    protected SingleTypeEqualsVerifierApi<Rgb> equals_Verify_(
            @Nonnull SingleTypeEqualsVerifierApi<Rgb> equalsVerifier) {
        return super.equals_Verify_(equalsVerifier)
                .suppress(Warning.NONFINAL_FIELDS)
                ;
    }
}
