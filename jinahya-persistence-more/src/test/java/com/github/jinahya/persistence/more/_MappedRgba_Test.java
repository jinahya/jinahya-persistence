package com.github.jinahya.persistence.more;

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

    @Nonnull
    @Override
    protected SingleTypeEqualsVerifierApi<_MappedRgba> equals_Verify_(
            @Nonnull final SingleTypeEqualsVerifierApi<_MappedRgba> equalsVerifier) {
        return EqualsVerifier.simple().forClass(mappedClass)
//        return super.equals_Verify_(equalsVerifier)
                ;
    }

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
