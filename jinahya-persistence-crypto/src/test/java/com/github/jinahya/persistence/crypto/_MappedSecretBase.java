package com.github.jinahya.persistence.crypto;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

/**
 * A mapped superclass declaring an encrypted pair whose plaintext column is insertable, so that an entity inheriting it
 * has to override the column to be acceptable.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Access(AccessType.FIELD)
@MappedSuperclass
public class _MappedSecretBase {

    // insertable defaults to true here; only a class-level override on the entity can make this safe
    @__EncryptedAttribute
    @Basic(optional = true)
    @Column(name = "inherited", nullable = true)
    public String inherited;

    @Basic(optional = true)
    @Column(name = "inherited_enc", nullable = true, length = 2048)
    public byte[] inheritedEnc__;
}
