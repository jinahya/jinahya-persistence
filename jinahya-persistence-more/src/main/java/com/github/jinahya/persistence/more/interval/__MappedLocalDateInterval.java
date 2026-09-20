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

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import org.jspecify.annotations.Nullable;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.time.Period;

/**
 * An abstract mapped superclass for an interval between two dates, mapped to two {@code DATE} columns.
 * <p>
 * The two columns come from {@link __MappedInterval}, the measuring from {@link __TemporalInterval}, and the two
 * points from {@link __Interval}. What this class fixes is the point type, and with it the amount an interval of
 * dates is measured in.
 *
 * <h2>The point type is {@link LocalDate}, and only {@link LocalDate}</h2>
 * There is deliberately no version of this class generic over
 * {@link java.time.chrono.ChronoLocalDate}, admitting a {@link java.time.chrono.HijrahDate} or a
 * {@link java.time.chrono.JapaneseDate}. Such a class could not be mapped: the basic types Jakarta Persistence
 * defines include the {@code java.time} date and time types and no member of {@code java.time.chrono}, so a date in
 * another calendar system would fall through to the serializable-type rule and land in the column as bytes — and two
 * columns of bytes cannot answer {@code interval_start <= :t AND interval_end > :t}, which is what an interval is
 * stored for.
 * <p>
 * The JDK gives the same advice for ordinary reasons, in bold, in {@link java.time.chrono.ChronoLocalDate}'s own
 * javadoc: most applications should declare their signatures, fields and variables as {@code LocalDate} and not as
 * that interface. Where a non-ISO calendar system really is what a schema holds, the conversion belongs in an
 * {@link jakarta.persistence.AttributeConverter} applied to the two inherited attributes, the way
 * {@link __MappedLocalTimeInterval} sets out, rather than in a type parameter here.
 *
 * <h2>This is the {@code DATE} column of a schema</h2>
 * {@link LocalDate} is a basic type, so the two inherited columns need no converter, and a provider maps them to the
 * database's own {@code DATE} — the one column type every database has and agrees about.
 * <p>
 * The JDK agrees on the half-open convention and says so in its own signature:
 * {@link LocalDate#datesUntil(LocalDate)} walks from a date up to, and not including, the one given.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __DiscreteInterval
 * @see __TemporalInterval
 */
@Access(AccessType.FIELD)
@MappedSuperclass
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __MappedLocalDateInterval extends __MappedTemporalInterval<LocalDate>
        implements __DiscreteInterval<LocalDate> {

    // --------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected __MappedLocalDateInterval() {
        super();
    }

    // -------------------------------------------------------------------------------------------------------- derived

    /**
     * {@inheritDoc}
     *
     * @return {@link ChronoUnit#DAYS}, the step between two adjacent dates.
     */
    @Override
    @Transient
    public TemporalUnit getGranularity() {
        return ChronoUnit.DAYS;
    }



    /**
     * {@inheritDoc}
     *
     * @return the period from the {@link #getIntervalStart() start} of this interval to its
     *         {@link #getIntervalEnd() end}; {@code null} when either point of this interval is absent.
     * @implNote The return type is narrowed to {@link Period}, the amount dates measure in, so that a caller holding
     *         this type needs no cast.
     */
    @Override
    @Transient
    public @Nullable Period getTemporalAmount() {
        final LocalDate startInclusive = getIntervalStart();
        final LocalDate endExclusive = getIntervalEnd();
        if (startInclusive == null || endExclusive == null) {
            return null;
        }
        return Period.between(startInclusive, endExclusive);
    }
}
