package com.github.jinahya.persistence.encryption;

/**
 * The service an application provides for encrypting its entity attributes: the cryptography, and the keys.
 * <p>
 * This is the only type an application implements. The module's {@link EncryptedAttributeConverter attribute
 * converters} turn an attribute's value into bytes, hand them to {@link #encrypt(byte[])} before the row is written,
 * and hand what the row holds to {@link #decrypt(byte[])} after it is read. The module never looks inside the bytes a
 * service returns: the algorithm, the keys, which key a value was encrypted with, the IV, the authentication tag, and
 * any versioning, all belong to the service, inside the bytes it returns.
 * <p>
 * <strong>Provide it once.</strong> A converter is created by the persistence provider, and finds the service in one of
 * two ways: injected, when the provider creates its converters through a bean container, a Jakarta EE container or a
 * CDI {@code BeanManager} handed to the provider as {@code jakarta.persistence.bean.manager}; or, otherwise, loaded
 * through {@link java.util.ServiceLoader}, from a {@code META-INF/services/} entry naming the implementation, which
 * then needs a public no-arg constructor. Exactly one implementation has to be found that way.
 * <p>
 * <strong>Thread safety.</strong> One instance serves every conversion of every persistence context, concurrently.
 * A {@link javax.crypto.Cipher} is not thread-safe, so an implementation creates one per call, or pools them; never
 * a field of the service.
 * <p>
 * <strong>{@code null} is {@code null}.</strong> An absent value never reaches this service: a {@code null} attribute
 * is stored as {@code NULL}, and a {@code NULL} column read as a {@code null} attribute, without a call. Every array
 * this service is given is non-{@code null} and a copy of the module's own, and every array it returns has to be
 * non-{@code null}; it is copied before it is used.
 * <p>
 * <strong>The service sees bytes only.</strong> It is not told which entity, attribute or row a value belongs to.
 * Whatever selects a key, a tenant for instance, comes from the application's own context, and what a stored value was
 * encrypted with is recorded by the service itself, inside the bytes.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see EncryptedAttributeConverter
 */
public interface EntityEncryptionService {

    /**
     * Encrypts the specified bytes, the encoding of one attribute's value.
     * <p>
     * Called only for a value which is present. Each call needs what the cipher suite requires to be fresh: an AES-GCM
     * nonce, for one, must never repeat under the same key.
     *
     * @param decryptedBytes the bytes to encrypt; never {@code null}, possibly empty.
     * @return the bytes to store, to be given back to {@link #decrypt(byte[])}; never {@code null}.
     */
    byte[] encrypt(byte[] decryptedBytes);

    /**
     * Decrypts the specified bytes, the stored form of one attribute's value, as {@link #encrypt(byte[])} returned
     * them.
     * <p>
     * Called only for a value which is present.
     *
     * @param encryptedBytes the stored bytes; never {@code null}.
     * @return the bytes {@link #encrypt(byte[])} was given; never {@code null}.
     */
    byte[] decrypt(byte[] encryptedBytes);
}
