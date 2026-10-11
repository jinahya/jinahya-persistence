/**
 * Application-level encryption of entity attributes: selected attributes are encrypted inside the application, so that
 * the database only ever stores their ciphertext.
 * <p>
 * Each encrypted value has two attributes in the same entity: a {@code byte[]}-typed one, annotated with
 * {@link EncryptedAttribute @EncryptedAttribute}, holds the ciphertext; the annotation names the other one, which holds
 * the plaintext and is never written with a value in it. An entity with no annotated attribute passes through untouched.
 *
 * <h2>The pieces</h2>
 * <dl>
 *   <dt>{@link EntityEncryptionManager}</dt>
 *   <dd>The cryptographic half, which an application implements: it derives the encryption identifier of an instance —
 *       that is, which key the instance is encrypted with — and performs the encryption and decryption.</dd>
 *   <dt>{@link AbstractEntityEncryptionService}</dt>
 *   <dd>The mapping half: it walks the metamodel of an instance, converts each annotated attribute's value to bytes by
 *       its java type, hands them to the manager, and moves the result between the two attributes. Embedded
 *       attributes are descended into.</dd>
 *   <dt>{@link EntityEncryptionListener}</dt>
 *   <dd>The entity listener which ties the service to the entity life cycle, so that an instance is encrypted before
 *       it is written and decrypted after it is read. Registered directly, with
 *       {@link jakarta.persistence.EntityListeners @EntityListeners}; it takes the service from CDI.</dd>
 * </dl>
 * A {@link jakarta.persistence.Transient @Transient} plaintext is Mode B, the steady state: it has no column, and
 * nothing mapped is ever nulled. A persistent plaintext is Mode A, for encrypting a column which already holds data:
 * the plaintext stays mapped to it, and is nulled as each row is written, migrating it.
 * <p>
 * The mapping is validated before any instance is touched, and at startup under CDI; each encrypt and decrypt is
 * all-or-nothing per instance. Every encoded value carries a header naming the format version and the codec.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@org.jspecify.annotations.NullMarked
package com.github.jinahya.persistence.crypto;
