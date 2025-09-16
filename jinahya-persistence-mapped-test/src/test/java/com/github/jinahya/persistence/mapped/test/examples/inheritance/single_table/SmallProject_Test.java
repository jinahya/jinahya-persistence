package com.github.jinahya.persistence.mapped.test.examples.inheritance.single_table;

import com.github.jinahya.persistence.mapped.test.__Disable_EqualsVerifier_Test;
import jakarta.annotation.Nonnull;
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;

@__Disable_EqualsVerifier_Test
class SmallProject_Test extends AbstractProject_Test<SmallProject> {

    SmallProject_Test() {
        super(SmallProject.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    @Override
    protected SingleTypeEqualsVerifierApi<SmallProject> equals_Verify_(
            @Nonnull SingleTypeEqualsVerifierApi<SmallProject> equalsVerifier) {
        return super.equals_Verify_(equalsVerifier);
    }
}
