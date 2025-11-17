package com.github.jinahya.persistence.crypto;

import jakarta.annotation.Nonnull;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.EntityType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

abstract class __PersistenceCryptoProcessor<X> {

    // -----------------------------------------------------------------------------------------------------------------
    static ByteBuffer boolean_1(@Nonnull final ByteBuffer b, final boolean v) {
        return b.put(v ? (byte) 1 : 0);
    }

    static boolean boolean_1(@Nonnull final ByteBuffer b) {
        return b.get() == 0;
    }

    static ByteBuffer short_2(@Nonnull final ByteBuffer b, final short v) {
        return b.putShort(v);
    }

    static short short_2(@Nonnull final ByteBuffer b) {
        return b.getShort();
    }

    static ByteBuffer int_4(@Nonnull final ByteBuffer b, final int v) {
        return b.putInt(v);
    }

    static int int_4(@Nonnull final ByteBuffer b) {
        return b.getInt();
    }

    static ByteBuffer long_8(@Nonnull final ByteBuffer b, final long v) {
        return b.putLong(v);
    }

    static long long_8(@Nonnull final ByteBuffer b) {
        return b.getLong();
    }

    static ByteBuffer char_2(@Nonnull final ByteBuffer b, final char v) {
        return b.putChar(v);
    }

    static char char_2(@Nonnull final ByteBuffer b) {
        return b.getChar();
    }

    // -----------------------------------------------------------------------------------------------------------------
    static ByteBuffer float_4(final @Nonnull ByteBuffer b, final float v) {
        return int_4(b, Float.floatToRawIntBits(v));
    }

    static float float_4(final @Nonnull ByteBuffer b) {
        return Float.intBitsToFloat(int_4(b));
    }

    // -----------------------------------------------------------------------------------------------------------------
    static ByteBuffer double_8(@Nonnull final ByteBuffer b, final double v) {
        return long_8(b, Double.doubleToRawLongBits(v));
    }

    static double double_8(@Nonnull final ByteBuffer b) {
        return Double.longBitsToDouble(long_8(b));
    }

    // ------------------------------------------------------------------------------------------------ java.lang.String
    static ByteBuffer string_4_(@Nonnull final ByteBuffer b, final @Nonnull Charset charset,
                                final @Nonnull String v) {
        return bytes_4_(b, v.getBytes(charset));
    }

    static String string_4_(@Nonnull final ByteBuffer b, final @Nonnull Charset charset) {
        return new String(bytes_4_(b), charset);
    }

    static ByteBuffer string_4_(@Nonnull final ByteBuffer b, final @Nonnull String v) {
        return string_4_(b, StandardCharsets.UTF_8, v);
    }

    static String string_4_(@Nonnull final ByteBuffer b) {
        return string_4_(b, StandardCharsets.UTF_8);
    }

    // ------------------------------------------------------------------------------------------------------------ UUID
    static ByteBuffer uuid_(final @Nonnull ByteBuffer b, final @Nonnull UUID v) {
        return long_8(
                long_8(b, v.getMostSignificantBits()),
                v.getLeastSignificantBits()
        );
    }

    static UUID uuid_(@Nonnull final ByteBuffer b) {
        return new UUID(
                long_8(b),
                long_8(b)
        );
    }

    // ------------------------------------------------------------------------------------------------------- java.math
    static ByteBuffer big_integer_(@Nonnull final ByteBuffer b, final @Nonnull BigInteger v) {
        return bytes_4_(b, v.toByteArray());
    }

    static BigInteger big_integer_8_(@Nonnull final ByteBuffer b) {
        return new BigInteger(bytes_4_(b));
    }

    static ByteBuffer big_decimal_(@Nonnull final ByteBuffer b, final @Nonnull BigDecimal v) {
        return string_4_(b, v.toPlainString());
    }

    static BigDecimal big_decimal_(@Nonnull final ByteBuffer b) {
        return new BigDecimal(string_4_(b));
    }

    // ------------------------------------------------------------------------------------------------------- java.time
    static ByteBuffer local_date_8(@Nonnull final ByteBuffer b, final LocalDate v) {
        return long_8(b, v.toEpochDay());
    }

    static LocalDate local_date_8(@Nonnull final ByteBuffer b) {
        return LocalDate.ofEpochDay(long_8(b));
    }

    static ByteBuffer local_Time_8(@Nonnull final ByteBuffer b, final LocalTime v) {
        return long_8(b, v.toNanoOfDay());
    }

    static LocalTime local_Time_8(@Nonnull final ByteBuffer b) {
        return LocalTime.ofNanoOfDay(long_8(b));
    }

    static ByteBuffer local_date_time_16(@Nonnull final ByteBuffer b, final LocalDateTime v) {
        return local_Time_8(
                local_date_8(b, v.toLocalDate()),
                v.toLocalTime()
        );
    }

    static LocalDateTime local_date_time_16(@Nonnull final ByteBuffer b) {
        return LocalDateTime.of(
                local_date_8(b),
                local_Time_8(b)
        );
    }

    static ByteBuffer offset_4(@Nonnull final ByteBuffer b, final ZoneOffset v) {
        return int_4(b, v.getTotalSeconds());
    }

    static ZoneOffset offset_4(@Nonnull final ByteBuffer b) {
        return ZoneOffset.ofTotalSeconds(int_4(b));
    }

    static ByteBuffer offset_time_12(@Nonnull final ByteBuffer b, final OffsetTime v) {
        return offset_4(
                local_Time_8(b, v.toLocalTime()),
                v.getOffset()
        );
    }

    static OffsetTime offset_time_12(@Nonnull final ByteBuffer b) {
        return OffsetTime.of(
                local_Time_8(b),
                offset_4(b)
        );
    }

    static ByteBuffer offset_date_time_20(@Nonnull final ByteBuffer b, final OffsetDateTime v) {
        return offset_4(
                local_date_time_16(b, v.toLocalDateTime()),
                v.getOffset()
        );
    }

    static OffsetDateTime offset_date_time_20(@Nonnull final ByteBuffer b) {
        return OffsetDateTime.of(
                local_date_time_16(b),
                offset_4(b)
        );
    }

    static ByteBuffer instant_16(@Nonnull final ByteBuffer b, final Instant v) {
        return long_8(
                long_8(b, v.getEpochSecond()),
                v.getNano()
        );
    }

    static Instant instant_16(@Nonnull final ByteBuffer b) {
        return Instant.ofEpochSecond(
                long_8(b),
                long_8(b)
        );
    }

    static ByteBuffer year_4(@Nonnull final ByteBuffer b, final Year v) {
        return int_4(b, v.getValue());
    }

    static Year year_4(@Nonnull final ByteBuffer b) {
        return Year.of(int_4(b));
    }

    // ------------------------------------------------------------------------------------------------------- java.util
    static ByteBuffer util_date_8(@Nonnull final ByteBuffer b, final java.util.Date v) {
        return long_8(b, v.getTime());
    }

    static java.util.Date util_date_8(@Nonnull final ByteBuffer b) {
        return new java.util.Date(long_8(b));
    }

    static ByteBuffer util_calendar_8(@Nonnull final ByteBuffer b, final Calendar v) {
        return util_date_8(b, v.getTime());
    }

    static Calendar util_calendar_8(@Nonnull final ByteBuffer b) {
        final var v = Calendar.getInstance();
        v.setTime(util_date_8(b));
        return v;
    }

    // -------------------------------------------------------------------------------------------------------- java.sql
    static ByteBuffer sql_date_8(@Nonnull final ByteBuffer b, final java.sql.Date v) {
        return long_8(b, v.getTime());
    }

    static java.sql.Date sql_date_8(@Nonnull final ByteBuffer b) {
        return new java.sql.Date(long_8(b));
    }

    static ByteBuffer sql_time_8(@Nonnull final ByteBuffer b, final java.sql.Time v) {
        return long_8(b, v.getTime());
    }

    static java.sql.Time sql_time_8(@Nonnull final ByteBuffer b) {
        return new java.sql.Time(long_8(b));
    }

    static ByteBuffer sql_timestamp_8(@Nonnull final ByteBuffer b, final java.sql.Timestamp v) {
        return long_8(b, v.getTime());
    }

    static java.sql.Timestamp sql_timestamp_8(@Nonnull final ByteBuffer b) {
        return new java.sql.Timestamp(long_8(b));
    }

    // -----------------------------------------------------------------------------------------------------------------
    static ByteBuffer bytes_4_(final @Nonnull ByteBuffer b, final @Nonnull byte[] v) {
        return int_4(b, v.length).put(v);
    }

    static byte[] bytes_4_(final @Nonnull ByteBuffer b) {
        final var v = new byte[int_4(b)];
        b.get(v);
        return v;
    }

    static ByteBuffer Bytes_4_(final @Nonnull ByteBuffer b, final @Nonnull Byte[] v) {
        int_4(b, v.length);
        for (var e : v) {
            boolean_1(b, e != null); // null -> 0; !null -> 1
            if (e != null) {
                b.put(e);
            }
        }
        return b;
    }

    static Byte[] Bytes_4_(final @Nonnull ByteBuffer b) {
        final var v = new Byte[int_4(b)];
        for (int i = 0; i < v.length; i++) {
            if (boolean_1(b)) {
                v[i] = b.get();
            }
        }
        return v;
    }

    static ByteBuffer chars_4_(final @Nonnull ByteBuffer b, final @Nonnull char[] v) {
        int_4(b, v.length);
        b.asCharBuffer().put(v);
        return b;
    }

    static char[] chars_4_(final @Nonnull ByteBuffer b) {
        final var v = new char[int_4(b)];
        b.asCharBuffer().get(v);
        return v;
    }

    static ByteBuffer Characters_4_(final @Nonnull ByteBuffer b, final @Nonnull Character[] v) {
        int_4(b, v.length);
        final var c = b.asCharBuffer();
        for (var e : v) {
            boolean_1(b, e != null);
            if (e != null) {
                c.put(e);
            }
        }
        return b;
    }

    static Character[] Characters_4_(@Nonnull final ByteBuffer b) {
        final var v = new Character[int_4(b)];
        final var c = b.asCharBuffer();
        for (int i = 0; i < v.length; i++) {
            if (boolean_1(b)) {
                v[i] = c.get();
            }
        }
        return v;
    }

    // ------------------------------------------------------------------------------------------------------------ enum
    static ByteBuffer enum_(@Nonnull final ByteBuffer b, final @Nonnull Enum<?> v) {
        return string_4_(b, v.name());
    }

    static <E extends Enum<E>> E enum_(@Nonnull final ByteBuffer b, final @Nonnull Class<E> enumClass) {
        return Enum.valueOf(enumClass, string_4_(b));
    }

    // -----------------------------------------------------------------------------------------------------------------
    static ByteBuffer serializable_4_(final @Nonnull ByteBuffer b, final @Nonnull Serializable v) {
        try (var baos = new ByteArrayOutputStream();
             var oos = new ObjectOutputStream(baos)) {
            oos.writeObject(v);
            oos.flush();
            return bytes_4_(b, baos.toByteArray());
        } catch (final IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    static <T extends Serializable> T serializable_4_(final @Nonnull ByteBuffer b) {
        try (var bais = new ByteArrayInputStream(bytes_4_(b));
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

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    __PersistenceCryptoProcessor(final @Nonnull EntityType<X> entityType,
                                 final @Nonnull __PersistenceCryptoManager manager) {
        super();
        this.entityType = Objects.requireNonNull(entityType, "entityType is null");
        attributes = this.entityType.getAttributes()
                .stream()
                .collect(Collectors.toUnmodifiableMap(Attribute::getName, Function.identity()));
        this.manager = Objects.requireNonNull(manager, "manager is null");
    }

    // -----------------------------------------------------------------------------------------------------------------
    abstract void process(final @Nonnull X entity, final @Nonnull String identifier);

    // ------------------------------------------------------------------------------------------------------ entityType

    // ------------------------------------------------------------------------------------------------------ attributes

    // --------------------------------------------------------------------------------------------------------- manager

    // -----------------------------------------------------------------------------------------------------------------
    final EntityType<X> entityType;

    final Map<String, Attribute<? super X, ?>> attributes;

    // -------------------------------------------------------------------------------------------------------------
    final __PersistenceCryptoManager manager;
}
