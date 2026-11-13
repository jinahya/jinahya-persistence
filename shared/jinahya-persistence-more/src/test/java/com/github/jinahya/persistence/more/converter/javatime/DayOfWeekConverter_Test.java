package com.github.jinahya.persistence.more.converter.javatime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link DayOfWeekConverter.OfString}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("OfDayOfWeek")
@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class DayOfWeekConverter_Test {

    private final DayOfWeekConverter.OfString converter =
            new DayOfWeekConverter.OfString();

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
