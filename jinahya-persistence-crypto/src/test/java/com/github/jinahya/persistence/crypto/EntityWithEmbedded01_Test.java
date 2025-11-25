package com.github.jinahya.persistence.crypto;

import com.github.jinahya.persistence.mapped.test.__MappedEntity_Test;
import jakarta.annotation.Nonnull;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;

class EntityWithEmbedded01_Test extends __MappedEntity_Test<EntityWithEmbedded01, Long> {

    EntityWithEmbedded01_Test() {
        super(EntityWithEmbedded01.class, Long.class);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Nonnull
    @Override
    protected SingleTypeEqualsVerifierApi<EntityWithEmbedded01> equals_Verify_(
            @Nonnull final SingleTypeEqualsVerifierApi<EntityWithEmbedded01> equalsVerifier) {
        return super.equals_Verify_(equalsVerifier)
                .suppress(Warning.SURROGATE_KEY);
    }
}
