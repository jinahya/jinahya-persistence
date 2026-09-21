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

import java.util.Objects;

/**
 * The four ways a range can include or exclude the two endpoints it has.
 * <p>
 * The pair, where {@link __BoundType} is the single end. A range carries its two ends independently — each column
 * holds its own bound character — so this is derived rather than stored: {@link ___OrderedRange#getRangeBounds()} pairs the
 * two and answers with a constant here.
 *
 * <h2>The notation</h2>
 * {@link #getNotation()} answers the two-character form — {@code "[)"}, {@code "[]"}, {@code "(]"}, {@code "()"} —
 * which is at once the mathematical notation and PostgreSQL's own, where it is the third argument to a range
 * constructor. So a range stored by this package reaches a native range column as
 * {@code daterange(lower, upper, '[)')}, with no translation.
 * <p>
 * ISO 80000-2 also admits {@code ]a,b[} for an open bound. It is not used here: every implementation anyone deploys
 * orients the bracket instead, and that is the form which round-trips.
 *
 * <h2>An absent endpoint is not one of these</h2>
 * These four describe the endpoints a range <em>has</em>. Where an endpoint is absent there is nothing to include or
 * to exclude, and {@link ___OrderedRange#getRangeBounds()} contributes {@link __BoundType#OPEN} for it — which is why a
 * range with neither endpoint reads as {@link #OPEN} and never as {@link #CLOSED}. PostgreSQL has to normalize that
 * case away, because its text form lets {@code [,]} be written; here it cannot be written at all.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __BoundType
 * @see <a href="https://www.postgresql.org/docs/current/rangetypes.html">PostgreSQL range types</a>
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public enum __RangeBounds {

    /**
     * Both endpoints included, {@code []}. The shape of a band declared as <em>from x to y</em> — an age band, a
     * grade band, an amount of time <em>at least</em> x and <em>at most</em> y.
     */
    CLOSED('[', ']'),

    /**
     * The lower endpoint included and the upper excluded, {@code [)}. The shape which tiles an axis: two adjacent
     * ranges meet with neither gap nor overlap, which is why it is the only one
     * {@link com.github.jinahya.persistence.more.temporalinterval the temporal-interval package} offers.
     */
    CLOSED_OPEN('[', ')'),

    /**
     * The lower endpoint excluded and the upper included, {@code (]}. The shape of a band declared as <em>over x, up
     * to y</em> — a tax bracket, a tariff step.
     */
    OPEN_CLOSED('(', ']'),

    /**
     * Neither endpoint included, {@code ()}. Also what a range with no endpoint at all reads as.
     */
    OPEN('(', ')');

    // -------------------------------------------------------------------------------------------------- STATIC FACTORY

    /**
     * Returns the constant pairing the specified two bound types.
     *
     * @param lowerBoundType the bound type of the lower end.
     * @param upperBoundType the bound type of the upper end.
     * @return the constant for the pair; never {@code null}.
     * @throws NullPointerException if either argument is {@code null}.
     */
    public static __RangeBounds of(final __BoundType lowerBoundType, final __BoundType upperBoundType) {
        Objects.requireNonNull(lowerBoundType, "lowerBoundType is null");
        Objects.requireNonNull(upperBoundType, "upperBoundType is null");
        for (final var value : values()) {
            if (value.lowerBoundCharacter == lowerBoundType.getLowerCharacter()
                && value.upperBoundCharacter == upperBoundType.getUpperCharacter()) {
                return value;
            }
        }
        throw new AssertionError("no constant for " + lowerBoundType + " and " + upperBoundType);
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    __RangeBounds(final char lowerBoundCharacter, final char upperBoundCharacter) {
        this.lowerBoundCharacter = lowerBoundCharacter;
        this.upperBoundCharacter = upperBoundCharacter;
        this.notation = new String(new char[] {lowerBoundCharacter, upperBoundCharacter});
    }

    // -------------------------------------------------------------------------------------------------------- NOTATION

    /**
     * Returns the two-character notation of this pair of bounds.
     *
     * @return one of {@code "[]"}, {@code "[)"}, {@code "(]"} or {@code "()"}.
     */
    public String getNotation() {
        return notation;
    }

    /**
     * Returns the character standing for the lower bound of this pair.
     *
     * @return {@code '['} when the lower endpoint is included; {@code '('} when it is not.
     */
    public char getLowerBoundCharacter() {
        return lowerBoundCharacter;
    }

    /**
     * Returns the character standing for the upper bound of this pair.
     *
     * @return {@code ']'} when the upper endpoint is included; {@code ')'} when it is not.
     */
    public char getUpperBoundCharacter() {
        return upperBoundCharacter;
    }

    // -----------------------------------------------------------------------------------------------------------------

    private final char lowerBoundCharacter;

    private final char upperBoundCharacter;

    private final String notation;
}
