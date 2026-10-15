package com.github.jinahya.persistence.encryption.hibernate.examples;

import com.github.jinahya.persistence.encryption.hibernate.Encrypted;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import org.hibernate.annotations.DynamicUpdate;

@DynamicUpdate
@Access(AccessType.FIELD)
@Entity
@Table(name = DynamicEntity.TABLE_NAME)
public class DynamicEntity {

    public static final String TABLE_NAME = "dynamic_entity";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long id;

    @Version
    @Column(name = "version")
    public Integer version;

    @Basic(optional = true)
    @Column(name = "note", nullable = true)
    public String note;

    @Encrypted
    @Basic(optional = true)
    @Column(name = "name_enc", nullable = true, length = 2048)
    public String name;
}
