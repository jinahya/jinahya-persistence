package com.github.jinahya.persistence.more.converter.test;

import com.github.jinahya.persistence.more.converter.javatime.InstantConverter;

import java.time.Instant;

import java.util.List;

/**
 * Tests {@link InstantConverter.OfString} against what every attribute converter is
 * expected to do.
 */
class InstantConverter_OfString_Test
        extends StringAttributeConverter_Test<InstantConverter.OfString, Instant> {

    InstantConverter_OfString_Test() {
        super(InstantConverter.OfString.class);
    }

    @Override
    protected List<__AttributeConverterTestCase<Instant, String>> testCases() {
        return List.of(
                __AttributeConverterTestCase.of(Instant.parse("2026-09-19T11:22:33.123456789Z"),
                                                Instant.parse("2026-09-19T11:22:33.123456789Z").toString())
        );
    }
}
