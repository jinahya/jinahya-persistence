package com.github.jinahya.persistence.encryption.converter;

import com.github.jinahya.persistence.encryption.EntityEncryptionService;
import com.github.jinahya.persistence.encryption.converter.ValueCodecs.OfTime;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.Year;

/**
 * Encrypts a {@code Year} attribute into its column, and decrypts it back.
 * <p>
 * The encoding: four bytes, the value.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see EncryptedAttributeConverter
 */
@Converter
public final class EncryptedYearConverter extends EncryptedAttributeConverter<Year>
        implements AttributeConverter<Year, byte[]> {

    /**
     * Creates a new instance.
     */
    public EncryptedYearConverter() {
        this(null);
    }

    EncryptedYearConverter(final EntityEncryptionService service) {
        super(Year.class, OfTime::encodeYear, OfTime::decodeYear, service);
    }
}
