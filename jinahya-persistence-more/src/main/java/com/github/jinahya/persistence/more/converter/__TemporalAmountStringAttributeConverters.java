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
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.time.Period;

/**
 * Concrete {@link __TemporalAmountStringAttributeConverter}s, for the two
 * {@link java.time.temporal.TemporalAmount TemporalAmount} implementations {@code java.time} declares.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __TemporalAmountStringAttributeConverter
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class __TemporalAmountStringAttributeConverters {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * A converter for an entity attribute of {@link Duration}.
     *
     * @implSpec A value is stored as {@link Duration#toString()} and read back with
     *         {@link Duration#parse(CharSequence)}; every {@code Duration} round-trips exactly, including a negative
     *         one, {@link Duration#ZERO} (stored as {@code "PT0S"}), and the nanosecond part.
     * @apiNote The form is normalized to hours, minutes and seconds on the way out: a {@code Duration} of two
     *         days is stored as {@code "PT48H"}, not {@code "P2D"}. That is {@code Duration}'s own spelling, and it is
     *         what keeps the value unambiguous in the column.
     * @implNote The read is overridden rather than left to the reflective default, which would find exactly
     *         this method: the call happens once per row per attribute, and there is no reason to reach it through
     *         {@link java.lang.reflect.Method#invoke(Object, Object...)} when the type is known here.
     */
    @Converter(autoApply = false)
    public static class OfDuration extends __TemporalAmountStringAttributeConverter<Duration>
            implements AttributeConverter<Duration, String> {

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

        @Override
        public @Nullable Duration convertToEntityAttribute(final @Nullable String dbData) {
            if (dbData == null) {
                return null;
            }
            return Duration.parse(dbData);
        }
    }

    /**
     * A converter for an entity attribute of {@link Period}.
     *
     * @implSpec A value is stored as {@link Period#toString()} and read back with
     *         {@link Period#parse(CharSequence)}; every {@code Period} round-trips exactly, including a negative one
     *         and {@link Period#ZERO} (stored as {@code "P0D"}).
     * @apiNote The units are kept as they were declared, not normalized: a {@code Period} of {@code 14} months
     *         is stored as {@code "P14M"} and reads back as {@code 14} months, not as one year and two months. Call
     *         {@link Period#normalized()} before persisting if a canonical form is wanted in the column.
     * @implNote The read is overridden for the same reason as in {@link OfDuration}.
     */
    @Converter(autoApply = false)
    public static class OfPeriod extends __TemporalAmountStringAttributeConverter<Period>
            implements AttributeConverter<Period, String> {

        /**
         * Creates a new instance.
         */
        public OfPeriod() {
            super(Period.class);
        }

        @Override
        public @Nullable Period convertToEntityAttribute(final @Nullable String dbData) {
            if (dbData == null) {
                return null;
            }
            return Period.parse(dbData);
        }
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    private __TemporalAmountStringAttributeConverters() {
        throw new AssertionError("instantiation is not allowed");
    }
}
