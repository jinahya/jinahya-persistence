package com.github.jinahya.persistence.more.converter;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/**
 * An entity which registers {@link __NumberStringAttributeConverters.OfBigDecimal} on a real attribute, so that the
 * provider — not a unit test — is what instantiates the converter.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Entity
@Table(name = "converted_entity")
public class _ConvertedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @Convert(converter = __NumberStringAttributeConverters.OfBigDecimal.class)
    @Basic(optional = true)
    @Column(name = "amount", nullable = true, length = 64, columnDefinition = "VARCHAR(64)")
    public BigDecimal amount;
}
