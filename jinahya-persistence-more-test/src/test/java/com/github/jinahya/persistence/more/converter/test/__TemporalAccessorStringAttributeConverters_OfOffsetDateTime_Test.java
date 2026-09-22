package com.github.jinahya.persistence.more.converter.test;

import com.github.jinahya.persistence.more.converter.__TemporalAccessorStringAttributeConverters;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import java.util.List;

/**
 * Tests {@link __TemporalAccessorStringAttributeConverters.OfOffsetDateTime} against what every attribute converter is expected to do.
 */
class __TemporalAccessorStringAttributeConverters_OfOffsetDateTime_Test
        extends __StringAttributeConverter_Test<__TemporalAccessorStringAttributeConverters.OfOffsetDateTime, OffsetDateTime> {

    __TemporalAccessorStringAttributeConverters_OfOffsetDateTime_Test() {
        super(__TemporalAccessorStringAttributeConverters.OfOffsetDateTime.class);
    }

    @Override
    protected List<__AttributeConverterTestCase<OffsetDateTime, String>> testCases() {
        return List.of(
                __AttributeConverterTestCase.of(OffsetDateTime.of(LocalDateTime.of(2026, 9, 19, 11, 22, 33), ZoneOffset.ofHours(9)), OffsetDateTime.of(LocalDateTime.of(2026, 9, 19, 11, 22, 33), ZoneOffset.ofHours(9)).toString())
        );
    }
}
