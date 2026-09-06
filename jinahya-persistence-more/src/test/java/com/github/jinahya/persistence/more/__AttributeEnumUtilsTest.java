package com.github.jinahya.persistence.more;

/*-
 * #%L
 * jinahya-persistence-more
 * %%
 * Copyright (C) 2024 - 2025 Jinahya, Inc.
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

import static com.github.jinahya.persistence.more.__AttributeEnumUtils.find;
import static com.github.jinahya.persistence.more.__AttributeEnumUtils.valueOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings({
        "java:S114", // Interface names should comply with a naming convention
        "java:S3577" // Test classes should comply with a naming convention
})
class __AttributeEnumUtilsTest {

    // -----------------------------------------------------------------------------------------------------------------
    @SuppressWarnings({
            "java:S114", // Interface names should comply with a naming convention
            "java:S119" // Type parameter names should comply with a naming convention
    })
    private interface __SomeAttributeEnum<E extends Enum<E> & __SomeAttributeEnum<E>>
            extends __AttributeEnum.__OfString<E> {

    }

    private enum _SomeAttributeEnum1 implements __SomeAttributeEnum<_SomeAttributeEnum1> {

        A,

        B,
    }

    private enum _SomeAttributeEnum2 implements __SomeAttributeEnum<_SomeAttributeEnum2> {

        B,

        C,
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("valueOf(enumClass, attributeValue)")
    @Nested
    class ValueOf_WithSingleEnumClass_Test {

        @EnumSource(_SomeAttributeEnum1.class)
        @ParameterizedTest
        void __1(final _SomeAttributeEnum1 enumConstant) {
            final var actual = valueOf(
                    _SomeAttributeEnum1.class,
                    enumConstant.attributeValue()
            );
            assertThat(actual).isSameAs(enumConstant);
        }

        @Test
        void _IllegalArgumentException_1C() {
            assertThatThrownBy(() -> {
                valueOf(
                        _SomeAttributeEnum1.class,
                        "C"
                );
            }).isInstanceOf(IllegalArgumentException.class);
        }

        @EnumSource(_SomeAttributeEnum2.class)
        @ParameterizedTest
        void __2(final _SomeAttributeEnum2 enumConstant) {
            final var actual = valueOf(
                    _SomeAttributeEnum2.class,
                    enumConstant.attributeValue()
            );
            assertThat(actual).isSameAs(enumConstant);
        }

        @Test
        void _IllegalArgumentException_2A() {
            assertThatThrownBy(() -> {
                valueOf(
                        _SomeAttributeEnum2.class,
                        "A"
                );
            }).isInstanceOf(IllegalArgumentException.class);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("valueOf(enumClasses, attributeValue)")
    @Nested
    class ValueOf_WithMultipleEnumClasses_Test {

        private static final List<Class<? extends __SomeAttributeEnum<?>>> ENUM_CLASSES =
                List.of(
                        _SomeAttributeEnum1.class,
                        _SomeAttributeEnum2.class
                );

        @DisplayName("(1, 2, A) -> 1.A")
        @Test
        void __A() {
            final var actual = valueOf(ENUM_CLASSES, "A");
            assertThat(actual).isSameAs(_SomeAttributeEnum1.A);
        }

        @DisplayName("(1, 2, B) -> 1.B")
        @Test
        void __B1() {
            final var actual = valueOf(ENUM_CLASSES, "B");
            assertThat(actual).isSameAs(_SomeAttributeEnum1.B);
        }

        @DisplayName("(2, 1, B) -> 2.B")
        @Test
        void __B2() {
            final var actual = valueOf(ENUM_CLASSES.reversed(), "B");
            assertThat(actual).isSameAs(_SomeAttributeEnum2.B);
        }

        @DisplayName("(1, 2, C) -> 2.C")
        @Test
        void __C() {
            final var actual = valueOf(ENUM_CLASSES, "C");
            assertThat(actual).isSameAs(_SomeAttributeEnum2.C);
        }

        @DisplayName("(1, 2, D) -> IllegalArgumentException")
        @Test
        void __D() {
            assertThatThrownBy(() -> valueOf(ENUM_CLASSES, "D"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        // -------------------------------------------------------------------------------------------------------------
        @SuppressWarnings({"rawtypes"})
        private interface __SomeOtherAttributeEnum extends __SomeAttributeEnum {

        }

        @Test
        @SuppressWarnings({"unchecked", "rawtypes"})
        void __X() {
            final var enumClasses = List.of(
                    _SomeAttributeEnum1.class,
                    _SomeAttributeEnum2.class,
                    __SomeOtherAttributeEnum.class
            );
            assertThatThrownBy(() -> {
                __AttributeEnumUtils.<__SomeAttributeEnum, Object>valueOf(enumClasses, "X");
            }).isInstanceOf(IllegalArgumentException.class);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("find(...)")
    @Nested
    class Find_Test {

        private static final List<Class<? extends __SomeAttributeEnum<?>>> ENUM_CLASSES =
                List.of(
                        _SomeAttributeEnum1.class,
                        _SomeAttributeEnum2.class
                );

        @DisplayName("find(enumClass, known) -> present")
        @Test
        void __single() {
            assertThat(find(_SomeAttributeEnum1.class, "A")).hasValue(_SomeAttributeEnum1.A);
        }

        @DisplayName("find(enumClass, unknown) -> empty, not thrown")
        @Test
        void _Empty_SingleUnknown() {
            assertThat(find(_SomeAttributeEnum1.class, "C")).isEmpty();
        }

        @DisplayName("find(enumClasses, known) -> present, first match wins")
        @Test
        void __multiple() {
            assertThat(find(ENUM_CLASSES, "B")).hasValue(_SomeAttributeEnum1.B);
            assertThat(find(ENUM_CLASSES.reversed(), "B")).hasValue(_SomeAttributeEnum2.B);
        }

        @DisplayName("find(enumClasses, unknown) -> empty, not thrown")
        @Test
        void _Empty_MultipleUnknown() {
            assertThat(find(ENUM_CLASSES, "D")).isEmpty();
        }

        @DisplayName("find(emptyEnumClasses, ...) -> IllegalArgumentException")
        @Test
        void _IllegalArgumentException_Empty() {
            assertThatThrownBy(() -> find(List.<Class<? extends __SomeAttributeEnum<?>>>of(), "A"))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("invalid attribute values")
    @Nested
    class InvalidAttributeValues_Test {

        private enum _DuplicateAttributeValueEnum
                implements __AttributeEnum<_DuplicateAttributeValueEnum, String> {

            A,

            B;

            @Override
            public String attributeValue() {
                return "SAME";
            }
        }

        private enum _NullAttributeValueEnum
                implements __AttributeEnum<_NullAttributeValueEnum, String> {

            A;

            @Override
            public String attributeValue() {
                return null;
            }
        }

        @DisplayName("two constants carrying the same attribute value -> IllegalArgumentException")
        @Test
        void _IllegalArgumentException_DuplicateAttributeValues() {
            assertThatThrownBy(() -> valueOf(_DuplicateAttributeValueEnum.class, "SAME"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("duplicate attributeValue");
        }

        @DisplayName("a constant carrying a null attribute value -> IllegalArgumentException")
        @Test
        void _IllegalArgumentException_NullAttributeValue() {
            assertThatThrownBy(() -> valueOf(_NullAttributeValueEnum.class, "A"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null attributeValue");
        }

        @DisplayName("find() is absent-tolerant, but a malformed enum still throws")
        @Test
        void _IllegalArgumentException_FindDoesNotSwallow() {
            assertThatThrownBy(() -> find(_DuplicateAttributeValueEnum.class, "SAME"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("duplicate attributeValue");
            assertThatThrownBy(() -> find(_NullAttributeValueEnum.class, "A"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null attributeValue");
        }

        @DisplayName("an invalid enum fails on every lookup, not just the first")
        @Test
        void _IllegalArgumentException_NotCached() {
            for (int i = 0; i < 2; i++) {
                assertThatThrownBy(() -> valueOf(_DuplicateAttributeValueEnum.class, "SAME"))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("repeated lookups")
    @Nested
    class RepeatedLookups_Test {

        @DisplayName("a second lookup resolves to the same constant")
        @EnumSource(_SomeAttributeEnum1.class)
        @ParameterizedTest
        void __SameConstant(final _SomeAttributeEnum1 enumConstant) {
            final var attributeValue = enumConstant.attributeValue();
            final var first = valueOf(_SomeAttributeEnum1.class, attributeValue);
            final var second = valueOf(_SomeAttributeEnum1.class, attributeValue);
            assertThat(first).isSameAs(enumConstant);
            assertThat(second).isSameAs(first);
        }
    }
}
