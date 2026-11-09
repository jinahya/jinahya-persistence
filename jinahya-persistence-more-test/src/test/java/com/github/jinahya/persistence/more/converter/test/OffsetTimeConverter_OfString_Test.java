package com.github.jinahya.persistence.more.converter.test;

import com.github.jinahya.persistence.more.converter.javatime.OffsetTimeConverter;

import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;

import java.util.List;

/**
 * Tests {@link OffsetTimeConverter.OfString} against what every attribute converter is
 * expected to do.
 */
class OffsetTimeConverter_OfString_Test
        extends StringAttributeConverter_Test<OffsetTimeConverter.OfString, OffsetTime> {

    OffsetTimeConverter_OfString_Test() {
        super(OffsetTimeConverter.OfString.class);
    }

    @Override
    protected List<__AttributeConverterTestCase<OffsetTime, String>> testCases() {
        return List.of(
                __AttributeConverterTestCase.of(OffsetTime.of(LocalTime.of(11, 22, 33), ZoneOffset.ofHours(9)),
                                                OffsetTime.of(LocalTime.of(11, 22, 33),
                                                              ZoneOffset.ofHours(9)).toString())
        );
    }
}
