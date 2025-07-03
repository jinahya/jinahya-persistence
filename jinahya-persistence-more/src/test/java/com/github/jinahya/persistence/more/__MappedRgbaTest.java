package com.github.jinahya.persistence.more;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

abstract class __MappedRgbaTest<T extends __MappedRgba<T>> extends ___MappedColorTest<T> {

    // -----------------------------------------------------------------------------------------------------------------
    __MappedRgbaTest(final Class<T> colorClass) {
        super(colorClass);
    }

    @Nested
    class RedTest {

        @Disabled
        @Test
        void getRed_Zero_NewInstance() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstance();
            if (ThreadLocalRandom.current().nextBoolean()) {
                instance.setValue_(null);
            }
            // ---------------------------------------------------------------------------------------------------- when
            assertThat(instance.getRed()).isZero();
        }

        @Test
        void red_SELF_NewInstance() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstanceSpy();
            final var red = __MappedRgbaTestUtils.randomComponent();
            if (ThreadLocalRandom.current().nextBoolean()) {
                instance.setValue_(null);
            }
            // ---------------------------------------------------------------------------------------------------- when
            final var result = instance.red(red);
            // ---------------------------------------------------------------------------------------------------- then
            assertThat(result).isSameAs(instance);
            verify(instance, times(1)).setRed(red);
            assertThat(instance.getRed()).isEqualTo(red);
            {
                instance.setValue_(null);
                assertThat(instance.getRed()).isZero();
            }
        }

        @Disabled
        @Test
        void getNormalizedRed_Zero_NewInstance() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstance();
            // ---------------------------------------------------------------------------------------------------- when
            assertThat(instance.getNormalizedRed()).isZero();
        }

        @Disabled
        @Test
        void normalizedRed_SELF_NewInstance() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstanceSpy();
            final var normalizedRed = __MappedRgbaTestUtils.randomNormalizedComponent();
            // ---------------------------------------------------------------------------------------------------- when
            final var result = instance.normalizedRed(normalizedRed);
            // ---------------------------------------------------------------------------------------------------- then
            assertThat(result).isSameAs(instance);
            verify(instance, times(1)).setNormalizedRed(normalizedRed);
            final var actual = instance.getNormalizedRed();
            assertThat(actual).isEqualTo(normalizedRed);
        }
    }

    @Nested
    class GreenTest {

        @Test
        void getGreen_Zero_NewInstance() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstance();
            // ---------------------------------------------------------------------------------------------------- when
            assertThat(instance.getGreen()).isZero();
        }

        @Test
        void green_SELF_NewInstance() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstanceSpy();
            final var green = __MappedRgbaTestUtils.randomComponent();
            // ---------------------------------------------------------------------------------------------------- when
            final var result = instance.green(green);
            // ---------------------------------------------------------------------------------------------------- then
            assertThat(result).isSameAs(instance);
            verify(instance, times(1)).setGreen(green);
            assertThat(instance.getGreen()).isEqualTo(green);
        }

        @Test
        void getNormalizedGreen_Zero_NewInstance() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstance();
            // ---------------------------------------------------------------------------------------------------- when
            assertThat(instance.getNormalizedGreen()).isZero();
        }

        @Test
        void normalizedGreen_SELF_NewInstance() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstanceSpy();
            final var normalizedGreen = __MappedRgbaTestUtils.randomNormalizedComponent();
            // ---------------------------------------------------------------------------------------------------- when
            final var result = instance.normalizedGreen(normalizedGreen);
            // ---------------------------------------------------------------------------------------------------- then
            assertThat(result).isSameAs(instance);
            verify(instance, times(1)).setNormalizedGreen(normalizedGreen);
            final var actual = instance.getNormalizedGreen();
            assertThat(actual).isEqualTo(normalizedGreen);
        }
    }

    @Nested
    class BlueTest {

        @Test
        void getBlue_Zero_NewInstance() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstance();
            // ---------------------------------------------------------------------------------------------------- when
            assertThat(instance.getBlue()).isZero();
        }

        @Test
        void blue_SELF_NewInstance() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstanceSpy();
            final var blue = __MappedRgbaTestUtils.randomComponent();
            // ---------------------------------------------------------------------------------------------------- when
            final var result = instance.blue(blue);
            // ---------------------------------------------------------------------------------------------------- then
            assertThat(result).isSameAs(instance);
            verify(instance, times(1)).setBlue(blue);
            assertThat(instance.getBlue()).isEqualTo(blue);
        }

        @Test
        void getNormalizedBlue_Zero_NewInstance() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstance();
            // ---------------------------------------------------------------------------------------------------- when
            assertThat(instance.getNormalizedBlue()).isZero();
        }

        @Test
        void normalizedBlue_SELF_NewInstance() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstanceSpy();
            final var normalizedBlue = __MappedRgbaTestUtils.randomNormalizedComponent();
            // ---------------------------------------------------------------------------------------------------- when
            final var result = instance.normalizedBlue(normalizedBlue);
            // ---------------------------------------------------------------------------------------------------- then
            assertThat(result).isSameAs(instance);
            verify(instance, times(1)).setNormalizedBlue(normalizedBlue);
            final var actual = instance.getNormalizedBlue();
            assertThat(actual).isEqualTo(normalizedBlue);
        }
    }

    @Nested
    class AlphaTest {

        @Test
        void getAlpha_Zero_NewInstance() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstance();
            // ---------------------------------------------------------------------------------------------------- when
            assertThat(instance.getAlpha()).isZero();
        }

        @Test
        void alpha_SELF_NewInstance() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstanceSpy();
            final var alpha = __MappedRgbaTestUtils.randomComponent();
            // ---------------------------------------------------------------------------------------------------- when
            final var result = instance.alpha(alpha);
            // ---------------------------------------------------------------------------------------------------- then
            assertThat(result).isSameAs(instance);
            verify(instance, times(1)).setAlpha(alpha);
            assertThat(instance.getAlpha()).isEqualTo(alpha);
        }

        @Test
        void getNormalizedAlpha_Zero_NewInstance() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstance();
            // ---------------------------------------------------------------------------------------------------- when
            assertThat(instance.getNormalizedAlpha()).isZero();
        }

        @Test
        void normalizedAlpha_SELF_NewInstance() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstanceSpy();
            final var normalizedAlpha = __MappedRgbaTestUtils.randomNormalizedComponent();
            // ---------------------------------------------------------------------------------------------------- when
            final var result = instance.normalizedAlpha(normalizedAlpha);
            // ---------------------------------------------------------------------------------------------------- then
            assertThat(result).isSameAs(instance);
            verify(instance, times(1)).setNormalizedAlpha(normalizedAlpha);
            final var actual = instance.getNormalizedAlpha();
            assertThat(actual).isEqualTo(normalizedAlpha);
        }
    }
}
