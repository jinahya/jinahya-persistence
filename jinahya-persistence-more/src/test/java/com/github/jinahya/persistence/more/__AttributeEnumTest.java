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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings({
        "java:S114", // Interface names should comply with a naming convention
        "java:S3577" // Test classes should comply with a naming convention
})
class __AttributeEnumTest {

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("__OfInteger")
    @Nested
    class OfInteger_Test {

        private enum _SomeIntegerAttributeEnum implements __AttributeEnum.__OfInteger<_SomeIntegerAttributeEnum> {

            A(11),

            B(22);

            _SomeIntegerAttributeEnum(final int attributeValue) {
                this.attributeValue = attributeValue;
            }

            @Override
            public Integer attributeValue() {
                return attributeValue;
            }

            private final Integer attributeValue;
        }

        @DisplayName("intValue() unboxes attributeValue()")
        @Test
        void intValue__() {
            assertThat(_SomeIntegerAttributeEnum.A.intValue()).isEqualTo(11);
            assertThat(_SomeIntegerAttributeEnum.B.intValue()).isEqualTo(22);
        }

        @DisplayName("valueOfAttributeValue(enumClass, int)")
        @Test
        void valueOfAttributeValue__() {
            assertThat(__AttributeEnum.__OfInteger.valueOfAttributeValue(_SomeIntegerAttributeEnum.class, 22))
                    .isSameAs(_SomeIntegerAttributeEnum.B);
        }

        @DisplayName("valueOfAttributeValue(enumClass, unknown) -> IllegalArgumentException")
        @Test
        void valueOfAttributeValue_IllegalArgumentException_Unknown() {
            assertThatThrownBy(
                    () -> __AttributeEnum.__OfInteger.valueOfAttributeValue(_SomeIntegerAttributeEnum.class, 33))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("a null attributeValue names the constant instead of a bare NPE")
    @Nested
    class NullAttributeValue_Test {

        private enum _NullValued implements __AttributeEnum.__OfInteger<_NullValued> {

            INSTANCE;

            @Override
            public Integer attributeValue() {
                return null;
            }
        }

        private enum _NullValuedLong implements __AttributeEnum.__OfLong<_NullValuedLong> {

            INSTANCE;

            @Override
            public Long attributeValue() {
                return null;
            }
        }

        @DisplayName("intValue() -> NullPointerException naming the constant")
        @org.junit.jupiter.api.Test
        void _NullPointerException_intValue() {
            org.assertj.core.api.Assertions.assertThatThrownBy(_NullValued.INSTANCE::intValue)
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("INSTANCE");
        }

        @DisplayName("longValue() -> NullPointerException naming the constant")
        @org.junit.jupiter.api.Test
        void _NullPointerException_longValue() {
            org.assertj.core.api.Assertions.assertThatThrownBy(_NullValuedLong.INSTANCE::longValue)
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("INSTANCE");
        }
    }

    @DisplayName("__OfLong")
    @Nested
    class OfLong_Test {

        private enum _SomeLongAttributeEnum implements __AttributeEnum.__OfLong<_SomeLongAttributeEnum> {

            A(11L),

            B(22L);

            _SomeLongAttributeEnum(final long attributeValue) {
                this.attributeValue = attributeValue;
            }

            @Override
            public Long attributeValue() {
                return attributeValue;
            }

            private final Long attributeValue;
        }

        @DisplayName("longValue() unboxes attributeValue()")
        @Test
        void longValue__() {
            assertThat(_SomeLongAttributeEnum.A.longValue()).isEqualTo(11L);
            assertThat(_SomeLongAttributeEnum.B.longValue()).isEqualTo(22L);
        }

        @DisplayName("valueOfAttributeValue(enumClass, long)")
        @Test
        void valueOfAttributeValue__() {
            assertThat(__AttributeEnum.__OfLong.valueOfAttributeValue(_SomeLongAttributeEnum.class, 22L))
                    .isSameAs(_SomeLongAttributeEnum.B);
        }

        @DisplayName("valueOfAttributeValue(enumClass, unknown) -> IllegalArgumentException")
        @Test
        void valueOfAttributeValue_IllegalArgumentException_Unknown() {
            assertThatThrownBy(
                    () -> __AttributeEnum.__OfLong.valueOfAttributeValue(_SomeLongAttributeEnum.class, 33L))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("an Integer of the same numeric value does not match a Long-valued constant")
        @Test
        @SuppressWarnings({"unchecked", "rawtypes"})
        void valueOfAttributeValue_IllegalArgumentException_Integer() {
            // the typed overloads do not allow this; only a raw call can get an Integer in here
            assertThatThrownBy(
                    () -> __AttributeEnumUtils.valueOf(
                            (Class) _SomeLongAttributeEnum.class, (Object) 22))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
