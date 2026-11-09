package com.github.jinahya.persistence.more.converter.javatime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ZonedDateTimeConverter.OfString}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("OfZonedDateTime")
class __TemporalAccessorStringAttributeConverter_OfZonedDateTime_Test {

    private final ZonedDateTimeConverter.OfString converter =
            new ZonedDateTimeConverter.OfString();

    @DisplayName("null <-> null")
    @Test
    void __null() {
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }
}
