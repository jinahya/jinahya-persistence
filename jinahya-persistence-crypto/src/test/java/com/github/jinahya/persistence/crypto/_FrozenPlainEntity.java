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
 * An entity whose plaintext column is not updatable, for verifying that such a mapping is rejected: the update which
 * migrates a legacy row is the only thing which nulls that column.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@EncryptedEntity
@Access(AccessType.FIELD)
@Entity
@Table(name = "frozen_plain_entity")
public class _FrozenPlainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    // looks like the safer mapping, but a legacy row would keep its plaintext forever
    @EncryptedAttribute
    @Basic(optional = true)
    @Column(name = "name", nullable = true, insertable = false, updatable = false)
    public String name;

    @Basic(optional = true)
    @Column(name = "name_enc", nullable = true, length = 2048)
    public byte[] nameEnc__;
}
