package com.github.jinahya.persistence.encryption.converter;

import com.github.jinahya.persistence.encryption.EntityEncryptionService;
import com.github.jinahya.persistence.encryption.converter.ValueCodecs.OfPrimitive;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.lang.invoke.MethodHandles;

/**
 * Encrypts a {@code byte[]} attribute into its column, and decrypts it back.
 * <p>
 * The encoding: the bytes, as they are.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see EncryptedAttributeConverter
 */
@Converter
public final class EncryptedBytesConverter extends EncryptedAttributeConverter<byte[]>
        implements AttributeConverter<byte[], byte[]> {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    /**
     * Creates a new instance.
     */
    public EncryptedBytesConverter() {
        this(null);
    }

    EncryptedBytesConverter(final EntityEncryptionService service) {
        super(byte[].class, OfPrimitive::encodeBytes, OfPrimitive::decodeBytes, service);
    }
}
