package com.github.jinahya.persistence.more.converter.time;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link __TemporalAccessorTextStringAttributeConverters.OfDayOfWeek}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("OfDayOfWeek")
@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class __TemporalAccessorTextStringAttributeConverters_OfDayOfWeek_Test {

    private final __TemporalAccessorTextStringAttributeConverters.OfDayOfWeek converter =
            new __TemporalAccessorTextStringAttributeConverters.OfDayOfWeek();

    @DisplayName("every day is stored as its full English name, and read back")
    @ParameterizedTest
    @EnumSource(DayOfWeek.class)
    void __roundTrip(final DayOfWeek value) {
        final var column = converter.convertToDatabaseColumn(value);
        assertThat(column).isEqualTo(value.getDisplayName(TextStyle.FULL_STANDALONE, Locale.ENGLISH));
        assertThat(converter.convertToEntityAttribute(column)).isSameAs(value);
    }

    @DisplayName("the upper-cased constant name is read, too")
    @ParameterizedTest
    @EnumSource(DayOfWeek.class)
    void __constantName(final DayOfWeek value) {
        assertThat(converter.convertToEntityAttribute(value.name())).isSameAs(value);
    }
}
