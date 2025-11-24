package com.github.jinahya.persistence.crypto;

import com.github.jinahya.persistence.mapped.test.__MappedEntity_Test;
import jakarta.annotation.Nonnull;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;

class ManyEntityToOneEntity01_Test extends __MappedEntity_Test<ManyEntityToOneEntity01, Long> {

    ManyEntityToOneEntity01_Test() {
        super(ManyEntityToOneEntity01.class, Long.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    @Override
    protected SingleTypeEqualsVerifierApi<ManyEntityToOneEntity01> equals_Verify_(
            @Nonnull SingleTypeEqualsVerifierApi<ManyEntityToOneEntity01> equalsVerifier) {
        return super.equals_Verify_(equalsVerifier)
                .suppress(Warning.SURROGATE_KEY);
    }
}
