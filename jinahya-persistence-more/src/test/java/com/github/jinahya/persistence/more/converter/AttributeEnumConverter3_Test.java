package com.github.jinahya.persistence.more.converter;

import com.github.jinahya.persistence.more.AttributeEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * A class for testing {@link AttributeEnumConverter3}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class AttributeEnumConverter3_Test {

    // a size no converter here lists
    private enum _OtherSize implements _ISize<_OtherSize> {
        HUGE
    }

    private interface _ILevel<L extends Enum<L> & _ILevel<L>> extends AttributeEnum.OfInteger<L> {
    }

    private enum _Level implements _ILevel<_Level> {

        LOW(1),

        HIGH(9);

        _Level(final int attributeValue) {
            this.attributeValue = attributeValue;
        }

        @Override
        public Integer attributeValue() {
            return attributeValue;
        }

        private final int attributeValue;
    }

    private enum _ExtendedLevel implements _ILevel<_ExtendedLevel> {

        CRITICAL(99);

        _ExtendedLevel(final int attributeValue) {
            this.attributeValue = attributeValue;
        }

        @Override
        public Integer attributeValue() {
            return attributeValue;
        }

        private final int attributeValue;
    }

    private static class _LevelConverter extends AttributeEnumConverter3.OfInteger<_Level> {

        _LevelConverter() {
            super(_Level.class);
        }
    }

    private static class _ILevelConverter extends AttributeEnumConverter3.OfInteger<_ILevel<?>> {

        _ILevelConverter() {
            super(_Level.class, _ExtendedLevel.class);
        }
    }

    private static class _ListedISizeConverter extends AttributeEnumConverter3<_ISize<?>, String> {

        _ListedISizeConverter(final List<Class<? extends _ISize<?>>> enumClasses) {
            super(enumClasses, String.class);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("a single enum class")
    @Nested
    class SingleTest {

        private final _SizeConverters.OfSize converter = new _SizeConverters.OfSize();

        @DisplayName("each constant -> its attribute value -> the same constant")
        @Test
        void _RoundTrip_Each() {
            for (final var constant : _Size.values()) {
                final var dbData = converter.convertToDatabaseColumn(constant);
                assertThat(dbData).isEqualTo(constant.attributeValue());
                assertThat(converter.convertToEntityAttribute(dbData)).isSameAs(constant);
            }
        }

        @DisplayName("null <-> null")
        @Test
        void _Null_Null() {
            assertThat(converter.convertToDatabaseColumn(null)).isNull();
            assertThat(converter.convertToEntityAttribute(null)).isNull();
        }

        @DisplayName("convertToEntityAttribute(unknown) -> IllegalArgumentException")
        @Test
        void _IllegalArgumentException_Unknown() {
            assertThatThrownBy(() -> converter.convertToEntityAttribute("Extra-Large"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("convertToEntityAttribute(of another type) -> IllegalArgumentException")
        @SuppressWarnings({"unchecked", "rawtypes"})
        @Test
        void _IllegalArgumentException_AnotherType() {
            final AttributeEnumConverter3 raw = converter;
            assertThatThrownBy(() -> raw.convertToEntityAttribute(1))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("the enumClasses is the enum class alone")
        @Test
        void _EnumClasses_Single() {
            assertThat(converter.enumClasses).containsExactly(_Size.class);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("enum classes of a shared interface")
    @Nested
    class SharedTest {

        private final _SizeConverters.OfISize converter = new _SizeConverters.OfISize();

        @DisplayName("each constant, of each enum class -> its attribute value -> the same constant")
        @Test
        void _RoundTrip_Each() {
            Stream.<_ISize<?>>concat(Stream.of(_Size.values()), Stream.of(_ExtendedSize.values())).forEach(c -> {
                final var dbData = converter.convertToDatabaseColumn(c);
                assertThat(dbData).isEqualTo(c.attributeValue());
                assertThat(converter.convertToEntityAttribute(dbData)).isSameAs(c);
            });
        }

        @DisplayName("null <-> null")
        @Test
        void _Null_Null() {
            assertThat(converter.convertToDatabaseColumn(null)).isNull();
            assertThat(converter.convertToEntityAttribute(null)).isNull();
        }

        @DisplayName("convertToEntityAttribute(unknown) -> IllegalArgumentException")
        @Test
        void _IllegalArgumentException_Unknown() {
            assertThatThrownBy(() -> converter.convertToEntityAttribute("Huge"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("convertToDatabaseColumn(of an unlisted enum class) -> IllegalArgumentException")
        @Test
        void _IllegalArgumentException_Unlisted() {
            assertThatThrownBy(() -> converter.convertToDatabaseColumn(_OtherSize.HUGE))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("the enumClasses is the enum classes, in order")
        @Test
        void _EnumClasses_InOrder() {
            assertThat(converter.enumClasses).containsExactly(_Size.class, _ExtendedSize.class);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("OfInteger")
    @Nested
    class OfIntegerTest {

        @DisplayName("a single enum class; each constant -> its attribute value -> the same constant")
        @Test
        void _RoundTrip_Single() {
            final var converter = new _LevelConverter();
            for (final var constant : _Level.values()) {
                assertThat(converter.convertToEntityAttribute(converter.convertToDatabaseColumn(constant)))
                        .isSameAs(constant);
            }
        }

        @DisplayName("enum classes of a shared interface; each constant -> its attribute value -> the same constant")
        @Test
        void _RoundTrip_Shared() {
            final var converter = new _ILevelConverter();
            Stream.<_ILevel<?>>concat(Stream.of(_Level.values()), Stream.of(_ExtendedLevel.values())).forEach(c -> {
                assertThat(converter.convertToEntityAttribute(converter.convertToDatabaseColumn(c))).isSameAs(c);
            });
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("constructors")
    @Nested
    class ConstructorTest {

        @DisplayName("(emptyEnumClasses, ...) -> IllegalArgumentException")
        @Test
        void _IllegalArgumentException_Empty() {
            assertThatThrownBy(() -> new _ListedISizeConverter(List.of()))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("(enumClasses with an interface, ...) -> IllegalArgumentException")
        @SuppressWarnings({"unchecked"})
        @Test
        void _IllegalArgumentException_Interface() {
            final var interfaceClass = (Class<? extends _ISize<?>>) (Class<?>) _ISize.class;
            assertThatThrownBy(() -> new _ListedISizeConverter(List.of(_Size.class, interfaceClass)))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("(enumClass, null...) -> NullPointerException")
        @Test
        void _NullPointerException_NullElement() {
            assertThatThrownBy(() -> new AttributeEnumConverter3.OfString<_ISize<?>>(_Size.class, (Class<_ExtendedSize>) null) {
            }).isInstanceOf(NullPointerException.class);
        }
    }
}
