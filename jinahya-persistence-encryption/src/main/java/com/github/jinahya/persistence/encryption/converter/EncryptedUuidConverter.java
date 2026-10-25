package com.github.jinahya.persistence.encryption.converter;

import com.github.jinahya.persistence.encryption.EntityEncryptionService;
import com.github.jinahya.persistence.encryption.converter.ValueCodecs.OfUtil;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.UUID;

/**
 * Encrypts a {@code UUID} attribute into its column, and decrypts it back.
 * <p>
 * The encoding: sixteen bytes, the most significant then the least significant.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see EncryptedAttributeConverter
 */
@Converter
public final class EncryptedUuidConverter extends EncryptedAttributeConverter<UUID>
        implements AttributeConverter<UUID, byte[]> {

    /**
     * Creates a new instance.
     */
    public EncryptedUuidConverter() {
        this(null);
    }

    EncryptedUuidConverter(final EntityEncryptionService service) {
        super(UUID.class, OfUtil::encodeUuid, OfUtil::decodeUuid, service);
    }
}
