package com.github.jinahya.persistence.more.converter.test;

import com.github.jinahya.persistence.more.converter.javatime.YearConverter;

import java.time.Year;

import java.util.List;

/**
 * Tests {@link YearConverter.OfShort} against what every attribute converter is expected to do.
 */
class YearConverter_OfShort_Test
        extends __AttributeConverter_Test<YearConverter.OfShort, Year, Short> {

    YearConverter_OfShort_Test() {
        super(YearConverter.OfShort.class);
    }

    @Override
    protected List<__AttributeConverterTestCase<Year, Short>> testCases() {
        return List.of(
                __AttributeConverterTestCase.of(Year.of(2026), (short) 2026),
                __AttributeConverterTestCase.of(Year.of(0), (short) 0)
        );
    }
}
