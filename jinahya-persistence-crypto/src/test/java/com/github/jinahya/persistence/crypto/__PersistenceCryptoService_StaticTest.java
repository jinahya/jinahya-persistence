package com.github.jinahya.persistence.crypto;

import lombok.EqualsAndHashCode;
import org.apache.commons.text.RandomStringGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * .
 *
 * @see <a href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#a486>2.6. Basic
 *         Types</a> (Jakarta Persistence 3.2 Specification Document
 */
class __PersistenceCryptoService_StaticTest {

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
        final var b = ByteBuffer.allocate(1);
        __PersistenceCryptoService.boolean_1(b, v);
        assertThat(__PersistenceCryptoService.boolean_1(b.flip())).isEqualTo(v);
    }

    @Test
    void short__() {
        final var b = ByteBuffer.allocate(Short.BYTES);
        final var v = (short) ThreadLocalRandom.current().nextInt();
        __PersistenceCryptoService.short_2(b, v);
        assertThat(__PersistenceCryptoService.short_2(b.flip())).isEqualTo(v);
    }

    @Test
    void int__() {
        final var b = ByteBuffer.allocate(Integer.BYTES);
        final var v = ThreadLocalRandom.current().nextInt();
        __PersistenceCryptoService.int_4(b, v);
        assertThat(__PersistenceCryptoService.int_4(b.flip())).isEqualTo(v);
    }

    @Test
    void long__() {
        final var b = ByteBuffer.allocate(Long.BYTES);
        final var v = ThreadLocalRandom.current().nextLong();
        __PersistenceCryptoService.long_8(b, v);
        assertThat(__PersistenceCryptoService.long_8(b.flip())).isEqualTo(v);
    }

    @Test
    void float__() {
        final var b = ByteBuffer.allocate(Float.BYTES);
        final var v = Float.intBitsToFloat(ThreadLocalRandom.current().nextInt());
        __PersistenceCryptoService.float_4(b, v);
        assertThat(__PersistenceCryptoService.float_4(b.flip())).isEqualTo(v);
    }

    @Test
    void double__() {
        final var b = ByteBuffer.allocate(Double.BYTES);
        final var v = Double.longBitsToDouble(ThreadLocalRandom.current().nextLong());
        __PersistenceCryptoService.double_8(b, v);
        assertThat(__PersistenceCryptoService.double_8(b.flip())).isEqualTo(v);
    }

    @Test
    void char__() {
        final var b = ByteBuffer.allocate(Character.BYTES);
        final var v = (char) Double.longBitsToDouble(ThreadLocalRandom.current().nextInt());
        __PersistenceCryptoService.char_2(b, v);
        assertThat(__PersistenceCryptoService.char_2(b.flip())).isEqualTo(v);
    }

    @Test
    void string__() {
        final var b = ByteBuffer.allocate(1024);
        final var v = new RandomStringGenerator.Builder().build().generate(ThreadLocalRandom.current().nextInt(128));
        __PersistenceCryptoService.string_4_(b, v);
        assertThat(__PersistenceCryptoService.string_4_(b.flip())).isEqualTo(v);
    }

    @Test
    void big_integer__() {
        final var b = ByteBuffer.allocate(1024);
        final var v = new BigInteger(randomBytes(1, 128));
        __PersistenceCryptoService.big_integer_(b, v);
        assertThat(__PersistenceCryptoService.big_integer_(b.flip())).isEqualTo(v);
    }

    @Test
    void big_decimal__() {
        final var b = ByteBuffer.allocate(1024);
        final var v = new BigDecimal(randomDigitString(0, 128) + '.' + randomDigitString(0, 128));
        __PersistenceCryptoService.big_decimal_(b, v);
        assertThat(__PersistenceCryptoService.big_decimal_(b.flip())).isEqualTo(v);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Test
    void local_date__() {
        final var b = ByteBuffer.allocate(Long.BYTES);
        final var v = LocalDate.now();
        __PersistenceCryptoService.local_date_8(b, v);
        assertThat(__PersistenceCryptoService.local_date_8(b.flip())).isEqualTo(v);
    }

    @Test
    void local_time__() {
        final var b = ByteBuffer.allocate(Long.BYTES);
        final var v = LocalTime.now();
        __PersistenceCryptoService.local_time_8(b, v);
        assertThat(__PersistenceCryptoService.local_time_8(b.flip())).isEqualTo(v);
    }

    @Test
    void local_date_time__() {
        final var b = ByteBuffer.allocate(Long.BYTES << 1);
        final var v = LocalDateTime.now();
        __PersistenceCryptoService.local_date_time_16(b, v);
        assertThat(__PersistenceCryptoService.local_date_time_16(b.flip())).isEqualTo(v);
    }

    @Test
    void offset_time__() {
        final var b = ByteBuffer.allocate(12);
        final var v = OffsetTime.now();
        __PersistenceCryptoService.offset_time_12(b, v);
        assertThat(__PersistenceCryptoService.offset_time_12(b.flip())).isEqualTo(v);
    }

    @Test
    void offset_date_time__() {
        final var b = ByteBuffer.allocate(20);
        final var v = OffsetDateTime.now();
        __PersistenceCryptoService.offset_date_time_20(b, v);
        assertThat(__PersistenceCryptoService.offset_date_time_20(b.flip())).isEqualTo(v);
    }

    @Test
    void instant__() {
        final var b = ByteBuffer.allocate(16);
        final var v = Instant.now();
        __PersistenceCryptoService.instant_16(b, v);
        assertThat(__PersistenceCryptoService.instant_16(b.flip())).isEqualTo(v);
    }

    @Test
    void year__() {
        final var b = ByteBuffer.allocate(4);
        final var v = Year.now();
        __PersistenceCryptoService.year_4(b, v);
        assertThat(__PersistenceCryptoService.year_4(b.flip())).isEqualTo(v);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Test
    void util_date__() {
        final var b = ByteBuffer.allocate(8);
        final var v = new java.util.Date();
        __PersistenceCryptoService.util_date_8(b, v);
        assertThat(__PersistenceCryptoService.util_date_8(b.flip())).isEqualTo(v);
    }

    @Test
    void util_calendar__() {
        final var b = ByteBuffer.allocate(8);
        final var v = java.util.Calendar.getInstance();
        __PersistenceCryptoService.util_calendar_8(b, v);
        assertThat(__PersistenceCryptoService.util_calendar_8(b.flip())).isEqualTo(v);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Test
    void sql_date__() {
        final var b = ByteBuffer.allocate(8);
        final var v = new java.sql.Date(new java.util.Date().getTime());
        __PersistenceCryptoService.sql_date_8(b, v);
        assertThat(__PersistenceCryptoService.sql_date_8(b.flip())).isEqualTo(v);
    }

    @Test
    void sql_time__() {
        final var b = ByteBuffer.allocate(8);
        final var v = new java.sql.Time(new java.util.Date().getTime());
        __PersistenceCryptoService.sql_time_8(b, v);
        assertThat(__PersistenceCryptoService.sql_time_8(b.flip())).isEqualTo(v);
    }

    @Test
    void sql_timestamp__() {
        final var b = ByteBuffer.allocate(8);
        final var v = new java.sql.Timestamp(new java.util.Date().getTime());
        __PersistenceCryptoService.sql_timestamp_8(b, v);
        assertThat(__PersistenceCryptoService.sql_timestamp_8(b.flip())).isEqualTo(v);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName(("byte[0]"))
    @Test
    void bytes__0() {
        final var b = ByteBuffer.allocate(1024);
        final var v = new byte[0];
        __PersistenceCryptoService.bytes_4_(b, v);
        assertThat(__PersistenceCryptoService.bytes_4_(b.flip())).isEqualTo(v);
    }

    @DisplayName(("byte[]"))
    @Test
    void bytes__() {
        final var b = ByteBuffer.allocate(1024);
        final var v = randomBytes(0, 128);
        __PersistenceCryptoService.bytes_4_(b, v);
        assertThat(__PersistenceCryptoService.bytes_4_(b.flip())).isEqualTo(v);
    }

    @DisplayName(("byte[0]"))
    @Test
    void Bytes__0() {
        final var b = ByteBuffer.allocate(1024);
        final var v = new Byte[0];
        __PersistenceCryptoService.Bytes_4_(b, v);
        assertThat(__PersistenceCryptoService.Bytes_4_(b.flip())).isEqualTo(v);
    }

    @DisplayName(("byte[]"))
    @Test
    void Bytes__() {
        final var b = ByteBuffer.allocate(1024);
        final Byte[] v;
        {
            final var bytes = randomBytes(0, 128);
            v = new Byte[bytes.length];
            for (int i = 0; i < v.length; i++) {
                v[i] = bytes[i];
            }
        }
        __PersistenceCryptoService.Bytes_4_(b, v);
        assertThat(__PersistenceCryptoService.Bytes_4_(b.flip())).isEqualTo(v);
    }

    @DisplayName(("char[0]"))
    @Test
    void chars__0() {
        final var b = ByteBuffer.allocate(1024);
        final var v = new char[0];
        __PersistenceCryptoService.chars_4_(b, v);
        assertThat(__PersistenceCryptoService.chars_4_(b.flip())).isEqualTo(v);
    }

    @DisplayName(("char[]"))
    @Test
    void chars__() {
        final var b = ByteBuffer.allocate(1024);
        final var v = randomChars(0, 128);
        __PersistenceCryptoService.chars_4_(b, v);
        assertThat(__PersistenceCryptoService.chars_4_(b.flip())).isEqualTo(v);
    }

    @DisplayName(("Character[0]"))
    @Test
    void Characters__0() {
        final var b = ByteBuffer.allocate(1024);
        final var v = new Character[0];
        __PersistenceCryptoService.Characters_4_(b, v);
        assertThat(__PersistenceCryptoService.Characters_4_(b.flip())).isEqualTo(v);
    }

    @DisplayName(("Character[]"))
    @Test
    void Characters__() {
        final var b = ByteBuffer.allocate(1024);
        final Character[] v;
        {
            final var chars = randomChars(0, 128);
            v = new Character[chars.length];
            for (int i = 0; i < v.length; i++) {
                v[i] = chars[i];
            }
        }
        __PersistenceCryptoService.Characters_4_(b, v);
        assertThat(__PersistenceCryptoService.Characters_4_(b.flip())).isEqualTo(v);
    }

    private enum MyEnum {
        A,
        B;
    }

    @EnumSource(MyEnum.class)
    @ParameterizedTest
    void enum__(final MyEnum v) {
        final var b = ByteBuffer.allocate(1024);
        __PersistenceCryptoService.enum_(b, v);
        assertThat(__PersistenceCryptoService.enum_(b.flip(), MyEnum.class)).isSameAs(v);
    }

    @EqualsAndHashCode
    static class MySerializable implements Serializable {

        private static final long serialVersionUID = -7002669353525847L;

        private String name;

        private int age;
    }

    @EnumSource(MyEnum.class)
    @ParameterizedTest
    void serializable__() {
        final var b = ByteBuffer.allocate(1024);
        final var v = new MySerializable();
        __PersistenceCryptoService.serializable_4_(b, v);
        assertThat((MySerializable) __PersistenceCryptoService.serializable_4_(b.flip())).isEqualTo(v);
    }
}
