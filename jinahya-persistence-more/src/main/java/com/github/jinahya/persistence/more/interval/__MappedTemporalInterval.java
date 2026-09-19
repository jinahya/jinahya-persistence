package com.github.jinahya.persistence.more.interval;

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

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import org.jspecify.annotations.Nullable;

import java.time.DateTimeException;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.OptionalLong;

/**
 * An abstract mapped superclass for an interval on a temporal axis.
 * <p>
 * Everything order alone decides comes from {@link __Interval}, and the two columns from {@link __MappedInterval}. What
 * this class adds is the one thing which separates an interval from a plain range: the distance between its two points
 * is itself a value.
 *
 * <h2>Why {@link Temporal}, and not {@link java.time.temporal.TemporalAccessor}</h2>
 * That distance is {@link Temporal#until(Temporal, TemporalUnit) until(endExclusive, unit)}, which only
 * {@code Temporal} declares — a {@code TemporalAccessor} can be read, a {@code Temporal} can be measured and shifted.
 * Taking the narrower of the two is what makes this layer mean anything.
 * <p>
 * It also filters, which the {@link Comparable} bound below it does not: {@link java.time.MonthDay},
 * {@link java.time.Month}, {@link java.time.DayOfWeek} and {@link java.time.ZoneOffset} are all
 * {@code TemporalAccessor} <em>and</em> {@code Comparable}, and none of them belongs at the end of an interval — the
 * first three wrap rather than advance, so there is no axis for them to bound.
 *
 * <h2>An amount is particular, a unit is not</h2>
 * {@link #lengthIn(TemporalUnit)} is defaulted here because {@code until} serves every point type. The amount natural
 * to a point type is not: a {@link java.time.Period} where the points are dates, a {@link java.time.Duration} where
 * they are instants, both and differently where they are local date-times. So {@link #getTemporalAmount()} is declared
 * and left to the class which knows which it has, to answer with the return type <em>narrowed</em> to it — a caller
 * holding that class then needs no cast, and one holding this class still reads the result through
 * {@link TemporalAmount#get(TemporalUnit) get(unit)} and {@link TemporalAmount#getUnits() getUnits()}.
 *
 * @param <T> the type of the two points limiting this interval
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __MappedInterval
 */
@MappedSuperclass
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __MappedTemporalInterval<T extends Temporal & Comparable<? super T>>
        extends __MappedInterval<T> {

    // --------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected __MappedTemporalInterval() {
        super();
    }

    // -------------------------------------------------------------------------------------------------------- derived

    /**
     * Returns the length of this interval, as the amount natural to its point type.
     *
     * @return the amount from the {@link #getStart() start} of this interval to its {@link #getEnd() end};
     *         {@code null} when this interval is not {@link #isBounded() bounded}.
     * @implSpec An implementation should narrow the return type to the amount its points actually measure in.
     * @see #lengthIn(TemporalUnit)
     */
    @Transient
    public abstract @Nullable TemporalAmount getTemporalAmount();

    /**
     * Returns the length of this interval, counted in the specified unit.
     *
     * @param unit the unit to count in.
     * @return an {@link OptionalLong} carrying the number of complete {@code unit}s from the {@link #getStart() start}
     *         of this interval to its {@link #getEnd() end}; {@link OptionalLong#empty() empty} when this interval is
     *         not {@link #isBounded() bounded}.
     * @throws DateTimeException                when the length cannot be measured.
     * @throws UnsupportedTemporalTypeException when the {@code unit} is not supported by the point type.
     * @throws ArithmeticException              when the result overflows a {@code long}.
     * @apiNote This is not {@link #getTemporalAmount()} said differently. An amount is a composite — a {@code Period}
     *         of {@code P1M2D} answers {@code 2} for {@link java.time.temporal.ChronoUnit#DAYS DAYS}, because two days
     *         is its day <em>component</em> — where this method answers the whole length counted in days.
     */
    public OptionalLong lengthIn(final TemporalUnit unit) {
        final T start = getStart();
        final T end = getEnd();
        if (start == null || end == null) {
            return OptionalLong.empty();
        }
        return OptionalLong.of(start.until(end, unit));
    }
}
