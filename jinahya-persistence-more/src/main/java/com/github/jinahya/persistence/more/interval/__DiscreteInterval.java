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

import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;

/**
 * An interval on an axis whose points are a fixed step apart, and which therefore has a last point.
 * <p>
 * A date has a predecessor, and so do a month and a year. An instant does not, and neither does a time of day at any
 * precision a schema might choose. That difference is the whole of this interface: where it holds, the exclusive end
 * of an interval converts to the closed form people speak in without losing anything, and where it does not, any
 * attempt to convert is the {@code 23:59:59} substitution {@link __Interval} exists to refuse.
 *
 * <h2>What implementing this asserts</h2>
 * That {@link #getGranularity()} is the step: that two adjacent points of {@code T} are exactly one of that unit
 * apart, and that nothing of {@code T} falls between them. {@link java.time.LocalDate} with
 * {@link java.time.temporal.ChronoUnit#DAYS DAYS}, {@link java.time.YearMonth} with
 * {@link java.time.temporal.ChronoUnit#MONTHS MONTHS} and {@link java.time.Year} with
 * {@link java.time.temporal.ChronoUnit#YEARS YEARS} each hold to it.
 * <p>
 * {@link java.time.Instant}, {@link java.time.LocalTime}, {@link java.time.LocalDateTime} and
 * {@link java.time.OffsetDateTime} do not, at any unit: a nanosecond is the finest step {@code java.time} offers and
 * not the finest step there is, so the point before an end would be an artefact of the precision rather than a fact
 * about the axis. An interval of those is a {@link __TemporalInterval} and stops there.
 *
 * <h2>The unit is worth having on its own</h2>
 * It is not only the argument {@link #getIntervalEndInclusive()} needs. It is the unit the axis is counted in, so
 * {@link #lengthIn(TemporalUnit) lengthIn}({@link #getGranularity() getGranularity()}) is <em>how many steps long</em>
 * an interval is — the natural question on such an axis, and one a caller could otherwise only ask by knowing which
 * unit a given implementation steps in.
 *
 * @param <T> the type of the two points limiting this interval
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __TemporalInterval
 */
@SuppressWarnings({
        "java:S114" // Interface names should comply with a naming convention
})
public interface __DiscreteInterval<T extends Temporal & Comparable<? super T>> extends __TemporalInterval<T> {

    /**
     * Returns the unit by which the points of this interval step.
     *
     * @return the unit two adjacent points of this interval's point type are apart.
     * @implSpec An implementation returns a constant, and the same constant every time: it is a property of the point
     *         type rather than of an instance.
     */
    @Transient
    TemporalUnit getGranularity();

    /**
     * Returns the last point which this interval contains.
     *
     * @return the point one {@link #getGranularity() step} before the {@link #getIntervalEnd() end} of this interval;
     *         {@code null} when this interval has no upper bound, or is {@link #isEmpty() empty} and so contains no
     *         point at all.
     * @implSpec The default implementation steps back from the exclusive end by one
     *         {@link #getGranularity() granularity}, with {@link Temporal#minus(long, TemporalUnit)}.
     * @implNote The result is cast to {@code T}. The cast is sound by the contract of
     *         {@link Temporal#minus(long, TemporalUnit)}, which is specified to return an object of the same type as
     *         the one it was called on; the signature says {@link Temporal} because it cannot say more.
     * @apiNote This is the closed form people speak in — a thing <em>runs through</em> its last day — and it is exact
     *         only because this axis is discrete.
     */
    @SuppressWarnings({
            "unchecked" // (T) Temporal, per Temporal#minus(long, TemporalUnit)
    })
    @Transient
    default @Nullable T getIntervalEndInclusive() {
        final T endExclusive = getIntervalEnd();
        if (endExclusive == null || isEmpty()) {
            return null;
        }
        return (T) endExclusive.minus(1L, getGranularity());
    }
}
