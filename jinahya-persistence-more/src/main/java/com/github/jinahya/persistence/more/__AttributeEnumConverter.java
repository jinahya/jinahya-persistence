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

import jakarta.persistence.AttributeConverter;

import java.util.Objects;

/**
 * An abstract attribute converter for converting between {@link ENUM} type and {@link ATTRIBUTE} type.
 *
 * @param <ENUM>      enum type parameter
 * @param <ATTRIBUTE> attribute type parameter
 */
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public abstract class __AttributeEnumConverter<ENUM extends Enum<ENUM> & __AttributeEnum<ENUM, ATTRIBUTE>, ATTRIBUTE>
        implements AttributeConverter<ENUM, ATTRIBUTE> {

    /**
     * An abstract attribute converter for converting between {@link ENUM} and {@link String} attribute type.
     *
     * @param <ENUM> enum type parameter
     */
    public abstract static class __OfString<ENUM extends Enum<ENUM> & __AttributeEnum.__OfString<ENUM>>
            extends __AttributeEnumConverter<ENUM, String> {

        /**
         * Creates a new instance for the specified enum class.
         *
         * @param enumClass the enum class.
         * @see #enumClass
         */
        protected __OfString(final Class<ENUM> enumClass) {
            super(enumClass, String.class);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance for the specified enum class.
     *
     * @param enumClass      a class of {@link ENUM}.
     * @param attributeClass a class of {@link ATTRIBUTE}
     * @see #enumClass
     * @see #attributeClass
     */
    protected __AttributeEnumConverter(final Class<ENUM> enumClass, final Class<ATTRIBUTE> attributeClass) {
        super();
        this.enumClass = Objects.requireNonNull(enumClass, "enumClass is null");
        this.attributeClass = Objects.requireNonNull(attributeClass, "attributeClass is null");
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Converts specified entity attribute to the database column value.
     *
     * @param attribute the entity attribute value to be converted.
     * @return a database column value; {@code null} when {@code attribute} is {@code null}.
     */
    @Override
    public ATTRIBUTE convertToDatabaseColumn(final ENUM attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.attributeValue();
    }

    /**
     * Converts specified db data to an entity attribute.
     *
     * @param dbData the data from the database column to be converted.
     * @return an entity attribute; {@code null} when {@code dbData} is {@code null}.
     */
    @Override
    public ENUM convertToEntityAttribute(final ATTRIBUTE dbData) {
        if (dbData == null) {
            return null;
        }
        return __AttributeEnumUtils.valueOfAttributeValue(enumClass, dbData);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The class of {@link ENUM}.
     */
    protected final Class<ENUM> enumClass;

    /**
     * The class of {@link ATTRIBUTE}.
     */
    protected final Class<ATTRIBUTE> attributeClass;
}
