/**
 * Mapped superclasses for single {@code java.time} values which no date-time column keeps whole.
 * <p>
 * A {@link java.time.ZonedDateTime} or an {@link java.time.OffsetDateTime} written to a database comes back as the same
 * moment, and not reliably as the same value: the zone or the offset is lost, or replaced by the session's. And the one
 * MySQL type which keeps a moment as such, {@code TIMESTAMP}, ends in 2038. The classes here keep such a value in two
 * columns of portable types — the moment as a UTC date-time, in a column of no zone, and beside it what rebuilds the
 * value:
 * <ul>
 * <li>{@link com.github.jinahya.persistence.more.javatime.__MappedZonedDateTime} — a zone, for where the place
 *     matters.
 * <li>{@link com.github.jinahya.persistence.more.javatime.__MappedOffsetDateTime} — an offset, for where the offset
 *     itself is the fact to keep.
 * </ul>
 * Both extend {@link com.github.jinahya.persistence.more.javatime.___MappedUtcDateTime}, which holds the moment, and
 * nothing in any of them names a database. Intervals of {@code java.time} values are in
 * {@link com.github.jinahya.persistence.more.temporalinterval}.
 * <p>
 * Like the classes there, these are mapped superclasses: an entity extends one and gets one value, and an
 * {@link jakarta.persistence.Embeddable @Embeddable} of one's own extending one is embedded as many times as wanted,
 * each with its own {@link jakarta.persistence.AttributeOverride @AttributeOverride}s.
 * {@snippet :
 * @Embeddable
 * @Access(AccessType.FIELD)
 * public class Zoned extends __MappedZonedDateTime {}
 *
 * @Entity
 * public class Order {
 *
 *     @Embedded
 *     @AttributeOverride(name = "utcDateTime", column = @Column(name = "created_utc"))
 *     @AttributeOverride(name = "zoneId", column = @Column(name = "created_zone"))
 *     private Zoned created;
 * }
 *}
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
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
