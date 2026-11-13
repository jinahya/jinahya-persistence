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

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class __JoinedStringAttributeConverter_Test {

    private static __JoinedStringAttributeConverter<String> comma() {
        return new __JoinedStringAttributeConverter.__OfStrings(",");
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("a delimiter is a literal, not a regex")
    @Nested
    class LiteralDelimiter_Test {

        @DisplayName("'|' is not an alternation")
        @Test
        void __pipe() {
            final var converter = new __JoinedStringAttributeConverter.__OfStrings("|");
            final var attribute = Arrays.asList("a", "b");
            assertThat(converter.convertToDatabaseColumn(attribute)).isEqualTo("a|b");
            assertThat(converter.convertToEntityAttribute("a|b")).containsExactly("a", "b");
        }

        @DisplayName("'.' is not a wildcard")
        @Test
        void __dot() {
            final var converter = new __JoinedStringAttributeConverter.__OfStrings(".");
            final var attribute = Arrays.asList("a", "b");
            assertThat(converter.convertToDatabaseColumn(attribute)).isEqualTo("a.b");
            assertThat(converter.convertToEntityAttribute("a.b")).containsExactly("a", "b");
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("corrupting values are rejected, not silently written")
    @Nested
    class Rejection_Test {

        @DisplayName("an element containing the delimiter -> IllegalArgumentException")
        @Test
        void _IllegalArgumentException_ElementHoldsDelimiter() {
            final var converter = comma();
            assertThatThrownBy(() -> converter.convertToDatabaseColumn(List.of("a,b")))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("joiningDelimiter");
        }

        @DisplayName("an element converting to null -> IllegalArgumentException")
        @Test
        void _IllegalArgumentException_ElementConvertsToNull() {
            final var converter = comma();
            assertThatThrownBy(() -> converter.convertToDatabaseColumn(Arrays.asList("a", null)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("converted to null");
        }

        @DisplayName("a delimiter forming across an element boundary -> IllegalArgumentException")
        @Test
        void _IllegalArgumentException_DelimiterStraddlesABoundary() {
            // no element contains "||", so the per-element guard passes, yet joining produces
            // "|" + "||" + "x" == "|||x", which splits back into ["", "|x"]
            final var converter = new __JoinedStringAttributeConverter.__OfStrings("||");
            assertThatThrownBy(() -> converter.convertToDatabaseColumn(Arrays.asList("|", "x")))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("an EMPTY delimiter is rejected by the constructor")
        @Test
        void _IllegalArgumentException_EmptyDelimiter() {
            // String.contains("") is always true, so every element would trip the per-element
            // guard; and Pattern.quote("") matches at every position, so a read splits per character
            assertThatThrownBy(() -> new __JoinedStringAttributeConverter.__OfStrings(""))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("a whitespace delimiter is NOT rejected; only the empty string is")
        @Test
        void __whitespaceDelimiterStillAllowed() {
            final var converter = new __JoinedStringAttributeConverter.__OfStrings(" ");
            final var attribute = Arrays.asList("a", "b");
            assertThat(converter.convertToDatabaseColumn(attribute)).isEqualTo("a b");
            assertThat(converter.convertToEntityAttribute("a b")).containsExactly("a", "b");
        }

        @DisplayName("a self-overlapping delimiter is fine when no element straddles the seam")
        @Test
        void __overlappingDelimiterStillWorksWhenSafe() {
            // "||" overlaps itself, but ["x", "|"] never forms a spurious match: the premature
            // match has to begin inside the element PRECEDING a separator
            final var converter = new __JoinedStringAttributeConverter.__OfStrings("||");
            final var attribute = Arrays.asList("x", "|");
            final var column = converter.convertToDatabaseColumn(attribute);
            assertThat(column).isEqualTo("x|||");
            assertThat(converter.convertToEntityAttribute(column)).containsExactlyElementsOf(attribute);
        }

        @DisplayName("'aba' straddles too, not just '||'")
        @Test
        void _IllegalArgumentException_AbaDelimiter() {
            final var converter = new __JoinedStringAttributeConverter.__OfStrings("aba");
            assertThatThrownBy(() -> converter.convertToDatabaseColumn(Arrays.asList("ab", "x")))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("nothing is dropped silently; pre-filter the list instead")
        @Test
        void __noSilentDrop() {
            final var converter = comma();
            final var attribute = Arrays.asList("a", null, "b");
            assertThatThrownBy(() -> converter.convertToDatabaseColumn(attribute))
                    .isInstanceOf(IllegalArgumentException.class);
            final var filtered = attribute.stream().filter(java.util.Objects::nonNull).toList();
            assertThat(converter.convertToDatabaseColumn(filtered)).isEqualTo("a,b");
        }
    }
}
