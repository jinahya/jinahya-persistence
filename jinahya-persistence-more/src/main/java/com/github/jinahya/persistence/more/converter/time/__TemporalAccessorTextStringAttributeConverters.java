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
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.DayOfWeek;
import java.time.Month;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.util.Locale;

/**
 * Concrete {@link __TemporalAccessorTextStringAttributeConverter}s, one per {@code java.time} type which is a single
 * named field.
 * <p>
 * Each converter has two constructors, and only the no-argument one is ever reached by a persistence provider: a
 * converter is registered by class, and nothing in {@link jakarta.persistence.Convert @Convert} or in
 * {@code persistence.xml} can pass an argument. The no-argument one stores the
 * {@link __TemporalAccessorTextStringAttributeConverter#DEFAULT_TEXT_STYLE full standalone} name in
 * {@link __TemporalAccessorTextStringAttributeConverter#DEFAULT_LOCALE English}, never in the default locale. The one
 * taking a {@link TextStyle} and a {@link Locale} is for holding the converter directly &mdash; composed into another,
 * or delegated to from one which <em>is</em> registered. Registering a converter for another language means
 * subclassing one of these and carrying {@link Converter @Converter} on the subclass, which is a constructor and an
 * annotation:
 * {@snippet lang = "java":
 * @Converter(autoApply = false)
 * public class KoreanDayOfWeekConverter extends __TemporalAccessorTextStringAttributeConverters.OfDayOfWeek {
 *     public KoreanDayOfWeekConverter() {
 *         super(TextStyle.FULL_STANDALONE, Locale.KOREAN); // 월요일
 *     }
 * }
 * }
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __TemporalAccessorTextStringAttributeConverter
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class __TemporalAccessorTextStringAttributeConverters {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * A converter for an entity attribute of {@link DayOfWeek}, stored as the name of the day.
     *
     * @implSpec With no arguments, a value is stored as its full English name &mdash; {@code "Monday"} for
     *         {@link DayOfWeek#MONDAY} &mdash; and read back regardless of case.
     */
    @Converter(autoApply = false)
    public static class OfDayOfWeek extends __TemporalAccessorTextStringAttributeConverter<DayOfWeek>
            implements AttributeConverter<DayOfWeek, String> {

        /**
         * Creates a new instance storing the full English name of the day.
         *
         * @implNote {@code public}, not {@code protected}: a {@link Converter @Converter} class is instantiated
         *         by the persistence provider, and is documented as needing a public no-argument constructor. It also
         *         lets a caller use this converter directly, without subclassing a leaf class.
         */
        public OfDayOfWeek() {
            this(DEFAULT_TEXT_STYLE, DEFAULT_LOCALE);
        }

        /**
         * Creates a new instance storing the name of the day in the specified style and locale.
         *
         * @param textStyle the style of the name.
         * @param locale    the locale of the name.
         * @apiNote A persistence provider never calls this constructor &mdash; it instantiates a converter
         *         through the no-argument one, and neither {@link jakarta.persistence.Convert @Convert} nor
         *         {@code persistence.xml} can pass a locale. Use this to hold the converter directly, composed into
         *         another or delegated to from one which <em>is</em> registered; to register a converter for another
         *         language, subclass and carry {@link Converter @Converter} on the subclass.
         */
        public OfDayOfWeek(final TextStyle textStyle, final Locale locale) {
            super(DayOfWeek.class, ChronoField.DAY_OF_WEEK, textStyle, locale);
        }
    }

    /**
     * A converter for an entity attribute of {@link Month}, stored as the name of the month.
     *
     * @implSpec With no arguments, a value is stored as its full English name &mdash; {@code "March"} for
     *         {@link Month#MARCH} &mdash; and read back regardless of case.
     */
    @Converter(autoApply = false)
    public static class OfMonth extends __TemporalAccessorTextStringAttributeConverter<Month>
            implements AttributeConverter<Month, String> {

        /**
         * Creates a new instance storing the full English name of the month.
         */
        public OfMonth() {
            this(DEFAULT_TEXT_STYLE, DEFAULT_LOCALE);
        }

        /**
         * Creates a new instance storing the name of the month in the specified style and locale.
         *
         * @param textStyle the style of the name.
         * @param locale    the locale of the name.
         * @apiNote A persistence provider never calls this constructor &mdash; it instantiates a converter
         *         through the no-argument one, and neither {@link jakarta.persistence.Convert @Convert} nor
         *         {@code persistence.xml} can pass a locale. Use this to hold the converter directly, composed into
         *         another or delegated to from one which <em>is</em> registered; to register a converter for another
         *         language, subclass and carry {@link Converter @Converter} on the subclass.
         */
        public OfMonth(final TextStyle textStyle, final Locale locale) {
            super(Month.class, ChronoField.MONTH_OF_YEAR, textStyle, locale);
        }
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance, which is not allowed.
     */
    private __TemporalAccessorTextStringAttributeConverters() {
        throw new AssertionError("instantiation is not allowed");
    }
}
