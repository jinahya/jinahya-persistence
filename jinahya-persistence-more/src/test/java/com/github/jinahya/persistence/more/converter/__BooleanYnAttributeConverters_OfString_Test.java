package com.github.jinahya.persistence.more.converter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link __BooleanYnAttributeConverters.OfString}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("__BooleanYnAttributeConverters.OfString")
@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class __BooleanYnAttributeConverters_OfString_Test {

    private final __BooleanYnAttributeConverters.OfString converter = new __BooleanYnAttributeConverters.OfString();

    @DisplayName("null <-> null")
    @Test
    void __null() {
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }

    @DisplayName("true is \"Y\" and false is \"N\"")
    @Test
    void __write() {
        assertThat(converter.convertToDatabaseColumn(Boolean.TRUE)).isEqualTo("Y");
        assertThat(converter.convertToDatabaseColumn(Boolean.FALSE)).isEqualTo("N");
    }

    @DisplayName("the read is case-insensitive")
    @ParameterizedTest
    @CsvSource({"Y,true", "y,true", "N,false", "n,false"})
    void __eitherCase(final String dbData, final boolean expected) {
        assertThat(converter.convertToEntityAttribute(dbData)).isEqualTo(expected);
    }

    @DisplayName("a string of any length but one is rejected, so \"YES\" is not a yes")
    @ParameterizedTest
    @ValueSource(strings = {"", "YES", "NO", "yes", "Y ", " Y", "YY"})
    void __lengthRejected(final String dbData) {
        assertThatThrownBy(() -> converter.convertToEntityAttribute(dbData))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("a single character which is neither is rejected rather than read as false")
    @ParameterizedTest
    @ValueSource(strings = {"T", "F", "1", "0", " ", "X"})
    void __otherRejected(final String dbData) {
        assertThatThrownBy(() -> converter.convertToEntityAttribute(dbData))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("it writes and reads exactly what the character converter does, one character wide")
    @Test
    void __agreesWithTheCharacterConverter() {
        final var character = new __BooleanYnAttributeConverters.OfCharacter();
        for (final var attribute : new Boolean[]{Boolean.TRUE, Boolean.FALSE}) {
            assertThat(converter.convertToDatabaseColumn(attribute))
                    .isEqualTo(String.valueOf(character.convertToDatabaseColumn(attribute)));
        }
        for (final var dbData : new String[]{"Y", "y", "N", "n"}) {
            assertThat(converter.convertToEntityAttribute(dbData))
                    .isEqualTo(character.convertToEntityAttribute(dbData.charAt(0)));
        }
    }
}
