package com.github.jinahya.persistence.more.converter.test;

import com.github.jinahya.persistence.more.converter.__NumberStringAttributeConverters;

import java.util.List;

/**
 * Tests {@link __NumberStringAttributeConverters.OfFloat} against what every attribute converter is expected to do.
 */
class __NumberStringAttributeConverters_OfFloat_Test
        extends __StringAttributeConverter_Test<__NumberStringAttributeConverters.OfFloat, Float> {

    __NumberStringAttributeConverters_OfFloat_Test() {
        super(__NumberStringAttributeConverters.OfFloat.class);
    }

    @Override
    protected List<__AttributeConverterTestCase<Float, String>> testCases() {
        return List.of(
                __AttributeConverterTestCase.of(0.0f, "0.0"),
                __AttributeConverterTestCase.of(1.5f, "1.5"),
                __AttributeConverterTestCase.of(-1.5f, "-1.5")
        );
    }
}
