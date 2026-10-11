package com.github.jinahya.persistence.encryption.examples;

import com.github.jinahya.persistence.encryption.EncryptedAttribute;
import com.github.jinahya.persistence.encryption.LifecycleListenerEntity;
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
import jakarta.persistence.Version;

/**
 * A Mode B entity: the same table as {@link GuardedEntity}, with the plaintext {@link Transient @Transient}, so that
 * rows written in one mode can be read in the other (#82).
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@EntityListeners(LifecycleListenerEntity.class)
@Access(AccessType.FIELD)
@Entity
@Table(name = GuardedEntity.TABLE_NAME)
public class GuardedModeBEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long id;

    @Version
    @Column(name = "version")
    public long version;

    // the plaintext: no column; the `name` column of the table is left over
    @Transient
    public String name;

    @EncryptedAttribute("name")
    @Basic(optional = true)
    @Column(name = "name_enc", nullable = true, length = 2048)
    public byte[] nameEnc__;

    /**
     * Sets the plaintext, and makes the ciphertext stale, so that the provider sees a change and the next flush
     * re-encrypts.
     *
     * @param name the plaintext.
     */
    public void setName(final String name) {
        this.name = name;
        this.nameEnc__ = new byte[0]; // stale: shows the provider a change, so that the value is re-encrypted
    }
}
