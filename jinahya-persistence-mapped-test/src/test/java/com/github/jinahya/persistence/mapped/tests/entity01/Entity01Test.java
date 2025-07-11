package com.github.jinahya.persistence.mapped.tests.entity01;

import com.github.jinahya.persistence.mapped.tests._MappedEntityTest;
import jakarta.annotation.Nonnull;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;

public class Entity01Test extends _MappedEntityTest<Entity01, Long> {

    protected Entity01Test() {
        super(Entity01.class, Long.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    protected void _verify_equals() {
        super._verify_equals();
    }

    @Nonnull
    @Override
    protected SingleTypeEqualsVerifierApi<Entity01> getEqualsVerifier() {
        return super.getEqualsVerifier()
                .suppress(Warning.SURROGATE_KEY);
    }
}
