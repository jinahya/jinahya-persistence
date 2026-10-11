package com.github.jinahya.persistence.crypto.examples;

import com.github.jinahya.persistence.crypto.EncryptedAttribute;
import com.github.jinahya.persistence.crypto.EntityEncryptionListener;
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
import jakarta.persistence.Version;

/**
 * An entity which registers {@link EntityEncryptionListener} itself, not a subclass of it, for verifying that the
 * listener is safe by default.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@EntityListeners(EntityEncryptionListener.class)
@Access(AccessType.FIELD)
@Entity
@Table(name = DirectListenerEntity.TABLE_NAME)
public class DirectListenerEntity {

    public static final String TABLE_NAME = "direct_listener_entity";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long id;

    @Version
    @Column(name = "version")
    public long version;

    @Basic(optional = true)
    @Column(name = "name", nullable = true, insertable = false)
    public String name;

    @EncryptedAttribute("name")
    @Basic(optional = true)
    @Column(name = "name_enc", nullable = true, length = 2048)
    public byte[] nameEnc__;
}
