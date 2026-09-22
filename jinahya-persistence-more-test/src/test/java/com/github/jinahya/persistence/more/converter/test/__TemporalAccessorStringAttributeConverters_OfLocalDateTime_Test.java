package com.github.jinahya.persistence.more.converter.test;

import com.github.jinahya.persistence.more.converter.__TemporalAccessorStringAttributeConverters;

import java.time.LocalDateTime;

import java.util.List;

/**
 * Tests {@link __TemporalAccessorStringAttributeConverters.OfLocalDateTime} against what every attribute converter is expected to do.
 */
class __TemporalAccessorStringAttributeConverters_OfLocalDateTime_Test
        extends __StringAttributeConverter_Test<__TemporalAccessorStringAttributeConverters.OfLocalDateTime, LocalDateTime> {

    __TemporalAccessorStringAttributeConverters_OfLocalDateTime_Test() {
        super(__TemporalAccessorStringAttributeConverters.OfLocalDateTime.class);
    }

    @Override
    protected List<__AttributeConverterTestCase<LocalDateTime, String>> testCases() {
        return List.of(
                __AttributeConverterTestCase.of(LocalDateTime.of(2026, 9, 19, 11, 22, 33), LocalDateTime.of(2026, 9, 19, 11, 22, 33).toString())
        );
    }
}
