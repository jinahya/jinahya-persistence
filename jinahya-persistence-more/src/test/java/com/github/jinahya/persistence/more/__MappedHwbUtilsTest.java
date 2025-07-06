package com.github.jinahya.persistence.more;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class __MappedHwbUtilsTest {

    private static Stream<String[]> hwbAndRgb() {
        // google://"hwb to rgb test vectors"
        return Stream.of(
                new String[]{"0", "0", "0", "255", "0", "0"},
                new String[]{"120", "0", "0", "0", "255", "0"},
                new String[]{"240", "0", "0", "0", "0", "255"},
                new String[]{"0", "50", "0", "255", "128", "128"}
        );
    }

    @Disabled
    @MethodSource({"hwbAndRgb"})
    @ParameterizedTest(name = "{index}: h: {0}, w: {1}, b: {2}, r: {3}, g: {4}, b: {5}")
    void hwbToRgb(final int hue, final int whiteness, final int blackness,
                  final int red, final int green, final int blue) {
        __MappedHwbUtils.hwbToRgb(
                hue, whiteness, blackness,
                r -> g -> b -> {
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
