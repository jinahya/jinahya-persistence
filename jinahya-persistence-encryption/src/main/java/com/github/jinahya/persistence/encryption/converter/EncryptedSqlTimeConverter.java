package com.github.jinahya.persistence.encryption.converter;

import com.github.jinahya.persistence.encryption.converter.ValueCodecs.OfSql;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Encrypts a {@code java.sql.Time} attribute into its column, and decrypts it back.
 * <p>
 * The encoding: eight bytes, the milliseconds since the epoch.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see EncryptedAttributeConverter
 */
@Converter
public final class EncryptedSqlTimeConverter extends EncryptedAttributeConverter<java.sql.Time>
        implements AttributeConverter<java.sql.Time, byte[]> {

    /**
     * Creates a new instance.
     */
    public EncryptedSqlTimeConverter() {
        super(java.sql.Time.class, OfSql::encodeTime, OfSql::decodeTime);
    }
}
