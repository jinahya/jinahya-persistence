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
import jakarta.persistence.Table;

/**
 * A Mode A entity with an ordinary column besides its encrypted pair, so that a legacy row can be written without its
 * plaintext being touched.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@EntityListeners(LifecycleListenerEntity.class)
@Access(AccessType.FIELD)
@Entity
@Table(name = NotedModeAEntity.TABLE_NAME)
public class NotedModeAEntity {

    public static final String TABLE_NAME = "noted_mode_a_entity";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long id;

    // an ordinary column, for a write which does not touch the secret
    @Basic(optional = true)
    @Column(name = "note", nullable = true)
    public String note;

    // the existing plaintext column
    @Basic(optional = true)
    @Column(name = "name", nullable = true, insertable = false)
    public String name;

    @EncryptedAttribute("name")
    @Basic(optional = true)
    @Column(name = "name_enc", nullable = true, length = 2048)
    public byte[] nameEnc;
}
