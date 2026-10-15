package com.github.jinahya.persistence.encryption.converter;

import com.github.jinahya.persistence.encryption.ValueCodecs.OfPrimitive;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Encrypts a {@code char[]} attribute into its column, and decrypts it back.
 * <p>
 * The encoding: each {@code char} two bytes, big-endian.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see EncryptedAttributeConverter
 */
@Converter
public final class EncryptedCharsConverter extends EncryptedAttributeConverter<char[]>
        implements AttributeConverter<char[], byte[]> {

    /**
     * Creates a new instance.
     */
    public EncryptedCharsConverter() {
        super(char[].class, OfPrimitive::encodeChars, OfPrimitive::decodeChars);
    }
}
