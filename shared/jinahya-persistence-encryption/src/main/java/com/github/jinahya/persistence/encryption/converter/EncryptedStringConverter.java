package com.github.jinahya.persistence.encryption.converter;

import com.github.jinahya.persistence.encryption.EntityEncryptionService;
import com.github.jinahya.persistence.encryption.converter.ValueCodecs.OfLang;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.lang.invoke.MethodHandles;

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

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    /**
     * Creates a new instance.
     */
    public EncryptedStringConverter() {
        this(null);
    }

    EncryptedStringConverter(final EntityEncryptionService service) {
        super(String.class, OfLang::encodeString, OfLang::decodeString, service);
    }
}
