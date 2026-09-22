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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class ___MappedColor_Test {

    private static final double TOLERANCE = 1.0e-9d;

    // ------------------------------------------------------------------------------------------------------- FIXTURES
    private static final class Rgba extends __MappedRgba {

        private Rgba() {
            super();
        }
    }

    private static final class Hsl extends __MappedHsl {

        private Hsl() {
            super();
        }
    }

    private static final class Hwb extends __MappedHwb {

        private Hwb() {
            super();
        }
    }

    private static final class Cmyk extends __MappedCmyk {

        private Cmyk() {
            super();
        }
    }

    // ------------------------------------------------------------------------------------------------------ FACTORIES
    private static Rgba rgba(final double r, final double g, final double b) {
        final var rgba = new Rgba();
        rgba.setSrgb(r, g, b);
        return rgba;
    }

    private static Rgba rgba(final double r, final double g, final double b, final double a) {
        final var rgba = rgba(r, g, b);
        rgba.setAlpha(a);
        return rgba;
    }

    private static Hsl hsl(final double h, final double s, final double l) {
        final var hsl = new Hsl();
        hsl.setHue(h);
        hsl.setSaturation(s);
        hsl.setLightness(l);
        return hsl;
    }

    private static Hwb hwb(final double h, final double w, final double b) {
        final var hwb = new Hwb();
        hwb.setHue(h);
        hwb.setWhiteness(w);
        hwb.setBlackness(b);
        return hwb;
    }

    private static Cmyk cmyk(final double c, final double m, final double y, final double k) {
        final var cmyk = new Cmyk();
        cmyk.setCyan(c);
        cmyk.setMagenta(m);
        cmyk.setYellow(y);
        cmyk.setBlack(k);
        return cmyk;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nested
    class ComponentsTest {

        @DisplayName("the alpha is not a component: rgb and rgba alike report three")
        @Test
        void componentCountExcludesAlpha__() {
            assertThat(new Rgba().getComponentCount()).isEqualTo(__MappedRgb.COMPONENT_COUNT).isEqualTo(3);
            assertThat(new Hsl().getComponentCount()).isEqualTo(3);
            assertThat(new Hwb().getComponentCount()).isEqualTo(3);
            assertThat(new Cmyk().getComponentCount()).isEqualTo(4);
        }

        @DisplayName("the component array holds the coordinates, and not the alpha")
        @Test
        void componentArray__() {
            final var rgba = rgba(1.0d, .25d, .5d, .75d);
            assertThat(rgba.toComponentArray()).containsExactly(1.0d, .25d, .5d);
            assertThat(rgba.getAlpha()).isEqualTo(.75d);
        }

        @DisplayName("every component round-trips through its index, in every model")
        @Test
        void componentByIndexRoundTrips__() {
            for (final ___MappedColor color : new ___MappedColor[]{new Rgba(), new Hsl(), new Hwb(), new Cmyk()}) {
                for (var i = 0; i < color.getComponentCount(); i++) {
                    color.setComponent(i, .25d);
                    assertThat(color.getComponent(i))
                            .as("%s[%d]", color.getClass().getSimpleName(), i)
                            .isCloseTo(.25d, within(TOLERANCE));
                }
            }
        }

        @DisplayName("writing past the last coordinate is rejected")
        @Test
        void setComponentIndexOutOfRange__IndexOutOfBoundsException() {
            assertThatThrownBy(() -> new Rgba().setComponent(3, .5d)).isInstanceOf(IndexOutOfBoundsException.class);
            assertThatThrownBy(() -> new Cmyk().setComponent(4, .5d)).isInstanceOf(IndexOutOfBoundsException.class);
        }

        @DisplayName("an index past the last coordinate is rejected, alpha or not")
        @Test
        void componentIndexOutOfRange__IndexOutOfBoundsException() {
            assertThatThrownBy(() -> new Hsl().getComponent(3)).isInstanceOf(IndexOutOfBoundsException.class);
            assertThatThrownBy(() -> new Rgba().getComponent(3)).isInstanceOf(IndexOutOfBoundsException.class);
            assertThatThrownBy(() -> new Cmyk().getComponent(4)).isInstanceOf(IndexOutOfBoundsException.class);
        }

        @DisplayName("a hue is persisted in degrees, but reported normalized")
        @Test
        void hueIsNormalizedAsAComponent__() {
            final var hsl = hsl(180.0d, .0d, .0d);
            assertThat(hsl.getHue()).isEqualTo(180.0d);
            assertThat(hsl.getComponent(___MappedHueColor.COMPONENT_INDEX_HUE)).isEqualTo(.5d);
        }

        @DisplayName("a hue is an angle: it is reduced onto [0, 360), never rejected")
        @Test
        void hueIsReducedModulo360__() {
            assertThat(hsl(420.0d, .0d, .0d).getHue()).isCloseTo(60.0d, within(TOLERANCE));
            assertThat(hsl(-90.0d, .0d, .0d).getHue()).isCloseTo(270.0d, within(TOLERANCE));
            assertThat(hsl(360.0d, .0d, .0d).getHue()).isEqualTo(___MappedHueColor.MIN_HUE);
            assertThat(hsl(-1.0e-15d, .0d, .0d).getHue())
                    .isGreaterThanOrEqualTo(___MappedHueColor.MIN_HUE)
                    .isLessThan(___MappedHueColor.MAX_HUE);
        }

        @DisplayName("a component out of [0.0, 1.0], or NaN, is rejected")
        @Test
        void componentOutOfRange__IllegalArgumentException() {
            assertThatThrownBy(() -> rgba(1.5d, .0d, .0d)).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> rgba(.0d, .0d, .0d, -.1d)).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> rgba(Double.NaN, .0d, .0d)).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("an eight-bit setter reports the value the caller actually passed")
        @Test
        void eightBitsOutOfRange__IllegalArgumentException() {
            assertThatThrownBy(() -> new Rgba().setRedAsEightBits(300))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("300");
        }

        @DisplayName("an eight-bit value round-trips")
        @Test
        void eightBitsRoundTrip__() {
            final var rgba = new Rgba();
            for (var v = 0; v <= ___MappedColor.MAX_COMPONENT_8_BIT; v++) {
                rgba.setRedAsEightBits(v);
                assertThat(rgba.getRedAsEightBits()).isEqualTo(v);
            }
        }

        @DisplayName("a color with no alpha column is fully opaque")
        @Test
        void alphaDefaultsToOpaque__() {
            assertThat(new Hsl().getAlpha()).isEqualTo(___MappedColor.ALPHA_OPAQUE);
            assertThat(new Cmyk().getAlpha()).isEqualTo(___MappedColor.ALPHA_OPAQUE);
            assertThat(new Rgba().getAlpha()).isEqualTo(___MappedColor.ALPHA_OPAQUE);
            assertThat(new Hsl().isOpaque()).isTrue();
        }

        @DisplayName("opacity is judged on the eight-bit alpha, as rendered")
        @Test
        void opacityIsEightBit__() {
            assertThat(rgba(.0d, .0d, .0d, .999d).isOpaque()).isTrue();
            assertThat(rgba(.0d, .0d, .0d, .5d).isOpaque()).isFalse();
        }

        @DisplayName("hasSameComponentsAs compares components and alpha, within one class")
        @Test
        void hasSameComponentsAs__() {
            final var one = rgba(1.0d, .0d, .0d, .5d);
            final var two = rgba(1.0d, .0d, .0d, .5d);
            assertThat(one.hasSameComponentsAs(two)).isTrue();
            assertThat(one.hasSameComponentsAs(rgba(1.0d, .0d, .0d))).isFalse();
            assertThat(one.hasSameComponentsAs(null)).isFalse();
            assertThat(one.hasSameComponentsAs(new Hsl())).isFalse();
        }

        @DisplayName("no equals/hashCode is inherited: entity identity stays with the entity")
        @Test
        void identityIsNotComponentBased__() {
            final var one = rgba(1.0d, .0d, .0d);
            final var two = rgba(1.0d, .0d, .0d);
            assertThat(one).isNotEqualTo(two);
            assertThat(one).isEqualTo(one);
        }

        @DisplayName("toString prints each mapped column once")
        @Test
        void toStringHasNoDuplicates__() {
            final var text = rgba(1.0d, .0d, .0d, .5d).toString();
            assertThat(text.split("red=", -1)).hasSize(2);
            assertThat(text.split("alpha=", -1)).hasSize(2);
            assertThat(text).doesNotContain("components=");
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nested
    class SrgbTest {

        @DisplayName("hsl(0 100% 50%) is red; hsl(120 100% 50%) is green")
        @Test
        void hslToSrgb__() {
            assertThat(hsl(.0d, 1.0d, .5d).toComponentArrayInSrgb())
                    .containsExactly(1.0d, .0d, .0d);
            assertThat(hsl(120.0d, 1.0d, .5d).toComponentArrayInSrgb())
                    .containsExactly(.0d, 1.0d, .0d);
            assertThat(hsl(.0d, .0d, .5d).toComponentArrayInSrgb())
                    .containsExactly(.5d, .5d, .5d);
        }

        @DisplayName("hwb(0 0% 0%) is red; a whiteness and a blackness summing to 1 give a gray")
        @Test
        void hwbToSrgb__() {
            assertThat(hwb(.0d, .0d, .0d).toComponentArrayInSrgb())
                    .containsExactly(1.0d, .0d, .0d);
            assertThat(hwb(.0d, .5d, .5d).toComponentArrayInSrgb())
                    .containsExactly(.5d, .5d, .5d);
        }

        @DisplayName("device-cmyk(0% 100% 100% 0%) is red")
        @Test
        void cmykToSrgb__() {
            assertThat(cmyk(.0d, 1.0d, 1.0d, .0d).toComponentArrayInSrgb())
                    .containsExactly(1.0d, .0d, .0d);
            assertThat(cmyk(.0d, .0d, .0d, 1.0d).toComponentArrayInSrgb())
                    .containsExactly(.0d, .0d, .0d);
        }

        @DisplayName("setSrgb is the inverse of applySrgb, for every model")
        @Test
        void setSrgbRoundTrips__() {
            final var r = .2d;
            final var g = .7d;
            final var b = .4d;
            for (final ___MappedColor color : new ___MappedColor[]{new Hsl(), new Hwb(), new Cmyk(), new Rgba()}) {
                color.setSrgb(r, g, b);
                assertThat(color.toComponentArrayInSrgb())
                        .as("%s", color.getClass().getSimpleName())
                        .usingComparatorWithPrecision(TOLERANCE)
                        .containsExactly(r, g, b);
            }
        }

        @DisplayName("sRGB -> CMYK -> sRGB is exact, not approximate")
        @Test
        void cmykInverseIsExact__() {
            final var cmyk = new Cmyk();
            for (var i = 0; i <= 20; i++) {
                for (var j = 0; j <= 20; j++) {
                    final var r = i / 20.0d;
                    final var g = j / 20.0d;
                    final var b = ((i + j) % 21) / 20.0d;
                    cmyk.setSrgb(r, g, b);
                    assertThat(cmyk.toComponentArrayInSrgb())
                            .as("(%s, %s, %s)", r, g, b)
                            .usingComparatorWithPrecision(TOLERANCE)
                            .containsExactly(r, g, b);
                }
            }
        }

        @DisplayName("CMYK -> sRGB -> CMYK projects onto maximum black, and is not the identity")
        @Test
        void cmykRoundTripProjectsOntoMaximumBlack__() {
            // the naive forward conversion is not injective; these two are different ink mixes rendering alike
            final var gray = cmyk(.5d, .5d, .5d, .0d);
            final var back = new Cmyk();
            gray.applySrgb(r -> g -> b -> {
                back.setSrgb(r, g, b);
                return null;
            });
            assertThat(back.toComponentArray())
                    .usingComparatorWithPrecision(TOLERANCE)
                    .containsExactly(.0d, .0d, .0d, .5d);
            assertThat(back.toComponentArrayInSrgb())
                    .usingComparatorWithPrecision(TOLERANCE)
                    .containsExactly(gray.toComponentArrayInSrgb());
        }

        @DisplayName("a color converts to another model through sRGB")
        @Test
        void convertsBetweenModels__() {
            final var hsl = hsl(240.0d, 1.0d, .5d);
            final var cmyk = new Cmyk();
            hsl.applySrgb(r -> g -> b -> {
                cmyk.setSrgb(r, g, b);
                return null;
            });
            assertThat(cmyk.getCyan()).isCloseTo(1.0d, within(TOLERANCE));
            assertThat(cmyk.getMagenta()).isCloseTo(1.0d, within(TOLERANCE));
            assertThat(cmyk.getYellow()).isCloseTo(.0d, within(TOLERANCE));
            assertThat(cmyk.getBlack()).isCloseTo(.0d, within(TOLERANCE));
        }

        @DisplayName("setSrgb leaves the alpha alone")
        @Test
        void setSrgbKeepsAlpha__() {
            final var rgba = rgba(.0d, .0d, .0d, .25d);
            rgba.setSrgb(.1d, .2d, .3d);
            assertThat(rgba.getAlpha()).isEqualTo(.25d);
        }

        @DisplayName("setSrgb rejects an out-of-range argument")
        @Test
        void setSrgbOutOfRange__IllegalArgumentException() {
            assertThatThrownBy(() -> new Cmyk().setSrgb(1.5d, .0d, .0d)).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("an out-of-gamut rgb produces a positive saturation, rotated 180 degrees, per the spec")
        @Test
        void outOfGamutSaturationIsCorrected__() {
            // rgbToHsl(1.5, 1, 1) would otherwise yield a negative saturation
            // see https://github.com/w3c/csswg-drafts/issues/9222
            ___MappedColorUtils.rgbToHsl(1.5d, 1.0d, 1.0d, h -> s -> l -> {
                assertThat(s).isGreaterThanOrEqualTo(.0d);
                assertThat(h)
                        .isGreaterThanOrEqualTo(___MappedHueColor.MIN_HUE)
                        .isLessThan(___MappedHueColor.MAX_HUE);
                return null;
            });
        }

        @DisplayName("sRGB round-trips through the conversion utilities")
        @Test
        void utilsRoundTrip__() {
            final var r = .2d;
            final var g = .7d;
            final var b = .4d;
            ___MappedColorUtils.rgbToHsl(r, g, b, h -> s -> l -> ___MappedColorUtils.hslToRgb(
                    h, s, l,
                    r2 -> g2 -> b2 -> {
                        assertThat(r2).isCloseTo(r, within(TOLERANCE));
                        assertThat(g2).isCloseTo(g, within(TOLERANCE));
                        assertThat(b2).isCloseTo(b, within(TOLERANCE));
                        return null;
                    }
            ));
            ___MappedColorUtils.rgbToHwb(r, g, b, h -> w -> k -> ___MappedColorUtils.hwbToRgb(
                    h, w, k,
                    r2 -> g2 -> b2 -> {
                        assertThat(r2).isCloseTo(r, within(TOLERANCE));
                        assertThat(g2).isCloseTo(g, within(TOLERANCE));
                        assertThat(b2).isCloseTo(b, within(TOLERANCE));
                        return null;
                    }
            ));
            ___MappedColorUtils.rgbToCmyk(r, g, b, c -> m -> y -> k -> ___MappedColorUtils.cmykToRgb(
                    c, m, y, k,
                    r2 -> g2 -> b2 -> {
                        assertThat(r2).isCloseTo(r, within(TOLERANCE));
                        assertThat(g2).isCloseTo(g, within(TOLERANCE));
                        assertThat(b2).isCloseTo(b, within(TOLERANCE));
                        return null;
                    }
            ));
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nested
    class NotationTest {

        @DisplayName("an opaque color serializes without an alpha, in either syntax")
        @Test
        void opaque__() {
            final var red = rgba(1.0d, .0d, .0d);
            assertThat(red.toHexNotation()).isEqualTo("#ff0000");
            assertThat(red.toLegacyRgbNotation()).isEqualTo("rgb(255, 0, 0)");
            assertThat(red.toModernRgbNotation()).isEqualTo("rgb(255 0 0)");
        }

        @DisplayName("a translucent color takes rgba() in the legacy syntax, and a solidus in the modern one")
        @Test
        void translucent__() {
            final var red = rgba(1.0d, .0d, .0d, .5d);
            assertThat(red.toHexNotation()).isEqualTo("#ff000080");
            assertThat(red.toLegacyRgbNotation()).isEqualTo("rgba(255, 0, 0, 0.5)");
            assertThat(red.toModernRgbNotation()).isEqualTo("rgb(255 0 0 / 0.5)");
        }

        @DisplayName("the alpha serializes as the shortest decimal rendering identically")
        @Test
        void alphaIsShortest__() {
            final var red = rgba(1.0d, .0d, .0d);
            red.setAlphaAsEightBits(128);
            assertThat(red.getAlpha()).isEqualTo(128 / 255.0d);
            assertThat(red.toModernRgbNotation()).isEqualTo("rgb(255 0 0 / 0.5)");
            red.setAlphaAsEightBits(85);
            assertThat(red.toModernRgbNotation()).isEqualTo("rgb(255 0 0 / 0.333)");
            red.setAlpha(.0d);
            assertThat(red.toModernRgbNotation()).isEqualTo("rgb(255 0 0 / 0)");
        }

        @DisplayName("a color of any model serializes through its sRGB conversion")
        @Test
        void anyModel__() {
            assertThat(hsl(240.0d, 1.0d, .5d).toModernRgbNotation())
                    .isEqualTo("rgb(0 0 255)");
            assertThat(cmyk(1.0d, 1.0d, .0d, .0d).toHexNotation())
                    .isEqualTo("#0000ff");
        }

        @DisplayName("hsl() serializes in both the legacy and the modern syntax, losslessly")
        @Test
        void hslNotation__() {
            final var green = hsl(120.0d, 1.0d, .5d);
            assertThat(green.toLegacyHslNotation()).isEqualTo("hsl(120, 100%, 50%)");
            assertThat(green.toModernHslNotation()).isEqualTo("hsl(120 100% 50%)");
            // the rgb() notations round the components onto eight bits; hsl() keeps them
            final var odd = hsl(123.5d, .333d, .5d);
            assertThat(odd.toModernHslNotation()).isEqualTo("hsl(123.5 33.3% 50%)");
        }

        @DisplayName("hwb() has no legacy syntax, so there is only one notation")
        @Test
        void hwbNotation__() {
            assertThat(hwb(120.0d, .0d, .0d).toHwbNotation()).isEqualTo("hwb(120 0% 0%)");
            assertThat(hwb(120.0d, .25d, .5d).toHwbNotation()).isEqualTo("hwb(120 25% 50%)");
        }

        @DisplayName("device-cmyk() has both a legacy and a modern syntax, unlike hwb()")
        @Test
        void deviceCmykNotation__() {
            final var red = cmyk(.0d, 1.0d, 1.0d, .0d);
            assertThat(red.toModernDeviceCmykNotation()).isEqualTo("device-cmyk(0% 100% 100% 0%)");
            // the legacy grammar is device-cmyk( <number>#{4} ): commas, numbers, no alpha
            assertThat(red.toLegacyDeviceCmykNotation()).isEqualTo("device-cmyk(0, 1, 1, 0)");
        }

        @DisplayName("every hex form of CSS Color 4 parses, with or without the leading #")
        @Test
        void hexNotationParses__() {
            record Rgba4(double r, double g, double b, double a) {

            }
            final var parse = (java.util.function.Function<String, Rgba4>) n -> ___MappedColorUtils.applyHexNotation(
                    n, r -> g -> b -> a -> new Rgba4(r, g, b, a)
            );
            assertThat(parse.apply("#f00")).isEqualTo(new Rgba4(1.0d, .0d, .0d, 1.0d));
            assertThat(parse.apply("f00")).isEqualTo(new Rgba4(1.0d, .0d, .0d, 1.0d));
            assertThat(parse.apply("#ff0000")).isEqualTo(new Rgba4(1.0d, .0d, .0d, 1.0d));
            assertThat(parse.apply("#F00F")).isEqualTo(new Rgba4(1.0d, .0d, .0d, 1.0d));
            assertThat(parse.apply("#ff000080")).isEqualTo(new Rgba4(1.0d, .0d, .0d, 128 / 255.0d));
        }

        @DisplayName("hex notation round-trips through toHexNotation")
        @Test
        void hexNotationRoundTrips__() {
            final var rgba = new Rgba();
            ___MappedColorUtils.applyHexNotation("#1a2b3c80", r -> g -> b -> a -> {
                rgba.setSrgb(r, g, b);
                rgba.setAlpha(a);
                return null;
            });
            assertThat(rgba.toHexNotation()).isEqualTo("#1a2b3c80");
        }

        @DisplayName("a malformed hex notation is rejected")
        @Test
        void malformedHexNotation__IllegalArgumentException() {
            for (final var bad : new String[]{"", "#", "#ff", "#fffff", "#gg0000", "#ff00000"}) {
                assertThatThrownBy(() -> ___MappedColorUtils.applyHexNotation(bad, r -> g -> b -> a -> null))
                        .as("%s", bad)
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }
    }
}
