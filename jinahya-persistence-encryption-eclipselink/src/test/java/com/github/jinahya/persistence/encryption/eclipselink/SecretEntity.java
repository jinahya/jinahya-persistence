package com.github.jinahya.persistence.encryption.eclipselink;

import com.github.jinahya.persistence.encryption.EncryptedAttribute;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;

@Access(AccessType.FIELD)
@Entity
@EntityListeners(PreUpdateListener.class)
@Table(name = SecretEntity.TABLE_NAME)
public class SecretEntity {

    public static final String TABLE_NAME = "secret_entity";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long id;

    // incremented by every UPDATE; how the tests see a write
    @Version
    @Column(name = "version")
    public Long version;

    // an ordinary column, for a change which does not touch the secret
    @Basic(optional = true)
    @Column(name = "note", nullable = true)
    public String note;

    @Basic(optional = true)
    @Column(name = "name", nullable = true, insertable = false)
    public String name;

    @EncryptedAttribute("name")
    @Basic(optional = true)
    @Column(name = "name_enc", nullable = true, length = 2048)
    public byte[] nameEnc;

    @Embedded
    public SecretEmbeddable secret;

    // what the entity's own @PreUpdate assigns to name, when not null (#107)
    @Transient
    public String nameOnPreUpdate;

    // what PreUpdateListener assigns to name, when not null (#107)
    @Transient
    public String nameOnListenerPreUpdate;

    @PreUpdate
    void assignOnPreUpdate() {
        if (nameOnPreUpdate != null) {
            name = nameOnPreUpdate;
        }
    }
}
