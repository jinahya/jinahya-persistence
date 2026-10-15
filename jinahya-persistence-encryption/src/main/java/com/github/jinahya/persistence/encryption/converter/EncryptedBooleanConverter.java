package com.github.jinahya.persistence.encryption.converter;

import com.github.jinahya.persistence.encryption.converter.ValueCodecs.OfPrimitive;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Encrypts a {@code Boolean} attribute into its column, and decrypts it back.
 * <p>
 * The encoding: one byte: {@code 0} for {@code false}, {@code 1} for {@code true}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see EncryptedAttributeConverter
 */
@Converter
public final class EncryptedBooleanConverter extends EncryptedAttributeConverter<Boolean>
        implements AttributeConverter<Boolean, byte[]> {

    /**
     * Creates a new instance.
     */
    public EncryptedBooleanConverter() {
        super(Boolean.class, OfPrimitive::encodeBoolean, OfPrimitive::decodeBoolean);
    }
}
