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

import jakarta.persistence.Transient;
import org.jspecify.annotations.Nullable;

import java.time.DateTimeException;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.Objects;
import java.util.OptionalLong;

/**
 * An interval on a temporal axis, whose length is itself a value.
 * <p>
 * This is the one derivation {@link __Interval} does not treat as a convenience. The two points are kept
 * <em>instead</em> of an amount, for the reasons given there, so the amount has to be obtainable from them or the
 * choice would have cost something. Everything else an interval could be asked is left to whatever asks it.
 *
 * <h2>Why {@link Temporal}, and not {@link java.time.temporal.TemporalAccessor}</h2>
 * The length is {@link Temporal#until(Temporal, TemporalUnit) until(endExclusive, unit)}, which only {@code Temporal}
 * declares — a {@code TemporalAccessor} can be read, a {@code Temporal} can be measured and shifted. Taking the
 * narrower of the two is what makes this interface mean anything.
 * <p>
 * It also filters, which the {@link Comparable} bound of {@link __Interval} does not: {@link java.time.MonthDay},
 * {@link java.time.Month}, {@link java.time.DayOfWeek} and {@link java.time.ZoneOffset} are all
 * {@code TemporalAccessor} <em>and</em> {@code Comparable}, and none of them belongs at the end of an interval — the
 * first three wrap rather than advance, so there is no axis for them to bound.
 *
 * <h2>An amount is particular, a unit is not</h2>
 * {@link #lengthIn(TemporalUnit)} is defaulted here because {@code until} serves every point type. The amount natural
 * to a point type is not: a {@link java.time.Period} where the points are dates, a {@link java.time.Duration} where
 * they are instants. So {@link #getTemporalAmount()} is declared and left to the class which knows which it has, to
 * answer with the return type <em>narrowed</em> to it — a caller holding that class then needs no cast, and one
 * holding this interface still reads the result through {@link TemporalAmount#get(TemporalUnit) get(unit)} and
 * {@link TemporalAmount#getUnits() getUnits()}.
 *
 * @param <T> the type of the two points limiting this interval
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __Interval
 */
@SuppressWarnings({
        "java:S114" // Interface names should comply with a naming convention
})
public interface __TemporalInterval<T extends Temporal & Comparable<? super T>> extends __Interval<T> {

    /**
     * Returns the length of this interval, as the amount natural to its point type.
     *
     * @return the amount from the {@link #getIntervalStart() start} of this interval to its
     *         {@link #getIntervalEnd() end}; {@code null} when either point is absent.
     * @implSpec An implementation should narrow the return type to the amount its points actually measure in.
     * @see #lengthIn(TemporalUnit)
     */
    @Transient
    @Nullable
    TemporalAmount getTemporalAmount();

    /**
     * Returns the length of this interval, counted in the specified unit.
     *
     * @param unit the unit to count in.
     * @return an {@link OptionalLong} carrying the number of complete {@code unit}s from the
     *         {@link #getIntervalStart() start} of this interval to its {@link #getIntervalEnd() end};
     *         {@link OptionalLong#empty() empty} when either point is absent.
     * @throws NullPointerException             if {@code unit} is {@code null}.
     * @throws DateTimeException                when the length cannot be measured.
     * @throws UnsupportedTemporalTypeException when the {@code unit} is not supported by the point type.
     * @throws ArithmeticException              when the result overflows a {@code long}.
     * @implSpec The default implementation reads both points and, where neither is absent, measures from one to the
     *         other with {@link Temporal#until(Temporal, TemporalUnit)}.
     * @apiNote This is not {@link #getTemporalAmount()} said differently. An amount is a composite — a {@code Period}
     *         of {@code P1M2D} answers {@code 2} for {@link java.time.temporal.ChronoUnit#DAYS DAYS}, because two days
     *         is its day <em>component</em> — where this method answers the whole length counted in days.
    */
    default OptionalLong lengthIn(final TemporalUnit unit) {
        Objects.requireNonNull(unit, "unit is null");
        final T startInclusive = getIntervalStart();
        final T endExclusive = getIntervalEnd();
        if (startInclusive == null || endExclusive == null) {
            return OptionalLong.empty();
        }
        return OptionalLong.of(startInclusive.until(endExclusive, unit));
    }
}
