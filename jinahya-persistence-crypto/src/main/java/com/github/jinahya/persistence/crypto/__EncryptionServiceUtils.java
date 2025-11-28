package com.github.jinahya.persistence.crypto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

final class __EncryptionServiceUtils {

    // -----------------------------------------------------------------------------------------------------------------
    private static byte[] boolean_1(final byte[] b, final int i, final boolean v) {
        assert b != null;
        assert i >= 0;
        assert i + Byte.BYTES <= b.length;
        b[i] = (byte) (v ? 1 : 0);
        return b;
    }

    private static boolean boolean_1(final byte[] b, final int i) {
        assert b != null;
        assert i >= 0;
        assert i + Byte.BYTES <= b.length;
        return b[i] == 1;
    }

    static byte[] boolean_1(final boolean v) {
        return boolean_1(new byte[Byte.BYTES], 0, v);
    }

    static boolean boolean_1(final byte[] b) {
        return boolean_1(b, 0);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private static byte[] short_2(final byte[] b, int i, final short v) {
        assert b != null;
        assert i >= 0;
        assert i + Short.BYTES <= b.length;
        b[i] = (byte) (v >> Byte.SIZE);
        b[++i] = (byte) (v & 0xFF);
        return b;
    }

    private static short short_2(final byte[] b, int i) {
        assert b != null;
        assert i >= 0;
        assert i + Short.BYTES <= b.length;
        return (short) (
                (b[i] << Byte.SIZE) |
                (b[++i] & 0xFF)
        );
    }

    static byte[] short_2(final short v) {
        return short_2(new byte[Short.BYTES], 0, v);
    }

    static short short_2(final byte[] b) {
        return short_2(b, 0);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private static byte[] int_4(final byte[] b, final int i, final int v) {
        assert b != null;
        assert i >= 0;
        assert i + Integer.BYTES <= b.length;
        short_2(b, i, (short) (v >> Short.SIZE));
        short_2(b, i + Short.BYTES, (short) (v & 0xFFFF));
        return b;
    }

    private static int int_4(final byte[] b, final int i) {
        assert b != null;
        assert i >= 0;
        assert i + Integer.BYTES <= b.length;
        return (short_2(b, i) << Short.SIZE) |
               (short_2(b, i + Short.BYTES) & 0xFFFF);
    }

    static byte[] int_4(final int v) {
        return int_4(new byte[Integer.BYTES], 0, v);
    }

    static int int_4(final byte[] b) {
        return int_4(b, 0);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private static byte[] long_8(final byte[] b, final int i, final long v) {
        assert b != null;
        assert i >= 0;
        assert i + Long.BYTES <= b.length;
        int_4(b, i, (int) (v >> Integer.SIZE));
        int_4(b, i + Integer.BYTES, (int) v);
        return b;
    }

    private static long long_8(final byte[] b, final int i) {
        assert b != null;
        assert i >= 0;
        assert i + Long.BYTES <= b.length;
        return ((long) int_4(b, i) << Integer.SIZE) |
               (int_4(b, i + Integer.BYTES) & 0xFFFFFFFFL);
    }

    static byte[] long_8(final long v) {
        return long_8(new byte[Long.BYTES], 0, v);
    }

    static long long_8(final byte[] b) {
        return long_8(b, 0);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private static byte[] char_2(final byte[] b, final int i, final char v) {
        return short_2(b, i, (short) v);
    }

    private static char char_2(final byte[] b, final int i) {
        return (char) short_2(b, i);
    }

    static byte[] char_2(final char v) {
        return char_2(new byte[Character.BYTES], 0, v);
    }

    static char char_2(final byte[] b) {
        return char_2(b, 0);
    }

    // -----------------------------------------------------------------------------------------------------------------
    static byte[] float_4(final float v) {
        return int_4(Float.floatToRawIntBits(v));
    }

    static float float_4(final byte[] b) {
        return Float.intBitsToFloat(int_4(b));
    }

    // -----------------------------------------------------------------------------------------------------------------
    static byte[] double_8(final double v) {
        return long_8(Double.doubleToRawLongBits(v));
    }

    static double double_8(final byte[] b) {
        return Double.longBitsToDouble(long_8(b));
    }

    // ------------------------------------------------------------------------------------------------ java.lang.String
    static byte[] string_(final String v) {
        assert v != null;
        return v.getBytes(StandardCharsets.UTF_8);
    }

    static String string_(final byte[] v) {
        assert v != null;
        return new String(v, StandardCharsets.UTF_8);
    }

    // ------------------------------------------------------------------------------------------------------------ UUID
    private static byte[] uuid_16(final byte[] b, final int i, final UUID v) {
        assert b != null;
        assert i >= 0;
        assert i + (Long.BYTES << 1) <= b.length;
        assert v != null;
        long_8(b, i, v.getMostSignificantBits());
        long_8(b, i + Long.BYTES, v.getLeastSignificantBits());
        return b;
    }

    private static UUID uuid_16(final byte[] b, final int i) {
        assert b != null;
        assert i >= 0;
        assert i + (Long.BYTES << 1) <= b.length;
        return new UUID(
                long_8(b, i),
                long_8(b, i + Long.BYTES)
        );
    }

    static byte[] uuid_16(final UUID v) {
        return uuid_16(new byte[Long.BYTES << 1], 0, v);
    }

    static UUID uuid_16(final byte[] b) {
        return uuid_16(b, 0);
    }

    // ------------------------------------------------------------------------------------------------------- java.math
    static byte[] big_integer_(final BigInteger v) {
        assert v != null;
        return v.toByteArray();
    }

    static BigInteger big_integer_(final byte[] b) {
        return new BigInteger(b);
    }

    // ------------------------------------------------------------------------------------------------------- java.math
    static byte[] big_decimal_(final BigDecimal v) {
        assert v != null;
        final var encodedScale = int_4(v.scale());
        final var encodedUnscaledValue = big_integer_(v.unscaledValue());
        final var b = new byte[encodedScale.length + encodedUnscaledValue.length];
        System.arraycopy(encodedScale, 0, b, 0, encodedScale.length);
        System.arraycopy(encodedUnscaledValue, 0, b, encodedScale.length, encodedUnscaledValue.length);
        return b;
    }

    static BigDecimal big_decimal_(final byte[] b) {
        final var scale = int_4(b, 0);
        final var unscaledValue = big_integer_(Arrays.copyOfRange(b, Integer.BYTES, b.length));
        return new BigDecimal(unscaledValue, scale);
    }

    // ------------------------------------------------------------------------------------------------------- java.time
    private static byte[] local_date_8(final byte[] b, final int i, final LocalDate v) {
        assert v != null;
        assert i >= 0;
        assert i + Long.BYTES <= b.length;
        return long_8(b, i, v.toEpochDay());
    }

    private static LocalDate local_date_8(final byte[] b, final int i) {
        assert b != null;
        assert i >= 0;
        assert i + Long.BYTES <= b.length;
        return LocalDate.ofEpochDay(long_8(b, i));
    }

    static byte[] local_date_8(final LocalDate v) {
        return local_date_8(new byte[Long.BYTES], 0, v);
    }

    static LocalDate local_date_8(final byte[] b) {
        return local_date_8(b, 0);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private static byte[] local_time_8(final byte[] b, final int i, final LocalTime v) {
        return long_8(b, i, v.toNanoOfDay());
    }

    private static LocalTime local_time_8(final byte[] b, final int i) {
        return LocalTime.ofNanoOfDay(long_8(b, i));
    }

    static byte[] local_time_8(final LocalTime v) {
        return local_time_8(new byte[Long.BYTES], 0, v);
    }

    static LocalTime local_time_8(final byte[] b) {
        return LocalTime.ofNanoOfDay(long_8(b, 0));
    }

    // ----------------------------------------------------------------------------------------- java.time.LocalDateTime
    private static byte[] local_date_time_16(final byte[] b, final int i, final LocalDateTime v) {
        assert b != null;
        assert i >= 0;
        assert i + (Long.BYTES << 1) <= b.length;
        assert v != null;
        local_date_8(b, i, v.toLocalDate());
        local_time_8(b, i + Long.BYTES, v.toLocalTime());
        return b;
    }

    private static LocalDateTime local_date_time_16(final byte[] b, final int i) {
        assert b != null;
        assert i >= 0;
        assert i + (Long.BYTES << 1) <= b.length;
        return LocalDateTime.of(
                local_date_8(b, i),
                local_time_8(b, i + Long.BYTES)
        );
    }

    static byte[] local_date_time_16(final LocalDateTime v) {
        return local_date_time_16(new byte[Long.BYTES << 1], 0, v);
    }

    static LocalDateTime local_date_time_16(final byte[] b) {
        return local_date_time_16(b, 0);
    }

    // ------------------------------------------------------------------------------------------------ java.time.Offset
    private static byte[] offset_4(final byte[] b, final int i, final ZoneOffset v) {
        assert v != null;
        return int_4(b, i, v.getTotalSeconds());
    }

    private static ZoneOffset offset_4(final byte[] b, final int i) {
        return ZoneOffset.ofTotalSeconds(int_4(b, i));
    }

    static byte[] offset_4(final ZoneOffset v) {
        return offset_4(new byte[4], 0, v);
    }

    static ZoneOffset offset_4(final byte[] b) {
        return offset_4(b, 0);
    }

    // -------------------------------------------------------------------------------------------- java.time.OffsetTime
    private static byte[] offset_time_12(final byte[] b, final int i, final java.time.OffsetTime v) {
        return offset_4(
                local_time_8(
                        b,
                        i,
                        v.toLocalTime()
                ),
                Long.BYTES,
                v.getOffset()
        );
    }

    private static java.time.OffsetTime offset_time_12(final byte[] b, final int i) {
        return OffsetTime.of(
                local_time_8(b, i),
                offset_4(b, i + Long.BYTES)
        );
    }

    static byte[] offset_time_12(final java.time.OffsetTime v) {
        return offset_time_12(new byte[12], 0, v);
    }

    static java.time.OffsetTime offset_time_12(final byte[] b) {
        return offset_time_12(b, 0);
    }

    // ---------------------------------------------------------------------------------------- java.time.OffsetDateTime
    private static byte[] offset_date_time_20(final byte[] b, final int i, final OffsetDateTime v) {
        return offset_4(
                local_date_time_16(b, i, v.toLocalDateTime()),
                i + 16,
                v.getOffset()
        );
    }

    private static OffsetDateTime offset_date_time_20(final byte[] b, final int i) {
        return OffsetDateTime.of(
                local_date_time_16(b, i),
                offset_4(b, i + 16)
        );
    }

    static byte[] offset_date_time_20(final OffsetDateTime v) {
        return offset_date_time_20(new byte[20], 0, v);
    }

    static OffsetDateTime offset_date_time_20(final byte[] b) {
        return offset_date_time_20(b, 0);
    }

    // ----------------------------------------------------------------------------------------------- java.time.Instant
    private static byte[] instant_12(final byte[] b, final int i, final Instant v) {
        return int_4(
                long_8(b, i, v.getEpochSecond()),
                i + 8,
                v.getNano()
        );
    }

    private static Instant instant_12(final byte[] b, final int i) {
        return Instant.ofEpochSecond(
                long_8(b, i),
                int_4(b, i + 8)
        );
    }

    static byte[] instant_12(final Instant v) {
        return instant_12(new byte[12], 0, v);
    }

    static Instant instant_12(final byte[] b) {
        return instant_12(b, 0);
    }

    // -------------------------------------------------------------------------------------------------- java.time.Year
    private static byte[] year_4(final byte[] b, final int i, final Year v) {
        return int_4(b, i, v.getValue());
    }

    private static Year year_4(final byte[] b, final int i) {
        return Year.of(int_4(b, i));
    }

    static byte[] year_4(final Year v) {
        assert v != null;
        return year_4(new byte[4], 0, v);
    }

    static Year year_4(final byte[] b) {
        return year_4(b, 0);
    }

    // -------------------------------------------------------------------------------------------------- java.util.Date
    // https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#deprecations
    @Deprecated
    private static byte[] util_date_8(final byte[] b, final int i, final java.util.Date v) {
        assert b != null;
        assert i >= 0;
        assert i + 8 <= b.length;
        assert v != null;
        return long_8(b, i, v.getTime());
    }

    @Deprecated
    private static java.util.Date util_date_8(final byte[] b, final int i) {
        assert b != null;
        assert i >= 0;
        assert i + 8 <= b.length;
        return new java.util.Date(long_8(b, i));
    }

    @Deprecated
    static byte[] util_date_8(final java.util.Date v) {
        return util_date_8(new byte[8], 0, v);
    }

    @Deprecated
    static java.util.Date util_date_8(final byte[] b) {
        return util_date_8(b, 0);
    }

    // ---------------------------------------------------------------------------------------------- java.util.Calendar
    // https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#deprecations
    @Deprecated
    private static byte[] util_calendar_8(final byte[] b, final int i, final Calendar v) {
        assert b != null;
        assert i >= 0;
        assert i + 8 <= b.length;
        assert v != null;
        return util_date_8(b, i, v.getTime());
    }

    @Deprecated
    private static Calendar util_calendar_8(final byte[] b, final int i) {
        assert b != null;
        assert i >= 0;
        assert i + 8 <= b.length;
        final var v = Calendar.getInstance();
        v.setTime(util_date_8(b, i));
        return v;
    }

    @Deprecated
    static byte[] util_calendar_8(final Calendar v) {
        return util_calendar_8(new byte[8], 0, v);
    }

    @Deprecated
    static Calendar util_calendar_8(final byte[] b) {
        return util_calendar_8(b, 0);
    }

    // --------------------------------------------------------------------------------------------------- java.sql.Date
    // https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#deprecations
    @Deprecated
    private static byte[] sql_date_8(final byte[] b, final int i, final java.sql.Date v) {
        return long_8(b, i, v.getTime());
    }

    @Deprecated
    private static java.sql.Date sql_date_8(final byte[] b, final int i) {
        return new java.sql.Date(long_8(b, i));
    }

    @Deprecated
    static byte[] sql_date_8(final java.sql.Date v) {
        return sql_date_8(new byte[8], 0, v);
    }

    @Deprecated
    static java.sql.Date sql_date_8(final byte[] b) {
        return sql_date_8(b, 0);
    }

    // --------------------------------------------------------------------------------------------------- java.sql.Time
    // https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#deprecations
    @Deprecated
    private static byte[] sql_time_8(final byte[] b, final int i, final java.sql.Time v) {
        return long_8(b, i, v.getTime());
    }

    @Deprecated
    private static java.sql.Time sql_time_8(final byte[] b, final int i) {
        return new java.sql.Time(long_8(b, i));
    }

    @Deprecated
    static byte[] sql_time_8(final java.sql.Time v) {
        return sql_time_8(new byte[8], 0, v);
    }

    @Deprecated
    static java.sql.Time sql_time_8(final byte[] b) {
        return sql_time_8(b, 0);
    }

    // ---------------------------------------------------------------------------------------------- java.sql.Timestamp
    // https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#deprecations
    @Deprecated
    private static byte[] sql_timestamp_16(final byte[] b, final int i, final java.sql.Timestamp v) {
        assert b != null;
        assert i >= 0;
        assert i + 16 <= b.length;
        assert v != null;
        return instant_12(b, i, v.toInstant());
    }

    @Deprecated
    private static java.sql.Timestamp sql_timestamp_16(final byte[] b, final int i) {
        assert b != null;
        assert i >= 0;
        assert i + 16 <= b.length;
        return Timestamp.from(instant_12(b, i));
    }

    @Deprecated
    static byte[] sql_timestamp_16(final java.sql.Timestamp v) {
        return sql_timestamp_16(new byte[16], 0, v);
    }

    @Deprecated
    static java.sql.Timestamp sql_timestamp_16(final byte[] b) {
        return sql_timestamp_16(b, 0);
    }

    // ---------------------------------------------------------------------------------------------------------- byte[]

    // ---------------------------------------------------------------------------------------------------------- Byte[]
    @Deprecated
    static byte[] Bytes_l(final Byte[] v) {
        final var p = new byte[v.length];
        for (int i = 0; i < p.length; i++) {
            v[i] = p[i];
        }
        return p;
    }

    @Deprecated
    static Byte[] Bytes_l(final byte[] b) {
        final var v = new Byte[b.length];
        for (int i = 0; i < v.length; i++) {
            v[i] = b[i];
        }
        return v;
    }

    // ---------------------------------------------------------------------------------------------------------- char[]
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

    private static char[] chars_2l(final byte[] b, int i) {
        assert b != null;
        assert i >= 0;
        assert (b.length - i) % 2 == 0;
        final var v = new char[(b.length - i) >> 1];
        for (int j = 0; j < v.length; j++) {
            v[j] = (char) (
                    (b[i++] << Byte.SIZE)
                    | (b[i++] & 0xFF)
            );
        }
        return v;
    }

    static byte[] chars_2l(final char[] v) {
        return chars_2l(new byte[v.length << 1], 0, v);
    }

    static char[] chars_2l(final byte[] b) {
        return chars_2l(b, 0);
    }

    // ----------------------------------------------------------------------------------------------------- Character[]
    static byte[] Characters_2l(final Character[] v) {
        final var p = new char[v.length];
        for (int i = 0; i < p.length; i++) {
            p[i] = v[i];
        }
        return chars_2l(p);
    }

    static Character[] Characters_2l(final byte[] b) {
        final var p = chars_2l(b);
        final var v = new Character[p.length];
        for (int i = 0; i < v.length; i++) {
            v[i] = p[i];
        }
        return v;
    }

    // ------------------------------------------------------------------------------------------------------------ enum
    static byte[] enum_(final Enum<?> v) {
        assert v != null;
        final var name = v.name();
        return string_(name);
    }

    static <E extends Enum<E>> E enum_(final byte[] b, final Class<E> enumClass) {
        assert b != null;
        assert b.length > 0;
        assert enumClass != null;
        final var name = string_(b);
        return Enum.valueOf(enumClass, name);
    }

    // -----------------------------------------------------------------------------------------------------------------
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

    static <T extends Serializable> T serializable_(final byte[] b) {
        assert b != null;
        assert b.length > 0;
        try (var bais = new ByteArrayInputStream(b);
             var ois = new ObjectInputStream(bais)) {
            try {
                return (T) ois.readObject();
            } catch (final ClassNotFoundException cnfe) {
                throw new RuntimeException(cnfe);
            }
        } catch (final IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    private __EncryptionServiceUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
