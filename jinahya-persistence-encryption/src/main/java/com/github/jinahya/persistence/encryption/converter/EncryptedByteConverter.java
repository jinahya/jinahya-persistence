package com.github.jinahya.persistence.encryption.converter;

import com.github.jinahya.persistence.encryption.ValueCodecs.OfPrimitive;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Encrypts a {@code Byte} attribute into its column, and decrypts it back.
 * <p>
 * The encoding: one byte, the value.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see EncryptedAttributeConverter
 */
@Converter
public final class EncryptedByteConverter extends EncryptedAttributeConverter<Byte>
        implements AttributeConverter<Byte, byte[]> {

    /**
     * Creates a new instance.
     */
    public EncryptedByteConverter() {
        super(Byte.class, OfPrimitive::encodeByte, OfPrimitive::decodeByte);
    }
}
