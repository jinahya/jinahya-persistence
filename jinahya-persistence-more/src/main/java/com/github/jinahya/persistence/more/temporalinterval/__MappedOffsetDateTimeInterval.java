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
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * An abstract mapped superclass for an interval between two offset date-times.
 * <p>
 * The two columns come from {@link ___MappedTemporalInterval}, and the two points and their measuring from
 * {@link ___TemporalInterval}. What this class settles is the point type, the amount it is measured in, and one thing
 * the classes beside it have no need to settle: <strong>which order the interval is ordered by.</strong>
 *
 * <h2>Why this class exists at all</h2>
 * It would otherwise be unnecessary. An interval of {@link OffsetDateTime} is, to a database, the same interval as one
 * of {@link Instant} — the same column type, compared the same way — so an offset is payload the column carries rather
 * than part of what an interval is. On that reading {@link __MappedInstantInterval} is the class to reach for, and it
 * still is wherever the point type is a free choice.
 * <p>
 * This class exists because the point type is not always a free choice, and because the obvious way to use one anyway
 * is quietly wrong. Extending {@link ___MappedTemporalInterval}{@code <OffsetDateTime>} compiles, maps and reads back
 * correctly, and inherits an invariant which rejects intervals it should accept. There is nothing to warn a caller; the
 * rows simply fail validation.
 *
 * <h2>The order a database uses is not the one {@code compareTo} uses</h2>
 * {@link OffsetDateTime#compareTo(OffsetDateTime)} compares the instant first and then falls through to the local
 * date-time, so two points naming the same moment in different offsets compare as <em>distinct</em>. Every SQL type
 * these columns map to — {@code timestamp with time zone}, {@code datetimeoffset} — compares by the instant alone, and
 * calls the same two values equal.
 * <p>
 * An interval from {@code 2026-09-20T10:00+02:00} to {@code 2026-09-20T08:00Z} is therefore empty: one moment, twice
 * over. The database agrees. {@link OffsetDateTime#compareTo(OffsetDateTime)} does not — it reads the second point as
 * <em>before</em> the first — so anything decided on the natural order would answer differently depending on which of
 * two identical instants the caller happened to write.
 * <p>
 * {@link #isEmpty()} and {@link #contains(OffsetDateTime)} are overridden here to compare
 * {@link OffsetDateTime#toInstant() instants}, which is the order the column is actually kept in. That override is the
 * whole of this class.
 *
 * <h2>The instant round-trips; the offset may not</h2>
 * What comes back from these columns is reliably the same moment and not reliably the same offset, because the
 * databases disagree about whether an offset is worth keeping. H2, Oracle and SQL Server store it. PostgreSQL's
 * {@code timestamptz} keeps a UTC value and renders it in the session's zone, so the offset read back is the session's.
 * MySQL and DB2 map these columns without a zone at all and keep none.
 * <p>
 * Nothing above depends on that, since everything here is decided on the instant. But an application which needs the
 * offset it wrote — the one a user was standing in — should keep it in a column of its own rather than trust it to
 * survive here.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __MappedInstantInterval
 * @see ___MappedTemporalInterval
 */
@Access(AccessType.FIELD)
@MappedSuperclass
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __MappedOffsetDateTimeInterval extends ___MappedTemporalInterval<OffsetDateTime> {

    // --------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected __MappedOffsetDateTimeInterval() {
        super();
    }

    // ----------------------------------------------------------------------------------------------------- VALIDATION

    // ------------------------------------------------------------------------------------------------- order-dependent

    /**
     * {@inheritDoc}
     *
     * @return {@code true} when this interval is {@link #isBounded() bounded} and its two points name the same
     *         {@link OffsetDateTime#toInstant() instant}; {@code false} otherwise.
     * @implNote This overrides the inherited implementation because two spellings of one moment compare as
     *         distinct under {@link OffsetDateTime#compareTo(OffsetDateTime)}, and an interval whose ends are two such
     *         spellings is empty rather than merely short.
     */
    @Override
    @Transient
    public boolean isEmpty() {
        final OffsetDateTime startInclusive = getIntervalStart();
        final OffsetDateTime endExclusive = getIntervalEnd();
        return startInclusive != null
               && endExclusive != null
               && startInclusive.toInstant().equals(endExclusive.toInstant());
    }

    /**
     * {@inheritDoc}
     *
     * @param point {@inheritDoc}
     * @return {@code true} when the {@link OffsetDateTime#toInstant() instant} of the {@code point} is not before that
     *         of the {@link #getIntervalStart() start} and is before that of the {@link #getIntervalEnd() end};
     *         {@code false} otherwise.
     * @throws NullPointerException {@inheritDoc}
     * @implNote The offsets the three values are written in do not affect the answer, which is what the column
     *         they are stored in would also say.
     */
    @Override
    public boolean contains(final OffsetDateTime point) {
        Objects.requireNonNull(point, "point is null");
        final OffsetDateTime startInclusive = getIntervalStart();
        final OffsetDateTime endExclusive = getIntervalEnd();
        return (startInclusive == null || !startInclusive.toInstant().isAfter(point.toInstant()))
               && (endExclusive == null || point.toInstant().isBefore(endExclusive.toInstant()));
    }

    // -------------------------------------------------------------------------------------------------------- derived

    /**
     * {@inheritDoc}
     *
     * @return the duration from the {@link #getIntervalStart() start} of this interval to its
     *         {@link #getIntervalEnd() end}; {@code null} when either point of this interval is absent.
     * @implNote The return type is narrowed to {@link Duration}, so that a caller holding this type needs no
     *         cast. {@link Duration#between(java.time.temporal.Temporal, java.time.temporal.Temporal) Duration.between}
     *         measures these by their instants, so the length is unaffected by the offsets the two points were written
     *         in.
     */
    @Override
    @Transient
    public @Nullable Duration getTemporalAmount() {
        final OffsetDateTime startInclusive = getIntervalStart();
        final OffsetDateTime endExclusive = getIntervalEnd();
        if (startInclusive == null || endExclusive == null) {
            return null;
        }
        return Duration.between(startInclusive, endExclusive);
    }
}
