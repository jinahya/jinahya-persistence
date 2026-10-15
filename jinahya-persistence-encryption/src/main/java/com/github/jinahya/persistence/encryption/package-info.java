/**
 * Application-level encryption of entity attributes, through standard attribute converters: the attribute holds the
 * plaintext, its column holds the ciphertext, and the database never sees the plaintext.
 * <pre>{@code
 * @Convert(converter = EncryptedStringConverter.class)
 * @Column(name = "name_enc")
 * private String name;
 * }</pre>
 *
 * <h2>The pieces</h2>
 * <dl>
 *   <dt>{@link com.github.jinahya.persistence.encryption.converter.EncryptedAttributeConverter} and its subclasses</dt>
 *   <dd>One converter per supported java type, {@code Encrypted<Type>Converter}, which an application names on each
 *       attribute to encrypt. They encode a value to bytes, hand the bytes to the service, and store what it returns;
 *       and the reverse on read.</dd>
 *   <dt>{@link com.github.jinahya.persistence.encryption.EntityEncryptionService}</dt>
 *   <dd>What an application implements, once: the cryptography and the keys, bytes in and bytes out. Injected into
 *       the converters by a bean container, or loaded through {@link java.util.ServiceLoader}.</dd>
 *   <dt>the encodings</dt>
 *   <dd>Fixed per type, documented with each converter, and pinned by tests: stored data, so they never change once
 *       released. They are not an API.</dd>
 * </dl>
 * No type information is stored with the bytes: a value is always decoded by the converter the attribute names now.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@org.jspecify.annotations.NullMarked
package com.github.jinahya.persistence.encryption;
