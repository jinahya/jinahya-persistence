package com.github.jinahya.persistence.encryption.hibernate.examples;

import com.github.jinahya.persistence.encryption.converter.EncryptedStringConverter;
import com.github.jinahya.persistence.encryption.hibernate.Encrypted;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * The two mappings side by side: the rows are the same, so each reads what the other wrote.
 */
@Access(AccessType.FIELD)
@Entity
@Table(name = MixedEntity.TABLE_NAME)
public class MixedEntity {

    public static final String TABLE_NAME = "mixed_entity";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long id;

    @Encrypted
    @Basic(optional = true)
    @Column(name = "native_enc", nullable = true, length = 2048)
    public String nativeName;

    @Convert(converter = EncryptedStringConverter.class)
    @Basic(optional = true)
    @Column(name = "standard_enc", nullable = true, length = 2048)
    public String standardName;
}
