package com.github.jinahya.persistence.encryption.converter;

import com.github.jinahya.persistence.encryption.EntityEncryptionService;
import com.github.jinahya.persistence.encryption.converter.ValueCodecs.OfTime;

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
        this(null);
    }

    EncryptedInstantConverter(final @org.jspecify.annotations.Nullable EntityEncryptionService service) {
        super(Instant.class, OfTime::encodeInstant, OfTime::decodeInstant, service);
    }
}
