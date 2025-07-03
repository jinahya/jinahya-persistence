package com.github.jinahya.persistence.more;

import java.util.Objects;
import java.util.function.DoubleFunction;
import java.util.function.IntFunction;

public final class ___MappedColorUtils {

    // https://www.w3.org/TR/css-color-3/#hsl-color
    private static double hslToRgb(final int n, final double h, final double s, final double l) {
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
     *         h, s, l,
     *         r -> g -> b -> {
     *             return null;
     *         }
     * );
     *}
     *
     * @param h   a value of {@code hue} between {@value ___MappedColorConstants#HSL_MIN_HUE} and
     *            {@value ___MappedColorConstants#HSL_MAX_HUE}, both inclusive.
     * @param s   a normalized value of {@code saturation} between
     *            {@value ___MappedColorConstants#HSL_MIN_SATURATION_NORMALIZED} and
     *            {@value ___MappedColorConstants#HSL_MAX_SATURATION_NORMALIZED}, both inclusive.
     * @param l   a normalized value of {@code lightness} between
     *            {@value ___MappedColorConstants#HSL_MIN_LIGHTNESS_NORMALIZED} and
     *            {@value ___MappedColorConstants#HSL_MAX_LIGHTNESS_NORMALIZED}, both inclusive.
     * @param f   the function to be applied with, in currying, <span style="color:red;">R</span><span
     *            style="color:green;">G</span><span style="color:blue;">B</span> color components which each is a
     *            normalized value between {@value ___MappedColorConstants#RGB_MIN_COMPONENT_NORMALIZED} and
     *            {@value ___MappedColorConstants#RGB_MAX_COMPONENT_NORMALIZED}, both inclusive.
     * @param <R> result type parameter
     * @return the result of the {@code function}.
     */
    // https://www.w3.org/TR/css-color-3/#hsl-color
    public static <R> R hslToRgb(
            final int h, final double s, final double l,
            final DoubleFunction<? extends DoubleFunction<? extends DoubleFunction<? extends R>>> f) {
        if (h < ___MappedColorConstants.HSL_MIN_HUE || h > ___MappedColorConstants.HSL_MAX_HUE) {
            throw new IllegalArgumentException("invalid h: " + h);
        }
        if (s < ___MappedColorConstants.HSL_MIN_SATURATION_NORMALIZED ||
            s > ___MappedColorConstants.HSL_MAX_SATURATION_NORMALIZED) {
            throw new IllegalArgumentException("invalid s: " + s);
        }
        if (l < ___MappedColorConstants.HSL_MIN_LIGHTNESS_NORMALIZED ||
            l > ___MappedColorConstants.HSL_MAX_LIGHTNESS_NORMALIZED) {
            throw new IllegalArgumentException("invalid l: " + l);
        }
        Objects.requireNonNull(f, "f is null");
        final var f0 = hslToRgb(0, h, s, l);
        final var f8 = hslToRgb(8, h, s, l);
        final var f4 = hslToRgb(4, h, s, l);
        return f.apply(f0).apply(f8).apply(f4);
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
     *            {@value ___MappedColorConstants#RGB_MIN_COMPONENT_NORMALIZED} and
     *            {@value ___MappedColorConstants#RGB_MAX_COMPONENT_NORMALIZED}, both inclusive.
     * @param g   a normalized value of <span style="color:green;">green</span> component between
     *            {@value ___MappedColorConstants#RGB_MIN_COMPONENT_NORMALIZED} and
     *            {@value ___MappedColorConstants#RGB_MAX_COMPONENT_NORMALIZED}, both inclusive.
     * @param b   a normalized value of <span style="color:blue;">blue</span> component between
     *            {@value ___MappedColorConstants#RGB_MIN_COMPONENT_NORMALIZED} and
     *            {@value ___MappedColorConstants#RGB_MAX_COMPONENT_NORMALIZED}, both inclusive.
     * @param f   the function to be applied with, in currying, a
     *            {@code hue}([{@value ___MappedColorConstants#HSL_MIN_HUE}..{@value
     *            ___MappedColorConstants#HSL_MAX_HUE}]), a
     *            {@code saturation}({@value ___MappedColorConstants#HSL_MIN_SATURATION_NORMALIZED}..{@value
     *            ___MappedColorConstants#HSL_MAX_SATURATION_NORMALIZED}]), and a
     *            {@code lightness}([{@value ___MappedColorConstants#HSL_MIN_LIGHTNESS_NORMALIZED}..{@value
     *            ___MappedColorConstants#HSL_MAX_LIGHTNESS_NORMALIZED}]).
     * @param <R> result type parameter
     * @return the result of the {@code function}.
     */
    public static <R> R rgbToHsl(
            final double r, final double g, final double b,
            final IntFunction<? extends DoubleFunction<? extends DoubleFunction<? extends R>>> f) {
        if (r < __MappedRgba.MIN_COMPONENT_AS_DOUBLE || r > __MappedRgba.MAX_COMPONENT_AS_DOUBLE) {
            throw new IllegalArgumentException("invalid r: " + r);
        }
        if (g < __MappedRgba.MIN_COMPONENT_AS_DOUBLE || g > __MappedRgba.MAX_COMPONENT_AS_DOUBLE) {
            throw new IllegalArgumentException("invalid g: " + g);
        }
        if (b < __MappedRgba.MIN_COMPONENT_AS_DOUBLE || b > __MappedRgba.MAX_COMPONENT_AS_DOUBLE) {
            throw new IllegalArgumentException("invalid b: " + b);
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
        assert s >= ___MappedColorConstants.HSL_MIN_SATURATION_NORMALIZED &&
               s <= ___MappedColorConstants.HSL_MAX_SATURATION_NORMALIZED;
        assert l >= ___MappedColorConstants.HSL_MIN_LIGHTNESS_NORMALIZED &&
               l <= ___MappedColorConstants.HSL_MAX_LIGHTNESS_NORMALIZED;
        return f.apply((int) h).apply(s).apply(l);
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    private ___MappedColorUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}