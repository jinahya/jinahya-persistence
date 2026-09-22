package com.example.downstream;

import com.github.jinahya.persistence.more.orderedrange.test.__MappedOrderedRange_Test;

import java.util.List;

/**
 * Extends the published range base from outside its package, as a consumer does — including the codec hook, whose
 * whole premise is that a consumer's test sits in its entity's package and can therefore reach a {@code protected}
 * method. This is that arrangement.
 */
class DownstreamRange_Test extends __MappedOrderedRange_Test<DownstreamRange, Long> {

    DownstreamRange_Test() {
        super(DownstreamRange.class, List.of(Long.MIN_VALUE, -1L, 0L, 1L, Long.MAX_VALUE));
    }

    @Override
    protected String encode(final DownstreamRange instance, final Long value) {
        return instance.encode(value);
    }

    @Override
    protected Long decode(final DownstreamRange instance, final String encoded) {
        return instance.decode(encoded);
    }
}
