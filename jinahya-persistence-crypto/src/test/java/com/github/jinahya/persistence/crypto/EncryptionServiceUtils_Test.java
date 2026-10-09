package com.github.jinahya.persistence.crypto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.Bytes_l;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.Characters_2l;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.big_decimal_;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.big_integer_;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.boolean_1;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.byte_1;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.char_2;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.chars_2l;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.double_8;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.enum_;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.float_4;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.instant_12;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.int_4;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.local_date_8;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.local_date_time_16;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.local_time_8;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.long_8;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.offset_4;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.offset_date_time_20;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.offset_time_12;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.serializable_;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.short_2;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.sql_date_8;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.sql_time_8;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.sql_timestamp_16;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.string_;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.util_calendar_8;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.util_date_8;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.uuid_16;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.year_4;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for the encode/decode pairs of {@link EntityEncryptionServiceUtils}.
 * <p>
 * The nested classes follow the order of the pairs in {@link EntityEncryptionServiceUtils}. Each one pins the
 * <em>stored format</em> &mdash; against {@link ByteBuffer}, which is big-endian, as an independent reference &mdash;
 * because a change in an encoding makes every existing row decode differently, and nothing else would notice.
 *
 * @see <a href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#a486">2.6. Basic
 *         Types</a> (Jakarta Persistence 3.2 Specification Document)
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class EncryptionServiceUtils_Test {

    private static byte[] randomBytes(final int minLengthInclusive, final int maxLengthInclusive) {
        assert minLengthInclusive >= 0;
        assert maxLengthInclusive >= minLengthInclusive;
        final var v = new byte[ThreadLocalRandom.current().nextInt(minLengthInclusive, maxLengthInclusive + 1)];
        ThreadLocalRandom.current().nextBytes(v);
        return v;
    }

    /**
     * Returns the bytes the specified writer puts into a big-endian buffer of the specified capacity; the expected
     * format, built without the code under test.
     */
    private static byte[] buffer(final int capacity, final Consumer<? super ByteBuffer> writer) {
        final var buffer = ByteBuffer.allocate(capacity);
        writer.accept(buffer);
        assert !buffer.hasRemaining() : "the expected format does not fill the capacity";
        return buffer.array();
    }

    /**
     * Asserts that decoding a payload shorter than the codec's fixed width fails, rather than reading past it.
     *
     * @implNote The fixed-width codecs check their lengths with {@code assert}, so the failure is an
     *         {@link AssertionError} with {@code -ea}, which surefire enables, and an {@link IndexOutOfBoundsException}
     *         without it; the service treats both alike.
     */
    private static void assertShortPayloadFails(final ThrowableAssert.ThrowingCallable decode) {
        assertThatThrownBy(decode).isInstanceOfAny(AssertionError.class, IndexOutOfBoundsException.class);
    }

    // --------------------------------------------------------------------------------------------------------- boolean
    @DisplayName("boolean_1")
    @Nested
    class Boolean_1_Test {

        @DisplayName("boolean_1(boolean) encodes true as {1} and false as {0}")
        @Test
        void __encode() {
            // the stored format; a change here makes every existing row decode differently
            assertThat(boolean_1(true)).containsExactly(1);
            assertThat(boolean_1(false)).containsExactly(0);
        }

        @DisplayName("boolean_1(byte[]) decodes {1} as true and {0} as false")
        @Test
        void __decode() {
            assertThat(boolean_1(new byte[]{1})).isTrue();
            assertThat(boolean_1(new byte[]{0})).isFalse();
        }

        @DisplayName("boolean_1(byte[]) reads back what boolean_1(boolean) wrote")
        @ValueSource(booleans = {true, false})
        @ParameterizedTest
        void __roundTrip(final boolean v) {
            assertThat(boolean_1(boolean_1(v))).isEqualTo(v);
        }

        @DisplayName("boolean_1(byte[]) decodes any byte other than 1 as false")
        @ValueSource(bytes = {Byte.MIN_VALUE, -1, 2, Byte.MAX_VALUE})
        @ParameterizedTest
        void __decodeOtherThanOne(final byte b) {
            // pins the current, lenient, behavior; a malformed payload is NOT rejected the way other codecs reject it
            assertThat(boolean_1(new byte[]{b})).isFalse();
        }

        @DisplayName("boolean_1(byte[]) fails on an empty array")
        @Test
        void __decodeEmpty() {
            assertShortPayloadFails(() -> boolean_1(new byte[0]));
        }
    }

    // ------------------------------------------------------------------------------------------------------------ byte
    @DisplayName("byte_1")
    @Nested
    class Byte_1_Test {

        @DisplayName("byte_1(byte) encodes the byte itself")
        @ValueSource(bytes = {Byte.MIN_VALUE, -1, 0, 1, Byte.MAX_VALUE})
        @ParameterizedTest
        void __encode(final byte v) {
            assertThat(byte_1(v)).containsExactly(v);
        }

        @DisplayName("byte_1(byte[]) reads back what byte_1(byte) wrote")
        @ValueSource(bytes = {Byte.MIN_VALUE, -1, 0, 1, Byte.MAX_VALUE})
        @ParameterizedTest
        void __roundTrip(final byte v) {
            assertThat(byte_1(byte_1(v))).isEqualTo(v);
        }

        @DisplayName("byte_1(byte[]) fails on an empty array")
        @Test
        void __decodeShort() {
            assertShortPayloadFails(() -> byte_1(new byte[0]));
        }
    }

    // ----------------------------------------------------------------------------------------------------------- short
    @DisplayName("short_2")
    @Nested
    class Short_2_Test {

        @DisplayName("short_2(short) encodes two big-endian bytes")
        @ValueSource(shorts = {Short.MIN_VALUE, -1, 0, 1, 0x0102, Short.MAX_VALUE})
        @ParameterizedTest
        void __encode(final short v) {
            assertThat(short_2(v)).isEqualTo(buffer(Short.BYTES, b -> b.putShort(v)));
        }

        @DisplayName("short_2(byte[]) reads back what short_2(short) wrote")
        @ValueSource(shorts = {Short.MIN_VALUE, -1, 0, 1, 0x0102, Short.MAX_VALUE})
        @ParameterizedTest
        void __roundTrip(final short v) {
            assertThat(short_2(short_2(v))).isEqualTo(v);
        }

        @DisplayName("short_2(byte[]) fails on fewer than two bytes")
        @Test
        void __decodeShort() {
            assertShortPayloadFails(() -> short_2(new byte[1]));
        }
    }

    // ------------------------------------------------------------------------------------------------------------- int
    @DisplayName("int_4")
    @Nested
    class Int_4_Test {

        @DisplayName("int_4(int) encodes four big-endian bytes")
        @ValueSource(ints = {Integer.MIN_VALUE, -1, 0, 1, 0x01020304, Integer.MAX_VALUE})
        @ParameterizedTest
        void __encode(final int v) {
            assertThat(int_4(v)).isEqualTo(buffer(Integer.BYTES, b -> b.putInt(v)));
        }

        @DisplayName("int_4(byte[]) reads back what int_4(int) wrote")
        @ValueSource(ints = {Integer.MIN_VALUE, -1, 0, 1, 0x01020304, Integer.MAX_VALUE})
        @ParameterizedTest
        void __roundTrip(final int v) {
            assertThat(int_4(int_4(v))).isEqualTo(v);
        }

        @DisplayName("int_4(byte[]) fails on fewer than four bytes")
        @Test
        void __decodeShort() {
            assertShortPayloadFails(() -> int_4(new byte[Integer.BYTES - 1]));
        }
    }

    // ------------------------------------------------------------------------------------------------------------ long
    @DisplayName("long_8")
    @Nested
    class Long_8_Test {

        @DisplayName("long_8(long) encodes eight big-endian bytes")
        @ValueSource(longs = {Long.MIN_VALUE, -1L, 0L, 1L, 0x0102030405060708L, Long.MAX_VALUE})
        @ParameterizedTest
        void __encode(final long v) {
            assertThat(long_8(v)).isEqualTo(buffer(Long.BYTES, b -> b.putLong(v)));
        }

        @DisplayName("long_8(byte[]) reads back what long_8(long) wrote")
        @ValueSource(longs = {Long.MIN_VALUE, -1L, 0L, 1L, 0x0102030405060708L, Long.MAX_VALUE})
        @ParameterizedTest
        void __roundTrip(final long v) {
            assertThat(long_8(long_8(v))).isEqualTo(v);
        }

        @DisplayName("long_8(byte[]) fails on fewer than eight bytes")
        @Test
        void __decodeShort() {
            assertShortPayloadFails(() -> long_8(new byte[Long.BYTES - 1]));
        }
    }

    // ------------------------------------------------------------------------------------------------------------ char
    @DisplayName("char_2")
    @Nested
    class Char_2_Test {

        @DisplayName("char_2(char) encodes the UTF-16 code unit as two big-endian bytes")
        @ValueSource(chars = {'\u0000', 'A', '\u0080', 'ä', '한', '\uD800', '￿'})
        @ParameterizedTest
        void __encode(final char v) {
            assertThat(char_2(v)).isEqualTo(buffer(Character.BYTES, b -> b.putChar(v)));
        }

        @DisplayName("char_2(byte[]) reads back what char_2(char) wrote")
        @ValueSource(chars = {'\u0000', 'A', '\u0080', 'ä', '한', '\uD800', '￿'})
        @ParameterizedTest
        void __roundTrip(final char v) {
            assertThat(char_2(char_2(v))).isEqualTo(v);
        }

        @DisplayName("char_2(byte[]) fails on fewer than two bytes")
        @Test
        void __decodeShort() {
            assertShortPayloadFails(() -> char_2(new byte[1]));
        }
    }

    // ----------------------------------------------------------------------------------------------------------- float
    @DisplayName("float_4")
    @Nested
    class Float_4_Test {

        @DisplayName("float_4(float) encodes the raw IEEE 754 bits as four big-endian bytes")
        @ValueSource(floats = {-0.0f, 0.0f, 1.5f, Float.MIN_VALUE, Float.MAX_VALUE, Float.NEGATIVE_INFINITY,
                Float.POSITIVE_INFINITY, Float.NaN})
        @ParameterizedTest
        void __encode(final float v) {
            assertThat(float_4(v)).isEqualTo(buffer(Float.BYTES, b -> b.putInt(Float.floatToRawIntBits(v))));
        }

        @DisplayName("float_4(byte[]) reads back the same bits float_4(float) wrote")
        @ValueSource(floats = {-0.0f, 0.0f, 1.5f, Float.MIN_VALUE, Float.MAX_VALUE, Float.NEGATIVE_INFINITY,
                Float.POSITIVE_INFINITY, Float.NaN})
        @ParameterizedTest
        void __roundTrip(final float v) {
            // bits, not ==: -0.0f == 0.0f, and NaN != NaN
            assertThat(Float.floatToRawIntBits(float_4(float_4(v)))).isEqualTo(Float.floatToRawIntBits(v));
        }

        @DisplayName("float_4(byte[]) fails on fewer than four bytes")
        @Test
        void __decodeShort() {
            assertShortPayloadFails(() -> float_4(new byte[Float.BYTES - 1]));
        }
    }

    // ---------------------------------------------------------------------------------------------------------- double
    @DisplayName("double_8")
    @Nested
    class Double_8_Test {

        @DisplayName("double_8(double) encodes the raw IEEE 754 bits as eight big-endian bytes")
        @ValueSource(doubles = {-0.0d, 0.0d, 1.5d, Double.MIN_VALUE, Double.MAX_VALUE, Double.NEGATIVE_INFINITY,
                Double.POSITIVE_INFINITY, Double.NaN})
        @ParameterizedTest
        void __encode(final double v) {
            assertThat(double_8(v)).isEqualTo(buffer(Double.BYTES, b -> b.putLong(Double.doubleToRawLongBits(v))));
        }

        @DisplayName("double_8(byte[]) reads back the same bits double_8(double) wrote")
        @ValueSource(doubles = {-0.0d, 0.0d, 1.5d, Double.MIN_VALUE, Double.MAX_VALUE, Double.NEGATIVE_INFINITY,
                Double.POSITIVE_INFINITY, Double.NaN})
        @ParameterizedTest
        void __roundTrip(final double v) {
            // bits, not ==: -0.0d == 0.0d, and NaN != NaN
            assertThat(Double.doubleToRawLongBits(double_8(double_8(v)))).isEqualTo(Double.doubleToRawLongBits(v));
        }

        @DisplayName("double_8(byte[]) fails on fewer than eight bytes")
        @Test
        void __decodeShort() {
            assertShortPayloadFails(() -> double_8(new byte[Double.BYTES - 1]));
        }
    }

    // ------------------------------------------------------------------------------------------------ java.lang.String
    @DisplayName("string_")
    @Nested
    class String__Test {

        @DisplayName("string_(String) encodes UTF-8")
        @ValueSource(strings = {"", "a", "jin", "ä", "한글", "😀"})
        @ParameterizedTest
        void __encode(final String v) {
            assertThat(string_(v)).isEqualTo(v.getBytes(StandardCharsets.UTF_8));
        }

        @DisplayName("string_(byte[]) reads back what string_(String) wrote")
        @ValueSource(strings = {"", "a", "jin", "ä", "한글", "😀"})
        @ParameterizedTest
        void __roundTrip(final String v) {
            assertThat(string_(string_(v))).isEqualTo(v);
        }

        @DisplayName("string_ does NOT round-trip an unpaired surrogate; it comes back as '?'")
        @ValueSource(strings = {"\uD800", "\uDC00", "a\uD800b"})
        @ParameterizedTest
        void __unpairedSurrogateIsLost(final String v) {
            // pins the current behavior: UTF-8 cannot represent a lone surrogate, and getBytes replaces it silently
            assertThat(string_(string_(v))).isNotEqualTo(v).contains("?");
        }
    }

    // ------------------------------------------------------------------------------------------------------------ UUID
    @DisplayName("uuid_16")
    @Nested
    class Uuid_16_Test {

        static Stream<UUID> values() {
            return Stream.of(
                    new UUID(0L, 0L),
                    new UUID(-1L, -1L),
                    new UUID(0x0102030405060708L, 0x090a0b0c0d0e0f10L),
                    UUID.randomUUID()
            );
        }

        @DisplayName("uuid_16(UUID) encodes the most, then the least, significant bits, big-endian")
        @MethodSource("values")
        @ParameterizedTest
        void __encode(final UUID v) {
            assertThat(uuid_16(v)).isEqualTo(buffer(
                    Long.BYTES << 1,
                    b -> b.putLong(v.getMostSignificantBits()).putLong(v.getLeastSignificantBits())
            ));
        }

        @DisplayName("uuid_16(byte[]) reads back what uuid_16(UUID) wrote")
        @MethodSource("values")
        @ParameterizedTest
        void __roundTrip(final UUID v) {
            assertThat(uuid_16(uuid_16(v))).isEqualTo(v);
        }

        @DisplayName("uuid_16(byte[]) fails on fewer than sixteen bytes")
        @Test
        void __decodeShort() {
            assertShortPayloadFails(() -> uuid_16(new byte[(Long.BYTES << 1) - 1]));
        }
    }

    // ------------------------------------------------------------------------------------------- java.math.BigInteger
    @DisplayName("big_integer_")
    @Nested
    class Big_integer__Test {

        static Stream<BigInteger> values() {
            return Stream.of(
                    BigInteger.ZERO,
                    BigInteger.ONE,
                    BigInteger.ONE.negate(),
                    BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE),
                    BigInteger.valueOf(Long.MIN_VALUE).subtract(BigInteger.ONE),
                    new BigInteger(130, ThreadLocalRandom.current()).negate()
            );
        }

        @DisplayName("big_integer_(BigInteger) encodes the two's-complement, big-endian, minimal byte array")
        @MethodSource("values")
        @ParameterizedTest
        void __encode(final BigInteger v) {
            assertThat(big_integer_(v)).isEqualTo(v.toByteArray());
        }

        @DisplayName("big_integer_(byte[]) reads back what big_integer_(BigInteger) wrote")
        @MethodSource("values")
        @ParameterizedTest
        void __roundTrip(final BigInteger v) {
            assertThat(big_integer_(big_integer_(v))).isEqualTo(v);
        }

        @DisplayName("big_integer_(byte[]) fails on an empty array")
        @Test
        void __decodeEmpty() {
            assertThatThrownBy(() -> big_integer_(new byte[0])).isInstanceOf(NumberFormatException.class);
        }
    }

    // ------------------------------------------------------------------------------------------- java.math.BigDecimal
    @DisplayName("big_decimal_")
    @Nested
    class Big_decimal__Test {

        static Stream<BigDecimal> values() {
            return Stream.of(
                    BigDecimal.ZERO,
                    new BigDecimal("1.5"),
                    new BigDecimal("-1.50"),     // the scale is part of the value
                    new BigDecimal("1E+10"),     // a negative scale
                    new BigDecimal("0.000000000000000000000000000001"),
                    new BigDecimal(new BigInteger(130, ThreadLocalRandom.current()), 7)
            );
        }

        @DisplayName("big_decimal_(BigDecimal) encodes the scale as int_4, then the unscaled value as big_integer_")
        @MethodSource("values")
        @ParameterizedTest
        void __encode(final BigDecimal v) {
            final var unscaled = v.unscaledValue().toByteArray();
            assertThat(big_decimal_(v)).isEqualTo(buffer(
                    Integer.BYTES + unscaled.length,
                    b -> b.putInt(v.scale()).put(unscaled)
            ));
        }

        @DisplayName("big_decimal_(byte[]) reads back what big_decimal_(BigDecimal) wrote, scale included")
        @MethodSource("values")
        @ParameterizedTest
        void __roundTrip(final BigDecimal v) {
            // isEqualTo, not isEqualByComparingTo: 1.50 and 1.5 must not be confused
            assertThat(big_decimal_(big_decimal_(v))).isEqualTo(v);
        }

        @DisplayName("big_decimal_(byte[]) fails on fewer than four bytes")
        @Test
        void __decodeShort() {
            assertShortPayloadFails(() -> big_decimal_(new byte[Integer.BYTES - 1]));
        }

        @DisplayName("big_decimal_(byte[]) fails on a scale without an unscaled value")
        @Test
        void __decodeScaleOnly() {
            assertThatThrownBy(() -> big_decimal_(new byte[Integer.BYTES])).isInstanceOf(NumberFormatException.class);
        }
    }

    // ----------------------------------------------------------------------------------------- java.time.LocalDate
    @DisplayName("local_date_8")
    @Nested
    class Local_date_8_Test {

        static Stream<LocalDate> values() {
            return Stream.of(LocalDate.MIN, LocalDate.EPOCH, LocalDate.of(1969, 12, 31), LocalDate.of(2026, 10, 9),
                             LocalDate.MAX);
        }

        @DisplayName("local_date_8(LocalDate) encodes the epoch day as long_8")
        @MethodSource("values")
        @ParameterizedTest
        void __encode(final LocalDate v) {
            assertThat(local_date_8(v)).isEqualTo(buffer(Long.BYTES, b -> b.putLong(v.toEpochDay())));
        }

        @DisplayName("local_date_8(byte[]) reads back what local_date_8(LocalDate) wrote")
        @MethodSource("values")
        @ParameterizedTest
        void __roundTrip(final LocalDate v) {
            assertThat(local_date_8(local_date_8(v))).isEqualTo(v);
        }

        @DisplayName("local_date_8(byte[]) fails on fewer than eight bytes")
        @Test
        void __decodeShort() {
            assertShortPayloadFails(() -> local_date_8(new byte[Long.BYTES - 1]));
        }
    }

    // ----------------------------------------------------------------------------------------- java.time.LocalTime
    @DisplayName("local_time_8")
    @Nested
    class Local_time_8_Test {

        static Stream<LocalTime> values() {
            return Stream.of(LocalTime.MIN, LocalTime.NOON, LocalTime.of(1, 2, 3, 456_789_012), LocalTime.MAX);
        }

        @DisplayName("local_time_8(LocalTime) encodes the nano of day as long_8")
        @MethodSource("values")
        @ParameterizedTest
        void __encode(final LocalTime v) {
            assertThat(local_time_8(v)).isEqualTo(buffer(Long.BYTES, b -> b.putLong(v.toNanoOfDay())));
        }

        @DisplayName("local_time_8(byte[]) reads back what local_time_8(LocalTime) wrote, nanos included")
        @MethodSource("values")
        @ParameterizedTest
        void __roundTrip(final LocalTime v) {
            assertThat(local_time_8(local_time_8(v))).isEqualTo(v);
        }

        @DisplayName("local_time_8(byte[]) fails on fewer than eight bytes")
        @Test
        void __decodeShort() {
            assertShortPayloadFails(() -> local_time_8(new byte[Long.BYTES - 1]));
        }
    }

    // ------------------------------------------------------------------------------------- java.time.LocalDateTime
    @DisplayName("local_date_time_16")
    @Nested
    class Local_date_time_16_Test {

        static Stream<LocalDateTime> values() {
            return Stream.of(LocalDateTime.MIN, LocalDateTime.of(1969, 12, 31, 23, 59, 59, 999_999_999),
                             LocalDateTime.of(2026, 10, 9, 11, 36, 58, 123_456_789), LocalDateTime.MAX);
        }

        @DisplayName("local_date_time_16(LocalDateTime) encodes local_date_8, then local_time_8")
        @MethodSource("values")
        @ParameterizedTest
        void __encode(final LocalDateTime v) {
            assertThat(local_date_time_16(v)).isEqualTo(buffer(
                    Long.BYTES << 1,
                    b -> b.putLong(v.toLocalDate().toEpochDay()).putLong(v.toLocalTime().toNanoOfDay())
            ));
        }

        @DisplayName("local_date_time_16(byte[]) reads back what local_date_time_16(LocalDateTime) wrote")
        @MethodSource("values")
        @ParameterizedTest
        void __roundTrip(final LocalDateTime v) {
            assertThat(local_date_time_16(local_date_time_16(v))).isEqualTo(v);
        }

        @DisplayName("local_date_time_16(byte[]) fails on fewer than sixteen bytes")
        @Test
        void __decodeShort() {
            assertShortPayloadFails(() -> local_date_time_16(new byte[(Long.BYTES << 1) - 1]));
        }
    }

    // ---------------------------------------------------------------------------------------- java.time.ZoneOffset
    @DisplayName("offset_4")
    @Nested
    class Offset_4_Test {

        static Stream<ZoneOffset> values() {
            return Stream.of(ZoneOffset.MIN, ZoneOffset.ofHoursMinutes(-3, -30), ZoneOffset.UTC,
                             ZoneOffset.ofHours(9), ZoneOffset.ofHoursMinutesSeconds(5, 45, 30), ZoneOffset.MAX);
        }

        @DisplayName("offset_4(ZoneOffset) encodes the total seconds as int_4")
        @MethodSource("values")
        @ParameterizedTest
        void __encode(final ZoneOffset v) {
            assertThat(offset_4(v)).isEqualTo(buffer(Integer.BYTES, b -> b.putInt(v.getTotalSeconds())));
        }

        @DisplayName("offset_4(byte[]) reads back what offset_4(ZoneOffset) wrote")
        @MethodSource("values")
        @ParameterizedTest
        void __roundTrip(final ZoneOffset v) {
            assertThat(offset_4(offset_4(v))).isEqualTo(v);
        }

        @DisplayName("offset_4(byte[]) fails on fewer than four bytes")
        @Test
        void __decodeShort() {
            assertShortPayloadFails(() -> offset_4(new byte[Integer.BYTES - 1]));
        }

        @DisplayName("offset_4(byte[]) fails on an offset beyond +/-18:00")
        @Test
        void __decodeOutOfRange() {
            assertThatThrownBy(() -> offset_4(int_4(ZoneOffset.MAX.getTotalSeconds() + 1)))
                    .isInstanceOf(DateTimeException.class);
        }
    }

    // ---------------------------------------------------------------------------------------- java.time.OffsetTime
    @DisplayName("offset_time_12")
    @Nested
    class Offset_time_12_Test {

        static Stream<OffsetTime> values() {
            return Stream.of(OffsetTime.MIN, OffsetTime.of(1, 2, 3, 456_789_012, ZoneOffset.ofHours(9)),
                             OffsetTime.MAX);
        }

        @DisplayName("offset_time_12(OffsetTime) encodes local_time_8, then offset_4")
        @MethodSource("values")
        @ParameterizedTest
        void __encode(final OffsetTime v) {
            assertThat(offset_time_12(v)).isEqualTo(buffer(
                    Long.BYTES + Integer.BYTES,
                    b -> b.putLong(v.toLocalTime().toNanoOfDay()).putInt(v.getOffset().getTotalSeconds())
            ));
        }

        @DisplayName("offset_time_12(byte[]) reads back what offset_time_12(OffsetTime) wrote, offset included")
        @MethodSource("values")
        @ParameterizedTest
        void __roundTrip(final OffsetTime v) {
            // isEqualTo, not isEqualToIgnoringOffset... : the same instant in another offset is a different value
            assertThat(offset_time_12(offset_time_12(v))).isEqualTo(v);
        }

        @DisplayName("offset_time_12(byte[]) fails on fewer than twelve bytes")
        @Test
        void __decodeShort() {
            assertShortPayloadFails(() -> offset_time_12(new byte[Long.BYTES + Integer.BYTES - 1]));
        }
    }

    // ------------------------------------------------------------------------------------ java.time.OffsetDateTime
    @DisplayName("offset_date_time_20")
    @Nested
    class Offset_date_time_20_Test {

        static Stream<OffsetDateTime> values() {
            return Stream.of(OffsetDateTime.MIN,
                             OffsetDateTime.of(2026, 10, 9, 11, 36, 58, 123_456_789, ZoneOffset.ofHours(9)),
                             OffsetDateTime.MAX);
        }

        @DisplayName("offset_date_time_20(OffsetDateTime) encodes local_date_time_16, then offset_4")
        @MethodSource("values")
        @ParameterizedTest
        void __encode(final OffsetDateTime v) {
            assertThat(offset_date_time_20(v)).isEqualTo(buffer(
                    (Long.BYTES << 1) + Integer.BYTES,
                    b -> b.putLong(v.toLocalDate().toEpochDay())
                            .putLong(v.toLocalTime().toNanoOfDay())
                            .putInt(v.getOffset().getTotalSeconds())
            ));
        }

        @DisplayName("offset_date_time_20(byte[]) reads back what offset_date_time_20(OffsetDateTime) wrote")
        @MethodSource("values")
        @ParameterizedTest
        void __roundTrip(final OffsetDateTime v) {
            assertThat(offset_date_time_20(offset_date_time_20(v))).isEqualTo(v);
        }

        @DisplayName("offset_date_time_20(byte[]) fails on fewer than twenty bytes")
        @Test
        void __decodeShort() {
            assertShortPayloadFails(() -> offset_date_time_20(new byte[(Long.BYTES << 1) + Integer.BYTES - 1]));
        }
    }

    // ------------------------------------------------------------------------------------------- java.time.Instant
    @DisplayName("instant_12")
    @Nested
    class Instant_12_Test {

        static Stream<Instant> values() {
            return Stream.of(Instant.MIN, Instant.ofEpochSecond(-1L, 999_999_999), Instant.EPOCH,
                             Instant.ofEpochSecond(1_791_000_000L, 123_456_789), Instant.MAX);
        }

        @DisplayName("instant_12(Instant) encodes the epoch second as long_8, then the nano as int_4")
        @MethodSource("values")
        @ParameterizedTest
        void __encode(final Instant v) {
            assertThat(instant_12(v)).isEqualTo(buffer(
                    Long.BYTES + Integer.BYTES,
                    b -> b.putLong(v.getEpochSecond()).putInt(v.getNano())
            ));
        }

        @DisplayName("instant_12(byte[]) reads back what instant_12(Instant) wrote, nanos included")
        @MethodSource("values")
        @ParameterizedTest
        void __roundTrip(final Instant v) {
            assertThat(instant_12(instant_12(v))).isEqualTo(v);
        }

        @DisplayName("instant_12(byte[]) fails on fewer than twelve bytes")
        @Test
        void __decodeShort() {
            assertShortPayloadFails(() -> instant_12(new byte[Long.BYTES + Integer.BYTES - 1]));
        }
    }

    // ---------------------------------------------------------------------------------------------- java.time.Year
    @DisplayName("year_4")
    @Nested
    class Year_4_Test {

        static Stream<Year> values() {
            return Stream.of(Year.of(Year.MIN_VALUE), Year.of(-1), Year.of(0), Year.of(2026), Year.of(Year.MAX_VALUE));
        }

        @DisplayName("year_4(Year) encodes the value as int_4")
        @MethodSource("values")
        @ParameterizedTest
        void __encode(final Year v) {
            assertThat(year_4(v)).isEqualTo(buffer(Integer.BYTES, b -> b.putInt(v.getValue())));
        }

        @DisplayName("year_4(byte[]) reads back what year_4(Year) wrote")
        @MethodSource("values")
        @ParameterizedTest
        void __roundTrip(final Year v) {
            assertThat(year_4(year_4(v))).isEqualTo(v);
        }

        @DisplayName("year_4(byte[]) fails on fewer than four bytes")
        @Test
        void __decodeShort() {
            assertShortPayloadFails(() -> year_4(new byte[Integer.BYTES - 1]));
        }

        @DisplayName("year_4(byte[]) fails on a value beyond Year.MAX_VALUE")
        @Test
        void __decodeOutOfRange() {
            assertThatThrownBy(() -> year_4(int_4(Year.MAX_VALUE + 1))).isInstanceOf(DateTimeException.class);
        }
    }

    // ---------------------------------------------------------------------------------------------- java.util.Date
    @SuppressWarnings({"deprecation"})
    @DisplayName("util_date_8")
    @Nested
    class Util_date_8_Test {

        static Stream<java.util.Date> values() {
            return Stream.of(new java.util.Date(Long.MIN_VALUE), new java.util.Date(-1L), new java.util.Date(0L),
                             new java.util.Date(1_791_000_000_123L), new java.util.Date(Long.MAX_VALUE));
        }

        @DisplayName("util_date_8(Date) encodes the epoch millis as long_8")
        @MethodSource("values")
        @ParameterizedTest
        void __encode(final java.util.Date v) {
            assertThat(util_date_8(v)).isEqualTo(buffer(Long.BYTES, b -> b.putLong(v.getTime())));
        }

        @DisplayName("util_date_8(byte[]) reads back what util_date_8(Date) wrote")
        @MethodSource("values")
        @ParameterizedTest
        void __roundTrip(final java.util.Date v) {
            assertThat(util_date_8(util_date_8(v))).isEqualTo(v);
        }

        @DisplayName("util_date_8(byte[]) fails on fewer than eight bytes")
        @Test
        void __decodeShort() {
            assertShortPayloadFails(() -> util_date_8(new byte[Long.BYTES - 1]));
        }
    }

    // ------------------------------------------------------------------------------------------ java.util.Calendar
    @SuppressWarnings({"deprecation"})
    @DisplayName("util_calendar_8")
    @Nested
    class Util_calendar_8_Test {

        static Stream<Calendar> values() {
            return Stream.of(-1L, 0L, 1_791_000_000_123L).map(millis -> {
                // a zone which is unlikely to be the default, so that losing it is observable
                final var calendar = Calendar.getInstance(TimeZone.getTimeZone("Pacific/Chatham"));
                calendar.setTimeInMillis(millis);
                return calendar;
            });
        }

        @DisplayName("util_calendar_8(Calendar) encodes the epoch millis as long_8")
        @MethodSource("values")
        @ParameterizedTest
        void __encode(final Calendar v) {
            assertThat(util_calendar_8(v)).isEqualTo(buffer(Long.BYTES, b -> b.putLong(v.getTimeInMillis())));
        }

        @DisplayName("util_calendar_8(byte[]) reads back the instant, in the default time zone")
        @MethodSource("values")
        @ParameterizedTest
        void __roundTrip(final Calendar v) {
            final var decoded = util_calendar_8(util_calendar_8(v));
            assertThat(decoded.getTimeInMillis()).isEqualTo(v.getTimeInMillis());
            // pins the current behavior: only the instant is stored; the time zone is the JVM default on the way back
            assertThat(decoded.getTimeZone()).isEqualTo(TimeZone.getDefault());
        }

        @DisplayName("util_calendar_8(byte[]) fails on fewer than eight bytes")
        @Test
        void __decodeShort() {
            assertShortPayloadFails(() -> util_calendar_8(new byte[Long.BYTES - 1]));
        }
    }

    // ----------------------------------------------------------------------------------------------- java.sql.Date
    @SuppressWarnings({"deprecation"})
    @DisplayName("sql_date_8")
    @Nested
    class Sql_date_8_Test {

        static Stream<java.sql.Date> values() {
            return Stream.of(new java.sql.Date(-1L), new java.sql.Date(0L), java.sql.Date.valueOf("2026-10-09"));
        }

        @DisplayName("sql_date_8(java.sql.Date) encodes the epoch millis as long_8")
        @MethodSource("values")
        @ParameterizedTest
        void __encode(final java.sql.Date v) {
            assertThat(sql_date_8(v)).isEqualTo(buffer(Long.BYTES, b -> b.putLong(v.getTime())));
        }

        @DisplayName("sql_date_8(byte[]) reads back what sql_date_8(java.sql.Date) wrote")
        @MethodSource("values")
        @ParameterizedTest
        void __roundTrip(final java.sql.Date v) {
            assertThat(sql_date_8(sql_date_8(v))).isEqualTo(v);
        }

        @DisplayName("sql_date_8(byte[]) fails on fewer than eight bytes")
        @Test
        void __decodeShort() {
            assertShortPayloadFails(() -> sql_date_8(new byte[Long.BYTES - 1]));
        }
    }

    // ----------------------------------------------------------------------------------------------- java.sql.Time
    @SuppressWarnings({"deprecation"})
    @DisplayName("sql_time_8")
    @Nested
    class Sql_time_8_Test {

        static Stream<java.sql.Time> values() {
            return Stream.of(new java.sql.Time(-1L), new java.sql.Time(0L), java.sql.Time.valueOf("11:36:58"));
        }

        @DisplayName("sql_time_8(java.sql.Time) encodes the epoch millis as long_8")
        @MethodSource("values")
        @ParameterizedTest
        void __encode(final java.sql.Time v) {
            assertThat(sql_time_8(v)).isEqualTo(buffer(Long.BYTES, b -> b.putLong(v.getTime())));
        }

        @DisplayName("sql_time_8(byte[]) reads back what sql_time_8(java.sql.Time) wrote")
        @MethodSource("values")
        @ParameterizedTest
        void __roundTrip(final java.sql.Time v) {
            assertThat(sql_time_8(sql_time_8(v))).isEqualTo(v);
        }

        @DisplayName("sql_time_8(byte[]) fails on fewer than eight bytes")
        @Test
        void __decodeShort() {
            assertShortPayloadFails(() -> sql_time_8(new byte[Long.BYTES - 1]));
        }
    }

    // ------------------------------------------------------------------------------------------ java.sql.Timestamp
    @SuppressWarnings({"deprecation"})
    @DisplayName("sql_timestamp_16")
    @Nested
    class Sql_timestamp_16_Test {

        static Stream<java.sql.Timestamp> values() {
            final var preEpoch = new java.sql.Timestamp(-1_000L);
            preEpoch.setNanos(123_456_789);
            final var withNanos = java.sql.Timestamp.valueOf("2026-10-09 11:36:58.123456789");
            return Stream.of(preEpoch, new java.sql.Timestamp(0L), withNanos);
        }

        @DisplayName("sql_timestamp_16(Timestamp) encodes instant_12, then four zero bytes")
        @MethodSource("values")
        @ParameterizedTest
        void __encode(final java.sql.Timestamp v) {
            final var instant = v.toInstant();
            // pins the current format: sixteen bytes are allocated, and instant_12 fills only the first twelve
            assertThat(sql_timestamp_16(v)).isEqualTo(buffer(
                    16,
                    b -> b.putLong(instant.getEpochSecond()).putInt(instant.getNano()).putInt(0)
            ));
        }

        @DisplayName("sql_timestamp_16(byte[]) reads back what sql_timestamp_16(Timestamp) wrote, nanos included")
        @MethodSource("values")
        @ParameterizedTest
        void __roundTrip(final java.sql.Timestamp v) {
            // Timestamp.equals(Timestamp) compares the nanos as well
            assertThat(sql_timestamp_16(sql_timestamp_16(v))).isEqualTo(v);
        }

        @DisplayName("sql_timestamp_16(byte[]) fails on fewer than sixteen bytes")
        @Test
        void __decodeShort() {
            assertShortPayloadFails(() -> sql_timestamp_16(new byte[15]));
        }
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

    // ---------------------------------------------------------------------------------------------------------- char[]
    @DisplayName("chars_2l")
    @Nested
    class Chars_2l_Test {

        static Stream<char[]> values() {
            return Stream.of(new char[0], new char[]{'j'}, "jinä한￿\u0080\uD800".toCharArray());
        }

        @DisplayName("chars_2l(char[]) encodes each UTF-16 code unit as two big-endian bytes")
        @MethodSource("values")
        @ParameterizedTest
        void __encode(final char[] v) {
            assertThat(chars_2l(v)).isEqualTo(buffer(v.length << 1, b -> {
                for (final char c : v) {
                    b.putChar(c);
                }
            }));
        }

        @DisplayName("chars_2l(byte[]) reads back what chars_2l(char[]) wrote, unpaired surrogates included")
        @MethodSource("values")
        @ParameterizedTest
        void __roundTrip(final char[] v) {
            assertThat(chars_2l(chars_2l(v))).isEqualTo(v);
        }

        @DisplayName("an odd number of bytes is rejected, not silently truncated")
        @Test
        void __decodeOdd() {
            assertThatThrownBy(() -> chars_2l(new byte[]{1, 2, 3}))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("odd number of bytes");
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
            assertThat(b).as("the same format as chars_2l").isEqualTo(chars_2l("jinä한￿\u0080".toCharArray()));
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
                    .hasMessageContaining("odd number of bytes");
        }
    }

    // ------------------------------------------------------------------------------------------------------------ enum
    @DisplayName("enum_")
    @Nested
    class Enum__Test {

        enum Grade {
            A,
            B,
            ÉLÈVE // a name which is not ASCII
        }

        @DisplayName("enum_(Enum) encodes the name, NOT the ordinal, as string_")
        @EnumSource(Grade.class)
        @ParameterizedTest
        void __encode(final Grade v) {
            assertThat(enum_(v)).isEqualTo(v.name().getBytes(StandardCharsets.UTF_8));
        }

        @DisplayName("enum_(byte[], Class) reads back what enum_(Enum) wrote")
        @EnumSource(Grade.class)
        @ParameterizedTest
        void __roundTrip(final Grade v) {
            assertThat(enum_(enum_(v), Grade.class)).isSameAs(v);
        }

        @DisplayName("enum_(byte[], Class) fails on a name which is not a constant")
        @Test
        void __decodeUnknown() {
            assertThatThrownBy(() -> enum_(string_("C"), Grade.class)).isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("enum_(byte[], Class) fails on an empty array")
        @Test
        void __decodeEmpty() {
            // AssertionError with -ea; Enum.valueOf("") without it
            assertThatThrownBy(() -> enum_(new byte[0], Grade.class))
                    .isInstanceOfAny(AssertionError.class, IllegalArgumentException.class);
        }
    }

    // ---------------------------------------------------------------------------------------------------- Serializable
    @DisplayName("serializable_")
    @Nested
    class Serializable__Test {

        /**
         * A value with a nested object graph, for verifying that the filter pins the root type without rejecting what
         * the root holds.
         */
        static class Holder implements java.io.Serializable {

            private static final long serialVersionUID = 1L;

            Holder(final String name, final BigDecimal amount) {
                this.name = name;
                this.amount = amount;
            }

            final String name;

            final BigDecimal amount;
        }

        @DisplayName("serializable_(byte[], Class) reads back what serializable_(Serializable) wrote")
        @Test
        void __roundTrip() {
            final var decoded = (Holder) serializable_(serializable_(new Holder("jane", BigDecimal.ONE)), Holder.class);
            assertThat(decoded.name).isEqualTo("jane");
            assertThat(decoded.amount).isEqualTo(BigDecimal.ONE);
        }

        @DisplayName("serializable_ reads back a value which serializes through a proxy")
        @Test
        void __roundTripProxy() {
            // every java.time type writes a java.time.Ser as the root; the filter must not pin the root class
            final var v = LocalDate.of(2026, 10, 9);
            assertThat(serializable_(serializable_(v), LocalDate.class)).isEqualTo(v);
        }

        @DisplayName("serializable_ rejects a graph whose root is not of the expected type")
        @Test
        void __decodeWrongRoot() {
            final var encoded = serializable_(new Holder("jane", BigDecimal.ONE));
            // the cast enforces the type; the filter only caps resources, so that serialization proxies still work
            assertThatThrownBy(() -> serializable_(encoded, BigDecimal.class))
                    .isInstanceOf(ClassCastException.class);
        }

        @DisplayName("serializable_ refuses to WRITE what it could never read back")
        @Test
        void __encodeOversized() {
            // a value larger than the reader's stream cap used to encrypt and store happily, and then fail
            // every subsequent read -- with the plaintext already cleared, so the row was unrecoverable
            final var oversized = new byte[(1 << 20) + 1024];
            assertThatThrownBy(() -> serializable_((java.io.Serializable) oversized))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("too large");
        }
    }

    // --------------------------------------------------------------------------------------------------- payload frame
    @DisplayName("payload frame (#75)")
    @Nested
    class Frame_Test {

        @DisplayName("codec ids are stored data: pinned, and unique")
        @Test
        void __codecIdsPinned() {
            final var codecs = EntityEncryptionServiceUtils.Codec.values();
            for (int i = 0; i < codecs.length; i++) {
                // appending a codec is fine; renumbering or reusing an id is a format break
                assertThat(codecs[i].id).as("id of %s", codecs[i]).isEqualTo((byte) (i + 1));
                assertThat(EntityEncryptionServiceUtils.Codec.of(codecs[i].id)).isSameAs(codecs[i]);
            }
            assertThat(EntityEncryptionServiceUtils.Codec.of((byte) 0)).isNull();
            assertThat(EntityEncryptionServiceUtils.Codec.of(Byte.MAX_VALUE)).isNull();
        }

        @DisplayName("codecOf(Class) follows the ladders: java.sql before java.util.Date, Serializable last")
        @Test
        void __codecOf() {
            assertThat(EntityEncryptionServiceUtils.codecOf(Integer.class))
                    .isSameAs(EntityEncryptionServiceUtils.Codec.INT_4);
            assertThat(EntityEncryptionServiceUtils.codecOf(int.class))
                    .isSameAs(EntityEncryptionServiceUtils.Codec.INT_4);
            assertThat(EntityEncryptionServiceUtils.codecOf(java.sql.Timestamp.class))
                    .isSameAs(EntityEncryptionServiceUtils.Codec.SQL_TIMESTAMP_16);
            assertThat(EntityEncryptionServiceUtils.codecOf(java.util.Date.class))
                    .isSameAs(EntityEncryptionServiceUtils.Codec.UTIL_DATE_8);
            assertThat(EntityEncryptionServiceUtils.codecOf(java.util.GregorianCalendar.class))
                    .isSameAs(EntityEncryptionServiceUtils.Codec.UTIL_CALENDAR_8);
            assertThat(EntityEncryptionServiceUtils.codecOf(java.time.DayOfWeek.class))
                    .as("an enum is an enum before it is Serializable")
                    .isSameAs(EntityEncryptionServiceUtils.Codec.ENUM_);
            assertThat(EntityEncryptionServiceUtils.codecOf(java.util.ArrayList.class))
                    .isSameAs(EntityEncryptionServiceUtils.Codec.SERIALIZABLE_);
            assertThat(EntityEncryptionServiceUtils.codecOf(Object.class)).as("unsupported").isNull();
        }

        @DisplayName("frame(Codec, byte[]) prefixes the format version and the codec id")
        @Test
        void __frame() {
            final var framed = EntityEncryptionServiceUtils.frame(EntityEncryptionServiceUtils.Codec.INT_4,
                                                                  new byte[]{1, 2, 3, 4});
            assertThat(framed).containsExactly(
                    EntityEncryptionServiceUtils.FORMAT_VERSION, EntityEncryptionServiceUtils.Codec.INT_4.id,
                    1, 2, 3, 4);
        }

        @DisplayName("unframe(Codec, byte[]) reads back what frame wrote, empty included")
        @Test
        void __roundTrip() {
            final var codec = EntityEncryptionServiceUtils.Codec.STRING_;
            assertThat(EntityEncryptionServiceUtils.unframe(codec, EntityEncryptionServiceUtils.frame(codec, new byte[0])))
                    .isEmpty();
            final var encoded = randomBytes(1, 128);
            assertThat(EntityEncryptionServiceUtils.unframe(codec, EntityEncryptionServiceUtils.frame(codec, encoded)))
                    .isEqualTo(encoded);
        }

        @DisplayName("unframe(Codec, byte[]) rejects a missing header, another version, and another codec")
        @Test
        void __unframeRejects() {
            final var codec = EntityEncryptionServiceUtils.Codec.INT_4;
            assertThatThrownBy(() -> EntityEncryptionServiceUtils.unframe(codec, new byte[1]))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("no payload header");
            assertThatThrownBy(() -> EntityEncryptionServiceUtils.unframe(codec, new byte[]{0, codec.id}))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("unknown payload format version");
            assertThatThrownBy(() -> EntityEncryptionServiceUtils.unframe(
                    codec, EntityEncryptionServiceUtils.frame(EntityEncryptionServiceUtils.Codec.LONG_8, new byte[8])))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("encoded by LONG_8")
                    .hasMessageContaining("decoded by INT_4");
            assertThatThrownBy(() -> EntityEncryptionServiceUtils.unframe(
                    codec, new byte[]{EntityEncryptionServiceUtils.FORMAT_VERSION, Byte.MAX_VALUE}))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("unknown codec");
        }
    }
}
