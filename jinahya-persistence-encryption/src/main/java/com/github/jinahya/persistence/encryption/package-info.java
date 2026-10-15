/**
 * Application-level encryption of entity attributes: selected attributes are encrypted inside the application, so that
 * the database only ever stores their ciphertext.
 * <p>
 * Each encrypted value has two attributes in the same entity: a {@code byte[]}-typed one, annotated with
 * {@link EncryptedAttribute @EncryptedAttribute}, holds the ciphertext; the annotation names the other one, which holds
 * the plaintext and, when its column is mapped nullable, is never written with a value in it. An entity with no
 * annotated attribute passes through untouched.
 *
 * <h2>The pieces</h2>
 * <dl>
 *   <dt>{@link EncryptedAttribute}</dt>
 *   <dd>Marks the {@code byte[]} attribute holding a ciphertext, and names its plaintext.</dd>
 *   <dt>{@link EntityEncryptionService}</dt>
 *   <dd>What an application implements, and provides as a bean: the persistence unit, the keys an instance is
 *       encrypted with — selected once per instance, in its {@link EntityEncryptionContext context} — and the
 *       encryption and decryption.</dd>
 *   <dt>{@link EntityEncryptionContext}</dt>
 *   <dd>The context of one instance, for one encryption, or decryption, of it: what the service prepared for that
 *       instance travels in it from one attribute to the next.</dd>
 *   <dt>{@link EntityEncryptionListener}</dt>
 *   <dd>The entity listener an application registers, with
 *       {@link jakarta.persistence.EntityListeners @EntityListeners}. It is injected with the service, and does
 *       everything else: it walks the metamodel of an instance, converts each plaintext to bytes by its java type,
 *       hands them to the service, and moves the result between the two attributes, so that an instance is encrypted
 *       before it is written and decrypted after it is read. Embedded attributes are descended into.</dd>
 * </dl>
 * The plaintext stays mapped to its own column. Before an {@code UPDATE} it is set to {@code null} when, and only when,
 * that column is mapped nullable and the plaintext is not primitive, so that the column is never written with a value;
 * a row written before encryption was introduced keeps its plaintext there until it is next written, which migrates
 * it. Any other plaintext is left as it is: nothing is written which the mapping does not allow, as no Bean Validation
 * constraint is checked.
 * <p>
 * The mapping of an entity class is validated before any instance of it is touched; each encrypt and decrypt is
 * all-or-nothing per instance. An encoded value carries no type information: it is decoded by the attribute's declared
 * java type.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@org.jspecify.annotations.NullMarked
package com.github.jinahya.persistence.encryption;
