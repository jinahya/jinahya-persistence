package com.github.jinahya.persistence.crypto;

import org.jspecify.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Calendar;
import java.util.UUID;

/**
 * A utility class for turning the Jakarta Persistence basic types into bytes, and back.
 * <p>
 * Each method is named after the type it handles and the number of bytes it produces — {@code int_4}, {@code uuid_16},
 * {@code local_date_time_16} — with a trailing underscore alone, as in {@code string_} or {@code serializable_}, for
 * the types whose encoding is variable in length. The encodings are big-endian and self-contained, so that a value can
 * be reconstructed from its bytes without consulting the database.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see AbstractEntityEncryptionService
 */
@SuppressWarnings({
        "java:S100", // Method names should comply with a naming convention
        "java:S101"  // Class names should comply with a naming convention
})
final class EntityEncryptionServiceUtils {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns the specified payload after checking that it is exactly as long as a fixed-width codec reads.
     *
     * @param b      the payload.
     * @param length the width of the codec, in bytes.
     * @return the {@code b}.
     * @throws IllegalArgumentException when the {@code b} is shorter, or longer, than the {@code length}.
     * @implNote Checked unconditionally, not with {@code assert}: a payload which is too long would otherwise be read
     *         silently, as its first {@code length} bytes.
     */
    private static byte[] exactly(final byte[] b, final int length) {
        if (b.length != length) {
            throw new IllegalArgumentException(
                    "a payload of " + b.length + " byte(s); this codec reads exactly " + length);
        }
        return b;
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Writes the specified {@code boolean} value into the specified array of bytes, at the specified index.
     *
     * @param b the array of bytes to which the value is written.
     * @param i the index in the {@code b} at which the value is written.
     * @param v the value to write.
     * @return the specified {@code b}.
     */
    private static byte[] boolean_1(final byte[] b, final int i, final boolean v) {
        assert b != null;
        assert i >= 0;
        assert i + Byte.BYTES <= b.length;
        b[i] = (byte) (v ? 1 : 0);
        return b;
    }

    /**
     * Reads a {@code boolean} value from the specified array of bytes, at the specified index.
     *
     * @param b the array of bytes from which the value is read.
     * @param i the index in the {@code b} from which the value is read.
     * @return the value read from the {@code b}.
     * @throws IllegalArgumentException when the byte is neither {@code 0} nor {@code 1}.
     */
    private static boolean boolean_1(final byte[] b, final int i) {
        assert b != null;
        assert i >= 0;
        assert i + Byte.BYTES <= b.length;
        return switch (b[i]) {
            case 0 -> false;
            case 1 -> true;
            // anything else is not a value this codec wrote; reading it as false would hide the corruption
            default -> throw new IllegalArgumentException("not a boolean; byte: " + (b[i] & 0xFF) + "; expected 0 or 1");
        };
    }

    /**
     * Returns an array of bytes representing the specified {@code boolean} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     */
    static byte[] boolean_1(final boolean v) {
        return boolean_1(new byte[Byte.BYTES], 0, v);
    }

    /**
     * Returns the {@code boolean} value represented by the specified array of bytes.
     *
     * @param b the array of bytes.
     * @return the value represented by the {@code b}.
     */
    static boolean boolean_1(final byte[] b) {
        return boolean_1(exactly(b, Byte.BYTES), 0);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Writes the specified {@code byte} value into the specified array of bytes, at the specified index.
     *
     * @param b the array of bytes to which the value is written.
     * @param i the index in the {@code b} at which the value is written.
     * @param v the value to write.
     * @return the specified {@code b}.
     */
    private static byte[] byte_1(final byte[] b, final int i, final byte v) {
        assert b != null;
        assert i >= 0;
        assert i + Byte.BYTES <= b.length;
        b[i] = v;
        return b;
    }

    /**
     * Reads a {@code byte} value from the specified array of bytes, at the specified index.
     *
     * @param b the array of bytes from which the value is read.
     * @param i the index in the {@code b} from which the value is read.
     * @return the value read from the {@code b}.
     */
    private static byte byte_1(final byte[] b, final int i) {
        assert b != null;
        assert i >= 0;
        assert i + Byte.BYTES <= b.length;
        return b[i];
    }

    /**
     * Returns an array of bytes representing the specified {@code byte} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     */
    static byte[] byte_1(final byte v) {
        return byte_1(new byte[Byte.BYTES], 0, v);
    }

    /**
     * Returns the {@code byte} value represented by the specified array of bytes.
     *
     * @param b the array of bytes.
     * @return the value represented by the {@code b}.
     */
    static byte byte_1(final byte[] b) {
        return byte_1(exactly(b, Byte.BYTES), 0);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Writes the specified {@code short} value into the specified array of bytes, at the specified index.
     *
     * @param b the array of bytes to which the value is written.
     * @param i the index in the {@code b} at which the value is written.
     * @param v the value to write.
     * @return the specified {@code b}.
     */
    private static byte[] short_2(final byte[] b, int i, final short v) {
        assert b != null;
        assert i >= 0;
        assert i + Short.BYTES <= b.length;
        b[i] = (byte) (v >> Byte.SIZE);
        b[++i] = (byte) (v & 0xFF);
        return b;
    }

    /**
     * Reads a {@code short} value from the specified array of bytes, at the specified index.
     *
     * @param b the array of bytes from which the value is read.
     * @param i the index in the {@code b} from which the value is read.
     * @return the value read from the {@code b}.
     */
    private static short short_2(final byte[] b, int i) {
        assert b != null;
        assert i >= 0;
        assert i + Short.BYTES <= b.length;
        return (short) (
                (b[i] << Byte.SIZE) |
                (b[++i] & 0xFF)
        );
    }

    /**
     * Returns an array of bytes representing the specified {@code short} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     */
    static byte[] short_2(final short v) {
        return short_2(new byte[Short.BYTES], 0, v);
    }

    /**
     * Returns the {@code short} value represented by the specified array of bytes.
     *
     * @param b the array of bytes.
     * @return the value represented by the {@code b}.
     */
    static short short_2(final byte[] b) {
        return short_2(exactly(b, Short.BYTES), 0);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Writes the specified {@code int} value into the specified array of bytes, at the specified index.
     *
     * @param b the array of bytes to which the value is written.
     * @param i the index in the {@code b} at which the value is written.
     * @param v the value to write.
     * @return the specified {@code b}.
     */
    private static byte[] int_4(final byte[] b, final int i, final int v) {
        assert b != null;
        assert i >= 0;
        assert i + Integer.BYTES <= b.length;
        short_2(b, i, (short) (v >> Short.SIZE));
        short_2(b, i + Short.BYTES, (short) (v & 0xFFFF));
        return b;
    }

    /**
     * Reads a {@code int} value from the specified array of bytes, at the specified index.
     *
     * @param b the array of bytes from which the value is read.
     * @param i the index in the {@code b} from which the value is read.
     * @return the value read from the {@code b}.
     */
    private static int int_4(final byte[] b, final int i) {
        assert b != null;
        assert i >= 0;
        assert i + Integer.BYTES <= b.length;
        return (short_2(b, i) << Short.SIZE) |
               (short_2(b, i + Short.BYTES) & 0xFFFF);
    }

    /**
     * Returns an array of bytes representing the specified {@code int} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     */
    static byte[] int_4(final int v) {
        return int_4(new byte[Integer.BYTES], 0, v);
    }

    /**
     * Returns the {@code int} value represented by the specified array of bytes.
     *
     * @param b the array of bytes.
     * @return the value represented by the {@code b}.
     */
    static int int_4(final byte[] b) {
        return int_4(exactly(b, Integer.BYTES), 0);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Writes the specified {@code long} value into the specified array of bytes, at the specified index.
     *
     * @param b the array of bytes to which the value is written.
     * @param i the index in the {@code b} at which the value is written.
     * @param v the value to write.
     * @return the specified {@code b}.
     */
    private static byte[] long_8(final byte[] b, final int i, final long v) {
        assert b != null;
        assert i >= 0;
        assert i + Long.BYTES <= b.length;
        int_4(b, i, (int) (v >> Integer.SIZE));
        int_4(b, i + Integer.BYTES, (int) v);
        return b;
    }

    /**
     * Reads a {@code long} value from the specified array of bytes, at the specified index.
     *
     * @param b the array of bytes from which the value is read.
     * @param i the index in the {@code b} from which the value is read.
     * @return the value read from the {@code b}.
     */
    private static long long_8(final byte[] b, final int i) {
        assert b != null;
        assert i >= 0;
        assert i + Long.BYTES <= b.length;
        return ((long) int_4(b, i) << Integer.SIZE) |
               (int_4(b, i + Integer.BYTES) & 0xFFFFFFFFL);
    }

    /**
     * Returns an array of bytes representing the specified {@code long} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     */
    static byte[] long_8(final long v) {
        return long_8(new byte[Long.BYTES], 0, v);
    }

    /**
     * Returns the {@code long} value represented by the specified array of bytes.
     *
     * @param b the array of bytes.
     * @return the value represented by the {@code b}.
     */
    static long long_8(final byte[] b) {
        return long_8(exactly(b, Long.BYTES), 0);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Writes the specified {@code char} value into the specified array of bytes, at the specified index.
     *
     * @param b the array of bytes to which the value is written.
     * @param i the index in the {@code b} at which the value is written.
     * @param v the value to write.
     * @return the specified {@code b}.
     */
    private static byte[] char_2(final byte[] b, final int i, final char v) {
        return short_2(b, i, (short) v);
    }

    /**
     * Reads a {@code char} value from the specified array of bytes, at the specified index.
     *
     * @param b the array of bytes from which the value is read.
     * @param i the index in the {@code b} from which the value is read.
     * @return the value read from the {@code b}.
     */
    private static char char_2(final byte[] b, final int i) {
        return (char) short_2(b, i);
    }

    /**
     * Returns an array of bytes representing the specified {@code char} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     */
    static byte[] char_2(final char v) {
        return char_2(new byte[Character.BYTES], 0, v);
    }

    /**
     * Returns the {@code char} value represented by the specified array of bytes.
     *
     * @param b the array of bytes.
     * @return the value represented by the {@code b}.
     */
    static char char_2(final byte[] b) {
        return char_2(exactly(b, Character.BYTES), 0);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns an array of bytes representing the specified {@code float} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     */
    static byte[] float_4(final float v) {
        return int_4(Float.floatToRawIntBits(v));
    }

    /**
     * Returns the {@code float} value represented by the specified array of bytes.
     *
     * @param b the array of bytes.
     * @return the value represented by the {@code b}.
     */
    static float float_4(final byte[] b) {
        return Float.intBitsToFloat(int_4(b)); // int_4(byte[]) checks the length
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns an array of bytes representing the specified {@code double} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     */
    static byte[] double_8(final double v) {
        return long_8(Double.doubleToRawLongBits(v));
    }

    /**
     * Returns the {@code double} value represented by the specified array of bytes.
     *
     * @param b the array of bytes.
     * @return the value represented by the {@code b}.
     */
    static double double_8(final byte[] b) {
        return Double.longBitsToDouble(long_8(b)); // long_8(byte[]) checks the length
    }

    // ------------------------------------------------------------------------------------------------ java.lang.String

    /**
     * Returns an array of bytes representing the specified {@code String} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     * @throws IllegalArgumentException when the {@code v} is not encodable as UTF-8; it holds an unpaired surrogate.
     */
    static byte[] string_(final String v) {
        assert v != null;
        try {
            // String.getBytes(UTF_8) would replace an unpaired surrogate with '?', storing something else, silently
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
                    "the string is not encodable as UTF-8 (an unpaired surrogate?), so it would not be read back as is",
                    cce);
        }
    }

    /**
     * Returns the {@code String} value represented by the specified array of bytes.
     *
     * @param v the array of bytes.
     * @return the value represented by the {@code v}.
     * @throws IllegalArgumentException when the {@code v} is not well-formed UTF-8.
     */
    static String string_(final byte[] v) {
        assert v != null;
        try {
            // new String(v, UTF_8) would replace a malformed sequence with U+FFFD, silently
            return StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)
                    .decode(ByteBuffer.wrap(v))
                    .toString();
        } catch (final CharacterCodingException cce) {
            throw new IllegalArgumentException("the bytes are not well-formed UTF-8", cce);
        }
    }

    // ------------------------------------------------------------------------------------------------------------ UUID

    /**
     * Writes the specified {@code UUID} value into the specified array of bytes, at the specified index.
     *
     * @param b the array of bytes to which the value is written.
     * @param i the index in the {@code b} at which the value is written.
     * @param v the value to write.
     * @return the specified {@code b}.
     */
    private static byte[] uuid_16(final byte[] b, final int i, final UUID v) {
        assert b != null;
        assert i >= 0;
        assert i + (Long.BYTES << 1) <= b.length;
        assert v != null;
        long_8(b, i, v.getMostSignificantBits());
        long_8(b, i + Long.BYTES, v.getLeastSignificantBits());
        return b;
    }

    /**
     * Reads a {@code UUID} value from the specified array of bytes, at the specified index.
     *
     * @param b the array of bytes from which the value is read.
     * @param i the index in the {@code b} from which the value is read.
     * @return the value read from the {@code b}.
     */
    private static UUID uuid_16(final byte[] b, final int i) {
        assert b != null;
        assert i >= 0;
        assert i + (Long.BYTES << 1) <= b.length;
        return new UUID(
                long_8(b, i),
                long_8(b, i + Long.BYTES)
        );
    }

    /**
     * Returns an array of bytes representing the specified {@code UUID} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     */
    static byte[] uuid_16(final UUID v) {
        return uuid_16(new byte[Long.BYTES << 1], 0, v);
    }

    /**
     * Returns the {@code UUID} value represented by the specified array of bytes.
     *
     * @param b the array of bytes.
     * @return the value represented by the {@code b}.
     */
    static UUID uuid_16(final byte[] b) {
        return uuid_16(exactly(b, Long.BYTES << 1), 0);
    }

    // ------------------------------------------------------------------------------------------------------- java.math

    /**
     * Returns an array of bytes representing the specified {@code BigInteger} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     */
    static byte[] big_integer_(final BigInteger v) {
        assert v != null;
        return v.toByteArray();
    }

    /**
     * Returns the {@code BigInteger} value represented by the specified array of bytes.
     *
     * @param b the array of bytes.
     * @return the value represented by the {@code b}.
     */
    static BigInteger big_integer_(final byte[] b) {
        return new BigInteger(b);
    }

    // ------------------------------------------------------------------------------------------------------- java.math

    /**
     * Returns an array of bytes representing the specified {@code BigDecimal} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     */
    static byte[] big_decimal_(final BigDecimal v) {
        assert v != null;
        final var encodedScale = int_4(v.scale());
        final var encodedUnscaledValue = big_integer_(v.unscaledValue());
        final var b = new byte[encodedScale.length + encodedUnscaledValue.length];
        System.arraycopy(encodedScale, 0, b, 0, encodedScale.length);
        System.arraycopy(encodedUnscaledValue, 0, b, encodedScale.length, encodedUnscaledValue.length);
        return b;
    }

    /**
     * Returns the {@code BigDecimal} value represented by the specified array of bytes.
     *
     * @param b the array of bytes.
     * @return the value represented by the {@code b}.
     */
    static BigDecimal big_decimal_(final byte[] b) {
        if (b.length < Integer.BYTES) {
            throw new IllegalArgumentException(
                    "a payload of " + b.length + " byte(s); expected at least " + Integer.BYTES + " for the scale");
        }
        final var scale = int_4(b, 0);
        final var unscaledValue = big_integer_(Arrays.copyOfRange(b, Integer.BYTES, b.length));
        return new BigDecimal(unscaledValue, scale);
    }

    // ------------------------------------------------------------------------------------------------------- java.time

    /**
     * Writes the specified {@code LocalDate} value into the specified array of bytes, at the specified index.
     *
     * @param b the array of bytes to which the value is written.
     * @param i the index in the {@code b} at which the value is written.
     * @param v the value to write.
     * @return the specified {@code b}.
     */
    private static byte[] local_date_8(final byte[] b, final int i, final LocalDate v) {
        assert v != null;
        assert i >= 0;
        assert i + Long.BYTES <= b.length;
        return long_8(b, i, v.toEpochDay());
    }

    /**
     * Reads a {@code LocalDate} value from the specified array of bytes, at the specified index.
     *
     * @param b the array of bytes from which the value is read.
     * @param i the index in the {@code b} from which the value is read.
     * @return the value read from the {@code b}.
     */
    private static LocalDate local_date_8(final byte[] b, final int i) {
        assert b != null;
        assert i >= 0;
        assert i + Long.BYTES <= b.length;
        return LocalDate.ofEpochDay(long_8(b, i));
    }

    /**
     * Returns an array of bytes representing the specified {@code LocalDate} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     */
    static byte[] local_date_8(final LocalDate v) {
        return local_date_8(new byte[Long.BYTES], 0, v);
    }

    /**
     * Returns the {@code LocalDate} value represented by the specified array of bytes.
     *
     * @param b the array of bytes.
     * @return the value represented by the {@code b}.
     */
    static LocalDate local_date_8(final byte[] b) {
        return local_date_8(exactly(b, Long.BYTES), 0);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Writes the specified {@code LocalTime} value into the specified array of bytes, at the specified index.
     *
     * @param b the array of bytes to which the value is written.
     * @param i the index in the {@code b} at which the value is written.
     * @param v the value to write.
     * @return the specified {@code b}.
     */
    private static byte[] local_time_8(final byte[] b, final int i, final LocalTime v) {
        return long_8(b, i, v.toNanoOfDay());
    }

    /**
     * Reads a {@code LocalTime} value from the specified array of bytes, at the specified index.
     *
     * @param b the array of bytes from which the value is read.
     * @param i the index in the {@code b} from which the value is read.
     * @return the value read from the {@code b}.
     */
    private static LocalTime local_time_8(final byte[] b, final int i) {
        return LocalTime.ofNanoOfDay(long_8(b, i));
    }

    /**
     * Returns an array of bytes representing the specified {@code LocalTime} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     */
    static byte[] local_time_8(final LocalTime v) {
        return local_time_8(new byte[Long.BYTES], 0, v);
    }

    /**
     * Returns the {@code LocalTime} value represented by the specified array of bytes.
     *
     * @param b the array of bytes.
     * @return the value represented by the {@code b}.
     */
    static LocalTime local_time_8(final byte[] b) {
        return LocalTime.ofNanoOfDay(long_8(exactly(b, Long.BYTES), 0));
    }

    // ----------------------------------------------------------------------------------------- java.time.LocalDateTime

    /**
     * Writes the specified {@code LocalDateTime} value into the specified array of bytes, at the specified index.
     *
     * @param b the array of bytes to which the value is written.
     * @param i the index in the {@code b} at which the value is written.
     * @param v the value to write.
     * @return the specified {@code b}.
     */
    private static byte[] local_date_time_16(final byte[] b, final int i, final LocalDateTime v) {
        assert b != null;
        assert i >= 0;
        assert i + (Long.BYTES << 1) <= b.length;
        assert v != null;
        local_date_8(b, i, v.toLocalDate());
        local_time_8(b, i + Long.BYTES, v.toLocalTime());
        return b;
    }

    /**
     * Reads a {@code LocalDateTime} value from the specified array of bytes, at the specified index.
     *
     * @param b the array of bytes from which the value is read.
     * @param i the index in the {@code b} from which the value is read.
     * @return the value read from the {@code b}.
     */
    private static LocalDateTime local_date_time_16(final byte[] b, final int i) {
        assert b != null;
        assert i >= 0;
        assert i + (Long.BYTES << 1) <= b.length;
        return LocalDateTime.of(
                local_date_8(b, i),
                local_time_8(b, i + Long.BYTES)
        );
    }

    /**
     * Returns an array of bytes representing the specified {@code LocalDateTime} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     */
    static byte[] local_date_time_16(final LocalDateTime v) {
        return local_date_time_16(new byte[Long.BYTES << 1], 0, v);
    }

    /**
     * Returns the {@code LocalDateTime} value represented by the specified array of bytes.
     *
     * @param b the array of bytes.
     * @return the value represented by the {@code b}.
     */
    static LocalDateTime local_date_time_16(final byte[] b) {
        return local_date_time_16(exactly(b, Long.BYTES << 1), 0);
    }

    // ------------------------------------------------------------------------------------------------ java.time.Offset

    /**
     * Writes the specified {@code ZoneOffset} value into the specified array of bytes, at the specified index.
     *
     * @param b the array of bytes to which the value is written.
     * @param i the index in the {@code b} at which the value is written.
     * @param v the value to write.
     * @return the specified {@code b}.
     */
    private static byte[] offset_4(final byte[] b, final int i, final ZoneOffset v) {
        assert v != null;
        return int_4(b, i, v.getTotalSeconds());
    }

    /**
     * Reads a {@code ZoneOffset} value from the specified array of bytes, at the specified index.
     *
     * @param b the array of bytes from which the value is read.
     * @param i the index in the {@code b} from which the value is read.
     * @return the value read from the {@code b}.
     */
    private static ZoneOffset offset_4(final byte[] b, final int i) {
        return ZoneOffset.ofTotalSeconds(int_4(b, i));
    }

    /**
     * Returns an array of bytes representing the specified {@code ZoneOffset} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     */
    static byte[] offset_4(final ZoneOffset v) {
        return offset_4(new byte[4], 0, v);
    }

    /**
     * Returns the {@code ZoneOffset} value represented by the specified array of bytes.
     *
     * @param b the array of bytes.
     * @return the value represented by the {@code b}.
     */
    static ZoneOffset offset_4(final byte[] b) {
        return offset_4(exactly(b, Integer.BYTES), 0);
    }

    // -------------------------------------------------------------------------------------------- java.time.OffsetTime

    /**
     * Writes the specified {@code OffsetTime} value into the specified array of bytes, at the specified index.
     *
     * @param b the array of bytes to which the value is written.
     * @param i the index in the {@code b} at which the value is written.
     * @param v the value to write.
     * @return the specified {@code b}.
     */
    private static byte[] offset_time_12(final byte[] b, final int i, final java.time.OffsetTime v) {
        return offset_4(
                local_time_8(
                        b,
                        i,
                        v.toLocalTime()
                ),
                i + Long.BYTES,
                v.getOffset()
        );
    }

    /**
     * Reads a {@code OffsetTime} value from the specified array of bytes, at the specified index.
     *
     * @param b the array of bytes from which the value is read.
     * @param i the index in the {@code b} from which the value is read.
     * @return the value read from the {@code b}.
     */
    private static java.time.OffsetTime offset_time_12(final byte[] b, final int i) {
        return OffsetTime.of(
                local_time_8(b, i),
                offset_4(b, i + Long.BYTES)
        );
    }

    /**
     * Returns an array of bytes representing the specified {@code OffsetTime} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     */
    static byte[] offset_time_12(final java.time.OffsetTime v) {
        return offset_time_12(new byte[12], 0, v);
    }

    /**
     * Returns the {@code OffsetTime} value represented by the specified array of bytes.
     *
     * @param b the array of bytes.
     * @return the value represented by the {@code b}.
     */
    static java.time.OffsetTime offset_time_12(final byte[] b) {
        return offset_time_12(exactly(b, Long.BYTES + Integer.BYTES), 0);
    }

    // ---------------------------------------------------------------------------------------- java.time.OffsetDateTime

    /**
     * Writes the specified {@code OffsetDateTime} value into the specified array of bytes, at the specified index.
     *
     * @param b the array of bytes to which the value is written.
     * @param i the index in the {@code b} at which the value is written.
     * @param v the value to write.
     * @return the specified {@code b}.
     */
    private static byte[] offset_date_time_20(final byte[] b, final int i, final OffsetDateTime v) {
        return offset_4(
                local_date_time_16(b, i, v.toLocalDateTime()),
                i + 16,
                v.getOffset()
        );
    }

    /**
     * Reads a {@code OffsetDateTime} value from the specified array of bytes, at the specified index.
     *
     * @param b the array of bytes from which the value is read.
     * @param i the index in the {@code b} from which the value is read.
     * @return the value read from the {@code b}.
     */
    private static OffsetDateTime offset_date_time_20(final byte[] b, final int i) {
        return OffsetDateTime.of(
                local_date_time_16(b, i),
                offset_4(b, i + 16)
        );
    }

    /**
     * Returns an array of bytes representing the specified {@code OffsetDateTime} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     */
    static byte[] offset_date_time_20(final OffsetDateTime v) {
        return offset_date_time_20(new byte[20], 0, v);
    }

    /**
     * Returns the {@code OffsetDateTime} value represented by the specified array of bytes.
     *
     * @param b the array of bytes.
     * @return the value represented by the {@code b}.
     */
    static OffsetDateTime offset_date_time_20(final byte[] b) {
        return offset_date_time_20(exactly(b, (Long.BYTES << 1) + Integer.BYTES), 0);
    }

    // ----------------------------------------------------------------------------------------------- java.time.Instant

    /**
     * Writes the specified {@code Instant} value into the specified array of bytes, at the specified index.
     *
     * @param b the array of bytes to which the value is written.
     * @param i the index in the {@code b} at which the value is written.
     * @param v the value to write.
     * @return the specified {@code b}.
     */
    private static byte[] instant_12(final byte[] b, final int i, final Instant v) {
        return int_4(
                long_8(b, i, v.getEpochSecond()),
                i + 8,
                v.getNano()
        );
    }

    /**
     * Reads a {@code Instant} value from the specified array of bytes, at the specified index.
     *
     * @param b the array of bytes from which the value is read.
     * @param i the index in the {@code b} from which the value is read.
     * @return the value read from the {@code b}.
     */
    private static Instant instant_12(final byte[] b, final int i) {
        return Instant.ofEpochSecond(
                long_8(b, i),
                int_4(b, i + 8)
        );
    }

    /**
     * Returns an array of bytes representing the specified {@code Instant} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     */
    static byte[] instant_12(final Instant v) {
        return instant_12(new byte[12], 0, v);
    }

    /**
     * Returns the {@code Instant} value represented by the specified array of bytes.
     *
     * @param b the array of bytes.
     * @return the value represented by the {@code b}.
     */
    static Instant instant_12(final byte[] b) {
        return instant_12(exactly(b, Long.BYTES + Integer.BYTES), 0);
    }

    // -------------------------------------------------------------------------------------------------- java.time.Year

    /**
     * Writes the specified {@code Year} value into the specified array of bytes, at the specified index.
     *
     * @param b the array of bytes to which the value is written.
     * @param i the index in the {@code b} at which the value is written.
     * @param v the value to write.
     * @return the specified {@code b}.
     */
    private static byte[] year_4(final byte[] b, final int i, final Year v) {
        return int_4(b, i, v.getValue());
    }

    /**
     * Reads a {@code Year} value from the specified array of bytes, at the specified index.
     *
     * @param b the array of bytes from which the value is read.
     * @param i the index in the {@code b} from which the value is read.
     * @return the value read from the {@code b}.
     */
    private static Year year_4(final byte[] b, final int i) {
        return Year.of(int_4(b, i));
    }

    /**
     * Returns an array of bytes representing the specified {@code Year} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     */
    static byte[] year_4(final Year v) {
        assert v != null;
        return year_4(new byte[4], 0, v);
    }

    /**
     * Returns the {@code Year} value represented by the specified array of bytes.
     *
     * @param b the array of bytes.
     * @return the value represented by the {@code b}.
     */
    static Year year_4(final byte[] b) {
        return year_4(exactly(b, Integer.BYTES), 0);
    }

    // -------------------------------------------------------------------------------------------------- java.util.Date

    /**
     * Writes the specified {@code Date} value into the specified array of bytes, at the specified index.
     *
     * @param b the array of bytes to which the value is written.
     * @param i the index in the {@code b} at which the value is written.
     * @param v the value to write.
     * @return the specified {@code b}.
     * @see <a href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#deprecations">
     *         Jakarta Persistence 3.2, A.1.1. Deprecations</a>
     * @deprecated Jakarta Persistence 3.2 deprecates the use of {@link java.util.Date} in new applications in favor of
     *         the {@code java.time} API.
     */
    @Deprecated
    private static byte[] util_date_8(final byte[] b, final int i, final java.util.Date v) {
        assert b != null;
        assert i >= 0;
        assert i + 8 <= b.length;
        assert v != null;
        return long_8(b, i, v.getTime());
    }

    /**
     * Reads a {@code Date} value from the specified array of bytes, at the specified index.
     *
     * @param b the array of bytes from which the value is read.
     * @param i the index in the {@code b} from which the value is read.
     * @return the value read from the {@code b}.
     * @see <a href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#deprecations">
     *         Jakarta Persistence 3.2, A.1.1. Deprecations</a>
     * @deprecated Jakarta Persistence 3.2 deprecates the use of {@link java.util.Date} in new applications in favor of
     *         the {@code java.time} API.
     */
    @Deprecated
    private static java.util.Date util_date_8(final byte[] b, final int i) {
        assert b != null;
        assert i >= 0;
        assert i + 8 <= b.length;
        return new java.util.Date(long_8(b, i));
    }

    /**
     * Returns an array of bytes representing the specified {@code Date} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     * @see <a href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#deprecations">
     *         Jakarta Persistence 3.2, A.1.1. Deprecations</a>
     * @deprecated Jakarta Persistence 3.2 deprecates the use of {@link java.util.Date} in new applications in favor of
     *         the {@code java.time} API.
     */
    @Deprecated
    static byte[] util_date_8(final java.util.Date v) {
        return util_date_8(new byte[8], 0, v);
    }

    /**
     * Returns the {@code Date} value represented by the specified array of bytes.
     *
     * @param b the array of bytes.
     * @return the value represented by the {@code b}.
     * @see <a href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#deprecations">
     *         Jakarta Persistence 3.2, A.1.1. Deprecations</a>
     * @deprecated Jakarta Persistence 3.2 deprecates the use of {@link java.util.Date} in new applications in favor of
     *         the {@code java.time} API.
     */
    @Deprecated
    static java.util.Date util_date_8(final byte[] b) {
        return util_date_8(exactly(b, Long.BYTES), 0);
    }

    // ---------------------------------------------------------------------------------------------- java.util.Calendar

    /**
     * Writes the specified {@code Calendar} value into the specified array of bytes, at the specified index.
     *
     * @param b the array of bytes to which the value is written.
     * @param i the index in the {@code b} at which the value is written.
     * @param v the value to write.
     * @return the specified {@code b}.
     * @see <a href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#deprecations">
     *         Jakarta Persistence 3.2, A.1.1. Deprecations</a>
     * @deprecated Jakarta Persistence 3.2 deprecates the use of {@link java.util.Calendar} in new applications in favor
     *         of the {@code java.time} API.
     */
    @Deprecated
    private static byte[] util_calendar_8(final byte[] b, final int i, final Calendar v) {
        assert b != null;
        assert i >= 0;
        assert i + 8 <= b.length;
        assert v != null;
        return util_date_8(b, i, v.getTime());
    }

    /**
     * Reads a {@code Calendar} value from the specified array of bytes, at the specified index.
     *
     * @param b the array of bytes from which the value is read.
     * @param i the index in the {@code b} from which the value is read.
     * @return the value read from the {@code b}.
     * @see <a href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#deprecations">
     *         Jakarta Persistence 3.2, A.1.1. Deprecations</a>
     * @deprecated Jakarta Persistence 3.2 deprecates the use of {@link java.util.Calendar} in new applications in favor
     *         of the {@code java.time} API.
     */
    @Deprecated
    private static Calendar util_calendar_8(final byte[] b, final int i) {
        assert b != null;
        assert i >= 0;
        assert i + 8 <= b.length;
        final var v = Calendar.getInstance();
        v.setTime(util_date_8(b, i));
        return v;
    }

    /**
     * Returns an array of bytes representing the specified {@code Calendar} value.
     * <p>
     * Only the instant is stored; the calendar's time zone, locale-dependent settings and calendar system are not.
     * That is the same value a plain, unencrypted {@code Calendar} mapping persists: a {@code TIMESTAMP} column holds
     * no zone either, and a provider reads it back as a {@code Calendar} in the default time zone. Storing the epoch
     * millis also keeps the instant independent of the JVM's, or the JDBC driver's, time zone.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}; its epoch millis.
     * @see #util_calendar_8(byte[])
     * @see <a href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#deprecations">
     *         Jakarta Persistence 3.2, A.1.1. Deprecations</a>
     * @deprecated Jakarta Persistence 3.2 deprecates the use of {@link java.util.Calendar} in new applications in favor
     *         of the {@code java.time} API.
     */
    @Deprecated
    static byte[] util_calendar_8(final Calendar v) {
        return util_calendar_8(new byte[8], 0, v);
    }

    /**
     * Returns the {@code Calendar} value represented by the specified array of bytes.
     *
     * <p>
     * The returned calendar is in the default time zone, with the default locale's settings, as a provider's own
     * reading of a plain {@code Calendar} column is.
     *
     * @param b the array of bytes.
     * @return the value represented by the {@code b}.
     * @see <a href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#deprecations">
     *         Jakarta Persistence 3.2, A.1.1. Deprecations</a>
     * @deprecated Jakarta Persistence 3.2 deprecates the use of {@link java.util.Calendar} in new applications in favor
     *         of the {@code java.time} API.
     */
    @Deprecated
    static Calendar util_calendar_8(final byte[] b) {
        return util_calendar_8(exactly(b, Long.BYTES), 0);
    }

    // --------------------------------------------------------------------------------------------------- java.sql.Date

    /**
     * Writes the specified {@code Date} value into the specified array of bytes, at the specified index.
     *
     * @param b the array of bytes to which the value is written.
     * @param i the index in the {@code b} at which the value is written.
     * @param v the value to write.
     * @return the specified {@code b}.
     * @see <a href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#deprecations">
     *         Jakarta Persistence 3.2, A.1.1. Deprecations</a>
     * @deprecated Jakarta Persistence 3.2 deprecates the use of {@link java.sql.Date} in new applications in favor of
     *         the {@code java.time} API.
     */
    @Deprecated
    private static byte[] sql_date_8(final byte[] b, final int i, final java.sql.Date v) {
        return long_8(b, i, v.getTime());
    }

    /**
     * Reads a {@code Date} value from the specified array of bytes, at the specified index.
     *
     * @param b the array of bytes from which the value is read.
     * @param i the index in the {@code b} from which the value is read.
     * @return the value read from the {@code b}.
     * @see <a href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#deprecations">
     *         Jakarta Persistence 3.2, A.1.1. Deprecations</a>
     * @deprecated Jakarta Persistence 3.2 deprecates the use of {@link java.sql.Date} in new applications in favor of
     *         the {@code java.time} API.
     */
    @Deprecated
    private static java.sql.Date sql_date_8(final byte[] b, final int i) {
        return new java.sql.Date(long_8(b, i));
    }

    /**
     * Returns an array of bytes representing the specified {@code Date} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     * @see <a href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#deprecations">
     *         Jakarta Persistence 3.2, A.1.1. Deprecations</a>
     * @deprecated Jakarta Persistence 3.2 deprecates the use of {@link java.sql.Date} in new applications in favor of
     *         the {@code java.time} API.
     */
    @Deprecated
    static byte[] sql_date_8(final java.sql.Date v) {
        return sql_date_8(new byte[8], 0, v);
    }

    /**
     * Returns the {@code Date} value represented by the specified array of bytes.
     *
     * @param b the array of bytes.
     * @return the value represented by the {@code b}.
     * @see <a href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#deprecations">
     *         Jakarta Persistence 3.2, A.1.1. Deprecations</a>
     * @deprecated Jakarta Persistence 3.2 deprecates the use of {@link java.sql.Date} in new applications in favor of
     *         the {@code java.time} API.
     */
    @Deprecated
    static java.sql.Date sql_date_8(final byte[] b) {
        return sql_date_8(exactly(b, Long.BYTES), 0);
    }

    // --------------------------------------------------------------------------------------------------- java.sql.Time

    /**
     * Writes the specified {@code Time} value into the specified array of bytes, at the specified index.
     *
     * @param b the array of bytes to which the value is written.
     * @param i the index in the {@code b} at which the value is written.
     * @param v the value to write.
     * @return the specified {@code b}.
     * @see <a href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#deprecations">
     *         Jakarta Persistence 3.2, A.1.1. Deprecations</a>
     * @deprecated Jakarta Persistence 3.2 deprecates the use of {@link java.sql.Time} in new applications in favor of
     *         the {@code java.time} API.
     */
    @Deprecated
    private static byte[] sql_time_8(final byte[] b, final int i, final java.sql.Time v) {
        return long_8(b, i, v.getTime());
    }

    /**
     * Reads a {@code Time} value from the specified array of bytes, at the specified index.
     *
     * @param b the array of bytes from which the value is read.
     * @param i the index in the {@code b} from which the value is read.
     * @return the value read from the {@code b}.
     * @see <a href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#deprecations">
     *         Jakarta Persistence 3.2, A.1.1. Deprecations</a>
     * @deprecated Jakarta Persistence 3.2 deprecates the use of {@link java.sql.Time} in new applications in favor of
     *         the {@code java.time} API.
     */
    @Deprecated
    private static java.sql.Time sql_time_8(final byte[] b, final int i) {
        return new java.sql.Time(long_8(b, i));
    }

    /**
     * Returns an array of bytes representing the specified {@code Time} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     * @see <a href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#deprecations">
     *         Jakarta Persistence 3.2, A.1.1. Deprecations</a>
     * @deprecated Jakarta Persistence 3.2 deprecates the use of {@link java.sql.Time} in new applications in favor of
     *         the {@code java.time} API.
     */
    @Deprecated
    static byte[] sql_time_8(final java.sql.Time v) {
        return sql_time_8(new byte[8], 0, v);
    }

    /**
     * Returns the {@code Time} value represented by the specified array of bytes.
     *
     * @param b the array of bytes.
     * @return the value represented by the {@code b}.
     * @see <a href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#deprecations">
     *         Jakarta Persistence 3.2, A.1.1. Deprecations</a>
     * @deprecated Jakarta Persistence 3.2 deprecates the use of {@link java.sql.Time} in new applications in favor of
     *         the {@code java.time} API.
     */
    @Deprecated
    static java.sql.Time sql_time_8(final byte[] b) {
        return sql_time_8(exactly(b, Long.BYTES), 0);
    }

    // ---------------------------------------------------------------------------------------------- java.sql.Timestamp

    /**
     * Writes the specified {@code Timestamp} value into the specified array of bytes, at the specified index.
     *
     * @param b the array of bytes to which the value is written.
     * @param i the index in the {@code b} at which the value is written.
     * @param v the value to write.
     * @return the specified {@code b}.
     * @see <a href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#deprecations">
     *         Jakarta Persistence 3.2, A.1.1. Deprecations</a>
     * @deprecated Jakarta Persistence 3.2 deprecates the use of {@link java.sql.Timestamp} in new applications in favor
     *         of the {@code java.time} API.
     */
    @Deprecated
    private static byte[] sql_timestamp_12(final byte[] b, final int i, final java.sql.Timestamp v) {
        assert b != null;
        assert i >= 0;
        assert i + Long.BYTES + Integer.BYTES <= b.length;
        assert v != null;
        return instant_12(b, i, v.toInstant());
    }

    /**
     * Reads a {@code Timestamp} value from the specified array of bytes, at the specified index.
     *
     * @param b the array of bytes from which the value is read.
     * @param i the index in the {@code b} from which the value is read.
     * @return the value read from the {@code b}.
     * @see <a href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#deprecations">
     *         Jakarta Persistence 3.2, A.1.1. Deprecations</a>
     * @deprecated Jakarta Persistence 3.2 deprecates the use of {@link java.sql.Timestamp} in new applications in favor
     *         of the {@code java.time} API.
     */
    @Deprecated
    private static java.sql.Timestamp sql_timestamp_12(final byte[] b, final int i) {
        assert b != null;
        assert i >= 0;
        assert i + Long.BYTES + Integer.BYTES <= b.length;
        return Timestamp.from(instant_12(b, i));
    }

    /**
     * Returns an array of bytes representing the specified {@code Timestamp} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     * @see <a href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#deprecations">
     *         Jakarta Persistence 3.2, A.1.1. Deprecations</a>
     * @deprecated Jakarta Persistence 3.2 deprecates the use of {@link java.sql.Timestamp} in new applications in favor
     *         of the {@code java.time} API.
     */
    @Deprecated
    static byte[] sql_timestamp_12(final java.sql.Timestamp v) {
        return sql_timestamp_12(new byte[Long.BYTES + Integer.BYTES], 0, v);
    }

    /**
     * Returns the {@code Timestamp} value represented by the specified array of bytes.
     *
     * @param b the array of bytes.
     * @return the value represented by the {@code b}.
     * @see <a href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#deprecations">
     *         Jakarta Persistence 3.2, A.1.1. Deprecations</a>
     * @deprecated Jakarta Persistence 3.2 deprecates the use of {@link java.sql.Timestamp} in new applications in favor
     *         of the {@code java.time} API.
     */
    @Deprecated
    static java.sql.Timestamp sql_timestamp_12(final byte[] b) {
        return sql_timestamp_12(exactly(b, Long.BYTES + Integer.BYTES), 0);
    }

    // ---------------------------------------------------------------------------------------------------------- byte[]

    // ---------------------------------------------------------------------------------------------------------- Byte[]

    /**
     * Returns an array of bytes representing the specified {@code Byte[]} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     * @see <a href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#deprecations">
     *         Jakarta Persistence 3.2, A.1.1. Deprecations</a>
     * @deprecated Jakarta Persistence 3.2 deprecates the use of {@code Byte[]} for basic attributes in favor of
     *         {@code byte[]}.
     */
    @Deprecated
    static byte[] Bytes_l(final Byte[] v) {
        final var p = new byte[v.length];
        for (int i = 0; i < p.length; i++) {
            p[i] = v[i];
        }
        return p;
    }

    /**
     * Returns the {@code Byte[]} value represented by the specified array of bytes.
     *
     * @param b the array of bytes.
     * @return the value represented by the {@code b}.
     * @see <a href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#deprecations">
     *         Jakarta Persistence 3.2, A.1.1. Deprecations</a>
     * @deprecated Jakarta Persistence 3.2 deprecates the use of {@code Byte[]} for basic attributes in favor of
     *         {@code byte[]}.
     */
    @Deprecated
    static Byte[] Bytes_l(final byte[] b) {
        final var v = new Byte[b.length];
        for (int i = 0; i < v.length; i++) {
            v[i] = b[i];
        }
        return v;
    }

    // ---------------------------------------------------------------------------------------------------------- char[]

    /**
     * Writes the specified {@code char[]} value into the specified array of bytes, at the specified index.
     *
     * @param b the array of bytes to which the value is written.
     * @param i the index in the {@code b} at which the value is written.
     * @param v the value to write.
     * @return the specified {@code b}.
     */
    private static byte[] chars_2l(final byte[] b, int i, final char[] v) {
        assert b != null;
        assert i >= 0;
        assert v != null;
        assert i + (v.length << 1) <= b.length;
        for (int j = 0; j < v.length; j++) {
            b[i++] = (byte) (v[j] >> Byte.SIZE);
            b[i++] = (byte) (v[j] & 0xFF);
        }
        return b;
    }

    /**
     * Reads a {@code char[]} value from the specified array of bytes, at the specified index.
     *
     * @param b the array of bytes from which the value is read.
     * @param i the index in the {@code b} from which the value is read.
     * @return the value read from the {@code b}.
     */
    private static char[] chars_2l(final byte[] b, int i) {
        assert b != null;
        assert i >= 0;
        if (((b.length - i) & 1) != 0) {
            // an assertion would be disabled in production, and the payload would silently lose its last byte
            throw new IllegalArgumentException(
                    "odd number of bytes for a char[]; b.length: " + b.length + ", i: " + i);
        }
        final var v = new char[(b.length - i) >> 1];
        for (int j = 0; j < v.length; j++) {
            v[j] = (char) (
                    (b[i++] << Byte.SIZE)
                    | (b[i++] & 0xFF)
            );
        }
        return v;
    }

    /**
     * Returns an array of bytes representing the specified {@code char[]} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     */
    static byte[] chars_2l(final char[] v) {
        return chars_2l(new byte[v.length << 1], 0, v);
    }

    /**
     * Returns the {@code char[]} value represented by the specified array of bytes.
     *
     * @param b the array of bytes.
     * @return the value represented by the {@code b}.
     */
    static char[] chars_2l(final byte[] b) {
        return chars_2l(b, 0);
    }

    // ----------------------------------------------------------------------------------------------------- Character[]

    /**
     * Returns an array of bytes representing the specified {@code Character[]} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     */
    static byte[] Characters_2l(final Character[] v) {
        final var p = new char[v.length];
        for (int i = 0; i < p.length; i++) {
            p[i] = v[i];
        }
        return chars_2l(p);
    }

    /**
     * Returns the {@code Character[]} value represented by the specified array of bytes.
     *
     * @param b the array of bytes.
     * @return the value represented by the {@code b}.
     */
    static Character[] Characters_2l(final byte[] b) {
        final var p = chars_2l(b);
        final var v = new Character[p.length];
        for (int i = 0; i < v.length; i++) {
            v[i] = p[i];
        }
        return v;
    }

    // ------------------------------------------------------------------------------------------------------------ enum

    /**
     * Returns an array of bytes representing the specified {@code Enum<?>} value.
     * <p>
     * The constant is stored by its {@link Enum#name() name}, as {@code @Enumerated(EnumType.STRING)} stores it, never by
     * its ordinal: reordering, or inserting, constants is common, and would silently map every stored row to another
     * constant. Renaming a constant, by contrast, is a breaking change: the stored name no longer resolves, and the read
     * fails loudly, naming the attribute.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     */
    static byte[] enum_(final Enum<?> v) {
        assert v != null;
        final var name = v.name();
        return string_(name);
    }

    /**
     * Returns the constant, of the specified enum class, represented by the specified array of bytes.
     *
     * @param b         the array of bytes, holding the constant's {@link Enum#name() name}.
     * @param enumClass the enum class.
     * @param <E>       enum type parameter
     * @return the enum constant represented by the {@code b}.
     */
    static <E extends Enum<E>> E enum_(final byte[] b, final Class<E> enumClass) {
        assert b != null;
        assert b.length > 0;
        assert enumClass != null;
        final var name = string_(b);
        return Enum.valueOf(enumClass, name);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns an array of bytes representing the specified {@code Serializable} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}; its Java serialization.
     * @implNote Nothing is limited here, as nothing is limited by a plain {@code Serializable} mapping: the size of the
     *         value, and of the column holding it, is the application's concern.
     */
    static byte[] serializable_(final Serializable v) {
        assert v != null;
        try (var baos = new ByteArrayOutputStream();
             var oos = new ObjectOutputStream(baos)) {
            oos.writeObject(v);
            oos.flush();
            return baos.toByteArray();
        } catch (final IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    /**
     * Returns the {@code T} value represented by the specified array of bytes.
     *
     * @param b            the array of bytes.
     * @param expectedType the type the deserialized value has to be an instance of.
     * @return the value represented by the {@code b}.
     * @throws ClassCastException when the {@code b} holds anything but an {@code expectedType}.
     * @implNote No filter of this module's own is installed, so the stream is subject to the JVM-wide
     *         {@code jdk.serialFilter} (or filter factory), exactly as a plain {@code Serializable} mapping's
     *         deserialization is. Which classes may be deserialized, and how large a graph may be, is the
     *         application's to configure there.
     * @see <a href="https://docs.oracle.com/en/java/javase/21/core/serialization-filtering1.html">Serialization
     *         Filtering</a>
     */
    static Serializable serializable_(final byte[] b, final Class<?> expectedType) {
        assert b != null;
        assert b.length > 0;
        assert expectedType != null;
        try (var bais = new ByteArrayInputStream(b);
             var ois = new ObjectInputStream(bais)) {
            try {
                return (Serializable) expectedType.cast(ois.readObject());
            } catch (final ClassNotFoundException cnfe) {
                throw new RuntimeException(cnfe);
            }
        } catch (final IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    // -------------------------------------------------------------------------------------------------- payload frame

    /**
     * The codecs of this class, each with the id written into a payload's header.
     * <p>
     * An id is stored data: never renumber one, and never reuse a retired one.
     */
    enum Codec {

        /**
         * The codec of {@code boolean} and {@link Boolean}; {@code boolean_1}.
         */
        BOOLEAN_1(1),

        /**
         * The codec of {@code byte} and {@link Byte}; {@code byte_1}.
         */
        BYTE_1(2),

        /**
         * The codec of {@code short} and {@link Short}; {@code short_2}.
         */
        SHORT_2(3),

        /**
         * The codec of {@code int} and {@link Integer}; {@code int_4}.
         */
        INT_4(4),

        /**
         * The codec of {@code long} and {@link Long}; {@code long_8}.
         */
        LONG_8(5),

        /**
         * The codec of {@code char} and {@link Character}; {@code char_2}.
         */
        CHAR_2(6),

        /**
         * The codec of {@code float} and {@link Float}; {@code float_4}.
         */
        FLOAT_4(7),

        /**
         * The codec of {@code double} and {@link Double}; {@code double_8}.
         */
        DOUBLE_8(8),

        /**
         * The codec of {@link String}; {@code string_}.
         */
        STRING_(9),

        /**
         * The codec of {@link UUID}; {@code uuid_16}.
         */
        UUID_16(10),

        /**
         * The codec of {@link BigInteger}; {@code big_integer_}.
         */
        BIG_INTEGER_(11),

        /**
         * The codec of {@link BigDecimal}; {@code big_decimal_}.
         */
        BIG_DECIMAL_(12),

        /**
         * The codec of {@link LocalDate}; {@code local_date_8}.
         */
        LOCAL_DATE_8(13),

        /**
         * The codec of {@link LocalTime}; {@code local_time_8}.
         */
        LOCAL_TIME_8(14),

        /**
         * The codec of {@link LocalDateTime}; {@code local_date_time_16}.
         */
        LOCAL_DATE_TIME_16(15),

        /**
         * The codec of {@link OffsetTime}; {@code offset_time_12}.
         */
        OFFSET_TIME_12(16),

        /**
         * The codec of {@link OffsetDateTime}; {@code offset_date_time_20}.
         */
        OFFSET_DATE_TIME_20(17),

        /**
         * The codec of {@link Instant}; {@code instant_12}.
         */
        INSTANT_12(18),

        /**
         * The codec of {@link Year}; {@code year_4}.
         */
        YEAR_4(19),

        /**
         * The codec of {@link java.sql.Timestamp}; {@code sql_timestamp_12}.
         */
        SQL_TIMESTAMP_12(20),

        /**
         * The codec of {@link java.sql.Date}; {@code sql_date_8}.
         */
        SQL_DATE_8(21),

        /**
         * The codec of {@link java.sql.Time}; {@code sql_time_8}.
         */
        SQL_TIME_8(22),

        /**
         * The codec of {@link Calendar} and its subclasses; {@code util_calendar_8}.
         */
        UTIL_CALENDAR_8(23),

        /**
         * The codec of {@link java.util.Date} and its other subclasses; {@code util_date_8}.
         */
        UTIL_DATE_8(24),

        /**
         * The codec of {@code byte[]}, as is.
         */
        BYTES_L(25),

        /**
         * The codec of {@code Byte[]}; {@code Bytes_l}.
         */
        BOXED_BYTES_L(26),

        /**
         * The codec of {@code char[]}; {@code chars_2l}.
         */
        CHARS_2L(27),

        /**
         * The codec of {@code Character[]}; {@code Characters_2l}.
         */
        CHARACTERS_2L(28),

        /**
         * The codec of an enum, by its name; {@code enum_}.
         */
        ENUM_(29),

        /**
         * The codec of anything else {@link Serializable}; {@code serializable_}.
         */
        SERIALIZABLE_(30);

        /**
         * Returns the codec with the specified id.
         *
         * @param id the id.
         * @return the codec with the {@code id}; {@code null} when there is none.
         */
        static @Nullable Codec of(final byte id) {
            for (final var codec : values()) {
                if (codec.id == id) {
                    return codec;
                }
            }
            return null;
        }

        /**
         * Creates a new constant with the specified id.
         *
         * @param id the id written into a payload's header; between {@code 1} and {@link Byte#MAX_VALUE}.
         */
        Codec(final int id) {
            assert id > 0 && id <= Byte.MAX_VALUE;
            this.id = (byte) id;
        }

        /**
         * The id of this codec, written into a payload's header. Stored data: never renumbered, never reused.
         */
        final byte id;
    }

    /**
     * Returns the codec which encodes, and decodes, a value of the specified declared java type.
     *
     * @param javaType the declared java type of an attribute.
     * @return the codec for the {@code javaType}; {@code null} when the type is not supported.
     * @implNote The order of the checks is the order of the codec ladders in {@link AbstractEntityEncryptionService}:
     *         {@code java.sql} types before {@link java.util.Date}, and {@link Serializable} last.
     */
    static @Nullable Codec codecOf(final Class<?> javaType) {
        if (javaType == boolean.class || javaType == Boolean.class) {
            return Codec.BOOLEAN_1;
        } else if (javaType == byte.class || javaType == Byte.class) {
            return Codec.BYTE_1;
        } else if (javaType == short.class || javaType == Short.class) {
            return Codec.SHORT_2;
        } else if (javaType == int.class || javaType == Integer.class) {
            return Codec.INT_4;
        } else if (javaType == long.class || javaType == Long.class) {
            return Codec.LONG_8;
        } else if (javaType == char.class || javaType == Character.class) {
            return Codec.CHAR_2;
        } else if (javaType == float.class || javaType == Float.class) {
            return Codec.FLOAT_4;
        } else if (javaType == double.class || javaType == Double.class) {
            return Codec.DOUBLE_8;
        } else if (javaType == String.class) {
            return Codec.STRING_;
        } else if (javaType == UUID.class) {
            return Codec.UUID_16;
        } else if (javaType == BigInteger.class) {
            return Codec.BIG_INTEGER_;
        } else if (javaType == BigDecimal.class) {
            return Codec.BIG_DECIMAL_;
        } else if (javaType == LocalDate.class) {
            return Codec.LOCAL_DATE_8;
        } else if (javaType == LocalTime.class) {
            return Codec.LOCAL_TIME_8;
        } else if (javaType == LocalDateTime.class) {
            return Codec.LOCAL_DATE_TIME_16;
        } else if (javaType == OffsetTime.class) {
            return Codec.OFFSET_TIME_12;
        } else if (javaType == OffsetDateTime.class) {
            return Codec.OFFSET_DATE_TIME_20;
        } else if (javaType == Instant.class) {
            return Codec.INSTANT_12;
        } else if (javaType == Year.class) {
            return Codec.YEAR_4;
        } else if (javaType == java.sql.Timestamp.class) {
            return Codec.SQL_TIMESTAMP_12;
        } else if (javaType == java.sql.Date.class) {
            return Codec.SQL_DATE_8;
        } else if (javaType == java.sql.Time.class) {
            return Codec.SQL_TIME_8;
        } else if (Calendar.class.isAssignableFrom(javaType)) {
            return Codec.UTIL_CALENDAR_8;
        } else if (java.util.Date.class.isAssignableFrom(javaType)) {
            return Codec.UTIL_DATE_8;
        } else if (javaType == byte[].class) {
            return Codec.BYTES_L;
        } else if (javaType == Byte[].class) {
            return Codec.BOXED_BYTES_L;
        } else if (javaType == char[].class) {
            return Codec.CHARS_2L;
        } else if (javaType == Character[].class) {
            return Codec.CHARACTERS_2L;
        } else if (javaType.isEnum()) {
            return Codec.ENUM_;
        } else if (Serializable.class.isAssignableFrom(javaType)) {
            return Codec.SERIALIZABLE_;
        }
        return null;
    }

    /**
     * The version of the payload format this class writes, and the only one it reads. The value is
     * {@value #FORMAT_VERSION}.
     */
    static final byte FORMAT_VERSION = 1;

    /**
     * The length of a payload's header: the {@link #FORMAT_VERSION format version}, then the {@link Codec#id codec
     * id}. The value is {@value #HEADER_BYTES}.
     */
    static final int HEADER_BYTES = 2;

    /**
     * Returns the specified encoded value prefixed with a header: the {@link #FORMAT_VERSION format version}, and the
     * id of the codec which encoded it.
     *
     * @param codec   the codec which encoded the {@code encoded}.
     * @param encoded the encoded value.
     * @return a new array: the header followed by the {@code encoded}.
     */
    static byte[] frame(final Codec codec, final byte[] encoded) {
        final var framed = new byte[HEADER_BYTES + encoded.length];
        framed[0] = FORMAT_VERSION;
        framed[1] = codec.id;
        System.arraycopy(encoded, 0, framed, HEADER_BYTES, encoded.length);
        return framed;
    }

    /**
     * Returns the encoded value of the specified framed payload, after checking its header against the specified
     * expected codec.
     *
     * @param expected the codec the reader decodes with; the one of the attribute's declared type.
     * @param framed   the framed payload.
     * @return a new array holding the encoded value.
     * @throws IllegalArgumentException when the {@code framed} is shorter than a header, was written in another
     *                                  format version, or was encoded by a codec other than the {@code expected}.
     */
    static byte[] unframe(final Codec expected, final byte[] framed) {
        if (framed.length < HEADER_BYTES) {
            throw new IllegalArgumentException(
                    "no payload header; bytes: " + framed.length + "; expected at least " + HEADER_BYTES);
        }
        if (framed[0] != FORMAT_VERSION) {
            throw new IllegalArgumentException(
                    "unknown payload format version: " + (framed[0] & 0xFF) + "; supported: " + FORMAT_VERSION);
        }
        if (framed[1] != expected.id) {
            final var actual = Codec.of(framed[1]);
            // the declared type of the attribute changed after the row was written, or the bytes belong elsewhere
            throw new IllegalArgumentException(
                    "the payload was encoded by " + (actual == null ? "an unknown codec (" + (framed[1] & 0xFF) + ")" : actual)
                    + ", but the attribute is decoded by " + expected);
        }
        return Arrays.copyOfRange(framed, HEADER_BYTES, framed.length);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private EntityEncryptionServiceUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
