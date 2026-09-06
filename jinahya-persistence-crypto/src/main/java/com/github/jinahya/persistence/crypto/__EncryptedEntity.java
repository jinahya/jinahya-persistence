package com.github.jinahya.persistence.crypto;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * An annotation for marking an entity class which holds {@link __EncryptedAttribute encrypted attributes}.
 * <p>
 * Only instances of an annotated class are encrypted and decrypted; anything else passes through the listener and the
 * service untouched. The annotation is {@link java.lang.annotation.Inherited @Inherited}, so a subclass of an annotated
 * entity is covered as well.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __EncryptedAttribute
 * @see __EncryptionListener
 */
@Documented
@Inherited
@Retention(value = RUNTIME)
@Target({
        ElementType.TYPE
})
public @interface __EncryptedEntity {

    /**
     * The default name of the attribute holding the encryption identifier. The value is
     * {@value #DEFAULT_ENCRYPTION_IDENTIFIER}.
     */
    String DEFAULT_ENCRYPTION_IDENTIFIER = "encryptionIdentifier__";

    //    String encryptionIdentifierAttribute() default "__encryptionIdentifier";

    /**
     * The name of the attribute which holds the identifier the encryption keys are selected by.
     *
     * @return the name of the attribute holding the encryption identifier; an empty string, the default, for
     *         {@value #DEFAULT_ENCRYPTION_IDENTIFIER}.
     * @apiNote This element is not honored yet: the identifier comes only from
     *         {@link __EncryptionManager#getEncryptionIdentifier(Object)}.
     * @see __EncryptionManager#getEncryptionIdentifier(Object)
     */
    String encryptionIdentifierAttribute() default "";
}
