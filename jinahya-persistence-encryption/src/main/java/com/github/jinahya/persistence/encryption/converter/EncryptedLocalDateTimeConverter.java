package com.github.jinahya.persistence.encryption.converter;

import com.github.jinahya.persistence.encryption.converter.ValueCodecs.OfTime;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.LocalDateTime;

/**
 * Encrypts a {@code LocalDateTime} attribute into its column, and decrypts it back.
 * <p>
 * The encoding: sixteen bytes, the date then the time.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see EncryptedAttributeConverter
 */
@Converter
public final class EncryptedLocalDateTimeConverter extends EncryptedAttributeConverter<LocalDateTime>
        implements AttributeConverter<LocalDateTime, byte[]> {

    /**
     * Creates a new instance.
     */
    public EncryptedLocalDateTimeConverter() {
        super(LocalDateTime.class, OfTime::encodeLocalDateTime, OfTime::decodeLocalDateTime);
    }
}
