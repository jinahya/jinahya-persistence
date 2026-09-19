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

import jakarta.persistence.Converter;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Method;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Objects;

/**
 * An abstract class for converting {@code String} db data to an entity attribute of a specific subtype of
 * {@link TemporalAccessor}, and vice versa.
 * <p>
 * A value is stored in the ISO-8601 form its own type reads back &mdash; what {@code toString()} writes and
 * {@code parse(CharSequence)} reads &mdash; so the column is legible, sorts chronologically for every type of fixed
 * width, and carries no database-specific date/time semantics. Both directions have a default here, so a subclass
 * declares nothing but its class.
 * <p>
 * A {@link DateTimeFormatter} may be given instead, and then it is what writes and reads the column &mdash; for a
 * column whose format was decided elsewhere, or a shorter one than the ISO-8601 form. It has to be lossless for the
 * type: whatever the formatter does not write cannot be read back, so a pattern which drops a field the type carries
 * makes the round trip fail, or quietly return a different value.
 *
 * @param <X> temporal accessor type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @apiNote Jakarta Persistence already maps {@link LocalDate}, {@link LocalDateTime}, {@link LocalTime},
 *         {@link OffsetDateTime}, {@link OffsetTime} and {@link Instant} to real date/time columns, and every converter
 *         here is {@link Converter#autoApply() autoApply = false} so that none of that changes by accident. Reach for
 *         one of these only where the text form is wanted deliberately &mdash; a column shared with a system which
 *         reads ISO-8601, a type the provider does not map ({@link Year}, {@link YearMonth}, {@link MonthDay}), or a
 *         {@link ZonedDateTime}, whose zone id no {@code TIMESTAMP} column can hold.
 * @see __TemporalAmountStringAttributeConverter
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __TemporalAccessorStringAttributeConverter<X extends TemporalAccessor>
        implements __StringAttributeConverter<X> {

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance for the specified temporal accessor class, using the type's own ISO-8601 form.
     *
     * @param attributeClass the class of the entity attribute this converter converts.
     * @see #attributeClass
     * @see #__TemporalAccessorStringAttributeConverter(Class, DateTimeFormatter)
     */
    protected __TemporalAccessorStringAttributeConverter(final Class<X> attributeClass) {
        this(attributeClass, null);
    }

    /**
     * Creates a new instance for the specified temporal accessor class, formatting the column with the specified
     * formatter.
     *
     * @param attributeClass the class of the entity attribute this converter converts.
     * @param formatter      the formatter which writes and reads the column; {@code null} to use the type's own
     *                       ISO-8601 form.
     * @implNote The static factory the read defaults to is found here, once per instance, rather than on every
     *         read &mdash; {@code parse(CharSequence)} with no formatter, {@code from(TemporalAccessor)} with one. A
     *         class which has neither is <em>not</em> rejected: a subclass which overrides the read never needs one,
     *         and only a subclass which does neither finds out, from {@link #convertToEntityAttribute(String)}, that
     *         there is nothing to call. Not every {@link TemporalAccessor} has a {@code parse} &mdash;
     *         {@link java.time.Month Month} and {@link java.time.ZoneOffset ZoneOffset} do not, which is why neither
     *         has a converter here.
     * @see #attributeClass
     * @see #formatter
     */
    protected __TemporalAccessorStringAttributeConverter(final Class<X> attributeClass,
                                                         final @Nullable DateTimeFormatter formatter) {
        super();
        this.attributeClass = Objects.requireNonNull(attributeClass, "attributeClass is null");
        this.formatter = formatter;
        factoryMethod = this.formatter == null
                ? ___Utils.findStaticFactory(this.attributeClass, "parse", CharSequence.class)
                : ___Utils.findStaticFactory(this.attributeClass, "from", TemporalAccessor.class);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Converts the specified entity attribute to a database column value.
     *
     * @param attribute the entity attribute to convert.
     * @return a database column value; {@code null} when {@code attribute} is {@code null}.
     * @implSpec With no {@link #formatter}, the default implementation returns
     *         {@link Object#toString() attribute.toString()}: every {@code java.time} type here spells itself in the
     *         ISO-8601 form its own {@code parse} reads, so the write side is the same for all of them. With one, it
     *         returns {@link DateTimeFormatter#format(TemporalAccessor) formatter.format(attribute)}, which throws
     *         {@link java.time.DateTimeException DateTimeException} if the formatter asks for a field the attribute
     *         does not carry. A subclass whose type holds to neither has to override this method along with
     *         {@link #convertToEntityAttribute(String)}.
     */
    @Override
    public @Nullable String convertToDatabaseColumn(final @Nullable X attribute) {
        if (attribute == null) {
            return null;
        }
        if (formatter != null) {
            return formatter.format(attribute);
        }
        return attribute.toString();
    }

    /**
     * Converts the specified database column value to an entity attribute.
     *
     * @param dbData the database column value to convert.
     * @return an entity attribute; {@code null} when {@code dbData} is {@code null}.
     * @throws UnsupportedOperationException when {@link #attributeClass} declares no static factory to read the column
     *                                       with, and this method has not been overridden.
     * @implSpec With no {@link #formatter}, the default implementation invokes the
     *         {@code public static parse(CharSequence)} method of {@link #attributeClass}. With one, the formatter
     *         parses the column and {@code public static from(TemporalAccessor)} rebuilds the attribute out of what it
     *         produced &mdash; the step a {@link DateTimeFormatter} alone cannot do, since it yields a bare
     *         {@link TemporalAccessor} and not the attribute's own type. Either factory is found once, when this
     *         converter was constructed. Whatever it throws &mdash; a
     *         {@link java.time.format.DateTimeParseException DateTimeParseException} for a column which does not hold
     *         the expected form &mdash; is thrown from here unchanged.
     */
    @Override
    public @Nullable X convertToEntityAttribute(final @Nullable String dbData) {
        if (dbData == null) {
            return null;
        }
        if (factoryMethod == null) {
            throw ___Utils.noStaticFactory(
                    attributeClass, formatter == null ? "parse(CharSequence)" : "from(TemporalAccessor)", this);
        }
        return ___Utils.invokeStaticFactory(
                factoryMethod, attributeClass, formatter == null ? dbData : formatter.parse(dbData));
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The class of the entity attribute this converter converts.
     */
    protected final Class<X> attributeClass;

    /**
     * The formatter which writes and reads the column; {@code null} when the type's own ISO-8601 form is used.
     */
    protected final transient @Nullable DateTimeFormatter formatter;

    /**
     * The static factory {@link #convertToEntityAttribute(String)} reads a column with &mdash;
     * {@code parse(CharSequence)} with no {@link #formatter}, {@code from(TemporalAccessor)} with one; {@code null}
     * when {@link #attributeClass} declares neither.
     */
    private final transient @Nullable Method factoryMethod;
}
