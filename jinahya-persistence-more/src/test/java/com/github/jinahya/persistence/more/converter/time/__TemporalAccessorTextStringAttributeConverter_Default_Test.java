package com.github.jinahya.persistence.more.converter.time;

import jakarta.persistence.AttributeConverter;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.DayOfWeek;
import java.time.Month;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for what {@link __TemporalAccessorTextStringAttributeConverter} itself does, across every converter nested in
 * {@link __TemporalAccessorTextStringAttributeConverters}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("__TemporalAccessorTextStringAttributeConverter")
@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class __TemporalAccessorTextStringAttributeConverter_Default_Test {

    /**
     * Returns each nested converter, paired with a value of the type it converts and that value's full English name.
     *
     * @return a stream of arguments.
     */
    static Stream<Arguments> nestedConvertersAndValues() {
        return Stream.of(
                Arguments.of(new __TemporalAccessorTextStringAttributeConverters.OfDayOfWeek(), DayOfWeek.MONDAY,
                             "Monday"),
                Arguments.of(new __TemporalAccessorTextStringAttributeConverters.OfMonth(), Month.MARCH, "March")
        );
    }

    @DisplayName("every nested converter maps null both ways")
    @ParameterizedTest
    @MethodSource("nestedConvertersAndValues")
    void __null(final AttributeConverter<TemporalAccessor, String> converter) {
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }

    @DisplayName("with no arguments, the column is the full English name")
    @ParameterizedTest
    @MethodSource("nestedConvertersAndValues")
    void __english(final AttributeConverter<TemporalAccessor, String> converter, final TemporalAccessor value,
                   final String name) {
        assertThat(converter.convertToDatabaseColumn(value)).isEqualTo(name);
        assertThat(converter.convertToEntityAttribute(name)).isEqualTo(value);
    }

    @DisplayName("the column is read regardless of case")
    @ParameterizedTest
    @MethodSource("nestedConvertersAndValues")
    void __caseInsensitive(final AttributeConverter<TemporalAccessor, String> converter,
                           final TemporalAccessor value, final String name) {
        assertThat(converter.convertToEntityAttribute(name.toUpperCase(Locale.ENGLISH))).isEqualTo(value);
        assertThat(converter.convertToEntityAttribute(name.toLowerCase(Locale.ENGLISH))).isEqualTo(value);
    }

    @DisplayName("the default locale plays no part in either direction")
    @ParameterizedTest
    @MethodSource("nestedConvertersAndValues")
    void __defaultLocaleIgnored(final AttributeConverter<TemporalAccessor, String> converter,
                                final TemporalAccessor value, final String name) {
        final var saved = Locale.getDefault();
        try {
            // Korean names nothing in Latin letters; Turkish upper-cases 'i' to a dotted capital
            for (final var locale : new Locale[] {Locale.KOREAN, Locale.forLanguageTag("tr")}) {
                Locale.setDefault(locale);
                assertThat(converter.convertToDatabaseColumn(value)).isEqualTo(name);
                assertThat(converter.convertToEntityAttribute(name)).isEqualTo(value);
                assertThat(converter.convertToEntityAttribute(name.toUpperCase(Locale.ENGLISH))).isEqualTo(value);
            }
        } finally {
            Locale.setDefault(saved);
        }
    }

    @DisplayName("a column which is not a name in the locale is refused, not guessed")
    @ParameterizedTest
    @MethodSource("nestedConvertersAndValues")
    void __unknownName(final AttributeConverter<TemporalAccessor, String> converter) {
        assertThatThrownBy(() -> converter.convertToEntityAttribute("Funday"))
                .isInstanceOf(DateTimeParseException.class);
        // the Korean name is not a name in English
        assertThatThrownBy(() -> converter.convertToEntityAttribute("월요일"))
                .isInstanceOf(DateTimeParseException.class);
        // a trailing blank is not part of a name, either; a CHAR column has to be trimmed before it gets here
        assertThatThrownBy(() -> converter.convertToEntityAttribute("Monday "))
                .isInstanceOf(DateTimeParseException.class);
    }

    @DisplayName("a locale, when given, is what names the column, and the standalone form is what it writes")
    @Test
    void __locale() {
        final var korean = new __TemporalAccessorTextStringAttributeConverters.OfDayOfWeek(
                TextStyle.FULL_STANDALONE, Locale.KOREAN);
        assertThat(korean.convertToDatabaseColumn(DayOfWeek.FRIDAY)).isEqualTo("금요일");
        assertThat(korean.convertToEntityAttribute("금요일")).isEqualTo(DayOfWeek.FRIDAY);
        assertThatThrownBy(() -> korean.convertToEntityAttribute("Friday"))
                .isInstanceOf(DateTimeParseException.class);
        // Russian inflects a month inside a date; the standalone form is the bare one
        final var russian = new __TemporalAccessorTextStringAttributeConverters.OfMonth(
                TextStyle.FULL_STANDALONE, Locale.forLanguageTag("ru"));
        assertThat(russian.convertToDatabaseColumn(Month.MAY)).isEqualTo("май");
        assertThat(russian.convertToEntityAttribute("май")).isEqualTo(Month.MAY);
    }

    @DisplayName("a short style stores the abbreviation")
    @Test
    void __shortStyle() {
        final var converter = new __TemporalAccessorTextStringAttributeConverters.OfDayOfWeek(
                TextStyle.SHORT_STANDALONE, Locale.ENGLISH);
        assertThat(converter.convertToDatabaseColumn(DayOfWeek.MONDAY)).isEqualTo("Mon");
        assertThat(converter.convertToEntityAttribute("mon")).isEqualTo(DayOfWeek.MONDAY);
        // the full name is not the abbreviation
        assertThatThrownBy(() -> converter.convertToEntityAttribute("Monday"))
                .isInstanceOf(DateTimeParseException.class);
    }

    @DisplayName("a null style or locale is refused at construction")
    @Test
    void __nullArguments() {
        assertThatThrownBy(() -> new __TemporalAccessorTextStringAttributeConverters.OfDayOfWeek(null, Locale.ENGLISH))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new __TemporalAccessorTextStringAttributeConverters.OfMonth(TextStyle.FULL, null))
                .isInstanceOf(NullPointerException.class);
    }
}
