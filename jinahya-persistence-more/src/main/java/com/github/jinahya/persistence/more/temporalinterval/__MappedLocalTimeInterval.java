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

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import org.jspecify.annotations.Nullable;

import java.time.Duration;
import java.time.LocalTime;

/**
 * An abstract mapped superclass for an interval between two times of day, mapped to two {@code TIME} columns.
 * <p>
 * The two columns come from {@link ___MappedTemporalInterval}, and the two points and their measuring from
 * {@link ___TemporalInterval}. {@link LocalTime#compareTo(LocalTime)} compares hour, minute, second and nanosecond in
 * turn, which is chronological order within a day.
 *
 * <h2>The axis is a day, and it does not wrap</h2>
 * This is the one point type here whose axis is a closed loop in ordinary speech and a line in storage. A shift from
 * {@code 22:00} to {@code 02:00} is a real thing to want to record and is <em>not</em> an interval of this class: its
 * start is after its end, and the containment {@code interval_start <= :t AND interval_end > :t} a database would run
 * against the two columns matches nothing at all for such a row.
 * <p>
 * Such a span is two rows — {@code [22:00, null)} and {@code [null, 02:00)} — which is what a schema storing business
 * hours across midnight does regardless of the type system, and which the convention below makes exact rather than
 * approximate.
 *
 * <h2>An absent bound is the edge of the day</h2>
 * On an unbounded axis {@code null} means <em>no limit on that side</em>. Here the axis is itself bounded, by
 * {@link LocalTime#MIN 00:00} and by the end of the day, so an absent bound and the edge of the day select exactly the
 * same set of times. That is not a special case bolted on: it is what an absent bound already means in
 * {@link ___TemporalInterval}, read on an axis which happens to end.
 * <p>
 * It also supplies the answer to something otherwise unwritable. There is no {@code 24:00} in {@link LocalTime} —
 * {@link LocalTime#MAX} is {@code 23:59:59.999999999} — so a span running to the end of the day has no exclusive end to
 * name, and writing {@code MAX} as the end would be the closed-bound substitution {@link ___TemporalInterval} exists to
 * refuse, wrong by one nanosecond and wrong by more against a second-precision {@code TIME} column. An absent end says
 * it exactly.
 *
 * <h2>The column is a {@code TIME}, and that is the least portable column here</h2>
 * The two inherited columns map to a {@code TIME}, and it is worth knowing what that turns into before relying on it.
 * Three things are true of it and of nothing else in this package:
 * <ul>
 * <li>Oracle has no {@code TIME} type at all. A {@link LocalTime} lands there in a {@code TIMESTAMP} carrying a dummy
 *     date, which compares correctly, since both columns carry the same one, and reads as something other than a time
 *     to anyone looking at the schema.
 * <li>The column keeps whole seconds. That is the specification rather than a provider's choice:
 *     {@link jakarta.persistence.Column#secondPrecision() secondPrecision}, new in Jakarta Persistence 3.2, defaults
 *     to {@code -1}, documented as storing no fractional seconds in a {@code time} column. A bound carrying
 *     nanoseconds is truncated on the way in.
 * <li>Asking for fractional seconds is not portable either. Setting {@code secondPrecision} through an
 *     {@link jakarta.persistence.AttributeOverride @AttributeOverride} reaches some dialects and not others, so the
 *     same entity can keep whole seconds on one database and sub-second ticks on another.
 * </ul>
 * None of this touches the invariant: both bounds are the same column type, so they are truncated alike and compare
 * as they should. It touches what a bound is when it is read back.
 *
 * <h2>Where that matters, the column is a number</h2>
 * A time of day is one coordinate, and the JDK names it: {@link LocalTime#toNanoOfDay()}, from {@code 0} to
 * {@code 86,399,999,999,999}, increasing exactly as the time does and exact through
 * {@link LocalTime#ofNanoOfDay(long)}. Every database has an exact 64-bit integer to hold it &mdash; a {@code BIGINT},
 * which Oracle spells {@code number(19,0)} &mdash; so the column keeps the full precision everywhere, and still
 * compares and indexes as a {@code TIME} column would &mdash;
 * {@code interval_start <= :t AND interval_end > :t} is unchanged.
 * <p>
 * There is deliberately no subclass here for it. The two columns are declared in {@link ___MappedTemporalInterval} and an
 * entity reaches them by name, so this is the whole of it, written on the entity which wants it:
 * {@snippet lang = "java":
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @Entity
 * @Convert(attributeName = "intervalStart", converter =
 *         __TemporalAccessorLongAttributeConverters.OfLocalTime.class)
 * @Convert(attributeName = "intervalEnd", converter =
 *         __TemporalAccessorLongAttributeConverters.OfLocalTime.class) public class BusinessHours extends
 *         __MappedLocalTimeInterval { ... }} An {@link jakarta.persistence.AttributeOverride @AttributeOverride} on the
 *         same two attribute names goes alongside it where the column itself needs reshaping — a length, a precision, a
 *         name of the schema's own choosing. The conversion decides what is written; the override decides what it is
 *         written into.
 *         <p>
 *         What is lost is legibility: the database no longer knows the column is a time, no time function applies to
 *         it, and a report shows {@code 32400000000000} where it would have shown {@code 09:00:00}.
 *
 *         <h2>The amount is a {@link Duration}</h2>
 *         Times of day measure in elapsed time and nothing else — there are no months in a day — so
 *         {@link Duration#between} is the whole of it.
 * @see __MappedLocalDateTimeInterval
 * @see com.github.jinahya.persistence.more.converter.__TemporalAccessorLongAttributeConverters.OfLocalTime
 */
@Access(AccessType.FIELD)
@MappedSuperclass
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __MappedLocalTimeInterval extends ___MappedTemporalInterval<LocalTime> {

    // --------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected __MappedLocalTimeInterval() {
        super();
    }

    // -------------------------------------------------------------------------------------------------------- derived

    /**
     * {@inheritDoc}
     *
     * @return the duration from the {@link #getIntervalStart() start} of this interval to its
     *         {@link #getIntervalEnd() end}; {@code null} when either point of this interval is absent.
     * @implNote The return type is narrowed to {@link Duration}, so that a caller holding this type needs no
     *         cast.
     * @apiNote This is {@code null} where either bound is absent, which on this axis means the caller has
     *         written a span reaching an edge of the day. The length of such a span is a question about the day rather
     *         than about the interval, and {@link #lengthIn(java.time.temporal.TemporalUnit)} answers it no
     *         differently.
     */
    @Override
    @Transient
    public @Nullable Duration getTemporalAmount() {
        final LocalTime startInclusive = getIntervalStart();
        final LocalTime endExclusive = getIntervalEnd();
        if (startInclusive == null || endExclusive == null) {
            return null;
        }
        return Duration.between(startInclusive, endExclusive);
    }
}
