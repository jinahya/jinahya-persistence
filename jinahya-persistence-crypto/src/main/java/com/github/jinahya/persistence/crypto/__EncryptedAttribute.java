package com.github.jinahya.persistence.crypto;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * An annotation for marking an entity attribute whose value is stored encrypted.
 * <p>
 * The annotated attribute holds the plaintext, and is never written to the database with a value in it; the ciphertext
 * goes to a second, {@code byte[]}-typed attribute of the same entity, named by {@link #encryptedAttribute()}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __EncryptedEntity
 * @see __EncryptionService
 */
@Documented
@Retention(value = RUNTIME)
@Target({
        ElementType.FIELD,
        ElementType.METHOD
})
public @interface __EncryptedAttribute {

    // -----------------------------------------------------------------------------------------------------------------
    /**
     * The name of the attribute, of the same entity, which holds the encrypted bytes.
     *
     * @return the name of the attribute holding the encrypted bytes; an empty string, the default, for the name of the
     *         annotated attribute suffixed with {@code Enc__}.
     * @apiNote The named attribute has to be optional, has to be typed {@code byte[]}, and cannot be the annotated
     * attribute itself.
     */
    String encryptedAttribute() default "";
}
