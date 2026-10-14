package com.github.jinahya.persistence.encryption;

import jakarta.persistence.metamodel.Attribute;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link EntityEncryptorUtils}: the types it supports, and the decoding of a stored payload by the declared
 * java type alone, no type information being stored.
 * <p>
 * The encodings of the other codecs are pinned by the {@code ValueCodecs_Of*_Test} classes.
 *
 * @see <a href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#a486">2.6. Basic
 *         Types</a> (Jakarta Persistence 3.2 Specification Document)
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class EntityEncryptorUtils_Test {

    // ------------------------------------------------------------------------------------------------------- decode
    @DisplayName("decode(Pair, byte[]) reads a fixed-width payload by the declared java type")
    @Nested
    class Decode_Test {

        /**
         * Returns a pair of the specified declared type; only the name of its plaintext attribute is answered, which is
         * all {@code decode} reads of it.
         */
        static EncryptionMapping.Pair pair(final Class<?> javaType) {
            final var decrypted = (Attribute<?, ?>) Proxy.newProxyInstance(
                    Attribute.class.getClassLoader(),
                    new Class<?>[]{Attribute.class},
                    (proxy, method, args) -> {
                        if (method.getName().equals("getName")) {
                            return "secret";
                        }
                        throw new UnsupportedOperationException(method.getName());
                    });
            return new EncryptionMapping.Pair(decrypted, null, javaType, true);
        }

        @SuppressWarnings("deprecation")
        private static Stream<Arguments> fixedWidths() {
            return Stream.of(
                    Arguments.of(Boolean.class, Byte.BYTES),
                    Arguments.of(Byte.class, Byte.BYTES),
                    Arguments.of(Short.class, Short.BYTES),
                    Arguments.of(Integer.class, Integer.BYTES),
                    Arguments.of(Long.class, Long.BYTES),
                    Arguments.of(Character.class, Character.BYTES),
                    Arguments.of(Float.class, Float.BYTES),
                    Arguments.of(Double.class, Double.BYTES),
                    Arguments.of(UUID.class, ValueCodecs.OfUtil.UUID_BYTES),
                    Arguments.of(LocalDate.class, ValueCodecs.OfTime.LOCAL_DATE_BYTES),
                    Arguments.of(LocalTime.class, ValueCodecs.OfTime.LOCAL_TIME_BYTES),
                    Arguments.of(LocalDateTime.class, ValueCodecs.OfTime.LOCAL_DATE_TIME_BYTES),
                    Arguments.of(OffsetTime.class, ValueCodecs.OfTime.OFFSET_TIME_BYTES),
                    Arguments.of(OffsetDateTime.class, ValueCodecs.OfTime.OFFSET_DATE_TIME_BYTES),
                    Arguments.of(Instant.class, ValueCodecs.OfTime.INSTANT_BYTES),
                    Arguments.of(Year.class, ValueCodecs.OfTime.YEAR_BYTES),
                    Arguments.of(java.util.Date.class, ValueCodecs.OfUtil.DATE_BYTES),
                    Arguments.of(java.sql.Date.class, ValueCodecs.OfSql.DATE_BYTES),
                    Arguments.of(java.sql.Time.class, ValueCodecs.OfSql.TIME_BYTES),
                    Arguments.of(java.sql.Timestamp.class, ValueCodecs.OfSql.TIMESTAMP_BYTES)
            );
        }

        @DisplayName("a payload one byte short is rejected against the attribute")
        @MethodSource("fixedWidths")
        @ParameterizedTest
        void __short(final Class<?> javaType, final int width) {
            assertThatThrownBy(() -> EntityEncryptorUtils.decode(pair(javaType), new byte[width - 1]))
                    .as("%s from %d byte(s)", javaType.getSimpleName(), width - 1)
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("decrypted attribute: secret")
                    .cause()
                    .hasMessageContaining("this codec reads " + width);
        }

        @DisplayName("a payload of exactly the width is decoded")
        @MethodSource("fixedWidths")
        @ParameterizedTest
        void __exactLength(final Class<?> javaType, final int width) {
            assertThat(EntityEncryptorUtils.decode(pair(javaType), new byte[width])).isInstanceOf(javaType);
        }

        @DisplayName("a payload one byte over is decoded from its leading bytes; the extra byte is not read")
        @MethodSource("fixedWidths")
        @ParameterizedTest
        void __overLong(final Class<?> javaType, final int width) {
            assertThat(EntityEncryptorUtils.decode(pair(javaType), new byte[width + 1])).isInstanceOf(javaType);
        }
    }

    // ---------------------------------------------------------------------------------------------- encode / decode
    @DisplayName("decode(encode(v)) is v, for every supported declared java type")
    @Nested
    class RoundTrip_Test {

        /**
         * One value of each supported declared java type, primitives included, and an enum.
         */
        @SuppressWarnings("deprecation")
        static Stream<Arguments> values() {
            return Stream.of(
                    Arguments.of(boolean.class, true),
                    Arguments.of(Boolean.class, Boolean.TRUE),
                    Arguments.of(byte.class, (byte) -7),
                    Arguments.of(Byte.class, (byte) 7),
                    Arguments.of(short.class, (short) -1234),
                    Arguments.of(Short.class, (short) 1234),
                    Arguments.of(int.class, -37),
                    Arguments.of(Integer.class, 37),
                    Arguments.of(long.class, -1_234_567_890_123L),
                    Arguments.of(Long.class, 1_234_567_890_123L),
                    Arguments.of(char.class, 'K'),
                    Arguments.of(Character.class, '\uD55C'),
                    Arguments.of(float.class, -3.5f),
                    Arguments.of(Float.class, 3.5f),
                    Arguments.of(double.class, -2.25d),
                    Arguments.of(Double.class, 2.25d),
                    Arguments.of(String.class, "Jane Roe \uD55C\uAE00"),
                    Arguments.of(UUID.class, UUID.fromString("123e4567-e89b-12d3-a456-426614174000")),
                    Arguments.of(BigInteger.class, new BigInteger("-123456789012345678901234567890")),
                    Arguments.of(BigDecimal.class, new BigDecimal("12345.6789")),
                    Arguments.of(LocalDate.class, LocalDate.of(1988, 11, 3)),
                    Arguments.of(LocalTime.class, LocalTime.of(3, 4, 5, 123_456_789)),
                    Arguments.of(LocalDateTime.class, LocalDateTime.of(2026, 1, 2, 3, 4, 5, 123_456_789)),
                    Arguments.of(OffsetTime.class, OffsetTime.of(10, 11, 12, 13, ZoneOffset.ofHours(9))),
                    Arguments.of(OffsetDateTime.class,
                                 OffsetDateTime.of(2026, 1, 2, 3, 4, 5, 6, ZoneOffset.ofHoursMinutes(-5, -30))),
                    Arguments.of(Instant.class, Instant.ofEpochSecond(1_700_000_000L, 123_456_789)),
                    Arguments.of(Year.class, Year.of(2026)),
                    Arguments.of(java.util.Date.class, new java.util.Date(1_700_000_000_123L)),
                    Arguments.of(java.sql.Date.class, java.sql.Date.valueOf("2026-01-02")),
                    Arguments.of(java.sql.Time.class, java.sql.Time.valueOf("03:04:05")),
                    Arguments.of(java.sql.Timestamp.class, java.sql.Timestamp.valueOf("2026-01-02 03:04:05.123456789")),
                    Arguments.of(byte[].class, new byte[]{0, 1, -1, 127, -128}),
                    Arguments.of(Byte[].class, new Byte[]{0, 1, -1, 127, -128}),
                    Arguments.of(char[].class, new char[]{'a', '\uD55C', '\0'}),
                    Arguments.of(Character[].class, new Character[]{'a', '\uD55C', '\0'}),
                    Arguments.of(java.time.DayOfWeek.class, java.time.DayOfWeek.FRIDAY)
            );
        }

        @DisplayName("the value is decoded back, by the declared java type alone")
        @MethodSource("values")
        @ParameterizedTest
        void __roundTrip(final Class<?> javaType, final Object value) {
            final var pair = Decode_Test.pair(javaType);
            final var encoded = EntityEncryptorUtils.encode(pair, value);
            assertThat(EntityEncryptorUtils.decode(pair, encoded)).isEqualTo(value);
        }

        @DisplayName("every supported type has a value above; a type added to the codecs has to be added here")
        @Test
        void __everySupportedTypeIsCovered() throws ReflectiveOperationException {
            final var field = EntityEncryptorUtils.class.getDeclaredField("SUPPORTED");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            final var supported = (java.util.Set<Class<?>>) field.get(null);
            final java.util.Set<Class<?>> covered = values()
                    .map(a -> (Class<?>) a.get()[0])
                    .collect(java.util.stream.Collectors.toSet());
            assertThat(covered).containsAll(supported);
            assertThat(covered).anyMatch(Class::isEnum);
        }
    }

    // ------------------------------------------------------------------------------------------------------ supports
    @DisplayName("supports(Class)")
    @Nested
    class Supports_Test {

        @DisplayName("the supported types, a primitive and an enum included")
        @Test
        void __supported() {
            assertThat(EntityEncryptorUtils.supports(Integer.class)).isTrue();
            assertThat(EntityEncryptorUtils.supports(int.class)).isTrue();
            assertThat(EntityEncryptorUtils.supports(String.class)).isTrue();
            assertThat(EntityEncryptorUtils.supports(java.sql.Timestamp.class)).isTrue();
            assertThat(EntityEncryptorUtils.supports(java.util.Date.class)).isTrue();
            assertThat(EntityEncryptorUtils.supports(java.time.DayOfWeek.class)).isTrue();
            assertThat(EntityEncryptorUtils.supports(byte[].class)).isTrue();
        }

        @DisplayName("the unsupported types (#92, #99)")
        @Test
        void __unsupported() {
            assertThat(EntityEncryptorUtils.supports(java.util.Calendar.class)).isFalse();
            assertThat(EntityEncryptorUtils.supports(java.util.GregorianCalendar.class)).isFalse();
            assertThat(EntityEncryptorUtils.supports(java.util.ArrayList.class)).as("Serializable").isFalse();
            assertThat(EntityEncryptorUtils.supports(Object.class)).isFalse();
            assertThat(EntityEncryptorUtils.supports(UtilDateSubclass.class))
                    .as("another subclass of java.util.Date couldn't be rebuilt on read")
                    .isFalse();
        }
    }

    @SuppressWarnings("serial")
    private static final class UtilDateSubclass extends java.util.Date {
    }
}
