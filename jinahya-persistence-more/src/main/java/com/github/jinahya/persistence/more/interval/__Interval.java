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
 * An instance implementing this interface exposes the two points which limit it. Either may be absent: an interval
 * which has begun and has no planned end, or one which has always been in effect, is as ordinary in a schema as one
 * with both points known.
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
 * What the bound does <em>not</em> promise is that the natural order is the one a caller has in mind. Every point type
 * in {@code java.time} is {@link Comparable}, and for the offset-carrying ones the comparison falls through to the
 * local value once the instants agree — so an interval of those is ordered as the type orders itself, which is not the
 * timeline. An implementation whose points are of such a type has to say so, or not exist.
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
 * <h2>An absent bound is not an exception to it</h2>
 * Where there is no point, there is nothing to include or to exclude. The convention is in fact more uniform than it
 * looks: under {@code [start, end)} every bound is the same kind of cut — <em>at or after this point</em> — so an
 * interval is a pair drawn from one family of cuts, and absence is the improper member of that same family. Which is
 * why half-open tiles an axis, and why the methods below treat an absent bound as holding on its side rather than as a
 * special case.
 *
 * <h2>This is a view, not a mapping</h2>
 * As with {@link com.github.jinahya.persistence.more.__SelfReferencing}, these methods describe a view and are not
 * meant to be mapped, on their own, to persistent attributes. An implementing entity decides how the two points are
 * actually stored — two columns, either nullable.
 * <p>
 * Where an annotation here does and does not reach is worth being exact about. {@link #getStart()} and
 * {@link #getEnd()} are abstract: the implementation declares them and carries its own annotations, so annotating them
 * here would reach nothing and would suggest otherwise. The {@code boolean} methods below are different — they are
 * shaped like JavaBeans getters <em>and</em> are inherited as declared, which is the one way a member of this interface
 * could be taken for a persistent property by an implementation mapping by property access. They are annotated
 * {@link Transient @Transient} for that reason.
 *
 * @param <T> the type of the two points limiting this interval
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
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
    T getStart();

    /**
     * Returns the point at which this interval ends, which does not belong to it.
     *
     * @return the exclusive end of this interval; {@code null} when this interval has no upper bound.
     * @see #getStart()
     */
    @Nullable
    T getEnd();

    /**
     * Returns whether both points limiting this interval are present.
     *
     * @return {@code true} when neither {@link #getStart() start} nor {@link #getEnd() end} is {@code null};
     *         {@code false} otherwise.
     * @implSpec The default implementation reads both points and answers whether neither is {@code null}.
     */
    @Transient
    default boolean isBounded() {
        return getStart() != null && getEnd() != null;
    }

    /**
     * Returns whether this interval meets the one requirement an interval has, that its start is not after its end.
     *
     * @return {@code true} when either point of this interval is absent, or when its {@link #getStart() start} is not
     *         after its {@link #getEnd() end}; {@code false} otherwise.
     * @implSpec The default implementation compares the two points, and answers {@code true} where either is absent —
     *         an absent bound cannot be out of order, having no point to be out of order with.
     * @apiNote An interval whose two points compare equal meets this. It is {@link #isEmpty() empty}, which is a shape
     *         an interval may legitimately have rather than a violation.
     */
    @Transient
    default boolean isOrdered() {
        final T start = getStart();
        final T end = getEnd();
        return start == null || end == null || start.compareTo(end) <= 0;
    }

    /**
     * Returns whether this interval contains no point at all.
     *
     * @return {@code true} when this interval is {@link #isBounded() bounded} and its two points compare equal;
     *         {@code false} otherwise.
     * @implSpec The default implementation compares the two points rather than asking whether they are
     *         {@link Object#equals(Object) equal}, so that emptiness is decided by the same order everything else here
     *         is decided by.
     */
    @Transient
    default boolean isEmpty() {
        final T start = getStart();
        final T end = getEnd();
        return start != null && end != null && start.compareTo(end) == 0;
    }

    /**
     * Returns whether the specified point falls within this interval.
     *
     * @param point the point to test.
     * @return {@code true} when the {@code point} is not before the {@link #getStart() start} of this interval and is
     *         before its {@link #getEnd() end}; {@code false} otherwise.
     * @throws NullPointerException when the {@code point} is {@code null}.
     * @implSpec The default implementation compares the {@code point} against each present bound, and treats an absent
     *         bound as holding — so an interval with neither bound contains every point, and an
     *         {@link #isEmpty() empty} one contains none.
     */
    default boolean contains(final T point) {
        Objects.requireNonNull(point, "point is null");
        final T start = getStart();
        final T end = getEnd();
        return (start == null || start.compareTo(point) <= 0) && (end == null || point.compareTo(end) < 0);
    }
}
