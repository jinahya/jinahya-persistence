package com.github.jinahya.persistence.more;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class __HslaTest extends __MappedHslaTest<__Hsla> {

    __HslaTest() {
        super(__Hsla.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nested
    class ToRgbTest {

        @Test
        void __() {
            final var hsla1 = __MappedHslaTestUtils.randomizedInstance(__Hsla::new);
            final var rgba = __MappedRgbaTestUtils.randomizedInstance(__Rgba::new);
            final var result = hsla1.toRgb(rgba);
            assertThat(result).isSameAs(rgba);
            final var hsla2 = new __Hsla().fromRgb(rgba);
            assertThat(hsla2.getHue()).isEqualTo(hsla1.getHue());
//            assertThat(hsla2.getSaturation()).isEqualTo(hsla1.getSaturation());
        }
    }
}
