package com.github.jinahya.persistence.more.converter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link __BooleanYnAttributeConverters.OfCharacterLenient}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("__BooleanYnAttributeConverters.OfCharacterLenient")
@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class __BooleanYnAttributeConverters_OfCharacterLenient_Test {

    private final __BooleanYnAttributeConverters.OfCharacterLenient converter =
            new __BooleanYnAttributeConverters.OfCharacterLenient();

    @DisplayName("null <-> null")
    @Test
    void __null() {
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }

    @DisplayName("a yes, in either case, is true")
    @ParameterizedTest
    @ValueSource(chars = {'Y', 'y'})
    void __yesIsTrue(final char dbData) {
        assertThat(converter.convertToEntityAttribute(dbData)).isTrue();
    }

    @DisplayName("everything else is false, including what the strict converter would reject")
    @ParameterizedTest
    @ValueSource(chars = {'N', 'n', 'T', 't', 'F', 'f', '1', '0', ' ', 'X'})
    void __everythingElseIsFalse(final char dbData) {
        assertThat(converter.convertToEntityAttribute(dbData)).isFalse();
    }

    @DisplayName("the write is exactly what the strict converter writes")
    @Test
    void __writesWhatStrictWrites() {
        final var strict = new __BooleanYnAttributeConverters.OfCharacter();
        for (final var attribute : new Boolean[]{Boolean.TRUE, Boolean.FALSE, null}) {
            assertThat(converter.convertToDatabaseColumn(attribute))
                    .isEqualTo(strict.convertToDatabaseColumn(attribute));
        }
        assertThat(converter.convertToDatabaseColumn(Boolean.TRUE)).isEqualTo('Y');
        assertThat(converter.convertToDatabaseColumn(Boolean.FALSE)).isEqualTo('N');
    }

    @DisplayName("an unrecognized row is written back as a no, which is the cost of never failing")
    @ParameterizedTest
    @CsvSource({"X,N", "' ',N", "y,Y"})
    void __unrecognizedIsWrittenBackAsNo(final char dbData, final char expected) {
        assertThat(converter.convertToDatabaseColumn(converter.convertToEntityAttribute(dbData)))
                .isEqualTo(expected);
    }
}
