package com.github.jinahya.persistence.more.converter.test;

import com.github.jinahya.persistence.more.converter.__NumberStringAttributeConverters;

import java.math.BigDecimal;

import java.util.List;

/**
 * Tests {@link __NumberStringAttributeConverters.OfBigDecimal} against what every attribute converter is expected to do.
 */
class __NumberStringAttributeConverters_OfBigDecimal_Test
        extends __StringAttributeConverter_Test<__NumberStringAttributeConverters.OfBigDecimal, BigDecimal> {

    __NumberStringAttributeConverters_OfBigDecimal_Test() {
        super(__NumberStringAttributeConverters.OfBigDecimal.class);
    }

    @Override
    protected List<__AttributeConverterTestCase<BigDecimal, String>> testCases() {
        return List.of(
                __AttributeConverterTestCase.of(new BigDecimal("0"), "0"),
                __AttributeConverterTestCase.of(new BigDecimal("1.50"), "1.50"),
                __AttributeConverterTestCase.of(new BigDecimal("-12345.6789"), "-12345.6789")
        );
    }
}
