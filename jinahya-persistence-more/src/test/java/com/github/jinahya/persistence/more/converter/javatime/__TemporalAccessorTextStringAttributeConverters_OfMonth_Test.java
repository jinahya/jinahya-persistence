package com.github.jinahya.persistence.more.converter.javatime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link __TemporalAccessorTextStringAttributeConverters.OfMonth}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("OfMonth")
@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class __TemporalAccessorTextStringAttributeConverters_OfMonth_Test {

    private final __TemporalAccessorTextStringAttributeConverters.OfMonth converter =
            new __TemporalAccessorTextStringAttributeConverters.OfMonth();

    @DisplayName("every month is stored as its full English name, and read back")
    @ParameterizedTest
    @EnumSource(Month.class)
    void __roundTrip(final Month value) {
        final var column = converter.convertToDatabaseColumn(value);
        assertThat(column).isEqualTo(value.getDisplayName(TextStyle.FULL_STANDALONE, Locale.ENGLISH));
        assertThat(converter.convertToEntityAttribute(column)).isSameAs(value);
    }

    @DisplayName("the upper-cased constant name is read, too")
    @ParameterizedTest
    @EnumSource(Month.class)
    void __constantName(final Month value) {
        assertThat(converter.convertToEntityAttribute(value.name())).isSameAs(value);
    }
}
