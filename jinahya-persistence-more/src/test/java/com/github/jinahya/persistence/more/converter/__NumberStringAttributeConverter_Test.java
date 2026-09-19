package com.github.jinahya.persistence.more.converter;

/**
 * An abstract class for testing {@link __NumberStringAttributeConverter} implementations.
 *
 * @param <T> converter type parameter
 * @param <X> number type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
abstract class __NumberStringAttributeConverter_Test<T extends __NumberStringAttributeConverter<X>,
        X extends Number>
        extends __StringAttributeConverter_Test<T, X> {

    /**
     * Creates a new instance for testing the specified converter class.
     *
     * @param converterClass the converter class to test.
     * @param attributeClass the type of the entity attribute.
     */
    __NumberStringAttributeConverter_Test(final Class<T> converterClass, final Class<X> attributeClass) {
        super(converterClass, attributeClass);
    }
}
