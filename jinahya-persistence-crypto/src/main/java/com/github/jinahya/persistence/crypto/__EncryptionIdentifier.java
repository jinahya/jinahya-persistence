package com.github.jinahya.persistence.crypto;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * An annotation for marking the attribute, or the type, which carries the identifier the encryption keys are selected
 * by.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @apiNote This annotation is not honored yet: the identifier comes only from
 *         {@link __EncryptionManager#getEncryptionIdentifier(Object)}.
 * @see __EncryptedEntity#encryptionIdentifierAttribute()
 * @see __EncryptionManager#getEncryptionIdentifier(Object)
 */
@Documented
@Retention(value = RUNTIME)
@Target({
        ElementType.METHOD,
        ElementType.TYPE
})
public @interface __EncryptionIdentifier {

}
