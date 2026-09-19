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
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import java.util.Calendar;

/**
 * An abstract mapped superclass for an interval between two calendars.
 * <p>
 * {@link Calendar} is {@link Comparable} and is not a {@link java.time.temporal.Temporal}, so this class extends
 * {@link __MappedInterval} directly rather than {@link __MappedTemporalInterval}. It inherits the two columns, the
 * invariant and everything order alone decides, and adds nothing: there is no amount to measure with, since
 * {@code Calendar} has no {@code until}, and no predecessor to step back from.
 * <p>
 * Its ordering is at least not in question. {@link Calendar#compareTo(Calendar)} compares the two instants in
 * milliseconds, which is the chronological order and not a near miss at it.
 *
 * <h2>{@link Temporal @Temporal} cannot be declared here, and Jakarta Persistence says it must be</h2>
 * The specification still requires {@code @Temporal} on a persistent field of type {@link Calendar} or
 * {@link java.util.Date}, to say which of {@link TemporalType#DATE DATE}, {@link TemporalType#TIME TIME} or
 * {@link TemporalType#TIMESTAMP TIMESTAMP} the column holds. The two fields are declared in {@link __MappedInterval},
 * which is generic and carries no such annotation — and an inherited field cannot acquire one, because
 * {@link jakarta.persistence.AttributeOverride @AttributeOverride} overrides a
 * {@link jakarta.persistence.Column @Column} and there is no counterpart for {@code @Temporal}.
 * <p>
 * The annotation is itself {@code @Deprecated(since = "3.2")} in {@code jakarta.persistence-api}, saying
 * &quot;newly-written code should use the date/time types defined in {@code java.time}&quot; — so the type remains a
 * supported one whose only means of being mapped is deprecated, which is the whole case for deprecating this class.
 * <p>
 * <strong>So this class is not known to map.</strong> Both providers do default a missing {@code @Temporal} to
 * {@code TIMESTAMP} in practice, which is very likely why it will appear to work; that is a provider behaviour rather
 * than a portable one. If it has to be made portable, this class declares its own two columns instead of inheriting
 * them, and pays for the legacy types in the one place they are used.
 *
 * @param <T> the calendar type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __MappedInterval
 * @deprecated {@link Calendar} is kept for schemas which already have it, not offered for new ones. It is mutable, so
 *         {@link #getStart() getStart()} hands out a reference a caller can wind forward, and an invariant which
 *         passed validation is false afterwards. It keeps milliseconds, which is the precision the
 *         {@code 23:59:59.999} bug lives at. And {@code @Temporal} — the annotation it cannot do without — exists
 *         only for it and for {@link java.util.Date}. Use {@link __MappedTemporalInterval} and a {@code java.time}
 *         point type.
 */
@Deprecated
@MappedSuperclass
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __MappedCalendarInterval<T extends Calendar> extends __MappedInterval<T> {

    // --------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected __MappedCalendarInterval() {
        super();
    }
}
