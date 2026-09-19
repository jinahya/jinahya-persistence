package com.github.jinahya.persistence.more.converter;

import java.util.Objects;

/**
 * An abstract class for testing {@link __StringAttributeConverter} implementations, whose database column type is
 * always {@link String}.
 *
 * @param <T> converter type parameter
 * @param <X> entity attribute type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @apiNote This is not {@code jinahya-persistence-more-test}'s class of the same name. That module depends on
 *         this one, so this module's own tests can not reach it without a cycle; this hierarchy is local, and
 *         deliberately thin. Anything a <em>consumer</em> should be able to extend belongs in the other one.
 */
@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
abstract class __StringAttributeConverter_Test<T extends __StringAttributeConverter<X>, X> {

    /**
     * Creates a new instance for testing the specified converter class.
     *
     * @param converterClass the converter class to test.
     * @param attributeClass the type of the entity attribute.
     */
    __StringAttributeConverter_Test(final Class<T> converterClass, final Class<X> attributeClass) {
        super();
        this.converterClass = Objects.requireNonNull(converterClass, "converterClass is null");
        this.attributeClass = Objects.requireNonNull(attributeClass, "attributeClass is null");
    }

    /**
     * The converter class being tested.
     */
    final Class<T> converterClass;

    /**
     * The type of the entity attribute the {@link #converterClass} converts.
     */
    final Class<X> attributeClass;
}
