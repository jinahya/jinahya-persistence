package com.github.jinahya.persistence.more;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class __AttributeEnumUtilsTest {

    @SuppressWarnings({
            "java:S114", // Interface names should comply with a naming convention
            "java:S119" // Type parameter names should comply with a naming convention
    })
    private interface __SomeAttributeEnum<ENUM extends Enum<ENUM> & __SomeAttributeEnum<ENUM>>
            extends __AttributeEnum.__OfString<ENUM> {

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
        void __1A(final _SomeAttributeEnum1 enumConstant) {
            final var actual = __AttributeEnumUtils.valueOfAttributeValue(
                    _SomeAttributeEnum1.class,
                    enumConstant.attributeValue()
            );
            assertThat(actual).isSameAs(enumConstant);
        }

        @EnumSource(_SomeAttributeEnum2.class)
        @ParameterizedTest
        void __2A(final _SomeAttributeEnum2 enumConstant) {
            final var actual = __AttributeEnumUtils.valueOfAttributeValue(
                    _SomeAttributeEnum2.class,
                    enumConstant.attributeValue()
            );
            assertThat(actual).isSameAs(enumConstant);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("valueOfAttributeValue(attributeValue, enumClasses)")
    @Nested
    class ValueOfAttributeValue_WithMultipleEnumClasses_Test {

        private static final List<Class<? extends __SomeAttributeEnum<?>>> ENUM_CLASSES =
                List.of(_SomeAttributeEnum1.class, _SomeAttributeEnum2.class);

        @Test
        void __A() {
            final var actual = __AttributeEnumUtils.valueOfAttributeValue("A", ENUM_CLASSES);
            assertThat(actual).isSameAs(_SomeAttributeEnum1.A);
        }

        @Test
        void __B1() {
            final var actual = __AttributeEnumUtils.valueOfAttributeValue("B", ENUM_CLASSES);
            assertThat(actual).isSameAs(_SomeAttributeEnum1.B);
        }

        @Test
        void __B2() {
            final var actual = __AttributeEnumUtils.valueOfAttributeValue("B", ENUM_CLASSES.reversed());
            assertThat(actual).isSameAs(_SomeAttributeEnum2.B);
        }

        @Test
        void __C() {
            final var actual = __AttributeEnumUtils.valueOfAttributeValue("C", ENUM_CLASSES);
            assertThat(actual).isSameAs(_SomeAttributeEnum2.C);
        }

        @Test
        void __D() {
            assertThatThrownBy(() -> __AttributeEnumUtils.valueOfAttributeValue("D", ENUM_CLASSES))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
