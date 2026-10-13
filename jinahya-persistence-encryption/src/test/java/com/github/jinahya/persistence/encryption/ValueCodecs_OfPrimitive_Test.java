package com.github.jinahya.persistence.encryption;

import com.github.jinahya.persistence.encryption.ValueCodecs.OfPrimitive;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link ValueCodecs.OfPrimitive}.
 * <p>
 * Each nested class pins the encoding against {@link ByteBuffer}, which is big-endian, as an independent reference:
 * the encodings are stored data, and a change in one makes every existing value decode differently.
 */
class ValueCodecs_OfPrimitive_Test {

    /**
     * Returns the bytes the specified writer puts into a big-endian buffer of the specified capacity.
     */
    private static byte[] buffer(final int capacity, final Consumer<? super ByteBuffer> writer) {
        final var buffer = ByteBuffer.allocate(capacity);
        writer.accept(buffer);
        assert !buffer.hasRemaining() : "the expected format does not fill the capacity";
        return buffer.array();
    }

    // ------------------------------------------------------------------------------------------------------------ byte

    @DisplayName("byte")
    @Nested
    class ByteTest {

        @DisplayName("encodeByte(byte) encodes the byte itself, and decodeByte(byte[]) reads it back")
        @ValueSource(bytes = {Byte.MIN_VALUE, -1, 0, 1, Byte.MAX_VALUE})
        @ParameterizedTest
        void __roundTrip(final byte v) {
            assertThat(OfPrimitive.encodeByte(v)).containsExactly(v);
            assertThat(OfPrimitive.decodeByte(OfPrimitive.encodeByte(v))).isEqualTo(v);
        }

        @DisplayName("encodeByte(byte[], int, byte) writes at the index, and returns the next one")
        @Test
        void __index() {
            final var b = new byte[]{9, 9, 9};
            assertThat(OfPrimitive.encodeByte(b, 1, (byte) -2)).isEqualTo(2);
            assertThat(b).containsExactly(9, -2, 9);
            assertThat(OfPrimitive.decodeByte(b, 1)).isEqualTo((byte) -2);
        }

        @DisplayName("decodeByte(byte[]) fails on an array shorter than one byte(s)")
        @ValueSource(ints = {0})
        @ParameterizedTest
        void __tooShort(final int length) {
            assertThatThrownBy(() -> OfPrimitive.decodeByte(new byte[length]))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("decodeByte(byte[]) reads the beginning of a longer array, and leaves the rest")
        @Test
        void __longer() {
            assertThat(OfPrimitive.decodeByte(new byte[]{-2, 3})).isEqualTo((byte) -2);
        }

        @DisplayName("the index-based methods fail outside the array")
        @ValueSource(ints = {-1, 1})
        @ParameterizedTest
        void __outOfBounds(final int i) {
            final var b = new byte[1];
            assertThatThrownBy(() -> OfPrimitive.encodeByte(b, i, (byte) 0))
                    .isInstanceOf(IndexOutOfBoundsException.class);
            assertThatThrownBy(() -> OfPrimitive.decodeByte(b, i))
                    .isInstanceOf(IndexOutOfBoundsException.class);
        }
    }

    @DisplayName("byte[]")
    @Nested
    class BytesTest {

        private static byte[] sample() {
            return new byte[]{Byte.MIN_VALUE, -1, 0, 1, Byte.MAX_VALUE};
        }

        @DisplayName("encodeBytes(byte[]) encodes the elements one after another, with no length, and"
                     + " decodeBytes(byte[]) reads them back")
        @Test
        void __roundTrip() {
            final var v = sample();
            assertThat(OfPrimitive.encodeBytes(v)).isEqualTo(buffer(v.length * Byte.BYTES, bb -> {
                for (final var e : v) {
                    bb.put(e);
                }
            }));
            assertThat(OfPrimitive.decodeBytes(OfPrimitive.encodeBytes(v))).isEqualTo(v);
        }

        @DisplayName("an empty array encodes to no byte, and no byte decodes to an empty array")
        @Test
        void __empty() {
            assertThat(OfPrimitive.encodeBytes(new byte[0])).isEmpty();
            assertThat(OfPrimitive.decodeBytes(new byte[0])).isEmpty();
        }

        @DisplayName("the index-based methods write at, and read from, the index, and return the next one")
        @Test
        void __index() {
            final var v = sample();
            final var size = v.length * Byte.BYTES;
            final var b = new byte[size + 2];
            b[0] = 9;
            b[b.length - 1] = 9;
            assertThat(OfPrimitive.encodeBytes(b, 1, v)).isEqualTo(1 + size);
            assertThat(b[0]).isEqualTo((byte) 9);
            assertThat(b[b.length - 1]).isEqualTo((byte) 9);
            assertThat(Arrays.copyOfRange(b, 1, 1 + size)).isEqualTo(OfPrimitive.encodeBytes(v));
            final var dst = new byte[v.length];
            assertThat(OfPrimitive.decodeBytes(b, 1, dst)).isEqualTo(1 + size);
            assertThat(OfPrimitive.encodeBytes(dst)).isEqualTo(OfPrimitive.encodeBytes(v));
        }

        @DisplayName("the index-based methods fail when the values do not fit")
        @ValueSource(ints = {-1, 1})
        @ParameterizedTest
        void __outOfBounds(final int i) {
            final var v = sample();
            final var b = new byte[v.length * Byte.BYTES];
            assertThatThrownBy(() -> OfPrimitive.encodeBytes(b, i, v))
                    .isInstanceOf(IndexOutOfBoundsException.class);
            assertThatThrownBy(() -> OfPrimitive.decodeBytes(b, i, new byte[v.length]))
                    .isInstanceOf(IndexOutOfBoundsException.class);
        }
    }

    // ----------------------------------------------------------------------------------------------------------- short

    @DisplayName("short")
    @Nested
    class ShortTest {

        @DisplayName("encodeShort(short) encodes two big-endian bytes, and decodeShort(byte[]) reads them back")
        @ValueSource(shorts = {Short.MIN_VALUE, -1, 0, 1, 0x0102, Short.MAX_VALUE})
        @ParameterizedTest
        void __roundTrip(final short v) {
            assertThat(OfPrimitive.encodeShort(v)).isEqualTo(buffer(Short.BYTES, b -> b.putShort(v)));
            assertThat(OfPrimitive.decodeShort(OfPrimitive.encodeShort(v))).isEqualTo(v);
        }

        @DisplayName("encodeShort(byte[], int, short) writes at the index, and returns the next one")
        @Test
        void __index() {
            final var b = new byte[]{9, 9, 9, 9};
            assertThat(OfPrimitive.encodeShort(b, 1, (short) 0x0102)).isEqualTo(3);
            assertThat(b).containsExactly(9, 1, 2, 9);
            assertThat(OfPrimitive.decodeShort(b, 1)).isEqualTo((short) 0x0102);
        }

        @DisplayName("decodeShort(byte[]) fails on an array shorter than two byte(s)")
        @ValueSource(ints = {0, 1})
        @ParameterizedTest
        void __tooShort(final int length) {
            assertThatThrownBy(() -> OfPrimitive.decodeShort(new byte[length]))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("decodeShort(byte[]) reads the beginning of a longer array, and leaves the rest")
        @Test
        void __longer() {
            assertThat(OfPrimitive.decodeShort(new byte[]{1, 2, 3})).isEqualTo((short) 0x0102);
        }

        @DisplayName("the index-based methods fail when the value does not fit")
        @ValueSource(ints = {-1, 1, 2})
        @ParameterizedTest
        void __outOfBounds(final int i) {
            final var b = new byte[Short.BYTES];
            assertThatThrownBy(() -> OfPrimitive.encodeShort(b, i, (short) 0))
                    .isInstanceOf(IndexOutOfBoundsException.class);
            assertThatThrownBy(() -> OfPrimitive.decodeShort(b, i))
                    .isInstanceOf(IndexOutOfBoundsException.class);
        }
    }

    @DisplayName("short[]")
    @Nested
    class ShortsTest {

        private static short[] sample() {
            return new short[]{Short.MIN_VALUE, -1, 0, 1, 0x0102, Short.MAX_VALUE};
        }

        @DisplayName("encodeShorts(short[]) encodes the elements one after another, with no length, and"
                     + " decodeShorts(byte[]) reads them back")
        @Test
        void __roundTrip() {
            final var v = sample();
            assertThat(OfPrimitive.encodeShorts(v)).isEqualTo(buffer(v.length * Short.BYTES, bb -> {
                for (final var e : v) {
                    bb.putShort(e);
                }
            }));
            assertThat(OfPrimitive.decodeShorts(OfPrimitive.encodeShorts(v))).isEqualTo(v);
        }

        @DisplayName("an empty array encodes to no byte, and no byte decodes to an empty array")
        @Test
        void __empty() {
            assertThat(OfPrimitive.encodeShorts(new short[0])).isEmpty();
            assertThat(OfPrimitive.decodeShorts(new byte[0])).isEmpty();
        }

        @DisplayName("the index-based methods write at, and read from, the index, and return the next one")
        @Test
        void __index() {
            final var v = sample();
            final var size = v.length * Short.BYTES;
            final var b = new byte[size + 2];
            b[0] = 9;
            b[b.length - 1] = 9;
            assertThat(OfPrimitive.encodeShorts(b, 1, v)).isEqualTo(1 + size);
            assertThat(b[0]).isEqualTo((byte) 9);
            assertThat(b[b.length - 1]).isEqualTo((byte) 9);
            assertThat(Arrays.copyOfRange(b, 1, 1 + size)).isEqualTo(OfPrimitive.encodeShorts(v));
            final var dst = new short[v.length];
            assertThat(OfPrimitive.decodeShorts(b, 1, dst)).isEqualTo(1 + size);
            assertThat(OfPrimitive.encodeShorts(dst)).isEqualTo(OfPrimitive.encodeShorts(v));
        }

        @DisplayName("decodeShorts(byte[]) fails on a length which is not a multiple of the width")
        @Test
        void __notMultiple() {
            assertThatThrownBy(() -> OfPrimitive.decodeShorts(new byte[Short.BYTES + 1]))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("not a multiple of " + Short.BYTES);
        }

        @DisplayName("the index-based methods fail when the values do not fit")
        @ValueSource(ints = {-1, 1})
        @ParameterizedTest
        void __outOfBounds(final int i) {
            final var v = sample();
            final var b = new byte[v.length * Short.BYTES];
            assertThatThrownBy(() -> OfPrimitive.encodeShorts(b, i, v))
                    .isInstanceOf(IndexOutOfBoundsException.class);
            assertThatThrownBy(() -> OfPrimitive.decodeShorts(b, i, new short[v.length]))
                    .isInstanceOf(IndexOutOfBoundsException.class);
        }
    }

    // ------------------------------------------------------------------------------------------------------------- int

    @DisplayName("int")
    @Nested
    class IntTest {

        @DisplayName("encodeInt(int) encodes four big-endian bytes, and decodeInt(byte[]) reads them back")
        @ValueSource(ints = {Integer.MIN_VALUE, -1, 0, 1, 0x01020304, Integer.MAX_VALUE})
        @ParameterizedTest
        void __roundTrip(final int v) {
            assertThat(OfPrimitive.encodeInt(v)).isEqualTo(buffer(Integer.BYTES, b -> b.putInt(v)));
            assertThat(OfPrimitive.decodeInt(OfPrimitive.encodeInt(v))).isEqualTo(v);
        }

        @DisplayName("encodeInt(byte[], int, int) writes at the index, and returns the next one")
        @Test
        void __index() {
            final var b = new byte[]{9, 9, 9, 9, 9, 9};
            assertThat(OfPrimitive.encodeInt(b, 1, 0x01020304)).isEqualTo(5);
            assertThat(b).containsExactly(9, 1, 2, 3, 4, 9);
            assertThat(OfPrimitive.decodeInt(b, 1)).isEqualTo(0x01020304);
        }

        @DisplayName("decodeInt(byte[]) fails on an array shorter than four byte(s)")
        @ValueSource(ints = {0, 3})
        @ParameterizedTest
        void __tooShort(final int length) {
            assertThatThrownBy(() -> OfPrimitive.decodeInt(new byte[length]))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("decodeInt(byte[]) reads the beginning of a longer array, and leaves the rest")
        @Test
        void __longer() {
            assertThat(OfPrimitive.decodeInt(new byte[]{1, 2, 3, 4, 5})).isEqualTo(0x01020304);
        }

        @DisplayName("the index-based methods fail when the value does not fit")
        @ValueSource(ints = {-1, 1, 4})
        @ParameterizedTest
        void __outOfBounds(final int i) {
            final var b = new byte[Integer.BYTES];
            assertThatThrownBy(() -> OfPrimitive.encodeInt(b, i, 0))
                    .isInstanceOf(IndexOutOfBoundsException.class);
            assertThatThrownBy(() -> OfPrimitive.decodeInt(b, i))
                    .isInstanceOf(IndexOutOfBoundsException.class);
        }
    }

    @DisplayName("int[]")
    @Nested
    class IntsTest {

        private static int[] sample() {
            return new int[]{Integer.MIN_VALUE, -1, 0, 1, 0x01020304, Integer.MAX_VALUE};
        }

        @DisplayName("encodeInts(int[]) encodes the elements one after another, with no length, and"
                     + " decodeInts(byte[]) reads them back")
        @Test
        void __roundTrip() {
            final var v = sample();
            assertThat(OfPrimitive.encodeInts(v)).isEqualTo(buffer(v.length * Integer.BYTES, bb -> {
                for (final var e : v) {
                    bb.putInt(e);
                }
            }));
            assertThat(OfPrimitive.decodeInts(OfPrimitive.encodeInts(v))).isEqualTo(v);
        }

        @DisplayName("an empty array encodes to no byte, and no byte decodes to an empty array")
        @Test
        void __empty() {
            assertThat(OfPrimitive.encodeInts(new int[0])).isEmpty();
            assertThat(OfPrimitive.decodeInts(new byte[0])).isEmpty();
        }

        @DisplayName("the index-based methods write at, and read from, the index, and return the next one")
        @Test
        void __index() {
            final var v = sample();
            final var size = v.length * Integer.BYTES;
            final var b = new byte[size + 2];
            b[0] = 9;
            b[b.length - 1] = 9;
            assertThat(OfPrimitive.encodeInts(b, 1, v)).isEqualTo(1 + size);
            assertThat(b[0]).isEqualTo((byte) 9);
            assertThat(b[b.length - 1]).isEqualTo((byte) 9);
            assertThat(Arrays.copyOfRange(b, 1, 1 + size)).isEqualTo(OfPrimitive.encodeInts(v));
            final var dst = new int[v.length];
            assertThat(OfPrimitive.decodeInts(b, 1, dst)).isEqualTo(1 + size);
            assertThat(OfPrimitive.encodeInts(dst)).isEqualTo(OfPrimitive.encodeInts(v));
        }

        @DisplayName("decodeInts(byte[]) fails on a length which is not a multiple of the width")
        @Test
        void __notMultiple() {
            assertThatThrownBy(() -> OfPrimitive.decodeInts(new byte[Integer.BYTES + 1]))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("not a multiple of " + Integer.BYTES);
        }

        @DisplayName("the index-based methods fail when the values do not fit")
        @ValueSource(ints = {-1, 1})
        @ParameterizedTest
        void __outOfBounds(final int i) {
            final var v = sample();
            final var b = new byte[v.length * Integer.BYTES];
            assertThatThrownBy(() -> OfPrimitive.encodeInts(b, i, v))
                    .isInstanceOf(IndexOutOfBoundsException.class);
            assertThatThrownBy(() -> OfPrimitive.decodeInts(b, i, new int[v.length]))
                    .isInstanceOf(IndexOutOfBoundsException.class);
        }
    }

    // ------------------------------------------------------------------------------------------------------------ long

    @DisplayName("long")
    @Nested
    class LongTest {

        @DisplayName("encodeLong(long) encodes eight big-endian bytes, and decodeLong(byte[]) reads them back")
        @ValueSource(longs = {Long.MIN_VALUE, -1L, 0L, 1L, 0x0102030405060708L, Long.MAX_VALUE})
        @ParameterizedTest
        void __roundTrip(final long v) {
            assertThat(OfPrimitive.encodeLong(v)).isEqualTo(buffer(Long.BYTES, b -> b.putLong(v)));
            assertThat(OfPrimitive.decodeLong(OfPrimitive.encodeLong(v))).isEqualTo(v);
        }

        @DisplayName("encodeLong(byte[], int, long) writes at the index, and returns the next one")
        @Test
        void __index() {
            final var b = new byte[]{9, 9, 9, 9, 9, 9, 9, 9, 9, 9};
            assertThat(OfPrimitive.encodeLong(b, 1, 0x0102030405060708L)).isEqualTo(9);
            assertThat(b).containsExactly(9, 1, 2, 3, 4, 5, 6, 7, 8, 9);
            assertThat(OfPrimitive.decodeLong(b, 1)).isEqualTo(0x0102030405060708L);
        }

        @DisplayName("decodeLong(byte[]) fails on an array shorter than eight byte(s)")
        @ValueSource(ints = {0, 7})
        @ParameterizedTest
        void __tooShort(final int length) {
            assertThatThrownBy(() -> OfPrimitive.decodeLong(new byte[length]))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("decodeLong(byte[]) reads the beginning of a longer array, and leaves the rest")
        @Test
        void __longer() {
            assertThat(OfPrimitive.decodeLong(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9})).isEqualTo(0x0102030405060708L);
        }

        @DisplayName("the index-based methods fail when the value does not fit")
        @ValueSource(ints = {-1, 1, 8})
        @ParameterizedTest
        void __outOfBounds(final int i) {
            final var b = new byte[Long.BYTES];
            assertThatThrownBy(() -> OfPrimitive.encodeLong(b, i, 0L))
                    .isInstanceOf(IndexOutOfBoundsException.class);
            assertThatThrownBy(() -> OfPrimitive.decodeLong(b, i))
                    .isInstanceOf(IndexOutOfBoundsException.class);
        }
    }

    @DisplayName("long[]")
    @Nested
    class LongsTest {

        private static long[] sample() {
            return new long[]{Long.MIN_VALUE, -1L, 0L, 1L, 0x0102030405060708L, Long.MAX_VALUE};
        }

        @DisplayName("encodeLongs(long[]) encodes the elements one after another, with no length, and"
                     + " decodeLongs(byte[]) reads them back")
        @Test
        void __roundTrip() {
            final var v = sample();
            assertThat(OfPrimitive.encodeLongs(v)).isEqualTo(buffer(v.length * Long.BYTES, bb -> {
                for (final var e : v) {
                    bb.putLong(e);
                }
            }));
            assertThat(OfPrimitive.decodeLongs(OfPrimitive.encodeLongs(v))).isEqualTo(v);
        }

        @DisplayName("an empty array encodes to no byte, and no byte decodes to an empty array")
        @Test
        void __empty() {
            assertThat(OfPrimitive.encodeLongs(new long[0])).isEmpty();
            assertThat(OfPrimitive.decodeLongs(new byte[0])).isEmpty();
        }

        @DisplayName("the index-based methods write at, and read from, the index, and return the next one")
        @Test
        void __index() {
            final var v = sample();
            final var size = v.length * Long.BYTES;
            final var b = new byte[size + 2];
            b[0] = 9;
            b[b.length - 1] = 9;
            assertThat(OfPrimitive.encodeLongs(b, 1, v)).isEqualTo(1 + size);
            assertThat(b[0]).isEqualTo((byte) 9);
            assertThat(b[b.length - 1]).isEqualTo((byte) 9);
            assertThat(Arrays.copyOfRange(b, 1, 1 + size)).isEqualTo(OfPrimitive.encodeLongs(v));
            final var dst = new long[v.length];
            assertThat(OfPrimitive.decodeLongs(b, 1, dst)).isEqualTo(1 + size);
            assertThat(OfPrimitive.encodeLongs(dst)).isEqualTo(OfPrimitive.encodeLongs(v));
        }

        @DisplayName("decodeLongs(byte[]) fails on a length which is not a multiple of the width")
        @Test
        void __notMultiple() {
            assertThatThrownBy(() -> OfPrimitive.decodeLongs(new byte[Long.BYTES + 1]))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("not a multiple of " + Long.BYTES);
        }

        @DisplayName("the index-based methods fail when the values do not fit")
        @ValueSource(ints = {-1, 1})
        @ParameterizedTest
        void __outOfBounds(final int i) {
            final var v = sample();
            final var b = new byte[v.length * Long.BYTES];
            assertThatThrownBy(() -> OfPrimitive.encodeLongs(b, i, v))
                    .isInstanceOf(IndexOutOfBoundsException.class);
            assertThatThrownBy(() -> OfPrimitive.decodeLongs(b, i, new long[v.length]))
                    .isInstanceOf(IndexOutOfBoundsException.class);
        }
    }

    // ------------------------------------------------------------------------------------------------------------ char

    @DisplayName("char")
    @Nested
    class CharTest {

        @DisplayName("encodeChar(char) encodes the UTF-16 code unit as two big-endian bytes, and"
                     + " decodeChar(byte[]) reads them back")
        @ValueSource(chars = {Character.MIN_VALUE, 'A', '\u00FF', '\u0102', '\uD800', '\uDFFF', Character.MAX_VALUE})
        @ParameterizedTest
        void __roundTrip(final char v) {
            assertThat(OfPrimitive.encodeChar(v)).isEqualTo(buffer(Character.BYTES, b -> b.putChar(v)));
            assertThat(OfPrimitive.decodeChar(OfPrimitive.encodeChar(v))).isEqualTo(v);
        }

        @DisplayName("encodeChar(byte[], int, char) writes at the index, and returns the next one")
        @Test
        void __index() {
            final var b = new byte[]{9, 9, 9, 9};
            assertThat(OfPrimitive.encodeChar(b, 1, '\u0102')).isEqualTo(3);
            assertThat(b).containsExactly(9, 1, 2, 9);
            assertThat(OfPrimitive.decodeChar(b, 1)).isEqualTo('\u0102');
        }

        @DisplayName("decodeChar(byte[]) fails on an array shorter than two byte(s)")
        @ValueSource(ints = {0, 1})
        @ParameterizedTest
        void __tooShort(final int length) {
            assertThatThrownBy(() -> OfPrimitive.decodeChar(new byte[length]))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("decodeChar(byte[]) reads the beginning of a longer array, and leaves the rest")
        @Test
        void __longer() {
            assertThat(OfPrimitive.decodeChar(new byte[]{1, 2, 3})).isEqualTo('\u0102');
        }

        @DisplayName("the index-based methods fail when the value does not fit")
        @ValueSource(ints = {-1, 1, 2})
        @ParameterizedTest
        void __outOfBounds(final int i) {
            final var b = new byte[Character.BYTES];
            assertThatThrownBy(() -> OfPrimitive.encodeChar(b, i, 'A'))
                    .isInstanceOf(IndexOutOfBoundsException.class);
            assertThatThrownBy(() -> OfPrimitive.decodeChar(b, i))
                    .isInstanceOf(IndexOutOfBoundsException.class);
        }
    }

    @DisplayName("char[]")
    @Nested
    class CharsTest {

        private static char[] sample() {
            return new char[]{Character.MIN_VALUE, 'A', '\uD800', '\uDFFF', Character.MAX_VALUE};
        }

        @DisplayName("encodeChars(char[]) encodes the elements one after another, with no length, and"
                     + " decodeChars(byte[]) reads them back")
        @Test
        void __roundTrip() {
            final var v = sample();
            assertThat(OfPrimitive.encodeChars(v)).isEqualTo(buffer(v.length * Character.BYTES, bb -> {
                for (final var e : v) {
                    bb.putChar(e);
                }
            }));
            assertThat(OfPrimitive.decodeChars(OfPrimitive.encodeChars(v))).isEqualTo(v);
        }

        @DisplayName("an empty array encodes to no byte, and no byte decodes to an empty array")
        @Test
        void __empty() {
            assertThat(OfPrimitive.encodeChars(new char[0])).isEmpty();
            assertThat(OfPrimitive.decodeChars(new byte[0])).isEmpty();
        }

        @DisplayName("the index-based methods write at, and read from, the index, and return the next one")
        @Test
        void __index() {
            final var v = sample();
            final var size = v.length * Character.BYTES;
            final var b = new byte[size + 2];
            b[0] = 9;
            b[b.length - 1] = 9;
            assertThat(OfPrimitive.encodeChars(b, 1, v)).isEqualTo(1 + size);
            assertThat(b[0]).isEqualTo((byte) 9);
            assertThat(b[b.length - 1]).isEqualTo((byte) 9);
            assertThat(Arrays.copyOfRange(b, 1, 1 + size)).isEqualTo(OfPrimitive.encodeChars(v));
            final var dst = new char[v.length];
            assertThat(OfPrimitive.decodeChars(b, 1, dst)).isEqualTo(1 + size);
            assertThat(OfPrimitive.encodeChars(dst)).isEqualTo(OfPrimitive.encodeChars(v));
        }

        @DisplayName("decodeChars(byte[]) fails on a length which is not a multiple of the width")
        @Test
        void __notMultiple() {
            assertThatThrownBy(() -> OfPrimitive.decodeChars(new byte[Character.BYTES + 1]))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("not a multiple of " + Character.BYTES);
        }

        @DisplayName("the index-based methods fail when the values do not fit")
        @ValueSource(ints = {-1, 1})
        @ParameterizedTest
        void __outOfBounds(final int i) {
            final var v = sample();
            final var b = new byte[v.length * Character.BYTES];
            assertThatThrownBy(() -> OfPrimitive.encodeChars(b, i, v))
                    .isInstanceOf(IndexOutOfBoundsException.class);
            assertThatThrownBy(() -> OfPrimitive.decodeChars(b, i, new char[v.length]))
                    .isInstanceOf(IndexOutOfBoundsException.class);
        }
    }

    // ----------------------------------------------------------------------------------------------------------- float

    @DisplayName("float")
    @Nested
    class FloatTest {

        private static Stream<Float> values() {
            return Stream.of(
                    0.0f, -0.0f, 1.0f, -1.0f, Float.MIN_VALUE, Float.MIN_NORMAL, Float.MAX_VALUE,
                    Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NaN,
                    Float.intBitsToFloat(0x7FC00001), // a quiet NaN with a payload
                    Float.intBitsToFloat(0xFF800001)  // a signalling NaN, with the sign bit set
            );
        }

        @DisplayName("encodeFloat(float) encodes the raw IEEE 754 bits as four big-endian bytes, and"
                     + " decodeFloat(byte[]) reads back the same bits")
        @MethodSource("values")
        @ParameterizedTest
        void __roundTrip(final float v) {
            assertThat(OfPrimitive.encodeFloat(v))
                    .isEqualTo(buffer(Float.BYTES, b -> b.putInt(Float.floatToRawIntBits(v))));
            assertThat(Float.floatToRawIntBits(OfPrimitive.decodeFloat(OfPrimitive.encodeFloat(v))))
                    .isEqualTo(Float.floatToRawIntBits(v));
        }

        @DisplayName("encodeFloat(byte[], int, float) writes at the index, and returns the next one")
        @Test
        void __index() {
            final var b = new byte[]{9, 9, 9, 9, 9, 9};
            assertThat(OfPrimitive.encodeFloat(b, 1, Float.intBitsToFloat(0x01020304))).isEqualTo(5);
            assertThat(b).containsExactly(9, 1, 2, 3, 4, 9);
            assertThat(Float.floatToRawIntBits(OfPrimitive.decodeFloat(b, 1))).isEqualTo(0x01020304);
        }

        @DisplayName("decodeFloat(byte[]) fails on an array shorter than four byte(s)")
        @ValueSource(ints = {0, 3})
        @ParameterizedTest
        void __tooShort(final int length) {
            assertThatThrownBy(() -> OfPrimitive.decodeFloat(new byte[length]))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("decodeFloat(byte[]) reads the beginning of a longer array, and leaves the rest")
        @Test
        void __longer() {
            assertThat(Float.floatToRawIntBits(OfPrimitive.decodeFloat(new byte[]{1, 2, 3, 4, 5})))
                    .isEqualTo(0x01020304);
        }

        @DisplayName("the index-based methods fail when the value does not fit")
        @ValueSource(ints = {-1, 1, 4})
        @ParameterizedTest
        void __outOfBounds(final int i) {
            final var b = new byte[Float.BYTES];
            assertThatThrownBy(() -> OfPrimitive.encodeFloat(b, i, 0.0f))
                    .isInstanceOf(IndexOutOfBoundsException.class);
            assertThatThrownBy(() -> OfPrimitive.decodeFloat(b, i))
                    .isInstanceOf(IndexOutOfBoundsException.class);
        }
    }

    @DisplayName("float[]")
    @Nested
    class FloatsTest {

        private static float[] sample() {
            return new float[]{
                    -0.0f, Float.MIN_VALUE, Float.NaN, Float.intBitsToFloat(0x7FC00001), Float.NEGATIVE_INFINITY
            };
        }

        @DisplayName("encodeFloats(float[]) encodes the elements one after another, with no length, and"
                     + " decodeFloats(byte[]) reads them back")
        @Test
        void __roundTrip() {
            final var v = sample();
            assertThat(OfPrimitive.encodeFloats(v)).isEqualTo(buffer(v.length * Float.BYTES, bb -> {
                for (final var e : v) {
                    bb.putInt(Float.floatToRawIntBits(e));
                }
            }));
            final var decoded = OfPrimitive.decodeFloats(OfPrimitive.encodeFloats(v));
            assertThat(decoded).hasSameSizeAs(v);
            for (int j = 0; j < v.length; j++) {
                assertThat(Float.floatToRawIntBits(decoded[j])).isEqualTo(Float.floatToRawIntBits(v[j]));
            }
        }

        @DisplayName("an empty array encodes to no byte, and no byte decodes to an empty array")
        @Test
        void __empty() {
            assertThat(OfPrimitive.encodeFloats(new float[0])).isEmpty();
            assertThat(OfPrimitive.decodeFloats(new byte[0])).isEmpty();
        }

        @DisplayName("the index-based methods write at, and read from, the index, and return the next one")
        @Test
        void __index() {
            final var v = sample();
            final var size = v.length * Float.BYTES;
            final var b = new byte[size + 2];
            b[0] = 9;
            b[b.length - 1] = 9;
            assertThat(OfPrimitive.encodeFloats(b, 1, v)).isEqualTo(1 + size);
            assertThat(b[0]).isEqualTo((byte) 9);
            assertThat(b[b.length - 1]).isEqualTo((byte) 9);
            assertThat(Arrays.copyOfRange(b, 1, 1 + size)).isEqualTo(OfPrimitive.encodeFloats(v));
            final var dst = new float[v.length];
            assertThat(OfPrimitive.decodeFloats(b, 1, dst)).isEqualTo(1 + size);
            assertThat(OfPrimitive.encodeFloats(dst)).isEqualTo(OfPrimitive.encodeFloats(v));
        }

        @DisplayName("decodeFloats(byte[]) fails on a length which is not a multiple of the width")
        @Test
        void __notMultiple() {
            assertThatThrownBy(() -> OfPrimitive.decodeFloats(new byte[Float.BYTES + 1]))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("not a multiple of " + Float.BYTES);
        }

        @DisplayName("the index-based methods fail when the values do not fit")
        @ValueSource(ints = {-1, 1})
        @ParameterizedTest
        void __outOfBounds(final int i) {
            final var v = sample();
            final var b = new byte[v.length * Float.BYTES];
            assertThatThrownBy(() -> OfPrimitive.encodeFloats(b, i, v))
                    .isInstanceOf(IndexOutOfBoundsException.class);
            assertThatThrownBy(() -> OfPrimitive.decodeFloats(b, i, new float[v.length]))
                    .isInstanceOf(IndexOutOfBoundsException.class);
        }
    }

    // ---------------------------------------------------------------------------------------------------------- double

    @DisplayName("double")
    @Nested
    class DoubleTest {

        private static Stream<Double> values() {
            return Stream.of(
                    0.0d, -0.0d, 1.0d, -1.0d, Double.MIN_VALUE, Double.MIN_NORMAL, Double.MAX_VALUE,
                    Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NaN,
                    Double.longBitsToDouble(0x7FF8000000000001L), // a quiet NaN with a payload
                    Double.longBitsToDouble(0xFFF0000000000001L)  // a signalling NaN, with the sign bit set
            );
        }

        @DisplayName("encodeDouble(double) encodes the raw IEEE 754 bits as eight big-endian bytes, and"
                     + " decodeDouble(byte[]) reads back the same bits")
        @MethodSource("values")
        @ParameterizedTest
        void __roundTrip(final double v) {
            assertThat(OfPrimitive.encodeDouble(v))
                    .isEqualTo(buffer(Double.BYTES, b -> b.putLong(Double.doubleToRawLongBits(v))));
            assertThat(Double.doubleToRawLongBits(OfPrimitive.decodeDouble(OfPrimitive.encodeDouble(v))))
                    .isEqualTo(Double.doubleToRawLongBits(v));
        }

        @DisplayName("encodeDouble(byte[], int, double) writes at the index, and returns the next one")
        @Test
        void __index() {
            final var b = new byte[]{9, 9, 9, 9, 9, 9, 9, 9, 9, 9};
            assertThat(OfPrimitive.encodeDouble(b, 1, Double.longBitsToDouble(0x0102030405060708L))).isEqualTo(9);
            assertThat(b).containsExactly(9, 1, 2, 3, 4, 5, 6, 7, 8, 9);
            assertThat(Double.doubleToRawLongBits(OfPrimitive.decodeDouble(b, 1))).isEqualTo(0x0102030405060708L);
        }

        @DisplayName("decodeDouble(byte[]) fails on an array shorter than eight byte(s)")
        @ValueSource(ints = {0, 7})
        @ParameterizedTest
        void __tooShort(final int length) {
            assertThatThrownBy(() -> OfPrimitive.decodeDouble(new byte[length]))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("decodeDouble(byte[]) reads the beginning of a longer array, and leaves the rest")
        @Test
        void __longer() {
            assertThat(Double.doubleToRawLongBits(
                    OfPrimitive.decodeDouble(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9})))
                    .isEqualTo(0x0102030405060708L);
        }

        @DisplayName("the index-based methods fail when the value does not fit")
        @ValueSource(ints = {-1, 1, 8})
        @ParameterizedTest
        void __outOfBounds(final int i) {
            final var b = new byte[Double.BYTES];
            assertThatThrownBy(() -> OfPrimitive.encodeDouble(b, i, 0.0d))
                    .isInstanceOf(IndexOutOfBoundsException.class);
            assertThatThrownBy(() -> OfPrimitive.decodeDouble(b, i))
                    .isInstanceOf(IndexOutOfBoundsException.class);
        }
    }

    @DisplayName("double[]")
    @Nested
    class DoublesTest {

        private static double[] sample() {
            return new double[]{
                    -0.0d, Double.MIN_VALUE, Double.NaN, Double.longBitsToDouble(0x7FF8000000000001L),
                    Double.NEGATIVE_INFINITY
            };
        }

        @DisplayName("encodeDoubles(double[]) encodes the elements one after another, with no length, and"
                     + " decodeDoubles(byte[]) reads them back")
        @Test
        void __roundTrip() {
            final var v = sample();
            assertThat(OfPrimitive.encodeDoubles(v)).isEqualTo(buffer(v.length * Double.BYTES, bb -> {
                for (final var e : v) {
                    bb.putLong(Double.doubleToRawLongBits(e));
                }
            }));
            final var decoded = OfPrimitive.decodeDoubles(OfPrimitive.encodeDoubles(v));
            assertThat(decoded).hasSameSizeAs(v);
            for (int j = 0; j < v.length; j++) {
                assertThat(Double.doubleToRawLongBits(decoded[j])).isEqualTo(Double.doubleToRawLongBits(v[j]));
            }
        }

        @DisplayName("an empty array encodes to no byte, and no byte decodes to an empty array")
        @Test
        void __empty() {
            assertThat(OfPrimitive.encodeDoubles(new double[0])).isEmpty();
            assertThat(OfPrimitive.decodeDoubles(new byte[0])).isEmpty();
        }

        @DisplayName("the index-based methods write at, and read from, the index, and return the next one")
        @Test
        void __index() {
            final var v = sample();
            final var size = v.length * Double.BYTES;
            final var b = new byte[size + 2];
            b[0] = 9;
            b[b.length - 1] = 9;
            assertThat(OfPrimitive.encodeDoubles(b, 1, v)).isEqualTo(1 + size);
            assertThat(b[0]).isEqualTo((byte) 9);
            assertThat(b[b.length - 1]).isEqualTo((byte) 9);
            assertThat(Arrays.copyOfRange(b, 1, 1 + size)).isEqualTo(OfPrimitive.encodeDoubles(v));
            final var dst = new double[v.length];
            assertThat(OfPrimitive.decodeDoubles(b, 1, dst)).isEqualTo(1 + size);
            assertThat(OfPrimitive.encodeDoubles(dst)).isEqualTo(OfPrimitive.encodeDoubles(v));
        }

        @DisplayName("decodeDoubles(byte[]) fails on a length which is not a multiple of the width")
        @Test
        void __notMultiple() {
            assertThatThrownBy(() -> OfPrimitive.decodeDoubles(new byte[Double.BYTES + 1]))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("not a multiple of " + Double.BYTES);
        }

        @DisplayName("the index-based methods fail when the values do not fit")
        @ValueSource(ints = {-1, 1})
        @ParameterizedTest
        void __outOfBounds(final int i) {
            final var v = sample();
            final var b = new byte[v.length * Double.BYTES];
            assertThatThrownBy(() -> OfPrimitive.encodeDoubles(b, i, v))
                    .isInstanceOf(IndexOutOfBoundsException.class);
            assertThatThrownBy(() -> OfPrimitive.decodeDoubles(b, i, new double[v.length]))
                    .isInstanceOf(IndexOutOfBoundsException.class);
        }
    }

    // --------------------------------------------------------------------------------------------------------- boolean

    @DisplayName("boolean")
    @Nested
    class BooleanTest {

        @DisplayName("encodeBoolean(boolean) encodes true as {1} and false as {0}")
        @Test
        void __encode() {
            assertThat(OfPrimitive.encodeBoolean(true)).containsExactly(1);
            assertThat(OfPrimitive.encodeBoolean(false)).containsExactly(0);
        }

        @DisplayName("decodeBoolean(byte[]) reads back what encodeBoolean(boolean) wrote")
        @ValueSource(booleans = {true, false})
        @ParameterizedTest
        void __roundTrip(final boolean v) {
            assertThat(OfPrimitive.decodeBoolean(OfPrimitive.encodeBoolean(v))).isEqualTo(v);
        }

        @DisplayName("decodeBoolean reads any byte other than 0 as true")
        @ValueSource(bytes = {Byte.MIN_VALUE, -1, 2, Byte.MAX_VALUE})
        @ParameterizedTest
        void __decodeOtherThanZeroOrOne(final byte b) {
            assertThat(OfPrimitive.decodeBoolean(new byte[]{b})).isTrue();
            assertThat(OfPrimitive.decodeBoolean(new byte[]{0, b}, 1)).isTrue();
        }

        @DisplayName("encodeBoolean(byte[], int, boolean) writes at the index, and returns the next one")
        @Test
        void __index() {
            final var b = new byte[]{9, 9, 9};
            assertThat(OfPrimitive.encodeBoolean(b, 1, true)).isEqualTo(2);
            assertThat(b).containsExactly(9, 1, 9);
            assertThat(OfPrimitive.decodeBoolean(b, 1)).isTrue();
        }

        @DisplayName("decodeBoolean(byte[]) fails on an array shorter than one byte(s)")
        @ValueSource(ints = {0})
        @ParameterizedTest
        void __tooShort(final int length) {
            assertThatThrownBy(() -> OfPrimitive.decodeBoolean(new byte[length]))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("decodeBoolean(byte[]) reads the beginning of a longer array, and leaves the rest")
        @Test
        void __longer() {
            assertThat(OfPrimitive.decodeBoolean(new byte[]{1, 2, 3})).isTrue();
        }

        @DisplayName("the index-based methods fail outside the array")
        @ValueSource(ints = {-1, 1})
        @ParameterizedTest
        void __outOfBounds(final int i) {
            final var b = new byte[1];
            assertThatThrownBy(() -> OfPrimitive.encodeBoolean(b, i, true))
                    .isInstanceOf(IndexOutOfBoundsException.class);
            assertThatThrownBy(() -> OfPrimitive.decodeBoolean(b, i))
                    .isInstanceOf(IndexOutOfBoundsException.class);
        }
    }

    @DisplayName("boolean[]")
    @Nested
    class BooleansTest {

        private static boolean[] sample() {
            return new boolean[]{true, false, false, true};
        }

        @DisplayName("encodeBooleans(boolean[]) encodes the elements one after another, with no length, and"
                     + " decodeBooleans(byte[]) reads them back")
        @Test
        void __roundTrip() {
            final var v = sample();
            assertThat(OfPrimitive.encodeBooleans(v)).isEqualTo(buffer(v.length * Byte.BYTES, bb -> {
                for (final var e : v) {
                    bb.put((byte) (e ? 1 : 0));
                }
            }));
            assertThat(OfPrimitive.decodeBooleans(OfPrimitive.encodeBooleans(v))).isEqualTo(v);
        }

        @DisplayName("an empty array encodes to no byte, and no byte decodes to an empty array")
        @Test
        void __empty() {
            assertThat(OfPrimitive.encodeBooleans(new boolean[0])).isEmpty();
            assertThat(OfPrimitive.decodeBooleans(new byte[0])).isEmpty();
        }

        @DisplayName("the index-based methods write at, and read from, the index, and return the next one")
        @Test
        void __index() {
            final var v = sample();
            final var size = v.length * Byte.BYTES;
            final var b = new byte[size + 2];
            b[0] = 9;
            b[b.length - 1] = 9;
            assertThat(OfPrimitive.encodeBooleans(b, 1, v)).isEqualTo(1 + size);
            assertThat(b[0]).isEqualTo((byte) 9);
            assertThat(b[b.length - 1]).isEqualTo((byte) 9);
            assertThat(Arrays.copyOfRange(b, 1, 1 + size)).isEqualTo(OfPrimitive.encodeBooleans(v));
            final var dst = new boolean[v.length];
            assertThat(OfPrimitive.decodeBooleans(b, 1, dst)).isEqualTo(1 + size);
            assertThat(OfPrimitive.encodeBooleans(dst)).isEqualTo(OfPrimitive.encodeBooleans(v));
        }

        @DisplayName("decodeBooleans reads 0 as false, and any other byte as true")
        @Test
        void __lenient() {
            assertThat(OfPrimitive.decodeBooleans(new byte[]{0, 1, 2, -1})).containsExactly(false, true, true, true);
            final var dst = new boolean[2];
            OfPrimitive.decodeBooleans(new byte[]{0, Byte.MIN_VALUE}, 0, dst);
            assertThat(dst).containsExactly(false, true);
        }

        @DisplayName("the index-based methods fail when the values do not fit")
        @ValueSource(ints = {-1, 1})
        @ParameterizedTest
        void __outOfBounds(final int i) {
            final var v = sample();
            final var b = new byte[v.length * Byte.BYTES];
            assertThatThrownBy(() -> OfPrimitive.encodeBooleans(b, i, v))
                    .isInstanceOf(IndexOutOfBoundsException.class);
            assertThatThrownBy(() -> OfPrimitive.decodeBooleans(b, i, new boolean[v.length]))
                    .isInstanceOf(IndexOutOfBoundsException.class);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    @DisplayName("values packed into one array read back in order")
    @Test
    void __packing() {
        final var b = new byte[Byte.BYTES + Short.BYTES + Integer.BYTES + Long.BYTES + Byte.BYTES];
        var i = 0;
        i = OfPrimitive.encodeBoolean(b, i, true);
        i = OfPrimitive.encodeShort(b, i, (short) -3);
        i = OfPrimitive.encodeInt(b, i, 37);
        i = OfPrimitive.encodeLong(b, i, Long.MIN_VALUE);
        i = OfPrimitive.encodeByte(b, i, (byte) 7);
        assertThat(i).isEqualTo(b.length);
        assertThat(b).isEqualTo(buffer(b.length, bb -> bb.put((byte) 1).putShort((short) -3).putInt(37)
                .putLong(Long.MIN_VALUE).put((byte) 7)));
        assertThat(OfPrimitive.decodeBoolean(b, 0)).isTrue();
        assertThat(OfPrimitive.decodeShort(b, 1)).isEqualTo((short) -3);
        assertThat(OfPrimitive.decodeInt(b, 3)).isEqualTo(37);
        assertThat(OfPrimitive.decodeLong(b, 7)).isEqualTo(Long.MIN_VALUE);
        assertThat(OfPrimitive.decodeByte(b, 15)).isEqualTo((byte) 7);
    }
}
