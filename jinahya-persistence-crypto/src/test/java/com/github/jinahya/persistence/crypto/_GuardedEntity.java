package com.github.jinahya.persistence.crypto;

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
import jakarta.persistence.Version;

/**
 * The same as {@link _LifecycleEntity}, except that the plaintext column is declared
 * {@link jakarta.persistence.Column#insertable() insertable = false}, so that no {@code INSERT} can carry it however
 * the entity is mutated between {@code persist()} and the statement.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Access(AccessType.FIELD)
@Entity
@EntityListeners(_LifecycleListener.class)
@Table(name = "guarded_entity")
@__EncryptedEntity
public class _GuardedEntity {

    static final String TABLE_NAME = "guarded_entity";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long id;

    @Version
    @Column(name = "version")
    public long version;

    @__EncryptedAttribute
    @Basic(optional = true)
    @Column(name = "name", nullable = true, insertable = false, updatable = true)
    public String name;

    @Basic(optional = true)
    @Column(name = "name_enc", nullable = true, length = 2048)
    public byte[] nameEnc__;
}
