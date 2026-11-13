package com.github.jinahya.persistence.encryption.hibernate.examples;

import com.github.jinahya.persistence.encryption.converter.EncryptedStringConverter;
import com.github.jinahya.persistence.encryption.hibernate.Encrypted;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * The native and the standard mapping, each on a column which declares no length: what the DDL gives each.
 */
@Entity
@Table(name = DefaultLengthEntity.TABLE_NAME)
public class DefaultLengthEntity {

    public static final String TABLE_NAME = "default_length_entity";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long id;

    @Encrypted
    @Basic(optional = true)
    @Column(name = "native_enc", nullable = true)
    public String nativeName;

    @Convert(converter = EncryptedStringConverter.class)
    @Basic(optional = true)
    @Column(name = "standard_enc", nullable = true)
    public String standardName;
}
