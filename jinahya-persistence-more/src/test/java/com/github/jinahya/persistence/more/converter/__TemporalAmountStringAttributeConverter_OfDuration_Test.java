package com.github.jinahya.persistence.more.converter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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

    @DisplayName("an ordinary value round-trips")
    @ParameterizedTest
    @ValueSource(strings = {
            "PT0S", "PT1S", "PT-1S", "PT8H6M12.345S", "PT48H", "PT0.000000001S", "PT-0.000000001S"
    })
    void __ordinary(final String value) {
        final var duration = Duration.parse(value);
        assertThat(converter.convertToEntityAttribute(converter.convertToDatabaseColumn(duration)))
                .isEqualTo(duration);
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

    @DisplayName("the extremes round-trip, within 40 characters")
    @Test
    void __extremes() {
        for (final var duration : new Duration[]{
                Duration.ofSeconds(Long.MIN_VALUE),
                Duration.ofSeconds(Long.MAX_VALUE, 999_999_999L)
        }) {
            final var column = converter.convertToDatabaseColumn(duration);
            assertThat(column).isNotNull().hasSizeLessThanOrEqualTo(40);
            assertThat(converter.convertToEntityAttribute(column)).isEqualTo(duration);
        }
    }
}
