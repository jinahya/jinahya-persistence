package com.github.jinahya.persistence.more.converter.test;

import com.github.jinahya.persistence.more.converter.__TemporalAmountLongAttributeConverters;

import java.time.Duration;

import java.util.List;

/**
 * Tests {@link __TemporalAmountLongAttributeConverters.OfDuration} against what every attribute converter is expected to do.
 */
class __TemporalAmountLongAttributeConverters_OfDuration_Test
        extends __AttributeConverter_Test<__TemporalAmountLongAttributeConverters.OfDuration, Duration, Long> {

    __TemporalAmountLongAttributeConverters_OfDuration_Test() {
        super(__TemporalAmountLongAttributeConverters.OfDuration.class);
    }

    @Override
    protected List<__AttributeConverterTestCase<Duration, Long>> testCases() {
        return List.of(
                __AttributeConverterTestCase.of(Duration.ofHours(3L), Duration.ofHours(3L).toNanos()),
                __AttributeConverterTestCase.of(Duration.ofNanos(1L), 1L)
        );
    }
}
