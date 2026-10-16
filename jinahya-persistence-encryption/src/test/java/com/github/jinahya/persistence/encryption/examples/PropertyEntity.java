package com.github.jinahya.persistence.encryption.examples;

import com.github.jinahya.persistence.encryption.converter.EncryptedStringConverter;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * An entity with property access: the converter sits on the getter, not on the field.
 */
@Access(AccessType.PROPERTY)
@Entity
@Table(name = PropertyEntity.TABLE_NAME)
public class PropertyEntity {

    public static final String TABLE_NAME = "property_entity";

    private Long id;

    private String name;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    @Convert(converter = EncryptedStringConverter.class)
    @Basic(optional = true)
    @Column(name = "name_enc", nullable = true, length = 2048)
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
}
