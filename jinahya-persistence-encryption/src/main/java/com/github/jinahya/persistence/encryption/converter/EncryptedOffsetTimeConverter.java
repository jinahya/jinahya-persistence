package com.github.jinahya.persistence.encryption.converter;

import com.github.jinahya.persistence.encryption.EntityEncryptionService;
import com.github.jinahya.persistence.encryption.converter.ValueCodecs.OfTime;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.lang.invoke.MethodHandles;
import java.time.OffsetTime;

/**
 * Encrypts a {@code OffsetTime} attribute into its column, and decrypts it back.
 * <p>
 * The encoding: twelve bytes, the time then the offset's total seconds.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see EncryptedAttributeConverter
 */
@Converter
public final class EncryptedOffsetTimeConverter extends EncryptedAttributeConverter<OffsetTime>
        implements AttributeConverter<OffsetTime, byte[]> {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    /**
     * Creates a new instance.
     */
    public EncryptedOffsetTimeConverter() {
        this(null);
    }

    EncryptedOffsetTimeConverter(final EntityEncryptionService service) {
        super(OffsetTime.class, OfTime::encodeOffsetTime, OfTime::decodeOffsetTime, service);
    }
}
