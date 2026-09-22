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

import com.github.jinahya.persistence.more.temporalinterval.___DiscreteInterval;
import com.github.jinahya.persistence.more.temporalinterval.___MappedTemporalInterval;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * An abstract base class for testing {@link ___DiscreteInterval} implementations.
 * <p>
 * Beyond what {@link ___MappedTemporalInterval_Test} checks of every interval, this class checks the closed form which
 * only a discrete axis can answer exactly — the last point the interval actually contains.
 *
 * @param <I> the mapped superclass under test
 * @param <P> its point type
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ___MappedTemporalInterval_Test
 */
@SuppressWarnings({
        "java:S100", // Method names should comply with a naming convention
        "java:S101"  // Class names should comply with a naming convention
})
public abstract class ___DiscreteInterval_Test<
        I extends ___MappedTemporalInterval<P> & ___DiscreteInterval<P>,
        P extends Temporal & Comparable<? super P>
        >
        extends ___MappedTemporalInterval_Test<I, P> {

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance for testing the specified interval class with the specified sample points.
     *
     * @param intervalClass the interval class to test.
     * @param earlier       a point.
     * @param later         a point strictly after the {@code earlier} one.
     * @param unit          a unit the point type supports.
     * @param length        the number of complete {@code unit}s from {@code earlier} to {@code later}.
     */
    protected ___DiscreteInterval_Test(final Class<I> intervalClass, final P earlier, final P later,
                                       final TemporalUnit unit, final long length) {
        super(intervalClass, earlier, later, unit, length);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Verifies that the granularity is a property of the point type rather than of an instance.
     */
    @DisplayName("the granularity is a constant, and the same constant every time")
    @Test
    protected void _Constant_Granularity() {
        final var instance = newIntervalInstance();
        final var granularity = instance.getGranularity();
        assertNotNull(granularity, () -> "null granularity of an instance of " + intervalClass);
        assertSame(granularity, instance.getGranularity(), "the granularity varies between calls");
        assertSame(granularity, newInterval(earlier, later).getGranularity(),
                   "the granularity varies between instances");
    }

    /**
     * Verifies that an interval with no upper bound has no last point.
     */
    @DisplayName("an interval with no upper bound has no last point")
    @Test
    protected void _Null_EndInclusiveWithAbsentEnd() {
        assertNull(newInterval(earlier, null).getIntervalEndInclusive());
        assertNull(newInterval(null, null).getIntervalEndInclusive());
    }

    /**
     * Verifies that an empty interval has no last point, since it contains none.
     */
    @DisplayName("an empty interval has no last point")
    @Test
    protected void _Null_EndInclusiveOfEmpty() {
        assertNull(newInterval(earlier, earlier).getIntervalEndInclusive());
    }

    /**
     * Verifies that the last point is one step before the exclusive end.
     */
    @DisplayName("the last point is one granularity before the exclusive end")
    @Test
    protected void _OneStepBack_EndInclusiveOfBounded() {
        final var instance = newInterval(earlier, later);
        final var granularity = instance.getGranularity();
        assertEquals(
                later.minus(1L, granularity),
                instance.getIntervalEndInclusive(),
                () -> "the last point of the interval, stepping back by one " + granularity
        );
    }

    /**
     * Verifies that the last point is one the interval actually contains.
     *
     * @implNote This is what the closed form is for, and the two methods deciding it separately — one stepping
     *         back from the end, the other comparing against it — is what makes the agreement worth asserting.
     */
    @DisplayName("the last point is one the interval contains")
    @Test
    protected void _Contains_EndInclusive() {
        final var instance = newInterval(earlier, later);
        final var endInclusive = instance.getIntervalEndInclusive();
        assertNotNull(endInclusive, "a bounded, non-empty interval has no last point");
        assertTrue(instance.contains(endInclusive), "the interval does not contain its own last point");
    }
}
