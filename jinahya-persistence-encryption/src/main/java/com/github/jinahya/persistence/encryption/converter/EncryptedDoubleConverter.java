package com.github.jinahya.persistence.encryption.converter;

import com.github.jinahya.persistence.encryption.EntityEncryptionService;
import com.github.jinahya.persistence.encryption.converter.ValueCodecs.OfPrimitive;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Encrypts a {@code Double} attribute into its column, and decrypts it back.
 * <p>
 * The encoding: eight bytes, the raw bits, big-endian.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see EncryptedAttributeConverter
 */
@Converter
public final class EncryptedDoubleConverter extends EncryptedAttributeConverter<Double>
        implements AttributeConverter<Double, byte[]> {

    /**
     * Creates a new instance.
     */
    public EncryptedDoubleConverter() {
        this(null);
    }

    EncryptedDoubleConverter(final EntityEncryptionService service) {
        super(Double.class, OfPrimitive::encodeDouble, OfPrimitive::decodeDouble, service);
    }
}
