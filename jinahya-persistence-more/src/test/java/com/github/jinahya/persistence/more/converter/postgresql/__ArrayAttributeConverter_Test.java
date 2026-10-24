package com.github.jinahya.persistence.more.converter.postgresql;

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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class __ArrayAttributeConverter_Test {

    private static __ArrayAttributeConverters.OfStrings strings() {
        return new __ArrayAttributeConverters.OfStrings();
    }

    private static __ArrayAttributeConverters.OfIntegers integers() {
        return new __ArrayAttributeConverters.OfIntegers();
    }

    /**
     * Returns a {@link java.sql.Array} which answers {@link java.sql.Array#getArray()} with the specified array and
     * nothing else, or whose {@code getArray()} throws the specified exception.
     */
    private static java.sql.Array sqlArray(final Object array, final SQLException thrown) {
        final InvocationHandler handler = (proxy, method, args) -> {
            if ("getArray".equals(method.getName()) && (args == null || args.length == 0)) {
                if (thrown != null) {
                    throw thrown;
                }
                return array;
            }
            if ("toString".equals(method.getName())) {
                return "sqlArray";
            }
            throw new UnsupportedOperationException(method.getName());
        };
        return (java.sql.Array) Proxy.newProxyInstance(
                __ArrayAttributeConverter_Test.class.getClassLoader(), new Class<?>[] {java.sql.Array.class}, handler);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("convertToDatabaseColumn: the literal")
    @Nested
    class ConvertToDatabaseColumn_Test {

        @Test
        void _Null_Null() {
            assertThat(strings().convertToDatabaseColumn(null)).isNull();
        }

        @Test
        void _EmptyBraces_EmptyList() {
            assertThat(strings().convertToDatabaseColumn(List.of())).isEqualTo("{}");
        }

        @Test
        void _Bare_PlainElements() {
            assertThat(integers().convertToDatabaseColumn(List.of(1, 2, 3))).isEqualTo("{1,2,3}");
            assertThat(strings().convertToDatabaseColumn(List.of("a", "b"))).isEqualTo("{a,b}");
        }

        @Test
        void _NULL_NullElement() {
            assertThat(integers().convertToDatabaseColumn(Arrays.asList(1, null, 3))).isEqualTo("{1,NULL,3}");
        }

        @DisplayName("quoted when the element would otherwise be misread")
        @Test
        void _Quoted_WhenNeeded() {
            final var converter = strings();
            assertThat(converter.convertToDatabaseColumn(List.of(""))).isEqualTo("{\"\"}");
            assertThat(converter.convertToDatabaseColumn(List.of("NULL"))).isEqualTo("{\"NULL\"}");
            assertThat(converter.convertToDatabaseColumn(List.of("null"))).isEqualTo("{\"null\"}");
            assertThat(converter.convertToDatabaseColumn(List.of("a b"))).isEqualTo("{\"a b\"}");
            assertThat(converter.convertToDatabaseColumn(List.of("a,b"))).isEqualTo("{\"a,b\"}");
            assertThat(converter.convertToDatabaseColumn(List.of("{a}"))).isEqualTo("{\"{a}\"}");
        }

        @DisplayName("a double quote and a backslash are escaped inside the quotes")
        @Test
        void _Escaped_QuoteAndBackslash() {
            assertThat(strings().convertToDatabaseColumn(List.of("say \"hi\"", "a\\b")))
                    .isEqualTo("{\"say \\\"hi\\\"\",\"a\\\\b\"}");
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("convertToEntityAttribute: from text")
    @Nested
    class ConvertToEntityAttribute_Text_Test {

        @Test
        void _Null_Null() {
            assertThat(strings().convertToEntityAttribute(null)).isNull();
        }

        @Test
        void _EmptyList_EmptyBraces() {
            assertThat(strings().convertToEntityAttribute("{}")).isEmpty();
            assertThat(strings().convertToEntityAttribute(" { } ")).isEmpty();
        }

        @Test
        void _Elements_Bare() {
            assertThat(integers().convertToEntityAttribute("{1,2,3}")).containsExactly(1, 2, 3);
            assertThat(integers().convertToEntityAttribute("{ 1 , 2 , 3 }")).containsExactly(1, 2, 3);
        }

        @DisplayName("a bare NULL, in any case, is a null element; a quoted one is text")
        @Test
        void _NullElement_BareNull() {
            assertThat(strings().convertToEntityAttribute("{a,NULL,null,\"NULL\"}"))
                    .containsExactly("a", null, null, "NULL");
        }

        @Test
        void _Unquoted_Quoted() {
            assertThat(strings().convertToEntityAttribute("{\"\",\"a b\",\"a,b\",\"{a}\"}"))
                    .containsExactly("", "a b", "a,b", "{a}");
        }

        @Test
        void _Unescaped_Escapes() {
            assertThat(strings().convertToEntityAttribute("{\"say \\\"hi\\\"\",\"a\\\\b\"}"))
                    .containsExactly("say \"hi\"", "a\\b");
        }

        @DisplayName("a nested array is rejected, not flattened")
        @Test
        void _IllegalArgumentException_Nested() {
            assertThatThrownBy(() -> integers().convertToEntityAttribute("{{1},{2}}"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void _IllegalArgumentException_NotALiteral() {
            assertThatThrownBy(() -> integers().convertToEntityAttribute("1,2,3"))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> integers().convertToEntityAttribute("{1,2,"))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> integers().convertToEntityAttribute("{1,,2}"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("the element converter's failure comes through")
        @Test
        void _NumberFormatException_NotAnInteger() {
            assertThatThrownBy(() -> integers().convertToEntityAttribute("{1,x}"))
                    .isInstanceOf(NumberFormatException.class);
        }

        @DisplayName("the result is modifiable")
        @Test
        void _Modifiable_() {
            final var list = integers().convertToEntityAttribute("{1}");
            list.add(2);
            assertThat(list).containsExactly(1, 2);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("convertToEntityAttribute: from what a provider hands over")
    @Nested
    class ConvertToEntityAttribute_Unpacked_Test {

        @DisplayName("a Java array, as EclipseLink passes it")
        @Test
        void _Elements_JavaArray() {
            assertThat(integers().convertToEntityAttribute(new Integer[] {1, null, 3})).containsExactly(1, null, 3);
            assertThat(integers().convertToEntityAttribute(new int[] {1, 2})).containsExactly(1, 2);
            assertThat(strings().convertToEntityAttribute(new String[] {"a b", null})).containsExactly("a b", null);
        }

        @DisplayName("a java.sql.Array, as Hibernate passes it")
        @Test
        void _Elements_SqlArray() {
            assertThat(integers().convertToEntityAttribute(sqlArray(new Integer[] {1, 2}, null)))
                    .containsExactly(1, 2);
        }

        @DisplayName("a java.sql.Array which cannot be unpacked -> IllegalArgumentException")
        @Test
        void _IllegalArgumentException_SqlArrayFails() {
            final var cause = new SQLException("boom");
            assertThatThrownBy(() -> integers().convertToEntityAttribute(sqlArray(null, cause)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasCause(cause);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("round trips")
    @Nested
    class RoundTrip_Test {

        @Test
        void __strings() {
            final var converter = strings();
            final var attribute = Arrays.asList("", "NULL", null, "a b", "a,b", "{", "}", "\"", "\\", "x\\\"y");
            final var dbData = converter.convertToDatabaseColumn(attribute);
            assertThat(converter.convertToEntityAttribute(dbData)).isEqualTo(attribute);
        }

        @Test
        void __integers() {
            final var converter = integers();
            final var attribute = Arrays.asList(Integer.MIN_VALUE, -1, null, 0, Integer.MAX_VALUE);
            final var dbData = converter.convertToDatabaseColumn(attribute);
            assertThat(dbData).isEqualTo("{-2147483648,-1,NULL,0,2147483647}");
            assertThat(converter.convertToEntityAttribute(dbData)).isEqualTo(attribute);
        }

        @Test
        void __longs() {
            final var converter = new __ArrayAttributeConverters.OfLongs();
            final var attribute = Arrays.asList(Long.MIN_VALUE, null, Long.MAX_VALUE);
            final var dbData = converter.convertToDatabaseColumn(attribute);
            assertThat(converter.convertToEntityAttribute(dbData)).isEqualTo(attribute);
        }
    }
}
