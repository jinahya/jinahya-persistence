package com.github.jinahya.persistence.crypto;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * An annotation for marking the entity attribute which holds an encrypted value.
 * <p>
 * The annotated attribute is a persistent, {@code byte[]}-typed attribute holding the ciphertext. {@link #value()}
 * names the plaintext, in the same entity — or the same embeddable — which never reaches the database in the clear.
 * Whether the plaintext is mapped decides the mode:
 * <pre>{@code
 * @Transient
 * private String name;
 *
 * @EncryptedAttribute("name")
 * @Column(name = "name_enc")
 * private byte[] nameEnc;
 * }</pre>
 * <dl>
 *   <dt>a {@link jakarta.persistence.Transient @Transient} field — Mode B, the steady state</dt>
 *   <dd>The plaintext has no column, and nothing mapped is ever nulled: the instance keeps its value, and reading it
 *       does not write it. The entity needs a setter which also sets the ciphertext attribute to {@code null}, so that
 *       the provider, which dirty-checks mapped attributes only, sees a change; it is proven at startup.</dd>
 *   <dt>a persistent attribute — Mode A, for encrypting a column which already holds data</dt>
 *   <dd>The plaintext stays mapped to the existing column, which a legacy row still holds its plaintext in; encrypting
 *       nulls it, so that it is migrated as it is written.</dd>
 * </dl>
 * <p>
 * In Mode A, the plaintext has to be mapped {@code @Column(insertable = false)}.
 * {@link jakarta.persistence.PrePersist @PrePersist} runs when {@code persist()} is called, not when the {@code INSERT}
 * is built, and Jakarta Persistence has no callback in between; a provider which builds a single statement at commit
 * would otherwise carry a value assigned in that window in the clear. A mapping without it is rejected.
 * <p>
 * An applicable {@link jakarta.persistence.AttributeOverride @AttributeOverride} is resolved, and replaces the member's
 * own {@code @Column} — note that an override which only renames a column restores the annotation default
 * {@code insertable = true}. A mapping expressed in XML is <em>not</em> visible, so a Mode A plaintext mapped there
 * cannot be shown to be safe, and is rejected.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see EntityEncryptionListener
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
     * The name of the attribute, or of the transient field, of the same entity which holds the plaintext.
     *
     * @return the name of the plaintext; never blank.
     * @apiNote The annotated attribute has to be optional, has to be typed {@code byte[]}, has to be {@code BASIC},
     *         has to be insertable, updatable and nullable, and cannot be an identifier or a version. The named
     *         plaintext cannot be the annotated attribute itself, cannot itself be annotated, has to be of a
     *         non-primitive type which has a codec, and is either a {@link jakarta.persistence.Transient @Transient}
     *         field with a setter which also nulls the annotated attribute (Mode B), or a persistent, optional,
     *         {@code BASIC} attribute, neither an identifier nor a version, carrying no Bean Validation constraint,
     *         and mapped {@link jakarta.persistence.Column#insertable() @Column(insertable = false)}, nullable and
     *         updatable (Mode A) — see {@link EncryptedAttribute the type javadoc}.
     *         Anything else is rejected, on the first callback for the entity's class.
     */
    String value();
}
