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
import com.github.jinahya.persistence.more.__AttributeEnumConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static com.github.jinahya.persistence.more.test.__AttributeEnumTestUtils.acceptEachEnumConstantAndAttributeValue;
import static com.github.jinahya.persistence.more.test.__AttributeEnumTestUtils.acceptEnumConstantStream;
import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S112", // Generic exceptions should never be thrown
        "java:S119", // Type parameter names should comply with a naming convention
        "java:S3011" // Reflection should not be used to increase accessibility of classes, methods, or fields
})
public abstract class __AttributeEnumConverterTest<
        CONVERTER extends __AttributeEnumConverter<E, ATTRIBUTE>,
        E extends Enum<E> & __AttributeEnum<E, ATTRIBUTE>,
        ATTRIBUTE
        >
        extends __AttributeEnumTest<E, ATTRIBUTE> {

    /**
     * An abstract test class for testing subclasses of {@link __AttributeEnumConverter.__OfString}.
     *
     * @param <CONVERTER> converter type parameter
     * @param <E>         entity type parameter
     * @see #converterClass
     * @see #enumClass
     */
    public abstract static class __OfString<
            CONVERTER extends __AttributeEnumConverter.__OfString<E>,
            E extends Enum<E> & __AttributeEnum.__OfString<E>
            >
            extends __AttributeEnumConverterTest<CONVERTER, E, String> {

        /**
         * {@inheritDoc}
         *
         * @param converterClass {@inheritDoc}.
         * @param enumClass      {@inheritDoc}.
         * @see #converterClass
         * @see #enumClass
         */
        protected __OfString(final Class<CONVERTER> converterClass, final Class<E> enumClass) {
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
    protected __AttributeEnumConverterTest(final Class<CONVERTER> converterClass, final Class<E> enumClass,
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
            acceptEnumConstantStream(
                    enumClass,
                    s -> {
                        s.forEach(ec -> {
                            final var actual = converterInstance.convertToDatabaseColumn(ec);
                            assertThat(actual).isEqualTo(ec.attributeValue());
                        });
                    }
            );
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
            acceptEachEnumConstantAndAttributeValue(
                    enumClass,
                    (ec, av) -> {
                        final var actual = converterInstance.convertToEntityAttribute(av);
                        assertThat(actual).isEqualTo(ec);
                    }
            );
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
