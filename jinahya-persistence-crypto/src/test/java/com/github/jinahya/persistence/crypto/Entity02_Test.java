package com.github.jinahya.persistence.crypto;

import com.github.jinahya.persistence.mapped.test.__MappedEntity_Test;
import jakarta.annotation.Nonnull;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;

class Entity02_Test extends __MappedEntity_Test<Entity02, Long> {

    Entity02_Test() {
        super(Entity02.class, Long.class);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Nonnull
    @Override
    protected SingleTypeEqualsVerifierApi<Entity02> equals_Verify_(
            @Nonnull final SingleTypeEqualsVerifierApi<Entity02> equalsVerifier) {
        return super.equals_Verify_(equalsVerifier)
                .suppress(Warning.SURROGATE_KEY);
    }
}
