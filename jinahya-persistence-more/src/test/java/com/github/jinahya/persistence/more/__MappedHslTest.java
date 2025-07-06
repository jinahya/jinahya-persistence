package com.github.jinahya.persistence.more;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.github.jinahya.persistence.more.__MappedHslTestUtils.randomNormalizedHue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

abstract class __MappedHslTest<T extends __MappedHsl<T>> extends ___MappedColorTest<T> {

    // -----------------------------------------------------------------------------------------------------------------
    __MappedHslTest(final Class<T> colorClass) {
        super(colorClass);
    }

    @DisplayName("Hue")
    @Nested
    class HueTest {

        @DisplayName("newInstance.getHue()0")
        @Test
        void getHue_0_NewInstance() {
            final var instance = newColorInstanceSpy();
            assertThat(instance.getHue()).isZero();
        }

        @DisplayName("hue(hue)<SELF>")
        @Test
        void setHue__() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstanceSpy();
            final var expected = __MappedHslTestUtils.randomHue();
            // ---------------------------------------------------------------------------------------------------- when
            instance.setHue(expected);
            // ---------------------------------------------------------------------------------------------------- then
            verify(instance, times(1)).setComponent(__MappedHsl.COMPONENT_INDEX_H, expected);
        }

        @DisplayName("hue(hue)<SELF>")
        @Test
        void hue__() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstanceSpy();
            final var expected = __MappedHslTestUtils.randomHue();
            // ---------------------------------------------------------------------------------------------------- when
            final var result = instance.hue(expected);
            // ---------------------------------------------------------------------------------------------------- then
            verify(instance, times(1)).setHue(expected);
            assertThat(result).isSameAs(instance);
        }

        @DisplayName("newInstance.getNormalizedHue()0")
        @Test
        void getNormalizedHue_0_NewInstance() {
            final var instance = newColorInstanceSpy();
            assertThat(instance.getNormalizedHue()).isZero();
        }

        @DisplayName("setNormalizedHue(normalizedHue)")
        @Test
        void setNormalizedHue__() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstanceSpy();
            final var expected = randomNormalizedHue();
            // ---------------------------------------------------------------------------------------------------- when
            instance.setNormalizedHue(expected);
            // ---------------------------------------------------------------------------------------------------- then
            verify(instance, times(1)).setHue((int) (expected * __MappedHsl.MAX_HUE));
        }

        @DisplayName("normalizedHue(normalizedHue)<SELF>")
        @Test
        void normalizedHue__() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstanceSpy();
            final var normalizedHue = randomNormalizedHue();
            // ---------------------------------------------------------------------------------------------------- when
            final var result = instance.normalizedHue(normalizedHue);
            // ---------------------------------------------------------------------------------------------------- then
            assertThat(result).isSameAs(instance);
            verify(instance, times(1)).setNormalizedHue(normalizedHue);
            if (false) {
                assertThat(instance.getNormalizedHue()).isEqualTo(normalizedHue);
            }
        }
    }

    @DisplayName("Saturation")
    @Nested
    class SaturationTest {

        @DisplayName("newInstance.getSaturation()0")
        @Test
        void getSaturation_0_NewInstance() {
            final var instance = newColorInstanceSpy();
            assertThat(instance.getSaturation()).isZero();
        }

        @DisplayName("saturation(saturation)<SELF>")
        @Test
        void setSaturation__() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstanceSpy();
            final var expected = __MappedHslTestUtils.randomSaturation();
            // ---------------------------------------------------------------------------------------------------- when
            instance.setSaturation(expected);
            // ---------------------------------------------------------------------------------------------------- then
            verify(instance, times(1)).setComponent(
                    __MappedHsl.COMPONENT_INDEX_S,
                    expected
            );
        }

        @DisplayName("saturation(saturation)<SELF>")
        @Test
        void saturation__() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstanceSpy();
            final var expected = __MappedHslTestUtils.randomSaturation();
            // ---------------------------------------------------------------------------------------------------- when
            final var result = instance.saturation(expected);
            // ---------------------------------------------------------------------------------------------------- then
            verify(instance, times(1)).setSaturation(expected);
            assertThat(result).isSameAs(instance);
        }

        @DisplayName("newInstance.getNormalizedSaturation()0")
        @Test
        void getNormalizedSaturation_0_NewInstance() {
            final var instance = newColorInstanceSpy();
            assertThat(instance.getNormalizedSaturation()).isZero();
        }

        @DisplayName("setNormalizedSaturation(normalizedSaturation)")
        @Test
        void setNormalizedSaturation__() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstanceSpy();
            final var expected = __MappedColorTestUtils.randomNormalizedComponent();
            // ---------------------------------------------------------------------------------------------------- when
            instance.setNormalizedSaturation(expected);
            // ---------------------------------------------------------------------------------------------------- then
            verify(instance, times(1)).setSaturation((int) (expected * __MappedHsl.MAX_SATURATION));
        }

        @DisplayName("normalizedSaturation(normalizedSaturation)<SELF>")
        @Test
        void normalizedSaturation__() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstanceSpy();
            final var expected = __MappedHslTestUtils.randomNormalizedSaturation();
            // ---------------------------------------------------------------------------------------------------- when
            final var result = instance.normalizedSaturation(expected);
            // ---------------------------------------------------------------------------------------------------- then
            assertThat(result).isSameAs(instance);
            verify(instance, times(1)).setNormalizedSaturation(expected);
            if (false) {
                assertThat(instance.getNormalizedSaturation()).isEqualTo(expected);
            }
        }
    }

    @DisplayName("Lightness")
    @Nested
    class LightnessTest {

        @DisplayName("newInstance.getLightness()0")
        @Test
        void getLightness_0_NewInstance() {
            final var instance = newColorInstanceSpy();
            assertThat(instance.getLightness()).isZero();
        }

        @DisplayName("lightness(lightness)<SELF>")
        @Test
        void setLightness__() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstanceSpy();
            final var expected = __MappedHslTestUtils.randomLightness();
            // ---------------------------------------------------------------------------------------------------- when
            instance.setLightness(expected);
            // ---------------------------------------------------------------------------------------------------- then
            verify(instance, times(1)).setComponent(
                    __MappedHsl.COMPONENT_INDEX_L,
                    expected
            );
        }

        @DisplayName("lightness(lightness)<SELF>")
        @Test
        void lightness__() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstanceSpy();
            final var expected = __MappedHslTestUtils.randomLightness();
            // ---------------------------------------------------------------------------------------------------- when
            final var result = instance.lightness(expected);
            // ---------------------------------------------------------------------------------------------------- then
            verify(instance, times(1)).setLightness(expected);
            assertThat(result).isSameAs(instance);
        }

        @DisplayName("newInstance.getNormalizedLightness()0")
        @Test
        void getNormalizedLightness_0_NewInstance() {
            final var instance = newColorInstanceSpy();
            assertThat(instance.getNormalizedLightness()).isZero();
        }

        @DisplayName("setNormalizedLightness(normalizedLightness)")
        @Test
        void setNormalizedLightness__() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstanceSpy();
            final var expected = __MappedColorTestUtils.randomNormalizedComponent();
            // ---------------------------------------------------------------------------------------------------- when
            instance.setNormalizedLightness(expected);
            // ---------------------------------------------------------------------------------------------------- then
            verify(instance, times(1)).setLightness((int) (expected * __MappedHsl.MAX_LIGHTNESS));
        }

        @DisplayName("normalizedLightness(normalizedLightness)<SELF>")
        @Test
        void normalizedLightness__() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstanceSpy();
            final var normalizedLightness = __MappedColorTestUtils.randomNormalizedComponent();
            // ---------------------------------------------------------------------------------------------------- when
            final var result = instance.normalizedLightness(normalizedLightness);
            // ---------------------------------------------------------------------------------------------------- then
            assertThat(result).isSameAs(instance);
            verify(instance, times(1)).setNormalizedLightness(normalizedLightness);
            if (false) {
                assertThat(instance.getNormalizedLightness()).isEqualTo(normalizedLightness);
            }
        }
    }

    @DisplayName("Alpha")
    @Nested
    class AlphaTest {

        @DisplayName("newInstance.getAlpha()0")
        @Test
        void getAlpha_0_NewInstance() {
            final var instance = newColorInstanceSpy();
            assertThat(instance.getAlpha()).isZero();
        }

        @DisplayName("alpha(alpha)<SELF>")
        @Test
        void setAlpha__() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstanceSpy();
            final var expected = __MappedHslTestUtils.randomAlpha();
            // ---------------------------------------------------------------------------------------------------- when
            instance.setAlpha(expected);
            // ---------------------------------------------------------------------------------------------------- then
            verify(instance, times(1)).setComponent(
                    __MappedHsl.COMPONENT_INDEX_A,
                    expected
            );
        }

        @DisplayName("alpha(alpha)<SELF>")
        @Test
        void alpha__() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstanceSpy();
            final var expected = __MappedHslTestUtils.randomAlpha();
            // ---------------------------------------------------------------------------------------------------- when
            final var result = instance.alpha(expected);
            // ---------------------------------------------------------------------------------------------------- then
            verify(instance, times(1)).setAlpha(expected);
            assertThat(result).isSameAs(instance);
        }

        @DisplayName("newInstance.getNormalizedAlpha()0")
        @Test
        void getNormalizedAlpha_0_NewInstance() {
            final var instance = newColorInstanceSpy();
            assertThat(instance.getNormalizedAlpha()).isZero();
        }

        @DisplayName("setNormalizedAlpha(normalizedAlpha)")
        @Test
        void setNormalizedAlpha__() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstanceSpy();
            final var expected = __MappedHslTestUtils.randomNormalizedAlpha();
            // ---------------------------------------------------------------------------------------------------- when
            instance.setNormalizedAlpha(expected);
            // ---------------------------------------------------------------------------------------------------- then
            verify(instance, times(1))
                    .setAlpha(
                            (int) (expected * __MappedHsl.MAX_ALPHA)
                    );
        }

        @DisplayName("normalizedAlpha(normalizedAlpha)<SELF>")
        @Test
        void normalizedAlpha__() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstanceSpy();
            final var expected = __MappedHslTestUtils.randomNormalizedAlpha();
            // ---------------------------------------------------------------------------------------------------- when
            final var result = instance.normalizedAlpha(expected);
            // ---------------------------------------------------------------------------------------------------- then
            verify(instance, times(1)).setNormalizedAlpha(expected);
            assertThat(result).isSameAs(instance);
        }
    }
}
