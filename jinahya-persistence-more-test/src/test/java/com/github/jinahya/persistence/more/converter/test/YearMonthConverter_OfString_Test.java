package com.github.jinahya.persistence.more.converter.test;

import com.github.jinahya.persistence.more.converter.javatime.YearMonthConverter;

import java.time.YearMonth;

import java.util.List;

/**
 * Tests {@link YearMonthConverter.OfString} against what every attribute converter is
 * expected to do.
 */
class YearMonthConverter_OfString_Test
        extends StringAttributeConverter_Test<YearMonthConverter.OfString, YearMonth> {

    YearMonthConverter_OfString_Test() {
        super(YearMonthConverter.OfString.class);
    }

    @Override
    protected List<__AttributeConverterTestCase<YearMonth, String>> testCases() {
        return List.of(
                __AttributeConverterTestCase.of(YearMonth.of(2026, 9), YearMonth.of(2026, 9).toString())
        );
    }
}
