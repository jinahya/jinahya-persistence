package com.github.jinahya.persistence.encryption.examples.converter;

import com.github.jinahya.persistence.encryption.ValueCodecs;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A sketch: application-level encryption as a standard {@link AttributeConverter}. AES-GCM under a fixed key with a
 * random IV, so that encrypting the same value twice yields different bytes; it counts its calls. The value is encoded
 * by the module's own {@link ValueCodecs}; the cryptography here stands for an {@code EntityEncryptionService}.
 */
@Converter
public class EncryptedStringConverter implements AttributeConverter<String, byte[]> {

    private static final SecretKeySpec KEY = new SecretKeySpec(new byte[32], "AES");

    private static final int IV_BYTES = 12;

    private static final SecureRandom RANDOM = new SecureRandom();

    public static final AtomicInteger ENCRYPTED = new AtomicInteger();

    public static final AtomicInteger DECRYPTED = new AtomicInteger();

    @Override
    public byte[] convertToDatabaseColumn(final String attribute) {
        if (attribute == null) {
            return null;
        }
        ENCRYPTED.incrementAndGet();
        final var iv = new byte[IV_BYTES];
        RANDOM.nextBytes(iv);
        try {
            final var cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, KEY, new GCMParameterSpec(128, iv));
            final var encrypted = cipher.doFinal(ValueCodecs.OfLang.encodeString(attribute));
            return ByteBuffer.allocate(IV_BYTES + encrypted.length).put(iv).put(encrypted).array();
        } catch (final GeneralSecurityException gse) {
            throw new RuntimeException(gse);
        }
    }

    @Override
    public String convertToEntityAttribute(final byte[] dbData) {
        if (dbData == null) {
            return null;
        }
        DECRYPTED.incrementAndGet();
        try {
            final var cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, KEY, new GCMParameterSpec(128, dbData, 0, IV_BYTES));
            return ValueCodecs.OfLang.decodeString(cipher.doFinal(dbData, IV_BYTES, dbData.length - IV_BYTES));
        } catch (final GeneralSecurityException gse) {
            throw new RuntimeException(gse);
        }
    }
}
