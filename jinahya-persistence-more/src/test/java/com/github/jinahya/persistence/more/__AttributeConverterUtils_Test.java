package com.github.jinahya.persistence.more;

import jakarta.persistence.AttributeConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A class for testing {@link __AttributeConverterUtils}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class __AttributeConverterUtils_Test {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * A nested class for testing
     * {@link __AttributeConverterUtils#using(java.util.function.Function, java.util.function.Function)}
     */
    @DisplayName("using(toDatabaseColumn, toEntityColumn)")
    @Nested
    class Using_Test {

        @Test
        void __() {
            final var converter = __AttributeConverterUtils.<Integer, String>using(
                    i -> Optional.ofNullable(i).map(Object::toString).orElse(null),
                    s -> Optional.ofNullable(s).map(Integer::valueOf).orElse(null)
            );
            assertThat(converter.convertToDatabaseColumn(null)).isNull();
            assertThat(converter.convertToDatabaseColumn(1)).isEqualTo("1");
            assertThat(converter.convertToEntityAttribute(null)).isNull();
            assertThat(converter.convertToEntityAttribute("1")).isOne();
        }
    }

    /**
     * A nested class for testing {@link __AttributeConverterUtils#chaining(AttributeConverter, AttributeConverter)}
     */
    @DisplayName("chaining(attributeConverter1, attributeConverter2)")
    @Nested
    class Chaining_Test {

        @Test
        void __() {
            final var converter1 = __AttributeConverterUtils.<Integer, String>using(
                    i -> Optional.ofNullable(i).map(Object::toString).orElse(null),
                    s -> Optional.ofNullable(s).map(Integer::valueOf).orElse(null)
            );
            final var converter2 = __AttributeConverterUtils.<String, Integer>using(
                    s -> Optional.ofNullable(s).map(Integer::valueOf).orElse(null),
                    i -> Optional.ofNullable(i).map(Object::toString).orElse(null)
            );
            final var converter = __AttributeConverterUtils.<Integer, String, Integer>chaining(converter1, converter2);
            assertThat(converter.convertToDatabaseColumn(null)).isNull();
            assertThat(converter.convertToDatabaseColumn(1)).isOne();
            assertThat(converter.convertToEntityAttribute(null)).isNull();
            assertThat(converter.convertToEntityAttribute(1)).isOne();
        }
    }
}
