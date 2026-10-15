package com.github.jinahya.persistence.encryption.converter;

import com.github.jinahya.persistence.encryption.EntityEncryptionService;
import com.github.jinahya.persistence.encryption.converter.ValueCodecs.OfPrimitive;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Encrypts a {@code byte[]} attribute into its column, and decrypts it back.
 * <p>
 * The encoding: the bytes, as they are.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see EncryptedAttributeConverter
 */
@Converter
public final class EncryptedBytesConverter extends EncryptedAttributeConverter<byte[]>
        implements AttributeConverter<byte[], byte[]> {

    /**
     * Creates a new instance.
     */
    public EncryptedBytesConverter() {
        this(null);
    }

    EncryptedBytesConverter(final @org.jspecify.annotations.Nullable EntityEncryptionService service) {
        super(byte[].class, OfPrimitive::encodeBytes, OfPrimitive::decodeBytes, service);
    }
}
