package com.github.jinahya.persistence.more.converter.test;

import com.github.jinahya.persistence.more.converter.__TemporalAccessorLongAttributeConverters;

import java.time.LocalTime;

import java.util.List;

/**
 * Tests {@link __TemporalAccessorLongAttributeConverters.OfLocalTime} against what every attribute converter is expected to do.
 */
class __TemporalAccessorLongAttributeConverters_OfLocalTime_Test
        extends __AttributeConverter_Test<__TemporalAccessorLongAttributeConverters.OfLocalTime, LocalTime, Long> {

    __TemporalAccessorLongAttributeConverters_OfLocalTime_Test() {
        super(__TemporalAccessorLongAttributeConverters.OfLocalTime.class);
    }

    @Override
    protected List<__AttributeConverterTestCase<LocalTime, Long>> testCases() {
        return List.of(
                __AttributeConverterTestCase.of(LocalTime.of(11, 22, 33), LocalTime.of(11, 22, 33).toNanoOfDay()),
                __AttributeConverterTestCase.of(LocalTime.MIDNIGHT, 0L)
        );
    }
}
