package com.github.jinahya.persistence.encryption.examples;

import com.github.jinahya.persistence.encryption.EncryptedAttribute;
import com.github.jinahya.persistence.encryption.LifecycleListenerEntity;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/**
 * An encrypted entity with a {@code @PreUpdate} callback of its own, which runs after the encryption listener and
 * assigns the plaintext; rejected (#101).
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Access(AccessType.FIELD)
@Entity
@EntityListeners(LifecycleListenerEntity.class)
@Table(name = CallbackSecretEntity.TABLE_NAME)
public class CallbackSecretEntity {

    public static final String TABLE_NAME = "callback_secret_entity";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long id;

    @Basic(optional = true)
    @Column(name = "name", nullable = true, insertable = false)
    public String name;

    @EncryptedAttribute("name")
    @Basic(optional = true)
    @Column(name = "name_enc", nullable = true, length = 2048)
    public byte[] nameEnc;

    @Transient
    public String nameOnPreUpdate;

    @PreUpdate
    private void assignOnPreUpdate() {
        if (nameOnPreUpdate != null) {
            name = nameOnPreUpdate;
        }
    }
}
