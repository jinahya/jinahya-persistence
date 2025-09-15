package com.github.jinahya.persistence.more;

import com.github.jinahya.persistence.mapped.test.__Configure_EqualsVerifier;
import com.github.jinahya.persistence.mapped.test.__Mapped_Test;
import jakarta.annotation.Nonnull;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;

class _MappedRgb_Test extends __Mapped_Test<_MappedRgb> {

    _MappedRgb_Test() {
        super(_MappedRgb.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
//    @__Configure_EqualsVerifier
//    @Nonnull
//    @Override
//    protected SingleTypeEqualsVerifierApi<_MappedRgb> equals_Verify_(
//            @Nonnull SingleTypeEqualsVerifierApi<_MappedRgb> equalsVerifier) {
//        return super.equals_Verify_(equalsVerifier)
//                .suppress(Warning.NONFINAL_FIELDS)
//                ;
//    }

    @__Configure_EqualsVerifier
    void equals_Verify_2(@Nonnull final SingleTypeEqualsVerifierApi<_MappedRgb> equalsVerifier) {
        equalsVerifier
                .suppress(Warning.NONFINAL_FIELDS)
        ;
    }
}
