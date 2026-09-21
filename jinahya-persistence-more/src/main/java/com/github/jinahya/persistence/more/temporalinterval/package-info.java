/**
 * Interfaces and mapped superclasses for intervals on a temporal axis.
 *
 * <h2>One convention, fixed</h2>
 * An interval here runs from an inclusive start to an exclusive end — {@code [start, end)} — and that is not a
 * per-instance choice. {@link ___TemporalInterval} carries the reasoning; in
 * short, a closed upper bound does not exist on a continuous axis, so any attempt to write one is exact only at the
 * precision which happens to be in play, while the half-open form ends at the next point exactly and lets adjacent
 * intervals meet with neither gap nor overlap.
 * <p>
 * The consequence for a schema is that an interval is two columns and nothing else. There is no bound token to store
 * and nothing to canonicalize, so a containment reads {@code start <= :t AND end > :t} and uses an ordinary index.
 *
 * <h2>Either point may be absent</h2>
 * A schema needs that constantly — in effect from a date, with no end decided — and an absent point is simply
 * {@code null} in its column. It is not an exception to the convention above: where there is no point, there is
 * nothing to include or to exclude.
 *
 * <h2>Points, not amounts</h2>
 * Of the three self-contained forms ISO 8601-1:2019 gives an interval in clause 4.4 — start and end, start and
 * duration, duration and end — only the first can express an absent bound at all, and only it derives the other two
 * without ambiguity: calendar arithmetic does not run backwards, so a stored amount and a stored point disagree about
 * the third value across a month boundary. The two points are therefore what is kept, and an amount is measured from
 * them when asked for.
 *
 * <h2>Two requirements: an order, and an axis</h2>
 * An interval has to be able to say two things. That its start is not after its end, which needs its two points
 * ordered; and how long it is, which needs them measurable against each other. So {@link ___TemporalInterval} is
 * bound by {@link java.lang.Comparable} and {@link java.time.temporal.Temporal} together — the order written
 * {@code Comparable<? super T>}, since {@link java.time.LocalDate} and its siblings compare against their
 * {@code Chrono} interfaces rather than against themselves. The order is documented and not enforced — see
 * {@link ___MappedTemporalInterval} for why, and for the {@code CHECK} constraint which enforces it where a schema
 * wants that.
 *
 * <h2>What is derived, and where each thing is derived</h2>
 * Every derived value sits at the layer which knows enough to answer it, and no higher:
 * <ul>
 * <li>{@code isBounded}, {@code isEmpty}, {@code contains} and the length are all on
 *     {@link ___TemporalInterval}, which is where the two points, their order and the arithmetic over them all sit.
 *     The length is the one of them which is not a convenience: the two points are kept <em>instead</em> of an
 *     amount, so the amount has to be obtainable from them.
 * <li>{@code getGranularity} and {@code getIntervalEndInclusive} are on
 *     {@link ___DiscreteInterval}, which a point type implements when its
 *     points are a fixed step apart — a date, a month, a year. Only such an axis has a predecessor to step back to,
 *     and only it can say how many steps long an interval is. On a continuous axis the point before the end is
 *     whatever precision is in play, which is the substitution the convention above exists to refuse, so
 *     {@link com.github.jinahya.persistence.more.temporalinterval.__MappedInstantInterval} and its continuous siblings do not
 *     implement it.
 * </ul>
 * That last one is why the interfaces are worth having at all. Discreteness is a property of a point type, not a
 * coincidence of which classes happen to declare a method, and stating it as a type lets a caller accept any discrete
 * interval and lets the next one added join the group by saying so.
 * There is no overlap and no adjacency between two intervals anywhere here. Those are questions about a pair of rows,
 * and a pair of rows is the database's to compare.
 * <p>
 * Containment is worth being exact about, because it is two questions wearing one name. Finding the rows in effect at
 * a moment is a query — {@code interval_start <= :t AND (interval_end IS NULL OR interval_end > :t)} against an
 * ordinary index, where the {@code IS NULL} is what honours an absent bound and is the half worth remembering. Asking
 * an interval already in hand whether it holds a point is the method, and it is for the point which has not been to
 * the database.
 * <p>
 * Two of the defaults compare, so they are only as good as the order the point type gives.
 * {@link java.time.OffsetDateTime} is the one shipped type whose order is not the timeline, and
 * {@link com.github.jinahya.persistence.more.temporalinterval.__MappedOffsetDateTimeInterval} overrides both against instants
 * — which is the whole of that class.
 *
 * <h2>What a schema stores, and the class for it</h2>
 * A database offers two kinds of column for a point in time, and no more: a <em>local reading</em>, which is what a
 * clock said and carries no zone — {@code DATE}, {@code TIME}, {@code TIMESTAMP} — and an <em>instant</em>, which is a
 * point on the timeline — {@code timestamp with time zone}, {@code datetimeoffset}. Each class below fixes a point
 * type for one of them and answers with the amount that type measures in, and adds nothing else:
 * <ul>
 * <li>{@code DATE} — {@link com.github.jinahya.persistence.more.temporalinterval.__MappedLocalDateInterval}, measuring in a
 *     {@link java.time.Period}.
 * <li>{@code TIME} — {@link com.github.jinahya.persistence.more.temporalinterval.__MappedLocalTimeInterval}, measuring in a
 *     {@link java.time.Duration}. Its axis is a day and does not wrap, so a span across midnight is two rows, and it
 *     is the least portable column here — see the class.
 * <li>{@code TIMESTAMP}, or {@code DATETIME}, holding no zone —
 *     {@link com.github.jinahya.persistence.more.temporalinterval.__MappedLocalDateTimeInterval}, measuring in a
 *     {@link java.time.Duration} which counts elapsed nanoseconds as though no daylight-saving transition ever
 *     happened, because a local point type has no zone for one to belong to.
 * <li>A moment rather than a reading —
 *     {@link com.github.jinahya.persistence.more.temporalinterval.__MappedInstantInterval}, measuring in a
 *     {@link java.time.Duration}. This is the one to reach for where the interval says <em>when</em> something was
 *     true irrespective of where.
 * <li>The same moment, where the point type is not a free choice —
 *     {@link com.github.jinahya.persistence.more.temporalinterval.__MappedOffsetDateTimeInterval}, also measuring in a
 *     {@link java.time.Duration}. See below for why it exists when the entry above it already covers the column.
 * </ul>
 *
 * <h2>Coarser axes: a year, a month</h2>
 * Two classes bound an interval at a granularity coarser than a day:
 * {@link com.github.jinahya.persistence.more.temporalinterval.__MappedYearInterval} and
 * {@link com.github.jinahya.persistence.more.temporalinterval.__MappedYearMonthInterval}, each measuring in a
 * {@link java.time.Period} of whole years, or of years and months.
 * <p>
 * <strong>When they are the right bound.</strong> When the thing being bounded genuinely changes only at a year or a
 * month boundary, and the day it changes on is not a fact the schema holds: a tax or fiscal year, the edition of a
 * standard, an annually published rate table; a billing or statement period, a payroll run, a monthly reporting
 * window, the validity of a card. {@code [2026, 2029)} covers three years and {@code [2026-01, 2026-04)} three
 * months, half-open exactly as everywhere else here.
 * <p>
 * <strong>When they are not.</strong> As soon as the answer to <em>from when in 2026</em> exists. A coarse bound is a
 * claim that the boundary genuinely falls between years or between months; widening a date to fit one throws the day
 * away rather than rounding it, and {@link com.github.jinahya.persistence.more.temporalinterval.__MappedLocalDateInterval} is
 * the type for anything that starts in March. The two are not interchangeable, and a schema which stores a year
 * because the day is inconvenient has lost the day, not simplified it.
 * <p>
 * <strong>They are stored differently from each other, and neither by accident.</strong> {@link java.time.Year} is a
 * basic type as of Jakarta Persistence 3.2 and maps to an {@code integer} column. {@link java.time.YearMonth} is not
 * one: unconverted, it falls through to the rule for a {@linkplain java.io.Serializable serializable} type and lands
 * in the column as bytes — {@code varbinary}, {@code bytea}, {@code raw} — with nothing reporting it, and two columns
 * of bytes cannot answer a containment. So that class carries a converter to the ISO {@code uuuu-MM} form, which is
 * seven characters wide for every year and therefore sorts chronologically. It is the one class here whose conversion
 * is not a schema preference but the difference between an interval and two blobs.
 * <p>
 * The reverse holds for {@link java.time.Year}, which is why it keeps a number: {@link java.time.Year#toString()}
 * writes {@code "999"} and {@code "2026"} at different widths, so a year stored as text sorts the shorter one last
 * and answers a containment wrongly for any year before 1000.
 *
 * <h2>The zone-carrying types, and which of them has a class</h2>
 * To a database, an interval of {@link java.time.OffsetDateTime} is the same interval as one of
 * {@link java.time.Instant}: the same column, the same index, compared the same way. Every SQL type these map to
 * compares by the instant alone, so an offset is payload the column happens to carry rather than part of what an
 * interval is. Where the point type is a free choice,
 * {@link com.github.jinahya.persistence.more.temporalinterval.__MappedInstantInterval} is the one to use.
 * <p>
 * {@link com.github.jinahya.persistence.more.temporalinterval.__MappedOffsetDateTimeInterval} exists for where it is not a
 * free choice, and it earns its place by correcting something rather than by being convenient.
 * {@link java.time.OffsetDateTime#compareTo(java.time.OffsetDateTime)} compares the instant and then falls through to
 * the local value, so two spellings of one moment compare as distinct — where the column calls them equal. Inheriting
 * anything decided on the natural order would read an interval whose two ends name the same instant in different
 * offsets as merely short rather than empty. That class overrides emptiness and containment to compare instants, and
 * that override is the whole of it.
 * <p>
 * What comes back from such a column is reliably the same moment and not reliably the same offset: H2, Oracle and SQL
 * Server keep the offset, PostgreSQL renders a stored UTC value in the session's zone, and MySQL and DB2 keep none. An
 * application needing the offset it wrote should give it a column of its own.
 *
 * <h2>The two types with no class, and why</h2>
 * <ul>
 * <li>{@link java.time.ZonedDateTime} — the zone does not survive. Written as {@code Europe/Paris} it reads back as
 *     {@code +02:00}: the moment is kept and the zone is dropped, silently, so the attribute's type promises something
 *     the column cannot hold. Keeping a zone properly means a third column beside the two, which is a different shape
 *     from the one this package is built on — an interval here is two columns and nothing else.
 * <li>{@link java.time.OffsetTime} — there is no column to aim at. The offset is dropped outright on PostgreSQL, MySQL
 *     and DB2, and the column is inflated to a full timestamp on Oracle and SQL Server. A time of day with an offset
 *     and no date cannot resolve a daylight-saving rule in any case, which is why {@code TIME WITH TIME ZONE} is
 *     widely held to be a mistake in SQL itself.
 * </ul>
 * Both fail quietly rather than loudly, which is the reason for saying so here: an absent class is easier to explain
 * than a row which validated, persisted, and came back meaning something else.
 *
 * <h2>One class per point type, and not one per column shape</h2>
 * The list above is one class per point type, and it stays that way. How a point is <em>stored</em> is a second axis —
 * a native column, ISO text, a number — and a class per cell would be the two axes multiplied.
 * <p>
 * It is not needed, because the storage axis is reachable without a class. The two attributes are declared in
 * {@link ___MappedTemporalInterval} and an entity reaches them by name, so
 * {@link jakarta.persistence.Convert @Convert}({@code attributeName}) supplies a conversion and
 * {@link jakarta.persistence.AttributeOverride @AttributeOverride} reshapes the column it writes into — on the entity,
 * in four lines. What a library can usefully hold is the converters, in
 * {@link com.github.jinahya.persistence.more.converter}, and those are one per type rather than one per pairing;
 * {@link com.github.jinahya.persistence.more.temporalinterval.__MappedLocalTimeInterval} works the case through.
 *
 * <h2>The point types are the {@code java.time} ones, and only those</h2>
 * {@link java.util.Date} and {@link java.util.Calendar} have no interval here. Jakarta Persistence still admits both as
 * basic types, but only through {@link jakarta.persistence.Temporal @Temporal}, which is itself
 * {@code @Deprecated(since = "3.2")} in favour of {@code java.time} — and that annotation cannot be applied to the two
 * attributes in any case, since they are declared in
 * {@link ___MappedTemporalInterval} and no
 * {@link jakarta.persistence.AttributeOverride @AttributeOverride} reaches it. Both types are also mutable, which a value
 * reached through two accessors and compared for order cannot survive.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://www.iso.org/standard/70907.html">ISO 8601-1:2019</a>
 */
@org.jspecify.annotations.NullMarked
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
