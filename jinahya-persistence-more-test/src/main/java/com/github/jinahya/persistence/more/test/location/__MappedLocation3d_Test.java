package com.github.jinahya.persistence.more.test.location;

import com.github.jinahya.persistence.mapped.test.__Mapped_Test;
import com.github.jinahya.persistence.more.location.__MappedLocation3d;
import jakarta.annotation.Nonnull;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public abstract class __MappedLocation3d_Test<MAPPED extends __MappedLocation3d>
        extends __Mapped_Test<MAPPED> {

    protected __MappedLocation3d_Test(final Class<MAPPED> mappedClass) {
        super(mappedClass);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    @Override
    protected SingleTypeEqualsVerifierApi<MAPPED> equals_Verify_(
            final @Nonnull SingleTypeEqualsVerifierApi<MAPPED> equalsVerifier) {
        return super.equals_Verify_(equalsVerifier)
                .suppress(Warning.STRICT_INHERITANCE)
                .suppress(Warning.NONFINAL_FIELDS)
                .suppress(Warning.BIGDECIMAL_EQUALITY)
                ;
    }
}
