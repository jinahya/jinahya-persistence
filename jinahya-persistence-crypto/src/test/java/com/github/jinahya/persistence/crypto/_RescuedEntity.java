package com.github.jinahya.persistence.crypto;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * An entity embedding {@link _UnguardedEmbeddable}, whose member declares an insertable plaintext column, through an
 * override which makes it non-insertable.
 * <p>
 * This is the unsafe-member to safe-override direction: the mapping has to be <em>accepted</em>, because the override
 * is what the provider actually uses.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Access(AccessType.FIELD)
@Entity
@Table(name = "rescued_entity")
public class _RescuedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Embedded
    @AttributeOverride(name = "leak", column = @Column(name = "rescued", nullable = true, insertable = false))
    @AttributeOverride(name = "leakEnc__", column = @Column(name = "rescued_enc", nullable = true, length = 2048))
    public _UnguardedEmbeddable rescued;
}
