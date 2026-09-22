package com.github.jinahya.persistence.more.converter.test;

import com.github.jinahya.persistence.more.converter.__TemporalAccessorLongAttributeConverters;

import java.time.LocalDate;

import java.util.List;

/**
 * Tests {@link __TemporalAccessorLongAttributeConverters.OfLocalDate} against what every attribute converter is expected to do.
 */
class __TemporalAccessorLongAttributeConverters_OfLocalDate_Test
        extends __AttributeConverter_Test<__TemporalAccessorLongAttributeConverters.OfLocalDate, LocalDate, Long> {

    __TemporalAccessorLongAttributeConverters_OfLocalDate_Test() {
        super(__TemporalAccessorLongAttributeConverters.OfLocalDate.class);
    }

    @Override
    protected List<__AttributeConverterTestCase<LocalDate, Long>> testCases() {
        return List.of(
                __AttributeConverterTestCase.of(LocalDate.of(2026, 9, 19), LocalDate.of(2026, 9, 19).toEpochDay()),
                __AttributeConverterTestCase.of(LocalDate.EPOCH, 0L)
        );
    }
}
