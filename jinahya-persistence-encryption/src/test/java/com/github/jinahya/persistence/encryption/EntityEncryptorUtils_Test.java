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
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import static com.github.jinahya.persistence.encryption.EntityEncryptorUtils.Bytes_l;
import static com.github.jinahya.persistence.encryption.EntityEncryptorUtils.Characters_2l;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link EntityEncryptorUtils}: the codecs which are still its own ({@code Byte[]}, {@code Character[]}),
 * the exact-length check of a stored payload (#76), and the payload frame (#75).
 * <p>
 * The encodings of the other codecs are pinned by the {@code ValueCodecs_Of*_Test} classes.
 *
 * @see <a href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#a486">2.6. Basic
 *         Types</a> (Jakarta Persistence 3.2 Specification Document)
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class EntityEncryptorUtils_Test {

    private static byte[] randomBytes(final int minLengthInclusive, final int maxLengthInclusive) {
        assert minLengthInclusive >= 0;
        assert maxLengthInclusive >= minLengthInclusive;
        final var v = new byte[ThreadLocalRandom.current().nextInt(minLengthInclusive, maxLengthInclusive + 1)];
        ThreadLocalRandom.current().nextBytes(v);
        return v;
    }

    // ---------------------------------------------------------------------------------------------------------- Byte[]
    @SuppressWarnings({"deprecation"})
    @DisplayName("Bytes_l")
    @Nested
    class Bytes_l_Test {

        @DisplayName("Bytes_l(Byte[]) encodes the unboxed bytes, without modifying its argument")
        @Test
        void __encode() {
            final var p = randomBytes(1, 128);
            final var v = new Byte[p.length];
            for (int i = 0; i < v.length; i++) {
                v[i] = p[i];
            }
            // an expected value which the call under test cannot reach
            final var expected = v.clone();

            final var b = Bytes_l(v);

            assertThat(b).as("the encoded bytes").isEqualTo(p);
            assertThat(v).as("the argument must not be modified").isEqualTo(expected);
        }

        @DisplayName("Bytes_l(byte[]) decodes the boxed bytes, without modifying its argument")
        @Test
        void __decode() {
            final var b = randomBytes(1, 128);
            final var expected = b.clone();

            final var v = Bytes_l(b);

            assertThat(v).hasSameSizeAs(b);
            for (int i = 0; i < v.length; i++) {
                assertThat(v[i]).isEqualTo(b[i]);
            }
            assertThat(b).as("the argument must not be modified").isEqualTo(expected);
        }

        @DisplayName("Bytes_l(byte[]) reads back what Bytes_l(Byte[]) wrote, empty included")
        @Test
        void __roundTrip() {
            assertThat(Bytes_l(Bytes_l(new Byte[0]))).isEmpty();
            final var p = randomBytes(1, 128);
            final var v = new Byte[p.length];
            for (int i = 0; i < v.length; i++) {
                v[i] = p[i];
            }
            assertThat(Bytes_l(Bytes_l(v))).isEqualTo(v);
        }
    }

    // ----------------------------------------------------------------------------------------------------- Character[]
    @DisplayName("Characters_2l")
    @Nested
    class Characters_2l_Test {

        @DisplayName(
                "Characters_2l encodes two bytes per character, the same as chars_2l, without modifying its argument")
        @Test
        void __encode() {
            final var v = new Character[]{'j', 'i', 'n', 'ä', '한', '￿', '\u0080'};
            final var expected = v.clone();

            final var b = Characters_2l(v);

            assertThat(b).as("two bytes per character").hasSize(v.length << 1);
            assertThat(b).as("the same format as char[]")
                    .isEqualTo(ValueCodecs.OfPrimitive.encodeChars("jinä한￿\u0080".toCharArray()));
            assertThat(v).as("the argument must not be modified").isEqualTo(expected);
        }

        @DisplayName("Characters_2l(byte[]) reads back what Characters_2l(Character[]) wrote, empty included")
        @Test
        void __roundTrip() {
            assertThat(Characters_2l(Characters_2l(new Character[0]))).isEmpty();
            final var v = new Character[]{'j', 'i', 'n', 'ä', '한', '￿', '\u0080'};
            assertThat(Characters_2l(Characters_2l(v))).isEqualTo(v);
        }

        @DisplayName("an odd number of bytes is rejected, not silently truncated")
        @Test
        void __decodeOdd() {
            assertThatThrownBy(() -> Characters_2l(new byte[]{1, 2, 3}))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("not a multiple of " + Character.BYTES);
        }
    }

    // ------------------------------------------------------------------------------------------------- decode (#76)
    @DisplayName("decode(Pair, byte[]) reads a fixed-width payload of exactly its width (#76)")
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
            return new EncryptionMapping.Pair(decrypted, null, javaType);
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

        @DisplayName("a payload one byte short, or one byte over, is rejected against the attribute")
        @MethodSource("fixedWidths")
        @ParameterizedTest
        void __wrongLength(final Class<?> javaType, final int width) {
            final var codec = EntityEncryptorUtils.codecOf(javaType);
            for (final var length : new int[]{width - 1, width + 1}) {
                final var framed = EntityEncryptorUtils.frame(codec, new byte[length]);
                assertThatThrownBy(() -> EntityEncryptorUtils.decode(pair(javaType), framed))
                        .as("%s from %d byte(s)", javaType.getSimpleName(), length)
                        .isInstanceOf(RuntimeException.class)
                        .hasMessageContaining("decrypted attribute: secret")
                        .cause()
                        .hasMessageContaining("reads exactly " + width);
            }
        }

        @DisplayName("a payload of exactly the width is decoded")
        @MethodSource("fixedWidths")
        @ParameterizedTest
        void __exactLength(final Class<?> javaType, final int width) {
            final var framed = EntityEncryptorUtils.frame(EntityEncryptorUtils.codecOf(javaType), new byte[width]);
            assertThat(EntityEncryptorUtils.decode(pair(javaType), framed)).isInstanceOf(javaType);
        }
    }

    // --------------------------------------------------------------------------------------------------- payload frame
    @DisplayName("payload frame (#75)")
    @Nested
    class Frame_Test {

        @DisplayName("codec ids are stored data: pinned, and unique")
        @Test
        void __codecIdsPinned() {
            // retired ids are never reused: 23, java.util.Calendar, and 30, java.io.Serializable (#99)
            final var retired = java.util.Set.of((byte) 23, (byte) 30);
            byte expected = 0;
            for (final var codec : EntityEncryptorUtils.Codec.values()) {
                // appending a codec is fine; renumbering or reusing an id is a format break
                do {
                    expected++;
                } while (retired.contains(expected));
                assertThat(codec.id).as("id of %s", codec).isEqualTo(expected);
                assertThat(EntityEncryptorUtils.Codec.of(codec.id)).isSameAs(codec);
            }
            for (final var id : retired) {
                assertThat(EntityEncryptorUtils.Codec.of(id)).as("retired id %d", id).isNull();
            }
            assertThat(EntityEncryptorUtils.Codec.of((byte) 0)).isNull();
            assertThat(EntityEncryptorUtils.Codec.of(Byte.MAX_VALUE)).isNull();
        }

        @DisplayName("codecOf(Class) follows the ladders: java.sql before java.util.Date")
        @Test
        void __codecOf() {
            assertThat(EntityEncryptorUtils.codecOf(Integer.class))
                    .isSameAs(EntityEncryptorUtils.Codec.INT_4);
            assertThat(EntityEncryptorUtils.codecOf(int.class))
                    .isSameAs(EntityEncryptorUtils.Codec.INT_4);
            assertThat(EntityEncryptorUtils.codecOf(java.sql.Timestamp.class))
                    .isSameAs(EntityEncryptorUtils.Codec.SQL_TIMESTAMP_12);
            assertThat(EntityEncryptorUtils.codecOf(java.util.Date.class))
                    .isSameAs(EntityEncryptorUtils.Codec.UTIL_DATE_8);
            assertThat(EntityEncryptorUtils.codecOf(java.time.DayOfWeek.class))
                    .isSameAs(EntityEncryptorUtils.Codec.ENUM_);
            assertThat(EntityEncryptorUtils.codecOf(java.util.Calendar.class)).as("not supported (#99)").isNull();
            assertThat(EntityEncryptorUtils.codecOf(java.util.GregorianCalendar.class)).as("not supported (#99)")
                    .isNull();
            assertThat(EntityEncryptorUtils.codecOf(java.util.ArrayList.class))
                    .as("Serializable is not supported (#99)")
                    .isNull();
            assertThat(EntityEncryptorUtils.codecOf(Object.class)).as("unsupported").isNull();
        }

        @DisplayName("frame(Codec, byte[]) prefixes the format version and the codec id")
        @Test
        void __frame() {
            final var framed = EntityEncryptorUtils.frame(EntityEncryptorUtils.Codec.INT_4,
                                                          new byte[]{1, 2, 3, 4});
            assertThat(framed).containsExactly(
                    EntityEncryptorUtils.FORMAT_VERSION, EntityEncryptorUtils.Codec.INT_4.id,
                    1, 2, 3, 4);
        }

        @DisplayName("unframe(Codec, byte[]) reads back what frame wrote, empty included")
        @Test
        void __roundTrip() {
            final var codec = EntityEncryptorUtils.Codec.STRING_;
            assertThat(EntityEncryptorUtils.unframe(codec, EntityEncryptorUtils.frame(codec, new byte[0])))
                    .isEmpty();
            final var encoded = randomBytes(1, 128);
            assertThat(EntityEncryptorUtils.unframe(codec, EntityEncryptorUtils.frame(codec, encoded)))
                    .isEqualTo(encoded);
        }

        @DisplayName("unframe(Codec, byte[]) rejects a missing header, another version, and another codec")
        @Test
        void __unframeRejects() {
            final var codec = EntityEncryptorUtils.Codec.INT_4;
            assertThatThrownBy(() -> EntityEncryptorUtils.unframe(codec, new byte[1]))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("no payload header");
            assertThatThrownBy(() -> EntityEncryptorUtils.unframe(codec, new byte[]{0, codec.id}))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("unknown payload format version");
            assertThatThrownBy(() -> EntityEncryptorUtils.unframe(
                    codec, EntityEncryptorUtils.frame(EntityEncryptorUtils.Codec.LONG_8, new byte[8])))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("encoded by LONG_8")
                    .hasMessageContaining("decoded by INT_4");
            assertThatThrownBy(() -> EntityEncryptorUtils.unframe(
                    codec, new byte[]{EntityEncryptorUtils.FORMAT_VERSION, Byte.MAX_VALUE}))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("unknown codec");
        }
    }
}
