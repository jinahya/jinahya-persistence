package com.github.jinahya.persistence.crypto;

import jakarta.inject.Qualifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A CDI {@link Qualifier qualifier} for an {@link __EncryptionService} implementation.
 * <p>
 * Applying it to a service implementation lets an application deploy more than one, and select between them at the
 * injection point.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __EncryptionService
 */
@Qualifier
@Retention(
        RetentionPolicy.RUNTIME
)
@Target({
        ElementType.TYPE
})
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public @interface __EncryptionServiceQualifier {

}
