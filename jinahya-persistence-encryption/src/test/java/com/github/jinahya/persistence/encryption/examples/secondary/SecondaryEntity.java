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

/**
 * An entity whose ciphertext lives in a secondary table: the plaintext stays mapped to its column of the primary
 * table, which a legacy row still holds its value in, and the ciphertext attribute is mapped to a column of
 * {@value #SECONDARY_TABLE_NAME}, joined one-to-one by primary key.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@EntityListeners(LifecycleListenerEntity.class)
@Access(AccessType.FIELD)
@Entity
@Table(name = SecondaryEntity.TABLE_NAME)
@SecondaryTable(name = SecondaryEntity.SECONDARY_TABLE_NAME,
                pkJoinColumns = @PrimaryKeyJoinColumn(name = "id"))
public class SecondaryEntity {

    public static final String TABLE_NAME = "secondary_entity";

    public static final String SECONDARY_TABLE_NAME = "secondary_secret";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long id;

    // an ordinary column of the primary table, for writing a row without touching the secret
    @Basic(optional = true)
    @Column(name = "note", nullable = true)
    public String note;

    // the existing plaintext column, in the primary table
    @Basic(optional = true)
    @Column(name = "name", nullable = true, insertable = false)
    public String name;

    @EncryptedAttribute("name")
    @Basic(optional = true)
    @Column(table = SECONDARY_TABLE_NAME, name = "name_enc", nullable = true, length = 2048)
    public byte[] nameEnc;
}
