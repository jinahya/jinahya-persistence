package com.github.jinahya.persistence.more.converter;

import java.time.temporal.TemporalAmount;

/**
 * An abstract class for testing {@link __TemporalAmountStringAttributeConverter} implementations.
 *
 * @param <T> converter type parameter
 * @param <X> temporal amount type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
abstract class __TemporalAmountStringAttributeConverter_Test<
        T extends __TemporalAmountStringAttributeConverter<X>,
        X extends TemporalAmount>
        extends __StringAttributeConverter_Test<T, X> {

    /**
     * Creates a new instance for testing the specified converter class.
     *
     * @param converterClass the converter class to test.
     * @param attributeClass the type of the entity attribute.
     */
    __TemporalAmountStringAttributeConverter_Test(final Class<T> converterClass, final Class<X> attributeClass) {
        super(converterClass, attributeClass);
    }
}
