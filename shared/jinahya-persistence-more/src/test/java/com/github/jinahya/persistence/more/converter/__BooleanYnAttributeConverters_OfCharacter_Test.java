package com.github.jinahya.persistence.more.converter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link __BooleanYnAttributeConverters.OfCharacter}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("__BooleanYnAttributeConverters.OfCharacter")
@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class __BooleanYnAttributeConverters_OfCharacter_Test {

    private final __BooleanYnAttributeConverters.OfCharacter converter =
            new __BooleanYnAttributeConverters.OfCharacter();

    @DisplayName("null <-> null")
    @Test
    void __null() {
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }

    @DisplayName("the read is case-insensitive")
    @ParameterizedTest
    @CsvSource({"Y,true", "y,true", "N,false", "n,false"})
    void __eitherCase(final char dbData, final boolean expected) {
        assertThat(converter.convertToEntityAttribute(dbData)).isEqualTo(expected);
    }

    @DisplayName("anything which is neither is rejected rather than read as false")
    @ParameterizedTest
    @ValueSource(chars = {'T', 'F', '1', '0', ' ', 'X'})
    void __otherRejected(final char dbData) {
        assertThatThrownBy(() -> converter.convertToEntityAttribute(dbData))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(String.valueOf(dbData));
    }
}
