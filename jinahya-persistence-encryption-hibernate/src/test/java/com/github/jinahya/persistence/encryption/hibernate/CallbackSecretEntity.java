package com.github.jinahya.persistence.encryption.hibernate;

import com.github.jinahya.persistence.encryption.EncryptedAttribute;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/**
 * An entity whose own {@code @PreUpdate} callback assigns its plaintext.
 */
@Access(AccessType.FIELD)
@Entity
@Table(name = CallbackSecretEntity.TABLE_NAME)
public class CallbackSecretEntity {

    public static final String TABLE_NAME = "callback_secret_entity";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long id;

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

    // what the callback assigns to name, when not null
    @Transient
    public String nameOnPreUpdate;

    @PreUpdate
    void onPreUpdate() {
        if (nameOnPreUpdate != null) {
            name = nameOnPreUpdate;
        }
    }
}
