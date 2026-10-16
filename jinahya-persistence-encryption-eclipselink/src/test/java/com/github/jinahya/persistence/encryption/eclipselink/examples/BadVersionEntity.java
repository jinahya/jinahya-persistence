package com.github.jinahya.persistence.encryption.eclipselink.examples;

import com.github.jinahya.persistence.encryption.eclipselink.EncryptedConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Converter;

/**
 * An entity whose version is converted by the encrypted converter, which it refuses: the optimistic lock compares the
 * column with a fresh ciphertext, and never matches. In a persistence unit of its own.
 */
@Converter(name = BadVersionEntity.CONVERTER, converterClass = EncryptedConverter.class)
@Entity
@Table(name = "bad_version_entity")
public class BadVersionEntity {

    public static final String CONVERTER = "encrypted";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long id;

    @Convert(CONVERTER)
    @Version
    @Column(name = "version")
    public Integer version;
}
