package com.github.jinahya.persistence.crypto;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * An annotation for marking an entity attribute whose value is stored encrypted.
 * <p>
 * The annotated member holds the plaintext, which never reaches the database; the ciphertext goes to a second,
 * {@code byte[]}-typed attribute of the same entity, named by {@link #encryptedAttribute()}. Where the annotation sits
 * decides the mode:
 * <dl>
 *   <dt>on a {@link jakarta.persistence.Transient @Transient} field — Mode B, the steady state</dt>
 *   <dd>The plaintext has no column, and nothing mapped is ever nulled: the instance keeps its value, and reading it
 *       does not write it. The entity needs a setter which also sets the ciphertext attribute to {@code null}, so that
 *       the provider, which dirty-checks mapped attributes only, sees a change; it is proven at startup.</dd>
 *   <dt>on a persistent attribute — Mode A, for encrypting a column which already holds data</dt>
 *   <dd>The plaintext stays mapped to the existing column, which a legacy row still holds its plaintext in; encrypting
 *       nulls it, so that it is migrated as it is written.</dd>
 * </dl>
 * <p>
 * In Mode A, the annotated attribute has to be mapped {@code @Column(insertable = false)}.
 * {@link jakarta.persistence.PrePersist @PrePersist} runs when {@code persist()} is called, not when the {@code INSERT}
 * is built, and Jakarta Persistence has no callback in between; a provider which builds a single statement at commit
 * would otherwise carry a value assigned in that window in the clear. {@link AbstractEntityEncryptionService} rejects a mapping
 * without it.
 * <p>
 * An applicable {@link jakarta.persistence.AttributeOverride @AttributeOverride} is resolved, and replaces the member's
 * own {@code @Column} — note that an override which only renames a column restores the annotation default
 * {@code insertable = true}. A mapping expressed in XML is <em>not</em> visible; see
 * {@link AbstractEntityEncryptionService#resolveColumnRules(jakarta.persistence.metamodel.ManagedType, java.util.List,
 * jakarta.persistence.metamodel.Attribute) resolveColumnRules}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see EncryptedEntity
 * @see AbstractEntityEncryptionService
 */
@Documented
@Retention(value = RUNTIME)
@Target({
        ElementType.FIELD,
        ElementType.METHOD
})
public @interface EncryptedAttribute {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The name of the attribute, of the same entity, which holds the encrypted bytes.
     *
     * @return the name of the attribute holding the encrypted bytes; an empty string, the default, for the name of the
     *         annotated attribute suffixed with
     *         {@value EncryptedAttributeConstants#DEFAULT_ENCRYPTED_ATTRIBUTE_POSTFIX}.
     * @apiNote The named attribute has to be optional, has to be typed {@code byte[]}, has to be {@code BASIC},
     *         has to be insertable, updatable and nullable, and cannot be the annotated attribute itself, an
     *         identifier, a version, or itself annotated. The annotated attribute has to be a persistent, optional,
     *         {@code BASIC} attribute, neither an identifier nor a version, of a non-primitive type which has a codec,
     *         carrying no Bean Validation constraint, and mapped
     *         {@link jakarta.persistence.Column#insertable() @Column(insertable = false)}, nullable and updatable — see
     *         {@link EncryptedAttribute the type javadoc}. {@link AbstractEntityEncryptionService} rejects anything
     *         else.
     */
    String encryptedAttribute() default "";

}
