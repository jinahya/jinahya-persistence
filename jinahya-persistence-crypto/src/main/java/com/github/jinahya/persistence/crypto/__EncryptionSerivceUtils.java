package com.github.jinahya.persistence.crypto;

import jakarta.validation.constraints.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
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
import java.util.Calendar;
import java.util.UUID;

final class __EncryptionSerivceUtils {

    // -----------------------------------------------------------------------------------------------------------------

    // -----------------------------------------------------------------------------------------------------------------
    static ByteBuffer boolean_1(final @NotNull ByteBuffer b, final boolean v) {
        assert b.remaining() >= 1;
        return b.put(v ? (byte) 1 : 0);
    }

    static boolean boolean_1(final ByteBuffer b) {
        assert b.remaining() >= 1;
        return b.get() != 0;
    }

    static byte[] boolean_1(final boolean v) {
        return new byte[]{
                (byte) (v ? 1 : 0)
        };
    }

    static boolean boolean_1(final byte[] b) {
        return b[0] == 1;
    }

    // -----------------------------------------------------------------------------------------------------------------
//    static byte[] byte_1(byte[] b, int i, final byte v) {
//        b[i] = v;
//        return b;
//    }
//
//    static byte byte_1(final byte[] b, int i) {
//        return b[i];
//    }

    // -----------------------------------------------------------------------------------------------------------------
    static ByteBuffer short_2(final ByteBuffer b, final short v) {
        return b.putShort(v);
    }

    static short short_2(final ByteBuffer b) {
        return b.getShort();
    }

    static byte[] short_2(final byte[] b, final int i, final short v) {
        b[i] = (byte) (v >> Byte.SIZE);
        b[i + 1] = (byte) (v & 0xFF);
        return b;
    }

    static short short_2(final byte[] b, final int i) {
        assert b != null;
        assert i >= 0;
        assert i + Short.BYTES <= b.length;
        return (short) (
                ((b[i] & 0xFF) << Byte.SIZE)
                | b[i + 1] & 0xFF
        );
    }

    // -----------------------------------------------------------------------------------------------------------------
    static ByteBuffer int_4(final ByteBuffer b, final int v) {
        return b.putInt(v);
    }

    static int int_4(final ByteBuffer b) {
        return b.getInt();
    }

    static byte[] int_4(final byte[] b, final int i, final int v) {
        assert b != null;
        assert i >= 0;
        assert i + Integer.BYTES <= b.length;
        short_2(b, i, (short) (v >> Short.SIZE));
        short_2(b, i + Short.BYTES, (short) (v & 0xFFFF));
        return b;
    }

    static int int_4(final byte[] b, final int i) {
        assert b != null;
        assert i >= 0;
        assert i + Integer.BYTES <= b.length;
        return (short_2(b, i) << Short.SIZE)
               | (short_2(b, i + Short.BYTES) & 0xFFFF);
    }

    // -----------------------------------------------------------------------------------------------------------------
    static ByteBuffer long_8(final ByteBuffer b, final long v) {
        return b.putLong(v);
    }

    static long long_8(final ByteBuffer b) {
        return b.getLong();
    }

    static byte[] long_8(final byte[] b, final int i, final long v) {
        assert b != null;
        assert i >= 0;
        assert i + Long.BYTES <= b.length;
        int_4(b, i, (int) (v >> Integer.SIZE));
        int_4(b, i + Integer.BYTES, (int) (v & 0xFFFFFFFFL));
        return b;
    }

    static long long_8(final byte[] b, final int i) {
        assert b != null;
        assert i >= 0;
        assert (i + Long.BYTES) <= b.length;
        return (((long) int_4(b, i)) << Integer.SIZE)
               | (int_4(b, i + Integer.BYTES) & 0xFFFFFFFFL);
    }

    // -----------------------------------------------------------------------------------------------------------------
    static ByteBuffer char_2(final ByteBuffer b, final char v) {
        return b.putChar(v);
    }

    static char char_2(final ByteBuffer b) {
        return b.getChar();
    }

    static byte[] char_2(final byte[] b, final int i, final char v) {
        return short_2(b, i, (short) v);
    }

    static char char_2(final byte[] b, final int i) {
        return (char) short_2(b, i);
    }

    // -----------------------------------------------------------------------------------------------------------------
    static ByteBuffer float_4(final ByteBuffer b, final float v) {
        return int_4(b, Float.floatToRawIntBits(v));
    }

    static float float_4(final ByteBuffer b) {
        return Float.intBitsToFloat(int_4(b));
    }

    static byte[] float_4(final byte[] b, final int i, final float v) {
        return int_4(b, i, Float.floatToRawIntBits(v));
    }

    static float float_4(final byte[] b, final int i) {
        return Float.intBitsToFloat(int_4(b, i));
    }

    // -----------------------------------------------------------------------------------------------------------------
    static ByteBuffer double_8(final ByteBuffer b, final double v) {
        return long_8(b, Double.doubleToRawLongBits(v));
    }

    static double double_8(final ByteBuffer b) {
        return Double.longBitsToDouble(long_8(b));
    }

    static byte[] double_8(final byte[] b, final int i, final double v) {
        return long_8(b, i, Double.doubleToRawLongBits(v));
    }

    static double double_8(final byte[] b, final int i) {
        return Double.longBitsToDouble(long_8(b, i));
    }

    // ------------------------------------------------------------------------------------------------ java.lang.String
//    static ByteBuffer string_4_( final ByteBuffer b, final  Charset charset,
//                                final  String v) {
//        return octets_4_(b, v.getBytes(charset));
//    }
//
//    static String string_4_( final ByteBuffer b, final  Charset charset) {
//        return new String(octets_4_(b), charset);
//    }
//
//    @Deprecated(forRemoval = true)
//    static ByteBuffer string_4_( final ByteBuffer b, final  String v) {
//        return string_4_(b, StandardCharsets.UTF_8, v);
//    }
//
//    @Deprecated(forRemoval = true)
//    static String string_4_( final ByteBuffer b) {
//        assert b.hasRemaining();
//        return string_4_(b, StandardCharsets.UTF_8);
//    }

    static byte[] string_(final String v) {
        return v.getBytes(StandardCharsets.UTF_8);
    }

    static String string_(final byte[] v) {
        return new String(v, StandardCharsets.UTF_8);
    }

    // ------------------------------------------------------------------------------------------------------------ UUID
    static ByteBuffer uuid_16(final ByteBuffer b, final UUID v) {
        return long_8(
                long_8(b, v.getMostSignificantBits()),
                v.getLeastSignificantBits()
        );
    }

    static UUID uuid_16(final ByteBuffer b) {
        return new UUID(
                long_8(b),
                long_8(b)
        );
    }

    static byte[] uuid_16(final UUID v) {
        final var b = new byte[Long.BYTES << 1];
        long_8(b, 0, v.getMostSignificantBits());
        long_8(b, Long.BYTES, v.getLeastSignificantBits());
        return b;
    }

    static UUID uuid_16(final byte[] b) {
        return new UUID(
                long_8(b, 0),
                long_8(b, Long.BYTES)
        );
    }

    // ------------------------------------------------------------------------------------------------------- java.math
    static byte[] big_integer_(final BigInteger v) {
        return v.toByteArray();
    }

    static BigInteger big_integer_(final byte[] b) {
        return new BigInteger(b);
    }

    static byte[] big_decimal_(final BigDecimal v) {
        return string_(v.toPlainString());
    }

    static BigDecimal big_decimal_(final byte[] v) {
        return new BigDecimal(string_(v));
    }

    // ------------------------------------------------------------------------------------------------------- java.time
    static ByteBuffer local_date_8(final ByteBuffer b, final LocalDate v) {
        return long_8(b, v.toEpochDay());
    }

    static LocalDate local_date_8(final ByteBuffer b) {
        return LocalDate.ofEpochDay(long_8(b));
    }

    static byte[] local_date_8(final byte[] b, final int i, final LocalDate v) {
        return long_8(b, i, v.toEpochDay());
    }

    static LocalDate local_date_8(final byte[] b, final int i) {
        return LocalDate.ofEpochDay(long_8(b, i));
    }

    // -----------------------------------------------------------------------------------------------------------------
    static ByteBuffer local_time_8(final ByteBuffer b, final LocalTime v) {
        return long_8(b, v.toNanoOfDay());
    }

    static LocalTime local_time_8(final ByteBuffer b) {
        return LocalTime.ofNanoOfDay(long_8(b));
    }

    static byte[] local_time_8(final byte[] b, final int i, final LocalTime v) {
        return long_8(b, i, v.toNanoOfDay());
    }

    static LocalTime local_time_8(final byte[] b, final int i) {
        return LocalTime.ofNanoOfDay(long_8(b, i));
    }

    // -----------------------------------------------------------------------------------------------------------------
    static ByteBuffer local_date_time_16(final ByteBuffer b, final LocalDateTime v) {
        return local_time_8(
                local_date_8(b, v.toLocalDate()),
                v.toLocalTime()
        );
    }

    static LocalDateTime local_date_time_16(final ByteBuffer b) {
        return LocalDateTime.of(
                local_date_8(b),
                local_time_8(b)
        );
    }

    static byte[] local_date_time_16(final byte[] b, final int i, final LocalDateTime v) {
        local_date_8(b, i, v.toLocalDate());
        local_time_8(b, i + Long.BYTES, v.toLocalTime());
        return b;
    }

    static LocalDateTime local_date_time_16(final byte[] b, final int i) {
        return LocalDateTime.of(
                local_date_8(b, i),
                local_time_8(b, i + Long.BYTES)
        );
    }

    // -----------------------------------------------------------------------------------------------------------------
    static ByteBuffer offset_4(final ByteBuffer b, final ZoneOffset v) {
        return int_4(b, v.getTotalSeconds());
    }

    static ZoneOffset offset_4(final ByteBuffer b) {
        return ZoneOffset.ofTotalSeconds(int_4(b));
    }

    static byte[] offset_4(final byte[] b, final int i, final ZoneOffset v) {
        return int_4(b, i, v.getTotalSeconds());
    }

    static ZoneOffset offset_4(final byte[] b, final int i) {
        return ZoneOffset.ofTotalSeconds(int_4(b, i));
    }

    // -----------------------------------------------------------------------------------------------------------------
    static ByteBuffer offset_time_12(final ByteBuffer b, final OffsetTime v) {
        return offset_4(
                local_time_8(b, v.toLocalTime()),
                v.getOffset()
        );
    }

    static OffsetTime offset_time_12(final ByteBuffer b) {
        return OffsetTime.of(
                local_time_8(b),
                offset_4(b)
        );
    }

    static byte[] offset_time_12(final byte[] b, final int i, final OffsetTime v) {
        return offset_4(
                local_time_8(
                        b,
                        0,
                        v.toLocalTime()
                ),
                Long.BYTES,
                v.getOffset()
        );
    }

    static OffsetTime offset_time_12(final byte[] b, final int i) {
        return OffsetTime.of(
                local_time_8(b, i),
                offset_4(b, Long.BYTES)
        );
    }

    // -----------------------------------------------------------------------------------------------------------------
    static ByteBuffer offset_date_time_20(final ByteBuffer b, final OffsetDateTime v) {
        return offset_4(
                local_date_time_16(b, v.toLocalDateTime()),
                v.getOffset()
        );
    }

    static OffsetDateTime offset_date_time_20(final ByteBuffer b) {
        return OffsetDateTime.of(
                local_date_time_16(b),
                offset_4(b)
        );
    }

    static byte[] offset_date_time_20(final byte[] b, final int i, final OffsetDateTime v) {
        return offset_4(
                local_date_time_16(b, i, v.toLocalDateTime()),
                i + (Long.BYTES << 1),
                v.getOffset()
        );
    }

    static OffsetDateTime offset_date_time_20(final byte[] b, final int i) {
        return OffsetDateTime.of(
                local_date_time_16(b, i),
                offset_4(b, i + (Long.BYTES << 1))
        );
    }

    // -----------------------------------------------------------------------------------------------------------------
    static ByteBuffer instant_16(final ByteBuffer b, final Instant v) {
        return long_8(
                long_8(b, v.getEpochSecond()),
                v.getNano()
        );
    }

    static Instant instant_16(final ByteBuffer b) {
        return Instant.ofEpochSecond(
                long_8(b),
                long_8(b)
        );
    }

    static byte[] instant_16(final byte[] b, final int i, final Instant v) {
        return long_8(
                long_8(b, i, v.getEpochSecond()),
                i + Long.BYTES,
                v.getNano()
        );
    }

    static Instant instant_16(final byte[] b, final int i) {
        return Instant.ofEpochSecond(
                long_8(b, i),
                long_8(b, i + Long.BYTES)
        );
    }

    // -----------------------------------------------------------------------------------------------------------------
    static ByteBuffer year_4(final ByteBuffer b, final Year v) {
        return int_4(b, v.getValue());
    }

    static Year year_4(final ByteBuffer b) {
        return Year.of(int_4(b));
    }

    static byte[] year_4(final byte[] b, final int i, final Year v) {
        return int_4(b, i, v.getValue());
    }

    static Year year_4(final byte[] b, final int i) {
        return Year.of(int_4(b, i));
    }

    // ------------------------------------------------------------------------------------------------------- java.util
    static ByteBuffer util_date_8(final ByteBuffer b, final java.util.Date v) {
        return long_8(b, v.getTime());
    }

    static java.util.Date util_date_8(final ByteBuffer b) {
        return new java.util.Date(long_8(b));
    }

    static byte[] util_date_8(final byte[] b, final int i, final java.util.Date v) {
        return long_8(b, i, v.getTime());
    }

    static java.util.Date util_date_8(final byte[] b, final int i) {
        return new java.util.Date(long_8(b, i));
    }

    // -----------------------------------------------------------------------------------------------------------------
    static ByteBuffer util_calendar_8(final ByteBuffer b, final Calendar v) {
        return util_date_8(b, v.getTime());
    }

    static Calendar util_calendar_8(final ByteBuffer b) {
        final var v = Calendar.getInstance();
        v.setTime(util_date_8(b));
        return v;
    }

    static byte[] util_calendar_8(final byte[] b, final int i, final Calendar v) {
        return util_date_8(b, i, v.getTime());
    }

    static Calendar util_calendar_8(final byte[] b, final int i) {
        final var v = Calendar.getInstance();
        v.setTime(util_date_8(b, i));
        return v;
    }

    // -------------------------------------------------------------------------------------------------------- java.sql
    static ByteBuffer sql_date_8(final ByteBuffer b, final java.sql.Date v) {
        return long_8(b, v.getTime());
    }

    static java.sql.Date sql_date_8(final ByteBuffer b) {
        return new java.sql.Date(long_8(b));
    }

    static byte[] sql_date_8(final byte[] b, final int i, final java.sql.Date v) {
        return long_8(b, i, v.getTime());
    }

    static java.sql.Date sql_date_8(final byte[] b, final int i) {
        return new java.sql.Date(long_8(b, i));
    }

    // -----------------------------------------------------------------------------------------------------------------
    static ByteBuffer sql_time_8(final ByteBuffer b, final java.sql.Time v) {
        return long_8(b, v.getTime());
    }

    static java.sql.Time sql_time_8(final ByteBuffer b) {
        return new java.sql.Time(long_8(b));
    }

    static byte[] sql_time_8(final byte[] b, final int i, final java.sql.Time v) {
        return long_8(b, i, v.getTime());
    }

    static java.sql.Time sql_time_8(final byte[] b, final int i) {
        return new java.sql.Time(long_8(b, i));
    }

    // -----------------------------------------------------------------------------------------------------------------
    static ByteBuffer sql_timestamp_8(final ByteBuffer b, final java.sql.Timestamp v) {
        return long_8(b, v.getTime());
    }

    static java.sql.Timestamp sql_timestamp_8(final ByteBuffer b) {
        return new java.sql.Timestamp(long_8(b));
    }

    static byte[] sql_timestamp_16(final byte[] b, final int i, final java.sql.Timestamp v) {
        return instant_16(b, i, v.toInstant());
    }

    static java.sql.Timestamp sql_timestamp_16(final byte[] b, final int i) {
        assert b != null;
        assert i >= 0;
        assert i + 16 <= b.length;
        return Timestamp.from(instant_16(b, i));
    }

    // --------------------------------------------------------------------- byte[], or Bytes[], char[], or Characters[]
    static ByteBuffer bytes_l(final ByteBuffer b, final byte[] v) {
        return b.put(v);
    }

    static byte[] bytes_l(final ByteBuffer b) {
        final var v = new byte[b.remaining()];
        b.get(v);
        return v;
    }

    static byte[] bytes_l(final byte[] v) {
        return v;
    }

    static ByteBuffer Bytes_l(final ByteBuffer b, final Byte[] v) {
        final var p = new byte[v.length];
        for (int i = 0; i < v.length; i++) {
            p[i] = v[i]; // may throw NullPointerException
        }
        return bytes_l(b, p);
    }

    static Byte[] Bytes_l(final ByteBuffer b) {
        final var p = bytes_l(b);
        final var v = new Byte[p.length];
        for (int i = 0; i < v.length; i++) {
            p[i] = v[i]; // may throw NullPointerException
        }
        return v;
    }

    static byte[] Bytes_l(final Byte[] v) {
        final var p = new byte[v.length];
        for (int i = 0; i < v.length; i++) {
            v[i] = p[i];
        }
        return p;
    }

    static Byte[] Bytes_l(final byte[] p) {
        final var v = new Byte[p.length];
        for (int i = 0; i < v.length; i++) {
            v[i] = p[i];
        }
        return v;
    }

    static ByteBuffer chars_2l(final ByteBuffer b, final char[] v) {
        b.asCharBuffer().put(v);
        return b.position(b.position() + v.length << 1);
    }

    static char[] chars_2l(final ByteBuffer b) {
        final var v = new char[b.remaining() >> 1];
        b.asCharBuffer().get(v);
        b.position(b.position() + (v.length << 1));
        return v;
    }

    static byte[] chars_2l(final char[] v) {
        final var b = new byte[v.length << 1];
        var j = 0;
        for (int i = 0; i < v.length; i++) {
            b[j++] = (byte) (v[i] >> Byte.SIZE);
            b[j++] = (byte) (v[i] & 0xFF);
        }
        return b;
    }

    static char[] chars_2l(final byte[] b) {
        final var v = new char[b.length >> 1];
        var j = 0;
        for (int i = 0; i < v.length; i++) {
            v[i] = (char) (((b[j++] << Byte.SIZE) & 0xFF) | (b[j++] & 0xFF));
        }
        return v;
    }

    static ByteBuffer Characters_2l(final ByteBuffer b, final Character[] v) {
        final var p = new char[v.length];
        for (int i = 0; i < p.length; i++) {
            p[i] = v[i];
        }
        return chars_2l(b, p);
    }

    static Character[] Characters_2l(final ByteBuffer b) {
        final var p = chars_2l(b);
        final var v = new Character[p.length];
        for (int i = 0; i < v.length; i++) {
            v[i] = p[i];
        }
        return v;
    }

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
        return string_(v.name());
    }

    static <E extends Enum<E>> E enum_(final byte[] v, final Class<E> enumClass) {
        return Enum.valueOf(enumClass, string_(v));
    }

    // -----------------------------------------------------------------------------------------------------------------
    static byte[] serializable_(final Serializable v) {
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
    private __EncryptionSerivceUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
