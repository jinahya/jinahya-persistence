package com.github.jinahya.persistence.encryption.converter;

import com.github.jinahya.persistence.encryption.ValueCodecs.OfPrimitive;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Encrypts a {@code Long} attribute into its column, and decrypts it back.
 * <p>
 * The encoding: eight bytes, big-endian.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see EncryptedAttributeConverter
 */
@Converter
public final class EncryptedLongConverter extends EncryptedAttributeConverter<Long>
        implements AttributeConverter<Long, byte[]> {

    /**
     * Creates a new instance.
     */
    public EncryptedLongConverter() {
        super(Long.class, OfPrimitive::encodeLong, OfPrimitive::decodeLong);
    }
}
