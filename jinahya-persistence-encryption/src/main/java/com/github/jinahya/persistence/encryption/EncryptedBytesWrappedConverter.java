package com.github.jinahya.persistence.encryption;

import com.github.jinahya.persistence.encryption.ValueCodecs.OfLang;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Encrypts a {@code Byte[]} attribute into its column, and decrypts it back.
 * <p>
 * The encoding: unboxed; a {@code null} element is rejected.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see EncryptedAttributeConverter
 */
@Converter
public final class EncryptedBytesWrappedConverter extends EncryptedAttributeConverter<Byte[]>
        implements AttributeConverter<Byte[], byte[]> {

    /**
     * Creates a new instance.
     */
    public EncryptedBytesWrappedConverter() {
        super(Byte[].class, OfLang::encodeBytesWrapped, OfLang::decodeBytesWrapped);
    }
}
