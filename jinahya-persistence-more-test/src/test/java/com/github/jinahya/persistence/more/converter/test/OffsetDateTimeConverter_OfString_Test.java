package com.github.jinahya.persistence.more.converter.test;

import com.github.jinahya.persistence.more.converter.javatime.OffsetDateTimeConverter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import java.util.List;

/**
 * Tests {@link OffsetDateTimeConverter.OfString} against what every attribute converter is
 * expected to do.
 */
class OffsetDateTimeConverter_OfString_Test
        extends
        StringAttributeConverter_Test<OffsetDateTimeConverter.OfString, OffsetDateTime> {

    OffsetDateTimeConverter_OfString_Test() {
        super(OffsetDateTimeConverter.OfString.class);
    }

    @Override
    protected List<__AttributeConverterTestCase<OffsetDateTime, String>> testCases() {
        return List.of(
                __AttributeConverterTestCase.of(
                        OffsetDateTime.of(LocalDateTime.of(2026, 9, 19, 11, 22, 33), ZoneOffset.ofHours(9)),
                        OffsetDateTime.of(LocalDateTime.of(2026, 9, 19, 11, 22, 33), ZoneOffset.ofHours(9)).toString())
        );
    }
}
