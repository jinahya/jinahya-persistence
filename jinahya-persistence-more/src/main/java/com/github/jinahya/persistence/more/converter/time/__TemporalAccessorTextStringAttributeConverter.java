package com.github.jinahya.persistence.more.converter.time;

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
import java.time.DayOfWeek;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalField;
import java.util.Locale;
import java.util.Objects;

/**
 * An abstract class for converting {@code String} db data to an entity attribute of a specific subtype of
 * {@link TemporalAccessor} which is nothing but one named field, and vice versa.
 * <p>
 * A value is stored as the <em>name</em> of its field in a given {@link Locale} and {@link TextStyle} &mdash;
 * {@code "Monday"} for {@link DayOfWeek#MONDAY}, {@code "March"} for {@link Month#MARCH} &mdash; which is what a
 * column that was filled by {@code TO_CHAR(d, 'Day')} or by hand holds, and what the ISO-8601 form of
 * {@link __TemporalAccessorStringAttributeConverter} cannot be, since neither type has one: {@link DayOfWeek} and
 * {@link Month} declare no {@code parse(CharSequence)}. The column is read back case-insensitively, so
 * {@code "MONDAY"}, {@code "monday"} and {@code "Monday"} are all {@link DayOfWeek#MONDAY}.
 *
 * <h2>The locale is always named</h2>
 * The name of a day or a month is a fact about a language, and a column holds the names of exactly one. So a locale is
 * a constructor argument and is never taken from {@link Locale#getDefault()}: a converter which did so would write
 * {@code "Monday"} on one server and {@code "월요일"} on the next, and fail to read on either what the other wrote.
 * {@link #DEFAULT_LOCALE English} is what the registrable converters of
 * {@link __TemporalAccessorTextStringAttributeConverters} pin, because it is the one language a schema is likelier to
 * have been spelled in than the server's own.
 *
 * @param <X> temporal accessor type parameter; bounded on {@link TemporalAdjuster} as well, which with
 *            {@link TemporalAccessor} and short of {@link Temporal} is exactly what the single-field types are
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @implNote This is the {@link DateTimeFormatter} path of the superclass, with the formatter built here &mdash;
 *         {@link DateTimeFormatterBuilder#parseCaseInsensitive() case-insensitive},
 *         {@link DateTimeFormatterBuilder#appendText(TemporalField, TextStyle) one text field}, in the locale given.
 *         The write is then what {@link DayOfWeek#getDisplayName(TextStyle, Locale)} returns for the same style and
 *         locale, and the read is {@code from(TemporalAccessor)} over what the formatter parsed, which both types
 *         declare. {@link Locale#ROOT} is <em>not</em> a neutral choice for this: the JDK's locale data spells the
 *         full forms there as the short ones, {@code "Mon"} and {@code "Jan"}.
 * @see __TemporalAccessorTextStringAttributeConverters
 * @see DayOfWeek#getDisplayName(TextStyle, Locale)
 * @see Month#getDisplayName(TextStyle, Locale)
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __TemporalAccessorTextStringAttributeConverter<X extends TemporalAccessor & TemporalAdjuster>
        extends __TemporalAccessorStringAttributeConverter<X>
        implements __TemporalAdjusterAttributeConverter<X, String> {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The text style the registrable converters use when none is given; {@link TextStyle#FULL_STANDALONE}.
     *
     * @apiNote The <em>standalone</em> form is the one a name has on its own &mdash; in a column, a heading, a
     *         list &mdash; as against the form it has inside a date, which some languages inflect: Russian keeps
     *         {@code "май"} for the month and writes {@code "мая"} within {@code "1 мая"}. English makes no such
     *         distinction, and for it the two styles spell the same.
     */
    public static final TextStyle DEFAULT_TEXT_STYLE = TextStyle.FULL_STANDALONE;

    /**
     * The locale the registrable converters use when none is given; {@link Locale#ENGLISH}.
     */
    public static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Builds the formatter which writes and reads the specified field as text, in the specified style and locale.
     *
     * @param field     the field whose text the column holds.
     * @param textStyle the style of the text.
     * @param locale    the locale of the text.
     * @return a new formatter.
     */
    private static DateTimeFormatter formatter(final TemporalField field, final TextStyle textStyle,
                                               final Locale locale) {
        Objects.requireNonNull(field, "field is null");
        Objects.requireNonNull(textStyle, "textStyle is null");
        Objects.requireNonNull(locale, "locale is null");
        return new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendText(field, textStyle)
                .toFormatter(locale);
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance for the specified temporal accessor class, storing the specified field of it as text in
     * the specified style and locale.
     *
     * @param attributeClass the class of the entity attribute this converter converts.
     * @param field          the field whose text the column holds; {@link java.time.temporal.ChronoField#DAY_OF_WEEK
     *                       DAY_OF_WEEK} for a {@link DayOfWeek}.
     * @param textStyle      the style of the text; see {@link #DEFAULT_TEXT_STYLE}.
     * @param locale         the locale of the text; see {@link #DEFAULT_LOCALE}.
     * @apiNote The {@code attributeClass} has to be one whose every value is determined by the {@code field}
     *         alone, and which declares {@code public static from(TemporalAccessor)} &mdash; what the read rebuilds
     *         the value with out of the one field the column holds. A type with more fields than that loses them in
     *         the column, and the read fails the way
     *         {@link __TemporalAccessorStringAttributeConverter#convertToEntityAttribute(String) the superclass}
     *         describes for a lossy formatter.
     */
    protected __TemporalAccessorTextStringAttributeConverter(final Class<X> attributeClass, final TemporalField field,
                                                             final TextStyle textStyle, final Locale locale) {
        super(attributeClass, formatter(field, textStyle, locale));
        this.field = field;
        this.textStyle = textStyle;
        this.locale = locale;
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The field whose text the column holds.
     */
    protected final transient TemporalField field;

    /**
     * The style of the text the column holds.
     */
    protected final TextStyle textStyle;

    /**
     * The locale of the text the column holds.
     */
    protected final Locale locale;
}
