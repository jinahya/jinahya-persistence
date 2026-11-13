package com.github.jinahya.persistence.more.orderedrange.test;

import com.github.jinahya.persistence.more.orderedrange.__MappedOrderedRange;

/**
 * A range sample over {@link Integer}, encoded fixed width so that the four parts of the encoding contract hold.
 * <p>
 * The bias is what makes it work across zero: shifting onto {@code [0, 2^32)} leaves every value ten digits long, so
 * the text is prefix-free and sorts exactly as the numbers do, which a plain {@code Integer.toString} would not —
 * {@code "-1"} sorts before {@code "0"} by accident and {@code "10"} before {@code "9"} against the numbers.
 */
class _SampleIntegerRange extends __MappedOrderedRange<Integer> {

    private static final long BIAS = -(long) Integer.MIN_VALUE;

    @Override
    protected String encode(final Integer value) {
        return String.format("%010d", value + BIAS);
    }

    @Override
    protected Integer decode(final String encoded) {
        return (int) (Long.parseLong(encoded) - BIAS);
    }
}
