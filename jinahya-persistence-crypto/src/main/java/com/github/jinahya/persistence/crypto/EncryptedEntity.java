package com.github.jinahya.persistence.crypto;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * An annotation for marking an entity class which holds {@link EncryptedAttribute encrypted attributes}.
 * <p>
 * Only instances of an annotated class are encrypted and decrypted; an instance of a class which is not annotated,
 * and has no {@link EncryptedAttribute encrypted attribute}, passes through the listener and the service untouched. A
 * class which has encrypted attributes but is not annotated is rejected, rather than skipped. The annotation is
 * {@link java.lang.annotation.Inherited @Inherited}, so a subclass of an annotated entity is covered as well.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see EncryptedAttribute
 * @see EntityEncryptionListener
 */
@Documented
@Inherited
@Retention(value = RUNTIME)
@Target({
        ElementType.TYPE
})
public @interface EncryptedEntity {
}
