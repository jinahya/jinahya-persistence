package com.github.jinahya.persistence.more.converter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link __BooleanYnAttributeConverters.OfStringLenient}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("__BooleanYnAttributeConverters.OfStringLenient")
@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class __BooleanYnAttributeConverters_OfStringLenient_Test {

    private final __BooleanYnAttributeConverters.OfStringLenient converter =
            new __BooleanYnAttributeConverters.OfStringLenient();

    @DisplayName("null <-> null")
    @Test
    void __null() {
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }

    @DisplayName("a yes, in either case, is true")
    @ParameterizedTest
    @ValueSource(strings = {"Y", "y"})
    void __yesIsTrue(final String dbData) {
        assertThat(converter.convertToEntityAttribute(dbData)).isTrue();
    }

    @DisplayName("everything else is false, including what the strict converter would reject")
    @ParameterizedTest
    @ValueSource(strings = {"N", "n", "T", "F", "1", "0", " ", "X", "", "YES", "yes", "Y "})
    void __everythingElseIsFalse(final String dbData) {
        assertThat(converter.convertToEntityAttribute(dbData)).isFalse();
    }

    @DisplayName("the write is exactly what the strict converter writes")
    @Test
    void __writesWhatStrictWrites() {
        final var strict = new __BooleanYnAttributeConverters.OfString();
        for (final var attribute : new Boolean[]{Boolean.TRUE, Boolean.FALSE, null}) {
            assertThat(converter.convertToDatabaseColumn(attribute))
                    .isEqualTo(strict.convertToDatabaseColumn(attribute));
        }
        assertThat(converter.convertToDatabaseColumn(Boolean.TRUE)).isEqualTo("Y");
        assertThat(converter.convertToDatabaseColumn(Boolean.FALSE)).isEqualTo("N");
    }

    @DisplayName("an unrecognized row is written back as a no, which is the cost of never failing")
    @ParameterizedTest
    @CsvSource({"X,N", "YES,N", "'',N", "y,Y"})
    void __unrecognizedIsWrittenBackAsNo(final String dbData, final String expected) {
        assertThat(converter.convertToDatabaseColumn(converter.convertToEntityAttribute(dbData)))
                .isEqualTo(expected);
    }

    @DisplayName("it reads exactly as the lenient character converter does, one character wide")
    @ParameterizedTest
    @ValueSource(strings = {"Y", "y", "N", "n", "T", "0", " ", "X"})
    void __agreesWithTheLenientCharacterConverter(final String dbData) {
        final var character = new __BooleanYnAttributeConverters.OfCharacterLenient();
        assertThat(converter.convertToEntityAttribute(dbData))
                .isEqualTo(character.convertToEntityAttribute(dbData.charAt(0)));
    }
}
