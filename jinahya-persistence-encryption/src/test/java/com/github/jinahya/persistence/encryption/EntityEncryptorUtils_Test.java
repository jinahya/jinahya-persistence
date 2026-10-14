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
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
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
        private static EncryptionMapping.Pair pair(final Class<?> javaType) {
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
