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

/**
 * A Mode B entity with another mapped attribute, for verifying the safety net: a direct write to the plaintext is kept
 * when another change makes the provider flush the instance (#82).
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@EntityListeners(LifecycleListenerEntity.class)
@Access(AccessType.FIELD)
@Entity
@Table(name = "mode_b_note_entity")
public class ModeBNoteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Transient
    public String name;

    @EncryptedAttribute("name")
    @Basic(optional = true)
    @Column(name = "name_enc", nullable = true, length = 2048)
    public byte[] nameEnc__;

    @Basic(optional = true)
    @Column(name = "note", nullable = true)
    public String note;

    public void setName(final String name) {
        this.name = name;
        this.nameEnc__ = null;
    }
}
