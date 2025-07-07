package com.github.jinahya.persistence.more;

import java.util.function.DoubleFunction;

final class __MappedHslUtils {

    // https://www.w3.org/TR/css-color-3/#hsl-color
    private static double f(final int n, final double h, final double s, final double l) {
        final double k = (n + h / 30) % 12;
        return l - (s * Math.min(l, 1 - l)) * Math.clamp(Math.min(k - 3, 9 - k), -1, 1);
    }

    /**
     * Applies RGB color components, converted from specified HSL color components, to the specified function, and
     * returns the result.
     * <p>
     * {@snippet lang = "java":
     * hwbToRgb(
     *         hue,       // [0..360]
     *         whiteness, // [0..100]
     *         blackness, // [0..100]
     *         r -> g -> b -> {
     *             assert r >= 0.0d && r <= 1.0d;
     *             assert g >= 0.0d && g <= 1.0d;
     *             assert b >= 0.0d && b <= 1.0d;
     *             final var   red = (int) Math.round(r * 255);
     *             final var green = (int) Math.round(g * 255);
     *             final var  blue = (int) Math.round(b * 255);
     *             assert   red >= 0 &&   red <= 255;
     *             assert green >= 0 && green <= 255;
     *             assert  blue >= 0 &&  blue <= 255;
     *             return null;
     *         }
     * );
     *}
     *
     * @param hue        a value of {@code hue} between {@value __MappedHsl#MIN_HUE} and {@value __MappedHsl#MAX_HUE},
     *                   both inclusive.
     * @param saturation a value of {@code saturation}, in percent, between {@value __MappedHsl#MIN_SATURATION} and
     *                   {@value __MappedHsl#MAX_SATURATION}, both inclusive.
     * @param lightness  a value of {@code lightness}, in percent, between {@value __MappedHsl#MIN_LIGHTNESS} and
     *                   {@value __MappedHsl#MAX_LIGHTNESS}, both inclusive.
     * @param function   the function to be applied, in currying, with {@code red}, {@code green}, and {@code blue},
     *                   which each is between {@value ___MappedColorConstants#MIN_COMPONENT_NORMALIZED} and
     *                   {@value ___MappedColorConstants#MAX_COMPONENT_NORMALIZED}, both inclusive.
     * @param <R>        result type parameter
     * @return the result of the {@code function}.
     */
    // https://www.w3.org/TR/css-color-3/#hsl-color
    // https://www.rapidtables.com/convert/color/hsl-to-rgb.html
    // https://colordesigner.io/convert/hsltorgb
    static <R> R hslToRgb(
            final int hue, final int saturation, final int lightness,
            final DoubleFunction<? extends DoubleFunction<? extends DoubleFunction<? extends R>>> function) {
        assert hue >= __MappedHsl.MIN_HUE;
        assert hue <= __MappedHsl.MAX_HUE;
        assert saturation >= __MappedHsl.MIN_SATURATION;
        assert saturation <= __MappedHsl.MAX_SATURATION;
        assert lightness >= __MappedHsl.MIN_LIGHTNESS;
        assert lightness <= __MappedHsl.MAX_LIGHTNESS;
        assert function != null;
        final var h = hue % __MappedHsl.MAX_HUE;
        final var s = saturation / (double) __MappedHsl.MAX_SATURATION;
        final var l = lightness / (double) __MappedHsl.MAX_LIGHTNESS;
        final var r = f(0, h, s, l);
        final var g = f(8, h, s, l);
        final var b = f(4, h, s, l);
        assert r >= __MappedRgbConstants.MIN_NORMALIZED_COMPONENT;
        assert r <= __MappedRgbConstants.MAX_NORMALIZED_COMPONENT;
        assert g >= __MappedRgbConstants.MIN_NORMALIZED_COMPONENT;
        assert g <= __MappedRgbConstants.MAX_NORMALIZED_COMPONENT;
        assert b >= __MappedRgbConstants.MIN_NORMALIZED_COMPONENT;
        assert b <= __MappedRgbConstants.MAX_NORMALIZED_COMPONENT;
        return function.apply(r).apply(g).apply(b);
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    private __MappedHslUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
