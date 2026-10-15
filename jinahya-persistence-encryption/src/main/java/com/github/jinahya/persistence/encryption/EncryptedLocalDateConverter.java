package com.github.jinahya.persistence.encryption;

import com.github.jinahya.persistence.encryption.ValueCodecs.OfTime;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.LocalDate;

/**
 * Encrypts a {@code LocalDate} attribute into its column, and decrypts it back.
 * <p>
 * The encoding: eight bytes, the epoch day.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see EncryptedAttributeConverter
 */
@Converter
public final class EncryptedLocalDateConverter extends EncryptedAttributeConverter<LocalDate>
        implements AttributeConverter<LocalDate, byte[]> {

    /**
     * Creates a new instance.
     */
    public EncryptedLocalDateConverter() {
        super(LocalDate.class, OfTime::encodeLocalDate, OfTime::decodeLocalDate);
    }
}
