package com.github.jinahya.persistence.more;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class ___MappedColorUtilsTest {

    private static Stream<String[]> hslAndRgb() {
        // google://"hsl to rgb test vector with color name"
        return Stream.of(
                new String[]{"0", "1", "0.5", "1", "0", "0"},   // Red
                new String[]{"60", "1", "0.5", "1", "1", "0"},  // Yellow
                new String[]{"120", "1", "0.5", "0", "1", "0"}, // green
                new String[]{"300", "1", "0.5", "1", "0", "1"}  // magenta
        );
    }
//    private static Stream<Arguments> hslAndRgb() {
//        // google://"hsl to rgb test vector with color name"
//        return Stream.of(
//                Arguments.of("0", "1", "0.5", "1", "0", "0")
//        );
//    }

    // -----------------------------------------------------------------------------------------------------------------
    @Test
    void hslToRgb_000_() {
        ___MappedColorUtils.hslToRgb(
                0, 0, 0,
                r -> g -> b -> {
                    assertThat(r).isZero();
                    assertThat(g).isZero();
                    assertThat(b).isZero();
                    return null;
                }
        );
    }

    @MethodSource({"hslAndRgb"})
    @ParameterizedTest(name = "{index}: h: {0}, s: {1}, l: {2}, r: {3}, g: {4}, b: {5}")
    void hslToRgb__(final int hue, final double saturation, final double lightness,
                    final double red, final double green, final double blue) {
        ___MappedColorUtils.hslToRgb(
                hue, saturation, lightness,
                r -> g -> b -> {
                    log.info("r: {}, g: {}, b:{}", r, g, b);
                    assertThat(r).as("r").isEqualTo(red);
                    assertThat(g).as("g").isEqualTo(green);
                    assertThat(b).as("b").isEqualTo(blue);
                    return null;
                }
        );
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Test
    void rgbToHsl_000_() {
        ___MappedColorUtils.rgbToHsl(
                0, 0, 0,
                h -> s -> l -> {
                    assertThat(h).isZero();
                    assertThat(s).isZero();
                    assertThat(l).isZero();
                    return null;
                }
        );
    }

    @MethodSource({"hslAndRgb"})
    @ParameterizedTest(name = "{index}: h: {0}, s: {1}, l: {2}, r: {3}, g: {4}, b: {5}")
    void rgbToHsl__(final int hue, final double saturation, final double lightness,
                    final double red, final double green, final double blue) {
        ___MappedColorUtils.rgbToHsl(
                red, green, blue,
                h -> s -> l -> {
                    log.info("h: {}, s: {}, l: {}", h, s, l);
                    assertThat(h).as("h").isEqualTo(hue);
                    assertThat(s).as("s").isEqualTo(saturation);
                    assertThat(l).as("l").isEqualTo(lightness);
                    return null;
                }
        );
    }

    @MethodSource({"hslAndRgb"})
    @ParameterizedTest(name = "{index}: h: {0}, s: {1}, l: {2}, r: {3}, g: {4}, b: {5}")
    void rgbToHslToRgb__(final int hue, final double saturation, final double lightness,
                         final double red, final double green, final double blue) {
        ___MappedColorUtils.rgbToHsl(
                red, green, blue,
                h -> s -> l -> {
                    log.info("h: {}, s: {}, l: {}", h, s, l);
                    assertThat(h).as("h").isEqualTo(hue);
                    assertThat(s).as("s").isEqualTo(saturation);
                    assertThat(l).as("l").isEqualTo(lightness);
                    ___MappedColorUtils.hslToRgb(h, s, l, r -> g -> b -> {
                        assertThat(r).isEqualTo(red);
                        assertThat(g).isEqualTo(green);
                        assertThat(b).isEqualTo(blue);
                        return null;
                    });
                    return null;
                }
        );
    }

    @MethodSource({"hslAndRgb"})
    @ParameterizedTest(name = "{index}: h: {0}, s: {1}, l: {2}, r: {3}, g: {4}, b: {5}")
    void hslToRgbToHsl__(final int hue, final double saturation, final double lightness,
                         final double red, final double green, final double blue) {
        ___MappedColorUtils.hslToRgb(
                hue, saturation, lightness, r -> g -> b -> {
                    log.info("r: {}, g: {}, b:{}", r, g, b);
                    assertThat(r).as("r").isEqualTo(red);
                    assertThat(g).as("g").isEqualTo(green);
                    assertThat(b).as("b").isEqualTo(blue);
                    ___MappedColorUtils.rgbToHsl(r, g, b, h -> s -> l -> {
                        assertThat(r).isEqualTo(red);
                        assertThat(g).isEqualTo(green);
                        assertThat(b).isEqualTo(blue);
                        return null;
                    });
                    return null;
                });
    }
}
