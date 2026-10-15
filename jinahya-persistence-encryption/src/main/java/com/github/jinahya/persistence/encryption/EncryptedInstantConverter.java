package com.github.jinahya.persistence.encryption;

import com.github.jinahya.persistence.encryption.ValueCodecs.OfTime;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.Instant;

/**
 * Encrypts a {@code Instant} attribute into its column, and decrypts it back.
 * <p>
 * The encoding: twelve bytes, the epoch second then the nano.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see EncryptedAttributeConverter
 */
@Converter
public final class EncryptedInstantConverter extends EncryptedAttributeConverter<Instant>
        implements AttributeConverter<Instant, byte[]> {

    /**
     * Creates a new instance.
     */
    public EncryptedInstantConverter() {
        super(Instant.class, OfTime::encodeInstant, OfTime::decodeInstant);
    }
}
