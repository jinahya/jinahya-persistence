package com.github.jinahya.persistence.encryption.converter;

import com.github.jinahya.persistence.encryption.ValueCodecs.OfLang;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Encrypts a {@code String} attribute into its column, and decrypts it back.
 * <p>
 * The encoding: strict UTF-8.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see EncryptedAttributeConverter
 */
@Converter
public final class EncryptedStringConverter extends EncryptedAttributeConverter<String>
        implements AttributeConverter<String, byte[]> {

    /**
     * Creates a new instance.
     */
    public EncryptedStringConverter() {
        super(String.class, OfLang::encodeString, OfLang::decodeString);
    }
}
