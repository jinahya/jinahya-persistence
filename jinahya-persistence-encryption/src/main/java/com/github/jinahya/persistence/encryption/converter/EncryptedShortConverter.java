package com.github.jinahya.persistence.encryption.converter;

import com.github.jinahya.persistence.encryption.EntityEncryptionService;
import com.github.jinahya.persistence.encryption.converter.ValueCodecs.OfPrimitive;

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
        this(null);
    }

    EncryptedShortConverter(final @org.jspecify.annotations.Nullable EntityEncryptionService service) {
        super(Short.class, OfPrimitive::encodeShort, OfPrimitive::decodeShort, service);
    }
}
