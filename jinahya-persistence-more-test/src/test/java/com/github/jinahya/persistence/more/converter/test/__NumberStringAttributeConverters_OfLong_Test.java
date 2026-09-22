package com.github.jinahya.persistence.more.converter.test;

import com.github.jinahya.persistence.more.converter.__NumberStringAttributeConverters;

import java.util.List;

/**
 * Tests {@link __NumberStringAttributeConverters.OfLong} against what every attribute converter is expected to do.
 */
class __NumberStringAttributeConverters_OfLong_Test
        extends __StringAttributeConverter_Test<__NumberStringAttributeConverters.OfLong, Long> {

    __NumberStringAttributeConverters_OfLong_Test() {
        super(__NumberStringAttributeConverters.OfLong.class);
    }

    @Override
    protected List<__AttributeConverterTestCase<Long, String>> testCases() {
        return List.of(
                __AttributeConverterTestCase.of(0L, "0"),
                __AttributeConverterTestCase.of(-1L, "-1"),
                __AttributeConverterTestCase.of(Long.MAX_VALUE, "9223372036854775807"),
                __AttributeConverterTestCase.of(Long.MIN_VALUE, "-9223372036854775808")
        );
    }
}
