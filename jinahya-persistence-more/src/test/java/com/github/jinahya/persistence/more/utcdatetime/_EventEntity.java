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
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Access(AccessType.FIELD)
@Entity
@Table(name = _EventEntity.TABLE_NAME)
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public class _EventEntity {

    static final String TABLE_NAME = "event";

    static final String COLUMN_NAME_OCCURRED_UTC = "occurred_utc";

    static final String COLUMN_NAME_OCCURRED_ZONE = "occurred_zone";

    static final String COLUMN_NAME_RECORDED_UTC = "recorded_utc";

    static final String COLUMN_NAME_RECORDED_ZONE = "recorded_zone";

    static final String COLUMN_NAME_SIGNED_UTC = "signed_utc";

    static final String COLUMN_NAME_SIGNED_OFFSET = "signed_offset";

    /**
     * Returns the identifier of this entity.
     *
     * @return the identifier of this entity.
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns when this event occurred.
     *
     * @return when this event occurred.
     */
    public _ZonedDateTimeEmbeddable getOccurred() {
        return occurred;
    }

    /**
     * Replaces when this event occurred.
     *
     * @param occurred new value for the {@code occurred} attribute.
     */
    public void setOccurred(final _ZonedDateTimeEmbeddable occurred) {
        this.occurred = occurred;
    }

    /**
     * Returns when this event was recorded.
     *
     * @return when this event was recorded.
     */
    public _ZonedDateTimeEmbeddable getRecorded() {
        return recorded;
    }

    /**
     * Replaces when this event was recorded.
     *
     * @param recorded new value for the {@code recorded} attribute.
     */
    public void setRecorded(final _ZonedDateTimeEmbeddable recorded) {
        this.recorded = recorded;
    }

    /**
     * Returns when this event was signed.
     *
     * @return when this event was signed.
     */
    public _OffsetDateTimeEmbeddable getSigned() {
        return signed;
    }

    /**
     * Replaces when this event was signed.
     *
     * @param signed new value for the {@code signed} attribute.
     */
    public void setSigned(final _OffsetDateTimeEmbeddable signed) {
        this.signed = signed;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @AttributeOverride(name = "utcDateTime", column = @Column(name = COLUMN_NAME_OCCURRED_UTC))
    @AttributeOverride(name = "zoneId", column = @Column(name = COLUMN_NAME_OCCURRED_ZONE))
    @Embedded
    private _ZonedDateTimeEmbeddable occurred;

    @AttributeOverride(name = "utcDateTime", column = @Column(name = COLUMN_NAME_RECORDED_UTC))
    @AttributeOverride(name = "zoneId", column = @Column(name = COLUMN_NAME_RECORDED_ZONE))
    @Embedded
    private _ZonedDateTimeEmbeddable recorded;

    @AttributeOverride(name = "utcDateTime", column = @Column(name = COLUMN_NAME_SIGNED_UTC))
    @AttributeOverride(name = "zoneOffset", column = @Column(name = COLUMN_NAME_SIGNED_OFFSET))
    @Embedded
    private _OffsetDateTimeEmbeddable signed;
}

