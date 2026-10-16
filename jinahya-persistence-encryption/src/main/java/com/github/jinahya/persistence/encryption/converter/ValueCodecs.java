package com.github.jinahya.persistence.encryption.converter;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

import static java.time.temporal.ChronoField.NANO_OF_SECOND;

/**
 * Codecs which turn values into bytes, and back, directly on arrays of bytes, with no buffer allocated.
 * <p>
 * The codecs are grouped, in nested classes, by the package of the types they handle: {@link OfUtil} for the types of
 * {@code java.util}, and so on; the name of each is {@code Of} followed by the package's name under {@code java}, with
 * the dots removed. The primitive types, which belong to no package, are in {@link OfPrimitive}.
 * <p>
 * Each type has a pair of methods: one which encodes a value into a new array of exactly the value's width, and one
 * which decodes a value from the beginning of an array.
 * {@snippet :
 * byte[] b = ValueCodecs.OfPrimitive.encodeLong(37L);    // eight bytes
 * long v = ValueCodecs.OfPrimitive.decodeLong(b);        // 37L
 *} The encodings are big-endian. They are stored data, so each is frozen: an encoding never changes once released.
 * <p>
 * An array of a primitive type is encoded as its elements, one after another, and nothing else: no length is written.
 * The bytes stand for a single value, so whatever holds them, a column or a payload, already knows where they end.
 * <p>
 * Every check is made whether or not assertions are enabled. A decoding method throws {@link IllegalArgumentException}
 * for an array which is not exactly the value's width, or, for an array of a primitive type, whose length is not a
 * multiple of the element's width. A longer array is never read as a prefix: with no type information stored, a value
 * decoded by the wrong type has to fail, not come out as another value.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
final class ValueCodecs {

    private static final VarHandle SHORT = MethodHandles.byteArrayViewVarHandle(short[].class, ByteOrder.BIG_ENDIAN);

    private static final VarHandle INT = MethodHandles.byteArrayViewVarHandle(int[].class, ByteOrder.BIG_ENDIAN);

    private static final VarHandle LONG = MethodHandles.byteArrayViewVarHandle(long[].class, ByteOrder.BIG_ENDIAN);

    private static final VarHandle CHAR = MethodHandles.byteArrayViewVarHandle(char[].class, ByteOrder.BIG_ENDIAN);

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Checks that the specified array holds the specified number of bytes at the specified index.
     *
     * @param b     the array.
     * @param i     the index.
     * @param width the number of bytes.
     * @throws NullPointerException      when the {@code b} is {@code null}.
     * @throws IndexOutOfBoundsException when the {@code b} does not hold {@code width} bytes at the {@code i}.
     */
    private static void checkIndex(final byte[] b, final int i, final int width) {
        Objects.checkFromIndexSize(i, width, Objects.requireNonNull(b, "b is null").length);
    }

    /**
     * Returns the specified array after checking that it is at least as long as the specified number of bytes.
     *
     * @param b     the array.
     * @param least the least number of bytes the {@code b} has to hold; the width of the codec.
     * @return the {@code b}.
     * @throws NullPointerException     when the {@code b} is {@code null}.
     * @throws IllegalArgumentException when the {@code b} is shorter than the {@code least}.
     */
    private static byte[] checkLength(final byte[] b, final int least) {
        if (Objects.requireNonNull(b, "b is null").length < least) {
            throw new IllegalArgumentException(
                    "an array of " + b.length + " byte(s); this codec reads " + least);
        }
        return b;
    }

    /**
     * Returns the specified array after checking that it is exactly as long as the specified number of bytes.
     *
     * @param b     the array to check.
     * @param width the number of bytes the {@code b} has to hold; the width of the codec.
     * @return the {@code b}.
     * @throws NullPointerException     when the {@code b} is {@code null}.
     * @throws IllegalArgumentException when the {@code b} is not {@code width} bytes long.
     */
    private static byte[] checkExact(final byte[] b, final int width) {
        if (Objects.requireNonNull(b, "b is null").length != width) {
            // a longer array would otherwise be read as a prefix, a value of another type, silently
            throw new IllegalArgumentException(
                    "an array of " + b.length + " byte(s); this codec reads exactly " + width);
        }
        return b;
    }

    /**
     * Returns the number of bytes the specified number of elements, each of the specified width, take.
     *
     * @param n     the number of elements.
     * @param width the width of an element, in bytes.
     * @return {@code n * width}.
     * @throws ArithmeticException when the number of bytes overflows an {@code int}.
     */
    private static int size(final int n, final int width) {
        return Math.multiplyExact(n, width);
    }

    /**
     * Returns the number of elements, each of the specified width, the specified array holds, after checking that its
     * length is a multiple of the width.
     *
     * @param b     the array.
     * @param width the width of an element, in bytes.
     * @return the number of elements in the {@code b}.
     * @throws NullPointerException     when the {@code b} is {@code null}.
     * @throws IllegalArgumentException when the length of the {@code b} is not a multiple of the {@code width}.
     */
    private static int checkMultiple(final byte[] b, final int width) {
        if (Objects.requireNonNull(b, "b is null").length % width != 0) {
            // the last element would otherwise be silently dropped
            throw new IllegalArgumentException(
                    "an array of " + b.length + " byte(s), which is not a multiple of " + width);
        }
        return b.length / width;
    }

    /**
     * Codecs of the primitive types, and of arrays of them; {@code byte}, {@code short}, {@code int}, {@code long},
     * {@code char}, {@code float}, {@code double} and {@code boolean}, in the order
     * <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-4.html#jls-4.2">JLS 4.2</a> describes them.
     * <p>
     * Their wrappers, which are classes of {@code java.lang}, are not here.
     */
    public static final class OfPrimitive {

        // -------------------------------------------------------------------------------------------------------- byte

        /**
         * Encodes the specified {@code byte} value into the specified array, at the specified index, as itself.
         *
         * @param b the array into which the value is encoded.
         * @param i the index in the {@code b} at which the value is encoded.
         * @param v the value to encode.
         * @return the index following the encoded value; {@code i + 1}.
         * @throws NullPointerException      when the {@code b} is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} has no byte at the {@code i}.
         */
        static int encodeByte(final byte[] b, final int i, final byte v) {
            checkIndex(b, i, Byte.BYTES);
            b[i] = v;
            return i + Byte.BYTES;
        }

        /**
         * Decodes a {@code byte} value from the specified array, at the specified index.
         *
         * @param b the array from which the value is decoded.
         * @param i the index in the {@code b} from which the value is decoded.
         * @return the decoded value.
         * @throws NullPointerException      when the {@code b} is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} has no byte at the {@code i}.
         */
        static byte decodeByte(final byte[] b, final int i) {
            checkIndex(b, i, Byte.BYTES);
            return b[i];
        }

        /**
         * Encodes the specified {@code byte} value into a new array, as itself.
         *
         * @param v the value to encode.
         * @return a new array holding the encoded {@code v}.
         */
        public static byte[] encodeByte(final byte v) {
            final var b = new byte[Byte.BYTES];
            encodeByte(b, 0, v);
            return b;
        }

        /**
         * Decodes a {@code byte} value from the specified array, which has to hold exactly the value's bytes.
         *
         * @param b the array from which the value is decoded.
         * @return the decoded value.
         * @throws NullPointerException     when the {@code b} is {@code null}.
         * @throws IllegalArgumentException when the {@code b} is not one byte long.
         */
        public static byte decodeByte(final byte[] b) {
            return decodeByte(checkExact(b, Byte.BYTES), 0);
        }

        /**
         * Encodes the specified array of {@code byte} values into the specified array, at the specified index, as they
         * are.
         *
         * @param b the array into which the values are encoded.
         * @param i the index in the {@code b} at which the values are encoded.
         * @param v the values to encode.
         * @return the index following the encoded values; {@code i + v.length * Byte.BYTES}.
         * @throws NullPointerException      when the {@code b}, or the {@code v}, is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} does not hold the encoded values at the {@code i}.
         */
        static int encodeBytes(final byte[] b, final int i, final byte[] v) {
            final var size = size(Objects.requireNonNull(v, "v is null").length, Byte.BYTES);
            checkIndex(b, i, size);
            System.arraycopy(v, 0, b, i, v.length);
            return i + size;
        }

        /**
         * Decodes {@code byte} values from the specified array, at the specified index, into the specified destination;
         * as many as the destination holds.
         *
         * @param b   the array from which the values are decoded.
         * @param i   the index in the {@code b} from which the values are decoded.
         * @param dst the array into which the decoded values are stored.
         * @return the index following the decoded values; {@code i + dst.length * Byte.BYTES}.
         * @throws NullPointerException      when the {@code b}, or the {@code dst}, is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} does not hold {@code dst.length} values at the
         *                                   {@code i}.
         */
        static int decodeBytes(final byte[] b, final int i, final byte[] dst) {
            final var size = size(Objects.requireNonNull(dst, "dst is null").length, Byte.BYTES);
            checkIndex(b, i, size);
            System.arraycopy(b, i, dst, 0, dst.length);
            return i + size;
        }

        /**
         * Encodes the specified array of {@code byte} values into a new array, as they are.
         *
         * @param v the values to encode.
         * @return a new array holding the encoded {@code v}.
         * @throws NullPointerException when the {@code v} is {@code null}.
         * @throws ArithmeticException  when the encoded values would not fit in an array.
         */
        public static byte[] encodeBytes(final byte[] v) {
            final var b = new byte[size(Objects.requireNonNull(v, "v is null").length, Byte.BYTES)];
            encodeBytes(b, 0, v);
            return b;
        }

        /**
         * Decodes all the {@code byte} values the specified array holds, one byte per value; a copy of the array.
         *
         * @param b the array from which the values are decoded.
         * @return a new array of the decoded values.
         * @throws NullPointerException when the {@code b} is {@code null}.
         */
        public static byte[] decodeBytes(final byte[] b) {
            final var dst = new byte[checkMultiple(b, Byte.BYTES)];
            decodeBytes(b, 0, dst);
            return dst;
        }

        // ------------------------------------------------------------------------------------------------------- short

        /**
         * Encodes the specified {@code short} value into the specified array, at the specified index, as two big-endian
         * bytes.
         *
         * @param b the array into which the value is encoded.
         * @param i the index in the {@code b} at which the value is encoded.
         * @param v the value to encode.
         * @return the index following the encoded value; {@code i + 2}.
         * @throws NullPointerException      when the {@code b} is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} does not hold two bytes at the {@code i}.
         */
        static int encodeShort(final byte[] b, final int i, final short v) {
            checkIndex(b, i, Short.BYTES);
            SHORT.set(b, i, v);
            return i + Short.BYTES;
        }

        /**
         * Decodes a {@code short} value from the specified array, at the specified index.
         *
         * @param b the array from which the value is decoded.
         * @param i the index in the {@code b} from which the value is decoded.
         * @return the decoded value.
         * @throws NullPointerException      when the {@code b} is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} does not hold two bytes at the {@code i}.
         */
        static short decodeShort(final byte[] b, final int i) {
            checkIndex(b, i, Short.BYTES);
            return (short) SHORT.get(b, i);
        }

        /**
         * Encodes the specified {@code short} value into a new array, as two big-endian bytes.
         *
         * @param v the value to encode.
         * @return a new array holding the encoded {@code v}.
         */
        public static byte[] encodeShort(final short v) {
            final var b = new byte[Short.BYTES];
            encodeShort(b, 0, v);
            return b;
        }

        /**
         * Decodes a {@code short} value from the specified array, which has to hold exactly the value's bytes.
         *
         * @param b the array from which the value is decoded.
         * @return the decoded value.
         * @throws NullPointerException     when the {@code b} is {@code null}.
         * @throws IllegalArgumentException when the {@code b} is not two bytes long.
         */
        public static short decodeShort(final byte[] b) {
            return decodeShort(checkExact(b, Short.BYTES), 0);
        }

        /**
         * Encodes the specified array of {@code short} values into the specified array, at the specified index, each in
         * two big-endian bytes.
         *
         * @param b the array into which the values are encoded.
         * @param i the index in the {@code b} at which the values are encoded.
         * @param v the values to encode.
         * @return the index following the encoded values; {@code i + v.length * Short.BYTES}.
         * @throws NullPointerException      when the {@code b}, or the {@code v}, is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} does not hold the encoded values at the {@code i}.
         */
        static int encodeShorts(final byte[] b, final int i, final short[] v) {
            final var size = size(Objects.requireNonNull(v, "v is null").length, Short.BYTES);
            checkIndex(b, i, size);
            for (int j = 0; j < v.length; j++) {
                SHORT.set(b, i + j * Short.BYTES, v[j]);
            }
            return i + size;
        }

        /**
         * Decodes {@code short} values from the specified array, at the specified index, into the specified
         * destination; as many as the destination holds.
         *
         * @param b   the array from which the values are decoded.
         * @param i   the index in the {@code b} from which the values are decoded.
         * @param dst the array into which the decoded values are stored.
         * @return the index following the decoded values; {@code i + dst.length * Short.BYTES}.
         * @throws NullPointerException      when the {@code b}, or the {@code dst}, is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} does not hold {@code dst.length} values at the
         *                                   {@code i}.
         */
        static int decodeShorts(final byte[] b, final int i, final short[] dst) {
            final var size = size(Objects.requireNonNull(dst, "dst is null").length, Short.BYTES);
            checkIndex(b, i, size);
            for (int j = 0; j < dst.length; j++) {
                dst[j] = (short) SHORT.get(b, i + j * Short.BYTES);
            }
            return i + size;
        }

        /**
         * Encodes the specified array of {@code short} values into a new array, each in two big-endian bytes.
         *
         * @param v the values to encode.
         * @return a new array holding the encoded {@code v}.
         * @throws NullPointerException when the {@code v} is {@code null}.
         * @throws ArithmeticException  when the encoded values would not fit in an array.
         */
        public static byte[] encodeShorts(final short[] v) {
            final var b = new byte[size(Objects.requireNonNull(v, "v is null").length, Short.BYTES)];
            encodeShorts(b, 0, v);
            return b;
        }

        /**
         * Decodes all the {@code short} values the specified array holds, two bytes per value.
         *
         * @param b the array from which the values are decoded.
         * @return a new array of the decoded values.
         * @throws NullPointerException     when the {@code b} is {@code null}.
         * @throws IllegalArgumentException when the length of the {@code b} is not a multiple of {@code Short.BYTES}.
         */
        public static short[] decodeShorts(final byte[] b) {
            final var dst = new short[checkMultiple(b, Short.BYTES)];
            decodeShorts(b, 0, dst);
            return dst;
        }

        // --------------------------------------------------------------------------------------------------------- int

        /**
         * Encodes the specified {@code int} value into the specified array, at the specified index, as four big-endian
         * bytes.
         *
         * @param b the array into which the value is encoded.
         * @param i the index in the {@code b} at which the value is encoded.
         * @param v the value to encode.
         * @return the index following the encoded value; {@code i + 4}.
         * @throws NullPointerException      when the {@code b} is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} does not hold four bytes at the {@code i}.
         */
        static int encodeInt(final byte[] b, final int i, final int v) {
            checkIndex(b, i, Integer.BYTES);
            INT.set(b, i, v);
            return i + Integer.BYTES;
        }

        /**
         * Decodes an {@code int} value from the specified array, at the specified index.
         *
         * @param b the array from which the value is decoded.
         * @param i the index in the {@code b} from which the value is decoded.
         * @return the decoded value.
         * @throws NullPointerException      when the {@code b} is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} does not hold four bytes at the {@code i}.
         */
        static int decodeInt(final byte[] b, final int i) {
            checkIndex(b, i, Integer.BYTES);
            return (int) INT.get(b, i);
        }

        /**
         * Encodes the specified {@code int} value into a new array, as four big-endian bytes.
         *
         * @param v the value to encode.
         * @return a new array holding the encoded {@code v}.
         */
        public static byte[] encodeInt(final int v) {
            final var b = new byte[Integer.BYTES];
            encodeInt(b, 0, v);
            return b;
        }

        /**
         * Decodes an {@code int} value from the specified array, which has to hold exactly the value's bytes.
         *
         * @param b the array from which the value is decoded.
         * @return the decoded value.
         * @throws NullPointerException     when the {@code b} is {@code null}.
         * @throws IllegalArgumentException when the {@code b} is not four bytes long.
         */
        public static int decodeInt(final byte[] b) {
            return decodeInt(checkExact(b, Integer.BYTES), 0);
        }

        /**
         * Encodes the specified array of {@code int} values into the specified array, at the specified index, each in
         * four big-endian bytes.
         *
         * @param b the array into which the values are encoded.
         * @param i the index in the {@code b} at which the values are encoded.
         * @param v the values to encode.
         * @return the index following the encoded values; {@code i + v.length * Integer.BYTES}.
         * @throws NullPointerException      when the {@code b}, or the {@code v}, is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} does not hold the encoded values at the {@code i}.
         */
        static int encodeInts(final byte[] b, final int i, final int[] v) {
            final var size = size(Objects.requireNonNull(v, "v is null").length, Integer.BYTES);
            checkIndex(b, i, size);
            for (int j = 0; j < v.length; j++) {
                INT.set(b, i + j * Integer.BYTES, v[j]);
            }
            return i + size;
        }

        /**
         * Decodes {@code int} values from the specified array, at the specified index, into the specified destination;
         * as many as the destination holds.
         *
         * @param b   the array from which the values are decoded.
         * @param i   the index in the {@code b} from which the values are decoded.
         * @param dst the array into which the decoded values are stored.
         * @return the index following the decoded values; {@code i + dst.length * Integer.BYTES}.
         * @throws NullPointerException      when the {@code b}, or the {@code dst}, is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} does not hold {@code dst.length} values at the
         *                                   {@code i}.
         */
        static int decodeInts(final byte[] b, final int i, final int[] dst) {
            final var size = size(Objects.requireNonNull(dst, "dst is null").length, Integer.BYTES);
            checkIndex(b, i, size);
            for (int j = 0; j < dst.length; j++) {
                dst[j] = (int) INT.get(b, i + j * Integer.BYTES);
            }
            return i + size;
        }

        /**
         * Encodes the specified array of {@code int} values into a new array, each in four big-endian bytes.
         *
         * @param v the values to encode.
         * @return a new array holding the encoded {@code v}.
         * @throws NullPointerException when the {@code v} is {@code null}.
         * @throws ArithmeticException  when the encoded values would not fit in an array.
         */
        public static byte[] encodeInts(final int[] v) {
            final var b = new byte[size(Objects.requireNonNull(v, "v is null").length, Integer.BYTES)];
            encodeInts(b, 0, v);
            return b;
        }

        /**
         * Decodes all the {@code int} values the specified array holds, four bytes per value.
         *
         * @param b the array from which the values are decoded.
         * @return a new array of the decoded values.
         * @throws NullPointerException     when the {@code b} is {@code null}.
         * @throws IllegalArgumentException when the length of the {@code b} is not a multiple of
         *                                  {@code Integer.BYTES}.
         */
        public static int[] decodeInts(final byte[] b) {
            final var dst = new int[checkMultiple(b, Integer.BYTES)];
            decodeInts(b, 0, dst);
            return dst;
        }

        // -------------------------------------------------------------------------------------------------------- long

        /**
         * Encodes the specified {@code long} value into the specified array, at the specified index, as eight
         * big-endian bytes.
         *
         * @param b the array into which the value is encoded.
         * @param i the index in the {@code b} at which the value is encoded.
         * @param v the value to encode.
         * @return the index following the encoded value; {@code i + 8}.
         * @throws NullPointerException      when the {@code b} is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} does not hold eight bytes at the {@code i}.
         */
        static int encodeLong(final byte[] b, final int i, final long v) {
            checkIndex(b, i, Long.BYTES);
            LONG.set(b, i, v);
            return i + Long.BYTES;
        }

        /**
         * Decodes a {@code long} value from the specified array, at the specified index.
         *
         * @param b the array from which the value is decoded.
         * @param i the index in the {@code b} from which the value is decoded.
         * @return the decoded value.
         * @throws NullPointerException      when the {@code b} is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} does not hold eight bytes at the {@code i}.
         */
        static long decodeLong(final byte[] b, final int i) {
            checkIndex(b, i, Long.BYTES);
            return (long) LONG.get(b, i);
        }

        /**
         * Encodes the specified {@code long} value into a new array, as eight big-endian bytes.
         *
         * @param v the value to encode.
         * @return a new array holding the encoded {@code v}.
         */
        public static byte[] encodeLong(final long v) {
            final var b = new byte[Long.BYTES];
            encodeLong(b, 0, v);
            return b;
        }

        /**
         * Decodes a {@code long} value from the specified array, which has to hold exactly the value's bytes.
         *
         * @param b the array from which the value is decoded.
         * @return the decoded value.
         * @throws NullPointerException     when the {@code b} is {@code null}.
         * @throws IllegalArgumentException when the {@code b} is not eight bytes long.
         */
        public static long decodeLong(final byte[] b) {
            return decodeLong(checkExact(b, Long.BYTES), 0);
        }

        /**
         * Encodes the specified array of {@code long} values into the specified array, at the specified index, each in
         * eight big-endian bytes.
         *
         * @param b the array into which the values are encoded.
         * @param i the index in the {@code b} at which the values are encoded.
         * @param v the values to encode.
         * @return the index following the encoded values; {@code i + v.length * Long.BYTES}.
         * @throws NullPointerException      when the {@code b}, or the {@code v}, is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} does not hold the encoded values at the {@code i}.
         */
        static int encodeLongs(final byte[] b, final int i, final long[] v) {
            final var size = size(Objects.requireNonNull(v, "v is null").length, Long.BYTES);
            checkIndex(b, i, size);
            for (int j = 0; j < v.length; j++) {
                LONG.set(b, i + j * Long.BYTES, v[j]);
            }
            return i + size;
        }

        /**
         * Decodes {@code long} values from the specified array, at the specified index, into the specified destination;
         * as many as the destination holds.
         *
         * @param b   the array from which the values are decoded.
         * @param i   the index in the {@code b} from which the values are decoded.
         * @param dst the array into which the decoded values are stored.
         * @return the index following the decoded values; {@code i + dst.length * Long.BYTES}.
         * @throws NullPointerException      when the {@code b}, or the {@code dst}, is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} does not hold {@code dst.length} values at the
         *                                   {@code i}.
         */
        static int decodeLongs(final byte[] b, final int i, final long[] dst) {
            final var size = size(Objects.requireNonNull(dst, "dst is null").length, Long.BYTES);
            checkIndex(b, i, size);
            for (int j = 0; j < dst.length; j++) {
                dst[j] = (long) LONG.get(b, i + j * Long.BYTES);
            }
            return i + size;
        }

        /**
         * Encodes the specified array of {@code long} values into a new array, each in eight big-endian bytes.
         *
         * @param v the values to encode.
         * @return a new array holding the encoded {@code v}.
         * @throws NullPointerException when the {@code v} is {@code null}.
         * @throws ArithmeticException  when the encoded values would not fit in an array.
         */
        public static byte[] encodeLongs(final long[] v) {
            final var b = new byte[size(Objects.requireNonNull(v, "v is null").length, Long.BYTES)];
            encodeLongs(b, 0, v);
            return b;
        }

        /**
         * Decodes all the {@code long} values the specified array holds, eight bytes per value.
         *
         * @param b the array from which the values are decoded.
         * @return a new array of the decoded values.
         * @throws NullPointerException     when the {@code b} is {@code null}.
         * @throws IllegalArgumentException when the length of the {@code b} is not a multiple of {@code Long.BYTES}.
         */
        public static long[] decodeLongs(final byte[] b) {
            final var dst = new long[checkMultiple(b, Long.BYTES)];
            decodeLongs(b, 0, dst);
            return dst;
        }

        // -------------------------------------------------------------------------------------------------------- char

        /**
         * Encodes the specified {@code char} value into the specified array, at the specified index, as its UTF-16 code
         * unit in two big-endian bytes.
         *
         * @param b the array into which the value is encoded.
         * @param i the index in the {@code b} at which the value is encoded.
         * @param v the value to encode.
         * @return the index following the encoded value; {@code i + 2}.
         * @throws NullPointerException      when the {@code b} is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} does not hold two bytes at the {@code i}.
         */
        static int encodeChar(final byte[] b, final int i, final char v) {
            checkIndex(b, i, Character.BYTES);
            CHAR.set(b, i, v);
            return i + Character.BYTES;
        }

        /**
         * Decodes a {@code char} value from the specified array, at the specified index.
         *
         * @param b the array from which the value is decoded.
         * @param i the index in the {@code b} from which the value is decoded.
         * @return the decoded value.
         * @throws NullPointerException      when the {@code b} is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} does not hold two bytes at the {@code i}.
         */
        static char decodeChar(final byte[] b, final int i) {
            checkIndex(b, i, Character.BYTES);
            return (char) CHAR.get(b, i);
        }

        /**
         * Encodes the specified {@code char} value into a new array, as its UTF-16 code unit in two big-endian bytes.
         *
         * @param v the value to encode.
         * @return a new array holding the encoded {@code v}.
         */
        public static byte[] encodeChar(final char v) {
            final var b = new byte[Character.BYTES];
            encodeChar(b, 0, v);
            return b;
        }

        /**
         * Decodes a {@code char} value from the specified array, which has to hold exactly the value's bytes.
         *
         * @param b the array from which the value is decoded.
         * @return the decoded value.
         * @throws NullPointerException     when the {@code b} is {@code null}.
         * @throws IllegalArgumentException when the {@code b} is not two bytes long.
         */
        public static char decodeChar(final byte[] b) {
            return decodeChar(checkExact(b, Character.BYTES), 0);
        }

        /**
         * Encodes the specified array of {@code char} values into the specified array, at the specified index, each as
         * its UTF-16 code unit in two big-endian bytes; an unpaired surrogate is kept as it is.
         *
         * @param b the array into which the values are encoded.
         * @param i the index in the {@code b} at which the values are encoded.
         * @param v the values to encode.
         * @return the index following the encoded values; {@code i + v.length * Character.BYTES}.
         * @throws NullPointerException      when the {@code b}, or the {@code v}, is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} does not hold the encoded values at the {@code i}.
         */
        static int encodeChars(final byte[] b, final int i, final char[] v) {
            final var size = size(Objects.requireNonNull(v, "v is null").length, Character.BYTES);
            checkIndex(b, i, size);
            for (int j = 0; j < v.length; j++) {
                CHAR.set(b, i + j * Character.BYTES, v[j]);
            }
            return i + size;
        }

        /**
         * Decodes {@code char} values from the specified array, at the specified index, into the specified destination;
         * as many as the destination holds.
         *
         * @param b   the array from which the values are decoded.
         * @param i   the index in the {@code b} from which the values are decoded.
         * @param dst the array into which the decoded values are stored.
         * @return the index following the decoded values; {@code i + dst.length * Character.BYTES}.
         * @throws NullPointerException      when the {@code b}, or the {@code dst}, is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} does not hold {@code dst.length} values at the
         *                                   {@code i}.
         */
        static int decodeChars(final byte[] b, final int i, final char[] dst) {
            final var size = size(Objects.requireNonNull(dst, "dst is null").length, Character.BYTES);
            checkIndex(b, i, size);
            for (int j = 0; j < dst.length; j++) {
                dst[j] = (char) CHAR.get(b, i + j * Character.BYTES);
            }
            return i + size;
        }

        /**
         * Encodes the specified array of {@code char} values into a new array, each as its UTF-16 code unit in two
         * big-endian bytes; an unpaired surrogate is kept as it is.
         *
         * @param v the values to encode.
         * @return a new array holding the encoded {@code v}.
         * @throws NullPointerException when the {@code v} is {@code null}.
         * @throws ArithmeticException  when the encoded values would not fit in an array.
         */
        public static byte[] encodeChars(final char[] v) {
            final var b = new byte[size(Objects.requireNonNull(v, "v is null").length, Character.BYTES)];
            encodeChars(b, 0, v);
            return b;
        }

        /**
         * Decodes all the {@code char} values the specified array holds, two bytes per value.
         *
         * @param b the array from which the values are decoded.
         * @return a new array of the decoded values.
         * @throws NullPointerException     when the {@code b} is {@code null}.
         * @throws IllegalArgumentException when the length of the {@code b} is not a multiple of
         *                                  {@code Character.BYTES}.
         */
        public static char[] decodeChars(final byte[] b) {
            final var dst = new char[checkMultiple(b, Character.BYTES)];
            decodeChars(b, 0, dst);
            return dst;
        }

        // ------------------------------------------------------------------------------------------------------- float

        /**
         * Encodes the specified {@code float} value into the specified array, at the specified index, as its raw IEEE
         * 754 bits in four big-endian bytes.
         * <p>
         * The bits are those of {@link Float#floatToRawIntBits(float)}, not of {@link Float#floatToIntBits(float)}: a
         * {@code NaN}'s payload is kept, not collapsed into the canonical {@code NaN}, and {@code -0.0f} stays
         * {@code -0.0f}.
         *
         * @param b the array into which the value is encoded.
         * @param i the index in the {@code b} at which the value is encoded.
         * @param v the value to encode.
         * @return the index following the encoded value; {@code i + 4}.
         * @throws NullPointerException      when the {@code b} is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} does not hold four bytes at the {@code i}.
         */
        static int encodeFloat(final byte[] b, final int i, final float v) {
            return encodeInt(b, i, Float.floatToRawIntBits(v));
        }

        /**
         * Decodes a {@code float} value from the specified array, at the specified index.
         *
         * @param b the array from which the value is decoded.
         * @param i the index in the {@code b} from which the value is decoded.
         * @return the decoded value.
         * @throws NullPointerException      when the {@code b} is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} does not hold four bytes at the {@code i}.
         * @see Float#intBitsToFloat(int)
         */
        static float decodeFloat(final byte[] b, final int i) {
            return Float.intBitsToFloat(decodeInt(b, i));
        }

        /**
         * Encodes the specified {@code float} value into a new array, as its raw IEEE 754 bits in four big-endian
         * bytes.
         * <p>
         * The bits are those of {@link Float#floatToRawIntBits(float)}, not of {@link Float#floatToIntBits(float)}: a
         * {@code NaN}'s payload is kept, not collapsed into the canonical {@code NaN}, and {@code -0.0f} stays
         * {@code -0.0f}.
         *
         * @param v the value to encode.
         * @return a new array holding the encoded {@code v}.
         */
        public static byte[] encodeFloat(final float v) {
            final var b = new byte[Float.BYTES];
            encodeFloat(b, 0, v);
            return b;
        }

        /**
         * Decodes a {@code float} value from the specified array, which has to hold exactly the value's bytes.
         *
         * @param b the array from which the value is decoded.
         * @return the decoded value.
         * @throws NullPointerException     when the {@code b} is {@code null}.
         * @throws IllegalArgumentException when the {@code b} is not four bytes long.
         */
        public static float decodeFloat(final byte[] b) {
            return decodeFloat(checkExact(b, Float.BYTES), 0);
        }

        /**
         * Encodes the specified array of {@code float} values into the specified array, at the specified index, each as
         * its raw IEEE 754 bits in four big-endian bytes, as {@link #encodeFloat(float)} does.
         *
         * @param b the array into which the values are encoded.
         * @param i the index in the {@code b} at which the values are encoded.
         * @param v the values to encode.
         * @return the index following the encoded values; {@code i + v.length * Float.BYTES}.
         * @throws NullPointerException      when the {@code b}, or the {@code v}, is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} does not hold the encoded values at the {@code i}.
         */
        static int encodeFloats(final byte[] b, final int i, final float[] v) {
            final var size = size(Objects.requireNonNull(v, "v is null").length, Float.BYTES);
            checkIndex(b, i, size);
            for (int j = 0; j < v.length; j++) {
                INT.set(b, i + j * Float.BYTES, Float.floatToRawIntBits(v[j]));
            }
            return i + size;
        }

        /**
         * Decodes {@code float} values from the specified array, at the specified index, into the specified
         * destination; as many as the destination holds.
         *
         * @param b   the array from which the values are decoded.
         * @param i   the index in the {@code b} from which the values are decoded.
         * @param dst the array into which the decoded values are stored.
         * @return the index following the decoded values; {@code i + dst.length * Float.BYTES}.
         * @throws NullPointerException      when the {@code b}, or the {@code dst}, is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} does not hold {@code dst.length} values at the
         *                                   {@code i}.
         */
        static int decodeFloats(final byte[] b, final int i, final float[] dst) {
            final var size = size(Objects.requireNonNull(dst, "dst is null").length, Float.BYTES);
            checkIndex(b, i, size);
            for (int j = 0; j < dst.length; j++) {
                dst[j] = Float.intBitsToFloat((int) INT.get(b, i + j * Float.BYTES));
            }
            return i + size;
        }

        /**
         * Encodes the specified array of {@code float} values into a new array, each as its raw IEEE 754 bits in four
         * big-endian bytes, as {@link #encodeFloat(float)} does.
         *
         * @param v the values to encode.
         * @return a new array holding the encoded {@code v}.
         * @throws NullPointerException when the {@code v} is {@code null}.
         * @throws ArithmeticException  when the encoded values would not fit in an array.
         */
        public static byte[] encodeFloats(final float[] v) {
            final var b = new byte[size(Objects.requireNonNull(v, "v is null").length, Float.BYTES)];
            encodeFloats(b, 0, v);
            return b;
        }

        /**
         * Decodes all the {@code float} values the specified array holds, four bytes per value.
         *
         * @param b the array from which the values are decoded.
         * @return a new array of the decoded values.
         * @throws NullPointerException     when the {@code b} is {@code null}.
         * @throws IllegalArgumentException when the length of the {@code b} is not a multiple of {@code Float.BYTES}.
         */
        public static float[] decodeFloats(final byte[] b) {
            final var dst = new float[checkMultiple(b, Float.BYTES)];
            decodeFloats(b, 0, dst);
            return dst;
        }

        // ------------------------------------------------------------------------------------------------------ double

        /**
         * Encodes the specified {@code double} value into the specified array, at the specified index, as its raw IEEE
         * 754 bits in eight big-endian bytes.
         * <p>
         * The bits are those of {@link Double#doubleToRawLongBits(double)}, not of
         * {@link Double#doubleToLongBits(double)}: a {@code NaN}'s payload is kept, not collapsed into the canonical
         * {@code NaN}, and {@code -0.0d} stays {@code -0.0d}.
         *
         * @param b the array into which the value is encoded.
         * @param i the index in the {@code b} at which the value is encoded.
         * @param v the value to encode.
         * @return the index following the encoded value; {@code i + 8}.
         * @throws NullPointerException      when the {@code b} is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} does not hold eight bytes at the {@code i}.
         */
        static int encodeDouble(final byte[] b, final int i, final double v) {
            return encodeLong(b, i, Double.doubleToRawLongBits(v));
        }

        /**
         * Decodes a {@code double} value from the specified array, at the specified index.
         *
         * @param b the array from which the value is decoded.
         * @param i the index in the {@code b} from which the value is decoded.
         * @return the decoded value.
         * @throws NullPointerException      when the {@code b} is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} does not hold eight bytes at the {@code i}.
         * @see Double#longBitsToDouble(long)
         */
        static double decodeDouble(final byte[] b, final int i) {
            return Double.longBitsToDouble(decodeLong(b, i));
        }

        /**
         * Encodes the specified {@code double} value into a new array, as its raw IEEE 754 bits in eight big-endian
         * bytes.
         * <p>
         * The bits are those of {@link Double#doubleToRawLongBits(double)}, not of
         * {@link Double#doubleToLongBits(double)}: a {@code NaN}'s payload is kept, not collapsed into the canonical
         * {@code NaN}, and {@code -0.0d} stays {@code -0.0d}.
         *
         * @param v the value to encode.
         * @return a new array holding the encoded {@code v}.
         */
        public static byte[] encodeDouble(final double v) {
            final var b = new byte[Double.BYTES];
            encodeDouble(b, 0, v);
            return b;
        }

        /**
         * Decodes a {@code double} value from the specified array, which has to hold exactly the value's bytes.
         *
         * @param b the array from which the value is decoded.
         * @return the decoded value.
         * @throws NullPointerException     when the {@code b} is {@code null}.
         * @throws IllegalArgumentException when the {@code b} is not eight bytes long.
         */
        public static double decodeDouble(final byte[] b) {
            return decodeDouble(checkExact(b, Double.BYTES), 0);
        }

        /**
         * Encodes the specified array of {@code double} values into the specified array, at the specified index, each
         * as its raw IEEE 754 bits in eight big-endian bytes, as {@link #encodeDouble(double)} does.
         *
         * @param b the array into which the values are encoded.
         * @param i the index in the {@code b} at which the values are encoded.
         * @param v the values to encode.
         * @return the index following the encoded values; {@code i + v.length * Double.BYTES}.
         * @throws NullPointerException      when the {@code b}, or the {@code v}, is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} does not hold the encoded values at the {@code i}.
         */
        static int encodeDoubles(final byte[] b, final int i, final double[] v) {
            final var size = size(Objects.requireNonNull(v, "v is null").length, Double.BYTES);
            checkIndex(b, i, size);
            for (int j = 0; j < v.length; j++) {
                LONG.set(b, i + j * Double.BYTES, Double.doubleToRawLongBits(v[j]));
            }
            return i + size;
        }

        /**
         * Decodes {@code double} values from the specified array, at the specified index, into the specified
         * destination; as many as the destination holds.
         *
         * @param b   the array from which the values are decoded.
         * @param i   the index in the {@code b} from which the values are decoded.
         * @param dst the array into which the decoded values are stored.
         * @return the index following the decoded values; {@code i + dst.length * Double.BYTES}.
         * @throws NullPointerException      when the {@code b}, or the {@code dst}, is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} does not hold {@code dst.length} values at the
         *                                   {@code i}.
         */
        static int decodeDoubles(final byte[] b, final int i, final double[] dst) {
            final var size = size(Objects.requireNonNull(dst, "dst is null").length, Double.BYTES);
            checkIndex(b, i, size);
            for (int j = 0; j < dst.length; j++) {
                dst[j] = Double.longBitsToDouble((long) LONG.get(b, i + j * Double.BYTES));
            }
            return i + size;
        }

        /**
         * Encodes the specified array of {@code double} values into a new array, each as its raw IEEE 754 bits in eight
         * big-endian bytes, as {@link #encodeDouble(double)} does.
         *
         * @param v the values to encode.
         * @return a new array holding the encoded {@code v}.
         * @throws NullPointerException when the {@code v} is {@code null}.
         * @throws ArithmeticException  when the encoded values would not fit in an array.
         */
        public static byte[] encodeDoubles(final double[] v) {
            final var b = new byte[size(Objects.requireNonNull(v, "v is null").length, Double.BYTES)];
            encodeDoubles(b, 0, v);
            return b;
        }

        /**
         * Decodes all the {@code double} values the specified array holds, eight bytes per value.
         *
         * @param b the array from which the values are decoded.
         * @return a new array of the decoded values.
         * @throws NullPointerException     when the {@code b} is {@code null}.
         * @throws IllegalArgumentException when the length of the {@code b} is not a multiple of {@code Double.BYTES}.
         */
        public static double[] decodeDoubles(final byte[] b) {
            final var dst = new double[checkMultiple(b, Double.BYTES)];
            decodeDoubles(b, 0, dst);
            return dst;
        }

        // ----------------------------------------------------------------------------------------------------- boolean

        /**
         * Encodes the specified {@code boolean} value into the specified array, at the specified index, as a single
         * byte; {@code 1} for {@code true}, and {@code 0} for {@code false}.
         *
         * @param b the array into which the value is encoded.
         * @param i the index in the {@code b} at which the value is encoded.
         * @param v the value to encode.
         * @return the index following the encoded value; {@code i + 1}.
         * @throws NullPointerException      when the {@code b} is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} has no byte at the {@code i}.
         */
        static int encodeBoolean(final byte[] b, final int i, final boolean v) {
            checkIndex(b, i, Byte.BYTES);
            b[i] = (byte) (v ? 1 : 0);
            return i + Byte.BYTES;
        }

        /**
         * Decodes a {@code boolean} value from the specified array, at the specified index; {@code false} for
         * {@code 0}, and {@code true} for any other byte.
         *
         * @param b the array from which the value is decoded.
         * @param i the index in the {@code b} from which the value is decoded.
         * @return the decoded value.
         * @throws NullPointerException      when the {@code b} is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} has no byte at the {@code i}.
         */
        static boolean decodeBoolean(final byte[] b, final int i) {
            checkIndex(b, i, Byte.BYTES);
            return toBoolean(b[i]);
        }

        /**
         * Returns the {@code boolean} value the specified byte encodes.
         *
         * @param b the byte.
         * @return {@code false} for {@code 0}; {@code true} for any other byte.
         */
        private static boolean toBoolean(final byte b) {
            return b != 0;
        }

        /**
         * Encodes the specified {@code boolean} value into a new array, as a single byte; {@code 1} for {@code true},
         * and {@code 0} for {@code false}.
         *
         * @param v the value to encode.
         * @return a new array holding the encoded {@code v}.
         */
        public static byte[] encodeBoolean(final boolean v) {
            final var b = new byte[Byte.BYTES];
            encodeBoolean(b, 0, v);
            return b;
        }

        /**
         * Decodes a {@code boolean} value from the beginning of the specified array; {@code false} for {@code 0}, and
         * {@code true} for any other byte. The array has to hold exactly the value's one byte.
         *
         * @param b the array from which the value is decoded.
         * @return the decoded value.
         * @throws NullPointerException     when the {@code b} is {@code null}.
         * @throws IllegalArgumentException when the {@code b} is not one byte long.
         */
        public static boolean decodeBoolean(final byte[] b) {
            return decodeBoolean(checkExact(b, Byte.BYTES), 0);
        }

        /**
         * Encodes the specified array of {@code boolean} values into the specified array, at the specified index, each
         * as {@code 1} for {@code true}, and {@code 0} for {@code false}.
         *
         * @param b the array into which the values are encoded.
         * @param i the index in the {@code b} at which the values are encoded.
         * @param v the values to encode.
         * @return the index following the encoded values; {@code i + v.length * Byte.BYTES}.
         * @throws NullPointerException      when the {@code b}, or the {@code v}, is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} does not hold the encoded values at the {@code i}.
         */
        static int encodeBooleans(final byte[] b, final int i, final boolean[] v) {
            final var size = size(Objects.requireNonNull(v, "v is null").length, Byte.BYTES);
            checkIndex(b, i, size);
            for (int j = 0; j < v.length; j++) {
                b[i + j] = (byte) (v[j] ? 1 : 0);
            }
            return i + size;
        }

        /**
         * Decodes {@code boolean} values from the specified array, at the specified index, into the specified
         * destination; as many as the destination holds.
         *
         * @param b   the array from which the values are decoded.
         * @param i   the index in the {@code b} from which the values are decoded.
         * @param dst the array into which the decoded values are stored.
         * @return the index following the decoded values; {@code i + dst.length * Byte.BYTES}.
         * @throws NullPointerException      when the {@code b}, or the {@code dst}, is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} does not hold {@code dst.length} values at the
         *                                   {@code i}.
         */
        static int decodeBooleans(final byte[] b, final int i, final boolean[] dst) {
            final var size = size(Objects.requireNonNull(dst, "dst is null").length, Byte.BYTES);
            checkIndex(b, i, size);
            for (int j = 0; j < dst.length; j++) {
                dst[j] = toBoolean(b[i + j]);
            }
            return i + size;
        }

        /**
         * Encodes the specified array of {@code boolean} values into a new array, each as {@code 1} for {@code true},
         * and {@code 0} for {@code false}.
         *
         * @param v the values to encode.
         * @return a new array holding the encoded {@code v}.
         * @throws NullPointerException when the {@code v} is {@code null}.
         * @throws ArithmeticException  when the encoded values would not fit in an array.
         */
        public static byte[] encodeBooleans(final boolean[] v) {
            final var b = new byte[size(Objects.requireNonNull(v, "v is null").length, Byte.BYTES)];
            encodeBooleans(b, 0, v);
            return b;
        }

        /**
         * Decodes all the {@code boolean} values the specified array holds, one byte per value; {@code false} for
         * {@code 0}, and {@code true} for any other byte.
         *
         * @param b the array from which the values are decoded.
         * @return a new array of the decoded values.
         * @throws NullPointerException when the {@code b} is {@code null}.
         */
        public static boolean[] decodeBooleans(final byte[] b) {
            final var dst = new boolean[checkMultiple(b, Byte.BYTES)];
            decodeBooleans(b, 0, dst);
            return dst;
        }

        // -------------------------------------------------------------------------------------------------------------

        private OfPrimitive() {
            throw new AssertionError("instantiation is not allowed");
        }
    }

    /**
     * Codecs of the types of {@code java.lang}.
     */
    public static final class OfLang {

        // ------------------------------------------------------------------------------------------------------ String

        /**
         * Encodes the specified {@code String} value into a new array, as its UTF-8 bytes.
         * <p>
         * Unlike {@link String#getBytes(java.nio.charset.Charset)}, which replaces a character it cannot encode with
         * {@code '?'}, a string which is not encodable as it is, one holding an unpaired surrogate, is rejected.
         *
         * @param v the value to encode.
         * @return a new array holding the encoded {@code v}.
         * @throws NullPointerException     when the {@code v} is {@code null}.
         * @throws IllegalArgumentException when the {@code v} is not encodable as UTF-8; it holds an unpaired
         *                                  surrogate. The message never carries the value.
         */
        public static byte[] encodeString(final String v) {
            Objects.requireNonNull(v, "v is null");
            try {
                final var buffer = StandardCharsets.UTF_8.newEncoder()
                        .onMalformedInput(CodingErrorAction.REPORT)
                        .onUnmappableCharacter(CodingErrorAction.REPORT)
                        .encode(CharBuffer.wrap(v));
                final var b = new byte[buffer.remaining()];
                buffer.get(b);
                return b;
            } catch (final CharacterCodingException cce) {
                // never put the value in the message
                throw new IllegalArgumentException(
                        "the string is not encodable as UTF-8 (an unpaired surrogate?), so it would not be read back"
                        + " as is",
                        cce);
            }
        }

        /**
         * Decodes a {@code String} value from all the bytes of the specified array, as UTF-8.
         * <p>
         * Unlike {@link String#String(byte[], java.nio.charset.Charset)}, which replaces a malformed sequence with
         * {@code U+FFFD}, bytes which are not well-formed UTF-8 are rejected.
         *
         * @param b the array from which the value is decoded.
         * @return the decoded value.
         * @throws NullPointerException     when the {@code b} is {@code null}.
         * @throws IllegalArgumentException when the {@code b} is not well-formed UTF-8.
         */
        public static String decodeString(final byte[] b) {
            Objects.requireNonNull(b, "b is null");
            try {
                return StandardCharsets.UTF_8.newDecoder()
                        .onMalformedInput(CodingErrorAction.REPORT)
                        .onUnmappableCharacter(CodingErrorAction.REPORT)
                        .decode(ByteBuffer.wrap(b))
                        .toString();
            } catch (final CharacterCodingException cce) {
                throw new IllegalArgumentException("the bytes are not well-formed UTF-8", cce);
            }
        }

        // -------------------------------------------------------------------------------------------------------- enum

        /**
         * Encodes the specified enum constant into a new array, as its {@link Enum#name() name}, as
         * {@link #encodeString(String)} encodes it.
         * <p>
         * The constant is stored by its name, as {@code @Enumerated(EnumType.STRING)} stores it, never by its ordinal:
         * reordering, or inserting, constants is common, and would silently map every stored value to another constant.
         * Renaming a constant, by contrast, is a breaking change: the stored name no longer resolves, and the decoding
         * fails.
         *
         * @param v the constant to encode.
         * @return a new array holding the encoded {@code v}.
         * @throws NullPointerException when the {@code v} is {@code null}.
         */
        public static byte[] encodeEnum(final Enum<?> v) {
            return encodeString(Objects.requireNonNull(v, "v is null").name());
        }

        /**
         * Decodes the constant, of the specified enum class, whose name all the bytes of the specified array hold.
         *
         * @param b         the array from which the constant is decoded.
         * @param enumClass the enum class.
         * @param <E>       enum type parameter
         * @return the decoded constant.
         * @throws NullPointerException     when the {@code b}, or the {@code enumClass}, is {@code null}.
         * @throws IllegalArgumentException when the {@code b} is not well-formed UTF-8, or the name is not one of a
         *                                  constant of the {@code enumClass}.
         */
        public static <E extends Enum<E>> E decodeEnum(final byte[] b, final Class<E> enumClass) {
            Objects.requireNonNull(enumClass, "enumClass is null");
            return Enum.valueOf(enumClass, decodeString(b));
        }

        // ----------------------------------------------------------------------------------------- Byte[], Character[]

        /**
         * Encodes the specified array of {@code Byte} values into a new array, one byte per value, as
         * {@link OfPrimitive#encodeBytes(byte[])} encodes their unboxed values.
         *
         * @param v the values to encode.
         * @return a new array holding the encoded {@code v}.
         * @throws NullPointerException     when the {@code v} is {@code null}.
         * @throws IllegalArgumentException when an element of the {@code v} is {@code null}, which has no encoding.
         * @see <a
         *         href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#deprecations">
         *         Jakarta Persistence 3.2, A.1.1. Deprecations</a>
         * @deprecated Jakarta Persistence 3.2 deprecates the use of {@code Byte[]} for basic attributes in favor of
         *         {@code byte[]}.
         */
        @Deprecated
        public static byte[] encodeBytesWrapped(final Byte[] v) {
            final var p = new byte[Objects.requireNonNull(v, "v is null").length];
            for (int i = 0; i < p.length; i++) {
                if (v[i] == null) {
                    throw new IllegalArgumentException(
                            "a null element at " + i + "; a Byte[] is encoded one byte per element");
                }
                p[i] = v[i];
            }
            return OfPrimitive.encodeBytes(p);
        }

        /**
         * Decodes all the {@code Byte} values the specified array holds, one byte per value.
         *
         * @param b the array from which the values are decoded.
         * @return a new array of the decoded values.
         * @throws NullPointerException when the {@code b} is {@code null}.
         * @see <a
         *         href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#deprecations">
         *         Jakarta Persistence 3.2, A.1.1. Deprecations</a>
         * @deprecated Jakarta Persistence 3.2 deprecates the use of {@code Byte[]} for basic attributes in favor of
         *         {@code byte[]}.
         */
        @Deprecated
        public static Byte[] decodeBytesWrapped(final byte[] b) {
            final var p = OfPrimitive.decodeBytes(b);
            final var v = new Byte[p.length];
            for (int i = 0; i < v.length; i++) {
                v[i] = p[i];
            }
            return v;
        }

        /**
         * Encodes the specified array of {@code Character} values into a new array, two bytes per value, as
         * {@link OfPrimitive#encodeChars(char[])} encodes their unboxed values.
         *
         * @param v the values to encode.
         * @return a new array holding the encoded {@code v}.
         * @throws NullPointerException     when the {@code v} is {@code null}.
         * @throws IllegalArgumentException when an element of the {@code v} is {@code null}, which has no encoding.
         * @see <a
         *         href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#deprecations">
         *         Jakarta Persistence 3.2, A.1.1. Deprecations</a>
         * @deprecated Jakarta Persistence 3.2 deprecates the use of {@code Character[]} for basic attributes in favor
         *         of {@code char[]}.
         */
        @Deprecated
        public static byte[] encodeCharsWrapped(final Character[] v) {
            final var p = new char[Objects.requireNonNull(v, "v is null").length];
            for (int i = 0; i < p.length; i++) {
                if (v[i] == null) {
                    throw new IllegalArgumentException(
                            "a null element at " + i + "; a Character[] is encoded one char per element");
                }
                p[i] = v[i];
            }
            return OfPrimitive.encodeChars(p);
        }

        /**
         * Decodes all the {@code Character} values the specified array holds, two bytes per value.
         *
         * @param b the array from which the values are decoded.
         * @return a new array of the decoded values.
         * @throws NullPointerException     when the {@code b} is {@code null}.
         * @throws IllegalArgumentException when the length of the {@code b} is odd.
         * @see <a
         *         href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#deprecations">
         *         Jakarta Persistence 3.2, A.1.1. Deprecations</a>
         * @deprecated Jakarta Persistence 3.2 deprecates the use of {@code Character[]} for basic attributes in favor
         *         of {@code char[]}.
         */
        @Deprecated
        public static Character[] decodeCharsWrapped(final byte[] b) {
            final var p = OfPrimitive.decodeChars(b);
            final var v = new Character[p.length];
            for (int i = 0; i < v.length; i++) {
                v[i] = p[i];
            }
            return v;
        }

        // -------------------------------------------------------------------------------------------------------------

        private OfLang() {
            throw new AssertionError("instantiation is not allowed");
        }
    }

    /**
     * Codecs of the types of {@code java.math}.
     */
    public static final class OfMath {

        // -------------------------------------------------------------------------------------------------- BigInteger

        /**
         * Encodes the specified {@code BigInteger} value into a new array, as its two's-complement, big-endian, minimal
         * bytes; those of {@link BigInteger#toByteArray()}.
         *
         * @param v the value to encode.
         * @return a new array holding the encoded {@code v}; at least one byte long.
         * @throws NullPointerException when the {@code v} is {@code null}.
         */
        public static byte[] encodeBigInteger(final BigInteger v) {
            return Objects.requireNonNull(v, "v is null").toByteArray();
        }

        /**
         * Decodes a {@code BigInteger} value from all the bytes of the specified array.
         *
         * @param b the array from which the value is decoded.
         * @return the decoded value.
         * @throws NullPointerException  when the {@code b} is {@code null}.
         * @throws NumberFormatException when the {@code b} is empty; an {@link IllegalArgumentException}.
         * @see BigInteger#BigInteger(byte[])
         */
        public static BigInteger decodeBigInteger(final byte[] b) {
            return new BigInteger(Objects.requireNonNull(b, "b is null"));
        }

        // -------------------------------------------------------------------------------------------------- BigDecimal

        /**
         * Encodes the specified {@code BigDecimal} value into a new array, as its scale, in four big-endian bytes,
         * followed by its unscaled value, as {@link #encodeBigInteger(BigInteger)} encodes it.
         *
         * @param v the value to encode.
         * @return a new array holding the encoded {@code v}; at least five bytes long.
         * @throws NullPointerException when the {@code v} is {@code null}.
         */
        public static byte[] encodeBigDecimal(final BigDecimal v) {
            Objects.requireNonNull(v, "v is null");
            final var unscaledValue = encodeBigInteger(v.unscaledValue());
            final var b = new byte[Integer.BYTES + unscaledValue.length];
            OfPrimitive.encodeInt(b, 0, v.scale());
            System.arraycopy(unscaledValue, 0, b, Integer.BYTES, unscaledValue.length);
            return b;
        }

        /**
         * Decodes a {@code BigDecimal} value from all the bytes of the specified array.
         *
         * @param b the array from which the value is decoded.
         * @return the decoded value.
         * @throws NullPointerException     when the {@code b} is {@code null}.
         * @throws IllegalArgumentException when the {@code b} is shorter than four bytes, the scale; or a
         *                                  {@link NumberFormatException} when it holds no byte for the unscaled value.
         */
        public static BigDecimal decodeBigDecimal(final byte[] b) {
            final var scale = OfPrimitive.decodeInt(checkLength(b, Integer.BYTES), 0);
            final var unscaledValue = decodeBigInteger(Arrays.copyOfRange(b, Integer.BYTES, b.length));
            return new BigDecimal(unscaledValue, scale);
        }

        // -------------------------------------------------------------------------------------------------------------

        private OfMath() {
            throw new AssertionError("instantiation is not allowed");
        }
    }

    /**
     * Codecs of the types of {@code java.sql}.
     *
     * @see <a href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#deprecations">
     *         Jakarta Persistence 3.2, A.1.1. Deprecations</a>
     * @deprecated Jakarta Persistence 3.2 deprecates the use of the {@code java.sql} date and time types in new
     *         applications in favor of the {@code java.time} API.
     */
    @Deprecated
    public static final class OfSql {

        // -------------------------------------------------------------------------------------------------------- Date

        /**
         * The number of bytes a {@link java.sql.Date} is encoded in. The value is {@value}.
         *
         */
        public static final int DATE_BYTES = Long.BYTES;

        /**
         * Encodes the specified {@code Date} value into the specified array, at the specified index, as its epoch
         * millis in eight big-endian bytes.
         *
         * @param b the array into which the value is encoded.
         * @param i the index in the {@code b} at which the value is encoded.
         * @param v the value to encode.
         * @return the index following the encoded value; {@code i + DATE_BYTES}.
         * @throws NullPointerException      when the {@code b}, or the {@code v}, is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} does not hold {@link #DATE_BYTES} bytes at the
         *                                   {@code i}.
         */
        static int encodeDate(final byte[] b, final int i, final java.sql.Date v) {
            Objects.requireNonNull(v, "v is null");
            checkIndex(b, i, DATE_BYTES);
            OfPrimitive.encodeLong(b, i, v.getTime());
            return i + DATE_BYTES;
        }

        /**
         * Decodes a {@code Date} value from the specified array, at the specified index.
         *
         * @param b the array from which the value is decoded.
         * @param i the index in the {@code b} from which the value is decoded.
         * @return the decoded value.
         * @throws NullPointerException      when the {@code b} is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} does not hold {@link #DATE_BYTES} bytes at the
         *                                   {@code i}.
         */
        static java.sql.Date decodeDate(final byte[] b, final int i) {
            checkIndex(b, i, DATE_BYTES);
            return new java.sql.Date(OfPrimitive.decodeLong(b, i));
        }

        /**
         * Encodes the specified {@code Date} value into a new array of eight bytes, as its epoch millis in eight
         * big-endian bytes.
         *
         * @param v the value to encode.
         * @return a new array holding the encoded {@code v}.
         * @throws NullPointerException when the {@code v} is {@code null}.
         * @see #DATE_BYTES
         */
        public static byte[] encodeDate(final java.sql.Date v) {
            Objects.requireNonNull(v, "v is null");
            final var b = new byte[DATE_BYTES];
            encodeDate(b, 0, v);
            return b;
        }

        /**
         * Decodes a {@code Date} value from the specified array, which has to hold exactly the value's bytes.
         *
         * @param b the array from which the value is decoded.
         * @return the decoded value.
         * @throws NullPointerException     when the {@code b} is {@code null}.
         * @throws IllegalArgumentException when the {@code b} is not eight bytes long.
         */
        public static java.sql.Date decodeDate(final byte[] b) {
            return decodeDate(checkExact(b, DATE_BYTES), 0);
        }

        // -------------------------------------------------------------------------------------------------------- Time

        /**
         * The number of bytes a {@link java.sql.Time} is encoded in. The value is {@value}.
         *
         */
        public static final int TIME_BYTES = Long.BYTES;

        /**
         * Encodes the specified {@code Time} value into the specified array, at the specified index, as its epoch
         * millis in eight big-endian bytes.
         *
         * @param b the array into which the value is encoded.
         * @param i the index in the {@code b} at which the value is encoded.
         * @param v the value to encode.
         * @return the index following the encoded value; {@code i + TIME_BYTES}.
         * @throws NullPointerException      when the {@code b}, or the {@code v}, is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} does not hold {@link #TIME_BYTES} bytes at the
         *                                   {@code i}.
         */
        static int encodeTime(final byte[] b, final int i, final java.sql.Time v) {
            Objects.requireNonNull(v, "v is null");
            checkIndex(b, i, TIME_BYTES);
            OfPrimitive.encodeLong(b, i, v.getTime());
            return i + TIME_BYTES;
        }

        /**
         * Decodes a {@code Time} value from the specified array, at the specified index.
         *
         * @param b the array from which the value is decoded.
         * @param i the index in the {@code b} from which the value is decoded.
         * @return the decoded value.
         * @throws NullPointerException      when the {@code b} is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} does not hold {@link #TIME_BYTES} bytes at the
         *                                   {@code i}.
         */
        static java.sql.Time decodeTime(final byte[] b, final int i) {
            checkIndex(b, i, TIME_BYTES);
            return new java.sql.Time(OfPrimitive.decodeLong(b, i));
        }

        /**
         * Encodes the specified {@code Time} value into a new array of eight bytes, as its epoch millis in eight
         * big-endian bytes.
         *
         * @param v the value to encode.
         * @return a new array holding the encoded {@code v}.
         * @throws NullPointerException when the {@code v} is {@code null}.
         * @see #TIME_BYTES
         */
        public static byte[] encodeTime(final java.sql.Time v) {
            Objects.requireNonNull(v, "v is null");
            final var b = new byte[TIME_BYTES];
            encodeTime(b, 0, v);
            return b;
        }

        /**
         * Decodes a {@code Time} value from the specified array, which has to hold exactly the value's bytes.
         *
         * @param b the array from which the value is decoded.
         * @return the decoded value.
         * @throws NullPointerException     when the {@code b} is {@code null}.
         * @throws IllegalArgumentException when the {@code b} is not eight bytes long.
         */
        public static java.sql.Time decodeTime(final byte[] b) {
            return decodeTime(checkExact(b, TIME_BYTES), 0);
        }

        // --------------------------------------------------------------------------------------------------- Timestamp

        /**
         * The number of bytes a {@link java.sql.Timestamp} is encoded in. The value is {@value}.
         *
         */
        public static final int TIMESTAMP_BYTES = OfTime.INSTANT_BYTES;

        /**
         * Encodes the specified {@code Timestamp} value into the specified array, at the specified index, as its
         * instant, as {@link OfTime#encodeInstant(Instant)} encodes it, so that its nanos are kept.
         *
         * @param b the array into which the value is encoded.
         * @param i the index in the {@code b} at which the value is encoded.
         * @param v the value to encode.
         * @return the index following the encoded value; {@code i + TIMESTAMP_BYTES}.
         * @throws NullPointerException      when the {@code b}, or the {@code v}, is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} does not hold {@link #TIMESTAMP_BYTES} bytes at the
         *                                   {@code i}.
         */
        static int encodeTimestamp(final byte[] b, final int i, final java.sql.Timestamp v) {
            Objects.requireNonNull(v, "v is null");
            checkIndex(b, i, TIMESTAMP_BYTES);
            OfTime.encodeInstant(b, i, v.toInstant());
            return i + TIMESTAMP_BYTES;
        }

        /**
         * Decodes a {@code Timestamp} value from the specified array, at the specified index.
         *
         * @param b the array from which the value is decoded.
         * @param i the index in the {@code b} from which the value is decoded.
         * @return the decoded value.
         * @throws NullPointerException      when the {@code b} is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} does not hold {@link #TIMESTAMP_BYTES} bytes at the
         *                                   {@code i}.
         * @throws IllegalArgumentException  when the instant is beyond the range of a {@code Timestamp}.
         * @throws DateTimeException         when the instant is not valid, as {@link OfTime#decodeInstant(byte[])}
         *                                   rejects it.
         */
        static java.sql.Timestamp decodeTimestamp(final byte[] b, final int i) {
            checkIndex(b, i, TIMESTAMP_BYTES);
            return java.sql.Timestamp.from(OfTime.decodeInstant(b, i));
        }

        /**
         * Encodes the specified {@code Timestamp} value into a new array of twelve bytes, as its instant, as
         * {@link OfTime#encodeInstant(Instant)} encodes it, so that its nanos are kept.
         *
         * @param v the value to encode.
         * @return a new array holding the encoded {@code v}.
         * @throws NullPointerException when the {@code v} is {@code null}.
         * @see #TIMESTAMP_BYTES
         */
        public static byte[] encodeTimestamp(final java.sql.Timestamp v) {
            Objects.requireNonNull(v, "v is null");
            final var b = new byte[TIMESTAMP_BYTES];
            encodeTimestamp(b, 0, v);
            return b;
        }

        /**
         * Decodes a {@code Timestamp} value from the specified array, which has to hold exactly the value's bytes.
         *
         * @param b the array from which the value is decoded.
         * @return the decoded value.
         * @throws NullPointerException     when the {@code b} is {@code null}.
         * @throws IllegalArgumentException when the {@code b} is not twelve bytes long; or when the instant is
         *                                  beyond the range of a {@code Timestamp}.
         * @throws DateTimeException        when the instant is not valid, as {@link OfTime#decodeInstant(byte[])}
         *                                  rejects it.
         */
        public static java.sql.Timestamp decodeTimestamp(final byte[] b) {
            return decodeTimestamp(checkExact(b, TIMESTAMP_BYTES), 0);
        }

        // -------------------------------------------------------------------------------------------------------------

        private OfSql() {
            throw new AssertionError("instantiation is not allowed");
        }
    }

    /**
     * Codecs of the types of {@code java.time}.
     * <p>
     * A decoding method rejects a field out of the range of its type with a {@link DateTimeException}, through the
     * checks of {@code java.time} itself.
     */
    public static final class OfTime {

        // --------------------------------------------------------------------------------------------------- LocalDate

        /**
         * The number of bytes a {@link LocalDate} is encoded in. The value is {@value}.
         *
         */
        public static final int LOCAL_DATE_BYTES = Long.BYTES;

        /**
         * Encodes the specified {@code LocalDate} value into the specified array, at the specified index, as its epoch
         * day in eight big-endian bytes.
         *
         * @param b the array into which the value is encoded.
         * @param i the index in the {@code b} at which the value is encoded.
         * @param v the value to encode.
         * @return the index following the encoded value; {@code i + LOCAL_DATE_BYTES}.
         * @throws NullPointerException      when the {@code b}, or the {@code v}, is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} does not hold {@link #LOCAL_DATE_BYTES} bytes at the
         *                                   {@code i}.
         */
        static int encodeLocalDate(final byte[] b, final int i, final LocalDate v) {
            Objects.requireNonNull(v, "v is null");
            checkIndex(b, i, LOCAL_DATE_BYTES);
            OfPrimitive.encodeLong(b, i, v.toEpochDay());
            return i + LOCAL_DATE_BYTES;
        }

        /**
         * Decodes a {@code LocalDate} value from the specified array, at the specified index.
         *
         * @param b the array from which the value is decoded.
         * @param i the index in the {@code b} from which the value is decoded.
         * @return the decoded value.
         * @throws NullPointerException      when the {@code b} is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} does not hold {@link #LOCAL_DATE_BYTES} bytes at the
         *                                   {@code i}.
         * @throws DateTimeException         when the epoch day is beyond the range of {@code LocalDate}.
         */
        static LocalDate decodeLocalDate(final byte[] b, final int i) {
            checkIndex(b, i, LOCAL_DATE_BYTES);
            return LocalDate.ofEpochDay(OfPrimitive.decodeLong(b, i));
        }

        /**
         * Encodes the specified {@code LocalDate} value into a new array of eight bytes, as its epoch day in eight
         * big-endian bytes.
         *
         * @param v the value to encode.
         * @return a new array holding the encoded {@code v}.
         * @throws NullPointerException when the {@code v} is {@code null}.
         * @see #LOCAL_DATE_BYTES
         */
        public static byte[] encodeLocalDate(final LocalDate v) {
            Objects.requireNonNull(v, "v is null");
            final var b = new byte[LOCAL_DATE_BYTES];
            encodeLocalDate(b, 0, v);
            return b;
        }

        /**
         * Decodes a {@code LocalDate} value from the specified array, which has to hold exactly the value's bytes.
         *
         * @param b the array from which the value is decoded.
         * @return the decoded value.
         * @throws NullPointerException     when the {@code b} is {@code null}.
         * @throws IllegalArgumentException when the {@code b} is not eight bytes long.
         * @throws DateTimeException        when the epoch day is beyond the range of {@code LocalDate}.
         */
        public static LocalDate decodeLocalDate(final byte[] b) {
            return decodeLocalDate(checkExact(b, LOCAL_DATE_BYTES), 0);
        }

        // --------------------------------------------------------------------------------------------------- LocalTime

        /**
         * The number of bytes a {@link LocalTime} is encoded in. The value is {@value}.
         *
         */
        public static final int LOCAL_TIME_BYTES = Long.BYTES;

        /**
         * Encodes the specified {@code LocalTime} value into the specified array, at the specified index, as its
         * nano-of-day in eight big-endian bytes.
         *
         * @param b the array into which the value is encoded.
         * @param i the index in the {@code b} at which the value is encoded.
         * @param v the value to encode.
         * @return the index following the encoded value; {@code i + LOCAL_TIME_BYTES}.
         * @throws NullPointerException      when the {@code b}, or the {@code v}, is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} does not hold {@link #LOCAL_TIME_BYTES} bytes at the
         *                                   {@code i}.
         */
        static int encodeLocalTime(final byte[] b, final int i, final LocalTime v) {
            Objects.requireNonNull(v, "v is null");
            checkIndex(b, i, LOCAL_TIME_BYTES);
            OfPrimitive.encodeLong(b, i, v.toNanoOfDay());
            return i + LOCAL_TIME_BYTES;
        }

        /**
         * Decodes a {@code LocalTime} value from the specified array, at the specified index.
         *
         * @param b the array from which the value is decoded.
         * @param i the index in the {@code b} from which the value is decoded.
         * @return the decoded value.
         * @throws NullPointerException      when the {@code b} is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} does not hold {@link #LOCAL_TIME_BYTES} bytes at the
         *                                   {@code i}.
         * @throws DateTimeException         when the nano-of-day is beyond a day.
         */
        static LocalTime decodeLocalTime(final byte[] b, final int i) {
            checkIndex(b, i, LOCAL_TIME_BYTES);
            return LocalTime.ofNanoOfDay(OfPrimitive.decodeLong(b, i));
        }

        /**
         * Encodes the specified {@code LocalTime} value into a new array of eight bytes, as its nano-of-day in eight
         * big-endian bytes.
         *
         * @param v the value to encode.
         * @return a new array holding the encoded {@code v}.
         * @throws NullPointerException when the {@code v} is {@code null}.
         * @see #LOCAL_TIME_BYTES
         */
        public static byte[] encodeLocalTime(final LocalTime v) {
            Objects.requireNonNull(v, "v is null");
            final var b = new byte[LOCAL_TIME_BYTES];
            encodeLocalTime(b, 0, v);
            return b;
        }

        /**
         * Decodes a {@code LocalTime} value from the specified array, which has to hold exactly the value's bytes.
         *
         * @param b the array from which the value is decoded.
         * @return the decoded value.
         * @throws NullPointerException     when the {@code b} is {@code null}.
         * @throws IllegalArgumentException when the {@code b} is not eight bytes long.
         * @throws DateTimeException        when the nano-of-day is beyond a day.
         */
        public static LocalTime decodeLocalTime(final byte[] b) {
            return decodeLocalTime(checkExact(b, LOCAL_TIME_BYTES), 0);
        }

        // ----------------------------------------------------------------------------------------------- LocalDateTime

        /**
         * The number of bytes a {@link LocalDateTime} is encoded in. The value is {@value}.
         *
         */
        public static final int LOCAL_DATE_TIME_BYTES = LOCAL_DATE_BYTES + LOCAL_TIME_BYTES;

        /**
         * Encodes the specified {@code LocalDateTime} value into the specified array, at the specified index, as its
         * date, then its time, as {@link #encodeLocalDate(LocalDate)} and {@link #encodeLocalTime(LocalTime)} encode
         * them.
         *
         * @param b the array into which the value is encoded.
         * @param i the index in the {@code b} at which the value is encoded.
         * @param v the value to encode.
         * @return the index following the encoded value; {@code i + LOCAL_DATE_TIME_BYTES}.
         * @throws NullPointerException      when the {@code b}, or the {@code v}, is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} does not hold {@link #LOCAL_DATE_TIME_BYTES} bytes at
         *                                   the {@code i}.
         */
        static int encodeLocalDateTime(final byte[] b, final int i, final LocalDateTime v) {
            Objects.requireNonNull(v, "v is null");
            checkIndex(b, i, LOCAL_DATE_TIME_BYTES);
            encodeLocalTime(b, encodeLocalDate(b, i, v.toLocalDate()), v.toLocalTime());
            return i + LOCAL_DATE_TIME_BYTES;
        }

        /**
         * Decodes a {@code LocalDateTime} value from the specified array, at the specified index.
         *
         * @param b the array from which the value is decoded.
         * @param i the index in the {@code b} from which the value is decoded.
         * @return the decoded value.
         * @throws NullPointerException      when the {@code b} is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} does not hold {@link #LOCAL_DATE_TIME_BYTES} bytes at
         *                                   the {@code i}.
         * @throws DateTimeException         when the date, or the time, is beyond the range of its type.
         */
        static LocalDateTime decodeLocalDateTime(final byte[] b, final int i) {
            checkIndex(b, i, LOCAL_DATE_TIME_BYTES);
            return LocalDateTime.of(decodeLocalDate(b, i), decodeLocalTime(b, i + LOCAL_DATE_BYTES));
        }

        /**
         * Encodes the specified {@code LocalDateTime} value into a new array of sixteen bytes, as its date, then its
         * time, as {@link #encodeLocalDate(LocalDate)} and {@link #encodeLocalTime(LocalTime)} encode them.
         *
         * @param v the value to encode.
         * @return a new array holding the encoded {@code v}.
         * @throws NullPointerException when the {@code v} is {@code null}.
         * @see #LOCAL_DATE_TIME_BYTES
         */
        public static byte[] encodeLocalDateTime(final LocalDateTime v) {
            Objects.requireNonNull(v, "v is null");
            final var b = new byte[LOCAL_DATE_TIME_BYTES];
            encodeLocalDateTime(b, 0, v);
            return b;
        }

        /**
         * Decodes a {@code LocalDateTime} value from the specified array, which has to hold exactly the value's
         * bytes.
         *
         * @param b the array from which the value is decoded.
         * @return the decoded value.
         * @throws NullPointerException     when the {@code b} is {@code null}.
         * @throws IllegalArgumentException when the {@code b} is not sixteen bytes long.
         * @throws DateTimeException        when the date, or the time, is beyond the range of its type.
         */
        public static LocalDateTime decodeLocalDateTime(final byte[] b) {
            return decodeLocalDateTime(checkExact(b, LOCAL_DATE_TIME_BYTES), 0);
        }

        // -------------------------------------------------------------------------------------------------- ZoneOffset

        /**
         * The number of bytes a {@link ZoneOffset} is encoded in. The value is {@value}.
         *
         */
        public static final int ZONE_OFFSET_BYTES = Integer.BYTES;

        /**
         * Encodes the specified {@code ZoneOffset} value into the specified array, at the specified index, as its total
         * seconds in four big-endian bytes.
         *
         * @param b the array into which the value is encoded.
         * @param i the index in the {@code b} at which the value is encoded.
         * @param v the value to encode.
         * @return the index following the encoded value; {@code i + ZONE_OFFSET_BYTES}.
         * @throws NullPointerException      when the {@code b}, or the {@code v}, is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} does not hold {@link #ZONE_OFFSET_BYTES} bytes at the
         *                                   {@code i}.
         */
        static int encodeZoneOffset(final byte[] b, final int i, final ZoneOffset v) {
            Objects.requireNonNull(v, "v is null");
            checkIndex(b, i, ZONE_OFFSET_BYTES);
            OfPrimitive.encodeInt(b, i, v.getTotalSeconds());
            return i + ZONE_OFFSET_BYTES;
        }

        /**
         * Decodes a {@code ZoneOffset} value from the specified array, at the specified index.
         *
         * @param b the array from which the value is decoded.
         * @param i the index in the {@code b} from which the value is decoded.
         * @return the decoded value.
         * @throws NullPointerException      when the {@code b} is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} does not hold {@link #ZONE_OFFSET_BYTES} bytes at the
         *                                   {@code i}.
         * @throws DateTimeException         when the offset is beyond {@code +/-18:00}.
         */
        static ZoneOffset decodeZoneOffset(final byte[] b, final int i) {
            checkIndex(b, i, ZONE_OFFSET_BYTES);
            return ZoneOffset.ofTotalSeconds(OfPrimitive.decodeInt(b, i));
        }

        /**
         * Encodes the specified {@code ZoneOffset} value into a new array of four bytes, as its total seconds in four
         * big-endian bytes.
         *
         * @param v the value to encode.
         * @return a new array holding the encoded {@code v}.
         * @throws NullPointerException when the {@code v} is {@code null}.
         * @see #ZONE_OFFSET_BYTES
         */
        public static byte[] encodeZoneOffset(final ZoneOffset v) {
            Objects.requireNonNull(v, "v is null");
            final var b = new byte[ZONE_OFFSET_BYTES];
            encodeZoneOffset(b, 0, v);
            return b;
        }

        /**
         * Decodes a {@code ZoneOffset} value from the specified array, which has to hold exactly the value's
         * bytes.
         *
         * @param b the array from which the value is decoded.
         * @return the decoded value.
         * @throws NullPointerException     when the {@code b} is {@code null}.
         * @throws IllegalArgumentException when the {@code b} is not four bytes long.
         * @throws DateTimeException        when the offset is beyond {@code +/-18:00}.
         */
        public static ZoneOffset decodeZoneOffset(final byte[] b) {
            return decodeZoneOffset(checkExact(b, ZONE_OFFSET_BYTES), 0);
        }

        // -------------------------------------------------------------------------------------------------- OffsetTime

        /**
         * The number of bytes a {@link OffsetTime} is encoded in. The value is {@value}.
         *
         */
        public static final int OFFSET_TIME_BYTES = LOCAL_TIME_BYTES + ZONE_OFFSET_BYTES;

        /**
         * Encodes the specified {@code OffsetTime} value into the specified array, at the specified index, as its local
         * time, then its offset, as {@link #encodeLocalTime(LocalTime)} and {@link #encodeZoneOffset(ZoneOffset)}
         * encode them.
         *
         * @param b the array into which the value is encoded.
         * @param i the index in the {@code b} at which the value is encoded.
         * @param v the value to encode.
         * @return the index following the encoded value; {@code i + OFFSET_TIME_BYTES}.
         * @throws NullPointerException      when the {@code b}, or the {@code v}, is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} does not hold {@link #OFFSET_TIME_BYTES} bytes at the
         *                                   {@code i}.
         */
        static int encodeOffsetTime(final byte[] b, final int i, final OffsetTime v) {
            Objects.requireNonNull(v, "v is null");
            checkIndex(b, i, OFFSET_TIME_BYTES);
            encodeZoneOffset(b, encodeLocalTime(b, i, v.toLocalTime()), v.getOffset());
            return i + OFFSET_TIME_BYTES;
        }

        /**
         * Decodes a {@code OffsetTime} value from the specified array, at the specified index.
         *
         * @param b the array from which the value is decoded.
         * @param i the index in the {@code b} from which the value is decoded.
         * @return the decoded value.
         * @throws NullPointerException      when the {@code b} is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} does not hold {@link #OFFSET_TIME_BYTES} bytes at the
         *                                   {@code i}.
         * @throws DateTimeException         when the time, or the offset, is beyond the range of its type.
         */
        static OffsetTime decodeOffsetTime(final byte[] b, final int i) {
            checkIndex(b, i, OFFSET_TIME_BYTES);
            return OffsetTime.of(decodeLocalTime(b, i), decodeZoneOffset(b, i + LOCAL_TIME_BYTES));
        }

        /**
         * Encodes the specified {@code OffsetTime} value into a new array of twelve bytes, as its local time, then its
         * offset, as {@link #encodeLocalTime(LocalTime)} and {@link #encodeZoneOffset(ZoneOffset)} encode them.
         *
         * @param v the value to encode.
         * @return a new array holding the encoded {@code v}.
         * @throws NullPointerException when the {@code v} is {@code null}.
         * @see #OFFSET_TIME_BYTES
         */
        public static byte[] encodeOffsetTime(final OffsetTime v) {
            Objects.requireNonNull(v, "v is null");
            final var b = new byte[OFFSET_TIME_BYTES];
            encodeOffsetTime(b, 0, v);
            return b;
        }

        /**
         * Decodes a {@code OffsetTime} value from the specified array, which has to hold exactly the value's
         * bytes.
         *
         * @param b the array from which the value is decoded.
         * @return the decoded value.
         * @throws NullPointerException     when the {@code b} is {@code null}.
         * @throws IllegalArgumentException when the {@code b} is not twelve bytes long.
         * @throws DateTimeException        when the time, or the offset, is beyond the range of its type.
         */
        public static OffsetTime decodeOffsetTime(final byte[] b) {
            return decodeOffsetTime(checkExact(b, OFFSET_TIME_BYTES), 0);
        }

        // ---------------------------------------------------------------------------------------------- OffsetDateTime

        /**
         * The number of bytes a {@link OffsetDateTime} is encoded in. The value is {@value}.
         *
         */
        public static final int OFFSET_DATE_TIME_BYTES = LOCAL_DATE_TIME_BYTES + ZONE_OFFSET_BYTES;

        /**
         * Encodes the specified {@code OffsetDateTime} value into the specified array, at the specified index, as its
         * local date-time, then its offset, as {@link #encodeLocalDateTime(LocalDateTime)} and
         * {@link #encodeZoneOffset(ZoneOffset)} encode them.
         *
         * @param b the array into which the value is encoded.
         * @param i the index in the {@code b} at which the value is encoded.
         * @param v the value to encode.
         * @return the index following the encoded value; {@code i + OFFSET_DATE_TIME_BYTES}.
         * @throws NullPointerException      when the {@code b}, or the {@code v}, is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} does not hold {@link #OFFSET_DATE_TIME_BYTES} bytes at
         *                                   the {@code i}.
         */
        static int encodeOffsetDateTime(final byte[] b, final int i, final OffsetDateTime v) {
            Objects.requireNonNull(v, "v is null");
            checkIndex(b, i, OFFSET_DATE_TIME_BYTES);
            encodeZoneOffset(b, encodeLocalDateTime(b, i, v.toLocalDateTime()), v.getOffset());
            return i + OFFSET_DATE_TIME_BYTES;
        }

        /**
         * Decodes a {@code OffsetDateTime} value from the specified array, at the specified index.
         *
         * @param b the array from which the value is decoded.
         * @param i the index in the {@code b} from which the value is decoded.
         * @return the decoded value.
         * @throws NullPointerException      when the {@code b} is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} does not hold {@link #OFFSET_DATE_TIME_BYTES} bytes at
         *                                   the {@code i}.
         * @throws DateTimeException         when the date-time, or the offset, is beyond the range of its type.
         */
        static OffsetDateTime decodeOffsetDateTime(final byte[] b, final int i) {
            checkIndex(b, i, OFFSET_DATE_TIME_BYTES);
            return OffsetDateTime.of(decodeLocalDateTime(b, i), decodeZoneOffset(b, i + LOCAL_DATE_TIME_BYTES));
        }

        /**
         * Encodes the specified {@code OffsetDateTime} value into a new array of twenty bytes, as its local date-time,
         * then its offset, as {@link #encodeLocalDateTime(LocalDateTime)} and {@link #encodeZoneOffset(ZoneOffset)}
         * encode them.
         *
         * @param v the value to encode.
         * @return a new array holding the encoded {@code v}.
         * @throws NullPointerException when the {@code v} is {@code null}.
         * @see #OFFSET_DATE_TIME_BYTES
         */
        public static byte[] encodeOffsetDateTime(final OffsetDateTime v) {
            Objects.requireNonNull(v, "v is null");
            final var b = new byte[OFFSET_DATE_TIME_BYTES];
            encodeOffsetDateTime(b, 0, v);
            return b;
        }

        /**
         * Decodes a {@code OffsetDateTime} value from the specified array, which has to hold exactly the value's bytes.
         *
         * @param b the array from which the value is decoded.
         * @return the decoded value.
         * @throws NullPointerException     when the {@code b} is {@code null}.
         * @throws IllegalArgumentException when the {@code b} is not twenty bytes long.
         * @throws DateTimeException        when the date-time, or the offset, is beyond the range of its type.
         */
        public static OffsetDateTime decodeOffsetDateTime(final byte[] b) {
            return decodeOffsetDateTime(checkExact(b, OFFSET_DATE_TIME_BYTES), 0);
        }

        // ----------------------------------------------------------------------------------------------------- Instant

        /**
         * The number of bytes a {@link Instant} is encoded in. The value is {@value}.
         *
         */
        public static final int INSTANT_BYTES = Long.BYTES + Integer.BYTES;

        /**
         * Encodes the specified {@code Instant} value into the specified array, at the specified index, as its epoch
         * second in eight big-endian bytes, then its nano-of-second in four.
         *
         * @param b the array into which the value is encoded.
         * @param i the index in the {@code b} at which the value is encoded.
         * @param v the value to encode.
         * @return the index following the encoded value; {@code i + INSTANT_BYTES}.
         * @throws NullPointerException      when the {@code b}, or the {@code v}, is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} does not hold {@link #INSTANT_BYTES} bytes at the
         *                                   {@code i}.
         */
        static int encodeInstant(final byte[] b, final int i, final Instant v) {
            Objects.requireNonNull(v, "v is null");
            checkIndex(b, i, INSTANT_BYTES);
            OfPrimitive.encodeInt(b, OfPrimitive.encodeLong(b, i, v.getEpochSecond()), v.getNano());
            return i + INSTANT_BYTES;
        }

        /**
         * Decodes a {@code Instant} value from the specified array, at the specified index.
         *
         * @param b the array from which the value is decoded.
         * @param i the index in the {@code b} from which the value is decoded.
         * @return the decoded value.
         * @throws NullPointerException      when the {@code b} is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} does not hold {@link #INSTANT_BYTES} bytes at the
         *                                   {@code i}.
         * @throws DateTimeException         when the nano-of-second is not between {@code 0} and {@code 999,999,999},
         *                                   which {@code Instant} would otherwise silently carry into the second; or
         *                                   the instant is beyond the range of {@code Instant}.
         */
        static Instant decodeInstant(final byte[] b, final int i) {
            checkIndex(b, i, INSTANT_BYTES);
            return Instant.ofEpochSecond(
                    OfPrimitive.decodeLong(b, i),
                    NANO_OF_SECOND.checkValidIntValue(OfPrimitive.decodeInt(b, i + Long.BYTES))
            );
        }

        /**
         * Encodes the specified {@code Instant} value into a new array of twelve bytes, as its epoch second in eight
         * big-endian bytes, then its nano-of-second in four.
         *
         * @param v the value to encode.
         * @return a new array holding the encoded {@code v}.
         * @throws NullPointerException when the {@code v} is {@code null}.
         * @see #INSTANT_BYTES
         */
        public static byte[] encodeInstant(final Instant v) {
            Objects.requireNonNull(v, "v is null");
            final var b = new byte[INSTANT_BYTES];
            encodeInstant(b, 0, v);
            return b;
        }

        /**
         * Decodes a {@code Instant} value from the specified array, which has to hold exactly the value's bytes.
         *
         * @param b the array from which the value is decoded.
         * @return the decoded value.
         * @throws NullPointerException     when the {@code b} is {@code null}.
         * @throws IllegalArgumentException when the {@code b} is not twelve bytes long.
         * @throws DateTimeException        when the nano-of-second is not between {@code 0} and {@code 999,999,999},
         *                                  which {@code Instant} would otherwise silently carry into the second; or the
         *                                  instant is beyond the range of {@code Instant}.
         */
        public static Instant decodeInstant(final byte[] b) {
            return decodeInstant(checkExact(b, INSTANT_BYTES), 0);
        }

        // -------------------------------------------------------------------------------------------------------- Year

        /**
         * The number of bytes a {@link Year} is encoded in. The value is {@value}.
         *
         */
        public static final int YEAR_BYTES = Integer.BYTES;

        /**
         * Encodes the specified {@code Year} value into the specified array, at the specified index, as its value in
         * four big-endian bytes.
         *
         * @param b the array into which the value is encoded.
         * @param i the index in the {@code b} at which the value is encoded.
         * @param v the value to encode.
         * @return the index following the encoded value; {@code i + YEAR_BYTES}.
         * @throws NullPointerException      when the {@code b}, or the {@code v}, is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} does not hold {@link #YEAR_BYTES} bytes at the
         *                                   {@code i}.
         */
        static int encodeYear(final byte[] b, final int i, final Year v) {
            Objects.requireNonNull(v, "v is null");
            checkIndex(b, i, YEAR_BYTES);
            OfPrimitive.encodeInt(b, i, v.getValue());
            return i + YEAR_BYTES;
        }

        /**
         * Decodes a {@code Year} value from the specified array, at the specified index.
         *
         * @param b the array from which the value is decoded.
         * @param i the index in the {@code b} from which the value is decoded.
         * @return the decoded value.
         * @throws NullPointerException      when the {@code b} is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} does not hold {@link #YEAR_BYTES} bytes at the
         *                                   {@code i}.
         * @throws DateTimeException         when the value is beyond {@code +/-999,999,999}.
         */
        static Year decodeYear(final byte[] b, final int i) {
            checkIndex(b, i, YEAR_BYTES);
            return Year.of(OfPrimitive.decodeInt(b, i));
        }

        /**
         * Encodes the specified {@code Year} value into a new array of four bytes, as its value in four big-endian
         * bytes.
         *
         * @param v the value to encode.
         * @return a new array holding the encoded {@code v}.
         * @throws NullPointerException when the {@code v} is {@code null}.
         * @see #YEAR_BYTES
         */
        public static byte[] encodeYear(final Year v) {
            Objects.requireNonNull(v, "v is null");
            final var b = new byte[YEAR_BYTES];
            encodeYear(b, 0, v);
            return b;
        }

        /**
         * Decodes a {@code Year} value from the specified array, which has to hold exactly the value's bytes.
         *
         * @param b the array from which the value is decoded.
         * @return the decoded value.
         * @throws NullPointerException     when the {@code b} is {@code null}.
         * @throws IllegalArgumentException when the {@code b} is not four bytes long.
         * @throws DateTimeException        when the value is beyond {@code +/-999,999,999}.
         */
        public static Year decodeYear(final byte[] b) {
            return decodeYear(checkExact(b, YEAR_BYTES), 0);
        }

        // -------------------------------------------------------------------------------------------------------------

        private OfTime() {
            throw new AssertionError("instantiation is not allowed");
        }
    }

    /**
     * Codecs of the types of {@code java.util}.
     */
    public static final class OfUtil {

        // -------------------------------------------------------------------------------------------------------- Date

        /**
         * The number of bytes a {@link java.util.Date} is encoded in. The value is {@value}.
         *
         */
        public static final int DATE_BYTES = Long.BYTES;

        /**
         * Encodes the specified {@code Date} value into the specified array, at the specified index, as its epoch
         * millis in eight big-endian bytes.
         *
         * @param b the array into which the value is encoded.
         * @param i the index in the {@code b} at which the value is encoded.
         * @param v the value to encode.
         * @return the index following the encoded value; {@code i + DATE_BYTES}.
         * @throws NullPointerException      when the {@code b}, or the {@code v}, is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} does not hold {@link #DATE_BYTES} bytes at the
         *                                   {@code i}.
         * @see <a
         *         href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#deprecations">
         *         Jakarta Persistence 3.2, A.1.1. Deprecations</a>
         * @deprecated Jakarta Persistence 3.2 deprecates the use of {@link java.util.Date} in new applications in favor
         *         of the {@code java.time} API.
         */
        @Deprecated
        static int encodeDate(final byte[] b, final int i, final java.util.Date v) {
            Objects.requireNonNull(v, "v is null");
            checkIndex(b, i, DATE_BYTES);
            OfPrimitive.encodeLong(b, i, v.getTime());
            return i + DATE_BYTES;
        }

        /**
         * Decodes a {@code Date} value from the specified array, at the specified index.
         *
         * @param b the array from which the value is decoded.
         * @param i the index in the {@code b} from which the value is decoded.
         * @return the decoded value.
         * @throws NullPointerException      when the {@code b} is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} does not hold {@link #DATE_BYTES} bytes at the
         *                                   {@code i}.
         * @see <a
         *         href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#deprecations">
         *         Jakarta Persistence 3.2, A.1.1. Deprecations</a>
         * @deprecated Jakarta Persistence 3.2 deprecates the use of {@link java.util.Date} in new applications in favor
         *         of the {@code java.time} API.
         */
        @Deprecated
        static java.util.Date decodeDate(final byte[] b, final int i) {
            checkIndex(b, i, DATE_BYTES);
            return new java.util.Date(OfPrimitive.decodeLong(b, i));
        }

        /**
         * Encodes the specified {@code Date} value into a new array of eight bytes, as its epoch millis in eight
         * big-endian bytes.
         *
         * @param v the value to encode.
         * @return a new array holding the encoded {@code v}.
         * @throws NullPointerException when the {@code v} is {@code null}.
         * @see #DATE_BYTES
         * @see <a
         *         href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#deprecations">
         *         Jakarta Persistence 3.2, A.1.1. Deprecations</a>
         * @deprecated Jakarta Persistence 3.2 deprecates the use of {@link java.util.Date} in new applications in favor
         *         of the {@code java.time} API.
         */
        @Deprecated
        public static byte[] encodeDate(final java.util.Date v) {
            Objects.requireNonNull(v, "v is null");
            final var b = new byte[DATE_BYTES];
            encodeDate(b, 0, v);
            return b;
        }

        /**
         * Decodes a {@code Date} value from the specified array, which has to hold exactly the value's bytes.
         *
         * @param b the array from which the value is decoded.
         * @return the decoded value.
         * @throws NullPointerException     when the {@code b} is {@code null}.
         * @throws IllegalArgumentException when the {@code b} is not eight bytes long.
         * @see <a
         *         href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#deprecations">
         *         Jakarta Persistence 3.2, A.1.1. Deprecations</a>
         * @deprecated Jakarta Persistence 3.2 deprecates the use of {@link java.util.Date} in new applications in favor
         *         of the {@code java.time} API.
         */
        @Deprecated
        public static java.util.Date decodeDate(final byte[] b) {
            return decodeDate(checkExact(b, DATE_BYTES), 0);
        }
        // -------------------------------------------------------------------------------------------------------- UUID

        /**
         * The number of bytes a {@link UUID} is encoded in. The value is {@value}.
         */
        public static final int UUID_BYTES = Long.BYTES << 1;

        /**
         * Encodes the specified {@code UUID} value into the specified array, at the specified index, as its most, then
         * its least, significant bits, each in eight big-endian bytes.
         *
         * @param b the array into which the value is encoded.
         * @param i the index in the {@code b} at which the value is encoded.
         * @param v the value to encode.
         * @return the index following the encoded value; {@code i + 16}.
         * @throws NullPointerException      when the {@code b}, or the {@code v}, is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} does not hold sixteen bytes at the {@code i}.
         * @see #UUID_BYTES
         */
        static int encodeUuid(final byte[] b, final int i, final UUID v) {
            Objects.requireNonNull(v, "v is null");
            checkIndex(b, i, UUID_BYTES);
            LONG.set(b, i, v.getMostSignificantBits());
            LONG.set(b, i + Long.BYTES, v.getLeastSignificantBits());
            return i + UUID_BYTES;
        }

        /**
         * Decodes a {@code UUID} value from the specified array, at the specified index.
         *
         * @param b the array from which the value is decoded.
         * @param i the index in the {@code b} from which the value is decoded.
         * @return the decoded value.
         * @throws NullPointerException      when the {@code b} is {@code null}.
         * @throws IndexOutOfBoundsException when the {@code b} does not hold sixteen bytes at the {@code i}.
         */
        static UUID decodeUuid(final byte[] b, final int i) {
            checkIndex(b, i, UUID_BYTES);
            return new UUID((long) LONG.get(b, i), (long) LONG.get(b, i + Long.BYTES));
        }

        /**
         * Encodes the specified {@code UUID} value into a new array, as its most, then its least, significant bits,
         * each in eight big-endian bytes.
         *
         * @param v the value to encode.
         * @return a new array holding the encoded {@code v}.
         * @throws NullPointerException when the {@code v} is {@code null}.
         * @see #UUID_BYTES
         */
        public static byte[] encodeUuid(final UUID v) {
            Objects.requireNonNull(v, "v is null");
            final var b = new byte[UUID_BYTES];
            encodeUuid(b, 0, v);
            return b;
        }

        /**
         * Decodes a {@code UUID} value from the specified array, which has to hold exactly the value's bytes.
         *
         * @param b the array from which the value is decoded.
         * @return the decoded value.
         * @throws NullPointerException     when the {@code b} is {@code null}.
         * @throws IllegalArgumentException when the {@code b} is not sixteen bytes long.
         */
        public static UUID decodeUuid(final byte[] b) {
            return decodeUuid(checkExact(b, UUID_BYTES), 0);
        }

        // -------------------------------------------------------------------------------------------------------------

        private OfUtil() {
            throw new AssertionError("instantiation is not allowed");
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    private ValueCodecs() {
        throw new AssertionError("instantiation is not allowed");
    }
}
