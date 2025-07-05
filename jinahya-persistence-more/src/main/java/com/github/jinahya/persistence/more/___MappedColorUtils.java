package com.github.jinahya.persistence.more;

import java.util.Objects;
import java.util.function.DoubleFunction;
import java.util.function.IntFunction;

public final class ___MappedColorUtils {

    // https://www.w3.org/TR/css-color-3/#hsl-color
    private static double hslToRgb__(final int n, final double h, final double s, final double l) {
        final double k = (n + h / 30) % 12;
        return l - (s * Math.min(l, 1 - l)) * Math.clamp(Math.min(k - 3, 9 - k), -1, 1);
    }

    /**
     * Applies <span style="color:red;">R</span><span style="color:green;">G</span><span style="color:blue;">B</span>
     * color components, converted from specified HSL color components, to the specified function, and returns the
     * result.
     * <p>
     * {@snippet lang = "java":
     * hslToRgb(
     *         hue, s, l,
     *         r -> g -> b -> {
     *             assert r >= 0 && r <= 1;
     *             assert g >= 0 && g <= 1;
     *             assert b >= 0 && b <= 1;
     *             return null;
     *         }
     * );
     *}
     *
     * @param hue a value of {@code hue} between {@value __MappedHsla#HSL_MIN_HUE} and
     *            {@value __MappedHsla#HSL_MAX_HUE}, both inclusive.
     * @param s   a normalized value of {@code saturation} between
     *            {@value ___MappedColorConstants#MIN_COMPONENT_NORMALIZED} and
     *            {@value ___MappedColorConstants#MAX_COMPONENT_NORMALIZED}, both inclusive.
     * @param l   a normalized value of {@code lightness} between
     *            {@value ___MappedColorConstants#MIN_COMPONENT_NORMALIZED} and
     *            {@value ___MappedColorConstants#MAX_COMPONENT_NORMALIZED}, both inclusive.
     * @param f   the function to be applied with, in currying, <span style="color:red;">R</span><span
     *            style="color:green;">G</span><span style="color:blue;">B</span> color components which each is a
     *            normalized value between {@value ___MappedColorConstants#MIN_COMPONENT_NORMALIZED} and
     *            {@value ___MappedColorConstants#MAX_COMPONENT_NORMALIZED}, both inclusive.
     * @param <R> result type parameter
     * @return the result of the {@code function}.
     * @see #rgbToHsl(double, double, double, IntFunction)
     */
    // https://www.w3.org/TR/css-color-3/#hsl-color
    public static <R> R hslToRgb(
            final int hue, final double s, final double l,
            final DoubleFunction<? extends DoubleFunction<? extends DoubleFunction<? extends R>>> f) {
        if (hue < ___MappedColorConstants.HSL_MIN_HUE || hue > ___MappedColorConstants.HSL_MAX_HUE) {
            throw new IllegalArgumentException("invalid hue: " + hue);
        }
        if (s < ___MappedColorConstants.MIN_COMPONENT_NORMALIZED ||
            s > ___MappedColorConstants.MAX_COMPONENT_NORMALIZED) {
            throw new IllegalArgumentException("invalid s: " + s);
        }
        if (l < ___MappedColorConstants.MIN_COMPONENT_NORMALIZED ||
            l > ___MappedColorConstants.MAX_COMPONENT_NORMALIZED) {
            throw new IllegalArgumentException("invalid l: " + l);
        }
        Objects.requireNonNull(f, "f is null");
        final var h = hue % ___MappedColorConstants.HSL_MAX_HUE;
        final var r = hslToRgb__(0, hue, s, l);
        final var g = hslToRgb__(8, hue, s, l);
        final var b = hslToRgb__(4, hue, s, l);
        return f.apply(r).apply(g).apply(b);
    }

    public static <R> R hslToRgb(
            final int hue, final int saturation, final int lightness,
            final DoubleFunction<? extends DoubleFunction<? extends DoubleFunction<? extends R>>> f) {
        if (hue < ___MappedColorConstants.HSL_MIN_HUE || hue > ___MappedColorConstants.HSL_MAX_HUE) {
            throw new IllegalArgumentException("invalid hue: " + hue);
        }
        if (saturation < ___MappedColorConstants.HSL_MIN_SATURATION ||
            saturation > ___MappedColorConstants.HSL_MAX_SATURATION) {
            throw new IllegalArgumentException("invalid saturation: " + saturation);
        }
        if (lightness < ___MappedColorConstants.HSL_MIN_LIGHTNESS ||
            lightness > ___MappedColorConstants.HSL_MAX_LIGHTNESS) {
            throw new IllegalArgumentException("invalid lightness: " + lightness);
        }
        Objects.requireNonNull(f, "f is null");
        final var s = saturation / (double) ___MappedColorConstants.HSL_MAX_SATURATION;
        final var l = lightness / (double) ___MappedColorConstants.HSL_MAX_LIGHTNESS;
        return hslToRgb(hue, s, l, f);
    }

    /**
     * Applies HSL color components, converted from specified <span style="color:red;">R</span><span
     * style="color:green;">G</span><span style="color:blue;">B</span> color components, to the specified function, and
     * returns the result.
     * <p>
     * {@snippet lang = "java":
     * rgbToHsl(
     *         r, g, b,
     *         h -> s -> l -> {
     *             return null;
     *         }
     * );
     *}
     *
     * @param r   a normalized value of <span style="color:red;">red</span> component between
     *            {@value ___MappedColorConstants#MIN_COMPONENT_NORMALIZED} and
     *            {@value ___MappedColorConstants#MIN_COMPONENT_NORMALIZED}, both inclusive.
     * @param g   a normalized value of <span style="color:green;">green</span> component between
     *            {@value ___MappedColorConstants#MIN_COMPONENT_NORMALIZED} and
     *            {@value ___MappedColorConstants#MAX_COMPONENT_NORMALIZED}, both inclusive.
     * @param b   a normalized value of <span style="color:blue;">blue</span> component between
     *            {@value ___MappedColorConstants#MIN_COMPONENT_NORMALIZED} and
     *            {@value ___MappedColorConstants#MAX_COMPONENT_NORMALIZED}, both inclusive.
     * @param f   the function to be applied with, in currying, a
     *            {@code hue}([{@value ___MappedColorConstants#HSL_MIN_HUE}..{@value
     *            ___MappedColorConstants#HSL_MAX_HUE}]), a
     *            {@code saturation}({@value ___MappedColorConstants#MIN_COMPONENT_NORMALIZED}..{@value
     *            ___MappedColorConstants#MAX_COMPONENT_NORMALIZED}]), and a
     *            {@code lightness}([{@value ___MappedColorConstants#MIN_COMPONENT_NORMALIZED}..{@value
     *            ___MappedColorConstants#MAX_COMPONENT_NORMALIZED}]).
     * @param <R> result type parameter
     * @return the result of the {@code function}.
     * @see #hslToRgb(int, double, double, DoubleFunction)
     */
    public static <R> R rgbToHsl(
            final double r, final double g, final double b,
            final IntFunction<? extends DoubleFunction<? extends DoubleFunction<? extends R>>> f) {
        if (r < ___MappedColorConstants.MIN_COMPONENT_NORMALIZED ||
            r > ___MappedColorConstants.MAX_COMPONENT_NORMALIZED) {
            throw new IllegalArgumentException("invalid r: " + r);
        }
        if (g < ___MappedColorConstants.MIN_COMPONENT_NORMALIZED ||
            g > ___MappedColorConstants.MAX_COMPONENT_NORMALIZED) {
            throw new IllegalArgumentException("invalid g: " + g);
        }
        if (b < ___MappedColorConstants.MIN_COMPONENT_NORMALIZED ||
            b > ___MappedColorConstants.MAX_COMPONENT_NORMALIZED) {
            throw new IllegalArgumentException("invalid g: " + b);
        }
        Objects.requireNonNull(f, "f is null");
        final var min = Math.min(r, Math.min(g, b));
        final var max = Math.max(r, Math.max(g, b));
        final var e = max == min;
        final var d = max - min;
        double h;
        {
            if (e) {
                h = 0;
            } else if (max == r) {
                h = (g - b) / d % 6;
            } else if (max == g) {
                h = (b - r) / d + 2;
            } else {
                assert max == b;
                h = (r - g) / d + 4;
            }
            h *= 60;
            if (h < 0) {
                h += 360;
            }
        }
        assert h >= ___MappedColorConstants.HSL_MIN_HUE && h <= ___MappedColorConstants.HSL_MAX_HUE;
        final double l;
        {
            l = (max + min) / 2;
        }
        final double s;
        {
            if (e) {
                s = 0;
            } else {
                s = 1 - Math.abs(2 * l - 1);
            }
        }
        assert h >= ___MappedColorConstants.HSL_MIN_HUE && h <= ___MappedColorConstants.HSL_MAX_HUE;
        assert s >= ___MappedColorConstants.MIN_COMPONENT_NORMALIZED &&
               s <= ___MappedColorConstants.MAX_COMPONENT_NORMALIZED;
        assert l >= ___MappedColorConstants.MIN_COMPONENT_NORMALIZED &&
               l <= ___MappedColorConstants.MAX_COMPONENT_NORMALIZED;
        return f.apply((int) h).apply(s).apply(l);
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    private ___MappedColorUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
