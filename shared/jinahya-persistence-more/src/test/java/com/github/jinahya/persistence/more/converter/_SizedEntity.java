package com.github.jinahya.persistence.more.converter;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * An entity with an attribute of an enum, {@link _Size}, and another of a shared interface, {@link _ISize}, so that
 * the provider is what instantiates and applies {@link AttributeEnumConverter3}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Entity
@Table(name = "sized_entity")
public class _SizedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @Convert(converter = _SizeConverters.OfSize.class)
    @Basic(optional = true)
    @Column(name = "shirt_size", nullable = true, length = 16)
    public _Size size;

    @Convert(converter = _SizeConverters.OfISize.class)
    @Basic(optional = true)
    @Column(name = "any_size", nullable = true, length = 16)
    public _ISize<?> anySize;
}
