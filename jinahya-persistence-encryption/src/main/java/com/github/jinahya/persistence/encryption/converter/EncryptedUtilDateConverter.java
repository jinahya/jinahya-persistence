package com.github.jinahya.persistence.encryption.converter;

import com.github.jinahya.persistence.encryption.EntityEncryptionService;
import com.github.jinahya.persistence.encryption.converter.ValueCodecs.OfUtil;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Encrypts a {@code java.util.Date} attribute into its column, and decrypts it back.
 * <p>
 * The encoding: eight bytes, the milliseconds since the epoch; exactly {@code java.util.Date}, not a subclass.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see EncryptedAttributeConverter
 */
@Converter
public final class EncryptedUtilDateConverter extends EncryptedAttributeConverter<java.util.Date>
        implements AttributeConverter<java.util.Date, byte[]> {

    /**
     * Creates a new instance.
     */
    public EncryptedUtilDateConverter() {
        this(null);
    }

    EncryptedUtilDateConverter(final EntityEncryptionService service) {
        super(java.util.Date.class, OfUtil::encodeDate, OfUtil::decodeDate, service);
    }
}
