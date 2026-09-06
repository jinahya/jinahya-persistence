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
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * An abstract attribute converter for converting between {@link E} type and {@link ATTRIBUTE} type.
 * <p>
 * A subclass only has to name its enum class; the conversion itself is
 * {@link __AttributeEnum#attributeValue() attributeValue()} on the way out, and a lookup of the constant carrying that
 * value on the way in.
 *
 * @param <E>         enum type parameter
 * @param <ATTRIBUTE> attribute type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @apiNote This class is not annotated with {@link jakarta.persistence.Converter @Converter}; a concrete
 *         subclass has to carry that annotation itself, or the persistence unit will not know about it.
 * @see __AttributeEnum
 * @see __AttributeEnumUtils#valueOf(Class, Object)
 */
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public abstract class __AttributeEnumConverter<E extends Enum<E> & __AttributeEnum<E, ATTRIBUTE>, ATTRIBUTE>
        implements AttributeConverter<E, ATTRIBUTE> {

    /**
     * An abstract attribute converter for converting between {@link E} and {@link String} attribute type.
     *
     * @param <E> enum type parameter
     * @see __AttributeEnum.__OfString
     */
    public abstract static class __OfString<E extends Enum<E> & __AttributeEnum.__OfString<E>>
            extends __AttributeEnumConverter<E, String> {

        /**
         * Creates a new instance for the specified enum class.
         *
         * @param enumClass the enum class.
         * @see #enumClass
         */
        protected __OfString(final Class<E> enumClass) {
            super(enumClass, String.class);
        }
    }

    /**
     * An abstract attribute converter for converting between {@link E} and a {@link Number} attribute type.
     *
     * @param <E> enum type parameter
     * @param <N> number type parameter
     * @apiNote Unlike {@link __AttributeEnum.__OfString}, there is no default attribute value for a number — an
     *         {@link Enum#ordinal() ordinal} would re-introduce exactly the coupling this package avoids — so each
     *         constant has to declare its own.
     * @see __AttributeEnum.__OfNumber
     */
    public abstract static class __OfNumber<E extends Enum<E> & __AttributeEnum.__OfNumber<E, N>, N extends Number>
            extends __AttributeEnumConverter<E, N> {

        /**
         * Creates a new instance for the specified enum class and number class.
         *
         * @param enumClass   the enum class.
         * @param numberClass the number class.
         * @see #enumClass
         * @see #attributeClass
         */
        protected __OfNumber(final Class<E> enumClass, final Class<N> numberClass) {
            super(enumClass, numberClass);
        }
    }

    /**
     * An abstract attribute converter for converting between {@link E} and {@link Integer} attribute type.
     *
     * @param <E> enum type parameter
     * @see __AttributeEnum.__OfInteger
     */
    public abstract static class __OfInteger<E extends Enum<E> & __AttributeEnum.__OfInteger<E>>
            extends __OfNumber<E, Integer> {

        /**
         * Creates a new instance for the specified enum class.
         *
         * @param enumClass the enum class.
         * @see #enumClass
         */
        protected __OfInteger(final Class<E> enumClass) {
            super(enumClass, Integer.class);
        }
    }

    /**
     * An abstract attribute converter for converting between {@link E} and {@link Long} attribute type.
     *
     * @param <E> enum type parameter
     * @see __AttributeEnum.__OfLong
     */
    public abstract static class __OfLong<E extends Enum<E> & __AttributeEnum.__OfLong<E>>
            extends __OfNumber<E, Long> {

        /**
         * Creates a new instance for the specified enum class.
         *
         * @param enumClass the enum class.
         * @see #enumClass
         */
        protected __OfLong(final Class<E> enumClass) {
            super(enumClass, Long.class);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance for the specified enum class.
     *
     * @param enumClass      a class of {@link E}.
     * @param attributeClass a class of {@link ATTRIBUTE}
     * @see #enumClass
     * @see #attributeClass
     */
    protected __AttributeEnumConverter(final Class<E> enumClass, final Class<ATTRIBUTE> attributeClass) {
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
    public @Nullable ATTRIBUTE convertToDatabaseColumn(final @Nullable E attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.attributeValue();
    }

    /**
     * Converts the specified database column value to an entity attribute.
     *
     * @param dbData the database column value to convert.
     * @return an entity attribute; {@code null} when {@code dbData} is {@code null}.
     * @throws IllegalArgumentException when the {@code dbData} is not an instance of {@link #attributeClass}, or when
     *                                  no constant of {@link #enumClass} carries it.
     */
    @Override
    public @Nullable E convertToEntityAttribute(final @Nullable ATTRIBUTE dbData) {
        if (dbData == null) {
            return null;
        }
        if (!attributeClass.isInstance(dbData)) {
            throw new IllegalArgumentException(
                    "dbData(" + dbData + ") is not an instance of " + attributeClass +
                    "; of " + enumClass
            );
        }
        return __AttributeEnumUtils.valueOf(enumClass, dbData);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The class of {@link E}.
     */
    protected final Class<E> enumClass;

    /**
     * The class of {@link ATTRIBUTE}.
     */
    protected final Class<ATTRIBUTE> attributeClass;
}
