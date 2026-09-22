package com.github.jinahya.persistence.more.converter.test;

import com.github.jinahya.persistence.more.converter.__TemporalAccessorStringAttributeConverters;

import java.time.Year;

import java.util.List;

/**
 * Tests {@link __TemporalAccessorStringAttributeConverters.OfYear} against what every attribute converter is expected to do.
 */
class __TemporalAccessorStringAttributeConverters_OfYear_Test
        extends __StringAttributeConverter_Test<__TemporalAccessorStringAttributeConverters.OfYear, Year> {

    __TemporalAccessorStringAttributeConverters_OfYear_Test() {
        super(__TemporalAccessorStringAttributeConverters.OfYear.class, Year.class);
    }

    @Override
    protected List<__AttributeConverterTestCase<Year, String>> testCases() {
        return List.of(
                __AttributeConverterTestCase.of(Year.of(2026), Year.of(2026).toString())
        );
    }
}
