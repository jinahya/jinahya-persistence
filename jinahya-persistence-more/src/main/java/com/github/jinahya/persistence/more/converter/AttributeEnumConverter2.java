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
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * An abstract attribute converter for converting between {@link ENUM} type and {@link ATTRIBUTE} type, where {@link ENUM}
 * is an interface, extending {@link AttributeEnum}, which one or more enum classes implement.
 * <p>
 * A subclass only has to name those enum classes; e.g., a library defines an interface and an enum implementing it,
 * and a downstream adds another enum implementing the same interface, and converts the interface with both enum
 * classes. Each enum class is converted by an {@link AttributeEnumConverter} of its own, which this class creates.
 *
 * @param <ENUM>         the shared interface type parameter
 * @param <ATTRIBUTE> attribute type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @apiNote This class is not annotated with {@link jakarta.persistence.Converter @Converter}; a concrete
 *         subclass has to carry that annotation itself, or the persistence unit will not know about it.
 * @see AttributeEnum
 * @see AttributeEnumConverter
 * @see AttributeEnumUtils#find(Class, Object)
 */
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public abstract class AttributeEnumConverter2<ENUM extends AttributeEnum<? extends Enum<?>, ATTRIBUTE>, ATTRIBUTE>
        implements AttributeConverter<ENUM, ATTRIBUTE> {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // -----------------------------------------------------------------------------------------------------------------

    // a raw type, since a Class<? extends ENUM> can not be narrowed, without one, to the Class<T> of an enum type T
    // which AttributeEnumConverter's own type parameter requires
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <A>
    AttributeEnumConverter<?, A> newConverter(final Class<?> enumClass, final Class<A> attributeClass) {
        if (!enumClass.isEnum()) {
            throw new IllegalArgumentException("not an enum class: " + enumClass);
        }
        return new AttributeEnumConverter((Class) enumClass, attributeClass) {
        };
    }

    private static <T extends Enum<T> & AttributeEnum<T, A>, A>
    A convertToDatabaseColumn(final AttributeEnumConverter<T, A> converter, final Object attribute) {
        return converter.convertToDatabaseColumn(converter.enumClass.cast(attribute));
    }

    private static <T extends Enum<T> & AttributeEnum<T, A>, A>
    Optional<T> find(final AttributeEnumConverter<T, A> converter, final A dbData) {
        return AttributeEnumUtils.find(converter.enumClass, dbData);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance for the specified enum classes.
     *
     * @param enumClass      a class of {@link ENUM}.
     * @param enumClasses    the enum classes, each implementing {@link ENUM}, to search, in order.
     * @param attributeClass a class of {@link ATTRIBUTE}
     * @throws IllegalArgumentException when the {@code enumClasses} is empty, or when any of them is not an enum
     *                                  class.
     * @see #enumClass
     * @see #enumClasses
     * @see #attributeClass
     */
    protected AttributeEnumConverter2(final Class<ENUM> enumClass, final List<Class<? extends ENUM>> enumClasses,
                                      final Class<ATTRIBUTE> attributeClass) {
        super();
        this.enumClass = Objects.requireNonNull(enumClass, "enumClass is null");
        this.enumClasses = List.copyOf(Objects.requireNonNull(enumClasses, "enumClasses is null"));
        if (this.enumClasses.isEmpty()) {
            throw new IllegalArgumentException("empty enumClasses");
        }
        this.attributeClass = Objects.requireNonNull(attributeClass, "attributeClass is null");
        converters = this.enumClasses.stream()
                .<AttributeEnumConverter<?, ATTRIBUTE>>map(ec -> newConverter(ec, attributeClass))
                .toList();
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Converts specified entity attribute to the database column value, with the converter of its enum class.
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
        return converters.stream()
                .filter(c -> c.enumClass.isInstance(attribute))
                .findFirst()
                .map(c -> convertToDatabaseColumn(c, attribute))
                .orElseThrow(() -> new IllegalArgumentException(
                        "no converter for " + attribute + "; in " + enumClasses
                ));
    }

    /**
     * Converts the specified database column value to an entity attribute, with the first of {@link #enumClasses}
     * which has a constant carrying it.
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
                    "dbData(" + dbData + ") is not an instance of " + attributeClass + "; of " + enumClasses
            );
        }
        return converters.stream()
                .flatMap(c -> find(c, dbData).stream())
                .findFirst()
                .map(enumClass::cast)
                .orElseThrow(() -> new IllegalArgumentException(
                        "no enum constant, in any of " + enumClasses + ", for dbData: " + dbData
                ));
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The class of {@link ENUM}.
     */
    protected final Class<ENUM> enumClass;

    /**
     * The enum classes, each implementing {@link ENUM}, searched in order; unmodifiable.
     */
    protected final List<Class<? extends ENUM>> enumClasses;

    /**
     * The class of {@link ATTRIBUTE}.
     */
    protected final Class<ATTRIBUTE> attributeClass;

    /**
     * The converters, one for each of {@link #enumClasses}, in the same order.
     */
    private final List<AttributeEnumConverter<?, ATTRIBUTE>> converters;
}
