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
import jakarta.validation.constraints.Size;

/**
 * An entity with a Bean Validation constraint on an encrypted attribute, for verifying that such a mapping is
 * rejected: validation runs after encrypting has cleared the plaintext.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@EncryptedEntity
@Access(AccessType.FIELD)
@Entity
@Table(name = "constrained_secret_entity")
public class _ConstrainedSecretEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    // would pass vacuously against the cleared plaintext, letting a too-long value through unchecked
    @Size(max = 4)
    @EncryptedAttribute
    @Basic(optional = true)
    @Column(name = "name", nullable = true, insertable = false)
    public String name;

    @Basic(optional = true)
    @Column(name = "name_enc", nullable = true, length = 2048)
    public byte[] nameEnc__;
}
