package com.github.jinahya.persistence.encryption;

import com.github.jinahya.persistence.encryption.ValueCodecs.OfPrimitive;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Encrypts a {@code Short} attribute into its column, and decrypts it back.
 * <p>
 * The encoding: two bytes, big-endian.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see EncryptedAttributeConverter
 */
@Converter
public final class EncryptedShortConverter extends EncryptedAttributeConverter<Short>
        implements AttributeConverter<Short, byte[]> {

    /**
     * Creates a new instance.
     */
    public EncryptedShortConverter() {
        super(Short.class, OfPrimitive::encodeShort, OfPrimitive::decodeShort);
    }
}
