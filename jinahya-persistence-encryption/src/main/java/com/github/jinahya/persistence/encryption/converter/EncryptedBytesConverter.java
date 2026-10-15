package com.github.jinahya.persistence.encryption.converter;

import com.github.jinahya.persistence.encryption.ValueCodecs.OfPrimitive;

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
        super(byte[].class, OfPrimitive::encodeBytes, OfPrimitive::decodeBytes);
    }
}
