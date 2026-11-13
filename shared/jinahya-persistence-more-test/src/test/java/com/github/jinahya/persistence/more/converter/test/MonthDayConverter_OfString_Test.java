package com.github.jinahya.persistence.more.converter.test;

import com.github.jinahya.persistence.more.converter.javatime.MonthDayConverter;

import java.time.MonthDay;

import java.util.List;

/**
 * Tests {@link MonthDayConverter.OfString} against what every attribute converter is
 * expected to do.
 */
class MonthDayConverter_OfString_Test
        extends StringAttributeConverter_Test<MonthDayConverter.OfString, MonthDay> {

    MonthDayConverter_OfString_Test() {
        super(MonthDayConverter.OfString.class);
    }

    @Override
    protected List<__AttributeConverterTestCase<MonthDay, String>> testCases() {
        return List.of(
                __AttributeConverterTestCase.of(MonthDay.of(9, 19), MonthDay.of(9, 19).toString())
        );
    }
}
