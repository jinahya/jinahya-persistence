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

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Period;
import java.time.temporal.TemporalAmount;
import java.util.Objects;

/**
 * An abstract class for converting {@code String} db data to an entity attribute of a specific subtype of
 * {@link TemporalAmount}, and vice versa.
 * <p>
 * A value is stored in the ISO-8601 form its own type reads back, which keeps the column legible and independent of any
 * database's interval type. Both directions have a default here, so a subclass for a well-behaved type declares nothing
 * but its class &mdash; a constructor calling {@code super(Days.class)}, and a
 * {@link jakarta.persistence.Converter @Converter} on the class where the persistence unit has to know about it, is the
 * whole of one. {@link __TemporalAmountStringAttributeConverters} holds the two written this way here.
 *
 * @param <X> temporal amount type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @apiNote There is deliberately no converter for {@link TemporalAmount} itself. The two ISO-8601 forms overlap
 *         &mdash; {@code "P2D"} is a valid {@link Duration} (of {@code 48} hours) <em>and</em> a valid {@link Period}
 *         (of two days), and the two are not the same amount across a daylight-saving boundary &mdash; so a column
 *         alone does not say which type wrote it. The attribute's declared type is what settles it, and that is known
 *         only to the entity. Pick the converter which matches the attribute.
 * @see __TemporalAmountStringAttributeConverters.OfDuration
 * @see __TemporalAmountStringAttributeConverters.OfPeriod
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __TemporalAmountStringAttributeConverter<X extends TemporalAmount>
        implements __StringAttributeConverter<X> {

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance for the specified temporal amount class.
     *
     * @param attributeClass the class of the entity attribute this converter converts.
     * @implNote The {@code parse(CharSequence)} method which {@link #convertToEntityAttribute(String)} defaults
     *         to is looked up here, once per instance, rather than on every read. A class which has no such method is
     *         <em>not</em> rejected: a subclass which overrides the read never needs one, and only a subclass which
     *         does neither finds out, from {@code convertToEntityAttribute}, that there is nothing to call.
     * @see #attributeClass
     */
    protected __TemporalAmountStringAttributeConverter(final Class<X> attributeClass) {
        super();
        this.attributeClass = Objects.requireNonNull(attributeClass, "attributeClass is null");
        parseMethod = ___Utils.findStaticFactory(this.attributeClass, "parse", CharSequence.class);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Converts the specified entity attribute to a database column value.
     *
     * @param attribute the entity attribute to convert.
     * @return a database column value; {@code null} when {@code attribute} is {@code null}.
     * @implSpec The default implementation returns {@link Object#toString() attribute.toString()}. Both
     *         {@link Duration} and {@link Period} spell themselves in the ISO-8601 form their own {@code parse} reads,
     *         so the write side is the same for either. A subclass whose type does not hold to that has to override
     *         this method along with {@link #convertToEntityAttribute(String)}.
     */
    @Override
    public @Nullable String convertToDatabaseColumn(final @Nullable X attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.toString();
    }

    /**
     * Converts the specified database column value to an entity attribute.
     *
     * @param dbData the database column value to convert.
     * @return an entity attribute; {@code null} when {@code dbData} is {@code null}.
     * @throws UnsupportedOperationException when {@link #attributeClass} declares no
     *                                       {@code public static parse(CharSequence)} method, and this method has not
     *                                       been overridden.
     * @implSpec The default implementation invokes the {@code public static parse(CharSequence)} method of
     *         {@link #attributeClass}, found once when this converter was constructed. Whatever that method throws
     *         &mdash; a {@link java.time.format.DateTimeParseException DateTimeParseException} for a column which does
     *         not hold the type's ISO-8601 form &mdash; is thrown from here unchanged.
     */
    @Override
    public @Nullable X convertToEntityAttribute(final @Nullable String dbData) {
        if (dbData == null) {
            return null;
        }
        if (parseMethod == null) {
            throw ___Utils.noStaticFactory(attributeClass, "parse(CharSequence)", this);
        }
        return ___Utils.invokeStaticFactory(parseMethod, attributeClass, dbData);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The class of the entity attribute this converter converts.
     */
    protected final Class<X> attributeClass;

    /**
     * The {@code public static parse(CharSequence)} method of {@link #attributeClass}; {@code null} when it declares
     * none.
     */
    private final transient @Nullable Method parseMethod;
}
