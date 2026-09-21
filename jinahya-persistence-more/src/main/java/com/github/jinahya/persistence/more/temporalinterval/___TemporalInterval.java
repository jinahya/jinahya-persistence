package com.github.jinahya.persistence.more.temporalinterval;

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
 * An interface for an interval on a temporal axis, running from an inclusive start to an exclusive end.
 * <p>
 * An instance implementing this interface exposes the two points which limit it, and nothing else. Either may be
 * absent: an interval which has begun and has no planned end, or one which has always been in effect, is as ordinary
 * in a schema as one with both points known.
 *
 * <h2>Two requirements: an axis, and an order</h2>
 * An interval has to be able to say two things. That its start is not after its end, which needs its two points
 * ordered. And how long it is, which needs them measurable against each other. So the bound is the conjunction:
 * {@link Temporal} for the measuring, {@link Comparable} for the order.
 * <p>
 * The length is {@link Temporal#until(Temporal, TemporalUnit) until(endExclusive, unit)}, which only {@code Temporal}
 * declares — a {@link java.time.temporal.TemporalAccessor} can be read, a {@code Temporal} can be measured and
 * shifted. Taking the narrower of the two is what makes this interface mean anything.
 * <p>
 * It also filters, which {@code Comparable} alone would not: {@link java.time.MonthDay}, {@link java.time.Month},
 * {@link java.time.DayOfWeek} and {@link java.time.ZoneOffset} are all {@code TemporalAccessor} <em>and</em>
 * {@code Comparable}, and none of them belongs at the end of an interval — the first three wrap rather than advance,
 * so there is no axis for them to bound.
 * <p>
 * The order half is written {@code Comparable<? super T>} rather than {@code Comparable<T>} because the most likely
 * point types are not comparable to themselves: {@link java.time.LocalDate} implements
 * {@code Comparable<java.time.chrono.ChronoLocalDate>}, and {@link java.time.LocalDateTime} and
 * {@link java.time.ZonedDateTime} likewise compare against their {@code Chrono} interfaces. The tighter bound would
 * reject all three.
 * <p>
 * Nothing enforces the order itself — not here, and not on {@link ___MappedTemporalInterval} either. An interval whose
 * start is after its end is data a caller wrote, and what a schema does about it is the schema's business; the
 * mapped superclass says what that costs and where a {@code CHECK} constraint goes.
 *
 * <h2>What is derived here, and what is not</h2>
 * Four questions are answered from the two points alone and are defaulted below: whether both are present, whether
 * they coincide, whether a given point falls between them, and how long the interval is when counted in a unit.
 * Nothing else is. The amount natural to the point type is declared and left abstract, because only the class which
 * knows the point type knows which amount that is; a step back from the exclusive end belongs to the classes whose
 * axis is discrete, because only those have a predecessor to step to.
 * <p>
 * {@link #contains(Temporal)} is for a point in hand, and is not how rows are found. An interval is stored so that
 * {@code interval_start <= :t AND (interval_end IS NULL OR interval_end > :t)} can run against an index over the two
 * columns; an entity already loaded was found by that query, and asking it the same thing afterwards answers nothing
 * new. What the method is for is the point which has not been to the database — a value being validated, a candidate
 * being checked against an interval already in hand.
 *
 * <h2>An amount is particular, a unit is not</h2>
 * {@link #lengthIn(TemporalUnit)} is defaulted because {@code until} serves every point type. The amount natural to a
 * point type is not: a {@link java.time.Period} where the points are dates, a {@link java.time.Duration} where they
 * are instants. So {@link #getTemporalAmount()} is declared and left to the class which knows which it has, to answer
 * with the return type <em>narrowed</em> to it — a caller holding that class then needs no cast, and one holding this
 * interface still reads the result through {@link TemporalAmount#get(TemporalUnit) get(unit)} and
 * {@link TemporalAmount#getUnits() getUnits()}.
 *
 * <h2>Two of the derivations read the order, which is not always the timeline</h2>
 * {@link #isBounded()} asks only whether the points are present, so it is right for every {@code T}.
 * {@link #isEmpty()} and {@link #contains(Temporal)} compare, and a comparison is only as good as the order the
 * point type gives — which for {@link java.time.OffsetDateTime} and {@link java.time.ZonedDateTime} falls through to
 * the local value once two instants agree, so two spellings of one moment compare as distinct.
 * <p>
 * They are defaulted here regardless, because they are right for every point type this package ships but one, and the
 * one overrides them: see {@link __MappedOffsetDateTimeInterval}, which redefines both against
 * {@link java.time.OffsetDateTime#toInstant() instants}, as it already redefines the invariant. A point type added
 * later whose order is not the timeline has to do the same, or not be added.
 * <p>
 * Measuring is unaffected either way, since
 * {@link java.time.Duration#between(java.time.temporal.Temporal, java.time.temporal.Temporal) Duration.between} works
 * on instants. It is comparing such points that is not the timeline.
 *
 * <h2>The convention is fixed: {@code [start, end)}</h2>
 * The start belongs to the interval and the end does not. It is not a per-instance choice, and there is deliberately
 * nothing here to make it one.
 * <p>
 * On a continuous axis a closed upper bound does not exist. An end written as {@code 23:59:59} is a closed bound
 * standing in for an open one, and the substitution is never exact — only close enough at the precision in play. So it
 * rots with the schema: correct against a second-precision column, short by milliseconds against a millisecond one,
 * short by microseconds against a database which keeps microseconds, short by nanoseconds against {@code java.time}.
 * Written half-open, the same interval ends at the next point exactly, and two adjacent intervals meet with neither gap
 * nor overlap.
 * <p>
 * Everything nearby has settled there: SQL:2011 defines its {@code PERIOD} as closed-open, {@code java.time} names its
 * own parameters {@code startInclusive} and {@code endExclusive} throughout, and ISO 8601-1:2019, clause 4.4, gives the
 * {@code <start>/<end>} form.
 *
 * <h2>Points, not an amount</h2>
 * Of the three self-contained forms ISO 8601-1:2019 gives an interval — start and end, start and duration, duration and
 * end — only the first can express an absent bound at all, and only it derives the other two without ambiguity:
 * calendar arithmetic does not run backwards, so a stored amount and a stored point disagree about the third value
 * across a month boundary. The two points are what is kept, which is why the amount is derived here rather than
 * declared as state.
 *
 * <h2>An absent bound is not an exception to the convention</h2>
 * Where there is no point, there is nothing to include or to exclude. The convention is in fact more uniform than it
 * looks: under {@code [start, end)} every bound is the same kind of cut — <em>at or after this point</em> — so an
 * interval is a pair drawn from one family of cuts, and absence is the improper member of that same family. Which is
 * why half-open tiles an axis.
 *
 * <h2>This is a view, not a mapping</h2>
 * As with {@link com.github.jinahya.persistence.more.__SelfReferencing}, the two accessors describe a view and are not
 * meant to be mapped, on their own, to persistent attributes. An implementing entity decides how the two points are
 * actually stored — two columns, either nullable.
 * <p>
 * Both are abstract, which is what keeps that true: the implementation declares them and carries its own annotations,
 * so an annotation here would reach nothing and would suggest otherwise.
 *
 * @param <T> the type of the two points limiting this interval
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ___DiscreteInterval
 * @see ___MappedTemporalInterval
 * @see <a href="https://www.iso.org/standard/70907.html">ISO 8601-1:2019</a>
 */
@SuppressWarnings({
        "java:S114" // Interface names should comply with a naming convention
})
public interface ___TemporalInterval<T extends Temporal & Comparable<? super T>> {

    /**
     * Returns the point at which this interval starts, which belongs to it.
     *
     * @return the inclusive start of this interval; {@code null} when this interval has no lower bound.
     */
    @Nullable
    T getIntervalStart();

    /**
     * Returns the point at which this interval ends, which does not belong to it.
     *
     * @return the exclusive end of this interval; {@code null} when this interval has no upper bound.
     * @see #getIntervalStart()
     */
    @Nullable
    T getIntervalEnd();

    /**
     * Returns whether both points limiting this interval are present.
     *
     * @return {@code true} when neither {@link #getIntervalStart() start} nor {@link #getIntervalEnd() end} is
     *         {@code null}; {@code false} otherwise.
     * @implSpec The default implementation reads both points and answers whether neither is {@code null}. It compares
     *         nothing, so no point type can make it wrong.
     */
    @Transient
    default boolean isBounded() {
        return getIntervalStart() != null && getIntervalEnd() != null;
    }

    /**
     * Returns whether the two points limiting this interval coincide, leaving it containing no point at all.
     *
     * @return {@code true} when this interval is {@link #isBounded() bounded} and its two points compare equal;
     *         {@code false} otherwise.
     * @implSpec The default implementation compares the two points rather than asking whether they are
     *         {@link Object#equals(Object) equal}, so that emptiness is decided by the same order the invariant is.
     * @apiNote An empty interval is a shape an interval may legitimately have rather than a violation: its start is
     *         not after its end, so it satisfies the invariant, and it {@link #contains(Temporal) contains} no
     *         point.
     */
    @Transient
    default boolean isEmpty() {
        final T startInclusive = getIntervalStart();
        final T endExclusive = getIntervalEnd();
        return startInclusive != null && endExclusive != null && startInclusive.compareTo(endExclusive) == 0;
    }

    /**
     * Returns whether the specified point falls within this interval.
     *
     * @param point the point to test.
     * @return {@code true} when the {@code point} is not before the {@link #getIntervalStart() start} of this interval
     *         and is before its {@link #getIntervalEnd() end}; {@code false} otherwise.
     * @throws NullPointerException when the {@code point} is {@code null}.
     * @implSpec The default implementation compares the {@code point} against each present bound, and treats an absent
     *         bound as holding — so an interval with neither bound contains every point, and an
     *         {@link #isEmpty() empty} one contains none.
     * @apiNote This takes an argument, so it is not shaped like a JavaBeans getter and cannot be mistaken for a
     *         persistent property. The two methods above are, and carry {@link Transient @Transient} for that reason.
     */
    default boolean contains(final T point) {
        Objects.requireNonNull(point, "point is null");
        final T startInclusive = getIntervalStart();
        final T endExclusive = getIntervalEnd();
        return (startInclusive == null || startInclusive.compareTo(point) <= 0)
               && (endExclusive == null || point.compareTo(endExclusive) < 0);
    }

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
