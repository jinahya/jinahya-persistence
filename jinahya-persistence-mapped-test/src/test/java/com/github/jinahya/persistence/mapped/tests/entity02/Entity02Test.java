package com.github.jinahya.persistence.mapped.tests.entity02;

import com.github.jinahya.persistence.mapped.tests._MappedEntityTest;
import jakarta.annotation.Nonnull;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;

public class Entity02Test extends _MappedEntityTest<Entity02, Long> {

    protected Entity02Test() {
        super(Entity02.class, Long.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    protected void _verify_equals() {
        super._verify_equals();
    }

    @Nonnull
    @Override
    protected SingleTypeEqualsVerifierApi<Entity02> getEqualsVerifier() {
        return super.getEqualsVerifier()
                .suppress(Warning.SURROGATE_KEY);
    }
}
