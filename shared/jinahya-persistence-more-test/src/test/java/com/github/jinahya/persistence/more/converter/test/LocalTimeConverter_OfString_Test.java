package com.github.jinahya.persistence.more.converter.test;

import com.github.jinahya.persistence.more.converter.javatime.LocalTimeConverter;

import java.time.LocalTime;

import java.util.List;

/**
 * Tests {@link LocalTimeConverter.OfString} against what every attribute converter is
 * expected to do.
 */
class LocalTimeConverter_OfString_Test
        extends StringAttributeConverter_Test<LocalTimeConverter.OfString, LocalTime> {

    LocalTimeConverter_OfString_Test() {
        super(LocalTimeConverter.OfString.class);
    }

    @Override
    protected List<__AttributeConverterTestCase<LocalTime, String>> testCases() {
        return List.of(
                __AttributeConverterTestCase.of(LocalTime.of(11, 22, 33), LocalTime.of(11, 22, 33).toString())
        );
    }
}
