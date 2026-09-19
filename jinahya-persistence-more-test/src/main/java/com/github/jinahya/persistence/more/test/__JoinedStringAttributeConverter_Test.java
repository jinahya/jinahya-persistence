package com.github.jinahya.persistence.more.test;

import com.github.jinahya.persistence.more.converter.__JoinedStringAttributeConverter;

import java.util.List;
import java.util.Objects;

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
     * @param elementClass   the type of the elements of the list.
     */
    @SuppressWarnings({"unchecked"})
    protected __JoinedStringAttributeConverter_Test(final Class<CONVERTER> converterClass,
                                                    final Class<X> elementClass) {
        // List<X>.class cannot be written down; the erasure is all the superclass uses it for
        super(converterClass, (Class<List<X>>) (Class<?>) List.class);
        this.elementClass = Objects.requireNonNull(elementClass, "elementClass is null");
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The class of the elements of the list.
     */
    protected final Class<X> elementClass;
}
