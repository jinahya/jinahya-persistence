package com.github.jinahya.persistence.more.javatime;

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

import com.github.jinahya.persistence.more.converter.javatime.ZoneIdStringAttributeConverter;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;

import java.lang.invoke.MethodHandles;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * An abstract mapped superclass for a {@link ZonedDateTime}, kept as a UTC date-time and a zone.
 * <p>
 * The moment is in the {@value ___MappedUtcDateTime#COLUMN_NAME_UTC_DATE_TIME} column, and the zone it was written in
 * — {@code Asia/Seoul}, not {@code +09:00} — in the {@value #COLUMN_NAME_ZONE_ID} column, as its
 * {@link ZoneId#getId() identifier}. The two together rebuild the value exactly: a moment and a zone name one
 * {@link ZonedDateTime}, offset and all, including which of the two offsets a daylight-saving overlap was written in.
 * <p>
 * No database keeps a zone in a date-time column on its own terms. PostgreSQL's {@code timestamptz} keeps the moment
 * and renders it in the session's zone, SQL Server's {@code datetimeoffset} keeps an offset, MySQL keeps neither, and
 * Hibernate's {@code @TimeZoneStorage} keeps an offset; a zone written as {@code Europe/Paris} reads back as
 * {@code +02:00}. This class keeps the zone in a column of its own, on every provider.
 *
 * <h2>For a moment already fixed</h2>
 * The moment is the source of truth, and the zone only says where to read it. That is right for something which has
 * happened — a log line, an order, an audit row — whose moment no later rule can move. It is not right for something
 * still to come, booked in local time: if the zone's rules change before then, {@code 10:00 in New York} is the
 * appointment, and the moment stored here is what has to give. Such a value is a local date-time and a zone, kept the
 * other way round.
 * <p>
 * A zone which the running JDK does not know cannot be read back at all; see {@link ZoneIdStringAttributeConverter}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __MappedOffsetDateTime
 */
@Access(AccessType.FIELD)
@MappedSuperclass
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __MappedZonedDateTime extends ___MappedUtcDateTime {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // --------------------------------------------------------------------------------------------------------- zoneId

    /**
     * The name of the column, {@value}, holding the {@code zoneId} attribute.
     */
    public static final String COLUMN_NAME_ZONE_ID = "zone_id";

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected __MappedZonedDateTime() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    @Override
    public String toString() {
        return super.toString() + '{' +
               "zoneId=" + zoneId +
               '}';
    }

    // --------------------------------------------------------------------------------------------------------- zoneId

    /**
     * Returns the zone in which the moment of this value was written.
     *
     * @return the current value of the {@code zoneId} attribute.
     */
    public ZoneId getZoneId() {
        return zoneId;
    }

    /**
     * Replaces the zone in which the moment of this value is read, leaving the moment as it is.
     *
     * @param zoneId new value for the {@code zoneId} attribute.
     */
    public void setZoneId(final ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    // -------------------------------------------------------------------------------------------------------- derived

    /**
     * Returns this value as a zoned date-time.
     *
     * @return the moment of this value in its zone; {@code null} when either the {@code utcDateTime} attribute or the
     *         {@code zoneId} attribute is {@code null}.
     */
    @Transient
    public ZonedDateTime getZonedDateTime() {
        final var utcDateTime = getUtcDateTime();
        if (utcDateTime == null || zoneId == null) {
            return null;
        }
        return utcDateTime.atZone(ZoneOffset.UTC).withZoneSameInstant(zoneId);
    }

    /**
     * Replaces this value with specified zoned date-time.
     *
     * @param zonedDateTime the zoned date-time; {@code null} to clear both attributes.
     */
    public void setZonedDateTime(final ZonedDateTime zonedDateTime) {
        if (zonedDateTime == null) {
            setUtcDateTime(null);
            zoneId = null;
            return;
        }
        setUtcDateTime(zonedDateTime.withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime());
        zoneId = zonedDateTime.getZone();
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The zone in which the moment of this value was written, mapped to the {@value #COLUMN_NAME_ZONE_ID} column.
     */
    @Convert(converter = ZoneIdStringAttributeConverter.class)
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_ZONE_ID, nullable = true, insertable = true, updatable = true)
    private ZoneId zoneId;
}

