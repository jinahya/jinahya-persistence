package com.github.jinahya.persistence.more;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ___MappedColorConstantsTest {

    @Nested
    class RgbTest {

        @Test
        void __() {
            for (int i = ___MappedColorConstants.RGB_MIN_COMPONENT;
                 i <= ___MappedColorConstants.RGB_MAX_COMPONENT; i++) {
                final var normalized = i / (double) ___MappedColorConstants.RGB_MAX_COMPONENT;
                final var actual = normalized * ___MappedColorConstants.RGB_MAX_COMPONENT;
                assertThat(actual).isEqualTo(i);
            }
        }
    }

    @Nested
    class HslTest {

        @Disabled("fails, obviously")
        @Test
        void hue__() {
            for (int h = __MappedHsl.MIN_HUE; h <= __MappedHsl.MAX_HUE; h++) {
                final var hue = h;
                final var normalized = h / (double) __MappedHsl.MAX_HUE;
                final var actual = (int) (normalized * __MappedHsl.MAX_HUE);
                assertThat(actual).isEqualTo(h);
            }
        }

        @Disabled("fails, obviously")
        @Test
        void saturation__() {
//            for (int s = ___MappedColorConstants.HSL_MIN_SATURATION;
//                 s <= ___MappedColorConstants.HSL_MAX_SATURATION; s++) {
//                final var saturation = s;
//                final var normalized = s / (double) ___MappedColorConstants.HSL_MAX_SATURATION;
//                final var actual = (int) (normalized * ___MappedColorConstants.HSL_MAX_SATURATION);
//                assertThat(actual).isEqualTo(saturation);
//            }
        }

        @Disabled("fails, obviously")
        @Test
        void lightness__() {
//            for (int l = ___MappedColorConstants.HSL_MIN_LIGHTNESS;
//                 l <= ___MappedColorConstants.HSL_MAX_SATURATION; l++) {
//                final var lightness = l;
//                final var normalized = l / (double) ___MappedColorConstants.HSL_MAX_LIGHTNESS;
//                final var actual = (int) (normalized * ___MappedColorConstants.HSL_MAX_LIGHTNESS);
//                assertThat(actual).isEqualTo(lightness);
//            }
        }
    }
}
