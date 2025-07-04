package com.github.jinahya.persistence.more;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class __HslaTest extends __MappedHslaTest<__Hsla> {

    __HslaTest() {
        super(__Hsla.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nested
    class ToRgbTest {

        @Test
        void __() {
            final var hsla = __MappedHslaTestUtils.randomNormalizedHue();
        }
    }
}
