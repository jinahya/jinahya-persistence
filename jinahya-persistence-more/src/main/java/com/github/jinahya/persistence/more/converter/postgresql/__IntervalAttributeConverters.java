package com.github.jinahya.persistence.more.converter.postgresql;

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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Period;

/**
 * Concrete {@link __IntervalAttributeConverter}s, for the two
 * {@link java.time.temporal.TemporalAmount TemporalAmount} implementations {@code java.time} declares.
 * <p>
 * An {@code interval} can hold months, days and seconds at once; neither type can. {@link OfDuration} takes the days
 * and the seconds, and refuses a value holding years or months, which have no fixed length in seconds.
 * {@link OfPeriod} takes the years, the months and the days, and refuses a value holding hours, minutes or seconds,
 * which a {@link Period} has no field for. A column which may hold either kind needs a type of its own.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __IntervalAttributeConverter
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class __IntervalAttributeConverters {

    private static final BigDecimal SECONDS_PER_DAY = BigDecimal.valueOf(86_400L);

    private static final int NANOS_PER_SECOND_EXPONENT = 9;

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * A converter for an entity attribute of {@link Duration}.
     *
     * @implSpec A value is written as {@link Duration#toString()}, {@code PT26H5M0.5S}, which the server reads as
     *         an {@code interval} of that many hours, minutes and seconds; a value is read back from the days and the
     *         seconds of the column, a day counting as {@code 86,400} seconds, which is what the server itself does
     *         when it compares an interval of days with one of hours.
     * @apiNote The server stores days apart from seconds, so an interval of {@code 1 day} and one of
     *         {@code 24 hours} are different values to it, and only the first is affected by a daylight-saving change
     *         when added to a timestamp. Both read back as {@link Duration#ofHours(long) Duration.ofHours(24)}, and
     *         what is written is always the second: a {@code Duration} has no days to keep.
     */
    @Converter(autoApply = false)
    public static class OfDuration extends __IntervalAttributeConverter<Duration>
            implements AttributeConverter<Duration, Object> {

        /**
         * Creates a new instance.
         *
         * @implNote {@code public}, not {@code protected}: a {@link Converter @Converter} class is instantiated
         *         by the persistence provider, and is documented as needing a public no-argument constructor. It also
         *         lets a caller use this converter directly, without subclassing a leaf class.
         */
        public OfDuration() {
            super(Duration.class);
        }

        /**
         * {@inheritDoc}
         *
         * @param fields {@inheritDoc}
         * @return a {@link Duration} of the days and the seconds of the {@code fields}.
         * @throws IllegalArgumentException when the {@code fields} hold years or months.
         * @implSpec A fractional second is rounded to the nearest nanosecond, half away from zero.
         */
        @Override
        protected Duration fromFields(final Fields fields) {
            if (fields.years() != 0L || fields.months() != 0L) {
                throw new IllegalArgumentException("years and months have no fixed length in seconds: " + fields);
            }
            final var total = fields.days().multiply(SECONDS_PER_DAY).add(fields.seconds());
            final var whole = total.setScale(0, RoundingMode.DOWN);
            final var nanos = total.subtract(whole)
                    .movePointRight(NANOS_PER_SECOND_EXPONENT)
                    .setScale(0, RoundingMode.HALF_UP);
            return Duration.ofSeconds(whole.longValueExact(), nanos.longValueExact());
        }
    }

    /**
     * A converter for an entity attribute of {@link Period}.
     *
     * @implSpec A value is written as {@link Period#toString()}, {@code P1Y2M3D}, which the server reads as an
     *         {@code interval} of that many years, months and days; a value is read back from the years, the months
     *         and the days of the column, each as its own field.
     * @apiNote The server keeps months and prints them as years and months, twelve to a year, so a
     *         {@code Period} of {@code 14} months reads back as one year and two months. The days are kept as they
     *         are, never folded into months: {@code P45D} reads back as {@code 45} days.
     */
    @Converter(autoApply = false)
    public static class OfPeriod extends __IntervalAttributeConverter<Period>
            implements AttributeConverter<Period, Object> {

        /**
         * Creates a new instance.
         */
        public OfPeriod() {
            super(Period.class);
        }

        /**
         * {@inheritDoc}
         *
         * @param fields {@inheritDoc}
         * @return a {@link Period} of the years, the months and the days of the {@code fields}.
         * @throws IllegalArgumentException when the {@code fields} hold seconds, a fractional count of days, or a
         *                                  count of anything beyond an {@code int}.
         */
        @Override
        protected Period fromFields(final Fields fields) {
            if (fields.seconds().signum() != 0) {
                throw new IllegalArgumentException("a period has no field for seconds: " + fields);
            }
            final int days;
            try {
                days = fields.days().intValueExact();
            } catch (final ArithmeticException ae) {
                throw new IllegalArgumentException("a period has no field for a fraction of a day: " + fields, ae);
            }
            try {
                return Period.of(Math.toIntExact(fields.years()), Math.toIntExact(fields.months()), days);
            } catch (final ArithmeticException ae) {
                throw new IllegalArgumentException("a field is out of a period's range: " + fields, ae);
            }
        }
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance, which is not allowed.
     */
    private __IntervalAttributeConverters() {
        throw new AssertionError("instantiation is not allowed");
    }
}
