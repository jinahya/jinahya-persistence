package com.github.jinahya.persistence.more.converter.test;

import com.github.jinahya.persistence.more.converter.__TemporalAmountStringAttributeConverters;

import java.time.Period;

import java.util.List;

/**
 * Tests {@link __TemporalAmountStringAttributeConverters.OfPeriod} against what every attribute converter is expected to do.
 */
class __TemporalAmountStringAttributeConverters_OfPeriod_Test
        extends __StringAttributeConverter_Test<__TemporalAmountStringAttributeConverters.OfPeriod, Period> {

    __TemporalAmountStringAttributeConverters_OfPeriod_Test() {
        super(__TemporalAmountStringAttributeConverters.OfPeriod.class, Period.class);
    }

    @Override
    protected List<__AttributeConverterTestCase<Period, String>> testCases() {
        return List.of(
                __AttributeConverterTestCase.of(Period.of(1, 2, 3), Period.of(1, 2, 3).toString()),
                __AttributeConverterTestCase.of(Period.ZERO, Period.ZERO.toString())
        );
    }
}
