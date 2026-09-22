package com.github.jinahya.persistence.more.converter.test;

import com.github.jinahya.persistence.more.converter.__TemporalAmountStringAttributeConverters;

import java.time.Duration;

import java.util.List;

/**
 * Tests {@link __TemporalAmountStringAttributeConverters.OfDuration} against what every attribute converter is expected to do.
 */
class __TemporalAmountStringAttributeConverters_OfDuration_Test
        extends __StringAttributeConverter_Test<__TemporalAmountStringAttributeConverters.OfDuration, Duration> {

    __TemporalAmountStringAttributeConverters_OfDuration_Test() {
        super(__TemporalAmountStringAttributeConverters.OfDuration.class, Duration.class);
    }

    @Override
    protected List<__AttributeConverterTestCase<Duration, String>> testCases() {
        return List.of(
                __AttributeConverterTestCase.of(Duration.ofHours(3L), Duration.ofHours(3L).toString()),
                __AttributeConverterTestCase.of(Duration.ZERO, Duration.ZERO.toString())
        );
    }
}
