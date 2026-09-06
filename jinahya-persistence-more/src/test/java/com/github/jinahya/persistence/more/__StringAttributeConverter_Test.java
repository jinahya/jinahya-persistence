package com.github.jinahya.persistence.more;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link __StringAttributeConverter} and its nested numeric converters.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class __StringAttributeConverter_Test {

    @DisplayName("OfDouble")
    @Nested
    class OfDouble_Test {

        private final __StringAttributeConverter.OfDouble converter = new __StringAttributeConverter.OfDouble();

        @DisplayName("null <-> null")
        @Test
        void __null() {
            assertThat(converter.convertToDatabaseColumn(null)).isNull();
            assertThat(converter.convertToEntityAttribute(null)).isNull();
        }

        @DisplayName("an ordinary value round-trips")
        @ParameterizedTest
        @ValueSource(doubles = {0.0d, 1.0d, -1.0d, 0.5d, 1234.5678d, Double.MIN_VALUE, Double.MAX_VALUE})
        void __ordinary(final double value) {
            assertThat(converter.convertToEntityAttribute(converter.convertToDatabaseColumn(value)))
                    .isEqualTo(value);
        }

        @DisplayName("NaN and the infinities round-trip instead of throwing")
        @ParameterizedTest
        @ValueSource(doubles = {Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY})
        void __nonFinite(final double value) {
            // BigDecimal cannot represent these at all, so routing through it used to raise
            // NumberFormatException on a perfectly ordinary double
            final var column = converter.convertToDatabaseColumn(value);
            // boxed on purpose: AssertJ compares a primitive expected with ==, and NaN != NaN
            assertThat(converter.convertToEntityAttribute(column)).isEqualTo(Double.valueOf(value));
        }
    }

    @DisplayName("OfFloat")
    @Nested
    class OfFloat_Test {

        private final __StringAttributeConverter.OfFloat converter = new __StringAttributeConverter.OfFloat();

        @DisplayName("an ordinary value round-trips")
        @ParameterizedTest
        @ValueSource(floats = {0.0f, 1.0f, -1.0f, 0.5f, 1234.5f})
        void __ordinary(final float value) {
            assertThat(converter.convertToEntityAttribute(converter.convertToDatabaseColumn(value)))
                    .isEqualTo(value);
        }

        @DisplayName("NaN and the infinities round-trip instead of throwing")
        @ParameterizedTest
        @ValueSource(floats = {Float.NaN, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY})
        void __nonFinite(final float value) {
            final var column = converter.convertToDatabaseColumn(value);
            // boxed on purpose: AssertJ compares a primitive expected with ==, and NaN != NaN
            assertThat(converter.convertToEntityAttribute(column)).isEqualTo(Float.valueOf(value));
        }
    }
}
