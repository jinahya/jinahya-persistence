package com.github.jinahya.persistence.more.converter.test;

import com.github.jinahya.persistence.more.converter.__JoinedStringAttributeConverter;

import java.util.List;

/**
 * An abstract class for testing {@link __JoinedStringAttributeConverter} implementations.
 *
 * @param <CONVERTER> converter type parameter
 * @param <X>         element type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public abstract class __JoinedStringAttributeConverter_Test<
        CONVERTER extends __JoinedStringAttributeConverter<X>,
        X
        >
        extends __StringAttributeConverter_Test<CONVERTER, List<X>> {

    /**
     * Creates a new instance for testing the specified converter class.
     *
     * @param converterClass the converter class to test.
     */
    protected __JoinedStringAttributeConverter_Test(final Class<CONVERTER> converterClass) {
        super(converterClass);
    }
}
