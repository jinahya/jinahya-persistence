package com.github.jinahya.persistence.more.converter.test;

import com.github.jinahya.persistence.more.converter.javatime.LocalDateTimeConverter;

import java.time.LocalDateTime;

import java.util.List;

/**
 * Tests {@link LocalDateTimeConverter.OfString} against what every attribute converter is
 * expected to do.
 */
class LocalDateTimeConverter_OfString_Test
        extends
        StringAttributeConverter_Test<LocalDateTimeConverter.OfString, LocalDateTime> {

    LocalDateTimeConverter_OfString_Test() {
        super(LocalDateTimeConverter.OfString.class);
    }

    @Override
    protected List<__AttributeConverterTestCase<LocalDateTime, String>> testCases() {
        return List.of(
                __AttributeConverterTestCase.of(LocalDateTime.of(2026, 9, 19, 11, 22, 33),
                                                LocalDateTime.of(2026, 9, 19, 11, 22, 33).toString())
        );
    }
}
