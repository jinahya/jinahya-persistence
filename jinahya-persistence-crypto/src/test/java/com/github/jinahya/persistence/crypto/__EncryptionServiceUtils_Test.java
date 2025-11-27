package com.github.jinahya.persistence.crypto;

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
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.Bytes_l;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.Characters_2l;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.big_decimal_;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.boolean_1;
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
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.short_2;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.sql_date_8;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.sql_time_8;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.sql_timestamp_16;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.string_;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.util_calendar_8;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.util_date_8;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.uuid_16;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.year_4;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * .
 *
 * @see <a href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#a486>2.6. Basic
 *         Types</a> (Jakarta Persistence 3.2 Specification Document
 */
class __EncryptionServiceUtils_Test {

    private static byte[] randomBytes(final int minLength, final int maxLength) {
        final var v = new byte[ThreadLocalRandom.current().nextInt(minLength, maxLength + 1)];
        ThreadLocalRandom.current().nextBytes(v);
        return v;
    }

    private static char[] randomChars(final int minLength, final int maxLength) {
        final var v = new char[ThreadLocalRandom.current().nextInt(minLength, maxLength + 1)];
        for (int i = 0; i < v.length; i++) {
            v[i] = (char) ThreadLocalRandom.current().nextInt();
        }
        return v;
    }

    private static String randomDigitString(final int minLength, final int maxLength) {
        final int length = ThreadLocalRandom.current().nextInt(minLength, maxLength + 1);
        final var generator = RandomStringGenerator.builder().withinRange('0', '9').build();
        return generator.generate(length);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @ValueSource(booleans = {true, false})
    @ParameterizedTest
    void boolean__(final boolean v) {
        final var b = boolean_1(v);
        assertThat(boolean_1(b)).isEqualTo(v);
    }

    @Test
    void short__() {
        final var v = (short) ThreadLocalRandom.current().nextInt();
        final var b = short_2(v);
        assertThat(short_2(b)).isEqualTo(v);
    }

    @Test
    void int__() {
        final var v = ThreadLocalRandom.current().nextInt();
        final var b = int_4(v);
        assertThat(int_4(b)).isEqualTo(v);
    }

    @Test
    void long__() {
        final var v = ThreadLocalRandom.current().nextLong();
        final var b = long_8(v);
        assertThat(long_8(b)).isEqualTo(v);
    }

    @Test
    void float__() {
        final var v = Float.intBitsToFloat(ThreadLocalRandom.current().nextInt());
        final var b = float_4(v);
        assertThat(float_4(b)).isEqualTo(v);
    }

    @Test
    void double__() {
        final var v = Double.longBitsToDouble(ThreadLocalRandom.current().nextLong());
        final var b = double_8(v);
        assertThat(double_8(b)).isEqualTo(v);
    }

    @Test
    void char__() {
        final var v = (char) Double.longBitsToDouble(ThreadLocalRandom.current().nextInt());
        final var b = char_2(v);
        assertThat(char_2(b)).isEqualTo(v);
    }

    @Test
    void string__() {
        final var v = new RandomStringGenerator.Builder().build().generate(ThreadLocalRandom.current().nextInt(128));
        final var b = string_(v);
        assertThat(string_(b)).isEqualTo(v);
    }

    @Test
    void uuid__() {
        final var v = UUID.randomUUID();
        final var b = uuid_16(v);
        assertThat(uuid_16(b)).isEqualTo(v);
    }

    @Test
    void big_integer__() {
        final var v = new BigInteger(randomBytes(1, 128));
        final var b = v.toByteArray();
        assertThat(new BigInteger(b)).isEqualTo(v);
    }

    @Test
    void big_decimal__() {
        final var v = new BigDecimal(randomDigitString(0, 128) + '.' + randomDigitString(0, 128));
        final var encoded = big_decimal_(v);
        assertThat(big_decimal_(encoded)).isEqualTo(v);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Test
    void local_date__() {
        final var v = LocalDate.now();
        final var b = local_date_8(new byte[8], 0, v);
        assertThat(local_date_8(b, 0)).isEqualTo(v);
    }

    @Test
    void local_time__() {
        final var v = LocalTime.now();
        final var b = local_time_8(v);
        assertThat(local_time_8(b)).isEqualTo(v);
    }

    @Test
    void local_date_time__() {
        final var v = LocalDateTime.now();
        final var b = local_date_time_16(v);
        assertThat(local_date_time_16(b)).isEqualTo(v);
    }

    @Test
    void offset_time__() {
        final var v = OffsetTime.now();
        final var b = offset_time_12(v);
        assertThat(offset_time_12(b)).isEqualTo(v);
    }

    @Test
    void offset_date_time__() {
        final var v = OffsetDateTime.now();
        final var b = offset_date_time_20(v);
        assertThat(offset_date_time_20(b)).isEqualTo(v);
    }

    @Test
    void instant__() {
        final var v = Instant.now();
        final var b = instant_16(v);
        assertThat(instant_16(b)).isEqualTo(v);
    }

    @Test
    void year__() {
        final var v = Year.now();
        final var b = year_4(v);
        assertThat(year_4(b)).isEqualTo(v);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Test
    void util_date__() {
        final var v = new java.util.Date();
        final var b = util_date_8(v);
        assertThat(util_date_8(b)).isEqualTo(v);
    }

    @Test
    void util_calendar__() {
        final var v = java.util.Calendar.getInstance();
        final var b = util_calendar_8(v);
        assertThat(util_calendar_8(b)).isEqualTo(v);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Test
    void sql_date__() {
        final var v = new java.sql.Date(new java.util.Date().getTime());
        final var b = sql_date_8(v);
        assertThat(sql_date_8(b)).isEqualTo(v);
    }

    @Test
    void sql_time__() {
        final var v = new java.sql.Time(new java.util.Date().getTime());
        final var b = sql_time_8(v);
        assertThat(sql_time_8(b)).isEqualTo(v);
    }

    @Test
    void sql_timestamp__() {
        final var v = new java.sql.Timestamp(new java.util.Date().getTime());
        final var b = sql_timestamp_16(v);
        assertThat(sql_timestamp_16(b)).isEqualTo(v);
    }

    // ---------------------------------------------------------------------------------------------------------- byte[]

    // ---------------------------------------------------------------------------------------------------------- Byte[]
    @Test
    void Bytes_l__() {
        final Byte[] v;
        {
            final var p = new byte[ThreadLocalRandom.current().nextInt(128)];
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
        final var v = new char[ThreadLocalRandom.current().nextInt(128)];
        for (int i = 0; i < v.length; i++) {
            v[i] = (char) ThreadLocalRandom.current().nextInt(65536);
        }
        final var b = chars_2l(v);
        final var actual = chars_2l(b);
        assertThat(actual).isEqualTo(v);
    }

    // ----------------------------------------------------------------------------------------------------- Character[]
    @Test
    void Characters_l__() {
        final Character[] v;
        {
            final var p = new char[ThreadLocalRandom.current().nextInt(128)];
            v = new Character[p.length];
            for (int i = 0; i < v.length; i++) {
                v[i] = (char) i;
            }
        }
        final var b = Characters_2l(v);
        assertThat(Characters_2l(b)).isEqualTo(v);
    }

    // ------------------------------------------------------------------------------------------------------------ enum
    private static enum A {
        B,
        C,
    }

    @EnumSource(A.class)
    @ParameterizedTest
    void Enums_l__(final A v) {
        final var b = enum_(v);
        assertThat(enum_(b, A.class)).isSameAs(v);
    }
}
