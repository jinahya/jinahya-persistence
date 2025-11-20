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

import jakarta.annotation.Nonnull;
import jakarta.persistence.AttributeConverter;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

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
    @Nonnull
    public static <X, Y> AttributeConverter<X, Y> using(
            final @Nonnull Function<? super X, ? extends Y> toDatabaseColumn,
            final @Nonnull Function<? super Y, ? extends X> toEntityAttribute) {
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

    /**
     * Creates a new 2-step attribute converter with specified attribute converters.
     *
     * @param attributeConverter1 an attribute converter for converting an entity attribute to/from an intermediate
     *                            type.
     * @param attributeConverter2 an attribute converter for converting a database column to/from an intermediate type.
     * @param <T>                 an entity attribute type parameter
     * @param <U>                 an intermediate type parameter
     * @param <V>                 database column type parameter
     * @return a new attribute converter.
     */
    @Nonnull
    public static <T, U, V> AttributeConverter<T, V> chaining(
            final @Nonnull AttributeConverter<T, U> attributeConverter1,
            final @Nonnull AttributeConverter<U, V> attributeConverter2) {
        Objects.requireNonNull(attributeConverter1, "attributeConverter1 is null");
        Objects.requireNonNull(attributeConverter2, "attributeConverter2 is null");
        return new AttributeConverter<>() { // @formatter:off
            @Override public V convertToDatabaseColumn(final T attribute) {
                return attributeConverter2.convertToDatabaseColumn(
                        attributeConverter1.convertToDatabaseColumn(attribute)
                );
            }
            @Override public T convertToEntityAttribute(final V dbData) {
                return attributeConverter1.convertToEntityAttribute(
                        attributeConverter2.convertToEntityAttribute(dbData)
                );
            } // @formatter:on
        };
    }

    static <T, U, V, W> AttributeConverter<T, W> chaining(
            final @Nonnull AttributeConverter<T, U> attributeConverter1,
            final @Nonnull AttributeConverter<U, V> attributeConverter2,
            final @Nonnull AttributeConverter<V, W> attributeConverter3) {
        if (ThreadLocalRandom.current().nextBoolean()) {
            return chaining(
                    attributeConverter1,
                    chaining(attributeConverter2, attributeConverter3)
            );
        } else {
            return chaining(
                    chaining(attributeConverter1, attributeConverter2),
                    attributeConverter3
            );
        }
    }

    static <T, U, V, W, X> AttributeConverter<T, X> chaining(
            final @Nonnull AttributeConverter<T, U> attributeConverter1,
            final @Nonnull AttributeConverter<U, V> attributeConverter2,
            final @Nonnull AttributeConverter<V, W> attributeConverter3,
            final @Nonnull AttributeConverter<W, X> attributeConverter4) {
        return switch (ThreadLocalRandom.current().nextInt(3)) {
            case 0 -> chaining(
                    attributeConverter1,
                    chaining(attributeConverter2, attributeConverter3, attributeConverter4)
            );
            case 1 -> chaining(
                    chaining(attributeConverter1, attributeConverter2),
                    chaining(attributeConverter3, attributeConverter4)
            );
            default -> chaining(
                    chaining(attributeConverter1, attributeConverter2, attributeConverter3),
                    attributeConverter4
            );
        };
    }

    static <T, U, V, W, X, Y> AttributeConverter<T, Y> chaining(
            final @Nonnull AttributeConverter<T, U> attributeConverter1,
            final @Nonnull AttributeConverter<U, V> attributeConverter2,
            final @Nonnull AttributeConverter<V, W> attributeConverter3,
            final @Nonnull AttributeConverter<W, X> attributeConverter4,
            final @Nonnull AttributeConverter<X, Y> attributeConverter5) {
        return switch (ThreadLocalRandom.current().nextInt(4)) {
            case 0 -> chaining(
                    attributeConverter1,
                    chaining(attributeConverter2, attributeConverter3, attributeConverter4, attributeConverter5)
            );
            case 1 -> chaining(
                    chaining(attributeConverter1, attributeConverter2),
                    chaining(attributeConverter3, attributeConverter4, attributeConverter5)
            );
            case 2 -> chaining(
                    chaining(attributeConverter1, attributeConverter2, attributeConverter3),
                    chaining(attributeConverter4, attributeConverter5)
            );
            default -> chaining(
                    chaining(attributeConverter1, attributeConverter2, attributeConverter3, attributeConverter4),
                    attributeConverter5
            );
        };
    }

    static <T, U, V, W, X, Y, Z> AttributeConverter<T, Z> chaining(
            final @Nonnull AttributeConverter<T, U> attributeConverter1,
            final @Nonnull AttributeConverter<U, V> attributeConverter2,
            final @Nonnull AttributeConverter<V, W> attributeConverter3,
            final @Nonnull AttributeConverter<W, X> attributeConverter4,
            final @Nonnull AttributeConverter<X, Y> attributeConverter5,
            final @Nonnull AttributeConverter<Y, Z> attributeConverter6) {
        return switch (ThreadLocalRandom.current().nextInt(5)) {
            case 0 -> chaining(
                    attributeConverter1,
                    chaining(attributeConverter2, attributeConverter3, attributeConverter4, attributeConverter5,
                             attributeConverter6
                    )
            );
            case 1 -> chaining(
                    chaining(attributeConverter1, attributeConverter2),
                    chaining(attributeConverter3, attributeConverter4, attributeConverter5, attributeConverter6)
            );
            case 2 -> chaining(
                    chaining(attributeConverter1, attributeConverter2, attributeConverter3),
                    chaining(attributeConverter4, attributeConverter5, attributeConverter6)
            );
            case 3 -> chaining(
                    chaining(attributeConverter1, attributeConverter2, attributeConverter3, attributeConverter4),
                    chaining(attributeConverter5, attributeConverter6)
            );
            default -> chaining(
                    chaining(attributeConverter1, attributeConverter2, attributeConverter3, attributeConverter4,
                             attributeConverter5
                    ),
                    attributeConverter6
            );
        };
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    private __AttributeConverterUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
