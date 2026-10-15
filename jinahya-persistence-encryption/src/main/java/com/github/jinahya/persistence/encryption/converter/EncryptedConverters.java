package com.github.jinahya.persistence.encryption.converter;

import com.github.jinahya.persistence.encryption.EntityEncryptionService;
import jakarta.persistence.AttributeConverter;

import java.lang.invoke.MethodType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

/**
 * Converters for a declared java type, with a given service: the hook a persistence provider integration builds on,
 * so that it picks the codec of an attribute's declared type, and supplies the service itself, without reaching the
 * codecs.
 * <p>
 * A converter returned here is one of the module's own ({@code Encrypted<Type>Converter}, or an
 * {@link EncryptedEnumConverter} for an enum type), so its bytes are those of the named converter of the same type;
 * it differs only in that its service is the one given, neither injected nor loaded.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public final class EncryptedConverters {

    /**
     * The name of the persistence unit property a persistence provider integration reads its service from:
     * {@value}. The value is an {@link EntityEncryptionService} instance, its {@link Class}, or the name of its class;
     * a class has to have a public no-arg constructor.
     *
     * @see #serviceOf(Object)
     */
    public static final String PROPERTY_SERVICE = "jinahya.persistence.encryption.service";

    private static final Map<Class<?>, Function<EntityEncryptionService, AttributeConverter<?, byte[]>>> FACTORIES =
            Map.ofEntries(
                    Map.entry(Boolean.class, EncryptedBooleanConverter::new),
                    Map.entry(Byte.class, EncryptedByteConverter::new),
                    Map.entry(Short.class, EncryptedShortConverter::new),
                    Map.entry(Character.class, EncryptedCharacterConverter::new),
                    Map.entry(Integer.class, EncryptedIntegerConverter::new),
                    Map.entry(Float.class, EncryptedFloatConverter::new),
                    Map.entry(Long.class, EncryptedLongConverter::new),
                    Map.entry(Double.class, EncryptedDoubleConverter::new),
                    Map.entry(String.class, EncryptedStringConverter::new),
                    Map.entry(UUID.class, EncryptedUuidConverter::new),
                    Map.entry(BigInteger.class, EncryptedBigIntegerConverter::new),
                    Map.entry(BigDecimal.class, EncryptedBigDecimalConverter::new),
                    Map.entry(LocalDate.class, EncryptedLocalDateConverter::new),
                    Map.entry(LocalTime.class, EncryptedLocalTimeConverter::new),
                    Map.entry(LocalDateTime.class, EncryptedLocalDateTimeConverter::new),
                    Map.entry(OffsetTime.class, EncryptedOffsetTimeConverter::new),
                    Map.entry(OffsetDateTime.class, EncryptedOffsetDateTimeConverter::new),
                    Map.entry(Instant.class, EncryptedInstantConverter::new),
                    Map.entry(Year.class, EncryptedYearConverter::new),
                    Map.entry(java.util.Date.class, EncryptedUtilDateConverter::new),
                    Map.entry(java.sql.Date.class, EncryptedSqlDateConverter::new),
                    Map.entry(java.sql.Time.class, EncryptedSqlTimeConverter::new),
                    Map.entry(java.sql.Timestamp.class, EncryptedSqlTimestampConverter::new),
                    Map.entry(byte[].class, EncryptedBytesConverter::new),
                    Map.entry(Byte[].class, EncryptedBytesWrappedConverter::new),
                    Map.entry(char[].class, EncryptedCharsConverter::new),
                    Map.entry(Character[].class, EncryptedCharsWrappedConverter::new)
            );

    /**
     * The java types which {@link #forType(Class, EntityEncryptionService)} supports, enums aside: a primitive is
     * supported as its wrapper.
     */
    private static final Set<Class<?>> SUPPORTED = FACTORIES.keySet();

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns whether a converter exists for the specified declared java type.
     *
     * @param declaredType the declared java type of an attribute.
     * @return {@code true} when {@link #forType(Class, EntityEncryptionService)} returns a converter for it.
     */
    public static boolean supports(final Class<?> declaredType) {
        Objects.requireNonNull(declaredType, "declaredType is null");
        return SUPPORTED.contains(wrap(declaredType)) || declaredType.isEnum();
    }

    /**
     * Returns a converter for the specified declared java type, working through the specified service.
     *
     * @param declaredType the declared java type of the attribute; a primitive is taken as its wrapper, and an enum
     *                     type gets an {@link EncryptedEnumConverter} of its own.
     * @param service      the service the converter encrypts and decrypts with.
     * @param <T>          the java type.
     * @return a converter for the {@code declaredType}; a new instance on every call.
     * @throws IllegalArgumentException when no converter exists for the {@code declaredType}.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> AttributeConverter<T, byte[]> forType(final Class<T> declaredType,
                                                            final EntityEncryptionService service) {
        Objects.requireNonNull(declaredType, "declaredType is null");
        Objects.requireNonNull(service, "service is null");
        final var wrapped = wrap(declaredType);
        final var factory = FACTORIES.get(wrapped);
        if (factory != null) {
            return (AttributeConverter<T, byte[]>) factory.apply(service);
        }
        if (declaredType.isEnum()) {
            return new EncryptedEnumConverter(declaredType.asSubclass(Enum.class), service) {
            };
        }
        throw new IllegalArgumentException("no encrypted converter for " + declaredType.getName());
    }

    /**
     * Returns the service a converter would load when none is injected: the one named in {@code META-INF/services/}.
     *
     * @return the loaded service.
     * @throws IllegalStateException when none is named, or more than one is.
     */
    public static EntityEncryptionService loadService() {
        return EntityEncryptionServices.load();
    }

    /**
     * Returns the service the specified value of {@link #PROPERTY_SERVICE} stands for; or, for {@code null}, the one
     * {@link #loadService()} loads.
     *
     * @param value the value of the property: an {@link EntityEncryptionService}, its {@link Class}, the name of its
     *              class, or {@code null}.
     * @return the service; a new instance when the {@code value} is a class, or a class name.
     * @throws IllegalArgumentException when the {@code value} is none of the above, or the class cannot be
     *                                  instantiated.
     * @throws IllegalStateException    when the {@code value} is {@code null} and no service can be loaded.
     */
    public static EntityEncryptionService serviceOf(final @org.jspecify.annotations.Nullable Object value) {
        if (value == null) {
            return loadService();
        }
        if (value instanceof EntityEncryptionService service) {
            return service;
        }
        final Class<?> type;
        if (value instanceof Class<?> c) {
            type = c;
        } else if (value instanceof String name && !name.isBlank()) {
            try {
                type = Class.forName(name.strip(), true, Thread.currentThread().getContextClassLoader());
            } catch (final ClassNotFoundException cnfe) {
                throw new IllegalArgumentException("no class named " + name + " for " + PROPERTY_SERVICE, cnfe);
            }
        } else {
            throw new IllegalArgumentException(
                    PROPERTY_SERVICE + " has to be an EntityEncryptionService, its class, or the name of its class;"
                    + " found " + value.getClass().getName());
        }
        if (!EntityEncryptionService.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException(type.getName() + " is not an EntityEncryptionService");
        }
        try {
            return (EntityEncryptionService) type.getConstructor().newInstance();
        } catch (final ReflectiveOperationException roe) {
            throw new IllegalArgumentException("failed to instantiate " + type.getName(), roe);
        }
    }

    private static Class<?> wrap(final Class<?> type) {
        return type.isPrimitive() ? MethodType.methodType(type).wrap().returnType() : type;
    }

    private EncryptedConverters() {
        throw new AssertionError("instantiation is not allowed");
    }
}
