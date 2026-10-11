package com.github.jinahya.persistence.crypto.examples.hibernate;

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
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 * A Mode B entity under Hibernate ORM's {@link DynamicInsert @DynamicInsert} and {@link DynamicUpdate @DynamicUpdate},
 * which write only the non-{@code null}, and only the changed, columns.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DynamicInsert
@DynamicUpdate
@EntityListeners(LifecycleListenerEntity.class)
@Access(AccessType.FIELD)
@Entity
@Table(name = DynamicModeBEntity.TABLE_NAME)
public class DynamicModeBEntity {

    public static final String TABLE_NAME = "dynamic_mode_b_entity";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long id;

    // an ordinary column, for a change which does not touch the secret
    @Basic(optional = true)
    @Column(name = "note", nullable = true)
    public String note;

    @Transient
    public String name;

    @EncryptedAttribute("name")
    @Basic(optional = true)
    @Column(name = "name_enc", nullable = true, length = 2048)
    public byte[] nameEnc;

    /**
     * Sets the plaintext, and makes the ciphertext stale, so that the provider sees a change.
     *
     * @param name the plaintext.
     */
    public void setName(final String name) {
        this.name = name;
        this.nameEnc = new byte[0]; // stale: shows the provider a change, so that the value is re-encrypted
    }
}
