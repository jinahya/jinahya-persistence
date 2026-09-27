package com.github.jinahya.persistence.more.converter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link __TemporalAccessorStringAttributeConverters.OfZonedDateTime}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("OfZonedDateTime")
class __TemporalAccessorStringAttributeConverter_OfZonedDateTime_Test
 {

    private final __TemporalAccessorStringAttributeConverters.OfZonedDateTime converter =
            new __TemporalAccessorStringAttributeConverters.OfZonedDateTime();

    @DisplayName("null <-> null")
    @Test
    void __null() {
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }

}
