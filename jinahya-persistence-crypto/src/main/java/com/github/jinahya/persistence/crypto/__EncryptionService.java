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

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.util.Calendar;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.Bytes_4_;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.Characters_4_;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.char_2;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.chars_4_;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.double_8;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.float_4;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.instant_16;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.int_4;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.local_date_8;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.local_date_time_16;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.local_time_8;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.long_8;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.offset_date_time_20;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.offset_time_12;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.short_2;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.sql_date_8;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.sql_time_8;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.sql_timestamp_8;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.util_calendar_8;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.util_date_8;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.uuid_16;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.year_4;

/**
 * .
 *
 * @see <a href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#a486">2.6. Basic
 *         Types</a> (Jakarta Persistence 3.2 Specification Document)
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __EncryptionService {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

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
    protected __EncryptionService() {
        super();
    }

    // -----------------------------------------------------------------------------------------------------------------

    // -----------------------------------------------------------------------------------------------------------------
    @PostConstruct
    protected void onPostConstruct() {
        logger.log(System.Logger.Level.DEBUG, "onPostConstruct()");
        logger.log(System.Logger.Level.DEBUG, "factoryInstance: {0}", factoryInstance);
        logger.log(System.Logger.Level.DEBUG, "cryptoManager: {0}", encryptionManager);
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
    private void check(final Attribute<?, ?> decryptedAttribute,
                       final __Encrypted encryptionAnnotation, final Attribute<?, ?> encryptedAttribute) {
        // decrypted attribute should be optional
        {
            final var basic = JinahyaAttributeUtils.getJavaMemberAnnotation(decryptedAttribute, Basic.class);
            final var optional = Optional.ofNullable(basic).map(Basic::optional).orElse(true);
            if (!optional) {
                throw new RuntimeException(
                        "decrypted attribute should be optional" +
                        "; decrypted attribute: " + decryptedAttribute +
                        "; encryption annotation: " + encryptionAnnotation
                );
            }
        }
        // the encrypted attribute cannot be the same attribute as the decrypted attribute
        if (encryptedAttribute == decryptedAttribute) {
            throw new RuntimeException(
                    "encrypted attribute cannot be same as decrypted attribute" +
                    "; decrypted attribute: " + decryptedAttribute +
                    "; encryption annotation: " + encryptionAnnotation +
                    "; encrypted attribute: " + encryptedAttribute
            );
        }
        // encrypted attribute should be optional
        {
            final var basic = JinahyaAttributeUtils.getJavaMemberAnnotation(encryptedAttribute, Basic.class);
            final var optional = Optional.ofNullable(basic).map(Basic::optional).orElse(true);
            if (!optional) {
                throw new RuntimeException(
                        "decrypted attribute should be optional" +
                        "; decrypted attribute: " + decryptedAttribute +
                        "; encryption annotation: " + encryptionAnnotation +
                        "; encrypted attribute: " + encryptedAttribute
                );
            }
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    protected void encrypt(final @Nonnull @NotNull Object entity) {
        Objects.requireNonNull(entity, "entity is null");
        final var cryptoIdentifier = encryptionManager.getCryptoIdentifier(entity);
        final var entityClass = entity.getClass();
        final var entityType = getEntityType(entityClass);
        final var attributes = getAttributes(entityType);
        for (final var entry : attributes.entrySet()) {
            final var name = entry.getKey();
            final var decryptedAttribute = entry.getValue();
            final var persistenceAttributeType = decryptedAttribute.getPersistentAttributeType();
            if (persistenceAttributeType != Attribute.PersistentAttributeType.BASIC) {
                continue;
            }
            final var javaType = decryptedAttribute.getJavaType();
            final var encryptionAnnotation =
                    JinahyaAttributeUtils.getJavaMemberAnnotation(decryptedAttribute, __Encrypted.class);
            if (encryptionAnnotation == null) {
                continue;
            }
            final var encryptedAttribute = Optional.of(encryptionAnnotation.value())
                    .map(attributes::get)
                    .orElseThrow(() -> new RuntimeException(
                            "no encrypted attribute found" +
                            "; attribute: " + attributes +
                            "; encryption annotation: " + encryptionAnnotation
                    ));
            check(decryptedAttribute, encryptionAnnotation, encryptedAttribute);
            // encrypted attribute's value should be null; which means not yet encrypted
            {
                final var value = JinahyaAttributeUtils.getAttributeValue(entity, encryptedAttribute);
                if (value != null) {
                    logger.log(System.Logger.Level.DEBUG,
                               "seems already encrypted" +
                               "; decrypted attribute: " + decryptedAttribute +
                               "; encryption annotation: " + encryptionAnnotation +
                               "; decrypted attribute: " + decryptedAttribute);
                    continue;
                }
            }
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
            final var encrypted = encryptionManager.encrypt(cryptoIdentifier, decryptedBytes);
            JinahyaAttributeUtils.setAttributeValue(entity, encryptedAttribute, encrypted);
            JinahyaAttributeUtils.setAttributeValue(entity, decryptedAttribute, null);
        }
    }

    protected void decrypt(final @Nonnull @NotNull Object entity) {
        Objects.requireNonNull(entity, "entity is null");
        final var cryptoIdentifier = encryptionManager.getCryptoIdentifier(entity);
        final var entityClass = entity.getClass();
        final var entityType = getEntityType(entityClass);
        final var attributes = getAttributes(entityType);
        for (final var entry : attributes.entrySet()) {
            final var name = entry.getKey();
            final var decryptedAttribute = entry.getValue();
            final var persistenceAttributeType = decryptedAttribute.getPersistentAttributeType();
            if (persistenceAttributeType != Attribute.PersistentAttributeType.BASIC) {
                continue;
            }
            final var javaType = decryptedAttribute.getJavaType();
            final var annotation =
                    JinahyaAttributeUtils.getJavaMemberAnnotation(decryptedAttribute, __Encrypted.class);
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
            check(decryptedAttribute, annotation, encryptedAttribute);
            final var encryptedBytes = (byte[]) JinahyaAttributeUtils.getAttributeValue(entity, encryptedAttribute);
            if (encryptedBytes == null) {
                final var decryptedValue = JinahyaAttributeUtils.getAttributeValue(entity, decryptedAttribute);
                if (decryptedValue != null) {
                    continue;
                }
                JinahyaAttributeUtils.setAttributeValue(entity, decryptedAttribute, null);
                continue;
            }
            final var decryptedBytes = encryptionManager.decrypt(cryptoIdentifier, encryptedBytes);
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
            JinahyaAttributeUtils.setAttributeValue(entity, encryptedAttribute, null);
        }
    }

    // ----------------------------------------------------------------------------------------------------- entityTypes

    // ------------------------------------------------------------------------------------------------- factoryInstance
    protected @Nonnull EntityType<?> getEntityType(@Nonnull final Class<?> entityClass) {
        return entityTypes.computeIfAbsent(
                Objects.requireNonNull(entityClass, "entityClass is null"),
                k -> JinahyaEntityTypeUtils.getEntityType(k, factoryInstance)
        );
    }

    // ----------------------------------------------------------------------------------------------- encryptionManager

    // -----------------------------------------------------------------------------------------------------------------
    private final Map<Class<?>, EntityType<?>> entityTypes = new ConcurrentHashMap<>();

    @Any
    @Inject
    private Instance<EntityManagerFactory> factoryInstance;

    // -----------------------------------------------------------------------------------------------------------------
    @Inject
    private __EncryptionManager encryptionManager;
}
