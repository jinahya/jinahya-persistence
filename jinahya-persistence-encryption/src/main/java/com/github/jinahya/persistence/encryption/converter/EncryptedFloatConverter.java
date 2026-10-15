package com.github.jinahya.persistence.encryption.converter;

import com.github.jinahya.persistence.encryption.ValueCodecs.OfPrimitive;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Encrypts a {@code Float} attribute into its column, and decrypts it back.
 * <p>
 * The encoding: four bytes, the raw bits, big-endian.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see EncryptedAttributeConverter
 */
@Converter
public final class EncryptedFloatConverter extends EncryptedAttributeConverter<Float>
        implements AttributeConverter<Float, byte[]> {

    /**
     * Creates a new instance.
     */
    public EncryptedFloatConverter() {
        super(Float.class, OfPrimitive::encodeFloat, OfPrimitive::decodeFloat);
    }
}
