package com.github.jinahya.persistence.more.utcdatetime;

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
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;

import java.lang.invoke.MethodHandles;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * An abstract mapped superclass for a moment kept as a UTC date-time, in a column of no zone.
 * <p>
 * The moment is stored as the {@link LocalDateTime} a clock in UTC would read at it, in the
 * {@value #COLUMN_NAME_UTC_DATE_TIME} column. That column is the one to sort, compare and index on: every row is
 * written in the same zone, so the order of its values is the order of the moments. What a subclass adds beside it is
 * whatever rebuilds the value the moment was written as — a zone in {@link MappedZonedDateTime}, an offset in
 * {@link MappedOffsetDateTime}.
 *
 * <h2>Why a column of no zone</h2>
 * A {@link LocalDateTime} maps to the plainest date-time column a database has — {@code TIMESTAMP},
 * {@code DATETIME}, {@code datetime2} — and none of them has a range ending in 2038. MySQL's {@code TIMESTAMP}
 * does, being 32-bit seconds since the epoch; it is also the only MySQL type which normalizes a value to UTC, which is
 * why it gets chosen. This class does that normalization itself, so the column needs no zone and no such type.
 * <p>
 * The mapping asks for no {@code columnDefinition}, so every provider picks its own type for the column. A schema which
 * wants a particular one — MySQL's {@code DATETIME(6)}, say, for the microseconds — says so on the entity:
 * {@snippet :
 * @AttributeOverride(name = "utcDateTime",
 *                    column = @Column(name = "created_utc", columnDefinition = "DATETIME(6)"))
 *}
 *
 * <h2>Run the JVM in UTC</h2>
 * The column holds no zone, and the value written to it should pass through none. Hibernate and EclipseLink both carry
 * a {@link LocalDateTime} through a {@link java.sql.Timestamp} in the JVM's default zone, so a UTC date-time which
 * falls in a spring-forward gap of that zone — {@code 2026-03-08T02:30} where the default is
 * {@code America/New_York} — does not exist there, and is stored the length of the gap later. Every other value
 * survives, overlaps included. A JVM whose default zone is UTC, {@code -Duser.timezone=UTC}, has no gap, and is the
 * setting to run these classes under; a default zone with daylight saving loses an hour of moments a year.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @apiNote The precision of the value read back is the column's. A column of microseconds drops the nanoseconds
 *         a {@link LocalDateTime} can carry, and the moment read back is truncated accordingly.
 * @see MappedZonedDateTime
 * @see MappedOffsetDateTime
 */
@Access(AccessType.FIELD)
@MappedSuperclass
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class _MappedUtcDateTime {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // ---------------------------------------------------------------------------------------------------- utcDateTime

    /**
     * The name of the column, {@value}, holding the {@code utcDateTime} attribute.
     */
    public static final String COLUMN_NAME_UTC_DATE_TIME = "utc_date_time";

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected _MappedUtcDateTime() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    @Override
    public String toString() {
        return super.toString() + '{' +
               "utcDateTime=" + utcDateTime +
               '}';
    }

    // ---------------------------------------------------------------------------------------------------- utcDateTime

    /**
     * Returns the date-time a clock in UTC reads at the moment of this value.
     *
     * @return the current value of the {@code utcDateTime} attribute.
     */
    public LocalDateTime getUtcDateTime() {
        return utcDateTime;
    }

    /**
     * Replaces the date-time a clock in UTC reads at the moment of this value.
     *
     * @param utcDateTime new value for the {@code utcDateTime} attribute.
     */
    public void setUtcDateTime(final LocalDateTime utcDateTime) {
        this.utcDateTime = utcDateTime;
    }

    // -------------------------------------------------------------------------------------------------------- derived

    /**
     * Returns the moment of this value.
     *
     * @return the moment of this value; {@code null} when the {@code utcDateTime} attribute is {@code null}.
     * @apiNote This class keeps no zone, so it has no {@code getZonedDateTime()} or {@code getOffsetDateTime()}; a
     *         subclass which keeps one has its own. To read the moment at a zone or an offset of the caller's
     *         choosing, ask the instant itself:
     *         {@snippet :
     *         ZonedDateTime zonedDateTime = value.getInstant().atZone(zoneId);
     *         OffsetDateTime offsetDateTime = value.getInstant().atOffset(zoneOffset);
     *}
     */
    @Transient
    public Instant getInstant() {
        if (utcDateTime == null) {
            return null;
        }
        return utcDateTime.toInstant(ZoneOffset.UTC);
    }

    /**
     * Replaces the moment of this value, leaving whatever a subclass keeps beside it — a zone, an offset — as it is.
     *
     * @param instant the moment; {@code null} to clear the {@code utcDateTime} attribute.
     * @apiNote Only the moment of a zoned or an offset date-time passed here is kept; its zone or offset is dropped,
     *         and a subclass's own zone or offset stays as it was:
     *         {@snippet :
     *         value.setInstant(zonedDateTime.toInstant());    // value's zone, if any, unchanged
     *}
     *         To replace the zone or the offset too, use the subclass's
     *         {@link MappedZonedDateTime#setZonedDateTime(java.time.ZonedDateTime) setZonedDateTime} or
     *         {@link MappedOffsetDateTime#setOffsetDateTime(java.time.OffsetDateTime) setOffsetDateTime}.
     */
    public void setInstant(final Instant instant) {
        if (instant == null) {
            setUtcDateTime(null);
            return;
        }
        setUtcDateTime(LocalDateTime.ofInstant(instant, ZoneOffset.UTC));
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The date-time a clock in UTC reads at the moment of this value, mapped to the
     * {@value #COLUMN_NAME_UTC_DATE_TIME} column.
     */
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_UTC_DATE_TIME, nullable = true, insertable = true, updatable = true)
    private LocalDateTime utcDateTime;
}

