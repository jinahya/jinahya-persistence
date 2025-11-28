package com.github.jinahya.persistence.crypto;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.RandomStringGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.ZoneId;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.Bytes_l;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.Characters_2l;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.big_decimal_;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.big_integer_;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.boolean_1;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.char_2;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.chars_2l;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.double_8;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.enum_;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.float_4;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.instant_12;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.int_4;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.local_date_8;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.local_date_time_16;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.local_time_8;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.long_8;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.offset_date_time_20;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.offset_time_12;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.short_2;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.sql_date_8;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.sql_time_8;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.sql_timestamp_16;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.string_;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.util_calendar_8;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.util_date_8;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.uuid_16;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.year_4;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * .
 *
 * @see <a href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#a486>2.6. Basic
 *         Types</a> (Jakarta Persistence 3.2 Specification Document
 */
@Slf4j
class __EncryptionServiceUtils_Test {

    private static byte[] randomBytes(final int minLengthInclusive, final int maxLengthInclusive) {
        assert minLengthInclusive >= 0;
        assert maxLengthInclusive >= minLengthInclusive;
        final var v = new byte[ThreadLocalRandom.current().nextInt(minLengthInclusive, maxLengthInclusive + 1)];
        ThreadLocalRandom.current().nextBytes(v);
        return v;
    }

    private static char[] randomChars(final int minLengthInclusive, final int maxLengthInclusive) {
        assert minLengthInclusive >= 0;
        assert maxLengthInclusive >= minLengthInclusive;
        final var v = new char[ThreadLocalRandom.current().nextInt(minLengthInclusive, maxLengthInclusive + 1)];
        for (int i = 0; i < v.length; i++) {
            v[i] = (char) ThreadLocalRandom.current().nextInt();
        }
        return v;
    }

    private static String randomDigitString(final int minLengthInclusive, final int maxLengthInclusive) {
        assert minLengthInclusive >= 0;
        assert maxLengthInclusive >= minLengthInclusive;
        final int length = ThreadLocalRandom.current().nextInt(minLengthInclusive, maxLengthInclusive + 1);
        final var generator = RandomStringGenerator.builder().withinRange('0', '9').get();
        return generator.generate(length);
    }

    // -----------------------------------------------------------------------------------------------------------------
    __EncryptionServiceUtils_Test() {
        super();
    }

    // -----------------------------------------------------------------------------------------------------------------
    @ValueSource(booleans = {true, false})
    @ParameterizedTest
    void boolean__(final boolean v) {
        final var encoded = boolean_1(v);
        final var decoded = boolean_1(encoded);
        assertThat(decoded).isEqualTo(v);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Test
    void short__() {
        final var v = (short) ThreadLocalRandom.current().nextInt();
        final var encoded = short_2(v);
        final var decoded = short_2(encoded);
        assertThat(decoded).isEqualTo(v);
    }

    @Test
    void int__() {
        final var v = ThreadLocalRandom.current().nextInt();
        final var encoded = int_4(v);
        final var decoded = int_4(encoded);
        assertThat(decoded).isEqualTo(v);
    }

    @Test
    void long__() {
        final var v = ThreadLocalRandom.current().nextLong();
        final var encoded = long_8(v);
        final var decoded = long_8(encoded);
        assertThat(decoded).isEqualTo(v);
    }

    @Test
    void char__() {
        final var v = (char) Double.longBitsToDouble(ThreadLocalRandom.current().nextInt());
        final var encoded = char_2(v);
        final var decoded = char_2(encoded);
        assertThat(decoded).isEqualTo(v);
    }

    @Test
    void float__() {
        final var v = Float.intBitsToFloat(ThreadLocalRandom.current().nextInt());
        final var encoded = float_4(v);
        final var decoded = float_4(encoded);
        assertThat(decoded).isEqualTo(v);
    }

    @Test
    void double__() {
        final var v = Double.longBitsToDouble(ThreadLocalRandom.current().nextLong());
        final var encoded = double_8(v);
        final var decoded = double_8(encoded);
        assertThat(decoded).isEqualTo(v);
    }

    // ------------------------------------------------------------------------------------------------ java.lang.String
    @Test
    void string__() {
        final var v = new RandomStringGenerator.Builder().get().generate(ThreadLocalRandom.current().nextInt(128));
        final var encoded = string_(v);
        final var decoded = string_(encoded);
        assertThat(decoded).isEqualTo(v);
    }

    // -------------------------------------------------------------------------------------------------- java.util.UUID
    @Test
    void uuid__() {
        final var v = UUID.randomUUID();
        final var encoded = uuid_16(v);
        final var decoded = uuid_16(encoded);
        assertThat(decoded).isEqualTo(v);
    }

    // ------------------------------------------------------------------------------------------------------- java.math
    @Test
    void big_integer__() {
        final var v = new BigInteger(randomBytes(1, 128));
        final var encoded = big_integer_(v);
        final var decoded = big_integer_(encoded);
        assertThat(decoded).isEqualTo(v);
    }

    @Test
    void big_decimal__() {
        final var v = new BigDecimal(randomDigitString(0, 128) + '.' + randomDigitString(0, 128));
        final var encoded = big_decimal_(v);
        final var decoded = big_decimal_(encoded);
        assertThat(decoded).isEqualTo(v);
    }

    // ------------------------------------------------------------------------------------------------------- java.time
    @Test
    void local_date__() {
        final var v = LocalDate.now();
        final var encoded = local_date_8(v);
        final var decoded = local_date_8(encoded);
        assertThat(decoded).isEqualTo(v);
    }

    @Test
    void local_time__() {
        final var v = LocalTime.now();
        final var encoded = local_time_8(v);
        final var decoded = local_time_8(encoded);
        assertThat(decoded).isEqualTo(v);
    }

    @Test
    void local_date_time__() {
        final var v = LocalDateTime.now();
        final var encoded = local_date_time_16(v);
        final var decoded = local_date_time_16(encoded);
        assertThat(decoded).isEqualTo(v);
    }

    @Test
    void offset___() {
        ZoneId.getAvailableZoneIds().stream()
                .map(ZoneId::of)
                .peek(v -> log.debug("zone: {} ({}), rules: {}", v, v.getId(), v.getRules()))
                .map(v -> v.getRules().getOffset(Instant.now()))
//                .sorted(Comparator.comparingInt(ZoneOffset::getTotalSeconds))
                .peek(v -> log.debug("offset: {} ({})", v, v.getTotalSeconds()))
                .forEach(v -> {
                    final var encoded = __EncryptionServiceUtils.offset_4(v);
                    final var decoded = __EncryptionServiceUtils.offset_4(encoded);
                    assertThat(decoded).isEqualTo(v);
                });
    }

    @Test
    void offset_time__() {
        final var v = OffsetTime.now();
        final var encoded = offset_time_12(v);
        final var decoded = offset_time_12(encoded);
        assertThat(decoded).isEqualTo(v);
    }

    @Test
    void offset_date_time__() {
        final var v = OffsetDateTime.now();
        final var encoded = offset_date_time_20(v);
        final var decoded = offset_date_time_20(encoded);
        assertThat(decoded).isEqualTo(v);
    }

    @Test
    void instant__() {
        final var v = Instant.now();
        final var encoded = __EncryptionServiceUtils.instant_12(v);
        final var decoded = instant_12(encoded);
        assertThat(decoded).isEqualTo(v);
    }

    @Test
    void year__() {
        final var v = Year.now();
        final var encoded = year_4(v);
        final var decoded = year_4(encoded);
        assertThat(decoded).isEqualTo(v);
    }

    // ------------------------------------------------------------------------------------------------------- java.util
    @Test
    void util_date__() {
        final var v = new java.util.Date();
        final var encoded = util_date_8(v);
        final var decoded = util_date_8(encoded);
        assertThat(decoded).isEqualTo(v);
    }

    @Test
    void util_calendar__() {
        {
            final var v = java.util.Calendar.getInstance();
            final var encoded = util_calendar_8(v);
            final var decoded = util_calendar_8(encoded);
            assertThat(decoded).isEqualTo(v);
        }
        {
            final var v = java.util.GregorianCalendar.getInstance();
            final var encoded = util_calendar_8(v);
            final var decoded = util_calendar_8(encoded);
            assertThat(decoded).isEqualTo(v);
        }
    }

    // -------------------------------------------------------------------------------------------------------- java.sql
    @Test
    void sql_date__() {
        final var v = new java.sql.Date(new java.util.Date().getTime());
        final var encoded = sql_date_8(v);
        final var decoded = sql_date_8(encoded);
        assertThat(decoded).isEqualTo(v);
    }

    @Test
    void sql_time__() {
        final var v = new java.sql.Time(new java.util.Date().getTime());
        final var encoded = sql_time_8(v);
        final var decoded = sql_time_8(encoded);
        assertThat(decoded).isEqualTo(v);
    }

    @Test
    void sql_timestamp__() {
        final var v = new java.sql.Timestamp(new java.util.Date().getTime());
        final var encoded = sql_timestamp_16(v);
        final var decoded = sql_timestamp_16(encoded);
        assertThat(decoded).isEqualTo(v);
    }

    // ---------------------------------------------------------------------------------------------------------- byte[]

    // ---------------------------------------------------------------------------------------------------------- Byte[]
    @Test
    void Bytes_l__() {
        final Byte[] v;
        {
            final var p = randomBytes(0, 128);
            v = new Byte[p.length];
            for (int i = 0; i < v.length; i++) {
                v[i] = (byte) i;
            }
        }
        final var b = Bytes_l(v);
        assertThat(Bytes_l(b)).isEqualTo(v);
    }

    // ---------------------------------------------------------------------------------------------------------- char[]
    @Test
    void chars_2l__() {
        final var v = randomChars(0, 128);
        final var encoded = chars_2l(v);
        final var decoded = chars_2l(encoded);
        assertThat(decoded).isEqualTo(v);
    }

    // ----------------------------------------------------------------------------------------------------- Character[]
    @Test
    void Characters_l__() {
        final Character[] v;
        {
            final var p = randomChars(0, 128);
            v = new Character[p.length];
            for (int i = 0; i < v.length; i++) {
                v[i] = (char) i;
            }
        }
        final var encoded = Characters_2l(v);
        final var decoded = Characters_2l(encoded);
        assertThat(decoded).isEqualTo(v);
    }

    // ------------------------------------------------------------------------------------------------------------ enum
    private static enum A {
        B,
        C,
    }

    @EnumSource(A.class)
    @ParameterizedTest
    void Enums_l__(final A v) {
        final var encoded = enum_(v);
        final var decoded = enum_(encoded, A.class);
        assertThat(decoded).isSameAs(v);
    }
}
