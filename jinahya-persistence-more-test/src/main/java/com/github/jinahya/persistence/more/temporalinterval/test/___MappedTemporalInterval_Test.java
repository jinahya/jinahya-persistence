package com.github.jinahya.persistence.more.temporalinterval.test;

/*-
 * #%L
 * jinahya-persistence-more-test
 * %%
 * Copyright (C) 2024 - 2025 Jinahya, Inc.
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

import com.github.jinahya.persistence.more.temporalinterval.___MappedTemporalInterval;
import com.github.jinahya.persistence.more.test.___Utils;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * An abstract base class for testing {@link ___MappedTemporalInterval} implementations.
 * <p>
 * What an interval is expected to do does not vary with the type of its points, so it is written once here and a
 * subclass supplies only the two things which do vary: a concrete class, and two ordered sample points to build
 * intervals from.
 * <p>
 * A model which decides order by something other than the natural one —
 * {@link com.github.jinahya.persistence.more.temporalinterval.__MappedOffsetDateTimeInterval}, which compares instants
 * — is covered by the same assertions, because every one of them is phrased in terms of the two sample points rather
 * than of any particular comparison.
 *
 * @param <I> the mapped superclass under test
 * @param <P> its point type
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ___DiscreteInterval_Test
 */
@SuppressWarnings({
        "java:S100", // Method names should comply with a naming convention
        "java:S101"  // Class names should comply with a naming convention
})
public abstract class ___MappedTemporalInterval_Test<
        I extends ___MappedTemporalInterval<P>,
        P extends Temporal & Comparable<? super P>
        > {

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance for testing the specified interval class with the specified sample points.
     *
     * @param intervalClass the interval class to test.
     * @param earlier       a point.
     * @param later         a point strictly after the {@code earlier} one, by whatever order the
     *                      {@code intervalClass} decides it.
     * @param unit          a unit the point type supports.
     * @param length        the number of complete {@code unit}s from {@code earlier} to {@code later}.
     */
    protected ___MappedTemporalInterval_Test(final Class<I> intervalClass, final P earlier, final P later,
                                             final TemporalUnit unit, final long length) {
        super();
        this.intervalClass = Objects.requireNonNull(intervalClass, "intervalClass is null");
        this.earlier = Objects.requireNonNull(earlier, "earlier is null");
        this.later = Objects.requireNonNull(later, "later is null");
        this.unit = Objects.requireNonNull(unit, "unit is null");
        this.length = length;
    }

    // ---------------------------------------------------------------------------------------------------- the points

    /**
     * Verifies that a new instance holds neither point.
     */
    @DisplayName("a new instance holds neither point")
    @Test
    protected void _BothAbsent_NewInstance() {
        final var instance = newIntervalInstance();
        assertNull(instance.getIntervalStart(), () -> "a new instance of " + intervalClass + " holds a start");
        assertNull(instance.getIntervalEnd(), () -> "a new instance of " + intervalClass + " holds an end");
    }

    /**
     * Verifies that each point is returned as it was set, {@code null} included.
     */
    @DisplayName("each point is returned as it was set, including back to null")
    @Test
    protected void _RoundTrip_SetThenGet() {
        final var instance = newInterval(earlier, later);
        assertEquals(earlier, instance.getIntervalStart());
        assertEquals(later, instance.getIntervalEnd());
        instance.setIntervalStart(null);
        instance.setIntervalEnd(null);
        assertNull(instance.getIntervalStart());
        assertNull(instance.getIntervalEnd());
    }

    /**
     * Verifies that a start after its end is stored rather than rejected.
     *
     * @implNote The order of the two points is documented, not validated: nothing in the package refuses an
     *         inverted interval, and a test which expected an exception here would be testing a rule which was never
     *         made.
     */
    @DisplayName("a start after its end is stored: the order is documented, not validated")
    @Test
    protected void _Accepted_Inverted() {
        assertDoesNotThrow(() -> {
            final var instance = newInterval(later, earlier);
            assertEquals(later, instance.getIntervalStart());
            assertEquals(earlier, instance.getIntervalEnd());
        });
    }

    // --------------------------------------------------------------------------------------------- bounded / empty

    /**
     * Verifies that an interval is bounded exactly when it holds both points.
     */
    @DisplayName("an interval is bounded only when it holds both points")
    @Test
    protected void _Bounded_BothPointsPresent() {
        assertTrue(newInterval(earlier, later).isBounded());
        assertFalse(newInterval(null, later).isBounded());
        assertFalse(newInterval(earlier, null).isBounded());
        assertFalse(newInterval(null, null).isBounded());
    }

    /**
     * Verifies that an interval is empty exactly when its two points coincide.
     */
    @DisplayName("an interval is empty exactly when its two points coincide")
    @Test
    protected void _Empty_PointsCoincide() {
        assertTrue(newInterval(earlier, earlier).isEmpty(), "an interval of one point twice is not empty");
        assertFalse(newInterval(earlier, later).isEmpty());
        assertFalse(newInterval(null, later).isEmpty());
        assertFalse(newInterval(earlier, null).isEmpty());
        assertFalse(newInterval(null, null).isEmpty());
    }

    // ------------------------------------------------------------------------------------------------------ contains

    /**
     * Verifies that the start belongs to the interval and the end does not.
     */
    @DisplayName("the start belongs to the interval and the end does not")
    @Test
    protected void _HalfOpen_Contains() {
        final var instance = newInterval(earlier, later);
        assertTrue(instance.contains(earlier), "the start does not belong to the interval");
        assertFalse(instance.contains(later), "the end belongs to the interval");
    }

    /**
     * Verifies that an absent bound holds, so an unbounded interval contains everything.
     */
    @DisplayName("an absent bound holds, so an unbounded interval contains everything")
    @Test
    protected void _Holds_ContainsWithAbsentBound() {
        final var unbounded = newInterval(null, null);
        assertTrue(unbounded.contains(earlier));
        assertTrue(unbounded.contains(later));
        final var noStart = newInterval(null, later);
        assertTrue(noStart.contains(earlier));
        assertFalse(noStart.contains(later));
        final var noEnd = newInterval(earlier, null);
        assertTrue(noEnd.contains(earlier));
        assertTrue(noEnd.contains(later));
    }

    /**
     * Verifies that an empty interval contains no point at all.
     */
    @DisplayName("an empty interval contains no point at all")
    @Test
    protected void _ContainsNothing_Empty() {
        final var instance = newInterval(earlier, earlier);
        assertFalse(instance.contains(earlier));
        assertFalse(instance.contains(later));
    }

    /**
     * Verifies that a {@code null} point is refused rather than answered.
     */
    // the null is the point of the assertion: these methods are specified to refuse one, and NullAway cannot tell
    // a deliberate null argument from an accidental one
    @SuppressWarnings("NullAway")
    @DisplayName("a null point is refused rather than answered")
    @Test
    protected void _NullPointerException_ContainsNull() {
        final var instance = newInterval(earlier, later);
        assertThrows(NullPointerException.class, () -> instance.contains(null));
    }

    // -------------------------------------------------------------------------------------------- amount / length

    /**
     * Verifies that the amount is absent unless both points are present.
     */
    @DisplayName("the amount is absent unless both points are present")
    @Test
    protected void _Null_TemporalAmountWithAbsentBound() {
        assertNull(newInterval(null, later).getTemporalAmount());
        assertNull(newInterval(earlier, null).getTemporalAmount());
        assertNull(newInterval(null, null).getTemporalAmount());
    }

    /**
     * Verifies that a bounded interval measures something.
     */
    @DisplayName("a bounded interval measures something")
    @Test
    protected void _NotNull_TemporalAmountOfBounded() {
        assertNotNull(newInterval(earlier, later).getTemporalAmount());
    }

    /**
     * Verifies that an empty interval measures zero in every unit its amount carries.
     */
    @DisplayName("an empty interval measures zero in every unit its amount carries")
    @Test
    protected void _Zero_TemporalAmountOfEmpty() {
        final TemporalAmount amount = newInterval(earlier, earlier).getTemporalAmount();
        assertNotNull(amount, "an empty interval measures nothing at all");
        for (final TemporalUnit u : amount.getUnits()) {
            assertEquals(0L, amount.get(u), () -> "an empty interval measures a non-zero amount of " + u);
        }
    }

    /**
     * Verifies that the length is empty unless both points are present.
     */
    @DisplayName("the length is empty unless both points are present")
    @Test
    protected void _Empty_LengthInWithAbsentBound() {
        assertTrue(newInterval(null, later).lengthIn(unit).isEmpty());
        assertTrue(newInterval(earlier, null).lengthIn(unit).isEmpty());
        assertTrue(newInterval(null, null).lengthIn(unit).isEmpty());
    }

    /**
     * Verifies that the length counts whole units from the start to the end.
     */
    @DisplayName("the length counts whole units from the start to the end")
    @Test
    protected void _Counted_LengthInOfBounded() {
        final var actual = newInterval(earlier, later).lengthIn(unit);
        assertTrue(actual.isPresent(), "a bounded interval has no length");
        assertEquals(length, actual.getAsLong(), () -> "the length of the interval, counted in " + unit);
        assertEquals(0L, newInterval(earlier, earlier).lengthIn(unit).orElseThrow(),
                     "an empty interval has a non-zero length");
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance of {@link #intervalClass} holding the specified points.
     *
     * @param startInclusive the point at which the interval starts; {@code null} for no lower bound.
     * @param endExclusive   the point at which the interval ends; {@code null} for no upper bound.
     * @return a new instance holding the two points.
     */
    protected final I newInterval(final @Nullable P startInclusive, final @Nullable P endExclusive) {
        final var instance = newIntervalInstance();
        instance.setIntervalStart(startInclusive);
        instance.setIntervalEnd(endExclusive);
        return instance;
    }

    /**
     * Creates a new instance of {@link #intervalClass}.
     *
     * @return a new instance of {@link #intervalClass}, holding neither point.
     * @implSpec The default implementation invokes the no-argument constructor which Jakarta Persistence requires
     *           every entity to declare.
     */
    protected I newIntervalInstance() {
        return ___Utils.newInstance(intervalClass);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The interval class to test.
     */
    protected final Class<I> intervalClass;

    /**
     * A sample point.
     */
    protected final P earlier;

    /**
     * A sample point strictly after {@link #earlier}.
     */
    protected final P later;

    /**
     * A unit the point type supports.
     */
    protected final TemporalUnit unit;

    /**
     * The number of complete {@link #unit}s from {@link #earlier} to {@link #later}.
     */
    protected final long length;
}
