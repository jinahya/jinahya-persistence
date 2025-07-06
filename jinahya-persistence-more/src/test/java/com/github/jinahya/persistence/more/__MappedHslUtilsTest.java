package com.github.jinahya.persistence.more;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class __MappedHslUtilsTest {

    private static Stream<Arguments> hslAndRgb() {
        // google://"hsl to rgb test vector with color name"
        return Stream.of(
                Arguments.of("0", "100", "50", "255", "0", "0"),
                Arguments.of("120", "100", "50", "0", "255", "0"),
                Arguments.of("240", "100", "50", "0", "0", "255"),
                Arguments.of("0", "0", "0", "0", "0", "0"),
                Arguments.of("0", "0", "100", "255", "255", "255"),
                Arguments.of("0", "100", "25", "128", "0", "0"),
                Arguments.of("0", "100", "75", "255", "128", "128"),
                Arguments.of("120", "50", "50", "64", "191", "64"),
                Arguments.of("240", "50", "50", "64", "64", "191"),
                Arguments.of("180", "100", "50", "0", "255", "255"),
                Arguments.of("300", "100", "50", "255", "0", "255"),
                Arguments.of("60", "100", "50", "255", "255", "0"),
//                Arguments.of("180", "50", "25", "0", "64", "64"),
                Arguments.of("180", "50", "25", "32", "96", "96"),
//                Arguments.of("180", "50", "75", "128", "255", "255")
                Arguments.of("180", "50", "75", "159", "223", "223")
        );
    }

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
    @ParameterizedTest(name = "[{index}]: {0}, {1}%, {2}% -> {3}, {4}, {5}")
    void hslToRgb__(final int hue, final int saturation, final int lightness,
                    final int red, final int green, final int blue) {
        __MappedHslUtils.hslToRgb(
                hue, saturation, lightness,
                r -> g -> b -> {
                    log.info("r: {}, g: {}, b:{}", r, g, b);
                    assertThat((int) Math.round(r * ___MappedColorConstants.RGB_MAX_COMPONENT))
                            .as("r")
                            .isEqualTo(red);
                    assertThat((int) Math.round(g * ___MappedColorConstants.RGB_MAX_COMPONENT))
                            .as("g")
                            .isEqualTo(green);
                    assertThat((int) Math.round(b * ___MappedColorConstants.RGB_MAX_COMPONENT))
                            .as("b")
                            .isEqualTo(blue);
                    return null;
                }
        );
    }
}
