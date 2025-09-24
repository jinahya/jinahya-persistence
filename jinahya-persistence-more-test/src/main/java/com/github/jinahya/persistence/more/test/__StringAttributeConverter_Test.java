package com.github.jinahya.persistence.more.test;

import com.github.jinahya.persistence.more.__StringAttributeConverter;

@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __StringAttributeConverter_Test<C extends __StringAttributeConverter<X>, X>
        extends ___AttributeConverter_Test<C, X, String> {

    protected __StringAttributeConverter_Test(final Class<C> converterClass, final Class<X> attributeClass) {
        super(converterClass, attributeClass, String.class);
    }
}
