package com.github.jinahya.persistence.more.converter;

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

import com.github.jinahya.persistence.more.AttributeEnum;
import com.github.jinahya.persistence.more.AttributeEnumUtils;
import jakarta.persistence.AttributeConverter;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * An abstract attribute converter for converting between {@link ENUM} type and {@link ATTRIBUTE} type.
 * <p>
 * The {@link ENUM} is either an enum class implementing {@link AttributeEnum}, or an interface, extending
 * {@link AttributeEnum}, which one or more enum classes implement; e.g., a library defines an interface and an enum
 * implementing it, and a downstream adds another enum implementing the same interface, and converts the interface
 * with both enum classes. The conversion itself is {@link AttributeEnum#attributeValue() attributeValue()} on the way
 * out, and a lookup of the constant carrying that value, in any of the enum classes, on the way in.
 *
 * @param <ENUM>      enum, or shared interface, type parameter
 * @param <ATTRIBUTE> attribute type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @apiNote This class is not annotated with {@link jakarta.persistence.Converter @Converter}; a concrete
 *         subclass has to carry that annotation itself, or the persistence unit will not know about it.
 * @see AttributeEnum
 * @see AttributeEnumUtils#find(List, Object)
 */
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public abstract class AttributeEnumConverter3<ENUM extends AttributeEnum<? extends Enum<?>, ATTRIBUTE>, ATTRIBUTE>
        implements AttributeConverter<ENUM, ATTRIBUTE> {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    @SafeVarargs
    private static <E> List<Class<? extends E>> enumClasses(final Class<? extends E> enumClass,
                                                            final Class<? extends E>... moreEnumClasses) {
        Objects.requireNonNull(moreEnumClasses, "moreEnumClasses is null");
        final var enumClasses = new ArrayList<Class<? extends E>>(1 + moreEnumClasses.length);
        enumClasses.add(enumClass);
        enumClasses.addAll(Arrays.asList(moreEnumClasses));
        return enumClasses;
    }

    /**
     * An abstract attribute converter for converting between {@link E} and {@link String} attribute type.
     *
     * @param <E> enum, or shared interface, type parameter
     * @see AttributeEnum.OfString
     */
    public abstract static class OfString<E extends AttributeEnum.OfString<?>>
            extends AttributeEnumConverter3<E, String> {

        /**
         * Creates a new instance for the specified enum classes; either a single enum class, or the enum classes each
         * implementing a shared interface.
         *
         * @param enumClass       the first enum class to search.
         * @param moreEnumClasses the other enum classes, each of {@link E}, to search, in order.
         * @see #enumClasses
         */
        @SafeVarargs
        protected OfString(final Class<? extends E> enumClass, final Class<? extends E>... moreEnumClasses) {
            super(enumClasses(enumClass, moreEnumClasses), String.class);
        }
    }

    /**
     * An abstract attribute converter for converting between {@link E} and a {@link Number} attribute type.
     *
     * @param <E> enum, or shared interface, type parameter
     * @param <N> number type parameter
     * @apiNote Unlike {@link AttributeEnum.OfString}, there is no default attribute value for a number — an
     *         {@link Enum#ordinal() ordinal} would re-introduce exactly the coupling this package avoids — so each
     *         constant has to declare its own.
     * @see AttributeEnum.OfNumber
     */
    public abstract static class OfNumber<E extends AttributeEnum.OfNumber<?, N>, N extends Number>
            extends AttributeEnumConverter3<E, N> {

        /**
         * Creates a new instance for the specified enum classes, each implementing a shared interface, and the number
         * class.
         *
         * @param enumClasses the enum classes, each of {@link E}, to search, in order.
         * @param numberClass the number class.
         * @see #enumClasses
         * @see #attributeClass
         */
        protected OfNumber(final List<Class<? extends E>> enumClasses, final Class<N> numberClass) {
            super(enumClasses, numberClass);
        }

        /**
         * Creates a new instance for the specified enum class and number class.
         *
         * @param enumClass   the enum class.
         * @param numberClass the number class.
         * @see #enumClasses
         * @see #attributeClass
         */
        protected OfNumber(final Class<E> enumClass, final Class<N> numberClass) {
            super(enumClass, numberClass);
        }
    }

    /**
     * An abstract attribute converter for converting between {@link E} and {@link Integer} attribute type.
     *
     * @param <E> enum, or shared interface, type parameter
     * @see AttributeEnum.OfInteger
     */
    public abstract static class OfInteger<E extends AttributeEnum.OfInteger<?>>
            extends OfNumber<E, Integer> {

        /**
         * Creates a new instance for the specified enum classes; either a single enum class, or the enum classes each
         * implementing a shared interface.
         *
         * @param enumClass       the first enum class to search.
         * @param moreEnumClasses the other enum classes, each of {@link E}, to search, in order.
         * @see #enumClasses
         */
        @SafeVarargs
        protected OfInteger(final Class<? extends E> enumClass, final Class<? extends E>... moreEnumClasses) {
            super(enumClasses(enumClass, moreEnumClasses), Integer.class);
        }
    }

    /**
     * An abstract attribute converter for converting between {@link E} and {@link Long} attribute type.
     *
     * @param <E> enum, or shared interface, type parameter
     * @see AttributeEnum.OfLong
     */
    public abstract static class OfLong<E extends AttributeEnum.OfLong<?>>
            extends OfNumber<E, Long> {

        /**
         * Creates a new instance for the specified enum classes; either a single enum class, or the enum classes each
         * implementing a shared interface.
         *
         * @param enumClass       the first enum class to search.
         * @param moreEnumClasses the other enum classes, each of {@link E}, to search, in order.
         * @see #enumClasses
         */
        @SafeVarargs
        protected OfLong(final Class<? extends E> enumClass, final Class<? extends E>... moreEnumClasses) {
            super(enumClasses(enumClass, moreEnumClasses), Long.class);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance for the specified enum classes, each implementing a shared interface.
     *
     * @param enumClasses    the enum classes, each of {@link ENUM}, to search, in order.
     * @param attributeClass a class of {@link ATTRIBUTE}
     * @throws IllegalArgumentException when the {@code enumClasses} is empty, or when any of them is not an enum
     *                                  class.
     * @see #enumClasses
     * @see #attributeClass
     */
    protected AttributeEnumConverter3(final List<Class<? extends ENUM>> enumClasses,
                                      final Class<ATTRIBUTE> attributeClass) {
        super();
        this.enumClasses = List.copyOf(Objects.requireNonNull(enumClasses, "enumClasses is null"));
        if (this.enumClasses.isEmpty()) {
            throw new IllegalArgumentException("empty enumClasses");
        }
        for (final var ec : this.enumClasses) {
            if (!ec.isEnum()) {
                throw new IllegalArgumentException("not an enum class: " + ec);
            }
        }
        this.attributeClass = Objects.requireNonNull(attributeClass, "attributeClass is null");
    }

    /**
     * Creates a new instance for the specified enum class.
     *
     * @param enumClass      the enum class.
     * @param attributeClass a class of {@link ATTRIBUTE}
     * @throws IllegalArgumentException when the {@code enumClass} is not an enum class.
     * @see #enumClasses
     * @see #attributeClass
     */
    protected AttributeEnumConverter3(final Class<ENUM> enumClass, final Class<ATTRIBUTE> attributeClass) {
        this(List.of(Objects.requireNonNull(enumClass, "enumClass is null")), attributeClass);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Converts specified entity attribute to the database column value.
     *
     * @param attribute the entity attribute value to be converted.
     * @return a database column value; {@code null} when {@code attribute} is {@code null}.
     * @throws IllegalArgumentException when the {@code attribute} is of none of {@link #enumClasses}.
     * @apiNote An {@code attribute} of none of {@link #enumClasses} is rejected, rather than written, since it could
     *         not be read back.
     */
    @Override
    public ATTRIBUTE convertToDatabaseColumn(final ENUM attribute) {
        if (attribute == null) {
            return null;
        }
        if (enumClasses.stream().noneMatch(ec -> ec.isInstance(attribute))) {
            throw new IllegalArgumentException("attribute(" + attribute + ") is of none of " + enumClasses);
        }
        return attribute.attributeValue();
    }

    /**
     * Converts the specified database column value to an entity attribute.
     *
     * @param dbData the database column value to convert.
     * @return an entity attribute; {@code null} when {@code dbData} is {@code null}.
     * @throws IllegalArgumentException when the {@code dbData} is not an instance of {@link #attributeClass}, or when
     *                                  no constant of any of {@link #enumClasses} carries it.
     */
    @Override
    public ENUM convertToEntityAttribute(final ATTRIBUTE dbData) {
        if (dbData == null) {
            return null;
        }
        if (!attributeClass.isInstance(dbData)) {
            throw new IllegalArgumentException(
                    "dbData(" + dbData + ") is not an instance of " + attributeClass +
                    "; of " + enumClasses
            );
        }
        return AttributeEnumUtils.valueOf(enumClasses, dbData);
    }

    // -----------------------------------------------------------------------------------------------------------------

    // no Class<ENUM> is kept; nothing here needs one, since valueOf(enumClasses, ...) already returns an ENUM, and
    // taking one would cost a caller with a generic shared interface, e.g. ISize<S>, an unchecked cast just to name it.
    // keep one when something does need it; e.g., checking the enumClasses against it at runtime, a message naming
    // the interface, a test reading the type from the converter, or Array.newInstance, Collections.checkedList and
    // CriteriaBuilder.parameter, which all take a Class<ENUM>

    /**
     * The enum classes, each of {@link ENUM}, searched in order; unmodifiable.
     */
    protected final List<Class<? extends ENUM>> enumClasses;

    /**
     * The class of {@link ATTRIBUTE}.
     */
    protected final Class<ATTRIBUTE> attributeClass;
}
