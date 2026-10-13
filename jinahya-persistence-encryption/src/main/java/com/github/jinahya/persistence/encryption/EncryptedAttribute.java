package com.github.jinahya.persistence.encryption;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * An annotation for marking the entity attribute which holds an encrypted value.
 * <p>
 * The annotated attribute is a persistent, {@code byte[]}-typed attribute holding the ciphertext. {@link #value()}
 * names the persistent attribute, of the same entity — or the same embeddable — which holds the plaintext. The
 * plaintext stays mapped to its own column, which a row written before encryption was introduced still holds its value
 * in; when the column is mapped nullable, and the plaintext is not primitive, that column is never written with a value
 * again, and a legacy row is migrated when it is next written. Otherwise the plaintext is left as it is, and an
 * {@code UPDATE} writes it in the clear unless the column is mapped {@code updatable = false}; a primitive plaintext is
 * supported, but strongly discouraged.
 * <pre>{@code
 * @Column(name = "name", insertable = false)
 * private String name;
 *
 * @EncryptedAttribute("name")
 * @Column(name = "name_enc")
 * private byte[] nameEnc;
 * }</pre>
 * <p>
 * The plaintext has to be mapped {@code @Column(insertable = false)}.
 * {@link jakarta.persistence.PrePersist @PrePersist} runs when {@code persist()} is called, not when the {@code INSERT}
 * is built, and Jakarta Persistence has no callback in between; a provider which builds a single statement at commit
 * would otherwise carry a value assigned in that window in the clear. A mapping without it is rejected.
 * <p>
 * An applicable {@link jakarta.persistence.AttributeOverride @AttributeOverride} is resolved, and replaces the member's
 * own {@code @Column} — note that an override which only renames a column restores the annotation default
 * {@code insertable = true}. A mapping expressed in XML is <em>not</em> visible, so a plaintext mapped there cannot be
 * shown to be safe, and is rejected.
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
     * The name of the attribute, of the same entity, which holds the plaintext.
     *
     * @return the name of the plaintext attribute; never blank.
     * @apiNote The annotated attribute has to be optional, has to be typed {@code byte[]}, has to be
     *         {@code BASIC}, has to be insertable, updatable and nullable, and cannot be an identifier or a version.
     *         The named plaintext has to be a persistent, {@code BASIC} attribute, neither an identifier nor a
     *         version, of a type which has a codec, and mapped
     *         {@link jakarta.persistence.Column#insertable() @Column(insertable = false)}; it cannot be the annotated
     *         attribute itself, nor be annotated itself. Before an {@code UPDATE} it is set to {@code null} when, and
     *         only when, it is not primitive and its column is mapped
     *         {@link jakarta.persistence.Column#nullable() nullable}; it then has to be optional and updatable. One
     *         which is not nulled goes out with the {@code UPDATE} in the clear, unless its column is mapped
     *         {@link jakarta.persistence.Column#updatable() @Column(updatable = false)}. Anything else is rejected, on
     *         the first callback for the entity's class. Only the mapping is read, never the database: a column
     *         mapped nullable which the database declares {@code NOT NULL} fails the {@code UPDATE}. Bean Validation
     *         constraints on the plaintext are the application's to keep consistent with this.
     */
    String value();
}
