package com.github.jinahya.persistence.encryption.examples;

import com.github.jinahya.persistence.encryption.converter.EncryptedStringConverter;
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
 * The migration recipe for a table which already holds plaintext: the old column stays mapped, read-only on insert,
 * as {@code legacyName}; a row still holding its value there is moved into {@code name} when it is loaded, which makes
 * the entity dirty, so the next flush stores the ciphertext and nulls the old column. The attribute, and the old
 * column, go once every row is migrated.
 */
@Access(AccessType.FIELD)
@Entity
@Table(name = LegacySecretEntity.TABLE_NAME)
public class LegacySecretEntity {

    public static final String TABLE_NAME = "legacy_secret_entity";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long id;

    @Version
    @Column(name = "version")
    public Integer version;

    @Convert(converter = EncryptedStringConverter.class)
    @Basic(optional = true)
    @Column(name = "name_enc", nullable = true, length = 2048)
    public String name;

    // the old plaintext column: never inserted; nulled by the migrating UPDATE
    @Basic(optional = true)
    @Column(name = "name", nullable = true, insertable = false)
    public String legacyName;

    @PostLoad
    void migrate() {
        if (name == null && legacyName != null) {
            name = legacyName;
            legacyName = null;
        }
    }
}
