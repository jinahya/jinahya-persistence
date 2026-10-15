package com.github.jinahya.persistence.encryption.hibernate;

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
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;

/**
 * An encrypted entity with versionless optimistic locking ({@code DIRTY}); rejected (#105).
 */
@DynamicUpdate
@OptimisticLocking(type = OptimisticLockType.DIRTY)
@Access(AccessType.FIELD)
@Entity
@Table(name = LockDirtySecretEntity.TABLE_NAME)
public class LockDirtySecretEntity {

    public static final String TABLE_NAME = "lock_dirty_secret_entity";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long id;

    @Basic(optional = true)
    @Column(name = "name", nullable = true, insertable = false)
    public String name;

    @EncryptedAttribute("name")
    @Basic(optional = true)
    @Column(name = "name_enc", nullable = true, length = 2048)
    public byte[] nameEnc;
}
