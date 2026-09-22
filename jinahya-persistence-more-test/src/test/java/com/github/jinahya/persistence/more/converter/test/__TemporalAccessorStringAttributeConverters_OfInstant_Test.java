package com.github.jinahya.persistence.more.converter.test;

import com.github.jinahya.persistence.more.converter.__TemporalAccessorStringAttributeConverters;

import java.time.Instant;

import java.util.List;

/**
 * Tests {@link __TemporalAccessorStringAttributeConverters.OfInstant} against what every attribute converter is expected to do.
 */
class __TemporalAccessorStringAttributeConverters_OfInstant_Test
        extends __StringAttributeConverter_Test<__TemporalAccessorStringAttributeConverters.OfInstant, Instant> {

    __TemporalAccessorStringAttributeConverters_OfInstant_Test() {
        super(__TemporalAccessorStringAttributeConverters.OfInstant.class);
    }

    @Override
    protected List<__AttributeConverterTestCase<Instant, String>> testCases() {
        return List.of(
                __AttributeConverterTestCase.of(Instant.parse("2026-09-19T11:22:33.123456789Z"), Instant.parse("2026-09-19T11:22:33.123456789Z").toString())
        );
    }
}
