package com.github.jinahya.persistence.mapped.tests;

import jakarta.annotation.Nonnull;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;

class Entity1Test extends _MappedEntityTest<Entity1, Long> {

    Entity1Test() {
        super(Entity1.class, Long.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    protected void _verify_equals() {
        super._verify_equals();
    }

    @Nonnull
    @Override
    protected SingleTypeEqualsVerifierApi<Entity1> getEqualsVerifier() {
        return super.getEqualsVerifier()
                .suppress(Warning.SURROGATE_KEY);
    }
}
