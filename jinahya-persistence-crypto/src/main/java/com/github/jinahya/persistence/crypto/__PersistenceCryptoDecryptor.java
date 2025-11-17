package com.github.jinahya.persistence.crypto;

import com.github.jinahya.persistence.metamodel.JinahyaAttributeUtils;
import jakarta.annotation.Nonnull;
import jakarta.persistence.Basic;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.EntityType;

import java.io.Serializable;
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
import java.util.Optional;
import java.util.UUID;

final class __PersistenceCryptoDecryptor<X> extends __PersistenceCryptoProcessor<X> {

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    __PersistenceCryptoDecryptor(final @Nonnull EntityType<X> entityType,
                                 final @Nonnull __PersistenceCryptoManager manager) {
        super(entityType, manager);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    void process(final @Nonnull X entity, final @Nonnull String identifier) {
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
                return;
            }
            final var decryptedBytes = manager.decrypt(identifier, encryptedBytes);
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
                decryptedValue = uuid_(ByteBuffer.wrap(decryptedBytes));
            } else if (javaType == BigInteger.class) {
                decryptedValue = new BigInteger(decryptedBytes);
            } else if (javaType == BigDecimal.class) {
                decryptedValue = new BigDecimal(new String(decryptedBytes, StandardCharsets.UTF_8));
            } else if (javaType == LocalDate.class) {
                decryptedValue = local_date_8(ByteBuffer.wrap(decryptedBytes));
            } else if (javaType == LocalTime.class) {
                decryptedValue = local_Time_8(ByteBuffer.wrap((decryptedBytes)));
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

    // ------------------------------------------------------------------------------------------------ super.entityType

    // ------------------------------------------------------------------------------------------------ super.attributes

    // --------------------------------------------------------------------------------------------------- super.manager

    // -----------------------------------------------------------------------------------------------------------------
}
