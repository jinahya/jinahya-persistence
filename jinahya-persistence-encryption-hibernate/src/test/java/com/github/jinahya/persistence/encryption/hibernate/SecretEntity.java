package com.github.jinahya.persistence.encryption.hibernate;

import com.github.jinahya.persistence.encryption.EncryptedAttribute;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Access(AccessType.FIELD)
@Entity
@Table(name = SecretEntity.TABLE_NAME)
public class SecretEntity {

    public static final String TABLE_NAME = "secret_entity";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long id;

    // an ordinary column, for a change which does not touch the secret
    @Basic(optional = true)
    @Column(name = "note", nullable = true)
    public String note;

    // counted by a @PreUpdate callback, which makes Hibernate ORM copy the state back into the entity
    @Basic(optional = true)
    @Column(name = "updates", nullable = true)
    public Integer updates;

    @Basic(optional = true)
    @Column(name = "name", nullable = true, insertable = false)
    public String name;

    @EncryptedAttribute("name")
    @Basic(optional = true)
    @Column(name = "name_enc", nullable = true, length = 2048)
    public byte[] nameEnc;

    @Embedded
    public SecretEmbeddable secret;

    @PreUpdate
    void onPreUpdate() {
        updates = updates == null ? 1 : updates + 1;
    }
}
