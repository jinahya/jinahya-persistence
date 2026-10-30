package com.github.jinahya.persistence.more.converter.test;

/*-
 * #%L
 * jinahya-persistence-more-test
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

import com.github.jinahya.persistence.more.AttributeEnum;
import com.github.jinahya.persistence.more.converter.AttributeEnumConverter;
import com.github.jinahya.persistence.more.test.AttributeEnum_Test;
import com.github.jinahya.persistence.more.test.___Utils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * An abstract base class for testing {@link AttributeEnumConverter} implementations.
 * <p>
 * Beyond what {@link AttributeEnum_Test} already checks of the enum itself, this class round-trips every constant
 * through the converter, and asserts that {@code null} maps to {@code null} in both directions.
 *
 * @param <CONVERTER> converter type parameter
 * @param <ENUM>      enums type parameter
 * @param <ATTRIBUTE> attribute type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see AttributeEnum_Test
 */
@SuppressWarnings({
        "java:S100", // Method names should comply with a naming convention
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public abstract class AttributeEnumConverter_Test<
        CONVERTER extends AttributeEnumConverter<ENUM, ATTRIBUTE>,
        ENUM extends Enum<ENUM> & AttributeEnum<ENUM, ATTRIBUTE>,
        ATTRIBUTE
        >
        extends AttributeEnum_Test<ENUM, ATTRIBUTE> {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * An abstract test class for testing subclasses of {@link AttributeEnumConverter.OfString}.
     *
     * @param <CONVERTER> converter type parameter
     * @param <ENUM>      entity type parameter
     * @see #converterClass
     * @see #enumClass
     */
    public abstract static class OfStringTest<
            CONVERTER extends AttributeEnumConverter.OfString<ENUM>,
            ENUM extends Enum<ENUM> & AttributeEnum.OfString<ENUM>
            >
            extends AttributeEnumConverter_Test<CONVERTER, ENUM, String> {

        /**
         * Creates a new instance for testing the specified converter class, whose attribute value is a
         * {@link String}.
         *
         * @param converterClass the converter class to test.
         * @param enumClass      the enum class the {@code converterClass} converts.
         * @see #converterClass
         * @see #enumClass
         */
        protected OfStringTest(final Class<CONVERTER> converterClass, final Class<ENUM> enumClass) {
            super(converterClass, enumClass);
        }
    }

    /**
     * An abstract test class for testing subclasses of {@link AttributeEnumConverter.__OfNumber}.
     *
     * @param <CONVERTER> converter type parameter
     * @param <ENUM>      enum type parameter
     * @param <NUMBER>    number type parameter
     * @see #converterClass
     * @see #enumClass
     */
    public abstract static class __OfNumberTest<
            CONVERTER extends AttributeEnumConverter.__OfNumber<ENUM, NUMBER>,
            ENUM extends Enum<ENUM> & AttributeEnum.__OfNumber<ENUM, NUMBER>,
            NUMBER extends Number
            >
            extends AttributeEnumConverter_Test<CONVERTER, ENUM, NUMBER> {

        /**
         * Creates a new instance for testing the specified converter class, whose attribute value is a
         * {@link Number}.
         *
         * @param converterClass the converter class to test.
         * @param enumClass      the enum class the {@code converterClass} converts.
         */
        protected __OfNumberTest(final Class<CONVERTER> converterClass, final Class<ENUM> enumClass) {
            super(converterClass, enumClass);
        }
    }

    /**
     * An abstract test class for testing subclasses of {@link AttributeEnumConverter.__OfInteger}.
     *
     * @param <CONVERTER> converter type parameter
     * @param <ENUM>      enum type parameter
     * @see #converterClass
     * @see #enumClass
     */
    public abstract static class __OfIntegerTest<
            CONVERTER extends AttributeEnumConverter.__OfInteger<ENUM>,
            ENUM extends Enum<ENUM> & AttributeEnum.__OfInteger<ENUM>
            >
            extends __OfNumberTest<CONVERTER, ENUM, Integer> {

        /**
         * Creates a new instance for testing the specified converter class, whose attribute value is an
         * {@link Integer}.
         *
         * @param converterClass the converter class to test.
         * @param enumClass      the enum class the {@code converterClass} converts.
         */
        protected __OfIntegerTest(final Class<CONVERTER> converterClass, final Class<ENUM> enumClass) {
            super(converterClass, enumClass);
        }
    }

    /**
     * An abstract test class for testing subclasses of {@link AttributeEnumConverter.__OfLong}.
     *
     * @param <CONVERTER> converter type parameter
     * @param <ENUM>      enum type parameter
     * @see #converterClass
     * @see #enumClass
     */
    public abstract static class __OfLongTest<
            CONVERTER extends AttributeEnumConverter.__OfLong<ENUM>,
            ENUM extends Enum<ENUM> & AttributeEnum.__OfLong<ENUM>
            >
            extends __OfNumberTest<CONVERTER, ENUM, Long> {

        /**
         * Creates a new instance for testing the specified converter class, whose attribute value is a {@link Long}.
         *
         * @param converterClass the converter class to test.
         * @param enumClass      the enum class the {@code converterClass} converts.
         */
        protected __OfLongTest(final Class<CONVERTER> converterClass, final Class<ENUM> enumClass) {
            super(converterClass, enumClass);
        }
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance for testing specified converter class.
     *
     * @param converterClass the converter class to test.
     * @param enumClass      the enum class the converter converts.
     * @see #converterClass
     */
    protected AttributeEnumConverter_Test(final Class<CONVERTER> converterClass, final Class<ENUM> enumClass) {
        super(enumClass);
        this.converterClass = Objects.requireNonNull(converterClass, "converterClass is null");
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * A nested test class for testing
     * {@link AttributeEnumConverter#convertToDatabaseColumn(Enum) convertToDatabaseColumn(E)}.
     */
    @DisplayName("convertToDatabaseColumn(E)E.attributeValue")
    @Nested
    protected class ConvertToDatabaseColumnTest {

        /**
         * Creates a new instance.
         */
        protected ConvertToDatabaseColumnTest() {
            super();
        }

        /**
         * Verifies that a {@code null} constant converts to a {@code null} column.
         */
        @DisplayName("(null)null")
        @Test
        void _Null_Null() {
            final var converterInstance = newConverterInstance();
            assertNull(converterInstance.convertToDatabaseColumn(null));
        }

        /**
         * Verifies that each constant converts to the attribute value it carries.
         */
        @DisplayName("(enumConstant)enumConstant.attributeValue")
        @Test
        void __NotNull() {
            final var converterInstance = newConverterInstance();
            acceptEnumConstantStream(
                    s -> {
                        s.forEach(ec -> {
                            final var actual = converterInstance.convertToDatabaseColumn(ec);
                            assertEquals(ec.attributeValue(), actual);
                        });
                    }
            );
        }
    }

    /**
     * A nested test class for testing
     * {@link AttributeEnumConverter#convertToEntityAttribute(Object) convertToEntityAttribute(ATTRIBUTE)}.
     */
    @DisplayName("convertToEntityAttribute(E)E.attributeValue")
    @Nested
    protected class ConvertToEntityAttributeTest {

        /**
         * Creates a new instance.
         */
        protected ConvertToEntityAttributeTest() {
            super();
        }

        /**
         * Verifies that a {@code null} column converts to a {@code null} constant.
         */
        @DisplayName("(null)null")
        @Test
        void _Null_Null() {
            final var converterInstance = newConverterInstance();
            assertNull(converterInstance.convertToEntityAttribute(null));
        }

        /**
         * Verifies that each attribute value converts back to the constant carrying it.
         */
        @DisplayName("(attributeValue)E(attributeValue)")
        @Test
        void __NotNull() {
            final var converterInstance = newConverterInstance();
            acceptEachEnumConstantAndAttributeValue((ec, av) -> {
                final var actual = converterInstance.convertToEntityAttribute(av);
                assertEquals(ec, actual);
            });
        }
    }

    // -------------------------------------------------------------------------------------------------- converterClass

    /**
     * Creates a new instance of {@link #converterClass}.
     *
     * @return a new instance of {@link #converterClass}.
     */
    protected CONVERTER newConverterInstance() {
        return ___Utils.newInstance(converterClass);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The class of {@link CONVERTER}.
     */
    protected final Class<CONVERTER> converterClass;
}
