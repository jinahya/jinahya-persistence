package com.github.jinahya.persistence.more.orderedrange.test;

import java.util.List;

class _SampleIntegerRange_Test extends __MappedOrderedRange_Test<_SampleIntegerRange, Integer> {

    _SampleIntegerRange_Test() {
        // spread across zero and up to the extremes: a biased fixed-width encoding is exactly what those break
        super(_SampleIntegerRange.class, List.of(Integer.MIN_VALUE, -1000, -1, 0, 1, 9, 10, 1000, Integer.MAX_VALUE));
    }

    // the codec is protected on the range class, and this test sits in its package, so exposing it is one line
    @Override
    protected String encode(final _SampleIntegerRange instance, final Integer value) {
        return instance.encode(value);
    }

    @Override
    protected Integer decode(final _SampleIntegerRange instance, final String encoded) {
        return instance.decode(encoded);
    }
}
