package com.github.jinahya.persistence.crypto;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * An annotation for marking an embedded attribute which holds {@link __EncryptedAttribute encrypted attributes} of its
 * own.
 * <p>
 * An {@link jakarta.persistence.Embedded @Embedded} attribute is descended into while its owning entity is encrypted,
 * so that attributes nested one level down are covered too.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __EncryptedAttribute
 */
@Documented
@Retention(value = RUNTIME)
@Target({
        ElementType.FIELD,
        ElementType.METHOD
})
public @interface __EncryptedEmbedded {

}
