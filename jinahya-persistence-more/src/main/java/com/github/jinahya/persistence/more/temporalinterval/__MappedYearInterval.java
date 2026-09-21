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

import java.time.Period;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

/**
 * An abstract mapped superclass for an interval between two years, mapped to two integer columns.
 * <p>
 * The two columns come from {@link ___MappedTemporalInterval}, and the two points and their measuring from
 * {@link ___TemporalInterval}. {@link Year#compareTo(Year)} compares the year values, which is the timeline at the
 * granularity a year has.
 *
 * <h2>When a year is the right bound</h2>
 * When the thing being bounded genuinely changes only at a year boundary, and the day it changes on is not a fact the
 * schema holds: a tax or fiscal year, the edition of a standard, a rate table that is published annually, a model year.
 * Written {@code [2026, 2029)} such an interval covers 2026, 2027 and 2028, and the half-open form reads as it does
 * everywhere else here.
 * <p>
 * When it is <em>not</em> the right bound: when the answer to "from when in 2026" exists. A year interval cannot hold
 * it, and widening a date to a year to make it fit throws the day away rather than rounding it.
 * {@link __MappedLocalDateInterval} is the type for anything which starts in March.
 *
 * <h2>{@link Year} is a basic type, and its column is a number</h2>
 * {@link Year} was added to the basic types by Jakarta Persistence 3.2, so the two inherited columns need no converter:
 * a provider maps them to {@code integer}, or to {@code number(10,0)} where that is what the database calls one. The
 * column compares and indexes as a number, which is the order the years are in.
 * <p>
 * That is worth being deliberate about, because the text form of this type does <em>not</em> sort:
 * {@link Year#toString()} writes {@code "999"} and {@code "2026"} at different widths, and lexicographically the
 * shorter one is the larger. A {@link Year} stored as a string would answer
 * {@code interval_start <= :t AND interval_end > :t} wrongly for any year before 1000, silently. The numeric column is
 * not merely the default here, it is the only sound form.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ___DiscreteInterval
 * @see __MappedYearMonthInterval
 */
@Access(AccessType.FIELD)
@MappedSuperclass
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __MappedYearInterval extends ___MappedTemporalInterval<Year>
        implements ___DiscreteInterval<Year> {

    // --------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected __MappedYearInterval() {
        super();
    }

    // -------------------------------------------------------------------------------------------------------- derived

    /**
     * {@inheritDoc}
     *
     * @return {@link ChronoUnit#YEARS}, the step between two adjacent years.
     */
    @Override
    @Transient
    public TemporalUnit getGranularity() {
        return ChronoUnit.YEARS;
    }



    /**
     * {@inheritDoc}
     *
     * @return the period from the {@link #getIntervalStart() start} of this interval to its
     *         {@link #getIntervalEnd() end}; {@code null} when either point of this interval is absent.
     * @implNote The return type is narrowed to {@link Period}, and the period carries whole years and nothing else,
     *           since whole years are all this axis has. {@code [2026, 2029)} measures {@code P3Y}.
     */
    @Override
    @Transient
    public @Nullable Period getTemporalAmount() {
        final Year startInclusive = getIntervalStart();
        final Year endExclusive = getIntervalEnd();
        if (startInclusive == null || endExclusive == null) {
            return null;
        }
        return Period.ofYears(Math.toIntExact(startInclusive.until(endExclusive, ChronoUnit.YEARS)));
    }
}
