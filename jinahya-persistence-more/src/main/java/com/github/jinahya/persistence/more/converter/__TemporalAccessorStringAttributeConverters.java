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
import java.util.Objects;

/**
 * Concrete {@link __TemporalAccessorStringAttributeConverter}s, one per {@code java.time} type which can be written as
 * text and read back.
 * <p>
 * {@link java.time.Month Month} and {@link java.time.ZoneOffset ZoneOffset} are absent: both are
 * {@link java.time.temporal.TemporalAccessor TemporalAccessor}s, and neither declares the {@code parse(CharSequence)}
 * the read defaults to.
 * <p>
 * Each converter has two constructors, and only the no-argument one is ever reached by a persistence provider: a
 * converter is registered by class, and nothing in {@link jakarta.persistence.Convert @Convert} or in
 * {@code persistence.xml} can pass an argument. The one taking a {@link java.time.format.DateTimeFormatter} is for
 * holding the converter directly &mdash; composed into another, or delegated to from one which <em>is</em> registered.
 * Registering a <em>formatted</em> converter means subclassing one of these and carrying
 * {@link jakarta.persistence.Converter @Converter} on the subclass, which is a constructor and an annotation.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __TemporalAccessorStringAttributeConverter
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class __TemporalAccessorStringAttributeConverters {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * A converter for an entity attribute of {@link Instant}.
     *
     * @implSpec A value is stored as {@link Instant#toString()}, always in UTC and always ending in
     *         {@code "Z"}.
     */
    @Converter(autoApply = false)
    public static class OfInstant extends __TemporalAccessorStringAttributeConverter<Instant> {

        /**
         * Creates a new instance.
         *
         * @implNote {@code public}, not {@code protected}: a {@link Converter @Converter} class is instantiated
         *         by the persistence provider, and is documented as needing a public no-argument constructor. It also
         *         lets a caller use this converter directly, without subclassing a leaf class.
         */
        public OfInstant() {
            super(Instant.class);
        }

        /**
         * Creates a new instance formatting the column with the specified formatter.
         *
         * @param formatter the formatter which writes and reads the column.
         * @apiNote A persistence provider never calls this constructor &mdash; it instantiates a converter
         *         through the no-argument one, and neither {@link jakarta.persistence.Convert @Convert} nor
         *         {@code persistence.xml} can pass a formatter. Use this to hold the converter directly, composed into
         *         another or delegated to from one which <em>is</em> registered; to register a formatted converter,
         *         subclass and carry {@link Converter @Converter} on the subclass.
         */
        public OfInstant(final DateTimeFormatter formatter) {
            super(Instant.class, Objects.requireNonNull(formatter, "formatter is null"));
        }
    }

    /**
     * A converter for an entity attribute of {@link LocalDate}.
     *
     * @implSpec A value is stored as {@link LocalDate#toString()}, e.g. {@code "2026-09-19"}.
     */
    @Converter(autoApply = false)
    public static class OfLocalDate extends __TemporalAccessorStringAttributeConverter<LocalDate> {

        /**
         * Creates a new instance.
         */
        public OfLocalDate() {
            super(LocalDate.class);
        }

        /**
         * Creates a new instance formatting the column with the specified formatter.
         *
         * @param formatter the formatter which writes and reads the column.
         * @apiNote A persistence provider never calls this constructor &mdash; it instantiates a converter
         *         through the no-argument one, and neither {@link jakarta.persistence.Convert @Convert} nor
         *         {@code persistence.xml} can pass a formatter. Use this to hold the converter directly, composed into
         *         another or delegated to from one which <em>is</em> registered; to register a formatted converter,
         *         subclass and carry {@link Converter @Converter} on the subclass.
         */
        public OfLocalDate(final DateTimeFormatter formatter) {
            super(LocalDate.class, Objects.requireNonNull(formatter, "formatter is null"));
        }
    }

    /**
     * A converter for an entity attribute of {@link LocalDateTime}.
     *
     * @implSpec A value is stored as {@link LocalDateTime#toString()}, whose seconds and fraction are omitted
     *         when zero &mdash; {@code "2026-09-19T11:22"} is what a whole minute writes, and it reads back as the same
     *         value.
     */
    @Converter(autoApply = false)
    public static class OfLocalDateTime extends __TemporalAccessorStringAttributeConverter<LocalDateTime> {

        /**
         * Creates a new instance.
         */
        public OfLocalDateTime() {
            super(LocalDateTime.class);
        }

        /**
         * Creates a new instance formatting the column with the specified formatter.
         *
         * @param formatter the formatter which writes and reads the column.
         * @apiNote A persistence provider never calls this constructor &mdash; it instantiates a converter
         *         through the no-argument one, and neither {@link jakarta.persistence.Convert @Convert} nor
         *         {@code persistence.xml} can pass a formatter. Use this to hold the converter directly, composed into
         *         another or delegated to from one which <em>is</em> registered; to register a formatted converter,
         *         subclass and carry {@link Converter @Converter} on the subclass.
         */
        public OfLocalDateTime(final DateTimeFormatter formatter) {
            super(LocalDateTime.class, Objects.requireNonNull(formatter, "formatter is null"));
        }
    }

    /**
     * A converter for an entity attribute of {@link LocalTime}.
     *
     * @implSpec A value is stored as {@link LocalTime#toString()}, whose seconds and fraction are omitted when
     *         zero.
     */
    @Converter(autoApply = false)
    public static class OfLocalTime extends __TemporalAccessorStringAttributeConverter<LocalTime> {

        /**
         * Creates a new instance.
         */
        public OfLocalTime() {
            super(LocalTime.class);
        }

        /**
         * Creates a new instance formatting the column with the specified formatter.
         *
         * @param formatter the formatter which writes and reads the column.
         * @apiNote A persistence provider never calls this constructor &mdash; it instantiates a converter
         *         through the no-argument one, and neither {@link jakarta.persistence.Convert @Convert} nor
         *         {@code persistence.xml} can pass a formatter. Use this to hold the converter directly, composed into
         *         another or delegated to from one which <em>is</em> registered; to register a formatted converter,
         *         subclass and carry {@link Converter @Converter} on the subclass.
         */
        public OfLocalTime(final DateTimeFormatter formatter) {
            super(LocalTime.class, Objects.requireNonNull(formatter, "formatter is null"));
        }
    }

    /**
     * A converter for an entity attribute of {@link OffsetDateTime}.
     *
     * @implSpec A value is stored as {@link OffsetDateTime#toString()}, offset included; the offset is
     *         preserved as written, not normalized to UTC.
     */
    @Converter(autoApply = false)
    public static class OfOffsetDateTime extends __TemporalAccessorStringAttributeConverter<OffsetDateTime> {

        /**
         * Creates a new instance.
         */
        public OfOffsetDateTime() {
            super(OffsetDateTime.class);
        }

        /**
         * Creates a new instance formatting the column with the specified formatter.
         *
         * @param formatter the formatter which writes and reads the column.
         * @apiNote A persistence provider never calls this constructor &mdash; it instantiates a converter
         *         through the no-argument one, and neither {@link jakarta.persistence.Convert @Convert} nor
         *         {@code persistence.xml} can pass a formatter. Use this to hold the converter directly, composed into
         *         another or delegated to from one which <em>is</em> registered; to register a formatted converter,
         *         subclass and carry {@link Converter @Converter} on the subclass.
         */
        public OfOffsetDateTime(final DateTimeFormatter formatter) {
            super(OffsetDateTime.class, Objects.requireNonNull(formatter, "formatter is null"));
        }
    }

    /**
     * A converter for an entity attribute of {@link OffsetTime}.
     *
     * @implSpec A value is stored as {@link OffsetTime#toString()}, offset included.
     */
    @Converter(autoApply = false)
    public static class OfOffsetTime extends __TemporalAccessorStringAttributeConverter<OffsetTime> {

        /**
         * Creates a new instance.
         */
        public OfOffsetTime() {
            super(OffsetTime.class);
        }

        /**
         * Creates a new instance formatting the column with the specified formatter.
         *
         * @param formatter the formatter which writes and reads the column.
         * @apiNote A persistence provider never calls this constructor &mdash; it instantiates a converter
         *         through the no-argument one, and neither {@link jakarta.persistence.Convert @Convert} nor
         *         {@code persistence.xml} can pass a formatter. Use this to hold the converter directly, composed into
         *         another or delegated to from one which <em>is</em> registered; to register a formatted converter,
         *         subclass and carry {@link Converter @Converter} on the subclass.
         */
        public OfOffsetTime(final DateTimeFormatter formatter) {
            super(OffsetTime.class, Objects.requireNonNull(formatter, "formatter is null"));
        }
    }

    /**
     * A converter for an entity attribute of {@link ZonedDateTime}.
     *
     * @implSpec A value is stored as {@link ZonedDateTime#toString()}, which appends the zone id in brackets
     *         &mdash; {@code "2026-09-19T11:22:33+09:00[Asia/Seoul]"} &mdash; and reads back with the zone intact.
     * @apiNote This is the type with a real reason to be text: a {@code TIMESTAMP WITH TIME ZONE} column holds
     *         an offset, not a zone, so a provider's native mapping loses which zone the value was in. It is also the
     *         longest form here; size the column for the zone ids actually stored.
     */
    @Converter(autoApply = false)
    public static class OfZonedDateTime extends __TemporalAccessorStringAttributeConverter<ZonedDateTime> {

        /**
         * Creates a new instance.
         */
        public OfZonedDateTime() {
            super(ZonedDateTime.class);
        }

        /**
         * Creates a new instance formatting the column with the specified formatter.
         *
         * @param formatter the formatter which writes and reads the column.
         * @apiNote A persistence provider never calls this constructor &mdash; it instantiates a converter
         *         through the no-argument one, and neither {@link jakarta.persistence.Convert @Convert} nor
         *         {@code persistence.xml} can pass a formatter. Use this to hold the converter directly, composed into
         *         another or delegated to from one which <em>is</em> registered; to register a formatted converter,
         *         subclass and carry {@link Converter @Converter} on the subclass.
         */
        public OfZonedDateTime(final DateTimeFormatter formatter) {
            super(ZonedDateTime.class, Objects.requireNonNull(formatter, "formatter is null"));
        }
    }

    /**
     * A converter for an entity attribute of {@link Year}.
     *
     * @implSpec A value is stored as {@link Year#toString()}, which is the plain number for the four-digit
     *         years and a signed, wider form outside them &mdash; {@code "+12345"}, {@code "-0001"}.
     */
    @Converter(autoApply = false)
    public static class OfYear extends __TemporalAccessorStringAttributeConverter<Year> {

        /**
         * Creates a new instance.
         */
        public OfYear() {
            super(Year.class);
        }

        /**
         * Creates a new instance formatting the column with the specified formatter.
         *
         * @param formatter the formatter which writes and reads the column.
         * @apiNote A persistence provider never calls this constructor &mdash; it instantiates a converter
         *         through the no-argument one, and neither {@link jakarta.persistence.Convert @Convert} nor
         *         {@code persistence.xml} can pass a formatter. Use this to hold the converter directly, composed into
         *         another or delegated to from one which <em>is</em> registered; to register a formatted converter,
         *         subclass and carry {@link Converter @Converter} on the subclass.
         */
        public OfYear(final DateTimeFormatter formatter) {
            super(Year.class, Objects.requireNonNull(formatter, "formatter is null"));
        }
    }

    /**
     * A converter for an entity attribute of {@link YearMonth}.
     *
     * @implSpec A value is stored as {@link YearMonth#toString()}, e.g. {@code "2026-09"}.
     */
    @Converter(autoApply = false)
    public static class OfYearMonth extends __TemporalAccessorStringAttributeConverter<YearMonth> {

        /**
         * Creates a new instance.
         */
        public OfYearMonth() {
            super(YearMonth.class);
        }

        /**
         * Creates a new instance formatting the column with the specified formatter.
         *
         * @param formatter the formatter which writes and reads the column.
         * @apiNote A persistence provider never calls this constructor &mdash; it instantiates a converter
         *         through the no-argument one, and neither {@link jakarta.persistence.Convert @Convert} nor
         *         {@code persistence.xml} can pass a formatter. Use this to hold the converter directly, composed into
         *         another or delegated to from one which <em>is</em> registered; to register a formatted converter,
         *         subclass and carry {@link Converter @Converter} on the subclass.
         */
        public OfYearMonth(final DateTimeFormatter formatter) {
            super(YearMonth.class, Objects.requireNonNull(formatter, "formatter is null"));
        }
    }

    /**
     * A converter for an entity attribute of {@link MonthDay}.
     *
     * @implSpec A value is stored as {@link MonthDay#toString()}, whose leading {@code "--"} is part of the
     *         ISO-8601 form &mdash; {@code "--09-19"}.
     */
    @Converter(autoApply = false)
    public static class OfMonthDay extends __TemporalAccessorStringAttributeConverter<MonthDay> {

        /**
         * Creates a new instance.
         */
        public OfMonthDay() {
            super(MonthDay.class);
        }

        /**
         * Creates a new instance formatting the column with the specified formatter.
         *
         * @param formatter the formatter which writes and reads the column.
         * @apiNote A persistence provider never calls this constructor &mdash; it instantiates a converter
         *         through the no-argument one, and neither {@link jakarta.persistence.Convert @Convert} nor
         *         {@code persistence.xml} can pass a formatter. Use this to hold the converter directly, composed into
         *         another or delegated to from one which <em>is</em> registered; to register a formatted converter,
         *         subclass and carry {@link Converter @Converter} on the subclass.
         */
        public OfMonthDay(final DateTimeFormatter formatter) {
            super(MonthDay.class, Objects.requireNonNull(formatter, "formatter is null"));
        }
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance, which is not allowed.
     */
    private __TemporalAccessorStringAttributeConverters() {
        throw new AssertionError("instantiation is not allowed");
    }
}
