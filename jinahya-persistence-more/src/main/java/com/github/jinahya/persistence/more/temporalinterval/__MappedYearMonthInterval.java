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

import com.github.jinahya.persistence.more.converter.__TemporalAccessorStringAttributeConverters;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import org.jspecify.annotations.Nullable;

import java.time.Period;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

/**
 * An abstract mapped superclass for an interval between two months, each stored as an ISO {@code uuuu-MM} string.
 * <p>
 * The two columns come from {@link ___MappedTemporalInterval}, and the two points and their measuring from
 * {@link ___TemporalInterval}. {@link YearMonth#compareTo(YearMonth)} compares the year and then the month, which
 * is the timeline at the granularity a month has.
 *
 * <h2>When a month is the right bound</h2>
 * When the thing being bounded is itself monthly and no day within the month is a fact the schema holds: a billing or
 * statement period, a payroll run, a monthly reporting window, the validity of a card. Written {@code [2026-01,
 * 2026-04)} such an interval covers January, February and March.
 * <p>
 * When it is not: when a day exists. Month-granularity is a claim that the boundary genuinely falls between months,
 * and {@link __MappedLocalDateInterval} is the type for anything finer.
 *
 * <h2>This class is the one which <em>has</em> to convert</h2>
 * Every other class in this package maps its point type without help, because Jakarta Persistence defines it as a
 * basic type. {@link YearMonth} is not one — the list in {@link jakarta.persistence.Basic} runs to {@code Instant} and
 * {@link java.time.Year} and stops — so an unconverted {@code YearMonth} attribute falls through to the rule for any
 * other {@linkplain java.io.Serializable serializable} type and lands in the column as bytes: {@code varbinary} on H2,
 * {@code bytea} on PostgreSQL, {@code raw} on Oracle.
 * <p>
 * Nothing reports that. The entity maps, the rows persist, and the two columns cannot answer
 * {@code interval_start <= :t AND interval_end > :t}, because a database cannot order a serialized object. That is
 * what this class exists to prevent, and it is why it is a class rather than advice: the conversion is not a schema
 * preference here, it is the difference between an interval and two blobs.
 *
 * <h2>An entity has to restate the conversion, for EclipseLink</h2>
 * The two {@link Convert @Convert} declarations below are on a mapped superclass and name attributes declared two
 * classes further up, in {@link ___MappedTemporalInterval}. Hibernate applies them. <strong>EclipseLink ignores them</strong>,
 * and an unconverted {@link YearMonth} is then stored by the rule above — as bytes, silently, exactly as though this
 * class had declared nothing.
 * <p>
 * An entity restates them, which both providers honour:
 * {@snippet lang = "java":
 * @Entity
 * @Convert(attributeName = "intervalStart",
 *          converter = __TemporalAccessorStringAttributeConverters.OfYearMonth.class)
 * @Convert(attributeName = "intervalEnd",
 *          converter = __TemporalAccessorStringAttributeConverters.OfYearMonth.class)
 * public class BillingPeriod extends __MappedYearMonthInterval { ... }
 *}
 * The converter also has to be reachable by the persistence unit. Under
 * {@code <exclude-unlisted-classes>true</exclude-unlisted-classes>} EclipseLink resolves it only against the unit's
 * listed classes, so it has to be listed there; Hibernate finds one which is merely referenced.
 * <p>
 * This is the one class here which cannot fully declare its own mapping, and it is worth being plain about rather than
 * leaving to be discovered: the symptom is a column of bytes and no error. It is also why every concrete converter in
 * {@link com.github.jinahya.persistence.more.converter} states its own two type arguments — EclipseLink does not
 * resolve them through a converter's superclasses, and a converter whose arguments it cannot read is one it applies on
 * the way in and not on the way out.
 *
 * <h2>The ISO form sorts, because it is fixed-width</h2>
 * {@link YearMonth#toString()} writes {@code uuuu-MM}, always seven characters — {@code "2026-01"}, and
 * {@code "0999-12"} with the year padded — which is where the column length comes from. The lexicographic order of
 * the column is therefore the chronological order of the values it holds, so a containment still reads as it does
 * everywhere else here and still uses an ordinary index.
 * <p>
 * That is a property of this form and not of text storage in general. {@link java.time.Year} is the counter-example
 * one class over: it writes {@code "999"} and {@code "2026"} at different widths and does not sort, which is why it
 * keeps a numeric column instead.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ___DiscreteInterval
 * @see __MappedYearInterval
 * @see __TemporalAccessorStringAttributeConverters.OfYearMonth
 */
@AttributeOverride(
        name = "intervalStart",
        column = @Column(name = ___MappedTemporalInterval.COLUMN_NAME_INTERVAL_START, nullable = true, insertable = true,
                         updatable = true, length = 7)
)
@AttributeOverride(
        name = "intervalEnd",
        column = @Column(name = ___MappedTemporalInterval.COLUMN_NAME_INTERVAL_END, nullable = true, insertable = true,
                         updatable = true, length = 7)
)
@Convert(
        attributeName = "intervalStart",
        converter = __TemporalAccessorStringAttributeConverters.OfYearMonth.class
)
@Convert(
        attributeName = "intervalEnd",
        converter = __TemporalAccessorStringAttributeConverters.OfYearMonth.class
)
@Access(AccessType.FIELD)
@MappedSuperclass
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __MappedYearMonthInterval extends ___MappedTemporalInterval<YearMonth>
        implements ___DiscreteInterval<YearMonth> {

    // --------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected __MappedYearMonthInterval() {
        super();
    }

    // -------------------------------------------------------------------------------------------------------- derived

    /**
     * {@inheritDoc}
     *
     * @return {@link ChronoUnit#MONTHS}, the step between two adjacent months.
     */
    @Override
    @Transient
    public TemporalUnit getGranularity() {
        return ChronoUnit.MONTHS;
    }



    /**
     * {@inheritDoc}
     *
     * @return the period from the {@link #getIntervalStart() start} of this interval to its
     *         {@link #getIntervalEnd() end}; {@code null} when either point of this interval is absent.
     * @implNote The months between the two points are counted and then
     *           {@link Period#normalized() normalized}, so the period carries years and months and never a day:
     *           {@code [2026-01, 2027-03)} measures {@code P1Y2M} rather than {@code P14M}.
     */
    @Override
    @Transient
    public @Nullable Period getTemporalAmount() {
        final YearMonth startInclusive = getIntervalStart();
        final YearMonth endExclusive = getIntervalEnd();
        if (startInclusive == null || endExclusive == null) {
            return null;
        }
        return Period.ofMonths(Math.toIntExact(startInclusive.until(endExclusive, ChronoUnit.MONTHS))).normalized();
    }
}
