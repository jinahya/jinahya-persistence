package com.github.jinahya.persistence.mapped.test.examples.inheritance.single_table;

import com.github.jinahya.persistence.mapped.test.__Disable_EqualsVerifier_Test;
import com.github.jinahya.persistence.mapped.test.__MappedEntity_Test;
import jakarta.annotation.Nonnull;
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;

@__Disable_EqualsVerifier_Test
abstract class AbstractProject_Test<T extends Project> extends __MappedEntity_Test<T, Long> {

    AbstractProject_Test(final Class<T> entityClass) {
        super(entityClass, Long.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    @Override
    protected SingleTypeEqualsVerifierApi<T> equals_Verify_(
            @Nonnull final SingleTypeEqualsVerifierApi<T> equalsVerifier) {
        return super.equals_Verify_(equalsVerifier);
    }
}
