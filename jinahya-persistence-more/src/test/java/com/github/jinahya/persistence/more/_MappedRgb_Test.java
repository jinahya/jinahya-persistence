package com.github.jinahya.persistence.more;

import com.github.jinahya.persistence.mapped.test.__Mapped_Test;
import jakarta.annotation.Nonnull;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;

class _MappedRgb_Test extends __Mapped_Test<_MappedRgb> {

    _MappedRgb_Test() {
        super(_MappedRgb.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    @Override
    protected SingleTypeEqualsVerifierApi<_MappedRgb> equals_Verify_(
            @Nonnull final SingleTypeEqualsVerifierApi<_MappedRgb> equalsVerifier) {
        return EqualsVerifier.simple().forClass(mappedClass)
                ;
    }
}
