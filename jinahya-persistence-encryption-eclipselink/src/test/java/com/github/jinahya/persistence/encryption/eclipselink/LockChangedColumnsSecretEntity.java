package com.github.jinahya.persistence.encryption.eclipselink;

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
import org.eclipse.persistence.annotations.OptimisticLocking;
import org.eclipse.persistence.annotations.OptimisticLockingType;

/**
 * An encrypted entity with field-based optimistic locking (#105).
 */
@OptimisticLocking(type = OptimisticLockingType.CHANGED_COLUMNS)
@Access(AccessType.FIELD)
@Entity
@Table(name = LockChangedColumnsSecretEntity.TABLE_NAME)
public class LockChangedColumnsSecretEntity {

    public static final String TABLE_NAME = "lock_changed_columns";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long id;

    @Basic(optional = true)
    @Column(name = "note", nullable = true)
    public String note;

    @Basic(optional = true)
    @Column(name = "name", nullable = true, insertable = false)
    public String name;

    @EncryptedAttribute("name")
    @Basic(optional = true)
    @Column(name = "name_enc", nullable = true, length = 2048)
    public byte[] nameEnc;
}
