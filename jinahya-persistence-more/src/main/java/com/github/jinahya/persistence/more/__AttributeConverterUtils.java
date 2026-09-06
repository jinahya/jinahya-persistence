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
import java.util.function.Function;

/**
 * A utility class for {@link AttributeConverter}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @apiNote The converters returned by this class are plain objects, not annotated with
 *         {@link jakarta.persistence.Converter @Converter}; they are meant to be composed programmatically, and to be
 *         delegated to from a converter class which <em>is</em> registered with the persistence unit.
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class __AttributeConverterUtils {

    /**
     * Creates a new attribute converter with specified functions.
     *
     * @param toDatabaseColumn  a function for converting an entity attribute to a database column.
     * @param toEntityAttribute a function for converting a database column to an entity attribute.
     * @param <X>               entity attribute type parameter
     * @param <Y>               database column type parameter
     * @return a new attribute converter.
     */
    public static <X, Y> AttributeConverter<X, Y> using(
            final Function<? super X, ? extends Y> toDatabaseColumn,
            final Function<? super Y, ? extends X> toEntityAttribute) {
        Objects.requireNonNull(toDatabaseColumn, "toDatabaseColumn is null");
        Objects.requireNonNull(toEntityAttribute, "toEntityAttribute is null");
        return new AttributeConverter<>() { // @formatter:off
            @Override public Y convertToDatabaseColumn(final X attribute) {
                return toDatabaseColumn.apply(attribute);
            }
            @Override public X convertToEntityAttribute(final Y dbData) {
                return toEntityAttribute.apply(dbData);
            } // @formatter:on
        };
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance, which is not allowed.
     */
    private __AttributeConverterUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
