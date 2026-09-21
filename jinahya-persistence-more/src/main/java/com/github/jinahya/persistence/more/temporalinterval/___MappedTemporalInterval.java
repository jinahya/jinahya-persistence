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
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.AssertTrue;
import org.jspecify.annotations.Nullable;

import java.time.temporal.Temporal;

/**
 * An abstract mapped superclass for an interval on a temporal axis, from an inclusive start to an exclusive end.
 * <p>
 * Two columns are mapped — {@value #COLUMN_NAME_INTERVAL_START} and {@value #COLUMN_NAME_INTERVAL_END} — and either
 * may be {@code NULL}, for an interval with no lower or no upper bound. Nothing else is stored: the
 * {@code [start, end)} convention is fixed by {@link ___TemporalInterval}, so there is no bound to record and nothing
 * to canonicalize.
 * <p>
 * The type of the two points is left to the subclass, which is what lets one class serve every axis. Both providers
 * resolve the type variable off the concrete subclass: Hibernate and EclipseLink have each been run against an entity
 * extending this hierarchy and against an {@link jakarta.persistence.Embeddable @Embeddable} embedded twice, and both
 * map the two columns and read them back. The design does not rest on an unverified assumption.
 *
 * <h2>This is the extension point</h2>
 * The classes beside it fix every {@code java.time} point type which can soundly bound an interval and which a
 * database can compare — see the package documentation for the set and for the two types deliberately left out. This
 * class is what to extend for anything outside it: a {@link Temporal} of a downstream's own, or one of the standard
 * ones under a storage arrangement the classes beside it do not take.
 * <p>
 * A subclass supplies the point type and the one method left abstract, narrowing its return to the amount that type
 * measures in. This is the shape each of the neighbouring classes is written in, {@link __MappedYearInterval} among
 * them:
 * {@snippet lang = "java":
 * @Access(AccessType.FIELD)
 * @MappedSuperclass
 * public abstract class __MappedSomeInterval extends ___MappedTemporalInterval<Year> {
 *
 *     @Override
 *     @Transient
 *     public Period getTemporalAmount() {
 *         final Year startInclusive = getIntervalStart();
 *         final Year endExclusive = getIntervalEnd();
 *         if (startInclusive == null || endExclusive == null) {
 *             return null;
 *         }
 *         return Period.ofYears(endExclusive.getValue() - startInclusive.getValue());
 *     }
 * }
 *}
 * The point type has to be one Jakarta Persistence maps, or the subclass has to convert it — see
 * {@link __MappedLocalTimeInterval}, which works a conversion through. And the caution below about an order which is
 * not the timeline applies to whatever is chosen here.
 *
 * <h2>Why everything here is prefixed</h2>
 * Both bare words are reserved. {@code END} closes a {@code CASE} in the SQL standard and {@code START} is reserved
 * alongside it, and a check against the keyword sets of H2, PostgreSQL, MySQL, MariaDB, Oracle, SQL Server, DB2 and
 * HSQLDB finds both reserved in every one of them — as is {@code INTERVAL} by itself. Gluing them together is what
 * makes them ordinary identifiers, which is why the columns are {@value #COLUMN_NAME_INTERVAL_START} and
 * {@value #COLUMN_NAME_INTERVAL_END} and the attributes {@code intervalStart} and {@code intervalEnd}.
 * <p>
 * The attributes are prefixed for a second reason: they are also what a query names. Both providers happen to accept
 * {@code i.end} in a path expression today, but {@code END} is a reserved identifier in the query language too, so the
 * shorter name would have been leaning on leniency rather than on the grammar.
 *
 * <h2>The order of the two points is not validated</h2>
 * Nothing here rejects an interval whose start is after its end. That is deliberate, and a change: this class used
 * to carry an {@link AssertTrue @AssertTrue} constraint for it.
 * <p>
 * The reasoning is that a reversed pair is data the caller wrote, and what a schema does about it is the schema's
 * business. Bean Validation runs only where a persistence unit is wired for it, so the constraint was never a
 * guarantee in the first place — it was a default policy, imposed on consumers who had not asked for one and
 * silently absent for the rest.
 * <p>
 * What to know before deciding to do nothing about it. A reversed interval is <em>inert</em> under containment:
 * {@code interval_start <= :t AND interval_end > :t} matches no {@code :t} at all. It is <em>not</em> inert under
 * overlap — the usual test, {@code a.start <= b.end AND b.start <= a.end}, reports a reversed interval as
 * overlapping things it cannot overlap — and {@code MIN(interval_start)} / {@code MAX(interval_end)} widen silently
 * around one. PostgreSQL refuses such a range outright, and Guava throws from its factory; neither treats it as
 * data.
 * <p>
 * Where enforcement is wanted, the database is the better place for it, and these column types make it available:
 * {@snippet lang = "sql":
 * CHECK (interval_start IS NULL OR interval_end IS NULL OR interval_start <= interval_end)
 *}
 * That holds for every writer, including one which never loads this class — which the constraint it replaces did
 * not.
 *
 * <h2>One caution about the bound</h2>
 * {@code Comparable<? super T>} admits {@link java.time.OffsetDateTime} and {@link java.time.ZonedDateTime}, whose
 * natural order is not the timeline: it falls through to the local value once two instants agree, so two points naming
 * the same moment in different zones compare as distinct. Measuring such an interval is still correct, since
 * {@link java.time.Duration#between(java.time.temporal.Temporal, java.time.temporal.Temporal) Duration.between} works
 * on instants. It is comparing them that is not — and the constraint above compares them.
 *
 * <h2>No {@code equals} or {@code hashCode}</h2>
 * Deliberately. Both columns are mutable and neither is an identifier, so value equality here would change under a
 * setter and take an instance's position in a hash-based collection with it. Identity belongs to the entity which
 * extends this class, and it is the entity's to define.
 *
 * <h2>Two intervals in one table</h2>
 * A downstream entity extends this hierarchy and gets one interval, because a {@code @MappedSuperclass} is inherited
 * once. A table which carries two — a stay and a hold, an effective period and a billing period — needs the second
 * form: a downstream {@link jakarta.persistence.Embeddable @Embeddable} of its own extending one of these classes,
 * embedded as many times as wanted, each with its own
 * {@link jakarta.persistence.AttributeOverride @AttributeOverride} pair.
 * {@snippet lang = "java":
 * @Embeddable
 * @Access(AccessType.FIELD)
 * public class DateInterval extends __MappedLocalDateInterval {}
 *
 * @Entity
 * public class Booking {
 *     @Embedded
 *     @AttributeOverride(name = "intervalStart", column = @Column(name = "stay_start"))
 *     @AttributeOverride(name = "intervalEnd", column = @Column(name = "stay_end"))
 *     private DateInterval stay;
 *
 *     @Embedded
 *     @AttributeOverride(name = "intervalStart", column = @Column(name = "hold_start"))
 *     @AttributeOverride(name = "intervalEnd", column = @Column(name = "hold_end"))
 *     private DateInterval hold;
 * }
 *}
 * Which is what settles what this class is entitled to fix and what it is not. The column names below are
 * <em>defaults</em>: a downstream with two intervals replaces both pairs, and {@value #COLUMN_NAME_INTERVAL_START} is
 * then never written to a schema at all. The attribute names are the opposite — {@code intervalStart} and
 * {@code intervalEnd} are declared here and a downstream cannot rename them, so they are what it must spell exactly,
 * in an {@code @AttributeOverride}, in a {@link jakarta.persistence.Convert @Convert}({@code attributeName}), and in
 * every query path. They are ordinary Java identifiers, visible on the accessors, and the static metamodel names them
 * type-safely.
 *
 * <h2>Access type</h2>
 * As in {@link com.github.jinahya.persistence.more.color the colour package}, {@link Access @Access}({@code FIELD}) is
 * forced: an entity which puts its {@link jakarta.persistence.Id @Id} on a getter would otherwise flip this hierarchy
 * to property access, and the {@link Transient @Transient} accessors inherited from {@link ___TemporalInterval} —
 * {@link ___TemporalInterval#isBounded() isBounded} and {@link ___TemporalInterval#isEmpty() isEmpty}, both shaped
 * exactly like JavaBeans properties — would then be taken for columns of their own. An entity extending
 * this class should declare {@code @Access(AccessType.FIELD)} too: Hibernate infers it, EclipseLink does not.
 * <p>
 * Every mapped superclass in this hierarchy carries it for the same reason, and the embeddable form above is what
 * forces the issue: EclipseLink walks the mapped-superclass chain of an {@code @Embeddable} and fails with a
 * {@link NullPointerException} — in {@code EmbeddableAccessor.preProcessMappedSuperclassMetadata} — where any link in
 * that chain has no access type of its own. Hibernate infers one and never notices.
 *
 * @param <T> the type of the two points limiting this interval
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ___TemporalInterval
 */
// forced, so that an entity which puts its @Id on a getter cannot flip this hierarchy to
// property access and, with it, silently unmap the two columns below.
// the cost: EclipseLink then requires the entity to declare @Access(AccessType.FIELD) too.
@Access(AccessType.FIELD)
@MappedSuperclass
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class ___MappedTemporalInterval<T extends Temporal & Comparable<? super T>>
        implements ___TemporalInterval<T> {

    // ---------------------------------------------------------------------------------------------------------- start

    /**
     * The name of the column, {@value}, holding the {@code intervalStart} attribute.
     */
    public static final String COLUMN_NAME_INTERVAL_START = "interval_start";

    // ------------------------------------------------------------------------------------------------------------- end

    /**
     * The name of the column, {@value}, holding the {@code intervalEnd} attribute.
     */
    public static final String COLUMN_NAME_INTERVAL_END = "interval_end";

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected ___MappedTemporalInterval() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    /**
     * Returns a string representation of this interval, in the mathematical notation for a half-open interval.
     *
     * @return a string representation of this interval, with an absent bound shown as {@code null}.
     */
    @Override
    public String toString() {
        return '[' + String.valueOf(intervalStart) + ", " + intervalEnd + ')';
    }

    // ----------------------------------------------------------------------------------------------------------- start

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public @Nullable T getIntervalStart() {
        return intervalStart;
    }

    /**
     * Replaces the point at which this interval starts, which it contains.
     *
     * @param startInclusive new value for the {@code intervalStart} attribute; {@code null} for no lower
     *                       bound.
     */
    public void setIntervalStart(final @Nullable T startInclusive) {
        intervalStart = startInclusive;
    }

    // ------------------------------------------------------------------------------------------------------------- end

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public @Nullable T getIntervalEnd() {
        return intervalEnd;
    }

    /**
     * Replaces the point at which this interval ends, which it does not contain.
     *
     * @param endExclusive new value for the {@code intervalEnd} attribute; {@code null} for no upper bound.
     */
    public void setIntervalEnd(final @Nullable T endExclusive) {
        intervalEnd = endExclusive;
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The point at which this interval starts, which it contains, mapped to the
     * {@value #COLUMN_NAME_INTERVAL_START} column.
     */
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_INTERVAL_START, nullable = true, insertable = true, updatable = true)
    private @Nullable T intervalStart;

    /**
     * The point at which this interval ends, which it does not contain, mapped to the
     * {@value #COLUMN_NAME_INTERVAL_END} column.
     */
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_INTERVAL_END, nullable = true, insertable = true, updatable = true)
    private @Nullable T intervalEnd;
}
