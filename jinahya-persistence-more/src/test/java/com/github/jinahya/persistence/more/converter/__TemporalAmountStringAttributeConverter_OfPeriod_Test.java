package com.github.jinahya.persistence.more.converter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Period;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link __TemporalAmountStringAttributeConverters.OfPeriod}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("OfPeriod")
class __TemporalAmountStringAttributeConverter_OfPeriod_Test
        extends __TemporalAmountStringAttributeConverter_Test<__TemporalAmountStringAttributeConverters.OfPeriod,
        Period> {

    __TemporalAmountStringAttributeConverter_OfPeriod_Test() {
        super(__TemporalAmountStringAttributeConverters.OfPeriod.class, Period.class);
    }

    private final __TemporalAmountStringAttributeConverters.OfPeriod converter =
            new __TemporalAmountStringAttributeConverters.OfPeriod();

    @DisplayName("null <-> null")
    @Test
    void __null() {
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }

    @DisplayName("an ordinary value round-trips")
    @ParameterizedTest
    @ValueSource(strings = {"P0D", "P1D", "P1Y", "P1M", "P1Y2M3D", "P-1Y-2M-3D", "P14M"})
    void __ordinary(final String value) {
        final var period = Period.parse(value);
        assertThat(converter.convertToEntityAttribute(converter.convertToDatabaseColumn(period)))
                .isEqualTo(period);
    }

    @DisplayName("zero is stored as P0D")
    @Test
    void __zero() {
        assertThat(converter.convertToDatabaseColumn(Period.ZERO)).isEqualTo("P0D");
    }

    @DisplayName("the units are kept as declared, not normalized")
    @Test
    void __notNormalized() {
        final var period = Period.ofMonths(14);
        assertThat(converter.convertToDatabaseColumn(period)).isEqualTo("P14M");
        final var read = converter.convertToEntityAttribute("P14M");
        assertThat(read).isEqualTo(period);
        assertThat(read).isNotNull();
        assertThat(read.getYears()).isZero();
        assertThat(read.getMonths()).isEqualTo(14);
    }

    @DisplayName("the extremes round-trip, within 40 characters")
    @Test
    void __extremes() {
        for (final var period : new Period[]{
                Period.of(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE),
                Period.of(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE)
        }) {
            final var column = converter.convertToDatabaseColumn(period);
            assertThat(column).isNotNull().hasSizeLessThanOrEqualTo(40);
            assertThat(converter.convertToEntityAttribute(column)).isEqualTo(period);
        }
    }
}
