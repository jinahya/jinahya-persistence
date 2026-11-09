package com.github.jinahya.persistence.more.converter.test;

import com.github.jinahya.persistence.more.converter.javatime.YearConverter;

import java.time.Year;

import java.util.List;

/**
 * Tests {@link YearConverter.OfString} against what every attribute converter is expected to do.
 */
class YearConverter_OfString_Test
        extends StringAttributeConverter_Test<YearConverter.OfString, Year> {

    YearConverter_OfString_Test() {
        super(YearConverter.OfString.class);
    }

    @Override
    protected List<__AttributeConverterTestCase<Year, String>> testCases() {
        return List.of(
                __AttributeConverterTestCase.of(Year.of(2026), Year.of(2026).toString())
        );
    }
}
