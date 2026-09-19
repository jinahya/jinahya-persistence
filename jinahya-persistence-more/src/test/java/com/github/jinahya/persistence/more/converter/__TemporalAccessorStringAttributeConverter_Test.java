package com.github.jinahya.persistence.more.converter;

import java.time.temporal.TemporalAccessor;

/**
 * An abstract class for testing {@link __TemporalAccessorStringAttributeConverter} implementations.
 *
 * @param <T> converter type parameter
 * @param <X> temporal accessor type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
abstract class __TemporalAccessorStringAttributeConverter_Test<T extends __TemporalAccessorStringAttributeConverter<X>,
        X extends TemporalAccessor>
        extends __StringAttributeConverter_Test<T, X> {

    /**
     * Creates a new instance for testing the specified converter class.
     *
     * @param converterClass the converter class to test.
     * @param attributeClass the type of the entity attribute.
     */
    __TemporalAccessorStringAttributeConverter_Test(final Class<T> converterClass, final Class<X> attributeClass) {
        super(converterClass, attributeClass);
    }
}
