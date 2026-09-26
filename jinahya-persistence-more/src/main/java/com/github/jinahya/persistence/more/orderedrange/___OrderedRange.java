package com.github.jinahya.persistence.more.orderedrange;

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

import com.github.jinahya.persistence.more.temporalinterval.___TemporalInterval;
import jakarta.persistence.Transient;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * An interface for a range — two ends over a totally ordered domain, each of which may be absent, and each of which,
 * when present, either includes its endpoint or does not.
 *
 * <h2>The state space, which is nine shapes</h2>
 * Each end has three states, not four, because an absent endpoint has nothing for open or closed to apply to:
 * <table class="striped">
 * <caption>the nine shapes of a range</caption>
 * <thead><tr><th></th><th>upper absent</th><th>upper open</th><th>upper closed</th></tr></thead>
 * <tbody>
 * <tr><th scope="row">lower absent</th><td>{@code (,)}</td><td>{@code (,b)}</td><td>{@code (,b]}</td></tr>
 * <tr><th scope="row">lower open</th><td>{@code (a,)}</td><td>{@code (a,b)}</td><td>{@code (a,b]}</td></tr>
 * <tr><th scope="row">lower closed</th><td>{@code [a,)}</td><td>{@code [a,b)}</td><td>{@code [a,b]}</td></tr>
 * </tbody>
 * </table>
 * All nine are expressible, in any row, independently of every other row. The bound type of an end is <em>data</em>
 * here and not a property of the column — see {@link __MappedOrderedRange} for why that is worth the third fact per column.
 *
 * <h2>The two ends are independent, so the primitive is per end</h2>
 * {@link #getLowerBoundType()} and {@link #getUpperBoundType()} are what a range actually carries;
 * {@link __RangeBounds}, the pair, is derived from them by {@link #getRangeBounds()} and exists for the notation and
 * for the rules which are genuinely about the pair rather than about either end.
 * <p>
 * An absent endpoint has a {@code null} bound type, and the two always agree: a {@code null} endpoint has a
 * {@code null} bound type and a present one has a present bound type. Which is how the combination PostgreSQL has to
 * normalize away — a missing bound declared inclusive — is kept unwritable rather than corrected.
 *
 * <h2>The requirement is order, and only order</h2>
 * The one thing a range has to be able to say is that its lower endpoint is not above its upper. That needs the two
 * endpoints ordered and nothing else, so {@link Comparable} is the whole bound.
 * <p>
 * It is written {@code Comparable<? super C>} rather than {@code Comparable<C>} because several likely endpoint
 * types are not comparable to themselves: {@link java.time.LocalDate} implements
 * {@code Comparable<java.time.chrono.ChronoLocalDate>}, and {@link java.time.LocalDateTime} and
 * {@link java.time.ZonedDateTime} likewise compare against their {@code Chrono} interfaces.
 * <p>
 * Nothing enforces it — not here, and not on {@link __MappedOrderedRange} either. A range whose lower endpoint is above
 * its upper is data a caller wrote, and what a schema does about that is the schema's business. It is inert under
 * containment, matching nothing at all; it is <em>not</em> inert under the usual overlap test, which reports it as
 * overlapping ranges it cannot overlap. A {@code CHECK} constraint is the place to refuse it where that matters.
 *
 * <h2>How this differs from an interval</h2>
 * {@link ___TemporalInterval} is bound by
 * {@link java.time.temporal.Temporal} — a span between two <em>points on an axis</em>, which is what makes its
 * length a value — and it fixes {@code [start, end)} with no choice about it. This is bound by {@link Comparable}
 * alone, which is strictly weaker and admits what an interval cannot be:
 * <ul>
 * <li>{@link java.time.Duration} is {@code Comparable} but is a {@link java.time.temporal.TemporalAmount}, not a
 *     {@code Temporal}. <em>At least one hour, at most two</em> is an ordinary range and can never be an interval.
 * <li>{@link java.math.BigDecimal}, {@link Integer}, {@link String} — ordered, with no axis and no metric. It is
 *     meaningless to ask how long {@code ["apple","banana")} is.
 * </ul>
 * And one thing neither admits: {@link java.time.Period} is not {@link Comparable} at all, because {@code P1M} and
 * {@code P30D} have no defined order — is a month longer than thirty days? It depends which month, so
 * {@code java.time} declines to invent an answer.
 * <p>
 * That is the bound doing its job rather than getting in the way. A {@code Period} range is expressible the moment
 * the order is <em>stated</em>: wrap it in a type of the schema's own whose {@code compareTo} says what
 * <em>longer</em> means there — total months where days are always zero, say. The decision then has a name, which
 * is where it belongs. The same applies to any domain ordered by application rule rather than by nature: a status
 * ladder, a size chart, a version scheme.
 * <p>
 * The other difference is the bound lattice. Where the endpoints are temporal and the convention is uniformly
 * half-open, the temporal-interval package remains the better fit — it stores two plain typed columns, it measures, and it
 * needs none of the encoding this package requires.
 *
 * <h2>What is derived here</h2>
 * {@link #isBounded()}, {@link #isEmpty()}, {@link #contains(Comparable)} and {@link #getRangeBounds()}, because the
 * two endpoints, their bound types and their order are all any of them needs. Nothing else — no overlap, no
 * adjacency, no algebra over two ranges.
 * <p>
 * {@link #contains(Comparable)} is for a value in hand, and is not how rows are found; that is a query against the
 * two columns, and {@link __MappedOrderedRange} gives it.
 *
 * @param <C> the type of the two endpoints limiting this range
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __BoundType
 * @see __RangeBounds
 * @see __MappedOrderedRange
 */
@SuppressWarnings({
        "java:S114" // Interface names should comply with a naming convention
})
public interface ___OrderedRange<C extends Comparable<? super C>> {

    /**
     * Returns the endpoint at which this range begins.
     *
     * @return the lower endpoint; {@code null} when this range has no lower bound.
     */
    @Nullable
    C getRangeLower();

    /**
     * Returns whether the lower endpoint of this range belongs to it.
     *
     * @return the bound type of the lower end; {@code null} when, and only when, {@link #getRangeLower()} is
     *         {@code null}.
     */
    @Transient
    @Nullable
    __BoundType getLowerBoundType();

    /**
     * Returns the endpoint at which this range ends.
     *
     * @return the upper endpoint; {@code null} when this range has no upper bound.
     */
    @Nullable
    C getRangeUpper();

    /**
     * Returns whether the upper endpoint of this range belongs to it.
     *
     * @return the bound type of the upper end; {@code null} when, and only when, {@link #getRangeUpper()} is
     *         {@code null}.
     */
    @Transient
    @Nullable
    __BoundType getUpperBoundType();

    /**
     * Returns the two bound types of this range, as a pair.
     *
     * @return the bounds of this range; never {@code null}.
     * @implSpec The default implementation pairs {@link #getLowerBoundType()} with
     *         {@link #getUpperBoundType()}, contributing {@link __BoundType#OPEN} for an absent end — an end with no
     *         endpoint includes nothing, which is what {@code open} says.
     * @apiNote A pair is what the notation names and what PostgreSQL's range constructor takes — see
     *         {@link __RangeBounds#getNotation()}, which is the form a native range column is built from.
     */
    @Transient
    default __RangeBounds getRangeBounds() {
        final var lowerBoundType = getLowerBoundType();
        final var upperBoundType = getUpperBoundType();
        return __RangeBounds.of(lowerBoundType == null ? __BoundType.OPEN : lowerBoundType,
                                upperBoundType == null ? __BoundType.OPEN : upperBoundType);
    }

    /**
     * Returns whether both ends of this range have an endpoint.
     *
     * @return {@code true} when neither {@link #getRangeLower()} nor {@link #getRangeUpper()} is {@code null};
     *         {@code false} otherwise.
     * @implSpec The default implementation compares nothing and reads no bound type, so no endpoint type can
     *         make it wrong.
     */
    @Transient
    default boolean isBounded() {
        return getRangeLower() != null && getRangeUpper() != null;
    }

    /**
     * Returns whether this range contains no value at all.
     *
     * @return {@code true} when this range is {@link #isBounded() bounded}, its two endpoints compare equal, and at
     *         least one of its two ends is open; {@code false} otherwise.
     * @implSpec The default implementation compares the two endpoints rather than asking whether they are
     *         {@link Object#equals(Object) equal}, so emptiness is decided by the same order the invariant is.
     * @apiNote An empty range is a shape a range may legitimately have. Equal endpoints mean three different
     *         things across the four pairs: {@code [a,a]} holds exactly one value, {@code [a,a)} and {@code (a,a]} hold
     *         none, and {@code (a,a)} is not a range at all — the invariant on {@link __MappedOrderedRange} rejects
     *         it.
     *         <p>
     *         This answers only the emptiness visible in the endpoints. On a discrete domain {@code (a,b)} with nothing
     *         between {@code a} and {@code b} is equally empty, and seeing that needs a successor function, which
     *         {@link Comparable} does not supply and this package does not ask for.
     */
    @Transient
    default boolean isEmpty() {
        final C lower = getRangeLower();
        final C upper = getRangeUpper();
        if (lower == null || upper == null || lower.compareTo(upper) != 0) {
            return false;
        }
        return getLowerBoundType() == __BoundType.OPEN || getUpperBoundType() == __BoundType.OPEN;
    }

    /**
     * Returns whether the specified value falls within this range.
     *
     * @param value the value to test.
     * @return {@code true} when the {@code value} is inside both ends of this range; {@code false} otherwise.
     * @throws NullPointerException when the {@code value} is {@code null}.
     * @implSpec The default implementation compares the {@code value} against each present endpoint, strictly
     *         where that end is {@link __BoundType#OPEN}, and treats an absent end as holding — so a range with neither
     *         endpoint contains every value, and an {@link #isEmpty() empty} one contains none.
     * @apiNote This takes an argument, so it is not shaped like a JavaBeans getter and cannot be mistaken for a
     *         persistent property. The methods above are, and carry {@link Transient @Transient} for that reason.
     *         <p>
     *         Nothing in this package validates a range, so this and {@link #isEmpty()} are what a caller wanting to
     *         check one has — a candidate value against a range in hand, before persisting it, or a row already loaded.
     *         They are the reason {@code C} is bound by {@link Comparable} at all: no other method here compares
     *         anything, and storage would not need it.
     *         <p>
     *         They answer by {@code compareTo}, which is the order
     *         {@link __MappedOrderedRange#encode(Comparable) the encoding} is required to agree with. An encoding which
     *         sorts by something else leaves the database and these methods answering differently about the same range,
     *         silently.
     */
    default boolean contains(final C value) {
        Objects.requireNonNull(value, "value is null");
        final C lower = getRangeLower();
        if (lower != null) {
            final int compared = lower.compareTo(value);
            if (compared > 0 || (compared == 0 && getLowerBoundType() == __BoundType.OPEN)) {
                return false;
            }
        }
        final C upper = getRangeUpper();
        if (upper != null) {
            final int compared = value.compareTo(upper);
            if (compared > 0 || (compared == 0 && getUpperBoundType() == __BoundType.OPEN)) {
                return false;
            }
        }
        return true;
    }
}
