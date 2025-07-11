package com.github.jinahya.persistence.more.tests;

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
import com.github.jinahya.persistence.more.__AttributeEnumConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S112", // Generic exceptions should never be thrown
        "java:S119", // Type parameter names should comply with a naming convention
        "java:S3011" // Reflection should not be used to increase accessibility of classes, methods, or fields
})
public abstract class __AttributeEnumConverterTest<
        CONVERTER extends __AttributeEnumConverter<ENUM, ATTRIBUTE>,
        ENUM extends Enum<ENUM> & __AttributeEnum<ENUM, ATTRIBUTE>,
        ATTRIBUTE
        >
        extends ___AttributeEnumTest<ENUM, ATTRIBUTE> {

    /**
     * An abstract test class for testing subclasses of {@link __AttributeEnumConverter.__OfString}.
     *
     * @param <CONVERTER> converter type parameter
     * @param <ENUM>      entity type parameter
     * @see #converterClass
     * @see #enumClass
     */
    public abstract static class __OfString<
            CONVERTER extends __AttributeEnumConverter.__OfString<ENUM>,
            ENUM extends Enum<ENUM> & __AttributeEnum.__OfString<ENUM>
            >
            extends __AttributeEnumConverterTest<CONVERTER, ENUM, String> {

        /**
         * {@inheritDoc}
         *
         * @param converterClass {@inheritDoc}.
         * @param enumClass      {@inheritDoc}.
         * @see #converterClass
         * @see #enumClass
         */
        protected __OfString(final Class<CONVERTER> converterClass, final Class<ENUM> enumClass) {
            super(converterClass, enumClass, String.class);
        }
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance for testing specified converter class.
     *
     * @param converterClass the converter class to test.
     * @see #converterClass
     */
    protected __AttributeEnumConverterTest(final Class<CONVERTER> converterClass, final Class<ENUM> enumClass,
                                           final Class<ATTRIBUTE> attributeClass) {
        super(enumClass, attributeClass);
        this.converterClass = Objects.requireNonNull(converterClass, "converterClass is null");
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("convertToDatabaseColumn(E)E.attributeValue")
    @Nested
    protected class ConvertToDatabaseColumnTest {

        @DisplayName("(null)null")
        @Test
        void _Null_Null() {
            final var converterInstance = newConverterInstance();
            assertThat(converterInstance.convertToDatabaseColumn(null)).isNull();
        }

        @DisplayName("(enumConstant)enumConstant.attributeValue")
        @Test
        void __NotNull() {
            final var converterInstance = newConverterInstance();
            acceptEnumConstantStream(s -> {
                s.forEach(ec -> {
                    final var actual = converterInstance.convertToDatabaseColumn(ec);
                    assertThat(actual).isEqualTo(ec.attributeValue());
                });
            });
        }
    }

    @DisplayName("convertToEntityAttribute(E)E.attributeValue")
    @Nested
    protected class ConvertToEntityAttributeTest {

        @DisplayName("(null)null")
        @Test
        void _Null_Null() {
            final var converterInstance = newConverterInstance();
            assertThat(converterInstance.convertToEntityAttribute(null)).isNull();
        }

        @DisplayName("(attributeValue)E(attributeValue)")
        @Test
        void __NotNull() {
            final var converterInstance = newConverterInstance();
            acceptEnumConstantAndAttributeValue((ec, av) -> {
                final var actual = converterInstance.convertToEntityAttribute(av);
                assertThat(actual).isEqualTo(ec);
            });
        }
    }

    // -------------------------------------------------------------------------------------------------- converterClass
    protected CONVERTER newConverterInstance() {
        try {
            final var constructor = converterClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (final ReflectiveOperationException roe) {
            throw new RuntimeException("failed to initialize a new instance of " + converterClass, roe);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The class of {@link CONVERTER}.
     */
    protected final Class<CONVERTER> converterClass;
}
