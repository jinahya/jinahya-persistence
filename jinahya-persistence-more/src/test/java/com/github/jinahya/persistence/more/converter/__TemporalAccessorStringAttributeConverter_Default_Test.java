package com.github.jinahya.persistence.more.converter;

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
import java.time.temporal.TemporalAccessor;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for what {@link __TemporalAccessorStringAttributeConverter} itself does, across every converter nested in it.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("__TemporalAccessorStringAttributeConverter")
@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class __TemporalAccessorStringAttributeConverter_Default_Test {

    private static final ZoneOffset KST = ZoneOffset.ofHours(9);

    /**
     * Returns each nested converter, paired with a value of the type it converts.
     *
     * @return a stream of arguments.
     */
    static Stream<Arguments> nestedConvertersAndValues() {
        final var dateTime = LocalDateTime.of(2026, 9, 19, 11, 22, 33, 123_456_789);
        return Stream.of(
                Arguments.of(new __TemporalAccessorStringAttributeConverters.OfInstant(),
                             Instant.parse("2026-09-19T11:22:33.123456789Z")),
                Arguments.of(new __TemporalAccessorStringAttributeConverters.OfLocalDate(),
                             LocalDate.of(2026, 9, 19)),
                Arguments.of(new __TemporalAccessorStringAttributeConverters.OfLocalDateTime(), dateTime),
                Arguments.of(new __TemporalAccessorStringAttributeConverters.OfLocalTime(),
                             LocalTime.of(11, 22, 33)),
                Arguments.of(new __TemporalAccessorStringAttributeConverters.OfOffsetDateTime(),
                             OffsetDateTime.of(dateTime, KST)),
                Arguments.of(new __TemporalAccessorStringAttributeConverters.OfOffsetTime(),
                             OffsetTime.of(LocalTime.of(11, 22, 33), KST)),
                Arguments.of(new __TemporalAccessorStringAttributeConverters.OfZonedDateTime(),
                             ZonedDateTime.of(dateTime, ZoneId.of("Asia/Seoul"))),
                Arguments.of(new __TemporalAccessorStringAttributeConverters.OfYear(), Year.of(2026)),
                Arguments.of(new __TemporalAccessorStringAttributeConverters.OfYearMonth(), YearMonth.of(2026, 9)),
                Arguments.of(new __TemporalAccessorStringAttributeConverters.OfMonthDay(), MonthDay.of(9, 19))
        );
    }

    @DisplayName("every nested converter writes the type's own form, and reads it back")
    @ParameterizedTest
    @MethodSource("nestedConvertersAndValues")
    void __roundTrip(final __TemporalAccessorStringAttributeConverter<?> converter, final TemporalAccessor value) {
        final var column = widen(converter).convertToDatabaseColumn(value);
        assertThat(column).isEqualTo(value.toString());
        assertThat(widen(converter).convertToEntityAttribute(column)).isEqualTo(value);
    }

    @DisplayName("every nested converter maps null both ways")
    @ParameterizedTest
    @MethodSource("nestedConvertersAndValues")
    void __null(final __TemporalAccessorStringAttributeConverter<?> converter) {
        assertThat(widen(converter).convertToDatabaseColumn(null)).isNull();
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }

    @DisplayName("a formatter, when given, is what writes and reads the column")
    @Test
    void __formatter() {
        final var converter =
                new __TemporalAccessorStringAttributeConverters.OfLocalDate(DateTimeFormatter.BASIC_ISO_DATE);
        final var value = LocalDate.of(2026, 9, 19);
        final var column = converter.convertToDatabaseColumn(value);
        assertThat(column).isEqualTo("20260919");
        assertThat(converter.convertToEntityAttribute(column)).isEqualTo(value);
    }

    @DisplayName("a formatter which drops a field the type carries breaks the read, rather than losing it quietly")
    @Test
    void __lossyFormatter() {
        final var converter =
                new __TemporalAccessorStringAttributeConverters.OfLocalDateTime(DateTimeFormatter.ISO_LOCAL_DATE);
        final var column = converter.convertToDatabaseColumn(LocalDateTime.of(2026, 9, 19, 11, 22, 33));
        assertThat(column).isEqualTo("2026-09-19");
        // the column holds no time at all, so LocalDateTime.from() has nothing to rebuild one out of
        assertThatThrownBy(() -> converter.convertToEntityAttribute(column))
                .isInstanceOf(DateTimeException.class);
    }

    @DisplayName("what parse throws is thrown unchanged, not wrapped in a reflection exception")
    @Test
    void __parseFailure() {
        final var converter = new __TemporalAccessorStringAttributeConverters.OfLocalDate();
        assertThatThrownBy(() -> converter.convertToEntityAttribute("not a date"))
                .isInstanceOf(DateTimeException.class);
    }

    /**
     * Returns the specified converter, widened so that a value typed as a bare {@link TemporalAccessor} can be handed
     * to it.
     *
     * @param converter the converter.
     * @return the {@code converter}.
     * @implNote Every call site pairs a converter with a value of its own type; the pairing is just not visible
     *         to the compiler through {@link Arguments}.
     */
    @SuppressWarnings({"unchecked"})
    private static __TemporalAccessorStringAttributeConverter<TemporalAccessor> widen(
            final __TemporalAccessorStringAttributeConverter<?> converter) {
        return (__TemporalAccessorStringAttributeConverter<TemporalAccessor>) converter;
    }
}
