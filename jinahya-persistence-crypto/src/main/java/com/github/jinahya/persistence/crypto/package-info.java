/**
 * Application-level encryption of entity attributes: selected attributes are encrypted inside the application, so that
 * the database only ever stores their ciphertext.
 * <p>
 * An entity opts in by being annotated with {@link EncryptedEntity @EncryptedEntity}, and each attribute which is to
 * be stored encrypted with {@link EncryptedAttribute @EncryptedAttribute}. The annotated attribute holds the plaintext
 * and is never written with a value in it; the ciphertext lives in a second, {@code byte[]}-typed attribute of the same
 * entity, named by the annotation or derived from the first attribute's name.
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
 * An annotated {@link jakarta.persistence.Transient @Transient} field is Mode B, the steady state: the plaintext has no
 * column, and nothing mapped is ever nulled. An annotated persistent attribute is Mode A, for encrypting a column which
 * already holds data: the plaintext stays mapped to it, and is nulled as each row is written, migrating it.
 * <p>
 * The mapping is validated before any instance is touched, and at startup under CDI; each encrypt and decrypt is
 * all-or-nothing per instance. Every encoded value carries a header naming the format version and the codec.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@org.jspecify.annotations.NullMarked
package com.github.jinahya.persistence.crypto;
