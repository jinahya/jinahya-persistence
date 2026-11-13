package com.github.jinahya.persistence.encryption.eclipselink.examples;

import com.github.jinahya.persistence.encryption.eclipselink.EncryptedConverter;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Size;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Converter;
import org.eclipse.persistence.annotations.Mutable;

/**
 * Registers the named converter, once for the persistence unit, and uses it.
 */
@Converter(name = SecretEntity.CONVERTER, converterClass = EncryptedConverter.class)
@Access(AccessType.FIELD)
@Entity
@Table(name = SecretEntity.TABLE_NAME)
public class SecretEntity {

    public static final String TABLE_NAME = "secret_entity";

    public static final String CONVERTER = "encrypted";

    public static final int NAME_MAX = 16;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long id;

    @Version
    @Column(name = "version")
    public Integer version;

    @Basic(optional = true)
    @Column(name = "note", nullable = true)
    public String note;

    @Size(max = NAME_MAX)
    @Convert(CONVERTER)
    @Basic(optional = true)
    @Column(name = "name_enc", nullable = true, length = 2048)
    public String name;

    // a mutable value, changed in place; not @Mutable: the change is not detected
    @Convert(CONVERTER)
    @Basic(optional = true)
    @Column(name = "bytes_enc", nullable = true, length = 2048)
    public byte[] bytes;

    // a mutable value, changed in place; @Mutable: copied, and the change detected
    @Mutable
    @Convert(CONVERTER)
    @Basic(optional = true)
    @Column(name = "when_enc", nullable = true, length = 2048)
    public java.util.Date when;

    // an encrypted attribute of an embeddable, in the same table
    @Embedded
    public Secret secret;
}
