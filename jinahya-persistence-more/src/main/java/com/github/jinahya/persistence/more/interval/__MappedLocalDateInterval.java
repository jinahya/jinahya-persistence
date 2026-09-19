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

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import org.jspecify.annotations.Nullable;

import java.time.LocalDate;
import java.time.Period;

/**
 * An abstract mapped superclass for an interval between two dates.
 * <p>
 * The two columns come from {@link __MappedInterval}, the measuring from {@link __MappedTemporalInterval}, and
 * everything order alone decides from {@link __Interval}. Dates are where a type's natural order and the order an
 * interval wants agree, so all of it is correct here as inherited, and what this class adds is what only dates can
 * answer.
 *
 * <h2>What a discrete axis affords</h2>
 * {@link #getEndInclusive()} has no counterpart on a continuous axis. A date has a predecessor, so the day before the
 * exclusive end is an exact answer, and the closed form people speak in — a conference <em>runs</em> through its last
 * day — converts without loss. An instant has none, which is why nothing above declares it.
 * <p>
 * The JDK agrees on the convention and says so in its own signature: {@link LocalDate#datesUntil(LocalDate)} walks from
 * a date up to, and not including, the one given.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __MappedTemporalInterval
 */
@MappedSuperclass
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __MappedLocalDateInterval extends __MappedTemporalInterval<LocalDate> {

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
     * @return the period from the {@link #getStart() start} of this interval to its {@link #getEnd() end};
     *         {@code null} when this interval is not {@link #isBounded() bounded}.
     * @implNote The return type is narrowed to {@link Period}, the amount dates measure in, so that a caller holding
     *         this type needs no cast.
     */
    @Override
    @Transient
    public @Nullable Period getTemporalAmount() {
        final LocalDate start = getStart();
        final LocalDate end = getEnd();
        if (start == null || end == null) {
            return null;
        }
        return Period.between(start, end);
    }

    /**
     * Returns the last date which this interval contains.
     *
     * @return the date before the {@link #getEnd() end} of this interval; {@code null} when this interval has no upper
     *         bound or is {@link #isEmpty() empty}.
     * @apiNote This is the closed form people speak in, and it is exact only because dates are discrete.
     */
    @Transient
    public @Nullable LocalDate getEndInclusive() {
        final LocalDate end = getEnd();
        if (end == null || isEmpty()) {
            return null;
        }
        return end.minusDays(1L);
    }
}
