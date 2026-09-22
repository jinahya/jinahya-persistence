package com.github.jinahya.persistence.more.converter.test;

import com.github.jinahya.persistence.more.converter.__TemporalAccessorStringAttributeConverters;

import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;

import java.util.List;

/**
 * Tests {@link __TemporalAccessorStringAttributeConverters.OfOffsetTime} against what every attribute converter is expected to do.
 */
class __TemporalAccessorStringAttributeConverters_OfOffsetTime_Test
        extends __StringAttributeConverter_Test<__TemporalAccessorStringAttributeConverters.OfOffsetTime, OffsetTime> {

    __TemporalAccessorStringAttributeConverters_OfOffsetTime_Test() {
        super(__TemporalAccessorStringAttributeConverters.OfOffsetTime.class);
    }

    @Override
    protected List<__AttributeConverterTestCase<OffsetTime, String>> testCases() {
        return List.of(
                __AttributeConverterTestCase.of(OffsetTime.of(LocalTime.of(11, 22, 33), ZoneOffset.ofHours(9)), OffsetTime.of(LocalTime.of(11, 22, 33), ZoneOffset.ofHours(9)).toString())
        );
    }
}
