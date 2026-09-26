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
import java.time.Instant;

/**
 * An abstract mapped superclass for an interval between two instants.
 * <p>
 * The two columns come from {@link ___MappedTemporalInterval}, and the two points and their measuring from
 * {@link ___TemporalInterval}. What this class settles is the point type, and with it the amount an interval of
 * instants is measured in.
 * <p>
 * It is not a {@link ___DiscreteInterval}, and cannot be: an instant has no predecessor, so there is no last instant an
 * interval contains and no step to ask how many of. That is a fact about the axis rather than an omission here.
 *
 * <h2>This is the axis the others approximate</h2>
 * An {@link Instant} is a point on the timeline and carries nothing else — no zone, no offset, no calendar. So
 * {@link Instant#compareTo(Instant)} compares epoch-second and then nanosecond, which is chronological order and
 * nothing standing in for it. The invariant on {@link ___MappedTemporalInterval} therefore means here exactly what it
 * reads as, which is not true of every point type that class admits.
 * <p>
 * That is the difference between this class and an interval of {@link java.time.OffsetDateTime} or
 * {@link java.time.ZonedDateTime}, which name the same moments and do not order them the same way. Where a schema
 * records when something was in effect rather than what a clock on a wall read, this is the point type, and an offset
 * kept alongside is a display concern rather than part of the interval.
 *
 * <h2>{@link Instant} is a basic type, since Jakarta Persistence 3.2</h2>
 * The two inherited columns need no converter and no {@link jakarta.persistence.Convert @Convert}: the provider maps
 * {@code Instant} itself, to a timestamp column. Under 3.1 and earlier it was not a basic type and an interval of
 * instants had to be converted into one — which is a reason to be on this platform rather than a reason for anything in
 * this class.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ___TemporalInterval
 */
@Access(AccessType.FIELD)
@MappedSuperclass
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __MappedInstantInterval extends ___MappedTemporalInterval<Instant> {

    // --------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected __MappedInstantInterval() {
        super();
    }

    // -------------------------------------------------------------------------------------------------------- derived

    /**
     * {@inheritDoc}
     *
     * @return the duration from the {@link #getIntervalStart() start} of this interval to its
     *         {@link #getIntervalEnd() end}; {@code null} when either point of this interval is absent.
     * @implNote The return type is narrowed to {@link Duration}, the amount instants measure in, so that a
     *         caller holding this type needs no cast.
     * @apiNote Unlike the {@link java.time.Period} an interval of dates measures in, this is exact: a duration
     *         is a count of nanoseconds, so it neither depends on where the interval sits nor decomposes into
     *         components whose lengths vary.
     */
    @Override
    @Transient
    public @Nullable Duration getTemporalAmount() {
        final Instant startInclusive = getIntervalStart();
        final Instant endExclusive = getIntervalEnd();
        if (startInclusive == null || endExclusive == null) {
            return null;
        }
        return Duration.between(startInclusive, endExclusive);
    }
}
