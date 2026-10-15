package com.github.jinahya.persistence.encryption.converter;

import com.github.jinahya.persistence.encryption.converter.ValueCodecs.OfLang;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link ValueCodecs.OfLang}.
 * <p>
 * Each nested class pins the encoding against an independent reference: the encodings are stored data, and a change in
 * one makes every existing value decode differently.
 */
class ValueCodecs_OfLang_Test {

    // ---------------------------------------------------------------------------------------------------------- String

    @DisplayName("String")
    @Nested
    class StringTest {

        @DisplayName("encodeString(String) encodes UTF-8, and decodeString(byte[]) reads it back")
        @ValueSource(strings = {"", "a", "é", "한글", "😀", "\u0000", "mixed é😀"})
        @ParameterizedTest
        void __roundTrip(final String v) {
            assertThat(OfLang.encodeString(v)).isEqualTo(v.getBytes(StandardCharsets.UTF_8));
            assertThat(OfLang.decodeString(OfLang.encodeString(v))).isEqualTo(v);
        }

        @DisplayName("encodeString(String) rejects an unpaired surrogate, without the value in the message")
        @ValueSource(strings = {"\uD800", "\uDFFF", "a\uD800b", "\uDE00\uD83D"})
        @ParameterizedTest
        void __unpairedSurrogate(final String v) {
            assertThatThrownBy(() -> OfLang.encodeString(v))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("unpaired surrogate")
                    .hasMessageNotContaining(v);
        }

        @DisplayName("decodeString(byte[]) rejects bytes which are not well-formed UTF-8")
        @Test
        void __malformed() {
            assertThatThrownBy(() -> OfLang.decodeString(new byte[]{(byte) 0xC3}))       // a truncated sequence
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("not well-formed UTF-8");
            assertThatThrownBy(() -> OfLang.decodeString(new byte[]{'a', (byte) 0xFF}))  // never valid in UTF-8
                    .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> OfLang.decodeString(new byte[]{(byte) 0xED, (byte) 0xA0, (byte) 0x80})) // CESU
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("the methods fail on null")
        @Test
        void __null() {
            assertThatThrownBy(() -> OfLang.encodeString(null)).isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> OfLang.decodeString(null)).isInstanceOf(NullPointerException.class);
        }
    }

    // ------------------------------------------------------------------------------------------------------------ enum

    @DisplayName("enum")
    @Nested
    class EnumTest {

        enum Grade {
            A, B
        }

        @DisplayName("encodeEnum(Enum) encodes the name, not the ordinal, and decodeEnum(byte[], Class) reads it back")
        @Test
        void __roundTrip() {
            for (final var v : Grade.values()) {
                assertThat(OfLang.encodeEnum(v)).isEqualTo(v.name().getBytes(StandardCharsets.UTF_8));
                assertThat(OfLang.decodeEnum(OfLang.encodeEnum(v), Grade.class)).isSameAs(v);
            }
        }

        @DisplayName("decodeEnum(byte[], Class) fails on a name which is not a constant, empty included")
        @Test
        void __notAConstant() {
            assertThatThrownBy(() -> OfLang.decodeEnum(OfLang.encodeString("C"), Grade.class))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> OfLang.decodeEnum(new byte[0], Grade.class))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("the methods fail on null")
        @Test
        void __null() {
            assertThatThrownBy(() -> OfLang.encodeEnum(null)).isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> OfLang.decodeEnum(null, Grade.class)).isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> OfLang.decodeEnum(new byte[]{'A'}, null)).isInstanceOf(NullPointerException.class);
        }
    }

    // --------------------------------------------------------------------------------------------- Byte[], Character[]

    @SuppressWarnings("deprecation")
    @DisplayName("Byte[], Character[]")
    @Nested
    class WrappedTest {

        @DisplayName("encodeBytesWrapped(Byte[]) encodes as byte[] does, and decodeBytesWrapped(byte[]) reads it back")
        @Test
        void __bytes() {
            final var v = new Byte[]{Byte.MIN_VALUE, -1, 0, 1, Byte.MAX_VALUE};
            final var expected = v.clone();
            final var b = OfLang.encodeBytesWrapped(v);
            assertThat(b).isEqualTo(new byte[]{Byte.MIN_VALUE, -1, 0, 1, Byte.MAX_VALUE});
            assertThat(OfLang.decodeBytesWrapped(b)).isEqualTo(v);
            assertThat(v).as("the argument is not modified").isEqualTo(expected);
            assertThat(OfLang.encodeBytesWrapped(new Byte[0])).isEmpty();
            assertThat(OfLang.decodeBytesWrapped(new byte[0])).isEmpty();
        }

        @DisplayName("encodeCharsWrapped(Character[]) encodes as char[] does, and decodeCharsWrapped(byte[]) reads it"
                     + " back")
        @Test
        void __chars() {
            final var p = "jin\u00E4\uD55C\uFFFF\u0080\uD800".toCharArray();
            final var v = new Character[p.length];
            for (int i = 0; i < p.length; i++) {
                v[i] = p[i];
            }
            final var b = OfLang.encodeCharsWrapped(v);
            assertThat(b).isEqualTo(ValueCodecs.OfPrimitive.encodeChars(p));
            assertThat(OfLang.decodeCharsWrapped(b)).isEqualTo(v);
        }

        @DisplayName("a null element is rejected, with its index, rather than unboxed")
        @Test
        void __nullElement() {
            assertThatThrownBy(() -> OfLang.encodeBytesWrapped(new Byte[]{1, null, 3}))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("a null element at 1");
            assertThatThrownBy(() -> OfLang.encodeCharsWrapped(new Character[]{'a', 'b', null}))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("a null element at 2");
        }

        @DisplayName("decodeCharsWrapped(byte[]) rejects an odd number of bytes")
        @Test
        void __odd() {
            assertThatThrownBy(() -> OfLang.decodeCharsWrapped(new byte[]{1, 2, 3}))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("the methods fail on null")
        @Test
        void __null() {
            assertThatThrownBy(() -> OfLang.encodeBytesWrapped(null)).isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> OfLang.decodeBytesWrapped(null)).isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> OfLang.encodeCharsWrapped(null)).isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> OfLang.decodeCharsWrapped(null)).isInstanceOf(NullPointerException.class);
        }
    }
}
