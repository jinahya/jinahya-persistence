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
 * <p>
 * The annotated attribute has to be mapped {@code @Column(insertable = false)}.
 * {@link jakarta.persistence.PrePersist @PrePersist} runs when {@code persist()} is called, not when the {@code INSERT}
 * is built, and Jakarta Persistence has no callback in between; a provider which builds a single statement at commit
 * would otherwise carry a value assigned in that window in the clear. {@link __EncryptionService} rejects a mapping
 * without it.
 * <p>
 * An applicable {@link jakarta.persistence.AttributeOverride @AttributeOverride} is resolved, and replaces the member's
 * own {@code @Column} — note that an override which only renames a column restores the annotation default
 * {@code insertable = true}. A mapping expressed in XML is <em>not</em> visible; see
 * {@code __EncryptionService.resolveColumnRules}.
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
     *         annotated attribute suffixed with
     *         {@value __EncryptedAttributeConstants#DEFAULT_ENCRYPTED_ATTRIBUTE_POSTFIX}.
     * @apiNote The named attribute has to be optional, has to be typed {@code byte[]}, has to be {@code BASIC},
     *         and cannot be the annotated attribute itself, an identifier, a version, or itself annotated. The
     *         annotated attribute has to be optional, {@code BASIC}, neither an identifier nor a version, not of a
     *         primitive type, and mapped {@link jakarta.persistence.Column#insertable() @Column(insertable = false)} —
     *         see {@link __EncryptedAttribute the type javadoc}. {@link __EncryptionService} rejects anything else.
     */
    String encryptedAttribute() default "";
}
