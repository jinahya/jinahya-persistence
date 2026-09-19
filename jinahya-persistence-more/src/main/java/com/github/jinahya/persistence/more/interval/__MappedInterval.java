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

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.AssertTrue;
import org.jspecify.annotations.Nullable;

/**
 * An abstract mapped superclass for an interval, from an inclusive start to an exclusive end.
 * <p>
 * Two columns are mapped — {@value #COLUMN_NAME_START} and {@value #COLUMN_NAME_END} — and either may be {@code NULL},
 * for an interval with no lower or no upper bound. Nothing else is stored: the {@code [start, end)} convention is fixed
 * by {@link __Interval}, so there is no bound to record and nothing to canonicalize.
 * <p>
 * The type of the two points is left to the subclass, which is what lets one class serve every axis. Whether a provider
 * resolves it is a question about the provider, not about this design: Hibernate reads a type variable off the concrete
 * subclass, and EclipseLink is the one to verify. <strong>That verification has not been done.</strong> If it fails,
 * the fallback is one mapped superclass per point type, and only this class changes.
 *
 * <h2>Column names</h2>
 * The columns are named for the concept rather than after the attributes they hold, because {@code END} is a reserved
 * word in the SQL standard — it closes a {@code CASE} — and in H2, PostgreSQL, SQL Server and Oracle with it, so a
 * column of that name fails at {@code CREATE TABLE}. The prefix earns its place a second time in a table carrying more
 * than one interval, where an {@link jakarta.persistence.AttributeOverride @AttributeOverride} is wanted anyway.
 *
 * <h2>The invariant is validated, because it cannot be enforced</h2>
 * A value class checks that the end does not precede the start where an instance is created. This class has no such
 * place: a provider builds an instance through the no-argument constructor and then assigns, so between
 * {@link #setStart(Comparable)} and {@link #setEnd(Comparable)} the pair is momentarily whatever the caller's order
 * makes it, whichever order that is. Rejecting it in a setter would reject legitimate sequences.
 * <p>
 * So it is a constraint rather than a guard — {@link #isStartNotAfterEnd()}, annotated
 * {@link AssertTrue @AssertTrue}, checked when the instance is complete and admitting a {@code null} on either side.
 * The method is named for the violation it reports rather than for what it computes: a constraint on a property is
 * reported against that property's path, and {@code startNotAfterEnd} tells a reader what went wrong where
 * {@code ordered} would not.
 *
 * <h2>No {@code equals} or {@code hashCode}</h2>
 * Deliberately. Both columns are mutable and neither is an identifier, so value equality here would change under a
 * setter and take an instance's position in a hash-based collection with it. Identity belongs to the entity which
 * extends this class, and it is the entity's to define.
 *
 * <h2>Access type</h2>
 * As in {@link com.github.jinahya.persistence.more.color the colour package}, {@link Access @Access}({@code FIELD}) is
 * forced: an entity which puts its {@link jakarta.persistence.Id @Id} on a getter would otherwise flip this hierarchy
 * to property access, and the {@link Transient @Transient} accessors below — {@link #isStartNotAfterEnd()} among them,
 * which is shaped exactly like a JavaBeans property — would then unmap the two columns themselves. An entity extending
 * this class should declare {@code @Access(AccessType.FIELD)} too: Hibernate infers it, EclipseLink does not.
 *
 * @param <T> the type of the two points limiting this interval
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __Interval
 */
// forced, so that an entity which puts its @Id on a getter cannot flip this hierarchy to
// property access and, with it, silently unmap the two columns below.
// the cost: EclipseLink then requires the entity to declare @Access(AccessType.FIELD) too.
@Access(AccessType.FIELD)
@MappedSuperclass
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __MappedInterval<T extends Comparable<? super T>> implements __Interval<T> {

    // ---------------------------------------------------------------------------------------------------------- start

    /**
     * The name of the column, {@value}, of the {@value #ATTRIBUTE_NAME_START} attribute.
     */
    public static final String COLUMN_NAME_START = "interval_start";

    /**
     * The name of the attribute, {@value}, mapped to the {@value #COLUMN_NAME_START} column.
     */
    public static final String ATTRIBUTE_NAME_START = "start";

    // ------------------------------------------------------------------------------------------------------------ end

    /**
     * The name of the column, {@value}, of the {@value #ATTRIBUTE_NAME_END} attribute.
     */
    public static final String COLUMN_NAME_END = "interval_end";

    /**
     * The name of the attribute, {@value}, mapped to the {@value #COLUMN_NAME_END} column.
     */
    public static final String ATTRIBUTE_NAME_END = "end";

    // --------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected __MappedInterval() {
        super();
    }

    // ----------------------------------------------------------------------------------------------- java.lang.Object

    /**
     * Returns a string representation of this interval, in the mathematical notation for a half-open interval.
     *
     * @return a string representation of this interval, with an absent bound shown as {@code null}.
     */
    @Override
    public String toString() {
        return '[' + String.valueOf(start) + ", " + end + ')';
    }

    // ----------------------------------------------------------------------------------------------------- VALIDATION

    /**
     * Returns whether this interval meets the one requirement an interval has, that its start is not after its end.
     *
     * @return {@code true} when either point of this interval is absent, or when its {@link #getStart() start} is not
     *         after its {@link #getEnd() end}; {@code false} otherwise.
     * @implSpec This method delegates to {@link #isOrdered()}, and exists to carry the constraint under a name which
     *         reads as the violation it reports.
     */
    @AssertTrue
    @Transient
    protected boolean isStartNotAfterEnd() {
        return isOrdered();
    }

    // ---------------------------------------------------------------------------------------------------------- start

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public @Nullable T getStart() {
        return start;
    }

    /**
     * Replaces the point at which this interval starts, which it contains.
     *
     * @param startInclusive new value for the {@value #ATTRIBUTE_NAME_START} attribute; {@code null} for no lower
     *                       bound.
     */
    public void setStart(final @Nullable T startInclusive) {
        start = startInclusive;
    }

    // ------------------------------------------------------------------------------------------------------------ end

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public @Nullable T getEnd() {
        return end;
    }

    /**
     * Replaces the point at which this interval ends, which it does not contain.
     *
     * @param endExclusive new value for the {@value #ATTRIBUTE_NAME_END} attribute; {@code null} for no upper bound.
     */
    public void setEnd(final @Nullable T endExclusive) {
        end = endExclusive;
    }

    // ----------------------------------------------------------------------------------------------------------------

    /**
     * The point at which this interval starts, which it contains, mapped to the {@value #COLUMN_NAME_START} column.
     */
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_START, nullable = true, insertable = true, updatable = true)
    private @Nullable T start;

    /**
     * The point at which this interval ends, which it does not contain, mapped to the {@value #COLUMN_NAME_END}
     * column.
     */
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_END, nullable = true, insertable = true, updatable = true)
    private @Nullable T end;
}
