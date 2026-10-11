/**
 * Application-level encryption of entity attributes: selected attributes are encrypted inside the application, so that
 * the database only ever stores their ciphertext.
 * <p>
 * Each encrypted value has two attributes in the same entity: a {@code byte[]}-typed one, annotated with
 * {@link EncryptedAttribute @EncryptedAttribute}, holds the ciphertext; the annotation names the other one, which holds
 * the plaintext and is never written with a value in it. An entity with no annotated attribute passes through
 * untouched.
 *
 * <h2>The pieces</h2>
 * <dl>
 *   <dt>{@link EncryptedAttribute}</dt>
 *   <dd>Marks the {@code byte[]} attribute holding a ciphertext, and names its plaintext.</dd>
 *   <dt>{@link EntityEncryptionService}</dt>
 *   <dd>What an application implements, and provides as a bean: the persistence unit, the encryption identifier of
 *       an instance — that is, which key the instance is encrypted with — and the encryption and decryption.</dd>
 *   <dt>{@link EntityEncryptionListener}</dt>
 *   <dd>The entity listener an application registers, with
 *       {@link jakarta.persistence.EntityListeners @EntityListeners}. It is injected with the service, and does
 *       everything else: it walks the metamodel of an instance, converts each plaintext to bytes by its java type,
 *       hands them to the service, and moves the result between the two attributes, so that an instance is encrypted
 *       before it is written and decrypted after it is read. Embedded attributes are descended into.</dd>
 * </dl>
 * The plaintext stays mapped to its own column, which is never written with a value; a row written before encryption was
 * introduced keeps its plaintext there until it is next written, which migrates it.
 * <p>
 * The mapping of an entity class is validated before any instance of it is touched; each encrypt and decrypt is
 * all-or-nothing per instance. Every encoded value carries a header naming the format version and the codec.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@org.jspecify.annotations.NullMarked
package com.github.jinahya.persistence.encryption;
