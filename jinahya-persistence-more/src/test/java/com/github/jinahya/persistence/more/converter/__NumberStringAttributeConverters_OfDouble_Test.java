package com.github.jinahya.persistence.more.converter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link __NumberStringAttributeConverters.OfDouble}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("OfDouble")
class __NumberStringAttributeConverters_OfDouble_Test
        extends __NumberStringAttributeConverter_Test<__NumberStringAttributeConverters.OfDouble, Double> {

    __NumberStringAttributeConverters_OfDouble_Test() {
        super(__NumberStringAttributeConverters.OfDouble.class, Double.class);
    }

    private final __NumberStringAttributeConverters.OfDouble converter =
            new __NumberStringAttributeConverters.OfDouble();

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
