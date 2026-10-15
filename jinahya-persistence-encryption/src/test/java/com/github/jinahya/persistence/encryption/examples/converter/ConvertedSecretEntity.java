package com.github.jinahya.persistence.encryption.examples.converter;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

/**
 * A sketch: the plaintext is the one attribute, {@code name}, mapped through a converter to the ciphertext column
 * {@code name_enc}. The legacy plaintext column, {@code name}, is mapped read-only-on-insert as {@code legacyName},
 * for the migration phase only: a legacy row's value is moved into {@code name} on load, which makes the entity dirty,
 * so the next flush stores its ciphertext and nulls the legacy column.
 */
@Access(AccessType.FIELD)
@Entity
@Table(name = ConvertedSecretEntity.TABLE_NAME)
public class ConvertedSecretEntity {

    public static final String TABLE_NAME = "converted_secret_entity";

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

    /**
     * The plaintext; the only attribute the application touches. Stored as ciphertext only.
     */
    @Convert(converter = EncryptedStringConverter.class)
    @Basic(optional = true)
    @Column(name = "name_enc", nullable = true, length = 2048)
    public String name;

    /**
     * The ciphertext, as the row holds it: a read-only mirror of the same column. The application never writes it;
     * it is what was loaded, so it is stale after a change of {@code name} until the entity is refreshed.
     */
    @Basic(optional = true)
    @Column(name = "name_enc", nullable = true, insertable = false, updatable = false)
    public byte[] nameEnc;

    /**
     * The legacy plaintext column, during migration only: never inserted, nulled by the migrating UPDATE.
     */
    @Basic(optional = true)
    @Column(name = "name", nullable = true, insertable = false)
    public String legacyName;

    @PostLoad
    void migrateLegacyRow() {
        if (name == null && legacyName != null) {
            name = legacyName;
            legacyName = null;
        }
    }
}
