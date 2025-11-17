package com.github.jinahya.persistence.crypto;

import com.github.jinahya.persistence.metamodel.JinahyaAttributeUtils;
import com.github.jinahya.persistence.metamodel.JinahyaEntityTypeUtils;
import jakarta.annotation.Nonnull;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.Shutdown;
import jakarta.enterprise.event.Startup;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.persistence.Basic;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.EntityType;
import jakarta.validation.constraints.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.invoke.MethodHandles;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * .
 *
 * @see <a href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#a486">2.6. Basic
 *         Types</a> (Jakarta Persistence 3.2 Specification Document)
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __PersistenceCryptoService {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // -----------------------------------------------------------------------------------------------------------------
    static ByteBuffer boolean_1(@Nonnull final ByteBuffer b, final boolean v) {
        return b.put(v ? (byte) 1 : 0);
    }

    static boolean boolean_1(@Nonnull final ByteBuffer b) {
        return b.get() != 0;
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

    @Deprecated(forRemoval = true)
    static ByteBuffer string_4_(@Nonnull final ByteBuffer b, final @Nonnull String v) {
        return string_4_(b, StandardCharsets.UTF_8, v);
    }

    @Deprecated(forRemoval = true)
    static String string_4_(@Nonnull final ByteBuffer b) {
        assert b.hasRemaining();
        return string_4_(b, StandardCharsets.UTF_8);
    }

    // ------------------------------------------------------------------------------------------------------------ UUID
    static ByteBuffer uuid_16(final @Nonnull ByteBuffer b, final @Nonnull UUID v) {
        return long_8(
                long_8(b, v.getMostSignificantBits()),
                v.getLeastSignificantBits()
        );
    }

    static UUID uuid_16(@Nonnull final ByteBuffer b) {
        return new UUID(
                long_8(b),
                long_8(b)
        );
    }

    // ------------------------------------------------------------------------------------------------------- java.math
    static ByteBuffer big_integer_(@Nonnull final ByteBuffer b, final @Nonnull BigInteger v) {
        return bytes_4_(b, v.toByteArray());
    }

    static BigInteger big_integer_(@Nonnull final ByteBuffer b) {
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

    static ByteBuffer local_time_8(@Nonnull final ByteBuffer b, final LocalTime v) {
        return long_8(b, v.toNanoOfDay());
    }

    static LocalTime local_time_8(@Nonnull final ByteBuffer b) {
        return LocalTime.ofNanoOfDay(long_8(b));
    }

    static ByteBuffer local_date_time_16(@Nonnull final ByteBuffer b, final LocalDateTime v) {
        return local_time_8(
                local_date_8(b, v.toLocalDate()),
                v.toLocalTime()
        );
    }

    static LocalDateTime local_date_time_16(@Nonnull final ByteBuffer b) {
        return LocalDateTime.of(
                local_date_8(b),
                local_time_8(b)
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
                local_time_8(b, v.toLocalTime()),
                v.getOffset()
        );
    }

    static OffsetTime offset_time_12(@Nonnull final ByteBuffer b) {
        return OffsetTime.of(
                local_time_8(b),
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
        assert b.hasRemaining();
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
        for (var e : v) {
            b.put((byte) ((e >> Byte.SIZE) & 0xFF));
            b.put((byte) (e & 0xFF));
        }
        return b;
    }

    static char[] chars_4_(final @Nonnull ByteBuffer b) {
        final var v = new char[int_4(b)];
        for (int i = 0; i < v.length; i++) {
            v[i] = (char) (((b.get() & 0xFF) << Byte.SIZE) | (b.get() & 0xFF));
        }
        return v;
    }

    static ByteBuffer Characters_4_(final @Nonnull ByteBuffer b, final @Nonnull Character[] v) {
        int_4(b, v.length);
        for (var e : v) {
            boolean_1(b, e != null);
            if (e != null) {
                b.put((byte) ((e >> Byte.SIZE) & 0xFF));
                b.put((byte) (e & 0xFF));
            }
        }
        return b;
    }

    static Character[] Characters_4_(@Nonnull final ByteBuffer b) {
        final var v = new Character[int_4(b)];
        for (int i = 0; i < v.length; i++) {
            if (boolean_1(b)) {
                v[i] = (char) (((b.get() & 0xFF) << Byte.SIZE) | (b.get() & 0xFF));
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
    @Deprecated(forRemoval = true)
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

    @Deprecated(forRemoval = true)
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

    // -----------------------------------------------------------------------------------------------------------------
    private static final Map<EntityType<?>, Map<String, Attribute<?, ?>>> ATTRIBUTES = new ConcurrentHashMap<>();

    protected static Map<String, Attribute<?, ?>> getAttributes(@Nonnull final EntityType<?> entityType) {
        Objects.requireNonNull(entityType, "entityType is null");
        return ATTRIBUTES.computeIfAbsent(
                entityType,
                k -> k.getAttributes().stream()
                        .collect(Collectors.toUnmodifiableMap(Attribute::getName, Function.identity()))
        );
    }

    // -------------------------------------------------------------------------------------------------------- BUILDERS

    // ------------------------------------------------------------------------------------------ STATIC_FACTORY_METHODS

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    protected __PersistenceCryptoService() {
        super();
    }

    // -----------------------------------------------------------------------------------------------------------------

    // -----------------------------------------------------------------------------------------------------------------
    @PostConstruct
    protected void onPostConstruct() {
        logger.log(System.Logger.Level.DEBUG, "onPostConstruct()");
        logger.log(System.Logger.Level.DEBUG, "factoryInstance: {0}", factoryInstance);
        logger.log(System.Logger.Level.DEBUG, "cryptoManager: {0}", cryptoManager);
    }

    // https://stackoverflow.com/a/72628439/330457
    protected void onStartup(@Observes final Startup startup) {
        logger.log(System.Logger.Level.DEBUG, "onStartup({0})", startup);
    }

    @PreDestroy
    protected void onPreDestroy() {
        logger.log(System.Logger.Level.DEBUG, "onPreDestroy()");
    }

    // https://stackoverflow.com/a/72628439/330457
    protected void onShutdown(@Observes final Shutdown shutdown) {
        logger.log(System.Logger.Level.DEBUG, "onShutdown({0})", shutdown);
    }

    // -----------------------------------------------------------------------------------------------------------------
    protected void encrypt(final @Nonnull @NotNull Object entity) {
        Objects.requireNonNull(entity, "entity is null");
        final var cryptoIdentifier = cryptoManager.getCryptoIdentifier(entity);
        final var entityClass = entity.getClass();
        final var entityType = getEntityType(entityClass);
        final var attributes = new HashMap<>(getAttributes(entityType));
        for (final var entry : attributes.entrySet()) {
            final var name = entry.getKey();
            final var decryptedAttribute = entry.getValue();
            final var persistenceAttributeType = decryptedAttribute.getPersistentAttributeType();
            if (persistenceAttributeType != Attribute.PersistentAttributeType.BASIC) {
                continue;
            }
            final var javaType = decryptedAttribute.getJavaType();
            final var annotation =
                    JinahyaAttributeUtils.getJavaMemberAnnotation(decryptedAttribute, __EncryptedAttribute.class);
            if (annotation == null) {
                continue;
            }
            final var encryptedAttribute = Optional.of(annotation.value())
                    .map(attributes::get)
                    .orElseThrow(() -> new RuntimeException(
                            "no encrypted attribute found" +
                            "; attribute: " + attributes +
                            "; annotation: " + annotation
                    ));
            if (encryptedAttribute == decryptedAttribute) {
                throw new RuntimeException(
                        "encrypted attribute cannot be same as decrypted attribute" +
                        "; decrypted attribute: " + decryptedAttribute +
                        "; encrypted attribute: " + encryptedAttribute);
            }
            {
                final var encryptedAttributeOptional =
                        Optional.ofNullable(
                                        JinahyaAttributeUtils.getJavaMemberAnnotation(encryptedAttribute, Basic.class))
                                .map(Basic::optional)
                                .orElse(true);
                if (!encryptedAttributeOptional) {
                    throw new RuntimeException(
                            "decrypted attribute should be optional" +
                            "; decrypted attribute: " + decryptedAttribute +
                            "; annotation: " + annotation +
                            "; encrypted attribute: " + encryptedAttribute
                    );
                }
            }
            {
                final var encryptedValue = JinahyaAttributeUtils.getAttributeValue(entity, encryptedAttribute);
                if (encryptedValue != null) {
                    logger.log(System.Logger.Level.DEBUG,
                               "seems already encrypted" +
                               "; decrypted attribute: " + decryptedAttribute +
                               "; annotation: " + annotation +
                               "; decrypted attribute: " + decryptedAttribute);
                    continue;
                }
            }
            final var decryptedAttributeOptional =
                    Optional.ofNullable(JinahyaAttributeUtils.getJavaMemberAnnotation(decryptedAttribute, Basic.class))
                            .map(Basic::optional)
                            .orElse(true);
            final var decryptedValue = JinahyaAttributeUtils.getAttributeValue(entity, decryptedAttribute);
            if (decryptedValue == null) {
                JinahyaAttributeUtils.setAttributeValue(entity, encryptedAttribute, null);
                continue;
            }
            final byte[] decryptedBytes;
            if (javaType == byte.class || javaType == Byte.class) {
                decryptedBytes = new byte[]{(byte) decryptedValue};
            } else if (javaType == short.class || javaType == Short.class) {
                decryptedBytes = short_2(ByteBuffer.allocate(Short.BYTES), (short) decryptedValue).array();
            } else if (javaType == int.class || javaType == Integer.class) {
                decryptedBytes = int_4(ByteBuffer.allocate(Integer.BYTES), (int) decryptedValue).array();
            } else if (javaType == long.class || javaType == Long.class) {
                decryptedBytes = long_8(ByteBuffer.allocate(Long.BYTES), (long) decryptedValue).array();
            } else if (javaType == char.class || javaType == Character.class) {
                decryptedBytes = char_2(ByteBuffer.allocate(Character.BYTES), (char) decryptedValue).array();
            } else if (javaType == float.class || javaType == Float.class) {
                decryptedBytes = float_4(ByteBuffer.allocate(Float.BYTES), (float) decryptedValue).array();
            } else if (javaType == double.class || javaType == Double.class) {
                decryptedBytes = double_8(ByteBuffer.allocate(Double.BYTES), (double) decryptedValue).array();
            } else if (javaType == String.class) {
                final var string = (String) decryptedValue;
                decryptedBytes = string.getBytes(StandardCharsets.UTF_8);
            } else if (javaType == UUID.class) {
                decryptedBytes = uuid_16(ByteBuffer.allocate(16), (UUID) decryptedValue).array();
            } else if (javaType == BigInteger.class) {
                decryptedBytes = ((BigInteger) decryptedValue).toByteArray();
            } else if (javaType == BigDecimal.class) {
                decryptedBytes = ((BigDecimal) decryptedValue).toPlainString().getBytes(StandardCharsets.UTF_8);
            } else if (javaType == LocalDate.class) {
                decryptedBytes = local_date_8(ByteBuffer.allocate(8), (LocalDate) decryptedValue).array();
            } else if (javaType == LocalTime.class) {
                decryptedBytes = local_time_8(ByteBuffer.allocate(8), (LocalTime) decryptedValue).array();
            } else if (javaType == LocalDateTime.class) {
                decryptedBytes = local_date_time_16(ByteBuffer.allocate(16), (LocalDateTime) decryptedValue).array();
            } else if (javaType == OffsetTime.class) {
                decryptedBytes = offset_time_12(ByteBuffer.allocate(12), (OffsetTime) decryptedValue).array();
            } else if (javaType == OffsetDateTime.class) {
                decryptedBytes = offset_date_time_20(ByteBuffer.allocate(20), (OffsetDateTime) decryptedValue).array();
            } else if (javaType == Instant.class) {
                decryptedBytes = instant_16(ByteBuffer.allocate(16), (Instant) decryptedValue).array();
            } else if (javaType == Year.class) {
                decryptedBytes = year_4(ByteBuffer.allocate(4), (Year) decryptedValue).array();
            } else if (javaType == java.util.Date.class) {
                decryptedBytes = util_date_8(ByteBuffer.allocate(8), (java.util.Date) decryptedValue).array();
            } else if (javaType == java.util.Calendar.class) {
                decryptedBytes = util_calendar_8(ByteBuffer.allocate(8), (java.util.Calendar) decryptedValue).array();
            } else if (javaType == java.sql.Date.class) {
                decryptedBytes = sql_date_8(ByteBuffer.allocate(8), (java.sql.Date) decryptedValue).array();
            } else if (javaType == java.sql.Time.class) {
                decryptedBytes = sql_time_8(ByteBuffer.allocate(8), (java.sql.Time) decryptedValue).array();
            } else if (javaType == java.sql.Timestamp.class) {
                decryptedBytes = sql_timestamp_8(ByteBuffer.allocate(8), (java.sql.Timestamp) decryptedValue).array();
            } else if (javaType == byte[].class) {
                decryptedBytes = (byte[]) decryptedValue;
            } else if (javaType == Byte[].class) {
                final var cast = (Byte[]) decryptedValue;
                decryptedBytes = Bytes_4_(ByteBuffer.allocate(4 + (cast.length << 1)), cast).array();
            } else if (javaType == char[].class) {
                final var cast = (char[]) decryptedValue;
                decryptedBytes = chars_4_(ByteBuffer.allocate(4 + (cast.length << 1)), cast).array();
            } else if (javaType == Character[].class) {
                final var cast = (Character[]) decryptedValue;
                decryptedBytes = Characters_4_(ByteBuffer.allocate(4 + (cast.length * 3)), cast).array();
            } else if (javaType.isEnum()) {
                decryptedBytes = ((Enum<?>) decryptedValue).name().getBytes(StandardCharsets.UTF_8);
            } else if (Serializable.class.isAssignableFrom(javaType)) {
                // not platform-independent
                throw new RuntimeException("unsupported java type: " + javaType);
            } else {
                throw new RuntimeException("unsupported java type: " + javaType);
            }
            final var encrypted = cryptoManager.encrypt(cryptoIdentifier, decryptedBytes);
            JinahyaAttributeUtils.setAttributeValue(entity, encryptedAttribute, encrypted);
            if (decryptedAttributeOptional) {
                JinahyaAttributeUtils.setAttributeValue(entity, decryptedAttribute, null);
            }
        }
    }

    protected void decrypt(final @Nonnull @NotNull Object entity) {
        Objects.requireNonNull(entity, "entity is null");
        final var cryptoIdentifier = cryptoManager.getCryptoIdentifier(entity);
        final var entityClass = entity.getClass();
        final var entityType = getEntityType(entityClass);
        final var attributes = new HashMap<>(getAttributes(entityType));
        for (final var entry : attributes.entrySet()) {
            final var name = entry.getKey();
            final var decryptedAttribute = entry.getValue();
            final var persistenceAttributeType = decryptedAttribute.getPersistentAttributeType();
            if (persistenceAttributeType != Attribute.PersistentAttributeType.BASIC) {
                continue;
            }
            final var javaType = decryptedAttribute.getJavaType();
            final var annotation =
                    JinahyaAttributeUtils.getJavaMemberAnnotation(decryptedAttribute, __EncryptedAttribute.class);
            if (annotation == null) {
                continue;
            }
            final var encryptedAttribute = Optional.of(annotation.value())
                    .map(attributes::get)
                    .orElseThrow(() -> new RuntimeException(
                            "no encrypted attribute found" +
                            "; attribute: " + attributes +
                            "; annotation: " + annotation
                    ));
            if (encryptedAttribute == decryptedAttribute) {
                throw new RuntimeException(
                        "encrypted attribute cannot be same as encrypted attribute" +
                        "; encrypted attribute: " + decryptedAttribute +
                        "; encrypted attribute: " + encryptedAttribute);
            }
            final var encryptedAttributeOptional =
                    Optional.ofNullable(JinahyaAttributeUtils.getJavaMemberAnnotation(decryptedAttribute, Basic.class))
                            .map(Basic::optional)
                            .orElse(true);
            final var encryptedBytes = (byte[]) JinahyaAttributeUtils.getAttributeValue(entity, encryptedAttribute);
            if (encryptedBytes == null) {
                JinahyaAttributeUtils.setAttributeValue(entity, decryptedAttribute, null);
                continue;
            }
            final var decryptedBytes = cryptoManager.decrypt(cryptoIdentifier, encryptedBytes);
            final Object decryptedValue;
            if (javaType == byte.class || javaType == Byte.class) {
                decryptedValue = decryptedBytes[0];
            } else if (javaType == short.class || javaType == Short.class) {
                decryptedValue = short_2(ByteBuffer.wrap(decryptedBytes));
            } else if (javaType == int.class || javaType == Integer.class) {
                decryptedValue = int_4(ByteBuffer.wrap(decryptedBytes));
            } else if (javaType == long.class || javaType == Long.class) {
                decryptedValue = long_8(ByteBuffer.wrap(decryptedBytes));
            } else if (javaType == char.class || javaType == Character.class) {
                decryptedValue = char_2(ByteBuffer.wrap(decryptedBytes));
            } else if (javaType == float.class || javaType == Float.class) {
                decryptedValue = float_4(ByteBuffer.wrap(decryptedBytes));
            } else if (javaType == double.class || javaType == Double.class) {
                decryptedValue = double_8(ByteBuffer.wrap(decryptedBytes));
            } else if (javaType == String.class) {
                decryptedValue = new String(decryptedBytes, StandardCharsets.UTF_8);
            } else if (javaType == UUID.class) {
                decryptedValue = uuid_16(ByteBuffer.wrap(decryptedBytes));
            } else if (javaType == BigInteger.class) {
                decryptedValue = new BigInteger(decryptedBytes);
            } else if (javaType == BigDecimal.class) {
                decryptedValue = new BigDecimal(new String(decryptedBytes, StandardCharsets.UTF_8));
            } else if (javaType == LocalDate.class) {
                decryptedValue = local_date_8(ByteBuffer.wrap(decryptedBytes));
            } else if (javaType == LocalTime.class) {
                decryptedValue = local_time_8(ByteBuffer.wrap((decryptedBytes)));
            } else if (javaType == LocalDateTime.class) {
                decryptedValue = local_date_time_16(ByteBuffer.wrap(decryptedBytes));
            } else if (javaType == OffsetTime.class) {
                decryptedValue = offset_time_12(ByteBuffer.wrap(decryptedBytes));
            } else if (javaType == OffsetDateTime.class) {
                decryptedValue = offset_date_time_20(ByteBuffer.wrap(decryptedBytes));
            } else if (javaType == Instant.class) {
                decryptedValue = instant_16(ByteBuffer.wrap(decryptedBytes));
            } else if (javaType == Year.class) {
                decryptedValue = year_4(ByteBuffer.wrap(decryptedBytes));
            } else if (javaType == java.util.Date.class) {
                decryptedValue = util_date_8(ByteBuffer.wrap(decryptedBytes));
            } else if (javaType == Calendar.class) {
                decryptedValue = util_calendar_8(ByteBuffer.wrap(decryptedBytes));
            } else if (javaType == java.sql.Date.class) {
                decryptedValue = sql_date_8(ByteBuffer.wrap(decryptedBytes));
            } else if (javaType == java.sql.Time.class) {
                decryptedValue = sql_time_8(ByteBuffer.wrap(decryptedBytes));
            } else if (javaType == java.sql.Timestamp.class) {
                decryptedValue = sql_timestamp_8(ByteBuffer.wrap(decryptedBytes));
            } else if (javaType == byte[].class) {
                decryptedValue = decryptedBytes;
            } else if (javaType == Byte[].class) {
                decryptedValue = Bytes_4_(ByteBuffer.wrap(decryptedBytes));
            } else if (javaType == char[].class) {
                decryptedValue = chars_4_(ByteBuffer.wrap(decryptedBytes));
            } else if (javaType == Character[].class) {
                decryptedValue = Characters_4_(ByteBuffer.wrap(decryptedBytes));
            } else if (javaType.isEnum()) {
                decryptedValue = Enum.valueOf((Class) javaType, new String(decryptedBytes, StandardCharsets.UTF_8));
            } else if (Serializable.class.isAssignableFrom(javaType)) {
                // not platform-independent
                throw new RuntimeException("unsupported java type: " + javaType);
            } else {
                throw new RuntimeException("unsupported java type: " + javaType);
            }
            JinahyaAttributeUtils.setAttributeValue(entity, decryptedAttribute, decryptedValue);
            if (encryptedAttributeOptional) {
                JinahyaAttributeUtils.setAttributeValue(entity, encryptedAttribute, null);
            }
        }
    }

    // ----------------------------------------------------------------------------------------------------- entityTypes

    // ------------------------------------------------------------------------------------ entityManagerFactoryInstance
    protected @Nonnull EntityType<?> getEntityType(@Nonnull final Class<?> entityClass) {
        return entityTypes.computeIfAbsent(
                Objects.requireNonNull(entityClass, "entityClass is null"),
                k -> JinahyaEntityTypeUtils.getEntityType(k, factoryInstance)
        );
    }

    // ----------------------------------------------------------------------------------------------------- entityTypes

    // ------------------------------------------------------------------------------------------------- factoryInstance

    // --------------------------------------------------------------------------------------------------- cryptoManager

    // -----------------------------------------------------------------------------------------------------------------
    private final Map<Class<?>, EntityType<?>> entityTypes = new ConcurrentHashMap<>();

    @Any
    @Inject
    private Instance<EntityManagerFactory> factoryInstance;

    // -----------------------------------------------------------------------------------------------------------------
    @Inject
    private __PersistenceCryptoManager cryptoManager;
}
