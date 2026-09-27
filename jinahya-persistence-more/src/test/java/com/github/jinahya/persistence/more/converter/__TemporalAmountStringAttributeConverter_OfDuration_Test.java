package com.github.jinahya.persistence.more.converter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link __TemporalAmountStringAttributeConverters.OfDuration}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("OfDuration")
class __TemporalAmountStringAttributeConverter_OfDuration_Test
 {

    private final __TemporalAmountStringAttributeConverters.OfDuration converter =
            new __TemporalAmountStringAttributeConverters.OfDuration();

    @DisplayName("null <-> null")
    @Test
    void __null() {
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }

    @DisplayName("zero is stored as PT0S")
    @Test
    void __zero() {
        assertThat(converter.convertToDatabaseColumn(Duration.ZERO)).isEqualTo("PT0S");
    }

    @DisplayName("a value in days is stored in hours, which is Duration's own spelling")
    @Test
    void __days() {
        assertThat(converter.convertToDatabaseColumn(Duration.ofDays(2L))).isEqualTo("PT48H");
    }

}
