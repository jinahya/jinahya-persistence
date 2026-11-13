package com.github.jinahya.persistence.more.converter.test;

import com.github.jinahya.persistence.more.converter.javatime.YearConverter;

import java.time.Year;

import java.util.List;

/**
 * Tests {@link YearConverter.OfInteger} against what every attribute converter is expected to do.
 */
class YearConverter_OfInteger_Test
        extends __AttributeConverter_Test<YearConverter.OfInteger, Year, Integer> {

    YearConverter_OfInteger_Test() {
        super(YearConverter.OfInteger.class);
    }

    @Override
    protected List<__AttributeConverterTestCase<Year, Integer>> testCases() {
        return List.of(
                __AttributeConverterTestCase.of(Year.of(2026), 2026),
                __AttributeConverterTestCase.of(Year.of(Year.MAX_VALUE), Year.MAX_VALUE)
        );
    }
}
