package com.github.jinahya.persistence.encryption.converter;

import com.github.jinahya.persistence.encryption.converter.ValueCodecs.OfMath;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.math.BigInteger;

/**
 * Encrypts a {@code BigInteger} attribute into its column, and decrypts it back.
 * <p>
 * The encoding: {@code toByteArray()}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see EncryptedAttributeConverter
 */
@Converter
public final class EncryptedBigIntegerConverter extends EncryptedAttributeConverter<BigInteger>
        implements AttributeConverter<BigInteger, byte[]> {

    /**
     * Creates a new instance.
     */
    public EncryptedBigIntegerConverter() {
        super(BigInteger.class, OfMath::encodeBigInteger, OfMath::decodeBigInteger);
    }
}
