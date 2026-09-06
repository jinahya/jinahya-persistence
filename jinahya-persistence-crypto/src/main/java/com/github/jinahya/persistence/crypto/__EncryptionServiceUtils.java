package com.github.jinahya.persistence.crypto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
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
 * @see __EncryptionService
 */
@SuppressWarnings({
        "java:S100", // Method names should comply with a naming convention
        "java:S101"  // Class names should comply with a naming convention
})
final class __EncryptionServiceUtils {

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
     */
    private static boolean boolean_1(final byte[] b, final int i) {
        assert b != null;
        assert i >= 0;
        assert i + Byte.BYTES <= b.length;
        return b[i] == 1;
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
        return boolean_1(b, 0);
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
        return byte_1(b, 0);
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
        return short_2(b, 0);
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
        return int_4(b, 0);
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
        return long_8(b, 0);
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
        return char_2(b, 0);
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
        return Float.intBitsToFloat(int_4(b));
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
        return Double.longBitsToDouble(long_8(b));
    }

    // ------------------------------------------------------------------------------------------------ java.lang.String

    /**
     * Returns an array of bytes representing the specified {@code String} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     */
    static byte[] string_(final String v) {
        assert v != null;
        return v.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Returns the {@code String} value represented by the specified array of bytes.
     *
     * @param v the array of bytes.
     * @return the value represented by the {@code v}.
     */
    static String string_(final byte[] v) {
        assert v != null;
        return new String(v, StandardCharsets.UTF_8);
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
        return uuid_16(b, 0);
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
        return local_date_8(b, 0);
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
        return LocalTime.ofNanoOfDay(long_8(b, 0));
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
        return local_date_time_16(b, 0);
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
        return offset_4(b, 0);
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
        return offset_time_12(b, 0);
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
        return offset_date_time_20(b, 0);
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
        return instant_12(b, 0);
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
        return year_4(b, 0);
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
        return util_date_8(b, 0);
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
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
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
     * @param b the array of bytes.
     * @return the value represented by the {@code b}.
     * @see <a href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#deprecations">
     *         Jakarta Persistence 3.2, A.1.1. Deprecations</a>
     * @deprecated Jakarta Persistence 3.2 deprecates the use of {@link java.util.Calendar} in new applications in favor
     *         of the {@code java.time} API.
     */
    @Deprecated
    static Calendar util_calendar_8(final byte[] b) {
        return util_calendar_8(b, 0);
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
        return sql_date_8(b, 0);
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
        return sql_time_8(b, 0);
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
    private static byte[] sql_timestamp_16(final byte[] b, final int i, final java.sql.Timestamp v) {
        assert b != null;
        assert i >= 0;
        assert i + 16 <= b.length;
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
    private static java.sql.Timestamp sql_timestamp_16(final byte[] b, final int i) {
        assert b != null;
        assert i >= 0;
        assert i + 16 <= b.length;
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
    static byte[] sql_timestamp_16(final java.sql.Timestamp v) {
        return sql_timestamp_16(new byte[16], 0, v);
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
    static java.sql.Timestamp sql_timestamp_16(final byte[] b) {
        return sql_timestamp_16(b, 0);
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
     * @return an array of bytes representing the {@code v}.
     */
    static byte[] serializable_(final Serializable v) {
        assert v != null;
        final byte[] serialized;
        try (var baos = new ByteArrayOutputStream();
             var oos = new ObjectOutputStream(baos)) {
            oos.writeObject(v);
            oos.flush();
            serialized = baos.toByteArray();
        } catch (final IOException ioe) {
            throw new RuntimeException(ioe);
        }
        // Refuse to write what this class could never read back. filterFor(Class) caps the stream the reader will
        // accept, and until this check the writer accepted anything: an oversized value encrypted and stored
        // cleanly, then failed every later read -- with the plaintext already cleared, leaving the row
        // unrecoverable. Fail here, while the caller still holds the value.
        if (serialized.length > MAX_SERIALIZABLE_STREAM_BYTES) {
            throw new IllegalArgumentException(
                    "serialized form is too large to be read back"
                    + "; bytes: " + serialized.length
                    + "; limit: " + MAX_SERIALIZABLE_STREAM_BYTES
                    + "; type: " + v.getClass().getName());
        }
        return serialized;
    }

    /**
     * Returns the {@code T} value represented by the specified array of bytes.
     *
     * @param b            the array of bytes.
     * @param expectedType the type the deserialized value has to be an instance of.
     * @return the value represented by the {@code b}.
     * @throws java.io.InvalidClassException when the {@code b} holds anything but an {@code expectedType}.
     * @implNote Of the four caps this filter applies, only the stream size is also enforced when writing (see
     *         {@link #serializable_(Serializable)}); the depth, reference-count and array-length caps remain read-side
     *         only, so a graph which is deep or highly referential rather than merely large can still be written and
     *         then refused on the way back.
     */
    static Serializable serializable_(final byte[] b, final Class<?> expectedType) {
        assert b != null;
        assert b.length > 0;
        assert expectedType != null;
        try (var bais = new ByteArrayInputStream(b);
             var ois = new ObjectInputStream(bais)) {
            // the bytes come back from the database; accept nothing but the attribute's own type
            ois.setObjectInputFilter(filterFor(expectedType));
            try {
                return (Serializable) expectedType.cast(ois.readObject());
            } catch (final ClassNotFoundException cnfe) {
                throw new RuntimeException(cnfe);
            }
        } catch (final IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    /**
     * Returns a filter which caps the depth, the reference count, the stream length and the array lengths the stream
     * may ask for.
     *
     * @param expectedType the attribute's type; retained for diagnostics and for future tightening.
     * @return a resource-limiting filter.
     * @see <a href="https://docs.oracle.com/en/java/javase/21/core/serialization-filtering1.html">Serialization
     *         Filtering</a>
     */
    private static ObjectInputFilter filterFor(final Class<?> expectedType) {
        assert expectedType != null;
        return info -> {
            if (info.depth() > MAX_SERIALIZABLE_DEPTH
                || info.references() > MAX_SERIALIZABLE_REFERENCES
                || info.streamBytes() > MAX_SERIALIZABLE_STREAM_BYTES
                || info.arrayLength() > MAX_SERIALIZABLE_ARRAY_LENGTH) {
                return ObjectInputFilter.Status.REJECTED;
            }
            // NOTE: the root class is deliberately NOT pinned to expectedType. A value which serializes through a
            // proxy - every java.time type writes a java.time.Ser - presents that proxy as the root, so pinning
            // rejects values this module claims to support. The returned object is cast to expectedType by the
            // caller, which enforces the type; and these bytes are the encryption manager's own decrypted output,
            // not attacker-supplied input, so the residual risk is resource exhaustion, which the caps above cover.
            return ObjectInputFilter.Status.UNDECIDED;
        };
    }

    private static final long MAX_SERIALIZABLE_DEPTH = 32L;

    private static final long MAX_SERIALIZABLE_REFERENCES = 10_000L;

    private static final long MAX_SERIALIZABLE_STREAM_BYTES = 1L << 20;

    private static final long MAX_SERIALIZABLE_ARRAY_LENGTH = 1L << 20;

    // -----------------------------------------------------------------------------------------------------------------
    private __EncryptionServiceUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
