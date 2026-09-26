package com.github.jinahya.persistence.more.converter;

/*-
 * #%L
 * jinahya-persistence-more
 * %%
 * Copyright (C) 2025 - 2026 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class __JoinedStringAttributeConverter_Test {

    // the class is abstract, and a list of strings needs no element conversion
    private static __JoinedStringAttributeConverter<String> of(final String delimiter) {
        return new __JoinedStringAttributeConverter<>(delimiter, __AttributeConverterUtils.identity()) {
        };
    }

    private static __JoinedStringAttributeConverter<String> comma() {
        return of(",");
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("round trips")
    @Nested
    class RoundTrip_Test {

        @DisplayName("null <-> null")
        @Test
        void __null() {
            final var converter = comma();
            assertThat(converter.convertToDatabaseColumn(null)).isNull();
            assertThat(converter.convertToEntityAttribute(null)).isNull();
        }

        @DisplayName("an empty column splits into one empty element, not an empty list")
        @Test
        void __emptyColumn() {
            final var converter = comma();
            assertThat(converter.convertToEntityAttribute("")).containsExactly("");
        }

        @DisplayName("an empty list writes an empty column, and does NOT survive a round trip")
        @Test
        void __emptyList() {
            final var converter = comma();
            final var column = converter.convertToDatabaseColumn(new ArrayList<>());
            assertThat(column).isEmpty();
            // the delimited form cannot tell no elements from one empty element; the caller owns this
            assertThat(converter.convertToEntityAttribute(column)).containsExactly("");
        }

        @DisplayName("a single empty element survives a round trip")
        @Test
        void __singleEmptyElement() {
            final var converter = comma();
            final var attribute = List.of("");
            assertThat(converter.convertToEntityAttribute(converter.convertToDatabaseColumn(attribute)))
                    .containsExactlyElementsOf(attribute);
        }

        @DisplayName("trailing empty elements survive a round trip")
        @Test
        void __trailingEmpties() {
            final var converter = comma();
            final var attribute = Arrays.asList("a", "", "");
            final var column = converter.convertToDatabaseColumn(attribute);
            assertThat(column).isEqualTo("a,,");
            assertThat(converter.convertToEntityAttribute(column)).containsExactly("a", "", "");
        }

        @DisplayName("order and duplicates survive a round trip")
        @Test
        void __orderAndDuplicates() {
            final var converter = comma();
            final var attribute = Arrays.asList("b", "a", "b");
            assertThat(converter.convertToEntityAttribute(converter.convertToDatabaseColumn(attribute)))
                    .containsExactlyElementsOf(attribute);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("a delimiter is a literal, not a regex")
    @Nested
    class LiteralDelimiter_Test {

        @DisplayName("'|' is not an alternation")
        @Test
        void __pipe() {
            final var converter = of("|");
            final var attribute = Arrays.asList("a", "b");
            assertThat(converter.convertToDatabaseColumn(attribute)).isEqualTo("a|b");
            assertThat(converter.convertToEntityAttribute("a|b")).containsExactly("a", "b");
        }

        @DisplayName("'.' is not a wildcard")
        @Test
        void __dot() {
            final var converter = of(".");
            final var attribute = Arrays.asList("a", "b");
            assertThat(converter.convertToDatabaseColumn(attribute)).isEqualTo("a.b");
            assertThat(converter.convertToEntityAttribute("a.b")).containsExactly("a", "b");
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("an EMPTY delimiter is rejected by the constructor")
    @Nested
    class EmptyDelimiter_Test {

        @DisplayName("\"\" -> IllegalArgumentException")
        @Test
        void _IllegalArgumentException_EmptyDelimiter() {
            // Pattern.quote("") matches at every position, so a read would split a column per character
            assertThatThrownBy(() -> of(""))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("a whitespace delimiter is NOT rejected; only the empty string is")
        @Test
        void __whitespaceDelimiterStillAllowed() {
            final var converter = of(" ");
            final var attribute = Arrays.asList("a", "b");
            assertThat(converter.convertToDatabaseColumn(attribute)).isEqualTo("a b");
            assertThat(converter.convertToEntityAttribute("a b")).containsExactly("a", "b");
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("the contract is the caller's to keep; nothing is validated")
    @Nested
    class Contract_Test {

        @DisplayName("an element converting to null is silently dropped, and the list shrinks")
        @Test
        void __nullElementDropped() {
            final var converter = comma();
            final var attribute = Arrays.asList("a", null, "b");
            final var column = converter.convertToDatabaseColumn(attribute);
            assertThat(column).isEqualTo("a,b");
            assertThat(converter.convertToEntityAttribute(column))
                    .hasSizeLessThan(attribute.size())
                    .containsExactly("a", "b");
        }

        @DisplayName("a list of nothing but nulls writes an empty column")
        @Test
        void __allNullElementsDropped() {
            final var converter = comma();
            final var column = converter.convertToDatabaseColumn(Arrays.asList(null, null, null));
            assertThat(column).isEmpty();
            assertThat(converter.convertToEntityAttribute(column)).containsExactly("");
        }

        @DisplayName("an element holding the delimiter is written as-is, and splits back into more elements")
        @Test
        void __elementHoldingDelimiterIsNotRejected() {
            final var converter = comma();
            final var column = converter.convertToDatabaseColumn(List.of("a,b"));
            assertThat(column).isEqualTo("a,b");
            assertThat(converter.convertToEntityAttribute(column)).containsExactly("a", "b");
        }

        @DisplayName("a self-overlapping delimiter can form across a seam, and is not rejected either")
        @Test
        void __delimiterStraddlingABoundaryIsNotRejected() {
            // no element holds "||", yet joining produces "|" + "||" + "x" == "|||x"
            final var converter = of("||");
            final var column = converter.convertToDatabaseColumn(Arrays.asList("|", "x"));
            assertThat(column).isEqualTo("|||x");
            assertThat(converter.convertToEntityAttribute(column)).containsExactly("", "|x");
        }

        @DisplayName("a self-overlapping delimiter is fine when no element straddles the seam")
        @Test
        void __overlappingDelimiterStillWorksWhenSafe() {
            // the premature match has to begin inside the element PRECEDING a separator
            final var converter = of("||");
            final var attribute = Arrays.asList("x", "|");
            final var column = converter.convertToDatabaseColumn(attribute);
            assertThat(column).isEqualTo("x|||");
            assertThat(converter.convertToEntityAttribute(column)).containsExactlyElementsOf(attribute);
        }

        @DisplayName("a delimiter which does not overlap itself round trips whenever no element holds it")
        @Test
        void __nonOverlappingDelimiterIsSafe() {
            final var converter = comma();
            final var attribute = Arrays.asList("a", "", "b c", "d");
            assertThat(converter.convertToEntityAttribute(converter.convertToDatabaseColumn(attribute)))
                    .containsExactlyElementsOf(attribute);
        }
    }
}
