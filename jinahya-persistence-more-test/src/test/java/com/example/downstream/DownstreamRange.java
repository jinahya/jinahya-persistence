package com.example.downstream;

import com.github.jinahya.persistence.more.orderedrange.__MappedOrderedRange;

/** A range a downstream maps, supplying the codec the mapped superclass leaves abstract. */
public class DownstreamRange extends __MappedOrderedRange<Long> {

    private static final long BIAS = -(long) Long.MIN_VALUE;

    @Override
    protected String encode(final Long value) {
        // unsigned-shifted and zero-padded, so the text is fixed width and sorts as the numbers do
        return String.format("%020d", Long.toUnsignedString(value + BIAS).length() > 0
                ? new java.math.BigInteger(Long.toUnsignedString(value + BIAS))
                : java.math.BigInteger.ZERO);
    }

    @Override
    protected Long decode(final String encoded) {
        return new java.math.BigInteger(encoded).longValue() - BIAS;
    }
}
