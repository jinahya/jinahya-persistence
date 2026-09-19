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

import com.github.jinahya.persistence.more.converter.__TemporalAccessorStringAttributeConverters;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.MappedSuperclass;

import java.time.LocalDate;

/**
 * An abstract mapped superclass for an interval between two dates, each stored as an ISO 8601 string.
 * <p>
 * It behaves exactly as {@link __MappedLocalDateInterval}, which it extends, and differs only in what the two columns
 * hold: {@code CHAR(10)} carrying {@code "2026-09-19"} rather than a native {@code DATE}.
 *
 * <h2>Why this exists, and when not to use it</h2>
 * {@link LocalDate} is a natively supported basic type, so nothing here is <em>needed</em> — a native {@code DATE}
 * column is better in every way that matters: the database knows it is a date, and date functions and range indexes
 * apply to it. This class is for a schema which already stores its dates as text and cannot be migrated.
 * <p>
 * Its real value is as the worked example of the mechanism, at the one point type where the mechanism is optional. The
 * types which have no choice — {@link java.time.YearMonth} and {@link java.time.ZonedDateTime}, neither of which
 * Jakarta Persistence supports as a basic type — need exactly what is written below, and so do the deprecated
 * {@link java.util.Date} and {@link java.util.Calendar} intervals, for which a converted attribute is what removes the
 * requirement to carry the deprecated {@link jakarta.persistence.Temporal @Temporal}.
 *
 * <h2>The mechanism</h2>
 * The two attributes are declared in {@link __MappedInterval}, three classes up, and a subclass reaches them by name:
 * {@link Convert @Convert}({@code attributeName}) supplies the conversion, which is the answer to there being no
 * {@code @TemporalOverride}, and {@link AttributeOverride @AttributeOverride} reshapes the column the conversion now
 * writes into. Both are what the {@code @Convert} javadoc's own example does for attributes inherited from a mapped
 * superclass.
 * <p>
 * The converter is {@code @Converter(autoApply = false)}, so naming it here is the only way it applies, and it cannot
 * reach any other attribute in the unit.
 *
 * <h2>Ordering survives, because the form is fixed-width</h2>
 * {@link LocalDate#toString()} writes {@code uuuu-MM-dd}, always ten characters — which is where the column length
 * above comes from — so the lexicographic order of the
 * column is the chronological order of the values it holds. A containment still reads
 * {@code interval_start <= :t AND interval_end > :t} in SQL and still uses an index. That is a property of this form,
 * not of string storage in general — it does not hold for {@link java.time.ZonedDateTime}, whose zone suffix sorts
 * two equal instants apart.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __MappedLocalDateInterval
 * @see __TemporalAccessorStringAttributeConverters.OfLocalDate
 */
@AttributeOverride(
        name = __MappedInterval.ATTRIBUTE_NAME_START,
        column = @Column(name = __MappedInterval.COLUMN_NAME_START, nullable = true, insertable = true,
                         updatable = true, length = 10)
)
@AttributeOverride(
        name = __MappedInterval.ATTRIBUTE_NAME_END,
        column = @Column(name = __MappedInterval.COLUMN_NAME_END, nullable = true, insertable = true,
                         updatable = true, length = 10)
)
@Convert(
        attributeName = __MappedInterval.ATTRIBUTE_NAME_START,
        converter = __TemporalAccessorStringAttributeConverters.OfLocalDate.class
)
@Convert(
        attributeName = __MappedInterval.ATTRIBUTE_NAME_END,
        converter = __TemporalAccessorStringAttributeConverters.OfLocalDate.class
)
@MappedSuperclass
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __MappedLocalDateStringInterval extends __MappedLocalDateInterval {

    // --------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected __MappedLocalDateStringInterval() {
        super();
    }
}
