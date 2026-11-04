package com.github.jinahya.persistence.more.converter.test;

import com.github.jinahya.persistence.more.converter.__NumberStringAttributeConverters;

import java.math.BigInteger;
import java.util.List;

/**
 * Tests {@link __NumberStringAttributeConverters.OfBigInteger} against what every attribute converter is expected to
 * do.
 */
class __NumberStringAttributeConverters_OfBigInteger_Test
        extends __StringAttributeConverter_Test<__NumberStringAttributeConverters.OfBigInteger, BigInteger> {

    __NumberStringAttributeConverters_OfBigInteger_Test() {
        super(__NumberStringAttributeConverters.OfBigInteger.class);
    }

    @Override
    protected List<__AttributeConverterTestCase<BigInteger, String>> testCases() {
        return List.of(
                __AttributeConverterTestCase.of(BigInteger.ZERO, "0"),
                __AttributeConverterTestCase.of(BigInteger.ONE.negate(), "-1"),
                __AttributeConverterTestCase.of(new BigInteger("123456789012345678901234567890"),
                                                "123456789012345678901234567890"),
                __AttributeConverterTestCase.of(new BigInteger("-123456789012345678901234567890"),
                                                "-123456789012345678901234567890")
        );
    }
}
