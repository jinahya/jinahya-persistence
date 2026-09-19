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

import jakarta.persistence.AttributeConverter;

import java.util.Objects;

/**
 * An abstract class for chaining two {@link AttributeConverter}s.
 * <p>
 * An instance converts an entity attribute to the database column type through an intermediate type: the first
 * converter maps {@link T} to {@link U}, and the second maps {@link U} to {@link V}; reading a column runs the same two
 * converters in reverse.
 *
 * @param <T> attribute type parameter
 * @param <U> intermediate type parameter
 * @param <V> database type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @apiNote Extend this class when the composed converter has to be registered with the persistence unit: a
 *         concrete subclass can carry {@link jakarta.persistence.Converter @Converter} and be named in
 *         {@code persistence.xml} or in {@link jakarta.persistence.Convert @Convert}. Where none of that is needed, an
 *         instance composes two converters inline:
 *         {@snippet lang = "java":
 *                         final AttributeConverter<T, V> converter = new __ChainingAttributeConverter<>(first, second);
 *}
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public class __ChainingAttributeConverter<T, U, V> implements AttributeConverter<T, V> {

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance with the specified two attribute converters.
     *
     * @param attributeConverter1 the converter from the entity attribute type to the intermediate type.
     * @param attributeConverter2 the converter from the intermediate type to the database column type.
     */
    public __ChainingAttributeConverter(final AttributeConverter<T, U> attributeConverter1,
                                        final AttributeConverter<U, V> attributeConverter2) {
        super();
        this.attributeConverter1 = Objects.requireNonNull(attributeConverter1, "attributeConverter1 is null");
        this.attributeConverter2 = Objects.requireNonNull(attributeConverter2, "attributeConverter2 is null");
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Converts the specified database column value to an entity attribute, through the intermediate type.
     *
     * @param dbData the database column value to convert.
     * @return an entity attribute converted from the {@code dbData}.
     */
    @Override
    public T convertToEntityAttribute(final V dbData) {
        return attributeConverter1.convertToEntityAttribute(
                attributeConverter2.convertToEntityAttribute(dbData)
        );
    }

    /**
     * Converts the specified entity attribute to a database column value, through the intermediate type.
     *
     * @param attribute the entity attribute to convert.
     * @return a database column value converted from the {@code attribute}.
     */
    @Override
    public V convertToDatabaseColumn(final T attribute) {
        return attributeConverter2.convertToDatabaseColumn(
                attributeConverter1.convertToDatabaseColumn(attribute)
        );
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final AttributeConverter<T, U> attributeConverter1;

    private final AttributeConverter<U, V> attributeConverter2;
}
