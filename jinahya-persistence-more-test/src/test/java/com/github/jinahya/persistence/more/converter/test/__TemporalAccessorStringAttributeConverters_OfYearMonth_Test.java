package com.github.jinahya.persistence.more.converter.test;

import com.github.jinahya.persistence.more.converter.__TemporalAccessorStringAttributeConverters;

import java.time.YearMonth;

import java.util.List;

/**
 * Tests {@link __TemporalAccessorStringAttributeConverters.OfYearMonth} against what every attribute converter is expected to do.
 */
class __TemporalAccessorStringAttributeConverters_OfYearMonth_Test
        extends __StringAttributeConverter_Test<__TemporalAccessorStringAttributeConverters.OfYearMonth, YearMonth> {

    __TemporalAccessorStringAttributeConverters_OfYearMonth_Test() {
        super(__TemporalAccessorStringAttributeConverters.OfYearMonth.class, YearMonth.class);
    }

    @Override
    protected List<__AttributeConverterTestCase<YearMonth, String>> testCases() {
        return List.of(
                __AttributeConverterTestCase.of(YearMonth.of(2026, 9), YearMonth.of(2026, 9).toString())
        );
    }
}
