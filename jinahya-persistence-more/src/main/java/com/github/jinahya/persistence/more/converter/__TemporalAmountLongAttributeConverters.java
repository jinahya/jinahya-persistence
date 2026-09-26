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

import java.time.Duration;

/**
 * Concrete {@link __TemporalAmountLongAttributeConverter}s.
 * <p>
 * There is one, and the reason there is only one is the whole content of this class:
 * {@link java.time.temporal.TemporalAmount} has two implementations in {@code java.time}, and only {@link Duration} can
 * be reduced to a number that orders. See {@link __TemporalAmountLongAttributeConverter} for why
 * {@link java.time.Period} cannot, and {@link __TemporalAmountStringAttributeConverters} for the pair which keeps both
 * exactly and orders neither.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __TemporalAmountLongAttributeConverter
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class __TemporalAmountLongAttributeConverters {

    // ------------------------------------------------------------------------------------------------------ OfDuration

    /**
     * A converter for an entity attribute of {@link Duration}, stored as a whole number of nanoseconds.
     * <p>
     * The column holds {@link Duration#toNanos()} and the round trip through {@link Duration#ofNanos(long)} is exact to
     * the nanosecond, negative durations included.
     *
     * @implSpec The encoding is increasing: a longer duration is a larger number, and a negative one a smaller.
     *         {@link Duration#compareTo(Duration)} orders by seconds and then by the nanosecond part, which is the same
     *         order, so a column of these compares and indexes exactly as the values do.
     * @apiNote <strong>The range is ±292 years, and passing it throws.</strong> {@link Duration#toNanos()}
     *         raises an {@link ArithmeticException} where the value does not fit a {@code long}, so a duration measured
     *         in centuries cannot use this converter. That is the price of one ordered column, and it is the right
     *         trade for every duration a schema is likely to hold — an SLA, a session length, a billing increment.
     *         Where the value can be that large and does not have to be compared,
     *         {@link __TemporalAmountStringAttributeConverters.OfDuration} stores any {@code Duration} at all.
     *         <p>
     *         The column has to be a {@code BIGINT}: one second is already {@code 1,000,000,000}.
     */
    @Converter(autoApply = false)
    public static class OfDuration extends __TemporalAmountLongAttributeConverter<Duration>
            implements AttributeConverter<Duration, Long> {

        /**
         * Creates a new instance.
         *
         * @implNote {@code public}, not {@code protected}: a {@link Converter @Converter} class is instantiated
         *         by the persistence provider, and is documented as needing a public no-argument constructor.
         */
        public OfDuration() {
            super(Duration.class, Duration::toNanos, Duration::ofNanos);
        }
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    private __TemporalAmountLongAttributeConverters() {
        throw new AssertionError("instantiation is not allowed");
    }
}
