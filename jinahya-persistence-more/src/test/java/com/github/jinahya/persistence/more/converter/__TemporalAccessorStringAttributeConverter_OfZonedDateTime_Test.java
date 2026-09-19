package com.github.jinahya.persistence.more.converter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link __TemporalAccessorStringAttributeConverters.OfZonedDateTime}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("OfZonedDateTime")
class __TemporalAccessorStringAttributeConverter_OfZonedDateTime_Test
        extends __TemporalAccessorStringAttributeConverter_Test<
        __TemporalAccessorStringAttributeConverters.OfZonedDateTime, ZonedDateTime> {

    __TemporalAccessorStringAttributeConverter_OfZonedDateTime_Test() {
        super(__TemporalAccessorStringAttributeConverters.OfZonedDateTime.class, ZonedDateTime.class);
    }

    private final __TemporalAccessorStringAttributeConverters.OfZonedDateTime converter =
            new __TemporalAccessorStringAttributeConverters.OfZonedDateTime();

    @DisplayName("null <-> null")
    @Test
    void __null() {
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }

    @DisplayName("the zone id survives the round trip, which is what a TIMESTAMP column cannot do")
    @Test
    void __zoneIdSurvives() {
        final var seoul = ZoneId.of("Asia/Seoul");
        final var value = ZonedDateTime.of(LocalDateTime.of(2026, 9, 19, 11, 22, 33), seoul);
        final var column = converter.convertToDatabaseColumn(value);
        assertThat(column).isEqualTo("2026-09-19T11:22:33+09:00[Asia/Seoul]");
        final var read = converter.convertToEntityAttribute(column);
        assertThat(read).isEqualTo(value);
        assertThat(read).isNotNull();
        assertThat(read.getZone()).isEqualTo(seoul);
    }
}
