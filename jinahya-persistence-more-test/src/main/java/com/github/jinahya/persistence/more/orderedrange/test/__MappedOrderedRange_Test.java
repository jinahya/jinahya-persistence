package com.github.jinahya.persistence.more.orderedrange.test;

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

import com.github.jinahya.persistence.more.orderedrange.__BoundType;
import com.github.jinahya.persistence.more.orderedrange.__MappedOrderedRange;
import com.github.jinahya.persistence.more.orderedrange.__RangeBounds;
import com.github.jinahya.persistence.more.test.___Utils;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.abort;

/**
 * An abstract base class for testing {@link __MappedOrderedRange} implementations.
 * <p>
 * Two different things are checked here. The first is the range itself — its two ends, their bound types, and what it
 * contains — which is settled by the class under test and needs only sample endpoints to exercise.
 * <p>
 * The second is the <strong>encoding contract</strong>, and it is the reason this class is worth extending. A subclass
 * of {@link __MappedOrderedRange} supplies {@code encode}/{@code decode}, and those two are required to be mutually
 * inverse, to produce non-empty text, to be prefix-free, and to preserve the natural order of the endpoint type. The
 * class documentation is explicit that the contract is <em>"relied on rather than checked"</em>: nothing at runtime
 * verifies any of the four, and the failures they produce are quiet ones — a column which sorts correctly under every
 * query while
 * {@link com.github.jinahya.persistence.more.orderedrange.___OrderedRange#contains(Comparable) contains(value)}
 * answers differently about the same range. Checking it is exactly a test's job, and this is where it gets done.
 * <p>
 * Those four assertions need the encoding itself, which {@link __MappedOrderedRange} keeps {@code protected}. A
 * subclass of this class reaches it in one line, because a test for an entity conventionally sits in that entity's own
 * package:
 * {@snippet lang = "java":
 * protected String encode(final MyRange instance, final BigDecimal value) {
 *     return instance.encode(value); // an @Override, omitted here: javadoc reads a leading @ as a block tag
 * }
 *}
 * Left alone, those four assertions are
 * {@linkplain org.junit.jupiter.api.Assumptions#abort(String) skipped} rather than passed.
 *
 * @param <R> the range class under test
 * @param <C> its endpoint type
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S100", // Method names should comply with a naming convention
        "java:S101"  // Class names should comply with a naming convention
})
public abstract class __MappedOrderedRange_Test<
        R extends __MappedOrderedRange<C>,
        C extends Comparable<? super C>
        > {

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance for testing the specified range class with the specified sample endpoints.
     *
     * @param rangeClass the range class to test.
     * @param samples    at least two sample endpoints, in strictly ascending order; the more of them, and the more
     *                   they differ in length and in shape, the more the encoding contract is actually exercised.
     */
    protected __MappedOrderedRange_Test(final Class<R> rangeClass, final List<C> samples) {
        super();
        this.rangeClass = Objects.requireNonNull(rangeClass, "rangeClass is null");
        this.samples = List.copyOf(Objects.requireNonNull(samples, "samples is null"));
        if (this.samples.size() < 2) {
            throw new IllegalArgumentException("not enough samples to order: " + this.samples);
        }
    }

    // ------------------------------------------------------------------------------------------------------ endpoints

    /**
     * Verifies that the samples this test was given really are in strictly ascending order.
     *
     * @implNote Everything below reads the samples as ordered, and the encoding contract is decided against that
     *           order, so a mis-ordered sample list would report a failure of the class under test which is in fact a
     *           failure of the test.
     */
    @DisplayName("the sample endpoints are in strictly ascending order")
    @Test
    protected void _Ascending_Samples() {
        for (var i = 1; i < samples.size(); i++) {
            final var previous = samples.get(i - 1);
            final var current = samples.get(i);
            assertTrue(
                    previous.compareTo(current) < 0,
                    () -> "the sample endpoints are not in strictly ascending order: " + previous + ", " + current
            );
        }
    }

    /**
     * Verifies that a new instance holds neither endpoint, and reports no bound type for either end.
     */
    @DisplayName("a new instance holds neither endpoint")
    @Test
    protected void _BothAbsent_NewInstance() {
        final var instance = newRangeInstance();
        assertNull(instance.getRangeLower());
        assertNull(instance.getRangeUpper());
        assertNull(instance.getLowerBoundType(), "a bound type without an endpoint to apply to");
        assertNull(instance.getUpperBoundType(), "a bound type without an endpoint to apply to");
        assertFalse(instance.isBounded());
        assertFalse(instance.isEmpty());
    }

    /**
     * Verifies that each endpoint is returned as it was set, under either bound type.
     *
     * @implNote This is the encoding round-trip, seen from outside: each value goes through {@code encode} on the
     *         way in and {@code decode} on the way back, and an encoding which is not its own inverse fails here
     *         whether or not the subclass exposes the codec.
     */
    @DisplayName("each endpoint is returned as it was set, under either bound type")
    @Test
    protected void _RoundTrip_SetThenGet() {
        final var instance = newRangeInstance();
        for (final C sample : samples) {
            for (final __BoundType boundType : __BoundType.values()) {
                instance.setRangeLower(sample, boundType);
                assertEquals(sample, instance.getRangeLower(),
                             () -> "the lower endpoint " + sample + " did not survive the encoding");
                assertEquals(boundType, instance.getLowerBoundType(),
                             () -> "the lower bound type of " + sample + " did not survive the encoding");
                instance.setRangeUpper(sample, boundType);
                assertEquals(sample, instance.getRangeUpper(),
                             () -> "the upper endpoint " + sample + " did not survive the encoding");
                assertEquals(boundType, instance.getUpperBoundType(),
                             () -> "the upper bound type of " + sample + " did not survive the encoding");
            }
        }
    }

    /**
     * Verifies that a {@code null} endpoint clears its end, bound type included.
     */
    @DisplayName("a null endpoint clears its end, bound type included")
    @Test
    protected void _Null_ClearsEnd() {
        final var instance = newRange(samples.get(0), __BoundType.CLOSED, samples.get(1), __BoundType.OPEN);
        instance.setRangeLower(null);
        assertNull(instance.getRangeLower());
        assertNull(instance.getLowerBoundType());
        instance.setRangeUpper(null);
        assertNull(instance.getRangeUpper());
        assertNull(instance.getUpperBoundType());
    }

    /**
     * Verifies that the one-argument setters write the half-open convention the class documents.
     */
    @DisplayName("the one-argument setters write a closed lower end and an open upper end")
    @Test
    protected void _ClosedAndOpen_DefaultBoundTypes() {
        final var instance = newRangeInstance();
        instance.setRangeLower(samples.get(0));
        instance.setRangeUpper(samples.get(1));
        assertEquals(__BoundType.CLOSED, instance.getLowerBoundType());
        assertEquals(__BoundType.OPEN, instance.getUpperBoundType());
    }

    /**
     * Verifies that a {@code null} bound type is refused, whatever the endpoint is.
     */
    // the null is the point of the assertion: these methods are specified to refuse one, and NullAway cannot tell
    // a deliberate null argument from an accidental one
    @SuppressWarnings("NullAway")
    @DisplayName("a null bound type is refused, whatever the endpoint is")
    @Test
    protected void _NullPointerException_NullBoundType() {
        final var instance = newRangeInstance();
        assertThrows(NullPointerException.class, () -> instance.setRangeLower(samples.get(0), null));
        assertThrows(NullPointerException.class, () -> instance.setRangeUpper(samples.get(0), null));
        assertThrows(NullPointerException.class, () -> instance.setRangeLower(null, null));
        assertThrows(NullPointerException.class, () -> instance.setRangeUpper(null, null));
    }

    // -------------------------------------------------------------------------------------------- bounded / empty

    /**
     * Verifies that a range is bounded exactly when it holds both endpoints.
     */
    @DisplayName("a range is bounded only when it holds both endpoints")
    @Test
    protected void _Bounded_BothEndpointsPresent() {
        final var lower = samples.get(0);
        final var upper = samples.get(1);
        assertTrue(newRange(lower, __BoundType.CLOSED, upper, __BoundType.OPEN).isBounded());
        assertFalse(newRange(null, __BoundType.CLOSED, upper, __BoundType.OPEN).isBounded());
        assertFalse(newRange(lower, __BoundType.CLOSED, null, __BoundType.OPEN).isBounded());
        assertFalse(newRange(null, __BoundType.CLOSED, null, __BoundType.OPEN).isBounded());
    }

    /**
     * Verifies that equal endpoints leave the range empty only when at least one end is open.
     *
     * @implNote Equal endpoints mean different things across the pairs: {@code [a,a]} holds exactly one value,
     *         where {@code [a,a)} and {@code (a,a]} hold none. A range which answered alike for all three would be
     *         wrong about a shape which occurs in real data.
     */
    @DisplayName("equal endpoints are empty only when an end is open")
    @Test
    protected void _Empty_EqualEndpointsWithAnOpenEnd() {
        final var value = samples.get(0);
        assertTrue(newRange(value, __BoundType.CLOSED, value, __BoundType.OPEN).isEmpty(), "[a,a) is not empty");
        assertTrue(newRange(value, __BoundType.OPEN, value, __BoundType.CLOSED).isEmpty(), "(a,a] is not empty");
        assertFalse(newRange(value, __BoundType.CLOSED, value, __BoundType.CLOSED).isEmpty(),
                    "[a,a] is empty, though it holds exactly one value");
        assertFalse(newRange(samples.get(0), __BoundType.CLOSED, samples.get(1), __BoundType.OPEN).isEmpty());
    }

    // ------------------------------------------------------------------------------------------------------ contains

    /**
     * Verifies that an endpoint belongs to the range exactly when its end is closed.
     */
    @DisplayName("an endpoint belongs to the range exactly when its end is closed")
    @Test
    protected void _RespectsBoundTypes_Contains() {
        final var lower = samples.get(0);
        final var upper = samples.get(1);
        final var closedOpen = newRange(lower, __BoundType.CLOSED, upper, __BoundType.OPEN);
        assertTrue(closedOpen.contains(lower));
        assertFalse(closedOpen.contains(upper));
        final var openClosed = newRange(lower, __BoundType.OPEN, upper, __BoundType.CLOSED);
        assertFalse(openClosed.contains(lower));
        assertTrue(openClosed.contains(upper));
        final var closedClosed = newRange(lower, __BoundType.CLOSED, upper, __BoundType.CLOSED);
        assertTrue(closedClosed.contains(lower));
        assertTrue(closedClosed.contains(upper));
        final var openOpen = newRange(lower, __BoundType.OPEN, upper, __BoundType.OPEN);
        assertFalse(openOpen.contains(lower));
        assertFalse(openOpen.contains(upper));
    }

    /**
     * Verifies that an absent end holds, so a range with neither endpoint contains every value.
     */
    @DisplayName("an absent end holds, so an unbounded range contains everything")
    @Test
    protected void _Holds_ContainsWithAbsentEnd() {
        final var lower = samples.get(0);
        final var upper = samples.get(1);
        final var unbounded = newRangeInstance();
        assertTrue(unbounded.contains(lower));
        assertTrue(unbounded.contains(upper));
        final var noLower = newRange(null, __BoundType.CLOSED, upper, __BoundType.OPEN);
        assertTrue(noLower.contains(lower));
        assertFalse(noLower.contains(upper));
        final var noUpper = newRange(lower, __BoundType.CLOSED, null, __BoundType.OPEN);
        assertTrue(noUpper.contains(lower));
        assertTrue(noUpper.contains(upper));
    }

    /**
     * Verifies that a {@code null} value is refused rather than answered.
     */
    // the null is the point of the assertion: these methods are specified to refuse one, and NullAway cannot tell
    // a deliberate null argument from an accidental one
    @SuppressWarnings("NullAway")
    @DisplayName("a null value is refused rather than answered")
    @Test
    protected void _NullPointerException_ContainsNull() {
        final var instance = newRange(samples.get(0), __BoundType.CLOSED, samples.get(1), __BoundType.OPEN);
        assertThrows(NullPointerException.class, () -> instance.contains(null));
    }

    /**
     * Verifies that the pair of bound types reports each end, contributing an open end for an absent endpoint.
     */
    @DisplayName("the range bounds pair the two ends, an absent one contributing open")
    @Test
    protected void _Paired_RangeBounds() {
        final var lower = samples.get(0);
        final var upper = samples.get(1);
        assertEquals(
                __RangeBounds.of(__BoundType.CLOSED, __BoundType.OPEN),
                newRange(lower, __BoundType.CLOSED, upper, __BoundType.OPEN).getRangeBounds()
        );
        assertEquals(
                __RangeBounds.of(__BoundType.OPEN, __BoundType.OPEN),
                newRangeInstance().getRangeBounds(),
                "a range with neither endpoint is not open at both ends"
        );
        assertEquals(
                __RangeBounds.of(__BoundType.OPEN, __BoundType.CLOSED),
                newRange(null, __BoundType.CLOSED, upper, __BoundType.CLOSED).getRangeBounds(),
                "an absent lower end does not contribute open"
        );
    }

    // ----------------------------------------------------------------------------------------- the encoding contract

    /**
     * Verifies that {@code decode} is the inverse of {@code encode}, read through the codec itself.
     *
     * @see #encode(__MappedOrderedRange, Comparable)
     * @see #decode(__MappedOrderedRange, String)
     */
    @DisplayName("the encoding round-trips: decode(encode(value)) equals value")
    @Test
    protected void _RoundTrip_EncodeDecode() {
        final var instance = newRangeInstance();
        for (final C sample : samples) {
            final var encoded = encodeOrAbort(instance, sample);
            final var decoded = decode(instance, encoded);
            if (decoded == null) {
                abort("the codec contract is checked only when " + getClass().getSimpleName()
                      + " overrides decode(R, String)");
            }
            assertEquals(sample, decoded, () -> "decode(encode(" + sample + ")) is not the value it started as");
        }
    }

    /**
     * Verifies that no endpoint encodes to empty text.
     *
     * @implNote A stored cut is the encoded endpoint followed by its bound character, so empty text would leave the
     *         column holding the marker alone — which the class rejects when it reads the cut back, at which point the
     *         row is already written.
     */
    @DisplayName("no endpoint encodes to empty text")
    @Test
    protected void _NonEmpty_Encode() {
        final var instance = newRangeInstance();
        for (final C sample : samples) {
            final var encoded = encodeOrAbort(instance, sample);
            assertFalse(encoded.isEmpty(), () -> "the encoding of " + sample + " is empty");
        }
    }

    /**
     * Verifies that no encoded endpoint is a proper prefix of another.
     *
     * @implNote Prefix-freedom is what keeps the bound character unambiguous and the column's own ordering
     *         faithful: where one encoding is a prefix of another, the shorter cut sorts by its trailing bound
     *         character rather than by the endpoint, and two rows compare by something which is not the data. A
     *         fixed-width encoding gets this for free, which is why the class documentation advises one.
     */
    @DisplayName("no encoded endpoint is a proper prefix of another")
    @Test
    protected void _PrefixFree_Encode() {
        final var encoded = encodeAllOrAbort();
        for (var i = 0; i < encoded.size(); i++) {
            for (var j = 0; j < encoded.size(); j++) {
                if (i == j) {
                    continue;
                }
                final var shorter = i;
                final var longer = j;
                final var one = encoded.get(shorter);
                final var other = encoded.get(longer);
                assertFalse(
                        other.startsWith(one) && other.length() > one.length(),
                        () -> "the encoding of " + samples.get(shorter) + " (" + one
                              + ") is a proper prefix of that of " + samples.get(longer) + " (" + other + ')'
                );
            }
        }
    }

    /**
     * Verifies that the encoding sorts the way the endpoint type does.
     *
     * @implNote This is the quiet one. An encoding which sorts by something other than {@code C}'s natural order
     *         leaves every index and every query looking correct while
     *         {@link com.github.jinahya.persistence.more.orderedrange.___OrderedRange#contains(Comparable) contains}
     *         and {@link com.github.jinahya.persistence.more.orderedrange.___OrderedRange#isEmpty() isEmpty} — which
     *         answer by {@code compareTo} — disagree with the database about the same range. Where the wanted order
     *         is not the natural one, the endpoint type is what needs changing, not the encoding.
     */
    @DisplayName("the encoding preserves the natural order of the endpoint type")
    @Test
    protected void _OrderPreserving_Encode() {
        final var encoded = encodeAllOrAbort();
        for (var i = 1; i < encoded.size(); i++) {
            final var previous = encoded.get(i - 1);
            final var current = encoded.get(i);
            final var index = i;
            assertTrue(
                    previous.compareTo(current) < 0,
                    () -> "the encoding does not preserve the order of the endpoints: " + samples.get(index - 1)
                          + " encodes to " + previous + ", which does not sort before " + current + ", the encoding of "
                          + samples.get(index)
            );
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns the text the specified range class encodes the specified endpoint as.
     *
     * @param instance an instance of {@link #rangeClass}, on which the codec is invoked.
     * @param value    the endpoint to encode.
     * @return the encoded text; {@code null} for a subclass which does not expose the codec, which leaves the four
     *         assertions above skipped.
     * @implSpec The default implementation returns {@code null}. A subclass overrides it by delegating to the
     *           {@code protected} method on the range class, which it can reach when it sits in that class's package.
     */
    protected @Nullable String encode(final R instance, final C value) {
        return null;
    }

    /**
     * Returns the endpoint the specified range class decodes the specified text back to.
     *
     * @param instance an instance of {@link #rangeClass}, on which the codec is invoked.
     * @param encoded  the text to decode, as {@link #encode(__MappedOrderedRange, Comparable)} produced it.
     * @return the decoded endpoint; {@code null} for a subclass which does not expose the codec.
     * @implSpec As {@link #encode(__MappedOrderedRange, Comparable)}.
     */
    protected @Nullable C decode(final R instance, final String encoded) {
        return null;
    }

    /**
     * Encodes the specified endpoint, aborting the calling test when the codec is not exposed.
     *
     * @param instance an instance of {@link #rangeClass}.
     * @param value    the endpoint to encode.
     * @return the encoded text; never {@code null}.
     */
    private String encodeOrAbort(final R instance, final C value) {
        final var encoded = encode(instance, value);
        if (encoded == null) {
            return abort("the encoding contract is checked only when " + getClass().getSimpleName()
                         + " overrides encode(R, C)");
        }
        return encoded;
    }

    /**
     * Encodes every sample endpoint, in order, aborting the calling test when the codec is not exposed.
     *
     * @return the encoded texts, in the order of {@link #samples}.
     */
    private List<String> encodeAllOrAbort() {
        final var instance = newRangeInstance();
        final var encoded = new ArrayList<String>(samples.size());
        for (final C sample : samples) {
            encoded.add(encodeOrAbort(instance, sample));
        }
        return encoded;
    }

    /**
     * Creates a new instance of {@link #rangeClass} holding the specified ends.
     *
     * @param lower         the lower endpoint; {@code null} for no lower bound.
     * @param lowerBoundType whether the {@code lower} endpoint belongs to the range.
     * @param upper         the upper endpoint; {@code null} for no upper bound.
     * @param upperBoundType whether the {@code upper} endpoint belongs to the range.
     * @return a new instance holding the two ends.
     */
    protected final R newRange(final @Nullable C lower, final __BoundType lowerBoundType,
                               final @Nullable C upper, final __BoundType upperBoundType) {
        final var instance = newRangeInstance();
        instance.setRangeLower(lower, lowerBoundType);
        instance.setRangeUpper(upper, upperBoundType);
        return instance;
    }

    /**
     * Creates a new instance of {@link #rangeClass}.
     *
     * @return a new instance of {@link #rangeClass}, holding neither endpoint.
     * @implSpec The default implementation invokes the no-argument constructor which Jakarta Persistence requires
     *           every entity to declare.
     */
    protected R newRangeInstance() {
        return ___Utils.newInstance(rangeClass);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The range class to test.
     */
    protected final Class<R> rangeClass;

    /**
     * The sample endpoints, in strictly ascending order.
     */
    protected final List<C> samples;
}
