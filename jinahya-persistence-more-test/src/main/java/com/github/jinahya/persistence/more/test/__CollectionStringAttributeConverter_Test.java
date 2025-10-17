package com.github.jinahya.persistence.more.test;

import com.github.jinahya.persistence.more.__CollectionStringAttributeConverter;

import java.util.Collection;
import java.util.Objects;

public abstract class __CollectionStringAttributeConverter_Test<
        CONVERTER extends __CollectionStringAttributeConverter<C, X>,
        C extends Collection<X>,
        X
        >
        extends __StringAttributeConverter_Test<CONVERTER, C> {

    protected __CollectionStringAttributeConverter_Test(final Class<CONVERTER> converterClass,
                                                        final Class<C> attributeClass, final Class<X> elementClass) {
        super(converterClass, attributeClass);
        this.elementClass = Objects.requireNonNull(elementClass, "elementClass is null");
    }

    // -----------------------------------------------------------------------------------------------------------------
    protected final Class<X> elementClass;
}
