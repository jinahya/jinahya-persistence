package com.github.jinahya.persistence.more;

import java.util.function.DoubleFunction;

final class __MappedHslUtils {

    // https://www.w3.org/TR/css-color-3/#hsl-color
    private static double f(final int n, final double h, final double s, final double l) {
        final double k = (n + h / 30) % 12;
        return l - (s * Math.min(l, 1 - l)) * Math.clamp(Math.min(k - 3, 9 - k), -1, 1);
    }

    // https://www.w3.org/TR/css-color-3/#hsl-color
    static <R> R hslToRgb(
            final int hue, final int saturation, final int lightness,
            final DoubleFunction<? extends DoubleFunction<? extends DoubleFunction<? extends R>>> f) {
        assert hue >= ___MappedColorConstants.HSL_MIN_HUE;
        assert hue <= ___MappedColorConstants.HSL_MAX_HUE;
        assert saturation >= ___MappedColorConstants.HSL_MIN_SATURATION;
        assert saturation <= ___MappedColorConstants.HSL_MAX_SATURATION;
        assert lightness >= ___MappedColorConstants.HSL_MIN_LIGHTNESS;
        assert lightness <= ___MappedColorConstants.HSL_MAX_LIGHTNESS;
        assert f != null;
        final var h = hue % ___MappedColorConstants.HSL_MAX_HUE;
        final var s = saturation / (double) ___MappedColorConstants.HSL_MAX_SATURATION;
        final var l = lightness / (double) ___MappedColorConstants.HSL_MAX_LIGHTNESS;
        final var r = f(0, h, s, l);
        final var g = f(8, h, s, l);
        final var b = f(4, h, s, l);
        return f.apply(r).apply(g).apply(b);
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    private __MappedHslUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
