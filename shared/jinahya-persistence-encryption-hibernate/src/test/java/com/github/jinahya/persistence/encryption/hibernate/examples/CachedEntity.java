package com.github.jinahya.persistence.encryption.hibernate.examples;

import com.github.jinahya.persistence.encryption.hibernate.Encrypted;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Basic;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Cacheable
@Access(AccessType.FIELD)
@Entity
@Table(name = CachedEntity.TABLE_NAME)
public class CachedEntity {

    public static final String TABLE_NAME = "cached_entity";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long id;

    @Encrypted
    @Basic(optional = true)
    @Column(name = "name_enc", nullable = true, length = 2048)
    public String name;
}
