package com.github.jinahya.persistence.more;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An abstract class for converting {@code JSON} db data to a specific type of entity attribute, and vice versa.
 *
 * @param <X> entity attribute type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __JsonAttributeConverter<X> extends __StringAttributeConverter<X> {

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD})
    public @interface __JsonAttributeConverterDelegate {

    }

    // ------------------------------------------------------------------------------------------ STATIC_FACTORY_METHODS

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    protected __JsonAttributeConverter() {
        super();
    }
}
