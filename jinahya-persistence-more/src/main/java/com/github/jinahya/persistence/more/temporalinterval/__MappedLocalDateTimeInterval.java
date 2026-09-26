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
import java.time.LocalDateTime;

/**
 * An abstract mapped superclass for an interval between two local date-times, mapped to two {@code TIMESTAMP} columns.
 * <p>
 * The two columns come from {@link ___MappedTemporalInterval}, and the two points and their measuring from
 * {@link ___TemporalInterval}. {@link LocalDateTime#compareTo(java.time.chrono.ChronoLocalDateTime)} compares the date
 * and then the time, which on one implicit timeline is chronological order.
 *
 * <h2>This is the {@code TIMESTAMP} a schema usually means</h2>
 * A column declared {@code TIMESTAMP} — {@code DATETIME} in MySQL — stores a date and a time and no zone, which is
 * exactly what a {@link LocalDateTime} is. It is the right point type where the interval is stated in the reading of
 * one clock and the clock is understood: opening hours on a given date, a scheduled window in a known local zone.
 * <p>
 * Where the interval is about <em>when</em> something was true, irrespective of where, {@link __MappedInstantInterval}
 * is the type: an {@link java.time.Instant} names a moment, and no arrangement of a {@code LocalDateTime} does. The two
 * are not interchangeable, and the difference shows up at a daylight-saving transition, where a local time can be
 * skipped or can happen twice.
 *
 * <h2>The amount is a {@link Duration}, and it is a local one</h2>
 * {@link Duration#between} measures two local date-times by elapsed nanoseconds as though no transition ever happened —
 * which is what {@code local} means, not a defect: a gap or an overlap belongs to a zone, and this point type has none.
 * An interval spanning a spring-forward reads one hour longer than a clock in that zone would say it lasted. Where that
 * matters, the points are instants.
 * <p>
 * The other reading, a {@link java.time.Period} of whole days and months, is a question about the dates alone and is
 * reached through {@link LocalDateTime#toLocalDate()}. It is deliberately not offered here, because only one of the two
 * can be the answer to {@link #getTemporalAmount()} and the elapsed one is the one an interval means.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __MappedInstantInterval
 * @see __MappedLocalDateInterval
 */
@Access(AccessType.FIELD)
@MappedSuperclass
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __MappedLocalDateTimeInterval extends ___MappedTemporalInterval<LocalDateTime> {

    // --------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected __MappedLocalDateTimeInterval() {
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
     */
    @Override
    @Transient
    public @Nullable Duration getTemporalAmount() {
        final LocalDateTime startInclusive = getIntervalStart();
        final LocalDateTime endExclusive = getIntervalEnd();
        if (startInclusive == null || endExclusive == null) {
            return null;
        }
        return Duration.between(startInclusive, endExclusive);
    }
}
