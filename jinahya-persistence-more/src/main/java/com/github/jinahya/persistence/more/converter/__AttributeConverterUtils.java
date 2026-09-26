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
import java.util.function.Function;
import java.util.function.UnaryOperator;

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
     * The converter {@link #identity()} hands out. Stateless, and holding no type of its own, so one instance serves
     * every {@code X}.
     */
    private static final AttributeConverter<?, ?> IDENTITY =
            using(UnaryOperator.identity(), UnaryOperator.identity());

    /**
     * Returns an attribute converter which stores an attribute as itself, and reads it back as itself.
     *
     * @param <X> entity attribute type parameter, which is also the database column type parameter.
     * @return an attribute converter which converts in neither direction.
     * @apiNote This is what an attribute already of the column type needs, where a converter is required but a
     *         conversion is not &mdash; the elements of a {@link java.util.List} of {@link String}s handed to
     *         {@link __JoinedStringAttributeConverter}, for one. {@code null} passes through unchanged in both
     *         directions.
     * @implNote The returned converter is shared. It carries no state and does nothing with its type parameter,
     *         so handing the same instance to every caller is safe, and the cast is the one
     *         {@link java.util.Collections#emptyList()} makes for the same reason.
     */
    @SuppressWarnings({
            "unchecked"
    })
    public static <X> AttributeConverter<X, X> identity() {
        return (AttributeConverter<X, X>) IDENTITY;
    }

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
