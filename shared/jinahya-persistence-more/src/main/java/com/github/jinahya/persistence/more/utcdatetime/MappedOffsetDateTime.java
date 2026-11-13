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

import com.github.jinahya.persistence.more.converter.javatime.ZoneOffsetStringAttributeConverter;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;

import java.lang.invoke.MethodHandles;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * An abstract mapped superclass for an {@link OffsetDateTime}, kept as a UTC date-time and an offset.
 * <p>
 * The moment is in the {@value _MappedUtcDateTime#COLUMN_NAME_UTC_DATE_TIME} column, and the offset it was written
 * at in the {@value #COLUMN_NAME_ZONE_OFFSET} column, as its {@link ZoneOffset#getId() identifier} — {@code +09:00},
 * or {@code +08:27:52} for an offset of seconds, which some historical ones are. The two rebuild the value exactly,
 * and the local date-time it was written as is the moment plus the offset.
 * <p>
 * The moment is kept rather than the local date-time, so that the column the rows are ordered by is the one in which
 * their order is the order of the moments. Two local date-times at different offsets do not compare as their moments
 * do.
 *
 * <h2>An offset is not a zone</h2>
 * {@code +09:00} says what the clock was ahead of UTC by, not where it was: Seoul and Tokyo share it. Nothing here
 * depends on the rules of a zone, which is the point where the offset itself is the fact to keep — a signature, a legal
 * record — and no later change to a time-zone database should be able to move it. Where the place matters, use
 * {@link MappedZonedDateTime}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see MappedZonedDateTime
 */
@Access(AccessType.FIELD)
@MappedSuperclass
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class MappedOffsetDateTime extends _MappedUtcDateTime {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // ----------------------------------------------------------------------------------------------------- zoneOffset

    /**
     * The name of the column, {@value}, holding the {@code zoneOffset} attribute.
     */
    public static final String COLUMN_NAME_ZONE_OFFSET = "zone_offset";

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected MappedOffsetDateTime() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    @Override
    public String toString() {
        return super.toString() + '{' +
               "zoneOffset=" + zoneOffset +
               '}';
    }

    // ----------------------------------------------------------------------------------------------------- zoneOffset

    /**
     * Returns the offset at which the moment of this value was written.
     *
     * @return the current value of the {@code zoneOffset} attribute.
     */
    public ZoneOffset getZoneOffset() {
        return zoneOffset;
    }

    /**
     * Replaces the offset at which the moment of this value is read, leaving the moment as it is.
     *
     * @param zoneOffset new value for the {@code zoneOffset} attribute.
     */
    public void setZoneOffset(final ZoneOffset zoneOffset) {
        this.zoneOffset = zoneOffset;
    }

    // --------------------------------------------------------------------------------------------------------- derived

    /**
     * Returns this value as an offset date-time.
     *
     * @return the moment of this value at its offset; {@code null} when either the {@code utcDateTime} attribute or
     *         the {@code zoneOffset} attribute is {@code null}.
     */
    @Transient
    public OffsetDateTime getOffsetDateTime() {
        final var utcDateTime = getUtcDateTime();
        if (utcDateTime == null || zoneOffset == null) {
            return null;
        }
        return utcDateTime.atOffset(ZoneOffset.UTC).withOffsetSameInstant(zoneOffset);
    }

    /**
     * Replaces this value with specified offset date-time.
     *
     * @param offsetDateTime the offset date-time; {@code null} to clear both attributes.
     */
    public void setOffsetDateTime(final OffsetDateTime offsetDateTime) {
        if (offsetDateTime == null) {
            setUtcDateTime(null);
            zoneOffset = null;
            return;
        }
        setUtcDateTime(offsetDateTime.withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime());
        zoneOffset = offsetDateTime.getOffset();
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The offset at which the moment of this value was written, mapped to the {@value #COLUMN_NAME_ZONE_OFFSET}
     * column.
     */
    @Convert(converter = ZoneOffsetStringAttributeConverter.class)
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_ZONE_OFFSET, nullable = true, insertable = true, updatable = true)
    private ZoneOffset zoneOffset;
}

