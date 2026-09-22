package com.github.jinahya.persistence.more.converter.test;

import com.github.jinahya.persistence.more.converter.__TemporalAccessorStringAttributeConverters;

import java.time.MonthDay;

import java.util.List;

/**
 * Tests {@link __TemporalAccessorStringAttributeConverters.OfMonthDay} against what every attribute converter is expected to do.
 */
class __TemporalAccessorStringAttributeConverters_OfMonthDay_Test
        extends __StringAttributeConverter_Test<__TemporalAccessorStringAttributeConverters.OfMonthDay, MonthDay> {

    __TemporalAccessorStringAttributeConverters_OfMonthDay_Test() {
        super(__TemporalAccessorStringAttributeConverters.OfMonthDay.class);
    }

    @Override
    protected List<__AttributeConverterTestCase<MonthDay, String>> testCases() {
        return List.of(
                __AttributeConverterTestCase.of(MonthDay.of(9, 19), MonthDay.of(9, 19).toString())
        );
    }
}
