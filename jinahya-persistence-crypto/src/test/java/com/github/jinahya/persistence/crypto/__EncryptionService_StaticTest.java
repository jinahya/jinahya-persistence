package com.github.jinahya.persistence.crypto;

import org.apache.commons.text.RandomStringGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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

import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.big_decimal_;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.big_integer_;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.boolean_1;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.char_2;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.double_8;
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
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.sql_timestamp_8;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.string_;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.util_calendar_8;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.util_date_8;
import static com.github.jinahya.persistence.crypto.__EncryptionSerivceUtils.year_4;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * .
 *
 * @see <a href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#a486>2.6. Basic
 *         Types</a> (Jakarta Persistence 3.2 Specification Document
 */
class __EncryptionService_StaticTest {

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
        boolean_1(b, v);
        assertThat(boolean_1(b.flip())).isEqualTo(v);
    }

    @Test
    void short__() {
        final var b = ByteBuffer.allocate(Short.BYTES);
        final var v = (short) ThreadLocalRandom.current().nextInt();
        short_2(b, v);
        assertThat(short_2(b.flip())).isEqualTo(v);
    }

    @Test
    void int__() {
        final var b = ByteBuffer.allocate(Integer.BYTES);
        final var v = ThreadLocalRandom.current().nextInt();
        int_4(b, v);
        assertThat(int_4(b.flip())).isEqualTo(v);
    }

    @Test
    void long__() {
        final var b = ByteBuffer.allocate(Long.BYTES);
        final var v = ThreadLocalRandom.current().nextLong();
        long_8(b, v);
        assertThat(long_8(b.flip())).isEqualTo(v);
    }

    @Test
    void float__() {
        final var b = ByteBuffer.allocate(Float.BYTES);
        final var v = Float.intBitsToFloat(ThreadLocalRandom.current().nextInt());
        float_4(b, v);
        assertThat(float_4(b.flip())).isEqualTo(v);
    }

    @Test
    void double__() {
        final var b = ByteBuffer.allocate(Double.BYTES);
        final var v = Double.longBitsToDouble(ThreadLocalRandom.current().nextLong());
        double_8(b, v);
        assertThat(double_8(b.flip())).isEqualTo(v);
    }

    @Test
    void char__() {
        final var b = ByteBuffer.allocate(Character.BYTES);
        final var v = (char) Double.longBitsToDouble(ThreadLocalRandom.current().nextInt());
        char_2(b, v);
        assertThat(char_2(b.flip())).isEqualTo(v);
    }

    @Test
    void string__() {
        final var b = ByteBuffer.allocate(1024);
        final var v = new RandomStringGenerator.Builder().build().generate(ThreadLocalRandom.current().nextInt(128));
        final var encoded = __EncryptionSerivceUtils.string_(v);
        assertThat(string_(encoded)).isEqualTo(v);
    }

    @Test
    void big_integer__() {
        final var b = ByteBuffer.allocate(1024);
        final var v = new BigInteger(randomBytes(1, 128));
        final var encoded = big_integer_(v);
        assertThat(big_integer_(encoded)).isEqualTo(v);
    }

    @Test
    void big_decimal__() {
        final var b = ByteBuffer.allocate(1024);
        final var v = new BigDecimal(randomDigitString(0, 128) + '.' + randomDigitString(0, 128));
        final var encoded = big_decimal_(v);
        assertThat(big_decimal_(encoded)).isEqualTo(v);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Test
    void local_date__() {
        final var b = ByteBuffer.allocate(Long.BYTES);
        final var v = LocalDate.now();
        local_date_8(b, v);
        assertThat(local_date_8(b.flip())).isEqualTo(v);
    }

    @Test
    void local_time__() {
        final var b = ByteBuffer.allocate(Long.BYTES);
        final var v = LocalTime.now();
        local_time_8(b, v);
        assertThat(local_time_8(b.flip())).isEqualTo(v);
    }

    @Test
    void local_date_time__() {
        final var b = ByteBuffer.allocate(Long.BYTES << 1);
        final var v = LocalDateTime.now();
        local_date_time_16(b, v);
        assertThat(local_date_time_16(b.flip())).isEqualTo(v);
    }

    @Test
    void offset_time__() {
        final var b = ByteBuffer.allocate(12);
        final var v = OffsetTime.now();
        offset_time_12(b, v);
        assertThat(offset_time_12(b.flip())).isEqualTo(v);
    }

    @Test
    void offset_date_time__() {
        final var b = ByteBuffer.allocate(20);
        final var v = OffsetDateTime.now();
        offset_date_time_20(b, v);
        assertThat(offset_date_time_20(b.flip())).isEqualTo(v);
    }

    @Test
    void instant__() {
        final var b = ByteBuffer.allocate(16);
        final var v = Instant.now();
        instant_16(b, v);
        assertThat(instant_16(b.flip())).isEqualTo(v);
    }

    @Test
    void year__() {
        final var b = ByteBuffer.allocate(4);
        final var v = Year.now();
        year_4(b, v);
        assertThat(year_4(b.flip())).isEqualTo(v);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Test
    void util_date__() {
        final var b = ByteBuffer.allocate(8);
        final var v = new java.util.Date();
        util_date_8(b, v);
        assertThat(util_date_8(b.flip())).isEqualTo(v);
    }

    @Test
    void util_calendar__() {
        final var b = ByteBuffer.allocate(8);
        final var v = java.util.Calendar.getInstance();
        util_calendar_8(b, v);
        assertThat(util_calendar_8(b.flip())).isEqualTo(v);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Test
    void sql_date__() {
        final var b = ByteBuffer.allocate(8);
        final var v = new java.sql.Date(new java.util.Date().getTime());
        sql_date_8(b, v);
        assertThat(sql_date_8(b.flip())).isEqualTo(v);
    }

    @Test
    void sql_time__() {
        final var b = ByteBuffer.allocate(8);
        final var v = new java.sql.Time(new java.util.Date().getTime());
        sql_time_8(b, v);
        assertThat(sql_time_8(b.flip())).isEqualTo(v);
    }

    @Test
    void sql_timestamp__() {
        final var b = ByteBuffer.allocate(8);
        final var v = new java.sql.Timestamp(new java.util.Date().getTime());
        sql_timestamp_8(b, v);
        assertThat(sql_timestamp_8(b.flip())).isEqualTo(v);
    }

//    // -----------------------------------------------------------------------------------------------------------------
//    @DisplayName(("byte[0]"))
//    @Test
//    void bytes__0() {
//        final var b = ByteBuffer.allocate(1024);
//        final var v = new byte[0];
//        __EncryptionSerivceUtils.bytes_l()..octets_4_(b, v);
//        assertThat(octets_4_(b.flip())).isEqualTo(v);
//    }
//
//    @DisplayName(("byte[]"))
//    @Test
//    void bytes__() {
//        final var b = ByteBuffer.allocate(1024);
//        final var v = randomBytes(0, 128);
//        __EncryptionSerivceUtils.octets_4_(b, v);
//        assertThat(octets_4_(b.flip())).isEqualTo(v);
//    }
//
//    @DisplayName(("byte[0]"))
//    @Test
//    void Bytes__0() {
//        final var b = ByteBuffer.allocate(1024);
//        final var v = new Byte[0];
//        __EncryptionSerivceUtils.Bytes_l(b, v);
//        assertThat(Bytes_l(b.flip())).isEqualTo(v);
//    }
//
//    @DisplayName(("byte[]"))
//    @Test
//    void Bytes__() {
//        final var b = ByteBuffer.allocate(1024);
//        final Byte[] v;
//        {
//            final var bytes = randomBytes(0, 128);
//            v = new Byte[bytes.length];
//            for (int i = 0; i < v.length; i++) {
//                v[i] = bytes[i];
//            }
//        }
//        __EncryptionSerivceUtils.Bytes_l(b, v);
//        assertThat(Bytes_l(b.flip())).isEqualTo(v);
//    }
//
//    @DisplayName(("char[0]"))
//    @Test
//    void chars__0() {
//        final var b = ByteBuffer.allocate(1024);
//        final var v = new char[0];
//        __EncryptionSerivceUtils.chars_2l(b, v);
//        assertThat(chars_2l(b.flip())).isEqualTo(v);
//    }
//
//    @DisplayName(("char[]"))
//    @Test
//    void chars__() {
//        final var b = ByteBuffer.allocate(1024);
//        final var v = randomChars(0, 128);
//        __EncryptionSerivceUtils.chars_2l(b, v);
//        assertThat(chars_2l(b.flip())).isEqualTo(v);
//    }
//
//    @DisplayName(("Character[0]"))
//    @Test
//    void Characters__0() {
//        final var b = ByteBuffer.allocate(1024);
//        final var v = new Character[0];
//        __EncryptionSerivceUtils.Characters_2l(b, v);
//        assertThat(Characters_2l(b.flip())).isEqualTo(v);
//    }
//
//    @DisplayName(("Character[]"))
//    @Test
//    void Characters__() {
//        final var b = ByteBuffer.allocate(1024);
//        final Character[] v;
//        {
//            final var chars = randomChars(0, 128);
//            v = new Character[chars.length];
//            for (int i = 0; i < v.length; i++) {
//                v[i] = chars[i];
//            }
//        }
//        __EncryptionSerivceUtils.Characters_2l(b, v);
//        assertThat(Characters_2l(b.flip())).isEqualTo(v);
//    }
//
//    private enum MyEnum {
//        A,
//        B;
//    }
//
//    @EnumSource(MyEnum.class)
//    @ParameterizedTest
//    void enum__(final MyEnum v) {
//        final var b = ByteBuffer.allocate(1024);
//        enum_(b, v);
//        assertThat(enum_(b.flip(), MyEnum.class)).isSameAs(v);
//    }
//
//    @EqualsAndHashCode
//    static class MySerializable implements Serializable {
//
//        private static final long serialVersionUID = -7002669353525847L;
//
//        private String name;
//
//        private int age;
//    }
//
//    @EnumSource(MyEnum.class)
//    @ParameterizedTest
//    void serializable__() {
//        final var b = ByteBuffer.allocate(1024);
//        final var v = new MySerializable();
//        serializable_4_(b, v);
//        assertThat((MySerializable) serializable_4_(b.flip())).isEqualTo(v);
//    }
}
