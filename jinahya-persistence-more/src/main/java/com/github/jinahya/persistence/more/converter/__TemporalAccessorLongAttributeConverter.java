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

import java.time.temporal.TemporalAccessor;
import java.util.Objects;
import java.util.function.LongFunction;
import java.util.function.ToLongFunction;

/**
 * An abstract class for converting {@code Long} db data to an entity attribute of a specific subtype of
 * {@link TemporalAccessor}, and vice versa.
 * <p>
 * A value is stored as the one integral coordinate its own type is measured by &mdash; a count of nanoseconds since
 * midnight, a count of days since the epoch &mdash; so the column is compact, exact to the last digit the type carries,
 * and identical on every database, which a {@code TIME} or {@code TIMESTAMP} column is not.
 *
 * <h2>Why a pair of functions, where the text form needed none</h2>
 * {@link __TemporalAccessorStringAttributeConverter} defaults both directions for every type it serves, because
 * {@code java.time} spells every one of them with {@code toString()} and reads it back with
 * {@code parse(CharSequence)}. There is no such pair for numbers. Each type is measured by a coordinate of its own,
 * under its own name &mdash; {@link java.time.LocalTime#toNanoOfDay()} against
 * {@link java.time.LocalTime#ofNanoOfDay(long)}, {@link java.time.LocalDate#toEpochDay()} against
 * {@link java.time.LocalDate#ofEpochDay(long)} &mdash; and nothing declares them in common. So a subclass names the
 * two, and the reflection the text converter needs is not needed here.
 *
 * <h2>The encoding has to be order-preserving, and lossless</h2>
 * Two requirements, and a subclass which breaks either produces a column which cannot do what it was stored for.
 * <p>
 * <strong>Order.</strong> The number must increase exactly as the value does. A column of these is stored to be
 * compared &mdash; {@code start <= :t AND end > :t} against an ordinary index &mdash; and an encoding which reorders
 * anything makes every such comparison wrong, silently. Both coordinates named above hold to this; a packed decimal
 * such as {@code 20260920} happens to as well, while an encoding which puts a component in front of a more significant
 * one does not.
 * <p>
 * <strong>Loss.</strong> Whatever the number does not carry cannot be read back.
 * {@link java.time.Instant#toEpochMilli()} is the cautionary one: it is exactly the shape this class serves and it
 * drops the sub-millisecond part of an {@link java.time.Instant}, so a value written through it does not survive the
 * round trip. It is why no {@code Instant} converter appears in {@link __TemporalAccessorLongAttributeConverters}.
 *
 * @param <X> temporal accessor type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @apiNote Jakarta Persistence maps {@link java.time.LocalDate}, {@link java.time.LocalTime} and their siblings
 *         to real date/time columns, and every converter here is
 *         {@link jakarta.persistence.Converter#autoApply() autoApply = false} so that none of that changes by accident.
 *         Reach for one of these where a real column is the problem rather than the answer &mdash; where the type has
 *         no column at all on some database in the estate, or where the precision a column keeps differs across them.
 * @see __TemporalAccessorStringAttributeConverter
 * @see __TemporalAccessorLongAttributeConverters
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __TemporalAccessorLongAttributeConverter<X extends TemporalAccessor>
        implements __LongAttributeConverter<X> {

    // --------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance for the specified temporal accessor class, measuring and rebuilding a value with the
     * specified pair of functions.
     *
     * @param attributeClass the class of the entity attribute this converter converts.
     * @param encoder        the function which measures an attribute as the number written to the column.
     * @param decoder        the function which rebuilds an attribute out of the number read from the column.
     * @throws NullPointerException when any argument is {@code null}.
     * @apiNote The two have to be inverses of one another, and the {@code encoder} has to be increasing;
     *         neither is checked here, and the consequences of breaking either are in this class's own documentation.
     */
    protected __TemporalAccessorLongAttributeConverter(final Class<X> attributeClass,
                                                       final ToLongFunction<? super X> encoder,
                                                       final LongFunction<? extends X> decoder) {
        super();
        this.attributeClass = Objects.requireNonNull(attributeClass, "attributeClass is null");
        this.encoder = Objects.requireNonNull(encoder, "encoder is null");
        this.decoder = Objects.requireNonNull(decoder, "decoder is null");
    }

    // ----------------------------------------------------------------------------------------------------------------

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
     *         Whatever it throws for a number outside the range the type accepts &mdash; a
     *         {@link java.time.DateTimeException DateTimeException} &mdash; is thrown from here unchanged, which is
     *         what a column holding something other than what this converter wrote deserves.
     */
    @Override
    public @Nullable X convertToEntityAttribute(final @Nullable Long dbData) {
        if (dbData == null) {
            return null;
        }
        return decoder.apply(dbData);
    }

    // ----------------------------------------------------------------------------------------------------------------

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
