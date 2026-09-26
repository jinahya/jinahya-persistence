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

/**
 * Whether one end of a range includes the endpoint it names.
 * <p>
 * This is the per-end primitive. {@link __RangeBounds} is the pair of them, and is what a range as a whole is described
 * by; this is what a single end carries when the two ends are recorded independently, as they are in
 * {@link __MappedOrderedRange}.
 *
 * <h2>One constant, two characters</h2>
 * A closed lower bound is written {@code [} and a closed upper bound {@code ]}; the same constant, a different
 * character, because the bracket points into the range. {@link #getLowerCharacter()} and {@link #getUpperCharacter()}
 * give the two, and which one applies is decided by the end, not by this constant.
 *
 * <h2>An absent endpoint has no bound type</h2>
 * Where a range has no endpoint on one side there is nothing to include or to exclude, so no constant here applies —
 * {@link __MappedOrderedRange} leaves that column {@code NULL} and the question does not arise. The unbounded-and-
 * inclusive combination which PostgreSQL has to normalize away cannot be written at all.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __RangeBounds
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public enum __BoundType {

    /**
     * The endpoint belongs to the range: {@code [} as a lower bound, {@code ]} as an upper bound.
     */
    CLOSED('[', ']'),

    /**
     * The endpoint does not belong to the range: {@code (} as a lower bound, {@code )} as an upper bound.
     */
    OPEN('(', ')');

    // ----------------------------------------------------------------------------------------------- STATIC FACTORIES

    /**
     * Returns the constant whose {@link #getLowerCharacter() lower character} is the specified character.
     *
     * @param character the character to look up; {@code '['} or {@code '('}.
     * @return the constant for the {@code character}.
     * @throws IllegalArgumentException if no constant has that lower character.
     */
    public static __BoundType ofLowerCharacter(final char character) {
        for (final var value : values()) {
            if (value.lowerCharacter == character) {
                return value;
            }
        }
        throw new IllegalArgumentException("not a lower bound character: '" + character + "'");
    }

    /**
     * Returns the constant whose {@link #getUpperCharacter() upper character} is the specified character.
     *
     * @param character the character to look up; {@code ']'} or {@code ')'}.
     * @return the constant for the {@code character}.
     * @throws IllegalArgumentException if no constant has that upper character.
     */
    public static __BoundType ofUpperCharacter(final char character) {
        for (final var value : values()) {
            if (value.upperCharacter == character) {
                return value;
            }
        }
        throw new IllegalArgumentException("not an upper bound character: '" + character + "'");
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    __BoundType(final char lowerCharacter, final char upperCharacter) {
        this.lowerCharacter = lowerCharacter;
        this.upperCharacter = upperCharacter;
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns the character standing for this bound type at the lower end of a range.
     *
     * @return {@code '['} for {@link #CLOSED}; {@code '('} for {@link #OPEN}.
     */
    public char getLowerCharacter() {
        return lowerCharacter;
    }

    /**
     * Returns the character standing for this bound type at the upper end of a range.
     *
     * @return {@code ']'} for {@link #CLOSED}; {@code ')'} for {@link #OPEN}.
     */
    public char getUpperCharacter() {
        return upperCharacter;
    }

    // -----------------------------------------------------------------------------------------------------------------

    private final char lowerCharacter;

    private final char upperCharacter;
}
