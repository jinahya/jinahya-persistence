package com.github.jinahya.persistence.encryption;

import jakarta.inject.Inject;
import jakarta.persistence.AttributeConverter;

import java.util.Objects;
import java.util.function.Function;

/**
 * The base of the attribute converters which encrypt an attribute into its column, and decrypt it back: the
 * attribute holds the plaintext, the column holds the ciphertext, and the two meet here only.
 * <pre>{@code
 * @Convert(converter = EncryptedStringConverter.class)
 * @Column(name = "name_enc")          // a binary column; holds the ciphertext only
 * private String name;                // holds the plaintext only
 * }</pre>
 * Going to the column, the value is encoded to bytes by the codec of its type ({@link ValueCodecs}), and the bytes are
 * handed to the application's {@link EntityEncryptionService#encrypt(byte[]) encryption service}; what it returns is
 * what the column stores. Coming back, the stored bytes are handed to {@link EntityEncryptionService#decrypt(byte[])},
 * and the result decoded by the same codec. A {@code null} is a {@code null} in both directions, without a call.
 * <p>
 * The persistence provider does the rest, as for any converted attribute: it dirty-checks the plaintext, so a read
 * never writes; it copies it on {@code merge()}; it validates it; and it never has a plaintext column to write, so
 * nothing in the entity is ever nulled, restored, or touched by a callback.
 * <p>
 * One subclass exists for each supported java type, named {@code Encrypted<Type>Converter}, and the type is matched
 * exactly: an attribute declared {@code Long} uses {@link EncryptedLongConverter}, never a converter of another width.
 * No type information is stored with the bytes, so changing an attribute's declared type, or its converter, is a
 * breaking change for the rows already written. An {@code enum} attribute uses its own subclass of
 * {@link EncryptedEnumConverter}, as a converter has to name its type.
 * <p>
 * The service is injected, when the provider creates its converters through a bean container, and otherwise loaded
 * through {@link java.util.ServiceLoader} on the first conversion.
 *
 * @implNote Each subclass declares {@code implements AttributeConverter<X, byte[]>} again, with its own type:
 *         EclipseLink reads a converter's type arguments from the class's own interface clause only, and derives the
 *         column type from the attribute's java type otherwise, which it then tries to convert the ciphertext to.
 *
 * @param <X> the java type of the attribute.
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see EntityEncryptionService
 * @see ValueCodecs
 */
public abstract class EncryptedAttributeConverter<X> implements AttributeConverter<X, byte[]> {

    /**
     * Creates a new instance with the specified codec.
     *
     * @param javaType the java type of the attribute, for diagnostics.
     * @param encoder  the function which encodes a value into bytes.
     * @param decoder  the function which decodes a value from bytes.
     */
    protected EncryptedAttributeConverter(final Class<X> javaType, final Function<? super X, byte[]> encoder,
                                          final Function<byte[], ? extends X> decoder) {
        super();
        this.javaType = Objects.requireNonNull(javaType, "javaType is null");
        this.encoder = Objects.requireNonNull(encoder, "encoder is null");
        this.decoder = Objects.requireNonNull(decoder, "decoder is null");
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @return the ciphertext of the encoded {@code attribute}; {@code null} for a {@code null} attribute.
     * @throws RuntimeException when the value cannot be encoded, or the service fails, or returns {@code null}.
     * @implNote The message of an exception never carries the value.
     */
    @Override
    public final byte @org.jspecify.annotations.Nullable [] convertToDatabaseColumn(
            final @org.jspecify.annotations.Nullable X attribute) {
        if (attribute == null) {
            return null;
        }
        final byte[] encoded;
        try {
            encoded = encoder.apply(attribute);
        } catch (final RuntimeException re) {
            throw new RuntimeException("cannot encode a value of " + javaType.getName(), re);
        }
        final var encrypted = service().encrypt(encoded);
        if (encrypted == null) {
            throw new RuntimeException("encryption service returned null; java type: " + javaType.getName());
        }
        // a copy: the service may keep, or reuse, the array it returned
        return encrypted.clone();
    }

    /**
     * {@inheritDoc}
     *
     * @return the value decoded from the decrypted {@code dbData}; {@code null} for a {@code NULL} column.
     * @throws RuntimeException when the service fails, or returns {@code null}, or the bytes cannot be decoded.
     */
    @Override
    public final @org.jspecify.annotations.Nullable X convertToEntityAttribute(
            final byte @org.jspecify.annotations.Nullable [] dbData) {
        if (dbData == null) {
            return null;
        }
        // a copy: the service must not be handed the provider's own array
        final var decrypted = service().decrypt(dbData.clone());
        if (decrypted == null) {
            throw new RuntimeException("encryption service returned null; java type: " + javaType.getName());
        }
        try {
            return decoder.apply(decrypted.clone());
        } catch (final RuntimeException re) {
            throw new RuntimeException(
                    "cannot decode a value of " + javaType.getName() + " from " + decrypted.length + " byte(s)", re);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns the encryption service: the one injected, or else the one {@link java.util.ServiceLoader} names.
     *
     * @return the encryption service.
     * @throws IllegalStateException when none is injected and none can be loaded.
     */
    protected final EntityEncryptionService service() {
        var result = service;
        if (result == null) {
            result = service = EntityEncryptionServices.load();
        }
        return result;
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final Class<X> javaType;

    private final Function<? super X, byte[]> encoder;

    private final Function<byte[], ? extends X> decoder;

    // injected when the persistence provider creates converters through a bean container; loaded otherwise
    @Inject
    private volatile @org.jspecify.annotations.Nullable EntityEncryptionService service;
}
