package com.example.downstream;

import com.github.jinahya.persistence.more.converter.test.__AttributeEnumConverter_Test;

/**
 * Extends the published enum-converter base from outside its package. This one carries its assertions in
 * {@code @Nested} classes rather than in plain methods, and whether those are discovered through a superclass in
 * another package is a separate question from whether inherited methods are.
 */
class DownstreamStatusConverter_Test
        extends __AttributeEnumConverter_Test.__OfStringTest<DownstreamStatusConverter, DownstreamStatus> {

    DownstreamStatusConverter_Test() {
        super(DownstreamStatusConverter.class, DownstreamStatus.class);
    }
}
