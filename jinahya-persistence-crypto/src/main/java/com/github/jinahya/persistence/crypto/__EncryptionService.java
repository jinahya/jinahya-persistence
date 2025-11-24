package com.github.jinahya.persistence.crypto;

import com.github.jinahya.persistence.metamodel.JinahyaAttributeUtils;
import com.github.jinahya.persistence.metamodel.JinahyaManagedTypeUtils;
import jakarta.annotation.Nonnull;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.Shutdown;
import jakarta.enterprise.event.Startup;
import jakarta.persistence.Basic;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.ManagedType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.Bytes_l;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.Characters_2l;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.big_decimal_;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.char_2;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.chars_2l;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.double_8;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.enum_;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.float_4;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.instant_16;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.int_4;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.local_date_8;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.local_date_time_16;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.local_time_8;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.long_8;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.offset_date_time_20;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.offset_time_12;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.serializable_;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.short_2;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.string_;
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
//    private static final Map<EntityType<?>, Map<String, Attribute<?, ?>>> ENTITY_TYPES_AND_ATTRIBUTES =
//            new ConcurrentHashMap<>();
//
//    /**
//     * Returns an unmodifiable map of attribute names and attributes of the specified entity type.
//     *
//     * @param entityType the entity type whose attributes are returned; must not be {@code null}
//     * @return a map of attribute names and attributes of the {@code entityType}
//     * @apiNote this method is thread-safe.
//     */
//    protected static Map<String, Attribute<?, ?>> getAttributes(@Nonnull final EntityType<?> entityType) {
//        Objects.requireNonNull(entityType, "entityType is null");
//        return ENTITY_TYPES_AND_ATTRIBUTES.computeIfAbsent(
//                entityType,
//                k -> k.getAttributes().stream()
//                        .collect(Collectors.toUnmodifiableMap(Attribute::getName, Function.identity()))
//        );
//    }

    private static final Map<ManagedType<?>, Map<String, Attribute<?, ?>>> MANAGED_TYPES_AND_ATTRIBUTES =
            new ConcurrentHashMap<>();

    protected static Map<String, Attribute<?, ?>> getAttributes(@Nonnull final ManagedType<?> managedType) {
        Objects.requireNonNull(managedType, "managedType is null");
        return MANAGED_TYPES_AND_ATTRIBUTES.computeIfAbsent(
                managedType,
                k -> k.getAttributes().stream()
                        .collect(Collectors.toUnmodifiableMap(Attribute::getName, Function.identity()))
        );
    }

    // -------------------------------------------------------------------------------------------------------- BUILDERS

    // ------------------------------------------------------------------------------------------ STATIC_FACTORY_METHODS

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     *
     * @param entityManagerFactory an entity manager factory; must not be {@code null}.
     * @param encryptionManager    the encryption manager; must not be {@code null}.
     */
    protected __EncryptionService(final @Nonnull EntityManagerFactory entityManagerFactory,
                                  final @Nonnull __EncryptionManager encryptionManager) {
        super();
        this.entityManagerFactory = Objects.requireNonNull(entityManagerFactory, "entityManagerFactory is null");
        this.encryptionManager = Objects.requireNonNull(encryptionManager, "encryptionManager is null");
    }

    // -----------------------------------------------------------------------------------------------------------------

    // -----------------------------------------------------------------------------------------------------------------
    @PostConstruct
    protected void onPostConstruct() {
        logger.log(System.Logger.Level.DEBUG, "onPostConstruct()");
        logger.log(System.Logger.Level.DEBUG, "entityManagerFactory: {0}", entityManagerFactory);
        logger.log(System.Logger.Level.DEBUG, "encryptionManager: {0}", encryptionManager);
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
                       final __EncryptedAttribute encryptionAnnotation, final Attribute<?, ?> encryptedAttribute,
                       final Set<Attribute<?, ?>> encryptedAttributes) {
        // the decrypted attribute should be optional
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
        // the encrypted attribute should be optional
        {
            final var mapping = JinahyaAttributeUtils.getJavaMemberAnnotation(encryptedAttribute, Basic.class);
            final var optional = Optional.ofNullable(mapping).map(Basic::optional).orElse(true);
            if (!optional) {
                throw new RuntimeException(
                        "encrypted attribute should be optional" +
                        "; decrypted attribute: " + decryptedAttribute +
                        "; encryption annotation: " + encryptionAnnotation +
                        "; encrypted attribute: " + encryptedAttribute
                );
            }
        }
        // the encrypted attribute's javaType should be byte[]
        if (encryptedAttribute.getJavaType() != byte[].class) {
            throw new RuntimeException(
                    "encrypted attribute's java type should be byte[]" +
                    "; decrypted attribute: " + decryptedAttribute +
                    "; encryption annotation: " + encryptionAnnotation +
                    "; encrypted attribute: " + encryptedAttribute
            );
        }
        // the encrypted attribute should not be duplicate
        if (!encryptedAttributes.add(encryptedAttribute)) {
            throw new RuntimeException(
                    "duplicat´encrypted attribute" +
                    "; decrypted attribute: " + decryptedAttribute +
                    "; encryption annotation: " + encryptionAnnotation +
                    "; encrypted attribute: " + encryptedAttribute
            );
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    protected void encrypt(final @NotBlank String encryptionIdentifier, final @Valid @NotNull Object object) {
        Objects.requireNonNull(object, "object is null");
        final var entityClass = object.getClass();
//        final var entityType = getEntityType(entityClass);
        final var managedType = getManagedType(entityClass);
        final var attributes = getAttributes(managedType);
        final var encryptedAttributes = new HashSet<Attribute<?, ?>>();
        for (final var entry : attributes.entrySet()) {
            final var name = entry.getKey();
            final var decryptedAttribute = entry.getValue();
            final var persistenceAttributeType = decryptedAttribute.getPersistentAttributeType();
            if (persistenceAttributeType == Attribute.PersistentAttributeType.EMBEDDED) {
                final var decryptedValue = JinahyaAttributeUtils.getAttributeValue(object, decryptedAttribute);
                encrypt(encryptionIdentifier, decryptedValue);
                continue;
            }
            if (persistenceAttributeType != Attribute.PersistentAttributeType.BASIC) {
                continue;
            }
            final var attributeAnnotation =
                    JinahyaAttributeUtils.getJavaMemberAnnotation(decryptedAttribute, __EncryptedAttribute.class);
            if (attributeAnnotation == null) {
                continue;
            }
            final var encryptedAttribute = Optional.of(attributeAnnotation.encryptedAttribute())
                    .map(v -> v.isBlank() ? decryptedAttribute.getName() + "Enc__" : v)
                    .map(attributes::get)
                    .orElseThrow(() -> new RuntimeException(
                            "no encrypted attribute found" +
                            "; decrypted attribute: " + decryptedAttribute +
                            "; encryption annotation: " + attributeAnnotation
                    ));
            check(decryptedAttribute, attributeAnnotation, encryptedAttribute, encryptedAttributes);
            final var decryptedValue = JinahyaAttributeUtils.getAttributeValue(object, decryptedAttribute);
            if (decryptedValue == null) {
                final var encryptedValue = JinahyaAttributeUtils.getAttributeValue(object, encryptedAttribute);
                if (encryptedValue != null) {
                    // already encrypted
                    continue;
                }
                JinahyaAttributeUtils.setAttributeValue(object, encryptedAttribute, null);
                continue;
            }
            final byte[] decryptedBytes;
            final var javaType = decryptedAttribute.getJavaType();
            if (decryptedValue instanceof Boolean b) {
                decryptedBytes = new byte[]{(byte) (b ? 1 : 0)};
            } else if (decryptedValue instanceof Byte b) {
                decryptedBytes = new byte[]{b};
            } else if (decryptedValue instanceof Short s) {
                decryptedBytes = short_2(new byte[Short.BYTES], 0, s);
            } else if (decryptedValue instanceof Integer i) {
                decryptedBytes = int_4(new byte[Integer.BYTES], 0, i);
            } else if (decryptedValue instanceof Long l) {
                decryptedBytes = long_8(new byte[Long.BYTES], 0, l);
            } else if (decryptedValue instanceof Character c) {
                decryptedBytes = char_2(new byte[Character.BYTES], 0, c);
            } else if (decryptedValue instanceof Float f) {
                decryptedBytes = float_4(new byte[Float.BYTES], 0, f);
            } else if (decryptedValue instanceof Double d) {
                decryptedBytes = double_8(new byte[Double.BYTES], 0, d);
            } else if (decryptedValue instanceof String s) {
                decryptedBytes = string_(s);
            } else if (decryptedValue instanceof UUID u) {
                decryptedBytes = uuid_16(u);
            } else if (decryptedValue instanceof BigInteger v) {
                decryptedBytes = v.toByteArray();
            } else if (decryptedValue instanceof BigDecimal v) {
                decryptedBytes = big_decimal_(v);
            } else if (decryptedValue instanceof LocalDate v) {
                decryptedBytes = local_date_8(new byte[Long.BYTES], 0, v);
            } else if (decryptedValue instanceof LocalTime v) {
                decryptedBytes = local_time_8(new byte[Long.BYTES], 0, v);
            } else if (decryptedValue instanceof LocalDateTime v) {
                decryptedBytes = local_date_time_16(new byte[Long.BYTES << 1], 0, v);
            } else if (decryptedValue instanceof OffsetTime v) {
                decryptedBytes = offset_time_12(new byte[12], 0, v);
            } else if (decryptedValue instanceof OffsetDateTime v) {
                decryptedBytes = offset_date_time_20(new byte[20], 0, v);
            } else if (decryptedValue instanceof Instant v) {
                decryptedBytes = instant_16(new byte[16], 0, v);
            } else if (decryptedValue instanceof Year v) {
                decryptedBytes = year_4(new byte[4], 0, v);
            } else if (decryptedValue instanceof java.util.Calendar v) {
                decryptedBytes = util_date_8(new byte[8], 0, v.getTime());
            } else if (decryptedValue instanceof java.util.Date v) { // should be checked after java.sql.*
                decryptedBytes = long_8(new byte[8], 0, v.getTime());
            } else if (decryptedValue instanceof byte[] v) {
                decryptedBytes = v;
            } else if (decryptedValue instanceof Byte[] v) {
                logger.log(System.Logger.Level.WARNING, "Byte[] is not encouraged; use byte[]");
                decryptedBytes = Bytes_l(v);
            } else if (decryptedValue instanceof char[] v) {
                decryptedBytes = chars_2l(v);
            } else if (decryptedValue instanceof Character[] v) {
                decryptedBytes = Characters_2l(v);
            } else if (decryptedValue instanceof Enum<?> v) {
                decryptedBytes = enum_(v);
            } else if (decryptedValue instanceof Serializable v) {
                decryptedBytes = serializable_(v);
            } else {
                throw new RuntimeException("unsupported java type: " + javaType);
            }
            final var encrypted = encryptionManager.encrypt(encryptionIdentifier, decryptedBytes);
            JinahyaAttributeUtils.setAttributeValue(object, encryptedAttribute, encrypted);
            JinahyaAttributeUtils.setAttributeValue(object, decryptedAttribute, null);
        }
    }

    public void encrypt(final @Valid @NotNull Object object) {
        Objects.requireNonNull(object, "object is null");
        final var encryptionIdentifier = encryptionManager.getEncryptionIdentifier(object);
        encrypt(encryptionIdentifier, object);
//        final var entityClass = object.getClass();
//        final var entityType = getEntityType(entityClass);
//        final var attributes = getAttributes(entityType);
//        final var encryptedAttributes = new HashSet<Attribute<?, ?>>();
//        for (final var entry : attributes.entrySet()) {
//            final var name = entry.getKey();
//            final var decryptedAttribute = entry.getValue();
//            final var persistenceAttributeType = decryptedAttribute.getPersistentAttributeType();
//            if (persistenceAttributeType != Attribute.PersistentAttributeType.EMBEDDED) {
//                encrypt(decryptedAttribute);
//                continue;
//            }
//            if (persistenceAttributeType != Attribute.PersistentAttributeType.BASIC) {
//                continue;
//            }
//            final var attributeAnnotation =
//                    JinahyaAttributeUtils.getJavaMemberAnnotation(decryptedAttribute, __EncryptedAttribute.class);
//            if (attributeAnnotation == null) {
//                continue;
//            }
//            final var encryptedAttribute = Optional.of(attributeAnnotation.encryptedAttribute())
//                    .map(v -> v.isBlank() ? decryptedAttribute.getName() + "Enc__" : v)
//                    .map(attributes::get)
//                    .orElseThrow(() -> new RuntimeException(
//                            "no encrypted attribute found" +
//                            "; decrypted attribute: " + decryptedAttribute +
//                            "; encryption annotation: " + attributeAnnotation
//                    ));
//            check(decryptedAttribute, attributeAnnotation, encryptedAttribute, encryptedAttributes);
//            final var decryptedValue = JinahyaAttributeUtils.getAttributeValue(object, decryptedAttribute);
//            if (decryptedValue == null) {
//                final var encryptedValue = JinahyaAttributeUtils.getAttributeValue(object, encryptedAttribute);
//                if (encryptedValue != null) {
//                    // already encrypted
//                    continue;
//                }
//                JinahyaAttributeUtils.setAttributeValue(object, encryptedAttribute, null);
//                continue;
//            }
//            final byte[] decryptedBytes;
//            final var javaType = decryptedAttribute.getJavaType();
//            if (decryptedValue instanceof Boolean b) {
//                decryptedBytes = new byte[]{(byte) (b ? 1 : 0)};
//            } else if (decryptedValue instanceof Byte b) {
//                decryptedBytes = new byte[]{b};
//            } else if (decryptedValue instanceof Short s) {
//                decryptedBytes = short_2(new byte[Short.BYTES], 0, s);
//            } else if (decryptedValue instanceof Integer i) {
//                decryptedBytes = int_4(new byte[Integer.BYTES], 0, i);
//            } else if (decryptedValue instanceof Long l) {
//                decryptedBytes = long_8(new byte[Long.BYTES], 0, l);
//            } else if (decryptedValue instanceof Character c) {
//                decryptedBytes = char_2(new byte[Character.BYTES], 0, c);
//            } else if (decryptedValue instanceof Float f) {
//                decryptedBytes = float_4(new byte[Float.BYTES], 0, f);
//            } else if (decryptedValue instanceof Double d) {
//                decryptedBytes = double_8(new byte[Double.BYTES], 0, d);
//            } else if (decryptedValue instanceof String s) {
//                decryptedBytes = string_(s);
//            } else if (decryptedValue instanceof UUID u) {
//                decryptedBytes = uuid_16(u);
//            } else if (decryptedValue instanceof BigInteger v) {
//                decryptedBytes = v.toByteArray();
//            } else if (decryptedValue instanceof BigDecimal v) {
//                decryptedBytes = big_decimal_(v);
//            } else if (decryptedValue instanceof LocalDate v) {
//                decryptedBytes = local_date_8(new byte[Long.BYTES], 0, v);
//            } else if (decryptedValue instanceof LocalTime v) {
//                decryptedBytes = local_time_8(new byte[Long.BYTES], 0, v);
//            } else if (decryptedValue instanceof LocalDateTime v) {
//                decryptedBytes = local_date_time_16(new byte[Long.BYTES << 1], 0, v);
//            } else if (decryptedValue instanceof OffsetTime v) {
//                decryptedBytes = offset_time_12(new byte[12], 0, v);
//            } else if (decryptedValue instanceof OffsetDateTime v) {
//                decryptedBytes = offset_date_time_20(new byte[20], 0, v);
//            } else if (decryptedValue instanceof Instant v) {
//                decryptedBytes = instant_16(new byte[16], 0, v);
//            } else if (decryptedValue instanceof Year v) {
//                decryptedBytes = year_4(new byte[4], 0, v);
//            } else if (decryptedValue instanceof java.util.Calendar v) {
//                decryptedBytes = util_date_8(new byte[8], 0, v.getTime());
//            } else if (decryptedValue instanceof java.util.Date v) { // should be checked after java.sql.*
//                decryptedBytes = long_8(new byte[8], 0, v.getTime());
//            } else if (decryptedValue instanceof byte[] v) {
//                decryptedBytes = v;
//            } else if (decryptedValue instanceof Byte[] v) {
//                logger.log(System.Logger.Level.WARNING, "Byte[] is not encouraged; use byte[]");
//                decryptedBytes = Bytes_l(v);
//            } else if (decryptedValue instanceof char[] v) {
//                decryptedBytes = chars_2l(v);
//            } else if (decryptedValue instanceof Character[] v) {
//                decryptedBytes = Characters_2l(v);
//            } else if (decryptedValue instanceof Enum<?> v) {
//                decryptedBytes = enum_(v);
//            } else if (decryptedValue instanceof Serializable v) {
//                decryptedBytes = serializable_(v);
//            } else {
//                throw new RuntimeException("unsupported java type: " + javaType);
//            }
//            final var encrypted = encryptionManager.encrypt(encryptionIdentifier, decryptedBytes);
//            JinahyaAttributeUtils.setAttributeValue(object, encryptedAttribute, encrypted);
//            JinahyaAttributeUtils.setAttributeValue(object, decryptedAttribute, null);
//        }
    }

    protected void decrypt(final @NotBlank String encryptionIdentifier, final @Valid @NotNull Object object) {
        Objects.requireNonNull(object, "object is null");
        final var entityClass = object.getClass();
//        final var entityType = getEntityType(entityClass);
        final var managedType = getManagedType(entityClass);
        final var attributes = getAttributes(managedType);
        final var encryptedAttributes = new HashSet<Attribute<?, ?>>();
        for (final var entry : attributes.entrySet()) {
            final var name = entry.getKey();
            final var decryptedAttribute = entry.getValue();
            final var persistenceAttributeType = decryptedAttribute.getPersistentAttributeType();
            if (persistenceAttributeType == Attribute.PersistentAttributeType.EMBEDDED) {
                final var encryptedValue = JinahyaAttributeUtils.getAttributeValue(object, decryptedAttribute);
                decrypt(encryptionIdentifier, encryptedValue);
                continue;
            }
            if (persistenceAttributeType != Attribute.PersistentAttributeType.BASIC) {
                continue;
            }
            final var attributeAnnotation =
                    JinahyaAttributeUtils.getJavaMemberAnnotation(decryptedAttribute, __EncryptedAttribute.class);
            if (attributeAnnotation == null) {
                continue;
            }
            // TODO: @Embedded
            final var encryptedAttribute = Optional.of(attributeAnnotation.encryptedAttribute())
                    .map(v -> v.isBlank() ? decryptedAttribute.getName() + "Enc__" : v)
                    .map(attributes::get)
                    .orElseThrow(() -> new RuntimeException(
                            "no encrypted attribute found" +
                            "; decrypted attribute: " + decryptedAttribute +
                            "; attribute annotation: " + attributeAnnotation
                    ));
            check(decryptedAttribute, attributeAnnotation, encryptedAttribute, encryptedAttributes);
            final var encryptedBytes = (byte[]) JinahyaAttributeUtils.getAttributeValue(object, encryptedAttribute);
            if (encryptedBytes == null) {
                final var decryptedValue = JinahyaAttributeUtils.getAttributeValue(object, decryptedAttribute);
                if (decryptedValue != null) {
                    // the encrypted column may be defined later
                    continue;
                }
                JinahyaAttributeUtils.setAttributeValue(object, decryptedAttribute, null);
                continue;
            }
            final var decryptedBytes = encryptionManager.decrypt(encryptionIdentifier, encryptedBytes);
            final Object decryptedValue;
            final var javaType = decryptedAttribute.getJavaType();
            if (javaType == boolean.class || javaType == Boolean.class) {
                decryptedValue = decryptedBytes[0] == 1;
            } else if (javaType == byte.class || javaType == Byte.class) {
                decryptedValue = decryptedBytes[0];
            } else if (javaType == short.class || javaType == Short.class) {
                decryptedValue = short_2(decryptedBytes, 0);
            } else if (javaType == int.class || javaType == Integer.class) {
                decryptedValue = int_4(decryptedBytes, 0);
            } else if (javaType == long.class || javaType == Long.class) {
                decryptedValue = long_8(decryptedBytes, 0);
            } else if (javaType == char.class || javaType == Character.class) {
                decryptedValue = char_2(decryptedBytes, 0);
            } else if (javaType == float.class || javaType == Float.class) {
                decryptedValue = float_4(decryptedBytes, 0);
            } else if (javaType == double.class || javaType == Double.class) {
                decryptedValue = double_8(decryptedBytes, 0);
            } else if (javaType == String.class) {
                decryptedValue = string_(decryptedBytes);
            } else if (javaType == UUID.class) {
                decryptedValue = uuid_16(decryptedBytes);
            } else if (javaType == BigInteger.class) {
                decryptedValue = new BigInteger(decryptedBytes);
            } else if (javaType == BigDecimal.class) {
                decryptedValue = big_decimal_(decryptedBytes);
            } else if (javaType == LocalDate.class) {
                decryptedValue = local_date_8(decryptedBytes, 0);
            } else if (javaType == LocalTime.class) {
                decryptedValue = local_time_8(decryptedBytes, 0);
            } else if (javaType == LocalDateTime.class) {
                decryptedValue = local_date_time_16(decryptedBytes, 0);
            } else if (javaType == OffsetTime.class) {
                decryptedValue = offset_time_12(decryptedBytes, 0);
            } else if (javaType == OffsetDateTime.class) {
                decryptedValue = offset_date_time_20(decryptedBytes, 0);
            } else if (javaType == Instant.class) {
                decryptedValue = instant_16(decryptedBytes, 0);
            } else if (javaType == Year.class) {
                decryptedValue = year_4(decryptedBytes, 0);
            } else if (javaType == Calendar.class) {
                final var date = util_date_8(decryptedBytes, 0);
                decryptedValue = Calendar.getInstance();
                ((Calendar) decryptedValue).setTime(date);
            } else if (java.util.Date.class.isAssignableFrom(javaType)) {
                final var time = long_8(decryptedBytes, 0);
                try {
                    decryptedValue = javaType.getConstructor(long.class).newInstance(time);
                } catch (final ReflectiveOperationException roe) {
                    throw new RuntimeException(roe);
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
                decryptedValue = javaType.cast(serializable_(encryptedBytes));
            } else {
                throw new RuntimeException("unsupported java type: " + javaType);
            }
            JinahyaAttributeUtils.setAttributeValue(object, decryptedAttribute, decryptedValue);
            JinahyaAttributeUtils.setAttributeValue(object, encryptedAttribute, null);
        }
    }

    public void decrypt(final @Valid @NotNull Object object) {
        Objects.requireNonNull(object, "object is null");
        final var encryptionIdentifier = encryptionManager.getEncryptionIdentifier(object);
        decrypt(encryptionIdentifier, object);
//        final var entityClass = object.getClass();
//        final var entityType = getEntityType(entityClass);
//        final var attributes = getAttributes(entityType);
//        final var encryptedAttributes = new HashSet<Attribute<?, ?>>();
//        for (final var entry : attributes.entrySet()) {
//            final var name = entry.getKey();
//            final var decryptedAttribute = entry.getValue();
//            final var persistenceAttributeType = decryptedAttribute.getPersistentAttributeType();
//            if (persistenceAttributeType != Attribute.PersistentAttributeType.BASIC) {
//                continue;
//            }
//            final var attributeAnnotation =
//                    JinahyaAttributeUtils.getJavaMemberAnnotation(decryptedAttribute, __EncryptedAttribute.class);
//            if (attributeAnnotation == null) {
//                continue;
//            }
//            // TODO: @Embedded
//            final var encryptedAttribute = Optional.of(attributeAnnotation.encryptedAttribute())
//                    .map(v -> v.isBlank() ? decryptedAttribute.getName() + "Enc__" : v)
//                    .map(attributes::get)
//                    .orElseThrow(() -> new RuntimeException(
//                            "no encrypted attribute found" +
//                            "; decrypted attribute: " + decryptedAttribute +
//                            "; attribute annotation: " + attributeAnnotation
//                    ));
//            check(decryptedAttribute, attributeAnnotation, encryptedAttribute, encryptedAttributes);
//            final var encryptedBytes = (byte[]) JinahyaAttributeUtils.getAttributeValue(object, encryptedAttribute);
//            if (encryptedBytes == null) {
//                final var decryptedValue = JinahyaAttributeUtils.getAttributeValue(object, decryptedAttribute);
//                if (decryptedValue != null) {
//                    // the encrypted column may be defined later
//                    continue;
//                }
//                JinahyaAttributeUtils.setAttributeValue(object, decryptedAttribute, null);
//                continue;
//            }
//            final var decryptedBytes = encryptionManager.decrypt(encryptionIdentifier, encryptedBytes);
//            final Object decryptedValue;
//            final var javaType = decryptedAttribute.getJavaType();
//            if (javaType == boolean.class || javaType == Boolean.class) {
//                decryptedValue = decryptedBytes[0] == 1;
//            } else if (javaType == byte.class || javaType == Byte.class) {
//                decryptedValue = decryptedBytes[0];
//            } else if (javaType == short.class || javaType == Short.class) {
//                decryptedValue = short_2(decryptedBytes, 0);
//            } else if (javaType == int.class || javaType == Integer.class) {
//                decryptedValue = int_4(decryptedBytes, 0);
//            } else if (javaType == long.class || javaType == Long.class) {
//                decryptedValue = long_8(decryptedBytes, 0);
//            } else if (javaType == char.class || javaType == Character.class) {
//                decryptedValue = char_2(decryptedBytes, 0);
//            } else if (javaType == float.class || javaType == Float.class) {
//                decryptedValue = float_4(decryptedBytes, 0);
//            } else if (javaType == double.class || javaType == Double.class) {
//                decryptedValue = double_8(decryptedBytes, 0);
//            } else if (javaType == String.class) {
//                decryptedValue = string_(decryptedBytes);
//            } else if (javaType == UUID.class) {
//                decryptedValue = uuid_16(decryptedBytes);
//            } else if (javaType == BigInteger.class) {
//                decryptedValue = new BigInteger(decryptedBytes);
//            } else if (javaType == BigDecimal.class) {
//                decryptedValue = big_decimal_(decryptedBytes);
//            } else if (javaType == LocalDate.class) {
//                decryptedValue = local_date_8(decryptedBytes, 0);
//            } else if (javaType == LocalTime.class) {
//                decryptedValue = local_time_8(decryptedBytes, 0);
//            } else if (javaType == LocalDateTime.class) {
//                decryptedValue = local_date_time_16(decryptedBytes, 0);
//            } else if (javaType == OffsetTime.class) {
//                decryptedValue = offset_time_12(decryptedBytes, 0);
//            } else if (javaType == OffsetDateTime.class) {
//                decryptedValue = offset_date_time_20(decryptedBytes, 0);
//            } else if (javaType == Instant.class) {
//                decryptedValue = instant_16(decryptedBytes, 0);
//            } else if (javaType == Year.class) {
//                decryptedValue = year_4(decryptedBytes, 0);
//            } else if (javaType == Calendar.class) {
//                final var date = util_date_8(decryptedBytes, 0);
//                decryptedValue = Calendar.getInstance();
//                ((Calendar) decryptedValue).setTime(date);
//            } else if (java.util.Date.class.isAssignableFrom(javaType)) {
//                final var time = long_8(decryptedBytes, 0);
//                try {
//                    decryptedValue = javaType.getConstructor(long.class).newInstance(time);
//                } catch (final ReflectiveOperationException roe) {
//                    throw new RuntimeException(roe);
//                }
//            } else if (javaType == byte[].class) {
//                decryptedValue = decryptedBytes;
//            } else if (javaType == Byte[].class) {
//                logger.log(System.Logger.Level.WARNING, "Byte[] is not encouraged; use byte[]");
//                decryptedValue = Bytes_l(decryptedBytes);
//            } else if (javaType == char[].class) {
//                decryptedValue = chars_2l(decryptedBytes);
//            } else if (javaType == Character[].class) {
//                logger.log(System.Logger.Level.WARNING, "Character[] is not encouraged; use char[]");
//                decryptedValue = Characters_2l(decryptedBytes);
//            } else if (javaType.isEnum()) {
//                decryptedValue = enum_(decryptedBytes, (Class) javaType);
//            } else if (Serializable.class.isAssignableFrom(javaType)) {
//                decryptedValue = javaType.cast(serializable_(encryptedBytes));
//            } else {
//                throw new RuntimeException("unsupported java type: " + javaType);
//            }
//            JinahyaAttributeUtils.setAttributeValue(object, decryptedAttribute, decryptedValue);
//            JinahyaAttributeUtils.setAttributeValue(object, encryptedAttribute, null);
//        }
    }

    // ----------------------------------------------------------------------------------------------------- entityTypes
//    protected @Nonnull EntityType<?> getEntityType(@Nonnull final Class<?> entityClass) {
//        return entityTypes.computeIfAbsent(
//                Objects.requireNonNull(entityClass, "entityClass is null"),
//                k -> JinahyaEntityTypeUtils.getEntityType(k, List.of(entityManagerFactory))
//        );
//    }

    // ----------------------------------------------------------------------------------------------------- managedTypes
    protected @Nonnull ManagedType<?> getManagedType(@Nonnull final Class<?> entityClass) {
        return managedTypes.computeIfAbsent(
                Objects.requireNonNull(entityClass, "entityClass is null"),
                k -> JinahyaManagedTypeUtils.getManagedType(k, List.of(entityManagerFactory))
        );
    }

    // -------------------------------------------------------------------------------------------- entityManagerFactory

    // ----------------------------------------------------------------------------------------------- encryptionManager

    // -----------------------------------------------------------------------------------------------------------------
//    private final Map<Class<?>, EntityType<?>> entityTypes = new ConcurrentHashMap<>();

    private final Map<Class<?>, ManagedType<?>> managedTypes = new ConcurrentHashMap<>();

//    @Any
//    @Inject
//    private Instance<EntityManagerFactory> factoryInstance;

    // -----------------------------------------------------------------------------------------------------------------
    private final EntityManagerFactory entityManagerFactory;

    private final __EncryptionManager encryptionManager;
}
