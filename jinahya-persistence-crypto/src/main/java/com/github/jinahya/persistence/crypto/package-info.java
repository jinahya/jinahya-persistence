/**
 * Interfaces, annotations and classes for encrypting entity attributes at rest.
 * <p>
 * An entity opts in by being annotated with
 * {@link com.github.jinahya.persistence.crypto.__EncryptedEntity @__EncryptedEntity}, and each attribute which is to
 * be stored encrypted with
 * {@link com.github.jinahya.persistence.crypto.__EncryptedAttribute @__EncryptedAttribute}. The annotated attribute
 * holds the plaintext and is never written with a value in it; the ciphertext lives in a second, {@code byte[]}-typed
 * attribute of the same entity, named by the annotation or derived from the first attribute's name.
 *
 * <h2>The pieces</h2>
 * <dl>
 *   <dt>{@link com.github.jinahya.persistence.crypto.__EncryptionManager}</dt>
 *   <dd>The cryptographic half, which an application implements: it derives the encryption identifier of an instance —
 *       that is, which key the instance is encrypted with — and performs the encryption and decryption.</dd>
 *   <dt>{@link com.github.jinahya.persistence.crypto.__EncryptionService}</dt>
 *   <dd>The mapping half: it walks the metamodel of an instance, converts each annotated attribute's value to bytes by
 *       its java type, hands them to the manager, and moves the result between the two attributes. Embedded
 *       attributes are descended into.</dd>
 *   <dt>{@link com.github.jinahya.persistence.crypto.__EncryptionListener}</dt>
 *   <dd>The entity listener which ties the service to the entity life cycle, so that an instance is encrypted before
 *       it is written and decrypted after it is read.</dd>
 * </dl>
 * {@link com.github.jinahya.persistence.crypto.__SecureAttributeConveter} offers the alternative shape — a single
 * attribute converter, needing no second column — and is not implemented yet.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
//@org.jspecify.annotations.NullMarked
package com.github.jinahya.persistence.crypto;
