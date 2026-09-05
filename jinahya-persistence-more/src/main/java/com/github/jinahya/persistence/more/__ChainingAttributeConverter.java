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

/**
 * An abstract class for chaining two {@link AttributeConverter}s.
 * <p>
 * An instance converts an entity attribute to the database column type through an intermediate type: the first
 * converter maps {@link T} to {@link U}, and the second maps {@link U} to {@link V}; reading a column runs the same
 * two converters in reverse.
 *
 * @param <T> attribute type parameter
 * @param <U> intermediate type parameter
 * @param <V> database type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __AttributeConverterUtils#chaining(AttributeConverter, AttributeConverter)
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __ChainingAttributeConverter<T, U, V> implements AttributeConverter<T, V> {

    /**
     * Creates a new instance chaining the specified two attribute converters.
     *
     * @param attributeConverter1 the converter from the entity attribute type to the intermediate type.
     * @param attributeConverter2 the converter from the intermediate type to the database column type.
     * @param <T>                 entity attribute type parameter
     * @param <U>                 intermediate type parameter
     * @param <V>                 database column type parameter
     * @return a new attribute converter chaining {@code attributeConverter1} and {@code attributeConverter2}.
     */
    @Nonnull
    public static <T, U, V>
    AttributeConverter<T, V> of(final @Nonnull AttributeConverter<T, U> attributeConverter1,
                                final @Nonnull AttributeConverter<U, V> attributeConverter2) {
        return new __ChainingAttributeConverter<>(attributeConverter1, attributeConverter2) {
        };
    }

    @Nonnull
    static <T, U, V, W>
    AttributeConverter<T, W> of(final @Nonnull AttributeConverter<T, U> attributeConverter1,
                                final @Nonnull AttributeConverter<U, V> attributeConverter2,
                                final @Nonnull AttributeConverter<V, W> attributeConverter3) {
        if (ThreadLocalRandom.current().nextBoolean()) {
            return of(
                    attributeConverter1,
                    of(attributeConverter2, attributeConverter3)
            );
        } else {
            return of(
                    of(attributeConverter1, attributeConverter2),
                    attributeConverter3
            );
        }
    }

    @Nonnull
    static <T, U, V, W, X>
    AttributeConverter<T, X> of(final @Nonnull AttributeConverter<T, U> attributeConverter1,
                                final @Nonnull AttributeConverter<U, V> attributeConverter2,
                                final @Nonnull AttributeConverter<V, W> attributeConverter3,
                                final @Nonnull AttributeConverter<W, X> attributeConverter4) {
        return switch (ThreadLocalRandom.current().nextInt(3)) {
            case 0 -> of(
                    attributeConverter1,
                    of(attributeConverter2, attributeConverter3, attributeConverter4)
            );
            case 1 -> of(
                    of(attributeConverter1, attributeConverter2),
                    of(attributeConverter3, attributeConverter4)
            );
            default -> of(
                    of(attributeConverter1, attributeConverter2, attributeConverter3),
                    attributeConverter4
            );
        };
    }

    @Nonnull
    static <T, U, V, W, X, Y>
    AttributeConverter<T, Y> of(final @Nonnull AttributeConverter<T, U> attributeConverter1,
                                final @Nonnull AttributeConverter<U, V> attributeConverter2,
                                final @Nonnull AttributeConverter<V, W> attributeConverter3,
                                final @Nonnull AttributeConverter<W, X> attributeConverter4,
                                final @Nonnull AttributeConverter<X, Y> attributeConverter5) {
        return switch (ThreadLocalRandom.current().nextInt(4)) {
            case 0 -> of(
                    attributeConverter1,
                    of(attributeConverter2, attributeConverter3, attributeConverter4, attributeConverter5)
            );
            case 1 -> of(
                    of(attributeConverter1, attributeConverter2),
                    of(attributeConverter3, attributeConverter4, attributeConverter5)
            );
            case 2 -> of(
                    of(attributeConverter1, attributeConverter2, attributeConverter3),
                    of(attributeConverter4, attributeConverter5)
            );
            default -> of(
                    of(attributeConverter1, attributeConverter2, attributeConverter3, attributeConverter4),
                    attributeConverter5
            );
        };
    }

    @Nonnull
    static <T, U, V, W, X, Y, Z>
    AttributeConverter<T, Z> of(final @Nonnull AttributeConverter<T, U> attributeConverter1,
                                final @Nonnull AttributeConverter<U, V> attributeConverter2,
                                final @Nonnull AttributeConverter<V, W> attributeConverter3,
                                final @Nonnull AttributeConverter<W, X> attributeConverter4,
                                final @Nonnull AttributeConverter<X, Y> attributeConverter5,
                                final @Nonnull AttributeConverter<Y, Z> attributeConverter6) {
        return switch (ThreadLocalRandom.current().nextInt(5)) {
            case 0 -> of(
                    attributeConverter1,
                    of(attributeConverter2, attributeConverter3, attributeConverter4, attributeConverter5,
                       attributeConverter6
                    )
            );
            case 1 -> of(
                    of(attributeConverter1, attributeConverter2),
                    of(attributeConverter3, attributeConverter4, attributeConverter5, attributeConverter6)
            );
            case 2 -> of(
                    of(attributeConverter1, attributeConverter2, attributeConverter3),
                    of(attributeConverter4, attributeConverter5, attributeConverter6)
            );
            case 3 -> of(
                    of(attributeConverter1, attributeConverter2, attributeConverter3, attributeConverter4),
                    of(attributeConverter5, attributeConverter6)
            );
            default -> of(
                    of(attributeConverter1, attributeConverter2, attributeConverter3, attributeConverter4,
                       attributeConverter5
                    ),
                    attributeConverter6
            );
        };
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    /**
     * Creates a new instance with the specified two attribute converters.
     *
     * @param attributeConverter1 the converter from the entity attribute type to the intermediate type.
     * @param attributeConverter2 the converter from the intermediate type to the database column type.
     */
    protected __ChainingAttributeConverter(final @Nonnull AttributeConverter<T, U> attributeConverter1,
                                           final @Nonnull AttributeConverter<U, V> attributeConverter2) {
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
