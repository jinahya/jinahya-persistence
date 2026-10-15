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
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@DynamicInsert
@DynamicUpdate
@Access(AccessType.FIELD)
@Entity
@Table(name = DynamicSecretEntity.TABLE_NAME)
public class DynamicSecretEntity {

    public static final String TABLE_NAME = "dynamic_secret_entity";

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
