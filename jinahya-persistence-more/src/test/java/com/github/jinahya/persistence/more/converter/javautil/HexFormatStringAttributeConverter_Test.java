package com.github.jinahya.persistence.more.converter.javautil;

/*-
 * #%L
 * jinahya-persistence-more
 * %%
 * Copyright (C) 2026 Jinahya, Inc.
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link HexFormatStringAttributeConverter}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("HexFormatStringAttributeConverter")
class HexFormatStringAttributeConverter_Test {

    private static Stream<HexFormatStringAttributeConverter> converters() {
        return Stream.of(
                new HexFormatStringAttributeConverter.OfLowerCase(),
                new HexFormatStringAttributeConverter.OfUpperCase()
        );
    }

    private static Stream<Arguments> texts() {
        final var bytes = new byte[]{0x00, 0x0A, (byte) 0xFB, (byte) 0xFF};
        return Stream.of(
                Arguments.of(new HexFormatStringAttributeConverter.OfLowerCase(), bytes, "000afbff"),
                Arguments.of(new HexFormatStringAttributeConverter.OfUpperCase(), bytes, "000AFBFF")
        );
    }

    @DisplayName("a value is stored as its digits, in the converter's case")
    @MethodSource("texts")
    @ParameterizedTest
    void __text(final HexFormatStringAttributeConverter converter, final byte[] bytes, final String text) {
        assertThat(converter.convertToDatabaseColumn(bytes)).isEqualTo(text);
        assertThat(converter.convertToEntityAttribute(text)).isEqualTo(bytes);
    }

    @DisplayName("a value is read back, of any length")
    @MethodSource("converters")
    @ParameterizedTest
    void __roundTrip(final HexFormatStringAttributeConverter converter) {
        for (int length = 0; length < 64; length++) {
            final var value = new byte[length];
            ThreadLocalRandom.current().nextBytes(value);
            final var column = converter.convertToDatabaseColumn(value);
            assertThat(column).hasSize(length << 1);
            assertThat(converter.convertToEntityAttribute(column)).isEqualTo(value);
        }
    }

    @DisplayName("a converter reads the digits in either case")
    @MethodSource("converters")
    @ParameterizedTest
    void __eitherCase(final HexFormatStringAttributeConverter converter) {
        final var bytes = new byte[]{(byte) 0xFB, (byte) 0xFF};
        assertThat(converter.convertToEntityAttribute("fbff")).isEqualTo(bytes);
        assertThat(converter.convertToEntityAttribute("FBFF")).isEqualTo(bytes);
        assertThat(converter.convertToEntityAttribute("fBfF")).isEqualTo(bytes);
    }

    @DisplayName("the digits sort as the bytes, compared as unsigned")
    @MethodSource("converters")
    @ParameterizedTest
    void __order(final HexFormatStringAttributeConverter converter) {
        for (int i = 0; i < 256; i++) {
            final var a = new byte[8];
            final var b = new byte[8];
            ThreadLocalRandom.current().nextBytes(a);
            ThreadLocalRandom.current().nextBytes(b);
            assertThat(Integer.signum(converter.convertToDatabaseColumn(a)
                                              .compareTo(converter.convertToDatabaseColumn(b))))
                    .isEqualTo(Integer.signum(Arrays.compareUnsigned(a, b)));
        }
    }

    @DisplayName("a column which is not an even number of hexadecimal digits is refused")
    @ParameterizedTest
    @ValueSource(strings = {"f", "fbf", "fg", "fb:ff", "0xfbff"})
    void __notHex(final String text) {
        converters().forEach(c -> assertThatThrownBy(() -> c.convertToEntityAttribute(text))
                .isInstanceOf(IllegalArgumentException.class));
    }

    @MethodSource("converters")
    @ParameterizedTest
    void __null(final HexFormatStringAttributeConverter converter) {
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }

    @Test
    void __empty() {
        converters().forEach(c -> {
            assertThat(c.convertToDatabaseColumn(new byte[0])).isEmpty();
            assertThat(c.convertToEntityAttribute("")).isEmpty();
        });
    }
}
