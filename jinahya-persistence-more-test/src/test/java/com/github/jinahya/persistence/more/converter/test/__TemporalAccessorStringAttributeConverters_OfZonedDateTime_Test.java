package com.github.jinahya.persistence.more.converter.test;

import com.github.jinahya.persistence.more.converter.__TemporalAccessorStringAttributeConverters;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import java.util.List;

/**
 * Tests {@link __TemporalAccessorStringAttributeConverters.OfZonedDateTime} against what every attribute converter is expected to do.
 */
class __TemporalAccessorStringAttributeConverters_OfZonedDateTime_Test
        extends __StringAttributeConverter_Test<__TemporalAccessorStringAttributeConverters.OfZonedDateTime, ZonedDateTime> {

    __TemporalAccessorStringAttributeConverters_OfZonedDateTime_Test() {
        super(__TemporalAccessorStringAttributeConverters.OfZonedDateTime.class);
    }

    @Override
    protected List<__AttributeConverterTestCase<ZonedDateTime, String>> testCases() {
        return List.of(
                __AttributeConverterTestCase.of(ZonedDateTime.of(LocalDateTime.of(2026, 9, 19, 11, 22, 33), ZoneId.of("Asia/Seoul")), ZonedDateTime.of(LocalDateTime.of(2026, 9, 19, 11, 22, 33), ZoneId.of("Asia/Seoul")).toString())
        );
    }
}
