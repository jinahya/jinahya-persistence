package com.github.jinahya.persistence.more.converter.test;

import com.github.jinahya.persistence.more.converter.__TemporalAccessorStringAttributeConverters;

import java.time.LocalTime;

import java.util.List;

/**
 * Tests {@link __TemporalAccessorStringAttributeConverters.OfLocalTime} against what every attribute converter is expected to do.
 */
class __TemporalAccessorStringAttributeConverters_OfLocalTime_Test
        extends __StringAttributeConverter_Test<__TemporalAccessorStringAttributeConverters.OfLocalTime, LocalTime> {

    __TemporalAccessorStringAttributeConverters_OfLocalTime_Test() {
        super(__TemporalAccessorStringAttributeConverters.OfLocalTime.class);
    }

    @Override
    protected List<__AttributeConverterTestCase<LocalTime, String>> testCases() {
        return List.of(
                __AttributeConverterTestCase.of(LocalTime.of(11, 22, 33), LocalTime.of(11, 22, 33).toString())
        );
    }
}
