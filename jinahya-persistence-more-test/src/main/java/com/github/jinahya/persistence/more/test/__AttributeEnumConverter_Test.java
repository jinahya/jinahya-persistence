package com.github.jinahya.persistence.more.test;

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

import com.github.jinahya.persistence.more.__AttributeEnum;
import com.github.jinahya.persistence.more.converter.__AttributeEnumConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * An abstract base class for testing {@link __AttributeEnumConverter} implementations.
 * <p>
 * Beyond what {@link __AttributeEnum_Test} already checks of the enum itself, this class round-trips every constant
 * through the converter, and asserts that {@code null} maps to {@code null} in both directions.
 *
 * @param <CONVERTER> converter type parameter
 * @param <ENUM>      enums type parameter
 * @param <ATTRIBUTE> attribute type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __AttributeEnum_Test
 */
@SuppressWarnings({
        "java:S100", // Method names should comply with a naming convention
        "java:S101", // Class names should comply with a naming convention
        "java:S112", // Generic exceptions should never be thrown
        "java:S119", // Type parameter names should comply with a naming convention
        "java:S3011" // Reflection should not be used to increase accessibility of classes, methods, or fields
})
public abstract class __AttributeEnumConverter_Test<
        CONVERTER extends __AttributeEnumConverter<ENUM, ATTRIBUTE>,
        ENUM extends Enum<ENUM> & __AttributeEnum<ENUM, ATTRIBUTE>,
        ATTRIBUTE
        >
        extends __AttributeEnum_Test<ENUM, ATTRIBUTE> {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * An abstract test class for testing subclasses of {@link __AttributeEnumConverter.__OfString}.
     *
     * @param <CONVERTER> converter type parameter
     * @param <ENUM>      entity type parameter
     * @see #converterClass
     * @see #enumClass
     */
    public abstract static class __OfStringTest<
            CONVERTER extends __AttributeEnumConverter.__OfString<ENUM>,
            ENUM extends Enum<ENUM> & __AttributeEnum.__OfString<ENUM>
            >
            extends __AttributeEnumConverter_Test<CONVERTER, ENUM, String> {

        /**
         * Creates a new instance for testing the specified converter class, whose attribute value is a
         * {@link String}.
         *
         * @param converterClass the converter class to test.
         * @param enumClass      the enum class the {@code converterClass} converts.
         * @see #converterClass
         * @see #enumClass
         */
        protected __OfStringTest(final Class<CONVERTER> converterClass, final Class<ENUM> enumClass) {
            super(converterClass, enumClass, String.class);
        }
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance for testing specified converter class.
     *
     * @param converterClass the converter class to test.
     * @param enumClass      the enum class the converter converts.
     * @param attributeClass the type of the attribute value the enum carries.
     * @see #converterClass
     */
    protected __AttributeEnumConverter_Test(final Class<CONVERTER> converterClass, final Class<ENUM> enumClass,
                                            final Class<ATTRIBUTE> attributeClass) {
        super(enumClass, attributeClass);
        this.converterClass = Objects.requireNonNull(converterClass, "converterClass is null");
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * A nested test class for testing
     * {@link __AttributeEnumConverter#convertToDatabaseColumn(Enum) convertToDatabaseColumn(E)}.
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

        @DisplayName("(null)null")
        @Test
        void _Null_Null() {
            final var converterInstance = newConverterInstance();
            assertNull(converterInstance.convertToDatabaseColumn(null));
        }

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
     * {@link __AttributeEnumConverter#convertToEntityAttribute(Object) convertToEntityAttribute(ATTRIBUTE)}.
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

        @DisplayName("(null)null")
        @Test
        void _Null_Null() {
            final var converterInstance = newConverterInstance();
            assertNull(converterInstance.convertToEntityAttribute(null));
        }

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
