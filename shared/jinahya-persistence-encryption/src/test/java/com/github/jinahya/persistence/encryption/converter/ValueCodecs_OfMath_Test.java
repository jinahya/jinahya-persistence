package com.github.jinahya.persistence.encryption.converter;

import com.github.jinahya.persistence.encryption.converter.ValueCodecs.OfMath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link ValueCodecs.OfMath}.
 * <p>
 * Each nested class pins the encoding against an independent reference: the encodings are stored data, and a change in
 * one makes every existing value decode differently.
 */
class ValueCodecs_OfMath_Test {

    // ------------------------------------------------------------------------------------------------------ BigInteger

    @DisplayName("BigInteger")
    @Nested
    class BigIntegerTest {

        private static Stream<BigInteger> values() {
            return Stream.of(
                    BigInteger.ZERO, BigInteger.ONE, BigInteger.ONE.negate(), BigInteger.valueOf(127L),
                    BigInteger.valueOf(128L), BigInteger.valueOf(-128L), BigInteger.valueOf(-129L),
                    BigInteger.valueOf(Long.MIN_VALUE), BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE),
                    BigInteger.TWO.pow(521).subtract(BigInteger.ONE).negate()
            );
        }

        @DisplayName("encodeBigInteger(BigInteger) encodes the two's-complement, big-endian, minimal bytes, and"
                     + " decodeBigInteger(byte[]) reads them back")
        @MethodSource("values")
        @ParameterizedTest
        void __roundTrip(final BigInteger v) {
            assertThat(OfMath.encodeBigInteger(v)).isEqualTo(v.toByteArray());
            assertThat(OfMath.decodeBigInteger(OfMath.encodeBigInteger(v))).isEqualTo(v);
        }

        @DisplayName("128 takes two bytes, the first of them a sign byte")
        @Test
        void __signByte() {
            assertThat(OfMath.encodeBigInteger(BigInteger.valueOf(128L))).containsExactly(0x00, 0x80);
            assertThat(OfMath.encodeBigInteger(BigInteger.valueOf(-128L))).containsExactly(0x80);
        }

        @DisplayName("decodeBigInteger(byte[]) fails on an empty array, with an IllegalArgumentException")
        @Test
        void __empty() {
            assertThatThrownBy(() -> OfMath.decodeBigInteger(new byte[0]))
                    .isInstanceOf(NumberFormatException.class)
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("the methods fail on null")
        @Test
        void __null() {
            assertThatThrownBy(() -> OfMath.encodeBigInteger(null)).isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> OfMath.decodeBigInteger(null)).isInstanceOf(NullPointerException.class);
        }
    }

    // ------------------------------------------------------------------------------------------------------ BigDecimal

    @DisplayName("BigDecimal")
    @Nested
    class BigDecimalTest {

        private static Stream<BigDecimal> values() {
            return Stream.of(
                    BigDecimal.ZERO, new BigDecimal("0.00"), BigDecimal.ONE, new BigDecimal("1.0"),
                    new BigDecimal("-123.456"), new BigDecimal("1E+10"), new BigDecimal("1E-10"),
                    new BigDecimal(BigInteger.ONE, Integer.MIN_VALUE),
                    new BigDecimal(BigInteger.ONE, Integer.MAX_VALUE),
                    new BigDecimal("3.14159265358979323846264338327950288419716939937510")
            );
        }

        @DisplayName("encodeBigDecimal(BigDecimal) encodes the scale, in four big-endian bytes, then the unscaled"
                     + " value, and decodeBigDecimal(byte[]) reads them back, scale included")
        @MethodSource("values")
        @ParameterizedTest
        void __roundTrip(final BigDecimal v) {
            final var unscaled = v.unscaledValue().toByteArray();
            final var expected = ByteBuffer.allocate(Integer.BYTES + unscaled.length)
                    .putInt(v.scale()).put(unscaled).array();
            assertThat(OfMath.encodeBigDecimal(v)).isEqualTo(expected);
            final var decoded = OfMath.decodeBigDecimal(OfMath.encodeBigDecimal(v));
            assertThat(decoded).isEqualTo(v);   // equals(), not compareTo(): 1.0 is not 1.00
            assertThat(decoded.scale()).isEqualTo(v.scale());
        }

        @DisplayName("decodeBigDecimal(byte[]) fails on fewer than four bytes, for the scale")
        @Test
        void __tooShort() {
            assertThatThrownBy(() -> OfMath.decodeBigDecimal(new byte[Integer.BYTES - 1]))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("decodeBigDecimal(byte[]) fails on a scale without an unscaled value")
        @Test
        void __scaleOnly() {
            assertThatThrownBy(() -> OfMath.decodeBigDecimal(new byte[Integer.BYTES]))
                    .isInstanceOf(NumberFormatException.class);
        }

        @DisplayName("the methods fail on null")
        @Test
        void __null() {
            assertThatThrownBy(() -> OfMath.encodeBigDecimal(null)).isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> OfMath.decodeBigDecimal(null)).isInstanceOf(NullPointerException.class);
        }
    }
}
