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
 * An entity holding a valid embeddable and an invalid one, for verifying that the invalid mapping is rejected before
 * the valid one has been transformed.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Access(AccessType.FIELD)
@Entity
@Table(name = "graph_entity")
public class _GraphEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Embedded
    @AttributeOverride(name = "note", column = @Column(name = "ok_note", nullable = true, insertable = false))
    @AttributeOverride(name = "noteEnc__", column = @Column(name = "ok_note_enc", nullable = true, length = 2048))
    public _SecretEmbeddable valid;

    @Embedded
    public _UnguardedEmbeddable invalid;

    /**
     * A second path to {@link _SecretEmbeddable}, under the same root as {@link #valid}, so that path isolation can be
     * observed without changing the root entity.
     */
    @Embedded
    @AttributeOverride(name = "note", column = @Column(name = "second_note", nullable = true, insertable = false))
    @AttributeOverride(name = "noteEnc__",
                       column = @Column(name = "second_note_enc", nullable = true, length = 2048))
    public _SecretEmbeddable second;
}
