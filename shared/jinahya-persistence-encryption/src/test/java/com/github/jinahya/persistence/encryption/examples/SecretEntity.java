package com.github.jinahya.persistence.encryption.examples;

import com.github.jinahya.persistence.encryption.converter.EncryptedStringConverter;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Size;

/**
 * An entity with one encrypted attribute: the plaintext is {@code name}, its column holds the ciphertext.
 */
@Access(AccessType.FIELD)
@Entity
@Table(name = SecretEntity.TABLE_NAME)
public class SecretEntity {

    public static final String TABLE_NAME = "secret_entity";

    public static final int NAME_MAX = 16;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long id;

    @Version
    @Column(name = "version")
    public Integer version;

    // an ordinary column, for a change which does not touch the secret
    @Basic(optional = true)
    @Column(name = "note", nullable = true)
    public String note;

    // the plaintext; validated as the plaintext, as the entity is never changed under the validator
    @Size(max = NAME_MAX)
    @Convert(converter = EncryptedStringConverter.class)
    @Basic(optional = true)
    @Column(name = "name_enc", nullable = true, length = 2048)
    public String name;

    // an encrypted attribute of an embeddable, in the same table
    @Embedded
    public Secret secret;
}
