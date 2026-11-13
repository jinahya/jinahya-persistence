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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.HexFormat;
import java.util.Locale;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link UuidAttributeConverter}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("UuidAttributeConverter")
class UuidAttributeConverter_Test {

    // a version 7 UUID whose timestamp is 0x017F22E279B0, as in RFC 9562 Appendix A.6
    private static final UUID V7 = UUID.fromString("017f22e2-79b0-7cc3-98c4-dc0c0c07398f");

    @DisplayName("OfString")
    @Nested
    class OfString_Test {

        private final UuidAttributeConverter.OfString converter = new UuidAttributeConverter.OfString();

        @DisplayName("a UUID is stored as its text, in lower case, and read back")
        @Test
        void __roundTrip() {
            final var value = UUID.randomUUID();
            final var column = converter.convertToDatabaseColumn(value);
            assertThat(column).isEqualTo(value.toString()).isLowerCase();
            assertThat(converter.convertToEntityAttribute(column)).isEqualTo(value);
        }

        @DisplayName("the text of a UUID reads back in either case, as SQL Server writes it in upper case")
        @ParameterizedTest
        @ValueSource(strings = {"6F9619FF-8B86-D011-B42D-00C04FC964FF", "6f9619ff-8b86-d011-b42d-00c04fc964ff"})
        void __eitherCase(final String text) {
            final var value = converter.convertToEntityAttribute(text);
            assertThat(value).isEqualTo(UUID.fromString(text.toLowerCase(Locale.ROOT)));
            assertThat(converter.convertToDatabaseColumn(value)).isEqualToIgnoringCase(text);
        }

        @DisplayName("a version 7 UUID is stored as its text")
        @Test
        void __v7() {
            assertThat(V7.version()).isEqualTo(7);
            assertThat(converter.convertToDatabaseColumn(V7)).isEqualTo("017f22e2-79b0-7cc3-98c4-dc0c0c07398f");
            assertThat(converter.convertToEntityAttribute(converter.convertToDatabaseColumn(V7)))
                    .isEqualTo(V7)
                    .satisfies(v -> assertThat(v.version()).isEqualTo(7));
        }

        @DisplayName("the texts of version 7 UUIDs sort by their timestamps")
        @Test
        void __v7Order() {
            final var earlier = new UUID(0x0000_0000_0001_7000L, 0x8000_0000_0000_0000L);
            final var later = new UUID(0x8000_0000_0000_7000L, 0x8000_0000_0000_0000L);
            assertThat(converter.convertToDatabaseColumn(earlier))
                    .isLessThan(converter.convertToDatabaseColumn(later));
        }

        @Test
        void __null() {
            assertThat(converter.convertToDatabaseColumn(null)).isNull();
            assertThat(converter.convertToEntityAttribute(null)).isNull();
        }

        @DisplayName("a column which is not the text of a UUID is refused")
        @ParameterizedTest
        @ValueSource(strings = {"", "not-a-uuid", "6F9619FF8B86D011B42D00C04FC964FF"})
        void __notUuid(final String text) {
            assertThatThrownBy(() -> converter.convertToEntityAttribute(text))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("OfBytes")
    @Nested
    class OfBytes_Test {

        private final UuidAttributeConverter.OfBytes converter = new UuidAttributeConverter.OfBytes();

        @DisplayName("a UUID is stored as its 16 bytes, in the order of its text, and read back")
        @Test
        void __roundTrip() {
            final var value = UUID.randomUUID();
            final var column = converter.convertToDatabaseColumn(value);
            assertThat(column).hasSize(16);
            assertThat(HexFormat.of().formatHex(column)).isEqualTo(value.toString().replace("-", ""));
            assertThat(converter.convertToEntityAttribute(column)).isEqualTo(value);
        }

        @DisplayName("a version 7 UUID is stored as its bytes, its timestamp first")
        @Test
        void __v7() {
            final var column = converter.convertToDatabaseColumn(V7);
            assertThat(HexFormat.of().formatHex(column)).isEqualTo("017f22e279b07cc398c4dc0c0c07398f");
            assertThat(converter.convertToEntityAttribute(column)).isEqualTo(V7);
        }

        @DisplayName("the bytes of version 7 UUIDs, compared as unsigned, sort by their timestamps")
        @Test
        void __v7Order() {
            final var earlier = new UUID(0x0000_0000_0001_7000L, 0x8000_0000_0000_0000L);
            final var later = new UUID(0x8000_0000_0000_7000L, 0x8000_0000_0000_0000L);
            assertThat(Arrays.compareUnsigned(converter.convertToDatabaseColumn(earlier),
                                              converter.convertToDatabaseColumn(later)))
                    .isNegative();
        }

        @Test
        void __null() {
            assertThat(converter.convertToDatabaseColumn(null)).isNull();
            assertThat(converter.convertToEntityAttribute(null)).isNull();
        }

        @DisplayName("a column which is not 16 bytes long is refused")
        @ParameterizedTest
        @ValueSource(ints = {0, 1, 15, 17, 32})
        void __notSixteen(final int length) {
            assertThatThrownBy(() -> converter.convertToEntityAttribute(new byte[length]))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("OfHexString")
    @Nested
    class OfHexString_Test {

        private final UuidAttributeConverter.OfHexString converter = new UuidAttributeConverter.OfHexString();

        @DisplayName("a UUID is stored as the 32 digits of its bytes, in lower case, and read back")
        @Test
        void __roundTrip() {
            final var value = UUID.randomUUID();
            final var column = converter.convertToDatabaseColumn(value);
            assertThat(column).hasSize(32).isLowerCase().isEqualTo(value.toString().replace("-", ""));
            assertThat(converter.convertToEntityAttribute(column)).isEqualTo(value);
        }

        @DisplayName("the digits read back in either case")
        @ParameterizedTest
        @ValueSource(strings = {"6F9619FF8B86D011B42D00C04FC964FF", "6f9619ff8b86d011b42d00c04fc964ff"})
        void __eitherCase(final String text) {
            assertThat(converter.convertToEntityAttribute(text))
                    .isEqualTo(UUID.fromString("6f9619ff-8b86-d011-b42d-00c04fc964ff"));
        }

        @DisplayName("a version 7 UUID is stored as its digits, its timestamp first")
        @Test
        void __v7() {
            assertThat(converter.convertToDatabaseColumn(V7)).isEqualTo("017f22e279b07cc398c4dc0c0c07398f");
            assertThat(converter.convertToEntityAttribute("017f22e279b07cc398c4dc0c0c07398f")).isEqualTo(V7);
        }

        @DisplayName("the digits of version 7 UUIDs sort by their timestamps")
        @Test
        void __v7Order() {
            final var earlier = new UUID(0x0000_0000_0001_7000L, 0x8000_0000_0000_0000L);
            final var later = new UUID(0x8000_0000_0000_7000L, 0x8000_0000_0000_0000L);
            assertThat(converter.convertToDatabaseColumn(earlier))
                    .isLessThan(converter.convertToDatabaseColumn(later));
        }

        @Test
        void __null() {
            assertThat(converter.convertToDatabaseColumn(null)).isNull();
            assertThat(converter.convertToEntityAttribute(null)).isNull();
        }

        @DisplayName("a column which is not 32 hexadecimal digits is refused")
        @ParameterizedTest
        @ValueSource(strings = {"", "6f9619ff", "6f9619ff-8b86-d011-b42d-00c04fc964ff",
                "6f9619ff8b86d011b42d00c04fc964ff00", "6f9619ff8b86d011b42d00c04fc964fg"})
        void __notHex(final String text) {
            assertThatThrownBy(() -> converter.convertToEntityAttribute(text))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
