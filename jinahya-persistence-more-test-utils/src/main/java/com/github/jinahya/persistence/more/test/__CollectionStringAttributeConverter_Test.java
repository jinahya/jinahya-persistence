package com.github.jinahya.persistence.more.test;

import com.github.jinahya.persistence.more.__CollectionStringAttributeConverter;

import java.util.Collection;
import java.util.Objects;

/**
 * An abstract class for testing {@link __CollectionStringAttributeConverter} implementations.
 *
 * @param <CONVERTER> converter type parameter
 * @param <C>         collection type parameter
 * @param <X>         element type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public abstract class __CollectionStringAttributeConverter_Test<
        CONVERTER extends __CollectionStringAttributeConverter<C, X>,
        C extends Collection<X>,
        X
        >
        extends __StringAttributeConverter_Test<CONVERTER, C> {

    /**
     * Creates a new instance for testing the specified converter class.
     *
     * @param converterClass the converter class to test.
     * @param attributeClass the type of the collection held by the entity attribute.
     * @param elementClass   the type of the elements of the collection.
     */
    protected __CollectionStringAttributeConverter_Test(final Class<CONVERTER> converterClass,
                                                        final Class<C> attributeClass, final Class<X> elementClass) {
        super(converterClass, attributeClass);
        this.elementClass = Objects.requireNonNull(elementClass, "elementClass is null");
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The class of the elements of the collection.
     */
    protected final Class<X> elementClass;
}
