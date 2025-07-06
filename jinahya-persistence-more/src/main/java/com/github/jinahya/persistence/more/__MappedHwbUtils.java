package com.github.jinahya.persistence.more;

import java.util.function.DoubleFunction;

final class __MappedHwbUtils {

    // https://www.w3.org/TR/css-color-4/#hwb-to-rgb
    static <R> R hwbToRgb(
            final int hue, final double whiteness, final double blackness,
            final DoubleFunction<
                    ? extends DoubleFunction<
                            ? extends DoubleFunction<
                                    ? extends R>>> f) {
        assert hue >= __MappedHwb.HWB_MIN_HUE;
        assert hue <= __MappedHwb.HWB_MAX_HUE;
        assert whiteness >= __MappedHwb.HWB_MIN_WHITENESS;
        assert whiteness <= __MappedHwb.HWB_MAX_WHITENESS;
        assert blackness >= __MappedHwb.HWB_MIN_BLACKNESS;
        assert blackness <= __MappedHwb.HWB_MAX_BLACKNESS;
        assert f != null;
        if ((whiteness + blackness) >= 1.0d) {
            final var grey = whiteness / (whiteness + blackness);
            return f.apply(grey).apply(grey).apply(grey);
        }
        return __MappedHslUtils.hslToRgb(
                hue,
                100, // saturation
                50,  // lightness
                r -> g -> b -> {
                    final var rgb = new double[]{r, g, b};
                    for (int i = 0; i < 3; i++) {
                        rgb[i] *= (1 - whiteness - blackness);
                        rgb[i] += whiteness;
                    }
                    return f.apply(rgb[0]).apply(rgb[1]).apply(rgb[2]);
                }
        );
    }

    // https://www.w3.org/TR/css-color-4/#hwb-to-rgb
    static <R> R hwbToRgb(
            final int hue,
            final int whiteness,
            final int blackness,
            final DoubleFunction< // r
                    ? extends DoubleFunction< // g
                            ? extends DoubleFunction< // b
                                    ? extends R>>> function) {
        assert hue >= __MappedHwb.HWB_MIN_HUE;
        assert hue <= __MappedHwb.HWB_MAX_HUE;
        assert whiteness >= __MappedHwb.HWB_MIN_WHITENESS;
        assert whiteness <= __MappedHwb.HWB_MAX_WHITENESS;
        assert blackness >= __MappedHwb.HWB_MIN_BLACKNESS;
        assert blackness <= __MappedHwb.HWB_MAX_BLACKNESS;
        return hwbToRgb(
                hue,
                whiteness / (double) __MappedHwb.HWB_MAX_WHITENESS,
                blackness / (double) __MappedHwb.HWB_MAX_BLACKNESS,
                function
        );
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    private __MappedHwbUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
