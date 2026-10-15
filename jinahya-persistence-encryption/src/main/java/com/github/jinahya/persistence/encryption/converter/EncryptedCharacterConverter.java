package com.github.jinahya.persistence.encryption.converter;

import com.github.jinahya.persistence.encryption.converter.ValueCodecs.OfPrimitive;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Encrypts a {@code Character} attribute into its column, and decrypts it back.
 * <p>
 * The encoding: two bytes, big-endian.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see EncryptedAttributeConverter
 */
@Converter
public final class EncryptedCharacterConverter extends EncryptedAttributeConverter<Character>
        implements AttributeConverter<Character, byte[]> {

    /**
     * Creates a new instance.
     */
    public EncryptedCharacterConverter() {
        super(Character.class, OfPrimitive::encodeChar, OfPrimitive::decodeChar);
    }
}
