package com.github.jinahya.persistence.more.colormodel;

/*-
 * #%L
 * jinahya-persistence-more
 * %%
 * Copyright (C) 2025 - 2026 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.Objects;
import java.util.function.DoubleFunction;

/**
 * Conversions between the color models mapped in this package and
 * <a href="https://www.w3.org/TR/css-color-4/#numeric-srgb">sRGB</a>.
 * <p>
 * Every method takes and applies <em>normalized</em> components, between {@value ___MappedColor#MIN_COMPONENT} and
 * {@value ___MappedColor#MAX_COMPONENT}, both inclusive — except a hue, which is in degrees; see
 * {@link ___MappedHueColor#MIN_HUE} and {@link ___MappedHueColor#MAX_HUE}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://www.w3.org/TR/css-color-4/">CSS Color Module Level 4</a>
 * @see <a href="https://www.w3.org/TR/css-color-4/#color-conversion-code">CSS Color 4, &sect;19 Sample code for
 *         Color Conversions</a>
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class ___MappedColorUtils {

    // ------------------------------------------------------------------------------------------------------ HSL <-> RGB
    // https://www.w3.org/TR/css-color-4/#hsl-to-rgb
    private static double hslToRgb(final double n, final double h, final double s, final double l) {
        final var k = (n + h / 30.0d) % 12.0d;
        final var a = s * Math.min(l, 1.0d - l);
        return l - a * Math.clamp(Math.min(k - 3.0d, 9.0d - k), -1.0d, 1.0d);
    }

    /**
     * Applies the sRGB components, converted from specified HSL components, to specified function, and returns the
     * result.
     *
     * @param hue        a hue, in degrees.
     * @param saturation a normalized saturation.
     * @param lightness  a normalized lightness.
     * @param function   the function to be applied with, in currying, the normalized
     *                   <span style="color:red;">red</span>, <span style="color:green;">green</span> and
     *                   <span style="color:blue;">blue</span> components.
     * @param <R>        result type parameter
     * @return the result of the {@code function}.
     * @see #rgbToHsl(double, double, double, DoubleFunction)
     * @see <a href="https://www.w3.org/TR/css-color-4/#hsl-to-rgb">CSS Color 4, &sect;7.1 Converting HSL Colors
     *         to sRGB</a>
     */
    // https://www.w3.org/TR/css-color-4/#hsl-to-rgb
    public static <R> R hslToRgb(
            final double hue, final double saturation, final double lightness,
            final DoubleFunction<? extends DoubleFunction<? extends DoubleFunction<? extends R>>> function) {
        Objects.requireNonNull(function, "function is null");
        final var h = ___MappedHueColor.reduceHue(hue);
        return function
                .apply(hslToRgb(0.0d, h, saturation, lightness))
                .apply(hslToRgb(8.0d, h, saturation, lightness))
                .apply(hslToRgb(4.0d, h, saturation, lightness));
    }

    /**
     * Applies the HSL components, converted from specified sRGB components, to specified function, and returns the
     * result.
     *
     * @param r        a normalized <span style="color:red;">red</span> component.
     * @param g        a normalized <span style="color:green;">green</span> component.
     * @param b        a normalized <span style="color:blue;">blue</span> component.
     * @param function the function to be applied with, in currying, a hue in degrees, a normalized saturation, and a
     *                 normalized lightness.
     * @param <R>      result type parameter
     * @return the result of the {@code function}.
     * @implNote An achromatic color has no hue to recover. The sample code of the spec yields
     *         {@link Double#NaN} there, marking the hue a <a href="https://www.w3.org/TR/css-color-4/#missing">missing
     *         component</a>; this method yields {@value ___MappedHueColor#MIN_HUE} instead, which is how a missing hue
     *         behaves when it is used, because nothing in this package can persist a missing component.
     * @see #hslToRgb(double, double, double, DoubleFunction)
     * @see <a href="https://www.w3.org/TR/css-color-4/#rgb-to-hsl">CSS Color 4, &sect;7.2 Converting sRGB Colors
     *         to HSL</a>
     */
    // https://www.w3.org/TR/css-color-4/#rgb-to-hsl
    public static <R> R rgbToHsl(
            final double r, final double g, final double b,
            final DoubleFunction<? extends DoubleFunction<? extends DoubleFunction<? extends R>>> function) {
        Objects.requireNonNull(function, "function is null");
        final var max = Math.max(r, Math.max(g, b));
        final var min = Math.min(r, Math.min(g, b));
        final var d = max - min;
        final var l = (min + max) / 2.0d;
        var h = 0.0d;
        var s = 0.0d;
        if (d != 0.0d) {
            s = (l == 0.0d || l == 1.0d) ? 0.0d : (max - l) / Math.min(l, 1.0d - l);
            if (max == r) {
                h = (g - b) / d + (g < b ? 6.0d : 0.0d);
            } else if (max == g) {
                h = (b - r) / d + 2.0d;
            } else {
                h = (r - g) / d + 4.0d;
            }
            h *= 60.0d;
        }
        // a very out-of-gamut color can produce a negative saturation; rotate the hue by 180 and take the
        // magnitude, as the sample code of the spec does
        // see https://github.com/w3c/csswg-drafts/issues/9222
        if (s < 0.0d) {
            h += 180.0d;
            s = Math.abs(s);
        }
        if (h >= ___MappedHueColor.MAX_HUE) {
            h -= ___MappedHueColor.MAX_HUE;
        }
        return function.apply(h).apply(s).apply(l);
    }

    // ----------------------------------------------------------------------------------------------------- HWB <-> RGB

    /**
     * Applies the sRGB components, converted from specified HWB components, to specified function, and returns the
     * result.
     *
     * @param hue       a hue, in degrees.
     * @param whiteness a normalized whiteness.
     * @param blackness a normalized blackness.
     * @param function  the function to be applied with, in currying, the normalized
     *                  <span style="color:red;">red</span>, <span style="color:green;">green</span> and
     *                  <span style="color:blue;">blue</span> components.
     * @param <R>       result type parameter
     * @return the result of the {@code function}.
     * @see #rgbToHwb(double, double, double, DoubleFunction)
     * @see <a href="https://www.w3.org/TR/css-color-4/#hwb-to-rgb">CSS Color 4, &sect;8.1 Converting HWB Colors
     *         to sRGB</a>
     */
    // https://www.w3.org/TR/css-color-4/#hwb-to-rgb
    public static <R> R hwbToRgb(
            final double hue, final double whiteness, final double blackness,
            final DoubleFunction<? extends DoubleFunction<? extends DoubleFunction<? extends R>>> function) {
        Objects.requireNonNull(function, "function is null");
        if (whiteness + blackness >= 1.0d) {
            final var gray = whiteness / (whiteness + blackness);
            return function.apply(gray).apply(gray).apply(gray);
        }
        final var scale = 1.0d - whiteness - blackness;
        return hslToRgb(
                hue, 1.0d, 0.5d,
                r -> g -> b -> function
                        .apply(r * scale + whiteness)
                        .apply(g * scale + whiteness)
                        .apply(b * scale + whiteness)
        );
    }

    /**
     * Applies the HWB components, converted from specified sRGB components, to specified function, and returns the
     * result.
     *
     * @param r        a normalized <span style="color:red;">red</span> component.
     * @param g        a normalized <span style="color:green;">green</span> component.
     * @param b        a normalized <span style="color:blue;">blue</span> component.
     * @param function the function to be applied with, in currying, a hue in degrees, a normalized whiteness, and a
     *                 normalized blackness.
     * @param <R>      result type parameter
     * @return the result of the {@code function}.
     * @see #hwbToRgb(double, double, double, DoubleFunction)
     * @see <a href="https://www.w3.org/TR/css-color-4/#rgb-to-hwb">CSS Color 4, &sect;8.2 Converting sRGB Colors
     *         to HWB</a>
     */
    // https://www.w3.org/TR/css-color-4/#rgb-to-hwb
    public static <R> R rgbToHwb(
            final double r, final double g, final double b,
            final DoubleFunction<? extends DoubleFunction<? extends DoubleFunction<? extends R>>> function) {
        Objects.requireNonNull(function, "function is null");
        final var whiteness = Math.min(r, Math.min(g, b));
        final var blackness = 1.0d - Math.max(r, Math.max(g, b));
        return rgbToHsl(r, g, b, h -> s -> l -> function.apply(h).apply(whiteness).apply(blackness));
    }

    // ---------------------------------------------------------------------------------------------------- CMYK <-> RGB

    /**
     * Applies the sRGB components, converted from specified CMYK components, to specified function, and returns the
     * result.
     * <p>
     * The conversion is the naive one CSS Color 5 specifies for
     * <a href="https://www.w3.org/TR/css-color-5/#device-cmyk">{@code device-cmyk()}</a> when no color profile is
     * known. It is not colorimetrically accurate, and no round trip through a real CMYK device is implied.
     *
     * @param cyan     a normalized <span style="color:cyan;">cyan</span> component.
     * @param magenta  a normalized <span style="color:magenta;">magenta</span> component.
     * @param yellow   a normalized <span style="color:yellow;">yellow</span> component.
     * @param black    a normalized black component.
     * @param function the function to be applied with, in currying, the normalized
     *                 <span style="color:red;">red</span>, <span style="color:green;">green</span> and
     *                 <span style="color:blue;">blue</span> components.
     * @param <R>      result type parameter
     * @return the result of the {@code function}.
     * @see #rgbToCmyk(double, double, double, DoubleFunction)
     * @see <a href="https://www.w3.org/TR/css-color-5/#device-cmyk">CSS Color 5, &sect;6.1 Naively Converting
     *         Between Uncalibrated CMYK and sRGB-Based Color</a>
     */
    // https://www.w3.org/TR/css-color-5/#device-cmyk
    public static <R> R cmykToRgb(
            final double cyan, final double magenta, final double yellow, final double black,
            final DoubleFunction<? extends DoubleFunction<? extends DoubleFunction<? extends R>>> function) {
        Objects.requireNonNull(function, "function is null");
        return function
                .apply(1.0d - Math.min(1.0d, cyan * (1.0d - black) + black))
                .apply(1.0d - Math.min(1.0d, magenta * (1.0d - black) + black))
                .apply(1.0d - Math.min(1.0d, yellow * (1.0d - black) + black));
    }

    /**
     * Applies the CMYK components, converted from specified sRGB components, to specified function, and returns the
     * result.
     * <p>
     * <strong>CSS specifies no conversion in this direction.</strong> CSS Color 5 defines only
     * {@linkplain #cmykToRgb(double, double, double, double, DoubleFunction) the naive conversion to sRGB}, and says of
     * {@code device-cmyk()} that it &quot;has no colorimetric basis&quot;. Everything below is therefore this method's
     * own contract, not the spec's.
     * <p>
     * What this method computes is nonetheless <em>exact</em>, not an approximation. The naive forward conversion is
     * {@code r = (1-c)(1-k)}, {@code g = (1-m)(1-k)}, {@code b = (1-y)(1-k)}, which is not injective: infinitely many
     * CMYK quadruples render to one sRGB color. This method picks one of them by
     * <b>maximum black generation</b> — the unique preimage whose {@code min(c, m, y)} is {@code 0}, that is, the one
     * using as much black ink as possible:
     * <pre>{@code
     * k = 1 - max(r, g, b)
     * c = (1 - r - k) / (1 - k)      m = (1 - g - k) / (1 - k)      y = (1 - b - k) / (1 - k)
     * }</pre>
     * Given that choice, the algebra is exact and no rounding is introduced. Two consequences follow, and both are
     * covered by tests:
     * <ul>
     *   <li>{@code sRGB → CMYK → sRGB} is the identity, to within the rounding of a {@code double}.</li>
     *   <li>{@code CMYK → sRGB → CMYK} is <em>not</em> the identity. It projects onto the maximum-black surface:
     *       {@code device-cmyk(0.5 0.5 0.5 0)} returns as {@code device-cmyk(0 0 0 0.5)}. Both render to the same
     *       sRGB color; they are different ink mixes.</li>
     * </ul>
     * A real conversion for a real printer needs that printer's ICC profile, which is outside the scope of both CSS
     * and this package.
     *
     * @param r        a normalized <span style="color:red;">red</span> component.
     * @param g        a normalized <span style="color:green;">green</span> component.
     * @param b        a normalized <span style="color:blue;">blue</span> component.
     * @param function the function to be applied with, in currying, the normalized
     *                 <span style="color:cyan;">cyan</span>, <span style="color:magenta;">magenta</span>,
     *                 <span style="color:yellow;">yellow</span> and black components.
     * @param <R>      result type parameter
     * @return the result of the {@code function}.
     * @see #cmykToRgb(double, double, double, double, DoubleFunction)
     * @see <a href="https://www.w3.org/TR/css-color-5/#device-cmyk">CSS Color 5, &sect;6 Uncalibrated CMYK Colors:
     *         the device-cmyk() Function</a>
     */
    public static <R> R rgbToCmyk(
            final double r, final double g, final double b,
            final DoubleFunction<
                    ? extends DoubleFunction<
                            ? extends DoubleFunction<
                                    ? extends DoubleFunction<? extends R>>>> function) {
        Objects.requireNonNull(function, "function is null");
        final var black = 1.0d - Math.max(r, Math.max(g, b));
        if (black == 1.0d) {
            return function.apply(0.0d).apply(0.0d).apply(0.0d).apply(black);
        }
        return function
                .apply((1.0d - r - black) / (1.0d - black))
                .apply((1.0d - g - black) / (1.0d - black))
                .apply((1.0d - b - black) / (1.0d - black))
                .apply(black);
    }

    // ------------------------------------------------------------------------------------------------- HEX NOTATION

    private static int digit(final String digits, final int index, final String notation) {
        final var c = digits.charAt(index);
        final var digit = c >= '0' && c <= '9' ? c - '0'
                : c >= 'a' && c <= 'f' ? c - 'a' + 10
                : c >= 'A' && c <= 'F' ? c - 'A' + 10
                : -1;
        if (digit < 0) {
            throw new IllegalArgumentException("invalid hex notation: " + notation);
        }
        return digit;
    }

    /**
     * Applies the sRGB components and the alpha, parsed from specified
     * <a href="https://www.w3.org/TR/css-color-4/#hex-notation">hex notation</a>, to specified function, and returns
     * the result.
     * <p>
     * All four forms CSS Color 4 defines are accepted, with or without the leading {@code #}: {@code rgb},
     * {@code rgba}, {@code rrggbb} and {@code rrggbbaa}. In the two short forms each digit is doubled, as the spec
     * prescribes — {@code #f00} is {@code #ff0000}. When the notation carries no alpha, the alpha applied is
     * {@value ___MappedColor#ALPHA_OPAQUE}.
     * <p>
     * This is the counterpart of {@link ___MappedColor#toHexNotation()}; paired with
     * {@link ___MappedColor#setSrgb(double, double, double)} it loads a color of any model from a hex string:
     * {@snippet lang = "java":
     * applyHexNotation("#ff000080", r -> g -> b -> a -> { rgba.setSrgb(r, g, b); rgba.setAlpha(a); return null; });
     *}
     *
     * @param notation the hex notation to parse.
     * @param function the function to be applied with, in currying, the normalized
     *                 <span style="color:red;">red</span>, <span style="color:green;">green</span> and
     *                 <span style="color:blue;">blue</span> components, and the alpha.
     * @param <R>      result type parameter
     * @return the result of the {@code function}.
     * @throws IllegalArgumentException when the {@code notation} is not a valid hex notation.
     * @see <a href="https://www.w3.org/TR/css-color-4/#hex-notation">CSS Color 4, &sect;4.2 The RGB Hexadecimal
     *         Notations</a>
     */
    public static <R> R applyHexNotation(
            final String notation,
            final DoubleFunction<
                    ? extends DoubleFunction<
                            ? extends DoubleFunction<
                                    ? extends DoubleFunction<? extends R>>>> function) {
        Objects.requireNonNull(notation, "notation is null");
        Objects.requireNonNull(function, "function is null");
        final var digits = notation.startsWith("#") ? notation.substring(1) : notation;
        final var length = digits.length();
        if (length != 3 && length != 4 && length != 6 && length != 8) {
            throw new IllegalArgumentException("invalid hex notation: " + notation);
        }
        final var shorthand = length < 5;
        final var count = shorthand ? length : length / 2;
        final var values = new double[]{
                ___MappedColor.MIN_COMPONENT,
                ___MappedColor.MIN_COMPONENT,
                ___MappedColor.MIN_COMPONENT,
                ___MappedColor.ALPHA_OPAQUE // opaque unless the notation carries an alpha
        };
        for (var i = 0; i < count; i++) {
            final var value = shorthand
                    ? digit(digits, i, notation) * 17
                    : digit(digits, i * 2, notation) * 16 + digit(digits, i * 2 + 1, notation);
            values[i] = value / (double) ___MappedColor.MAX_COMPONENT_8_BIT;
        }
        return function.apply(values[0]).apply(values[1]).apply(values[2]).apply(values[3]);
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    private ___MappedColorUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
