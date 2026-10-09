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
 * An entity with a valid {@link EncryptedAttribute encrypted attribute}, but <em>not</em> annotated with
 * {@link EncryptedEntity @EncryptedEntity}, for verifying that the forgotten annotation is rejected rather than
 * skipped.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Access(AccessType.FIELD)
@Entity
@Table(name = "unmarked_secret_entity")
public class _UnmarkedSecretEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @EncryptedAttribute
    @Basic(optional = true)
    @Column(name = "name", nullable = true, insertable = false)
    public String name;

    @Basic(optional = true)
    @Column(name = "name_enc", nullable = true, length = 2048)
    public byte[] nameEnc__;
}
