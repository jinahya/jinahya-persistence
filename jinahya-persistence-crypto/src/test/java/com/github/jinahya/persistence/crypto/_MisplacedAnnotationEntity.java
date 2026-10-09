package com.github.jinahya.persistence.crypto;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * An entity under field access whose {@link EncryptedAttribute} sits on a getter, which the provider does not read,
 * for verifying that it is rejected rather than silently ignored (#70).
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@EncryptedEntity
@Access(AccessType.FIELD)
@Entity
@Table(name = "misplaced_annotation_entity")
public class _MisplacedAnnotationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Basic(optional = true)
    @Column(name = "name", nullable = true, insertable = false)
    public String name;

    @Basic(optional = true)
    @Column(name = "name_enc", nullable = true, length = 2048)
    public byte[] nameEnc__;

    // field access: the provider reads the field, never this getter
    @EncryptedAttribute
    public String getName() {
        return name;
    }
}
