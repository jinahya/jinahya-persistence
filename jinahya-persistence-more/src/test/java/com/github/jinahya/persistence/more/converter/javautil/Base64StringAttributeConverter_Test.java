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

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link Base64StringAttributeConverter}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("Base64StringAttributeConverter")
class Base64StringAttributeConverter_Test {

    private static Stream<Base64StringAttributeConverter> converters() {
        return Stream.of(
                new Base64StringAttributeConverter.OfBasic(),
                new Base64StringAttributeConverter.OfBasicWithoutPadding(),
                new Base64StringAttributeConverter.OfUrl(),
                new Base64StringAttributeConverter.OfUrlWithoutPadding(),
                new Base64StringAttributeConverter.OfMime(),
                new Base64StringAttributeConverter.OfMimeWithoutPadding()
        );
    }

    // {0xFB, 0xFF} is the one which shows the alphabet, and its padding
    private static Stream<Arguments> texts() {
        final var bytes = new byte[]{(byte) 0xFB, (byte) 0xFF};
        return Stream.of(
                Arguments.of(new Base64StringAttributeConverter.OfBasic(), bytes, "+/8="),
                Arguments.of(new Base64StringAttributeConverter.OfBasicWithoutPadding(), bytes, "+/8"),
                Arguments.of(new Base64StringAttributeConverter.OfUrl(), bytes, "-_8="),
                Arguments.of(new Base64StringAttributeConverter.OfUrlWithoutPadding(), bytes, "-_8"),
                Arguments.of(new Base64StringAttributeConverter.OfMime(), bytes, "+/8="),
                Arguments.of(new Base64StringAttributeConverter.OfMimeWithoutPadding(), bytes, "+/8")
        );
    }

    @DisplayName("a value is stored as the text of its encoder")
    @MethodSource("texts")
    @ParameterizedTest
    void __text(final Base64StringAttributeConverter converter, final byte[] bytes, final String text) {
        assertThat(converter.convertToDatabaseColumn(bytes)).isEqualTo(text);
        assertThat(converter.convertToEntityAttribute(text)).isEqualTo(bytes);
    }

    @DisplayName("a value is read back, of any length")
    @MethodSource("converters")
    @ParameterizedTest
    void __roundTrip(final Base64StringAttributeConverter converter) {
        for (int length = 0; length < 128; length++) {
            final var value = new byte[length];
            ThreadLocalRandom.current().nextBytes(value);
            final var column = converter.convertToDatabaseColumn(value);
            assertThat(converter.convertToEntityAttribute(column)).isEqualTo(value);
        }
    }

    @DisplayName("a converter reads its text with or without the padding")
    @Test
    void __padding() {
        final var bytes = new byte[]{(byte) 0xFB, (byte) 0xFF};
        assertThat(new Base64StringAttributeConverter.OfBasic().convertToEntityAttribute("+/8")).isEqualTo(bytes);
        assertThat(new Base64StringAttributeConverter.OfBasicWithoutPadding().convertToEntityAttribute("+/8="))
                .isEqualTo(bytes);
        assertThat(new Base64StringAttributeConverter.OfUrl().convertToEntityAttribute("-_8")).isEqualTo(bytes);
        assertThat(new Base64StringAttributeConverter.OfUrlWithoutPadding().convertToEntityAttribute("-_8="))
                .isEqualTo(bytes);
    }

    @DisplayName("the MIME converters write lines of 76 characters, and the MIME decoder reads them back")
    @Test
    void __mime() {
        final var value = new byte[100];
        ThreadLocalRandom.current().nextBytes(value);
        final var converter = new Base64StringAttributeConverter.OfMime();
        final var column = converter.convertToDatabaseColumn(value);
        final var lines = column.split("\r\n");
        assertThat(lines).hasSize(2);
        assertThat(lines[0]).hasSize(76);
        assertThat(converter.convertToEntityAttribute(column)).isEqualTo(value);
        // the basic decoder refuses the line separator
        assertThatThrownBy(() -> new Base64StringAttributeConverter.OfBasic().convertToEntityAttribute(column))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("a text out of the alphabet is refused")
    @Test
    void __alphabet() {
        assertThatThrownBy(() -> new Base64StringAttributeConverter.OfBasic().convertToEntityAttribute("-_8="))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Base64StringAttributeConverter.OfUrl().convertToEntityAttribute("+/8="))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @MethodSource("converters")
    @ParameterizedTest
    void __null(final Base64StringAttributeConverter converter) {
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }
}
