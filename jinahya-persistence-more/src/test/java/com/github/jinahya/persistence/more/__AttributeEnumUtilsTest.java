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

import static com.github.jinahya.persistence.more.__AttributeEnumUtils.valueOfAttributeValue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings({
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
    @DisplayName("valueOfAttributeValue(enumClass, attributeValue)")
    @Nested
    class ValueOfAttributeValue_WithSingleEnumClass_Test {

        @EnumSource(_SomeAttributeEnum1.class)
        @ParameterizedTest
        void __1(final _SomeAttributeEnum1 enumConstant) {
            final var actual = valueOfAttributeValue(
                    _SomeAttributeEnum1.class,
                    enumConstant.attributeValue()
            );
            assertThat(actual).isSameAs(enumConstant);
        }

        @Test
        void _IllegalArgumentException_1C() {
            assertThatThrownBy(() -> {
                valueOfAttributeValue(
                        _SomeAttributeEnum1.class,
                        "C"
                );
            }).isInstanceOf(IllegalArgumentException.class);
        }

        @EnumSource(_SomeAttributeEnum2.class)
        @ParameterizedTest
        void __2(final _SomeAttributeEnum2 enumConstant) {
            final var actual = valueOfAttributeValue(
                    _SomeAttributeEnum2.class,
                    enumConstant.attributeValue()
            );
            assertThat(actual).isSameAs(enumConstant);
        }

        @Test
        void _IllegalArgumentException_2A() {
            assertThatThrownBy(() -> {
                valueOfAttributeValue(
                        _SomeAttributeEnum2.class,
                        "A"
                );
            }).isInstanceOf(IllegalArgumentException.class);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("valueOfAttributeValue(attributeValue, enumClasses)")
    @Nested
    class ValueOfAttributeValue_WithMultipleEnumClasses_Test {

        private static final List<Class<? extends __SomeAttributeEnum<?>>> ENUM_CLASSES =
                List.of(
                        _SomeAttributeEnum1.class,
                        _SomeAttributeEnum2.class
                );

        @DisplayName("(a, 1, 2) -> 1.A")
        @Test
        void __A() {
            final var actual = valueOfAttributeValue("A", ENUM_CLASSES);
            assertThat(actual).isSameAs(_SomeAttributeEnum1.A);
        }

        @DisplayName("(B, 1, 2) -> 1.B")
        @Test
        void __B1() {
            final var actual = valueOfAttributeValue("B", ENUM_CLASSES);
            assertThat(actual).isSameAs(_SomeAttributeEnum1.B);
        }

        @DisplayName("(B, 2, 1) -> 2.B")
        @Test
        void __B2() {
            final var actual = valueOfAttributeValue("B", ENUM_CLASSES.reversed());
            assertThat(actual).isSameAs(_SomeAttributeEnum2.B);
        }

        @DisplayName("(C, 1, 2) -> 2.C")
        @Test
        void __C() {
            final var actual = valueOfAttributeValue("C", ENUM_CLASSES);
            assertThat(actual).isSameAs(_SomeAttributeEnum2.C);
        }

        @DisplayName("(D, , ) -> IllegalArgumentException")
        @Test
        void __D() {
            assertThatThrownBy(() -> valueOfAttributeValue("D", ENUM_CLASSES))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        // -------------------------------------------------------------------------------------------------------------
        @SuppressWarnings({"unchecked"})
        private interface __SomeOtherAttributeEnum extends __SomeAttributeEnum {

        }

        @Test
        void __X() {
            assertThatThrownBy(() -> {
                __AttributeEnumUtils.<__SomeAttributeEnum, Object>valueOfAttributeValue(
                        "X",
                        List.of(
                                _SomeAttributeEnum1.class,
                                _SomeAttributeEnum2.class,
                                __SomeOtherAttributeEnum.class
                        )
                );
            }).isInstanceOf(IllegalArgumentException.class);
        }
    }
}
