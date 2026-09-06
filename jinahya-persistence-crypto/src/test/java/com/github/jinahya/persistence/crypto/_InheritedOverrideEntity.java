package com.github.jinahya.persistence.crypto;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * An entity overriding, at class level, the column of an attribute inherited from a mapped superclass.
 * <p>
 * The attribute is held directly by this entity, so it is reached with an empty embedding path. An earlier revision
 * returned no override in that case and read the inherited member's own {@code @Column} instead, which both accepted
 * unsafe mappings and rejected safe ones.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Access(AccessType.FIELD)
@Entity
@AttributeOverride(name = "inherited", column = @Column(name = "inherited", nullable = true, insertable = false))
@Table(name = "inherited_override_entity")
public class _InheritedOverrideEntity extends _MappedSecretBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
}
