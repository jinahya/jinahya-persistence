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
 * An entity embedding {@link _SecretEmbeddable} through an override which only renames the column.
 * <p>
 * {@link jakarta.persistence.AttributeOverride @AttributeOverride} carries its own {@link Column @Column}, with its own
 * defaults, so the override restores {@code insertable = true} even though {@code _SecretEmbeddable.note} declares
 * {@code insertable = false}. The same embeddable is therefore safe through {@link _GraphEntity} and unsafe here.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Access(AccessType.FIELD)
@Entity
@Table(name = "overridden_entity")
public class _OverriddenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Embedded
    @AttributeOverride(name = "note", column = @Column(name = "bad_note", nullable = true))
    @AttributeOverride(name = "noteEnc__", column = @Column(name = "bad_note_enc", nullable = true, length = 2048))
    public _SecretEmbeddable overridden;
}
