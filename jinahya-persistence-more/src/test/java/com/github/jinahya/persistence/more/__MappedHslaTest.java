package com.github.jinahya.persistence.more;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.github.jinahya.persistence.more.__MappedHslaTestUtils.randomNormalizedHue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

abstract class __MappedHslaTest<T extends __MappedHsla<T>> extends ___MappedColorTest<T> {

    // -----------------------------------------------------------------------------------------------------------------
    __MappedHslaTest(final Class<T> colorClass) {
        super(colorClass);
    }

    // -----------------------------------------------------------------------------------------------------------------
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
        void hue__() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstanceSpy();
            final var hue = __MappedHslaTestUtils.randomHue();
            // ---------------------------------------------------------------------------------------------------- when
            final var result = instance.hue(hue);
            // ---------------------------------------------------------------------------------------------------- then
            assertThat(result).isSameAs(instance);
            verify(instance, times(1)).setHue(hue);
            assertThat(instance.getHue()).isEqualTo(hue);
        }

        @DisplayName("newInstance.getNormalizedHue()0")
        @Test
        void getNormalizedHue_0_NewInstance() {
            final var instance = newColorInstanceSpy();
            assertThat(instance.getNormalizedHue()).isZero();
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
            assertThat(instance.getNormalizedHue()).isEqualTo(normalizedHue);
        }
    }

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
        void saturation__() {
            // --------------------------------------------------------------------------------------------------- given
            final T instance = newColorInstanceSpy();
            final var saturation = __MappedHslaTestUtils.randomSaturation();
            // ---------------------------------------------------------------------------------------------------- when
            final T result = instance.saturation(saturation);
            // ---------------------------------------------------------------------------------------------------- then
            assertThat(result).isSameAs(instance);
            verify(instance, times(1)).setSaturation(saturation);
            assertThat(instance.getSaturation()).isEqualTo(saturation);
        }

        @DisplayName("newInstance.getNormalizedSaturation()0")
        @Test
        void getNormalizedSaturation_0_NewInstance() {
            final var instance = newColorInstanceSpy();
            assertThat(instance.getNormalizedSaturation()).isZero();
        }

        @DisplayName("normalizedSaturation(normalizedSaturation)<SELF>")
        @Test
        void normalizedSaturation__() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstanceSpy();
            final var normalizedSaturation = __MappedHslaTestUtils.randomNormalizedSaturation();
            // ---------------------------------------------------------------------------------------------------- when
            final var result = instance.normalizedSaturation(normalizedSaturation);
            // ---------------------------------------------------------------------------------------------------- then
            assertThat(result).isSameAs(instance);
            verify(instance, times(1)).setNormalizedSaturation(normalizedSaturation);
            assertThat(instance.getNormalizedSaturation()).isEqualTo(normalizedSaturation);
        }
    }

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
        void lightness__() {
            // --------------------------------------------------------------------------------------------------- given
            final T instance = newColorInstanceSpy();
            final var lightness = __MappedHslaTestUtils.randomLightness();
            // ---------------------------------------------------------------------------------------------------- when
            final T result = instance.lightness(lightness);
            // ---------------------------------------------------------------------------------------------------- then
            assertThat(result).isSameAs(instance);
            verify(instance, times(1)).setLightness(lightness);
            assertThat(instance.getLightness()).isEqualTo(lightness);
        }

        @DisplayName("newInstance.getNormalizedLightness()0")
        @Test
        void getNormalizedLightness_0_NewInstance() {
            final var instance = newColorInstanceSpy();
            assertThat(instance.getNormalizedLightness()).isZero();
        }

        @DisplayName("normalizedLightness(normalizedLightness)<SELF>")
        @Test
        void normalizedLightness__() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstanceSpy();
            final var normalizedLightness = __MappedHslaTestUtils.randomNormalizedLightness();
            // ---------------------------------------------------------------------------------------------------- when
            final var result = instance.normalizedLightness(normalizedLightness);
            // ---------------------------------------------------------------------------------------------------- then
            assertThat(result).isSameAs(instance);
            verify(instance, times(1)).setNormalizedLightness(normalizedLightness);
            assertThat(instance.getNormalizedLightness()).isEqualTo(normalizedLightness);
        }
    }

    @Nested
    class AlphaTest {

        @DisplayName("newInstance.getNormalizedAlpha()0")
        @Test
        void getNormalizedAlpha_0_NewInstance() {
            final var instance = newColorInstanceSpy();
            assertThat(instance.getNormalizedAlpha()).isZero();
        }

        @DisplayName("normalizedAlpha(normalizedAlpha)<SELF>")
        @Test
        void normalizedAlpha__() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstanceSpy();
            final var normalizedAlpha = __MappedHslaTestUtils.randomNormalizedAlpha();
            // ---------------------------------------------------------------------------------------------------- when
            final var result = instance.normalizedAlpha(normalizedAlpha);
            // ---------------------------------------------------------------------------------------------------- then
            assertThat(result).isSameAs(instance);
            verify(instance, times(1)).setNormalizedAlpha(normalizedAlpha);
            assertThat(instance.getNormalizedAlpha()).isEqualTo(normalizedAlpha);
        }
    }
}
