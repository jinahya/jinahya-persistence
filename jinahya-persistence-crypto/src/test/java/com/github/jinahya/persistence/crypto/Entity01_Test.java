package com.github.jinahya.persistence.crypto;

import com.github.jinahya.persistence.mapped.test.__MappedEntity_Test;
import jakarta.annotation.Nonnull;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;

class Entity01_Test extends __MappedEntity_Test<Entity01, Long> {

    Entity01_Test() {
        super(Entity01.class, Long.class);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Nonnull
    @Override
    protected SingleTypeEqualsVerifierApi<Entity01> equals_Verify_(
            @Nonnull final SingleTypeEqualsVerifierApi<Entity01> equalsVerifier) {
        return super.equals_Verify_(equalsVerifier)
                .suppress(Warning.SURROGATE_KEY);
    }
}
