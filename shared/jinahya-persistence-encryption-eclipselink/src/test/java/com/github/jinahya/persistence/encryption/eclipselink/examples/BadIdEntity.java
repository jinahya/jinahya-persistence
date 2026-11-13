package com.github.jinahya.persistence.encryption.eclipselink.examples;

import com.github.jinahya.persistence.encryption.eclipselink.EncryptedConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Converter;

/**
 * An entity whose identifier is converted by the encrypted converter, which it refuses: a randomized ciphertext never
 * matches the key it is looked up by. In a persistence unit of its own, so that refusing it fails nothing else.
 */
@Converter(name = BadIdEntity.CONVERTER, converterClass = EncryptedConverter.class)
@Entity
@Table(name = "bad_id_entity")
public class BadIdEntity {

    public static final String CONVERTER = "encrypted";

    @Convert(CONVERTER)
    @Id
    @Column(name = "id")
    public Long id;
}
