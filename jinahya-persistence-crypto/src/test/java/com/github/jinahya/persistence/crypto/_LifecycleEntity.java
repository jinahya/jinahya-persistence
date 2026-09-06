package com.github.jinahya.persistence.crypto;

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
 * An entity for observing when a provider actually writes, and re-writes, the two attributes of an encrypted pair.
 * <p>
 * The {@link #version} is the probe: it increments once per {@code UPDATE}, so a transaction which only reads can be
 * told apart from one which writes.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Access(AccessType.FIELD)
@Entity
@EntityListeners(_LifecycleListener.class)
@Table(name = "lifecycle_entity")
@__EncryptedEntity
public class _LifecycleEntity {

    static final String TABLE_NAME = "lifecycle_entity";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long id;

    @Version
    @Column(name = "version")
    public long version;

    @__EncryptedAttribute
    @Basic(optional = true)
    @Column(name = "name", nullable = true)
    public String name;

    @Basic(optional = true)
    @Column(name = "name_enc", nullable = true, length = 2048)
    public byte[] nameEnc__;
}
