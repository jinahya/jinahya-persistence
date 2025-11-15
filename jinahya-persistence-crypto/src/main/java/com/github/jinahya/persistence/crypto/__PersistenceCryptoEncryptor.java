package com.github.jinahya.persistence.crypto;

import com.github.jinahya.persistence.metamodel.JinahyaAttributeUtils;
import jakarta.annotation.Nonnull;
import jakarta.persistence.Basic;
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
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

final class __PersistenceCryptoEncryptor<X> {

    // -----------------------------------------------------------------------------------------------------------------
    private static ByteBuffer putBoolean_1(@Nonnull final ByteBuffer b, final boolean v) {
        return b.put(v ? (byte) 1 : 0);
    }

    private static boolean getBoolean_1(@Nonnull final ByteBuffer b) {
        return b.get() == 0;
    }

    private static ByteBuffer putShort_2(@Nonnull final ByteBuffer b, final short v) {
        return b.putShort(v);
    }

    private static short getShort_2(@Nonnull final ByteBuffer b) {
        return b.getShort();
    }

    private static ByteBuffer putInt_4(@Nonnull final ByteBuffer b, final int v) {
        return b.putInt(v);
    }

    private static int int4(@Nonnull final ByteBuffer b) {
        return b.getInt();
    }

    private static ByteBuffer putLong_8(@Nonnull final ByteBuffer b, final long v) {
        return b.putLong(v);
    }

    private static long getLong_8(@Nonnull final ByteBuffer b) {
        return b.getLong();
    }

    private static ByteBuffer char_2(@Nonnull final ByteBuffer b, final char v) {
        return b.putChar(v);
    }

    private static char char_2(@Nonnull final ByteBuffer b) {
        return b.getChar();
    }

    private static ByteBuffer putLocalDate_8(@Nonnull final ByteBuffer b, final LocalDate v) {
        return putLong_8(b, v.toEpochDay());
    }

    private static LocalDate getLocalDate_8(@Nonnull final ByteBuffer b) {
        return LocalDate.ofEpochDay(getLong_8(b));
    }

    private static ByteBuffer putLocalTime_8(@Nonnull final ByteBuffer b, final LocalTime v) {
        return putLong_8(b, v.toNanoOfDay());
    }

    private static LocalTime getLocalTime_8(@Nonnull final ByteBuffer b) {
        return LocalTime.ofNanoOfDay(getLong_8(b));
    }

    private static ByteBuffer putLocalDateTime_16(@Nonnull final ByteBuffer b, final LocalDateTime v) {
        return putLocalTime_8(
                putLocalDate_8(b, v.toLocalDate()),
                v.toLocalTime()
        );
    }

    private static LocalDateTime getLocalDateTime_16(@Nonnull final ByteBuffer b) {
        return LocalDateTime.of(
                getLocalDate_8(b),
                getLocalTime_8(b)
        );
    }

    private static ByteBuffer putOffset_4(@Nonnull final ByteBuffer b, final ZoneOffset v) {
        return putInt_4(b, v.getTotalSeconds());
    }

    private static ZoneOffset getOffset_4(@Nonnull final ByteBuffer b) {
        return ZoneOffset.ofTotalSeconds(int4(b));
    }

    private static ByteBuffer putOffsetTime_12(@Nonnull final ByteBuffer b, final OffsetTime v) {
        return putOffset_4(
                putLocalTime_8(b, v.toLocalTime()),
                v.getOffset()
        );
    }

    private static OffsetTime getOffsetTime_12(@Nonnull final ByteBuffer b) {
        return OffsetTime.of(
                getLocalTime_8(b),
                getOffset_4(b)
        );
    }

    private static ByteBuffer putOffsetDateTime_20(@Nonnull final ByteBuffer b, final OffsetDateTime v) {
        return putOffset_4(
                putLocalDateTime_16(b, v.toLocalDateTime()),
                v.getOffset()
        );
    }

    private static OffsetDateTime getOffsetDateTime_20(@Nonnull final ByteBuffer b) {
        return OffsetDateTime.of(
                getLocalDateTime_16(b),
                getOffset_4(b)
        );
    }

    private static ByteBuffer instant_16(@Nonnull final ByteBuffer b, final Instant v) {
        return putLong_8(
                putLong_8(b, v.getEpochSecond()),
                v.getNano()
        );
    }

    private static Instant instant_16(@Nonnull final ByteBuffer b) {
        return Instant.ofEpochSecond(
                getLong_8(b),
                getLong_8(b)
        );
    }

    private static ByteBuffer year_4(@Nonnull final ByteBuffer b, final Year v) {
        return putInt_4(b, v.getValue());
    }

    private static Year year_4(@Nonnull final ByteBuffer b) {
        return Year.of(int4(b));
    }

    private static ByteBuffer utilDate_16(@Nonnull final ByteBuffer b, final Date v) {
        return instant_16(b, v.toInstant());
    }

    private static Date utilDate_16(@Nonnull final ByteBuffer b) {
        return Date.from(instant_16(b));
    }

    private static ByteBuffer utilCalendar_16(@Nonnull final ByteBuffer b, final Calendar v) {
        return utilDate_16(b, v.getTime());
    }

    private static Calendar utilCalendar_16(@Nonnull final ByteBuffer b) {
        final var v = Calendar.getInstance();
        v.setTime(utilDate_16(b));
        return v;
    }

    private static ByteBuffer bytes_4L(@Nonnull final ByteBuffer b, final byte[] v) {
        return putInt_4(b, v.length).put(v);
    }

    private static byte[] bytes_4L(@Nonnull final ByteBuffer b) {
        final var v = new byte[int4(b)];
        b.get(v);
        return v;
    }

    private static ByteBuffer serializable_4L(@Nonnull final ByteBuffer b, final Serializable v) {
        try (var baos = new ByteArrayOutputStream();
             var oos = new ObjectOutputStream(baos)) {
            oos.writeObject(v);
            oos.flush();
            return bytes_4L(b, baos.toByteArray());
        } catch (final IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    private static <T extends Serializable> T serializable_4L(@Nonnull final ByteBuffer b) {
        try (var bais = new ByteArrayInputStream(bytes_4L(b));
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

    private static <T extends Serializable> ByteBuffer array_4L(@Nonnull final ByteBuffer b, final T[] v) {
        try (var baos = new ByteArrayOutputStream();
             var output = new ObjectOutputStream(baos)) {
            output.writeObject(v);
            output.flush();
            return bytes_4L(b, baos.toByteArray());
        } catch (final IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    private static <T extends Serializable> T[] array_4L(@Nonnull final ByteBuffer b) {
        try (var bais = new ByteArrayInputStream(bytes_4L(b));
             var ois = new ObjectInputStream(bais)) {
            try {
                return (T[]) ois.readObject();
            } catch (final ClassNotFoundException cnfe) {
                throw new RuntimeException(cnfe);
            }
        } catch (final IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    __PersistenceCryptoEncryptor(final @Nonnull Class<X> entityClass, final @Nonnull EntityType<X> entityType,
                                 final @Nonnull __PersistenceCryptoManager manager) {
        super();
        this.entityClass = Objects.requireNonNull(entityClass, "entityClass is null");
        this.entityType = Objects.requireNonNull(entityType, "entityType is null");
        {
            this.attributes =
                    this.entityType.getAttributes().stream()
                            .collect(Collectors.toUnmodifiableMap(Attribute::getName, Function.identity()));
        }
        this.manager = Objects.requireNonNull(manager, "manager is null");
        // -------------------------------------------------------------------------------------------------------------
        final var encryptingInstructions = new ArrayList<Function<? super String, Consumer<? super X>>>();
        final var decryptingInstructions = new ArrayList<Function<? super String, Consumer<? super X>>>();
        final ByteBuffer byteBuffer = ByteBuffer.allocate(Long.BYTES << 1);
        for (final var entry : this.attributes.entrySet()) {
            final var name = entry.getKey();
            final var attribute = entry.getValue();
            final var persistenceAttributeType = attribute.getPersistentAttributeType();
            if (persistenceAttributeType != Attribute.PersistentAttributeType.BASIC) {
                continue;
            }
            final var optional =
                    Optional.ofNullable(JinahyaAttributeUtils.getJavaMemberAnnotation(attribute, Basic.class))
                            .map(Basic::optional)
                            .orElse(true);
            final var javaType = attribute.getJavaType();
            final var annotation = JinahyaAttributeUtils.getJavaMemberAnnotation(attribute, __EncryptedAttribute.class);
            if (annotation == null) {
                continue;
            }
            final var targetAttribute = Optional.of(annotation.value())
                    .map(String::strip)
                    .map(v -> v.isEmpty() ? attribute : attributes.get(v))
                    .orElse(null);
            if (targetAttribute == null) {
                throw new RuntimeException(
                        "no target attribute; attribute: " + attributes + "; annotation: " + annotation);
            }
            final var targetOptional =
                    Optional.ofNullable(JinahyaAttributeUtils.getJavaMemberAnnotation(attribute, Basic.class))
                            .map(Basic::optional)
                            .orElse(true);
            encryptingInstructions.add(identifier -> entity -> {
                final var value = JinahyaAttributeUtils.getAttributeValue(entity, attribute);
                if (value == null) {
                    JinahyaAttributeUtils.setAttributeValue(entity, targetAttribute, null);
                    return;
                }
                if (javaType == String.class) {
                    final var string = (String) value;
                    JinahyaAttributeUtils.setAttributeValue(
                            entity,
                            targetAttribute,
                            Base64.getEncoder().encodeToString(
                                    manager.encrypt(identifier, string.getBytes(StandardCharsets.UTF_8))
                            )
                    );
                    if (optional) {
                        JinahyaAttributeUtils.setAttributeValue(entity, attribute, null);
                    }
                    return;
                }
                if (javaType == UUID.class) {
                    final var uuid = (UUID) value;
                    final var buffer = ByteBuffer.allocate(Long.BYTES << 1)
                            .putLong(uuid.getMostSignificantBits())
                            .putLong(uuid.getLeastSignificantBits());
                    final var decrypted = buffer.array();
                    final var encrypted = manager.encrypt(identifier, decrypted);
                    JinahyaAttributeUtils.setAttributeValue(
                            entity,
                            attribute,
                            encrypted
                    );
                    if (optional) {
                        JinahyaAttributeUtils.setAttributeValue(entity, attribute, null);
                    }
                    return;
                }
                if (javaType == BigInteger.class) {
                    final var decrypted = ((BigInteger) value).toByteArray();
                    final var encrypted = manager.encrypt(identifier, decrypted);
                    JinahyaAttributeUtils.setAttributeValue(entity, attribute, encrypted);
                    if (optional) {
                        JinahyaAttributeUtils.setAttributeValue(entity, attribute, null);
                    }
                    return;
                }
                if (javaType == BigDecimal.class) {
                    final var decrypted = ((BigDecimal) value).toBigIntegerExact().toByteArray();
                    final var encrypted = manager.encrypt(identifier, decrypted);
                    JinahyaAttributeUtils.setAttributeValue(entity, attribute, encrypted);
                    if (optional) {
                        JinahyaAttributeUtils.setAttributeValue(entity, attribute, null);
                    }
                    return;
                }
                if (javaType == LocalDate.class) {
                    final var localDate = (LocalDate) value;
                    final var buffer = ByteBuffer.allocate(Long.BYTES).putLong(localDate.toEpochDay());
                    final var decrypted = buffer.array();
                    final var encrypted = manager.encrypt(identifier, decrypted);
                    JinahyaAttributeUtils.setAttributeValue(entity, attribute, encrypted);
                    if (optional) {
                        JinahyaAttributeUtils.setAttributeValue(entity, attribute, null);
                    }
                    return;
                }
                if (javaType == LocalTime.class) {
                    final var localTime = (LocalTime) value;
                    final var buffer = ByteBuffer.allocate(Long.BYTES)
                            .putLong(Duration.between(LocalTime.MIDNIGHT, localTime).toNanos());
                    final var decrypted = buffer.array();
                    final var encrypted = manager.encrypt(identifier, decrypted);
                    JinahyaAttributeUtils.setAttributeValue(entity, attribute, encrypted);
                    if (optional) {
                        JinahyaAttributeUtils.setAttributeValue(entity, attribute, null);
                    }
                    return;
                }
                if (javaType == LocalDateTime.class) {
                    final var localDateTime = (LocalDateTime) value;
                    final var buffer = ByteBuffer.allocate(Long.BYTES << 1)
                            .putLong(localDateTime.toLocalDate().toEpochDay())
                            .putLong(Duration.between(LocalTime.MIDNIGHT, localDateTime.toLocalTime()).toNanos());
                    final var decrypted = buffer.array();
                    final var encrypted = manager.encrypt(identifier, decrypted);
                    JinahyaAttributeUtils.setAttributeValue(entity, attribute, encrypted);
                    if (optional) {
                        JinahyaAttributeUtils.setAttributeValue(entity, attribute, null);
                    }
                    return;
                }
                if (javaType == OffsetTime.class) {
                    final var offsetTime = (OffsetTime) value;
                    final var buffer = ByteBuffer.allocate(Long.BYTES)
                            .putLong(Duration.between(LocalTime.MIDNIGHT, offsetTime.toLocalTime()).getNano());
                    final var decrypted = buffer.array();
                    final var encrypted = manager.encrypt(identifier, decrypted);
                    JinahyaAttributeUtils.setAttributeValue(entity, attribute, encrypted);
                    if (optional) {
                        JinahyaAttributeUtils.setAttributeValue(entity, attribute, null);
                    }
                    return;
                }
                if (javaType == OffsetDateTime.class) {
                    final var offsetDateTime = (OffsetDateTime) value;
                    final var buffer = ByteBuffer.allocate(Long.BYTES)
                            .putLong(Duration.between(LocalTime.MIDNIGHT, offsetDateTime.toLocalTime()).getNano());
                    final var decrypted = buffer.array();
                    final var encrypted = manager.encrypt(identifier, decrypted);
                    JinahyaAttributeUtils.setAttributeValue(entity, attribute, encrypted);
                    if (optional) {
                        JinahyaAttributeUtils.setAttributeValue(entity, attribute, null);
                    }
                    return;
                }
                if (javaType == byte[].class) {
                    JinahyaAttributeUtils.setAttributeValue(
                            entity,
                            attribute,
                            manager.encrypt(identifier, (byte[]) value)
                    );
                    if (optional) {
                        JinahyaAttributeUtils.setAttributeValue(entity, attribute, null);
                    }
                    return;
                }
                throw new RuntimeException("unsupported type: " + javaType);
            });
        }
        this.encryptingInstructions = Collections.unmodifiableList(encryptingInstructions);
        this.decryptingInstructions = Collections.unmodifiableList(decryptingInstructions);
    }

    // -------------------------------------------------------------------------------------------------------------
    private final Class<X> entityClass;

    // -------------------------------------------------------------------------------------------------------------
    private final EntityType<X> entityType;

    private final Map<String, Attribute<? super X, ?>> attributes;

    // -------------------------------------------------------------------------------------------------------------
    private final __PersistenceCryptoManager manager;

    // -------------------------------------------------------------------------------------------------------------
    private final List<Function<? super String, Consumer<? super X>>> encryptingInstructions;

    private final List<Function<? super String, Consumer<? super X>>> decryptingInstructions;
}
