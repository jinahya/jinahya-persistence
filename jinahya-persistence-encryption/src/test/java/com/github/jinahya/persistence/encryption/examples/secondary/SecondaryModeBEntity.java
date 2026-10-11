package com.github.jinahya.persistence.encryption.examples.secondary;

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
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.SecondaryTable;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/**
 * A Mode B entity whose ciphertext lives in a secondary table: the plaintext is {@link Transient @Transient}, and the
 * ciphertext attribute is mapped to a column of {@value #SECONDARY_TABLE_NAME}, joined one-to-one by primary key.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@EntityListeners(LifecycleListenerEntity.class)
@Access(AccessType.FIELD)
@Entity
@Table(name = SecondaryModeBEntity.TABLE_NAME)
@SecondaryTable(name = SecondaryModeBEntity.SECONDARY_TABLE_NAME,
                pkJoinColumns = @PrimaryKeyJoinColumn(name = "id"))
public class SecondaryModeBEntity {

    public static final String TABLE_NAME = "secondary_mode_b_entity";

    public static final String SECONDARY_TABLE_NAME = "secondary_mode_b_secret";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long id;

    // an ordinary column of the primary table, so that a row can exist with no secret at all
    @Basic(optional = true)
    @Column(name = "note", nullable = true)
    public String note;

    @Transient
    public String name;

    @EncryptedAttribute("name")
    @Basic(optional = true)
    @Column(table = SECONDARY_TABLE_NAME, name = "name_enc", nullable = true, length = 2048)
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
