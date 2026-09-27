package com.github.jinahya.persistence.more.converter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Period;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link __TemporalAmountStringAttributeConverters.OfPeriod}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("OfPeriod")
class __TemporalAmountStringAttributeConverter_OfPeriod_Test
 {

    private final __TemporalAmountStringAttributeConverters.OfPeriod converter =
            new __TemporalAmountStringAttributeConverters.OfPeriod();

    @DisplayName("null <-> null")
    @Test
    void __null() {
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
        assertThat(converter.convertToEntityAttribute(null)).isNull();
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

}
