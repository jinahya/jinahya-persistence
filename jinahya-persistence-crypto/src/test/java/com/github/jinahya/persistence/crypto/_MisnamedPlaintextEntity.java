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
 * An entity whose {@link EncryptedAttribute} names a plaintext which does not exist, for verifying that it is
 * rejected rather than skipped.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@EncryptedEntity
@Access(AccessType.FIELD)
@Entity
@Table(name = "misnamed_plaintext_entity")
public class _MisnamedPlaintextEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Basic(optional = true)
    @Column(name = "name", nullable = true, insertable = false)
    public String name;

    @EncryptedAttribute("nmae")
    @Basic(optional = true)
    @Column(name = "name_enc", nullable = true, length = 2048)
    public byte[] nameEnc__;
}
