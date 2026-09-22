package com.github.jinahya.persistence.more.converter.test;

import com.github.jinahya.persistence.more.converter.__StringAttributeConverter;

/**
 * An abstract class for testing {@link __StringAttributeConverter} implementations, whose database column type is
 * always {@link String}.
 *
 * @param <C> converter type parameter
 * @param <X> entity attribute type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __JoinedStringAttributeConverter_Test
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __StringAttributeConverter_Test<C extends __StringAttributeConverter<X>, X>
        extends __AttributeConverter_Test<C, X, String> {

    /**
     * Creates a new instance for testing the specified converter class.
     *
     * @param converterClass the converter class to test.
     */
    protected __StringAttributeConverter_Test(final Class<C> converterClass) {
        super(converterClass);
    }
}
