package com.github.jinahya.persistence.encryption.converter;

import com.github.jinahya.persistence.encryption.ValueCodecs.OfTime;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.OffsetDateTime;

/**
 * Encrypts a {@code OffsetDateTime} attribute into its column, and decrypts it back.
 * <p>
 * The encoding: twenty bytes, the date and time then the offset's total seconds.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see EncryptedAttributeConverter
 */
@Converter
public final class EncryptedOffsetDateTimeConverter extends EncryptedAttributeConverter<OffsetDateTime>
        implements AttributeConverter<OffsetDateTime, byte[]> {

    /**
     * Creates a new instance.
     */
    public EncryptedOffsetDateTimeConverter() {
        super(OffsetDateTime.class, OfTime::encodeOffsetDateTime, OfTime::decodeOffsetDateTime);
    }
}
