package com.github.jinahya.persistence.encryption.converter;

import com.github.jinahya.persistence.encryption.EntityEncryptionService;
import com.github.jinahya.persistence.encryption.converter.ValueCodecs.OfTime;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.LocalTime;

/**
 * Encrypts a {@code LocalTime} attribute into its column, and decrypts it back.
 * <p>
 * The encoding: eight bytes, the nano of day.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see EncryptedAttributeConverter
 */
@Converter
public final class EncryptedLocalTimeConverter extends EncryptedAttributeConverter<LocalTime>
        implements AttributeConverter<LocalTime, byte[]> {

    /**
     * Creates a new instance.
     */
    public EncryptedLocalTimeConverter() {
        this(null);
    }

    EncryptedLocalTimeConverter(final @org.jspecify.annotations.Nullable EntityEncryptionService service) {
        super(LocalTime.class, OfTime::encodeLocalTime, OfTime::decodeLocalTime, service);
    }
}
