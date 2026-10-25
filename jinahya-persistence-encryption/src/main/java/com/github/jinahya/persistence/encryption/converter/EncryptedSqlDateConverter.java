package com.github.jinahya.persistence.encryption.converter;

import com.github.jinahya.persistence.encryption.EntityEncryptionService;
import com.github.jinahya.persistence.encryption.converter.ValueCodecs.OfSql;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Encrypts a {@code java.sql.Date} attribute into its column, and decrypts it back.
 * <p>
 * The encoding: eight bytes, the milliseconds since the epoch.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see EncryptedAttributeConverter
 */
@Converter
public final class EncryptedSqlDateConverter extends EncryptedAttributeConverter<java.sql.Date>
        implements AttributeConverter<java.sql.Date, byte[]> {

    /**
     * Creates a new instance.
     */
    public EncryptedSqlDateConverter() {
        this(null);
    }

    EncryptedSqlDateConverter(final EntityEncryptionService service) {
        super(java.sql.Date.class, OfSql::encodeDate, OfSql::decodeDate, service);
    }
}
