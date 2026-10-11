package com.github.jinahya.persistence.encryption.examples;

import com.github.jinahya.persistence.encryption.EncryptedAttribute;
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
 * An entity whose {@link EncryptedAttribute} sits on the plaintext and names the ciphertext — the placement before the
 * annotation moved to the ciphertext — for verifying that it is rejected with a hint, rather than misread.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Access(AccessType.FIELD)
@Entity
@Table(name = "plaintext_annotated_entity")
public class PlaintextAnnotatedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @EncryptedAttribute("nameEnc__")
    @Basic(optional = true)
    @Column(name = "name", nullable = true, insertable = false)
    public String name;

    @Basic(optional = true)
    @Column(name = "name_enc", nullable = true, length = 2048)
    public byte[] nameEnc__;
}
