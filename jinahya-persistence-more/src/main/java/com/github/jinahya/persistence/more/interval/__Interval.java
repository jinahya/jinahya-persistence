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

import java.util.Objects;

/**
 * An interface for an interval, running from an inclusive start to an exclusive end.
 * <p>
 * An instance implementing this interface exposes the two points which limit it, and nothing else. Either may be
 * absent: an interval which has begun and has no planned end, or one which has always been in effect, is as ordinary
 * in a schema as one with both points known.
 *
 * <h2>The requirement is order, and only order</h2>
 * The one thing an interval has to be able to say is that its start is not after its end. That needs its two points
 * ordered and nothing else, so {@link Comparable} is the whole bound.
 * <p>
 * It is written {@code Comparable<? super T>} rather than {@code Comparable<T>} because the most likely point types
 * are not comparable to themselves: {@link java.time.LocalDate} implements
 * {@code Comparable<java.time.chrono.ChronoLocalDate>}, and {@link java.time.LocalDateTime} and
 * {@link java.time.ZonedDateTime} likewise compare against their {@code Chrono} interfaces. The tighter bound would
 * reject all three.
 * <p>
 * The requirement is stated where it can be enforced, which is not here: {@link __MappedInterval} carries it as a
 * constraint on the mapped attributes, checked when an instance is complete.
 *
 * <h2>What is derived here, and what is not</h2>
 * Three questions are answered from the two points alone and are defaulted below: whether both are present, whether
 * they coincide, and whether a given point falls between them. Nothing else is. The length is
 * {@link __TemporalInterval}'s, because measuring needs arithmetic; a step back from the exclusive end belongs to the
 * classes whose axis is discrete, because only those have a predecessor to step to.
 * <p>
 * {@link #contains(Comparable)} is for a point in hand, and is not how rows are found. An interval is stored so that
 * {@code interval_start <= :t AND (interval_end IS NULL OR interval_end > :t)} can run against an index over the two
 * columns; an entity already loaded was found by that query, and asking it the same thing afterwards answers nothing
 * new. What the method is for is the point which has not been to the database — a value being validated, a candidate
 * being checked against an interval already in hand.
 *
 * <h2>Two of the three read the order, which is not always the timeline</h2>
 * {@link #isBounded()} asks only whether the points are present, so it is right for every {@code T}.
 * {@link #isEmpty()} and {@link #contains(Comparable)} compare, and a comparison is only as good as the order the
 * point type gives — which for {@link java.time.OffsetDateTime} and {@link java.time.ZonedDateTime} falls through to
 * the local value once two instants agree, so two spellings of one moment compare as distinct.
 * <p>
 * They are defaulted here regardless, because they are right for every point type this package ships but one, and the
 * one overrides them: see {@link __MappedOffsetDateTimeInterval}, which redefines both against
 * {@link java.time.OffsetDateTime#toInstant() instants}, as it already redefines the invariant. A point type added
 * later whose order is not the timeline has to do the same, or not be added.
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
 * across a month boundary. The two points are what is kept.
 *
 * <h2>An absent bound is not an exception to the convention</h2>
 * Where there is no point, there is nothing to include or to exclude. The convention is in fact more uniform than it
 * looks: under {@code [start, end)} every bound is the same kind of cut — <em>at or after this point</em> — so an
 * interval is a pair drawn from one family of cuts, and absence is the improper member of that same family. Which is
 * why half-open tiles an axis.
 *
 * <h2>This is a view, not a mapping</h2>
 * As with {@link com.github.jinahya.persistence.more.__SelfReferencing}, these two methods describe a view and are not
 * meant to be mapped, on their own, to persistent attributes. An implementing entity decides how the two points are
 * actually stored — two columns, either nullable.
 * <p>
 * Both are abstract, which is what keeps that true: the implementation declares them and carries its own annotations,
 * so an annotation here would reach nothing and would suggest otherwise.
 *
 * @param <T> the type of the two points limiting this interval
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://www.iso.org/standard/70907.html">ISO 8601-1:2019</a>
 */
@SuppressWarnings({
        "java:S114" // Interface names should comply with a naming convention
})
public interface __Interval<T extends Comparable<? super T>> {

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
     *         not after its end, so it satisfies the invariant, and it {@link #contains(Comparable) contains} no
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
}
