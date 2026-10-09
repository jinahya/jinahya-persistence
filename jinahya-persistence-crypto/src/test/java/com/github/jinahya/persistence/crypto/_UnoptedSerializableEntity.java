package com.github.jinahya.persistence.crypto;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * An entity whose encrypted attribute only Java serialization can encode, without opting in to it, for verifying
 * that such a mapping is rejected.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@EncryptedEntity
@Access(AccessType.FIELD)
@Entity
@Table(name = "unopted_serializable_entity")
public class _UnoptedSerializableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    // no serializable = true
    @EncryptedAttribute
    @Basic(optional = true)
    @Column(name = "opaque", nullable = true, insertable = false)
    public java.io.Serializable opaque;

    @Basic(optional = true)
    @Column(name = "opaque_enc", nullable = true, length = 4096)
    public byte[] opaqueEnc__;
}
