package com.github.jinahya.persistence.encryption.converter;

import com.github.jinahya.persistence.encryption.converter.ValueCodecs.OfMath;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.math.BigDecimal;

/**
 * Encrypts a {@code BigDecimal} attribute into its column, and decrypts it back.
 * <p>
 * The encoding: the scale, four bytes, then the unscaled value.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see EncryptedAttributeConverter
 */
@Converter
public final class EncryptedBigDecimalConverter extends EncryptedAttributeConverter<BigDecimal>
        implements AttributeConverter<BigDecimal, byte[]> {

    /**
     * Creates a new instance.
     */
    public EncryptedBigDecimalConverter() {
        super(BigDecimal.class, OfMath::encodeBigDecimal, OfMath::decodeBigDecimal);
    }
}
