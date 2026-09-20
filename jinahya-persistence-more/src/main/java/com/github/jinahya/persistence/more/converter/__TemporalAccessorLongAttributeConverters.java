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

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Concrete {@link __TemporalAccessorLongAttributeConverter}s, one per {@code java.time} type which has a single
 * integral coordinate that is both increasing and lossless.
 * <p>
 * That is a short list, and deliberately so. {@link LocalTime} and {@link LocalDate} each are one number and nothing
 * else &mdash; a position on a bounded axis, a position on an unbounded one &mdash; so each has a coordinate the JDK
 * itself names, and the round trip through it is exact.
 *
 * <h2>What is absent, and why</h2>
 * {@link java.time.LocalDateTime} is a pair rather than a coordinate. Flattening it needs an epoch chosen from outside
 * the type, and the obvious flattening &mdash; {@code epochDay} scaled up plus {@code nanoOfDay} &mdash; overflows a
 * {@code long} outside roughly 1678 to 2262. A schema which wants it numeric should say which epoch and which scale it
 * means, in its own converter.
 * <p>
 * {@link java.time.Instant} has {@link java.time.Instant#toEpochMilli()}, which is increasing and is <em>not</em>
 * lossless: it drops everything below a millisecond. An {@code Instant} column also has nothing wrong with it on any
 * database, which is the other half of the reason there is no converter for it here.
 * <p>
 * {@link java.time.OffsetDateTime} and {@link java.time.ZonedDateTime} carry an offset or a zone, which no single
 * number holds alongside the moment.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __TemporalAccessorLongAttributeConverter
 * @see __TemporalAccessorStringAttributeConverters
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class __TemporalAccessorLongAttributeConverters {

    // ----------------------------------------------------------------------------------------------------------------

    /**
     * A converter for an entity attribute of {@link LocalTime}, stored as its nanosecond of the day.
     * <p>
     * The column holds {@link LocalTime#toNanoOfDay()} &mdash; {@code 0} at midnight, and
     * {@code 86,399,999,999,999} at {@link LocalTime#MAX}, which needs a {@code BIGINT} and not an {@code INTEGER}.
     * The round trip through {@link LocalTime#ofNanoOfDay(long)} is exact to the nanosecond.
     *
     * @implSpec The encoding is increasing: a later time of day is a larger number, so a column of these compares and
     *         indexes exactly as a {@code TIME} column would.
     * @apiNote This is the converter to reach for where a {@code TIME} column is the difficulty. Oracle has no
     *         {@code TIME} type at all, and a {@link LocalTime} lands there in a {@code TIMESTAMP} carrying a dummy
     *         date; elsewhere the column keeps whole seconds by default, since
     *         {@link jakarta.persistence.Column#secondPrecision() secondPrecision} defaults to storing no fractional
     *         seconds in a {@code TIME} column, and not every provider and dialect honour a different value. An
     *         exact 64-bit integer column exists on every database &mdash; a {@code BIGINT}, which Oracle spells
     *         {@code number(19,0)} &mdash; and it holds the full precision.
     */
    @Converter(autoApply = false)
    public static class OfLocalTime extends __TemporalAccessorLongAttributeConverter<LocalTime>
            implements AttributeConverter<LocalTime, Long> {

        /**
         * Creates a new instance.
         *
         * @implNote {@code public}, not {@code protected}: a {@link Converter @Converter} class is instantiated by the
         *         persistence provider, and is documented as needing a public no-argument constructor.
         */
        public OfLocalTime() {
            super(LocalTime.class, LocalTime::toNanoOfDay, LocalTime::ofNanoOfDay);
        }
    }

    // ----------------------------------------------------------------------------------------------------------------

    /**
     * A converter for an entity attribute of {@link LocalDate}, stored as its epoch day.
     * <p>
     * The column holds {@link LocalDate#toEpochDay()} &mdash; {@code 0} on 1970-01-01, negative before it &mdash; and
     * the round trip through {@link LocalDate#ofEpochDay(long)} is exact. The full range of {@link LocalDate} needs a
     * {@code BIGINT}, though every date up to the year 9999 fits an {@code INTEGER} with room to spare.
     *
     * @implSpec The encoding is increasing: a later date is a larger number.
     * @apiNote A {@code DATE} column has no portability problem to solve &mdash; every database has one, and means the
     *         same thing by it &mdash; so unlike {@link OfLocalTime} this is not a workaround. Use it where a schema
     *         already counts days, or where the arithmetic wanted is subtraction rather than a date function.
     */
    @Converter(autoApply = false)
    public static class OfLocalDate extends __TemporalAccessorLongAttributeConverter<LocalDate>
            implements AttributeConverter<LocalDate, Long> {

        /**
         * Creates a new instance.
         *
         * @implNote {@code public}, not {@code protected}: a {@link Converter @Converter} class is instantiated by the
         *         persistence provider, and is documented as needing a public no-argument constructor.
         */
        public OfLocalDate() {
            super(LocalDate.class, LocalDate::toEpochDay, LocalDate::ofEpochDay);
        }
    }

    // --------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    private __TemporalAccessorLongAttributeConverters() {
        throw new AssertionError("instantiation is not allowed");
    }
}
