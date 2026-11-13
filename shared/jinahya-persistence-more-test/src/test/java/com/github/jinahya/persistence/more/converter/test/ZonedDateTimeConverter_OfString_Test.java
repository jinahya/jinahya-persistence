package com.github.jinahya.persistence.more.converter.test;

import com.github.jinahya.persistence.more.converter.javatime.ZonedDateTimeConverter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import java.util.List;

/**
 * Tests {@link ZonedDateTimeConverter.OfString} against what every attribute converter is
 * expected to do.
 */
class ZonedDateTimeConverter_OfString_Test
        extends
        StringAttributeConverter_Test<ZonedDateTimeConverter.OfString, ZonedDateTime> {

    ZonedDateTimeConverter_OfString_Test() {
        super(ZonedDateTimeConverter.OfString.class);
    }

    @Override
    protected List<__AttributeConverterTestCase<ZonedDateTime, String>> testCases() {
        return List.of(
                __AttributeConverterTestCase.of(
                        ZonedDateTime.of(LocalDateTime.of(2026, 9, 19, 11, 22, 33), ZoneId.of("Asia/Seoul")),
                        ZonedDateTime.of(LocalDateTime.of(2026, 9, 19, 11, 22, 33), ZoneId.of("Asia/Seoul")).toString())
        );
    }
}
