package com.github.jinahya.persistence.more;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

abstract class __MappedRgbTest<T extends __MappedRgb<T>> extends ___MappedColorTest<T> {

    // -----------------------------------------------------------------------------------------------------------------
    __MappedRgbTest(final Class<T> colorClass) {
        super(colorClass);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nested
    class ValueTest {

        @Test
        void resetValue__() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstance();
            // ---------------------------------------------------------------------------------------------------- when
            final var result = instance.resetValue_();
            // ---------------------------------------------------------------------------------------------------- then
            assertThat(result).isSameAs(instance);
        }
    }

    @DisplayName("red")
    @Nested
    class RedTest {

        @DisplayName("newInstance.getRed()Zero")
        @Test
        void getRed_Zero_NewInstance() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstance();
            // ----------------------------------------------------------------------------------------------- when/then
            assertThat(instance.getRed()).isZero();
            assertThat(instance.getNormalizedRed()).isZero();
        }

        @DisplayName("setRed(r) -> setNormalizedComponent(1, r / 255.0)")
        @Test
        void setRed__() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstanceSpy();
            final var expected = __MappedRgbTestUtils.randomComponent();
            // ---------------------------------------------------------------------------------------------------- when
            instance.setRed(expected);
            // ---------------------------------------------------------------------------------------------------- then
            verify(instance, times(1)).setComponent(__MappedRgb.COMPONENT_INDEX_R, expected);
        }

        @DisplayName("red(r)<SELF> -> setRed(r)")
        @Test
        void red_SELF_() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstanceSpy();
            final var expected = __MappedRgbTestUtils.randomComponent();
            // ---------------------------------------------------------------------------------------------------- when
            final var result = instance.red(expected);
            // ---------------------------------------------------------------------------------------------------- then
            assertThat(result).isSameAs(instance);
            verify(instance, times(1)).setRed(expected);
        }
    }

    @DisplayName("normalizedRed")
    @Nested
    class NormalizedRedTest {

        @Test
        void _Zero_NewInstance() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstance();
            // ----------------------------------------------------------------------------------------------- when/then
            assertThat(instance.getNormalizedRed()).isZero();
        }

        @DisplayName("""
                setNormalizedRed(r)
                -> setComponent(1, r)
                , setNormalizedComponent(1, r / 255.0)""")
        @Test
        void setNormalizedRed__() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstanceSpy();
            final var expected = __MappedColorTestUtils.randomNormalizedComponent();
            // ---------------------------------------------------------------------------------------------------- when
            instance.setNormalizedRed(expected);
            // ---------------------------------------------------------------------------------------------------- then
            verify(instance, times(1)).setRed(
                    (int) (expected * ___MappedColorConstants.RGB_MAX_COMPONENT)
            );
        }

        @DisplayName("normalizedRed(r)<SELF> -> setNormalizedRed(r)")
        @Test
        void normalizedRed_SELF_() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstanceSpy();
            final var expected = __MappedColorTestUtils.randomNormalizedComponent();
            // ---------------------------------------------------------------------------------------------------- when
            final var result = instance.normalizedRed(expected);
            // ---------------------------------------------------------------------------------------------------- then
            assertThat(result).isSameAs(instance);
            verify(instance, times(1)).setNormalizedRed(expected);
        }
    }

    @DisplayName("green")
    @Nested
    class GreenTest {

        @DisplayName("newInstance.getGreen()Zero")
        @Test
        void getGreen_Zero_NewInstance() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstance();
            // ----------------------------------------------------------------------------------------------- when/then
            assertThat(instance.getGreen()).isZero();
            assertThat(instance.getNormalizedGreen()).isZero();
        }

        @DisplayName("setGreen(r) -> setNormalizedComponent(1, r / 255.0)")
        @Test
        void setGreen__() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstanceSpy();
            final var expected = __MappedRgbTestUtils.randomComponent();
            // ---------------------------------------------------------------------------------------------------- when
            instance.setGreen(expected);
            // ---------------------------------------------------------------------------------------------------- then
            verify(instance, times(1)).setComponent(
                    __MappedRgb.COMPONENT_INDEX_G,
                    expected
            );
        }

        @DisplayName("green(r)<SELF> -> setGreen(r)")
        @Test
        void green_SELF_() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstanceSpy();
            final var expected = __MappedRgbTestUtils.randomComponent();
            // ---------------------------------------------------------------------------------------------------- when
            final var result = instance.green(expected);
            // ---------------------------------------------------------------------------------------------------- then
            assertThat(result).isSameAs(instance);
            verify(instance, times(1)).setGreen(expected);
        }
    }

    @DisplayName("normalizedGreen")
    @Nested
    class NormalizedGreenTest {

        @Test
        void _Zero_NewInstance() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstance();
            // ----------------------------------------------------------------------------------------------- when/then
            assertThat(instance.getNormalizedGreen()).isZero();
        }

        @DisplayName("""
                setNormalizedGreen(r)
                -> setComponent(1, r)
                , setNormalizedComponent(1, r / 255.0)""")
        @Test
        void setNormalizedGreen__() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstanceSpy();
            final var expected = __MappedColorTestUtils.randomNormalizedComponent();
            // ---------------------------------------------------------------------------------------------------- when
            instance.setNormalizedGreen(expected);
            // ---------------------------------------------------------------------------------------------------- then
            verify(instance, times(1)).setGreen(
                    (int) (expected * ___MappedColorConstants.RGB_MAX_COMPONENT)
            );
        }

        @DisplayName("normalizedGreen(r)<SELF> -> setNormalizedGreen(r)")
        @Test
        void normalizedGreen_SELF_() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstanceSpy();
            final var expected = __MappedColorTestUtils.randomNormalizedComponent();
            // ---------------------------------------------------------------------------------------------------- when
            final var result = instance.normalizedGreen(expected);
            // ---------------------------------------------------------------------------------------------------- then
            assertThat(result).isSameAs(instance);
            verify(instance, times(1)).setNormalizedGreen(expected);
        }
    }

    @DisplayName("blue")
    @Nested
    class BlueTest {

        @DisplayName("newInstance.getBlue()Zero")
        @Test
        void getBlue_Zero_NewInstance() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstance();
            // ----------------------------------------------------------------------------------------------- when/then
            assertThat(instance.getBlue()).isZero();
            assertThat(instance.getNormalizedBlue()).isZero();
        }

        @DisplayName("setBlue(r) -> setNormalizedComponent(1, r / 255.0)")
        @Test
        void setBlue__() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstanceSpy();
            final var expected = __MappedRgbTestUtils.randomComponent();
            // ---------------------------------------------------------------------------------------------------- when
            instance.setBlue(expected);
            // ---------------------------------------------------------------------------------------------------- then
            verify(instance, times(1)).setComponent(
                    __MappedRgb.COMPONENT_INDEX_B,
                    expected
            );
        }

        @DisplayName("blue(r)<SELF> -> setBlue(r)")
        @Test
        void blue_SELF_() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstanceSpy();
            final var expected = __MappedRgbTestUtils.randomComponent();
            // ---------------------------------------------------------------------------------------------------- when
            final var result = instance.blue(expected);
            // ---------------------------------------------------------------------------------------------------- then
            assertThat(result).isSameAs(instance);
            verify(instance, times(1)).setBlue(expected);
        }
    }

    @DisplayName("normalizedBlue")
    @Nested
    class NormalizedBlueTest {

        @Test
        void _Zero_NewInstance() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstance();
            // ----------------------------------------------------------------------------------------------- when/then
            assertThat(instance.getNormalizedBlue()).isZero();
        }

        @DisplayName("""
                setNormalizedBlue(r)
                -> setComponent(1, r)
                , setNormalizedComponent(1, r / 255.0)""")
        @Test
        void setNormalizedBlue__() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstanceSpy();
            final var expected = __MappedColorTestUtils.randomNormalizedComponent();
            // ---------------------------------------------------------------------------------------------------- when
            instance.setNormalizedBlue(expected);
            // ---------------------------------------------------------------------------------------------------- then
            verify(instance, times(1)).setBlue(
                    (int) (expected * ___MappedColorConstants.RGB_MAX_COMPONENT)
            );
        }

        @DisplayName("normalizedBlue(r)<SELF> -> setNormalizedBlue(r)")
        @Test
        void normalizedBlue_SELF_() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstanceSpy();
            final var expected = __MappedColorTestUtils.randomNormalizedComponent();
            // ---------------------------------------------------------------------------------------------------- when
            final var result = instance.normalizedBlue(expected);
            // ---------------------------------------------------------------------------------------------------- then
            assertThat(result).isSameAs(instance);
            verify(instance, times(1)).setNormalizedBlue(expected);
        }
    }

    @DisplayName("alpha")
    @Nested
    class AlphaTest {

        @DisplayName("newInstance.getAlpha()Zero")
        @Test
        void getAlpha_Zero_NewInstance() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstance();
            // ----------------------------------------------------------------------------------------------- when/then
            assertThat(instance.getAlpha()).isZero();
            assertThat(instance.getNormalizedAlpha()).isZero();
        }

        @DisplayName("setAlpha(r) -> setNormalizedComponent(1, r / 255.0)")
        @Test
        void setAlpha__() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstanceSpy();
            final var expected = __MappedRgbTestUtils.randomComponent();
            // ---------------------------------------------------------------------------------------------------- when
            instance.setAlpha(expected);
            // ---------------------------------------------------------------------------------------------------- then
            verify(instance, times(1)).setComponent(
                    __MappedRgb.COMPONENT_INDEX_A,
                    expected
            );
        }

        @DisplayName("alpha(r)<SELF> -> setAlpha(r)")
        @Test
        void alpha_SELF_() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstanceSpy();
            final var expected = __MappedRgbTestUtils.randomComponent();
            // ---------------------------------------------------------------------------------------------------- when
            final var result = instance.alpha(expected);
            // ---------------------------------------------------------------------------------------------------- then
            assertThat(result).isSameAs(instance);
            verify(instance, times(1)).setAlpha(expected);
        }
    }

    @DisplayName("normalizedAlpha")
    @Nested
    class NormalizedAlphaTest {

        @Test
        void _Zero_NewInstance() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstance();
            // ----------------------------------------------------------------------------------------------- when/then
            assertThat(instance.getNormalizedAlpha()).isZero();
        }

        @DisplayName("""
                setNormalizedAlpha(r)
                -> setComponent(1, r)
                , setNormalizedComponent(1, r / 255.0)""")
        @Test
        void setNormalizedAlpha__() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstanceSpy();
            final var expected = __MappedColorTestUtils.randomNormalizedComponent();
            // ---------------------------------------------------------------------------------------------------- when
            instance.setNormalizedAlpha(expected);
            // ---------------------------------------------------------------------------------------------------- then
            verify(instance, times(1)).setAlpha(
                    (int) (expected * ___MappedColorConstants.RGB_MAX_COMPONENT)
            );
        }

        @DisplayName("normalizedAlpha(r)<SELF> -> setNormalizedAlpha(r)")
        @Test
        void normalizedAlpha_SELF_() {
            // --------------------------------------------------------------------------------------------------- given
            final var instance = newColorInstanceSpy();
            final var expected = __MappedColorTestUtils.randomNormalizedComponent();
            // ---------------------------------------------------------------------------------------------------- when
            final var result = instance.normalizedAlpha(expected);
            // ---------------------------------------------------------------------------------------------------- then
            assertThat(result).isSameAs(instance);
            verify(instance, times(1)).setNormalizedAlpha(expected);
        }
    }
}
