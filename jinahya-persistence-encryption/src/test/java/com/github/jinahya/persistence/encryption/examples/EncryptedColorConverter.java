package com.github.jinahya.persistence.encryption.examples;

import com.github.jinahya.persistence.encryption.EncryptedEnumConverter;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * The converter an application declares for one of its enum types; the interface is declared again, with the type,
 * for EclipseLink.
 */
@Converter
public class EncryptedColorConverter extends EncryptedEnumConverter<Color>
        implements AttributeConverter<Color, byte[]> {

    public EncryptedColorConverter() {
        super(Color.class);
    }
}
