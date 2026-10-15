package com.github.jinahya.persistence.encryption.converter;

import com.github.jinahya.persistence.encryption.EntityEncryptionService;
import com.github.jinahya.persistence.encryption.converter.ValueCodecs.OfSql;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Encrypts a {@code java.sql.Timestamp} attribute into its column, and decrypts it back.
 * <p>
 * The encoding: twelve bytes, as an {@code Instant}; the nanos are kept.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see EncryptedAttributeConverter
 */
@Converter
public final class EncryptedSqlTimestampConverter extends EncryptedAttributeConverter<java.sql.Timestamp>
        implements AttributeConverter<java.sql.Timestamp, byte[]> {

    /**
     * Creates a new instance.
     */
    public EncryptedSqlTimestampConverter() {
        this(null);
    }

    EncryptedSqlTimestampConverter(final @org.jspecify.annotations.Nullable EntityEncryptionService service) {
        super(java.sql.Timestamp.class, OfSql::encodeTimestamp, OfSql::decodeTimestamp, service);
    }
}
