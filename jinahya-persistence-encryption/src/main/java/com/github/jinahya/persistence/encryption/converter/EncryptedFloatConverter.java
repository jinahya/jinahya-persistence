package com.github.jinahya.persistence.encryption.converter;

import com.github.jinahya.persistence.encryption.EntityEncryptionService;
import com.github.jinahya.persistence.encryption.converter.ValueCodecs.OfPrimitive;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.lang.invoke.MethodHandles;

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

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    /**
     * Creates a new instance.
     */
    public EncryptedFloatConverter() {
        this(null);
    }

    EncryptedFloatConverter(final EntityEncryptionService service) {
        super(Float.class, OfPrimitive::encodeFloat, OfPrimitive::decodeFloat, service);
    }
}
