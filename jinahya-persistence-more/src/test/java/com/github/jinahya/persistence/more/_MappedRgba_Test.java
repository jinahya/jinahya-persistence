package com.github.jinahya.persistence.more;

import com.github.jinahya.persistence.mapped.test.__Configure_EqualsVerifier;
import com.github.jinahya.persistence.mapped.test.__Mapped_Test;
import jakarta.annotation.Nonnull;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.invoke.MethodHandles;

class _MappedRgba_Test extends __Mapped_Test<_MappedRgba> {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    _MappedRgba_Test() {
        super(_MappedRgba.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @__Configure_EqualsVerifier
    @Nonnull
    @Override
    protected SingleTypeEqualsVerifierApi<_MappedRgba> equals_Verify_(
            final @Nonnull SingleTypeEqualsVerifierApi<_MappedRgba> equalsVerifier) {
        return EqualsVerifier.simple().forClass(mappedClass)
                ;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nested
    class ToHexNotationTest {

        @Test
        void __() {
            final var instance = newRandomizedMappedInstance().orElseThrow();
            final var notation = instance.toHexNotation();
            logger.log(System.Logger.Level.DEBUG, "instance: {0}, notation: {1}", instance, notation);
        }
    }
}
