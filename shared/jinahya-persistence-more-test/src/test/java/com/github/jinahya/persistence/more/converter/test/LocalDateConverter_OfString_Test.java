package com.github.jinahya.persistence.more.converter.test;

import com.github.jinahya.persistence.more.converter.javatime.LocalDateConverter;

import java.time.LocalDate;

import java.util.List;

/**
 * Tests {@link LocalDateConverter.OfString} against what every attribute converter is
 * expected to do.
 */
class LocalDateConverter_OfString_Test
        extends StringAttributeConverter_Test<LocalDateConverter.OfString, LocalDate> {

    LocalDateConverter_OfString_Test() {
        super(LocalDateConverter.OfString.class);
    }

    @Override
    protected List<__AttributeConverterTestCase<LocalDate, String>> testCases() {
        return List.of(
                __AttributeConverterTestCase.of(LocalDate.of(2026, 9, 19), LocalDate.of(2026, 9, 19).toString())
        );
    }
}
