package com.github.jinahya.persistence.encryption;

import com.github.jinahya.persistence.encryption.EncryptionMapping.Pair;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.math.BigInteger;
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
 * @see EntityEncryptor
 */
@SuppressWarnings({
        "java:S100", // Method names should comply with a naming convention
        "java:S101"  // Class names should comply with a naming convention
})
final class EntityEncryptorUtils {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns the specified payload after checking that it is exactly as long as a fixed-width codec reads.
     *
     * @param b      the payload.
     * @param length the width of the codec, in bytes.
     * @return the {@code b}.
     * @throws IllegalArgumentException when the {@code b} is shorter, or longer, than the {@code length}.
     * @implNote Checked unconditionally, not with {@code assert}: a payload which is too long would otherwise
     *         be read silently, as its first {@code length} bytes.
     */
    private static byte[] exactly(final byte[] b, final int length) {
        if (b.length != length) {
            throw new IllegalArgumentException(
                    "a payload of " + b.length + " byte(s); this codec reads exactly " + length);
        }
        return b;
    }

    // ------------------------------------------------------------------- boolean, byte, short, int, long; ValueCodecs

    /**
     * Returns an array of bytes representing the specified {@code boolean} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     * @see ValueCodecs.OfPrimitive#encodeBoolean(boolean)
     */
    static byte[] boolean_1(final boolean v) {
        return ValueCodecs.OfPrimitive.encodeBoolean(v);
    }

    /**
     * Returns the {@code boolean} value represented by the specified array of bytes.
     *
     * @param b the array of bytes.
     * @return the value represented by the {@code b}.
     * @see ValueCodecs.OfPrimitive#decodeBoolean(byte[], int)
     */
    static boolean boolean_1(final byte[] b) {
        // a stored payload; no byte may be left over
        return ValueCodecs.OfPrimitive.decodeBoolean(exactly(b, Byte.BYTES), 0);
    }

    /**
     * Returns an array of bytes representing the specified {@code byte} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     * @see ValueCodecs.OfPrimitive#encodeByte(byte)
     */
    static byte[] byte_1(final byte v) {
        return ValueCodecs.OfPrimitive.encodeByte(v);
    }

    /**
     * Returns the {@code byte} value represented by the specified array of bytes.
     *
     * @param b the array of bytes.
     * @return the value represented by the {@code b}.
     * @see ValueCodecs.OfPrimitive#decodeByte(byte[], int)
     */
    static byte byte_1(final byte[] b) {
        // a stored payload; no byte may be left over
        return ValueCodecs.OfPrimitive.decodeByte(exactly(b, Byte.BYTES), 0);
    }

    /**
     * Writes the specified {@code short} value into the specified array of bytes, at the specified index.
     *
     * @param b the array of bytes to which the value is written.
     * @param i the index in the {@code b} at which the value is written.
     * @param v the value to write.
     * @return the specified {@code b}.
     * @see ValueCodecs.OfPrimitive#encodeShort(byte[], int, short)
     */
    private static byte[] short_2(final byte[] b, final int i, final short v) {
        ValueCodecs.OfPrimitive.encodeShort(b, i, v);
        return b;
    }

    /**
     * Reads a {@code short} value from the specified array of bytes, at the specified index.
     *
     * @param b the array of bytes from which the value is read.
     * @param i the index in the {@code b} from which the value is read.
     * @return the value read from the {@code b}.
     * @see ValueCodecs.OfPrimitive#decodeShort(byte[], int)
     */
    private static short short_2(final byte[] b, final int i) {
        return ValueCodecs.OfPrimitive.decodeShort(b, i);
    }

    /**
     * Returns an array of bytes representing the specified {@code short} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     * @see ValueCodecs.OfPrimitive#encodeShort(short)
     */
    static byte[] short_2(final short v) {
        return ValueCodecs.OfPrimitive.encodeShort(v);
    }

    /**
     * Returns the {@code short} value represented by the specified array of bytes.
     *
     * @param b the array of bytes.
     * @return the value represented by the {@code b}.
     * @see ValueCodecs.OfPrimitive#decodeShort(byte[], int)
     */
    static short short_2(final byte[] b) {
        // a stored payload; no byte may be left over
        return ValueCodecs.OfPrimitive.decodeShort(exactly(b, Short.BYTES), 0);
    }

    /**
     * Writes the specified {@code int} value into the specified array of bytes, at the specified index.
     *
     * @param b the array of bytes to which the value is written.
     * @param i the index in the {@code b} at which the value is written.
     * @param v the value to write.
     * @return the specified {@code b}.
     * @see ValueCodecs.OfPrimitive#encodeInt(byte[], int, int)
     */
    private static byte[] int_4(final byte[] b, final int i, final int v) {
        ValueCodecs.OfPrimitive.encodeInt(b, i, v);
        return b;
    }

    /**
     * Reads a {@code int} value from the specified array of bytes, at the specified index.
     *
     * @param b the array of bytes from which the value is read.
     * @param i the index in the {@code b} from which the value is read.
     * @return the value read from the {@code b}.
     * @see ValueCodecs.OfPrimitive#decodeInt(byte[], int)
     */
    private static int int_4(final byte[] b, final int i) {
        return ValueCodecs.OfPrimitive.decodeInt(b, i);
    }

    /**
     * Returns an array of bytes representing the specified {@code int} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     * @see ValueCodecs.OfPrimitive#encodeInt(int)
     */
    static byte[] int_4(final int v) {
        return ValueCodecs.OfPrimitive.encodeInt(v);
    }

    /**
     * Returns the {@code int} value represented by the specified array of bytes.
     *
     * @param b the array of bytes.
     * @return the value represented by the {@code b}.
     * @see ValueCodecs.OfPrimitive#decodeInt(byte[], int)
     */
    static int int_4(final byte[] b) {
        // a stored payload; no byte may be left over
        return ValueCodecs.OfPrimitive.decodeInt(exactly(b, Integer.BYTES), 0);
    }

    /**
     * Writes the specified {@code long} value into the specified array of bytes, at the specified index.
     *
     * @param b the array of bytes to which the value is written.
     * @param i the index in the {@code b} at which the value is written.
     * @param v the value to write.
     * @return the specified {@code b}.
     * @see ValueCodecs.OfPrimitive#encodeLong(byte[], int, long)
     */
    private static byte[] long_8(final byte[] b, final int i, final long v) {
        ValueCodecs.OfPrimitive.encodeLong(b, i, v);
        return b;
    }

    /**
     * Reads a {@code long} value from the specified array of bytes, at the specified index.
     *
     * @param b the array of bytes from which the value is read.
     * @param i the index in the {@code b} from which the value is read.
     * @return the value read from the {@code b}.
     * @see ValueCodecs.OfPrimitive#decodeLong(byte[], int)
     */
    private static long long_8(final byte[] b, final int i) {
        return ValueCodecs.OfPrimitive.decodeLong(b, i);
    }

    /**
     * Returns an array of bytes representing the specified {@code long} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     * @see ValueCodecs.OfPrimitive#encodeLong(long)
     */
    static byte[] long_8(final long v) {
        return ValueCodecs.OfPrimitive.encodeLong(v);
    }

    /**
     * Returns the {@code long} value represented by the specified array of bytes.
     *
     * @param b the array of bytes.
     * @return the value represented by the {@code b}.
     * @see ValueCodecs.OfPrimitive#decodeLong(byte[], int)
     */
    static long long_8(final byte[] b) {
        // a stored payload; no byte may be left over
        return ValueCodecs.OfPrimitive.decodeLong(exactly(b, Long.BYTES), 0);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns an array of bytes representing the specified {@code char} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     * @see ValueCodecs.OfPrimitive#encodeChar(char)
     */
    static byte[] char_2(final char v) {
        return ValueCodecs.OfPrimitive.encodeChar(v);
    }

    /**
     * Returns the {@code char} value represented by the specified array of bytes.
     *
     * @param b the array of bytes.
     * @return the value represented by the {@code b}.
     * @see ValueCodecs.OfPrimitive#decodeChar(byte[], int)
     */
    static char char_2(final byte[] b) {
        // a stored payload; no byte may be left over
        return ValueCodecs.OfPrimitive.decodeChar(exactly(b, Character.BYTES), 0);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns an array of bytes representing the specified {@code float} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     * @see ValueCodecs.OfPrimitive#encodeFloat(float)
     */
    static byte[] float_4(final float v) {
        return ValueCodecs.OfPrimitive.encodeFloat(v);
    }

    /**
     * Returns the {@code float} value represented by the specified array of bytes.
     *
     * @param b the array of bytes.
     * @return the value represented by the {@code b}.
     * @see ValueCodecs.OfPrimitive#decodeFloat(byte[], int)
     */
    static float float_4(final byte[] b) {
        // a stored payload; no byte may be left over
        return ValueCodecs.OfPrimitive.decodeFloat(exactly(b, Float.BYTES), 0);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns an array of bytes representing the specified {@code double} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     * @see ValueCodecs.OfPrimitive#encodeDouble(double)
     */
    static byte[] double_8(final double v) {
        return ValueCodecs.OfPrimitive.encodeDouble(v);
    }

    /**
     * Returns the {@code double} value represented by the specified array of bytes.
     *
     * @param b the array of bytes.
     * @return the value represented by the {@code b}.
     * @see ValueCodecs.OfPrimitive#decodeDouble(byte[], int)
     */
    static double double_8(final byte[] b) {
        // a stored payload; no byte may be left over
        return ValueCodecs.OfPrimitive.decodeDouble(exactly(b, Double.BYTES), 0);
    }

    // ------------------------------------------------------------------------------------------------ java.lang.String

    /**
     * Returns an array of bytes representing the specified {@code String} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     * @throws IllegalArgumentException when the {@code v} is not encodable as UTF-8; it holds an unpaired surrogate.
     * @see ValueCodecs.OfLang#encodeString(String)
     */
    static byte[] string_(final String v) {
        return ValueCodecs.OfLang.encodeString(v);
    }

    /**
     * Returns the {@code String} value represented by the specified array of bytes.
     *
     * @param v the array of bytes.
     * @return the value represented by the {@code v}.
     * @throws IllegalArgumentException when the {@code v} is not well-formed UTF-8.
     * @see ValueCodecs.OfLang#decodeString(byte[])
     */
    static String string_(final byte[] v) {
        return ValueCodecs.OfLang.decodeString(v);
    }

    // ------------------------------------------------------------------------------------------------------------ UUID

    /**
     * Returns an array of bytes representing the specified {@code UUID} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     * @see ValueCodecs.OfUtil#encodeUuid(UUID)
     */
    static byte[] uuid_16(final UUID v) {
        return ValueCodecs.OfUtil.encodeUuid(v);
    }

    /**
     * Returns the {@code UUID} value represented by the specified array of bytes.
     *
     * @param b the array of bytes.
     * @return the value represented by the {@code b}.
     * @see ValueCodecs.OfUtil#decodeUuid(byte[], int)
     */
    static UUID uuid_16(final byte[] b) {
        // a stored payload; no byte may be left over
        return ValueCodecs.OfUtil.decodeUuid(exactly(b, ValueCodecs.OfUtil.UUID_BYTES), 0);
    }

    // ------------------------------------------------------------------------------------------------------- java.math

    /**
     * Returns an array of bytes representing the specified {@code BigInteger} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     * @see ValueCodecs.OfMath#encodeBigInteger(BigInteger)
     */
    static byte[] big_integer_(final BigInteger v) {
        return ValueCodecs.OfMath.encodeBigInteger(v);
    }

    /**
     * Returns the {@code BigInteger} value represented by the specified array of bytes.
     *
     * @param b the array of bytes.
     * @return the value represented by the {@code b}.
     * @see ValueCodecs.OfMath#decodeBigInteger(byte[])
     */
    static BigInteger big_integer_(final byte[] b) {
        return ValueCodecs.OfMath.decodeBigInteger(b);
    }

    // ------------------------------------------------------------------------------------------------------- java.math

    /**
     * Returns an array of bytes representing the specified {@code BigDecimal} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     * @see ValueCodecs.OfMath#encodeBigDecimal(BigDecimal)
     */
    static byte[] big_decimal_(final BigDecimal v) {
        return ValueCodecs.OfMath.encodeBigDecimal(v);
    }

    /**
     * Returns the {@code BigDecimal} value represented by the specified array of bytes.
     *
     * @param b the array of bytes.
     * @return the value represented by the {@code b}.
     * @see ValueCodecs.OfMath#decodeBigDecimal(byte[])
     */
    static BigDecimal big_decimal_(final byte[] b) {
        return ValueCodecs.OfMath.decodeBigDecimal(b);
    }

    // ------------------------------------------------------------------------------------------------------- java.time

    /**
     * Returns an array of bytes representing the specified {@code LocalDate} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     * @see ValueCodecs.OfTime#encodeLocalDate(LocalDate)
     */
    static byte[] local_date_8(final LocalDate v) {
        return ValueCodecs.OfTime.encodeLocalDate(v);
    }

    /**
     * Returns the {@code LocalDate} value represented by the specified array of bytes.
     *
     * @param b the array of bytes.
     * @return the value represented by the {@code b}.
     * @see ValueCodecs.OfTime#decodeLocalDate(byte[])
     */
    static LocalDate local_date_8(final byte[] b) {
        // a stored payload; no byte may be left over
        return ValueCodecs.OfTime.decodeLocalDate(exactly(b, ValueCodecs.OfTime.LOCAL_DATE_BYTES), 0);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns an array of bytes representing the specified {@code LocalTime} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     * @see ValueCodecs.OfTime#encodeLocalTime(LocalTime)
     */
    static byte[] local_time_8(final LocalTime v) {
        return ValueCodecs.OfTime.encodeLocalTime(v);
    }

    /**
     * Returns the {@code LocalTime} value represented by the specified array of bytes.
     *
     * @param b the array of bytes.
     * @return the value represented by the {@code b}.
     * @see ValueCodecs.OfTime#decodeLocalTime(byte[])
     */
    static LocalTime local_time_8(final byte[] b) {
        // a stored payload; no byte may be left over
        return ValueCodecs.OfTime.decodeLocalTime(exactly(b, ValueCodecs.OfTime.LOCAL_TIME_BYTES), 0);
    }

    // ----------------------------------------------------------------------------------------- java.time.LocalDateTime

    /**
     * Returns an array of bytes representing the specified {@code LocalDateTime} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     * @see ValueCodecs.OfTime#encodeLocalDateTime(LocalDateTime)
     */
    static byte[] local_date_time_16(final LocalDateTime v) {
        return ValueCodecs.OfTime.encodeLocalDateTime(v);
    }

    /**
     * Returns the {@code LocalDateTime} value represented by the specified array of bytes.
     *
     * @param b the array of bytes.
     * @return the value represented by the {@code b}.
     * @see ValueCodecs.OfTime#decodeLocalDateTime(byte[])
     */
    static LocalDateTime local_date_time_16(final byte[] b) {
        // a stored payload; no byte may be left over
        return ValueCodecs.OfTime.decodeLocalDateTime(exactly(b, ValueCodecs.OfTime.LOCAL_DATE_TIME_BYTES), 0);
    }

    // ------------------------------------------------------------------------------------------------ java.time.Offset

    /**
     * Returns an array of bytes representing the specified {@code ZoneOffset} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     * @see ValueCodecs.OfTime#encodeZoneOffset(ZoneOffset)
     */
    static byte[] offset_4(final ZoneOffset v) {
        return ValueCodecs.OfTime.encodeZoneOffset(v);
    }

    /**
     * Returns the {@code ZoneOffset} value represented by the specified array of bytes.
     *
     * @param b the array of bytes.
     * @return the value represented by the {@code b}.
     * @see ValueCodecs.OfTime#decodeZoneOffset(byte[])
     */
    static ZoneOffset offset_4(final byte[] b) {
        // a stored payload; no byte may be left over
        return ValueCodecs.OfTime.decodeZoneOffset(exactly(b, ValueCodecs.OfTime.ZONE_OFFSET_BYTES), 0);
    }

    // -------------------------------------------------------------------------------------------- java.time.OffsetTime

    /**
     * Returns an array of bytes representing the specified {@code OffsetTime} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     * @see ValueCodecs.OfTime#encodeOffsetTime(OffsetTime)
     */
    static byte[] offset_time_12(final java.time.OffsetTime v) {
        return ValueCodecs.OfTime.encodeOffsetTime(v);
    }

    /**
     * Returns the {@code OffsetTime} value represented by the specified array of bytes.
     *
     * @param b the array of bytes.
     * @return the value represented by the {@code b}.
     * @see ValueCodecs.OfTime#decodeOffsetTime(byte[])
     */
    static java.time.OffsetTime offset_time_12(final byte[] b) {
        // a stored payload; no byte may be left over
        return ValueCodecs.OfTime.decodeOffsetTime(exactly(b, ValueCodecs.OfTime.OFFSET_TIME_BYTES), 0);
    }

    // ---------------------------------------------------------------------------------------- java.time.OffsetDateTime

    /**
     * Returns an array of bytes representing the specified {@code OffsetDateTime} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     * @see ValueCodecs.OfTime#encodeOffsetDateTime(OffsetDateTime)
     */
    static byte[] offset_date_time_20(final OffsetDateTime v) {
        return ValueCodecs.OfTime.encodeOffsetDateTime(v);
    }

    /**
     * Returns the {@code OffsetDateTime} value represented by the specified array of bytes.
     *
     * @param b the array of bytes.
     * @return the value represented by the {@code b}.
     * @see ValueCodecs.OfTime#decodeOffsetDateTime(byte[])
     */
    static OffsetDateTime offset_date_time_20(final byte[] b) {
        // a stored payload; no byte may be left over
        return ValueCodecs.OfTime.decodeOffsetDateTime(exactly(b, ValueCodecs.OfTime.OFFSET_DATE_TIME_BYTES), 0);
    }

    // ----------------------------------------------------------------------------------------------- java.time.Instant

    /**
     * Returns an array of bytes representing the specified {@code Instant} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     * @see ValueCodecs.OfTime#encodeInstant(Instant)
     */
    static byte[] instant_12(final Instant v) {
        return ValueCodecs.OfTime.encodeInstant(v);
    }

    /**
     * Returns the {@code Instant} value represented by the specified array of bytes.
     *
     * @param b the array of bytes.
     * @return the value represented by the {@code b}.
     * @see ValueCodecs.OfTime#decodeInstant(byte[])
     */
    static Instant instant_12(final byte[] b) {
        // a stored payload; no byte may be left over
        return ValueCodecs.OfTime.decodeInstant(exactly(b, ValueCodecs.OfTime.INSTANT_BYTES), 0);
    }

    // -------------------------------------------------------------------------------------------------- java.time.Year

    /**
     * Returns an array of bytes representing the specified {@code Year} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     * @see ValueCodecs.OfTime#encodeYear(Year)
     */
    static byte[] year_4(final Year v) {
        return ValueCodecs.OfTime.encodeYear(v);
    }

    /**
     * Returns the {@code Year} value represented by the specified array of bytes.
     *
     * @param b the array of bytes.
     * @return the value represented by the {@code b}.
     * @see ValueCodecs.OfTime#decodeYear(byte[])
     */
    static Year year_4(final byte[] b) {
        // a stored payload; no byte may be left over
        return ValueCodecs.OfTime.decodeYear(exactly(b, ValueCodecs.OfTime.YEAR_BYTES), 0);
    }

    // -------------------------------------------------------------------------------------------------- java.util.Date

    /**
     * Returns an array of bytes representing the specified {@code Date} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     * @see <a href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#deprecations">
     *         Jakarta Persistence 3.2, A.1.1. Deprecations</a>
     * @deprecated Jakarta Persistence 3.2 deprecates the use of {@link java.util.Date} in new applications in favor of
     *         the {@code java.time} API.
     * @see ValueCodecs.OfUtil#encodeDate(java.util.Date)
     */
    @Deprecated
    static byte[] util_date_8(final java.util.Date v) {
        return ValueCodecs.OfUtil.encodeDate(v);
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
     * @see ValueCodecs.OfUtil#decodeDate(byte[])
     */
    @Deprecated
    static java.util.Date util_date_8(final byte[] b) {
        // a stored payload; no byte may be left over
        return ValueCodecs.OfUtil.decodeDate(exactly(b, ValueCodecs.OfUtil.DATE_BYTES), 0);
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
        ValueCodecs.OfUtil.encodeDate(b, i, v.getTime());
        return b;
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
        v.setTime(ValueCodecs.OfUtil.decodeDate(b, i));
        return v;
    }

    /**
     * Returns an array of bytes representing the specified {@code Calendar} value.
     * <p>
     * Only the instant is stored; the calendar's time zone, locale-dependent settings and calendar system are not. That
     * is the same value a plain, unencrypted {@code Calendar} mapping persists: a {@code TIMESTAMP} column holds no
     * zone either, and a provider reads it back as a {@code Calendar} in the default time zone. Storing the epoch
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
     * Returns an array of bytes representing the specified {@code Date} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     * @see <a href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#deprecations">
     *         Jakarta Persistence 3.2, A.1.1. Deprecations</a>
     * @deprecated Jakarta Persistence 3.2 deprecates the use of {@link java.sql.Date} in new applications in favor of
     *         the {@code java.time} API.
     * @see ValueCodecs.OfSql#encodeDate(java.sql.Date)
     */
    @Deprecated
    static byte[] sql_date_8(final java.sql.Date v) {
        return ValueCodecs.OfSql.encodeDate(v);
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
     * @see ValueCodecs.OfSql#decodeDate(byte[])
     */
    @Deprecated
    static java.sql.Date sql_date_8(final byte[] b) {
        // a stored payload; no byte may be left over
        return ValueCodecs.OfSql.decodeDate(exactly(b, ValueCodecs.OfSql.DATE_BYTES), 0);
    }

    // --------------------------------------------------------------------------------------------------- java.sql.Time

    /**
     * Returns an array of bytes representing the specified {@code Time} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     * @see <a href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#deprecations">
     *         Jakarta Persistence 3.2, A.1.1. Deprecations</a>
     * @deprecated Jakarta Persistence 3.2 deprecates the use of {@link java.sql.Time} in new applications in favor of
     *         the {@code java.time} API.
     * @see ValueCodecs.OfSql#encodeTime(java.sql.Time)
     */
    @Deprecated
    static byte[] sql_time_8(final java.sql.Time v) {
        return ValueCodecs.OfSql.encodeTime(v);
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
     * @see ValueCodecs.OfSql#decodeTime(byte[])
     */
    @Deprecated
    static java.sql.Time sql_time_8(final byte[] b) {
        // a stored payload; no byte may be left over
        return ValueCodecs.OfSql.decodeTime(exactly(b, ValueCodecs.OfSql.TIME_BYTES), 0);
    }

    // ---------------------------------------------------------------------------------------------- java.sql.Timestamp

    /**
     * Returns an array of bytes representing the specified {@code Timestamp} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     * @see <a href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#deprecations">
     *         Jakarta Persistence 3.2, A.1.1. Deprecations</a>
     * @deprecated Jakarta Persistence 3.2 deprecates the use of {@link java.sql.Timestamp} in new applications in favor
     *         of the {@code java.time} API.
     * @see ValueCodecs.OfSql#encodeTimestamp(java.sql.Timestamp)
     */
    @Deprecated
    static byte[] sql_timestamp_12(final java.sql.Timestamp v) {
        return ValueCodecs.OfSql.encodeTimestamp(v);
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
     * @see ValueCodecs.OfSql#decodeTimestamp(byte[])
     */
    @Deprecated
    static java.sql.Timestamp sql_timestamp_12(final byte[] b) {
        // a stored payload; no byte may be left over
        return ValueCodecs.OfSql.decodeTimestamp(exactly(b, ValueCodecs.OfSql.TIMESTAMP_BYTES), 0);
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
     * Returns an array of bytes representing the specified {@code char[]} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     * @see ValueCodecs.OfPrimitive#encodeChars(char[])
     */
    static byte[] chars_2l(final char[] v) {
        return ValueCodecs.OfPrimitive.encodeChars(v);
    }

    /**
     * Returns the {@code char[]} value represented by the specified array of bytes.
     *
     * @param b the array of bytes.
     * @return the value represented by the {@code b}.
     * @see ValueCodecs.OfPrimitive#decodeChars(byte[])
     */
    static char[] chars_2l(final byte[] b) {
        return ValueCodecs.OfPrimitive.decodeChars(b);
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
     * The constant is stored by its {@link Enum#name() name}, as {@code @Enumerated(EnumType.STRING)} stores it, never
     * by its ordinal: reordering, or inserting, constants is common, and would silently map every stored row to another
     * constant. Renaming a constant, by contrast, is a breaking change: the stored name no longer resolves, and the
     * read fails loudly, naming the attribute.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}.
     * @see ValueCodecs.OfLang#encodeEnum(Enum)
     */
    static byte[] enum_(final Enum<?> v) {
        return ValueCodecs.OfLang.encodeEnum(v);
    }

    /**
     * Returns the constant, of the specified enum class, represented by the specified array of bytes.
     *
     * @param b         the array of bytes, holding the constant's {@link Enum#name() name}.
     * @param enumClass the enum class.
     * @param <E>       enum type parameter
     * @return the enum constant represented by the {@code b}.
     * @see ValueCodecs.OfLang#decodeEnum(byte[], Class)
     */
    static <E extends Enum<E>> E enum_(final byte[] b, final Class<E> enumClass) {
        return ValueCodecs.OfLang.decodeEnum(b, enumClass);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns an array of bytes representing the specified {@code Serializable} value.
     *
     * @param v the value to represent.
     * @return an array of bytes representing the {@code v}; its Java serialization.
     * @implNote Nothing is limited here, as nothing is limited by a plain {@code Serializable} mapping: the
     *         size of the value, and of the column holding it, is the application's concern.
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
     *         deserialization is. Which classes may be deserialized, and how large a graph may be, is the application's
     *         to configure there.
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
        static @org.jspecify.annotations.Nullable Codec of(final byte id) {
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
     * @implNote The order of the checks is the order of the codec ladders in {@link EntityEncryptor}:
     *         {@code java.sql} types before {@link java.util.Date}, and {@link Serializable} last.
     */
    static @org.jspecify.annotations.Nullable Codec codecOf(final Class<?> javaType) {
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
     * The length of a payload's header: the {@link #FORMAT_VERSION format version}, then the {@link Codec#id codec id}.
     * The value is {@value #HEADER_BYTES}.
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
     * @throws IllegalArgumentException when the {@code framed} is shorter than a header, was written in another format
     *                                  version, or was encoded by a codec other than the {@code expected}.
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
                    "the payload was encoded by " +
                    (actual == null ? "an unknown codec (" + (framed[1] & 0xFF) + ")" : actual)
                    + ", but the attribute is decoded by " + expected);
        }
        return Arrays.copyOfRange(framed, HEADER_BYTES, framed.length);
    }

    // ------------------------------------------------------------------------------------------------- encode / decode

    /**
     * Returns the specified plaintext value of the specified pair, encoded by its declared java type and framed with
     * the payload header.
     *
     * @param pair           the pair.
     * @param decryptedValue the plaintext value.
     * @return the framed encoding of the {@code decryptedValue}.
     */
    static byte[] encode(final Pair pair, final Object decryptedValue) {
        final byte[] decryptedBytes;
        final var javaType = pair.javaType();
        // The DECLARED type decides the encoding, exactly as it decides the decoding below. Dispatching on
        // the runtime class here instead let the two ladders pick different codecs for one attribute -- a
        // java.util.Date holding a java.sql.Timestamp was written as epoch seconds and read back as epoch
        // millis, silently. Keep this chain in the same order as the one in decode(...).
        if (!javaType.isInstance(decryptedValue)) {
            throw new RuntimeException(
                    "the value is not an instance of the attribute's declared java type" +
                    "; decrypted attribute: " + pair.name() +
                    "; java type: " + javaType.getName() +
                    "; value type: " + decryptedValue.getClass().getName()
            );
        }
        try {
            if (javaType == Boolean.class) {
                decryptedBytes = boolean_1((Boolean) decryptedValue);
            } else if (javaType == Byte.class) {
                decryptedBytes = byte_1((Byte) decryptedValue);
            } else if (javaType == Short.class) {
                decryptedBytes = short_2((Short) decryptedValue);
            } else if (javaType == Integer.class) {
                decryptedBytes = int_4((Integer) decryptedValue);
            } else if (javaType == Long.class) {
                decryptedBytes = long_8((Long) decryptedValue);
            } else if (javaType == Character.class) {
                decryptedBytes = char_2((Character) decryptedValue);
            } else if (javaType == Float.class) {
                decryptedBytes = float_4((Float) decryptedValue);
            } else if (javaType == Double.class) {
                decryptedBytes = double_8((Double) decryptedValue);
            } else if (javaType == String.class) {
                decryptedBytes = string_((String) decryptedValue);
            } else if (javaType == UUID.class) {
                decryptedBytes = uuid_16((UUID) decryptedValue);
            } else if (javaType == BigInteger.class) {
                decryptedBytes = big_integer_((BigInteger) decryptedValue);
            } else if (javaType == BigDecimal.class) {
                decryptedBytes = big_decimal_((BigDecimal) decryptedValue);
            } else if (javaType == LocalDate.class) {
                decryptedBytes = local_date_8((LocalDate) decryptedValue);
            } else if (javaType == LocalTime.class) {
                decryptedBytes = local_time_8((LocalTime) decryptedValue);
            } else if (javaType == LocalDateTime.class) {
                decryptedBytes = local_date_time_16((LocalDateTime) decryptedValue);
            } else if (javaType == OffsetTime.class) {
                decryptedBytes = offset_time_12((OffsetTime) decryptedValue);
            } else if (javaType == OffsetDateTime.class) {
                decryptedBytes = offset_date_time_20((OffsetDateTime) decryptedValue);
            } else if (javaType == Instant.class) {
                decryptedBytes = instant_12((Instant) decryptedValue);
            } else if (javaType == Year.class) {
                decryptedBytes = year_4((Year) decryptedValue);
            } else if (javaType == java.sql.Timestamp.class) { // before java.util.Date; keeps the nanos
                decryptedBytes = sql_timestamp_12((java.sql.Timestamp) decryptedValue);
            } else if (javaType == java.sql.Date.class) {      // before java.util.Date
                decryptedBytes = sql_date_8((java.sql.Date) decryptedValue);
            } else if (javaType == java.sql.Time.class) {      // before java.util.Date
                decryptedBytes = sql_time_8((java.sql.Time) decryptedValue);
            } else if (Calendar.class.isAssignableFrom(javaType)) {
                decryptedBytes = util_calendar_8((Calendar) decryptedValue);
            } else if (java.util.Date.class.isAssignableFrom(javaType)) {
                // one branch feeds both of decrypt's generic Date branches -- the exact `== java.util.Date` one
                // and the reflective (long) constructor one -- because both read long_8 millis
                decryptedBytes = util_date_8((java.util.Date) decryptedValue);
            } else if (javaType == byte[].class) {
                decryptedBytes =
                        ((byte[]) decryptedValue).clone(); // the service must not be handed the instance's own array
            } else if (javaType == Byte[].class) {
                logger.log(System.Logger.Level.WARNING, "Byte[] is not encouraged; use byte[]");
                decryptedBytes = Bytes_l((Byte[]) decryptedValue);
            } else if (javaType == char[].class) {
                decryptedBytes = chars_2l((char[]) decryptedValue);
            } else if (javaType == Character[].class) {
                logger.log(System.Logger.Level.WARNING, "Character[] is not encouraged; use char[]");
                decryptedBytes = Characters_2l((Character[]) decryptedValue);
            } else if (javaType.isEnum()) {
                decryptedBytes = enum_((Enum<?>) decryptedValue);
            } else if (Serializable.class.isAssignableFrom(javaType)) {
                decryptedBytes = serializable_((Serializable) decryptedValue);
            } else {
                throw new RuntimeException("unsupported java type: " + javaType);
            }
        } catch (final IllegalArgumentException iae) {
            // a value the codec refuses to encode, because it could not be read back as it is: a string with an
            // unpaired surrogate, an oversized serializable, ... -- reported here while the caller still holds it.
            // The message never carries the value.
            throw new RuntimeException(
                    "cannot encode the value (" + iae.getMessage() + ")" +
                    "; decrypted attribute: " + pair.name() +
                    "; java type: " + javaType.getName(),
                    iae
            );
        }
        // the header names the format version and the codec, so that a reader can tell what wrote the bytes
        final var codec = codecOf(javaType);
        assert codec != null : "the ladder above handled a type codecOf does not know: " + javaType;
        return frame(codec, decryptedBytes);
    }

    /**
     * Returns the plaintext value of the specified pair, decoded from the specified framed bytes.
     *
     * @param pair        the pair.
     * @param framedBytes the decrypted, framed, bytes.
     * @return the decoded value.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    static Object decode(final Pair pair, final byte[] framedBytes) {
        final Object decryptedValue;
        // the declared java type, for the same reason as in encode(...): the two ladders must agree on the type
        final var javaType = pair.javaType();
        final var codec = codecOf(javaType);
        if (codec == null) {
            throw new RuntimeException("unsupported java type: " + javaType);
        }
        try {
            // checks the format version, and that the codec which wrote the bytes is the one which reads them
            final var decryptedBytes = unframe(codec, framedBytes);
            if (javaType == boolean.class || javaType == Boolean.class) {
                decryptedValue = boolean_1(decryptedBytes);
            } else if (javaType == byte.class || javaType == Byte.class) {
                decryptedValue = byte_1(decryptedBytes);
            } else if (javaType == short.class || javaType == Short.class) {
                decryptedValue = short_2(decryptedBytes);
            } else if (javaType == int.class || javaType == Integer.class) {
                decryptedValue = int_4(decryptedBytes);
            } else if (javaType == long.class || javaType == Long.class) {
                decryptedValue = long_8(decryptedBytes);
            } else if (javaType == char.class || javaType == Character.class) {
                decryptedValue = char_2(decryptedBytes);
            } else if (javaType == float.class || javaType == Float.class) {
                decryptedValue = float_4(decryptedBytes);
            } else if (javaType == double.class || javaType == Double.class) {
                decryptedValue = double_8(decryptedBytes);
            } else if (javaType == String.class) {
                decryptedValue = string_(decryptedBytes);
            } else if (javaType == UUID.class) {
                decryptedValue = uuid_16(decryptedBytes);
            } else if (javaType == BigInteger.class) {
                decryptedValue = big_integer_(decryptedBytes);
            } else if (javaType == BigDecimal.class) {
                decryptedValue = big_decimal_(decryptedBytes);
            } else if (javaType == LocalDate.class) {
                decryptedValue = local_date_8(decryptedBytes);
            } else if (javaType == LocalTime.class) {
                decryptedValue = local_time_8(decryptedBytes);
            } else if (javaType == LocalDateTime.class) {
                decryptedValue = local_date_time_16(decryptedBytes);
            } else if (javaType == OffsetTime.class) {
                decryptedValue = offset_time_12(decryptedBytes);
            } else if (javaType == OffsetDateTime.class) {
                decryptedValue = offset_date_time_20(decryptedBytes);
            } else if (javaType == Instant.class) {
                decryptedValue = instant_12(decryptedBytes);
            } else if (javaType == Year.class) {
                decryptedValue = year_4(decryptedBytes);
            } else if (javaType == java.sql.Timestamp.class) { // before java.util.Date; keeps the nanos
                decryptedValue = sql_timestamp_12(decryptedBytes);
            } else if (javaType == java.sql.Date.class) {      // before java.util.Date
                decryptedValue = sql_date_8(decryptedBytes);
            } else if (javaType == java.sql.Time.class) {      // before java.util.Date
                decryptedValue = sql_time_8(decryptedBytes);
            } else if (Calendar.class.isAssignableFrom(javaType)) {
                decryptedValue = util_calendar_8(decryptedBytes);
            } else if (javaType == java.util.Date.class) {
                decryptedValue = util_date_8(decryptedBytes);
            } else if (java.util.Date.class.isAssignableFrom(javaType)) {
                final var time = long_8(decryptedBytes);
                try {
                    decryptedValue = javaType.getConstructor(long.class).newInstance(time);
                } catch (final ReflectiveOperationException roe) {
                    throw new RuntimeException("failed to construct " + javaType, roe);
                }
            } else if (javaType == byte[].class) {
                decryptedValue = decryptedBytes;
            } else if (javaType == Byte[].class) {
                logger.log(System.Logger.Level.WARNING, "Byte[] is not encouraged; use byte[]");
                decryptedValue = Bytes_l(decryptedBytes);
            } else if (javaType == char[].class) {
                decryptedValue = chars_2l(decryptedBytes);
            } else if (javaType == Character[].class) {
                logger.log(System.Logger.Level.WARNING, "Character[] is not encouraged; use char[]");
                decryptedValue = Characters_2l(decryptedBytes);
            } else if (javaType.isEnum()) {
                decryptedValue = enum_(decryptedBytes, (Class) javaType);
            } else if (Serializable.class.isAssignableFrom(javaType)) {
                decryptedValue = javaType.cast(serializable_(decryptedBytes, javaType));
            } else {
                throw new RuntimeException("unsupported java type: " + javaType);
            }
        } catch (final IndexOutOfBoundsException | IllegalArgumentException | DateTimeException
                       | AssertionError e) {
            // Everything the decode ladder can raise for bytes it cannot turn back into a value:
            //   IndexOutOfBounds     a payload shorter than the fixed-width codec expects
            //   AssertionError       an assert in EntityEncryptorUtils, when assertions are enabled;
            //                        the payload lengths themselves are checked unconditionally
            //   IllegalArgumentException  chars_2l on an odd-length payload, Enum.valueOf on a name
            //                        which is not a constant of the attribute's enum, and a payload header
            //                        naming another format version or another codec
            //   DateTimeException    a java.time field out of its range: an epoch day, a nano-of-day,
            //                        a nano-of-second, an offset, or a year
            // Without the last one, those two surfaced bare, with no clue which attribute they came from.
            throw new RuntimeException(
                    "cannot reconstruct the value from the decrypted bytes" +
                    (e.getMessage() == null ? "" : " (" + e.getMessage() + ")") +
                    "; decrypted attribute: " + pair.name() +
                    "; java type: " + javaType.getName() +
                    "; decrypted bytes: " + framedBytes.length,
                    e
            );
        }
        return decryptedValue;
    }

    // -----------------------------------------------------------------------------------------------------------------
    private EntityEncryptorUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
