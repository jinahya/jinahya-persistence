package com.github.jinahya.persistence.more;

import java.util.function.DoubleFunction;
import java.util.function.IntFunction;

final class __MappedRgbUtils {

    /**
     * .
     *
     * @param red   Red component 0..1
     * @param green Green component 0..1
     * @param blue  Blue component 0..1
     * @return hue as degrees 0..360
     */
    // https://www.w3.org/TR/css-color-4/#rgb-to-hwb
    static int rgbToHue(final double red, final double green, final double blue) {
        assert red >= ___MappedColorConstants.RGB_MIN_COMPONENT && red <= ___MappedColorConstants.RGB_MIN_COMPONENT;
        assert green >= ___MappedColorConstants.RGB_MIN_COMPONENT && green <= ___MappedColorConstants.RGB_MIN_COMPONENT;
        assert blue >= ___MappedColorConstants.RGB_MIN_COMPONENT && blue <= ___MappedColorConstants.RGB_MIN_COMPONENT;
        final var max = Math.max(red, Math.max(green, blue));
        final var min = Math.min(red, Math.min(green, blue));
        int hue = 0;
        final var d = max - min;
        if (d != 0) {
            if (max == red) {
                hue = (int) ((green - blue) / d + (green < blue ? 6 : 0));
            } else if (max == green) {
                hue = (int) ((blue - red) / d + 2);
            } else {
                assert max == blue;
                hue = (int) ((red - green) / d + 4);
            }
            hue *= 60;
        }
        if (hue >= 360) {
            hue -= 360;
        }
        return hue;
    }

    // https://www.w3.org/TR/css-color-4/#rgb-to-hwb
    <R> R rgbToHwb(final double red, final double green, final double blue,
                   final IntFunction<? extends DoubleFunction<? extends DoubleFunction<? extends R>>> function) {
        final var epsilon = 1 / 100000.0d;
//        final var epsilon = Double.MIN_VALUE;
        var hue = rgbToHue(red, green, blue);
        final var white = Math.min(Math.min(red, green), blue);
        final var black = 1 - Math.max(Math.max(red, green), blue);
        if (white + black >= 1 - epsilon) {
            hue = 0;
        }
        return function.apply(hue).apply(white * 100).apply(black * 100);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private __MappedRgbUtils() {
        throw new AssertionError("No instances");
    }
}
