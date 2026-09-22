package com.github.jinahya.persistence.more.converter.test;

import com.github.jinahya.persistence.more.converter.__NumberStringAttributeConverters;

import java.util.List;

/**
 * Tests {@link __NumberStringAttributeConverters.OfDouble} against what every attribute converter is expected to do.
 */
class __NumberStringAttributeConverters_OfDouble_Test
        extends __StringAttributeConverter_Test<__NumberStringAttributeConverters.OfDouble, Double> {

    __NumberStringAttributeConverters_OfDouble_Test() {
        super(__NumberStringAttributeConverters.OfDouble.class);
    }

    @Override
    protected List<__AttributeConverterTestCase<Double, String>> testCases() {
        return List.of(
                __AttributeConverterTestCase.of(0.0d, "0.0"),
                __AttributeConverterTestCase.of(1234.5678d, "1234.5678"),
                __AttributeConverterTestCase.of(-1.0d, "-1.0")
        );
    }
}
