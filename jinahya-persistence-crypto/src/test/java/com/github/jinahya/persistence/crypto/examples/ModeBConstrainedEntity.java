package com.github.jinahya.persistence.crypto.examples;

import com.github.jinahya.persistence.crypto.EncryptedAttribute;
import com.github.jinahya.persistence.crypto.LifecycleListenerEntity;
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
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Size;

/**
 * A Mode B entity with a Bean Validation constraint on its plaintext, for verifying that the constraint sees the real
 * value: in Mode B nothing nulls the plaintext (#82).
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@EntityListeners(LifecycleListenerEntity.class)
@Access(AccessType.FIELD)
@Entity
@Table(name = "mode_b_constrained_entity")
public class ModeBConstrainedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Size(max = 4)
    @Transient
    public String name;

    @EncryptedAttribute("name")
    @Basic(optional = true)
    @Column(name = "name_enc", nullable = true, length = 2048)
    public byte[] nameEnc__;

    public void setName(final String name) {
        this.name = name;
        this.nameEnc__ = new byte[0]; // stale: shows the provider a change, so that the value is re-encrypted
    }
}
