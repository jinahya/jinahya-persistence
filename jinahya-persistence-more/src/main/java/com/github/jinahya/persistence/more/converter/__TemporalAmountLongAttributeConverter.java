package com.github.jinahya.persistence.more.converter;

/*-
 * #%L
 * jinahya-persistence-more
 * %%
 * Copyright (C) 2025 - 2026 Jinahya, Inc.
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

import org.jspecify.annotations.Nullable;

import java.time.temporal.TemporalAmount;
import java.util.Objects;
import java.util.function.LongFunction;
import java.util.function.ToLongFunction;

/**
 * An abstract class for converting {@code Long} db data to an entity attribute of a specific subtype of
 * {@link TemporalAmount}, and vice versa.
 * <p>
 * This is {@link __TemporalAccessorLongAttributeConverter} for an <em>amount</em> rather than a point, and it exists
 * for the same reason: a number in a column orders the way the values order, where text does not.
 *
 * <h2>Why an amount needs this more than a point does</h2>
 * A point type usually has a column that already sorts — a {@code DATE} sorts, and the ISO {@code uuuu-MM} text of a
 * {@link java.time.YearMonth} sorts because it is fixed-width. An amount has neither. Jakarta Persistence 3.2 lists no
 * {@link java.time.Duration} among its basic types, so an unconverted one lands in the column as bytes; and its
 * ISO-8601 text, which {@link __TemporalAmountStringAttributeConverter} writes, does not sort: {@code "PT5H"} is above
 * {@code "PT48H"} lexicographically and below it in fact.
 * <p>
 * That is harmless where the column is only read back, which is what the string converters are for. It is fatal where
 * the column is compared — an amount used as an endpoint in
 * {@link com.github.jinahya.persistence.more.orderedrange the ordered-range package}, or as any bound a query filters
 * on. This converter is for that case.
 *
 * @param <X> temporal amount type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @apiNote There is deliberately no converter here for {@link java.time.Period}, and it is not an omission.
 *         {@code P1M} and {@code P30D} have no defined order — which is why {@code Period} is not {@link Comparable} —
 *         so no single number can encode one without inventing a month length. Where a period has to be stored,
 *         {@link __TemporalAmountStringAttributeConverters.OfPeriod} keeps it exactly, and the column simply cannot be
 *         ordered.
 * @see __TemporalAmountLongAttributeConverters.OfDuration
 * @see __TemporalAmountStringAttributeConverter
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __TemporalAmountLongAttributeConverter<X extends TemporalAmount>
        implements __LongAttributeConverter<X> {

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance for the specified temporal amount class, measuring and rebuilding a value with the
     * specified pair of functions.
     *
     * @param attributeClass the class of the entity attribute this converter converts.
     * @param encoder        the function which measures an attribute as the number written to the column.
     * @param decoder        the function which rebuilds an attribute out of the number read from the column.
     * @throws NullPointerException when any argument is {@code null}.
     * @apiNote The two have to be inverses of one another, and the {@code encoder} has to be increasing;
     *         neither is checked here.
     */
    protected __TemporalAmountLongAttributeConverter(final Class<X> attributeClass,
                                                     final ToLongFunction<? super X> encoder,
                                                     final LongFunction<? extends X> decoder) {
        super();
        this.attributeClass = Objects.requireNonNull(attributeClass, "attributeClass is null");
        this.encoder = Objects.requireNonNull(encoder, "encoder is null");
        this.decoder = Objects.requireNonNull(decoder, "decoder is null");
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Converts the specified entity attribute to a database column value.
     *
     * @param attribute the entity attribute to convert.
     * @return a database column value; {@code null} when {@code attribute} is {@code null}.
     * @implSpec The default implementation applies the {@code encoder} this converter was constructed with.
     */
    @Override
    public @Nullable Long convertToDatabaseColumn(final @Nullable X attribute) {
        if (attribute == null) {
            return null;
        }
        return encoder.applyAsLong(attribute);
    }

    /**
     * Converts the specified database column value to an entity attribute.
     *
     * @param dbData the database column value to convert.
     * @return an entity attribute; {@code null} when {@code dbData} is {@code null}.
     * @implSpec The default implementation applies the {@code decoder} this converter was constructed with.
     */
    @Override
    public @Nullable X convertToEntityAttribute(final @Nullable Long dbData) {
        if (dbData == null) {
            return null;
        }
        return decoder.apply(dbData);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The class of the entity attribute this converter converts.
     */
    protected final Class<X> attributeClass;

    /**
     * The function which measures an attribute as the number written to the column.
     */
    protected final transient ToLongFunction<? super X> encoder;

    /**
     * The function which rebuilds an attribute out of the number read from the column.
     */
    protected final transient LongFunction<? extends X> decoder;
}
