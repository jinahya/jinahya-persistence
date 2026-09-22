package com.github.jinahya.persistence.more.converter.test;

import com.github.jinahya.persistence.more.converter.__TemporalAccessorStringAttributeConverters;

import java.time.LocalDate;

import java.util.List;

/**
 * Tests {@link __TemporalAccessorStringAttributeConverters.OfLocalDate} against what every attribute converter is expected to do.
 */
class __TemporalAccessorStringAttributeConverters_OfLocalDate_Test
        extends __StringAttributeConverter_Test<__TemporalAccessorStringAttributeConverters.OfLocalDate, LocalDate> {

    __TemporalAccessorStringAttributeConverters_OfLocalDate_Test() {
        super(__TemporalAccessorStringAttributeConverters.OfLocalDate.class, LocalDate.class);
    }

    @Override
    protected List<__AttributeConverterTestCase<LocalDate, String>> testCases() {
        return List.of(
                __AttributeConverterTestCase.of(LocalDate.of(2026, 9, 19), LocalDate.of(2026, 9, 19).toString())
        );
    }
}
