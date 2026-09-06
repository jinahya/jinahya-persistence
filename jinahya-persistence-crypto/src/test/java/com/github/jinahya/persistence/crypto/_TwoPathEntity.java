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
 * An entity reaching one embeddable twice, by two different paths under the same root, so that mapping isolation can be
 * observed without changing the root entity.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Access(AccessType.FIELD)
@Entity
@Table(name = "two_path_entity")
public class _TwoPathEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Embedded
    @AttributeOverride(name = "note", column = @Column(name = "first_note", nullable = true, insertable = false))
    @AttributeOverride(name = "noteEnc__", column = @Column(name = "first_note_enc", nullable = true, length = 2048))
    public _SecretEmbeddable first;

    @Embedded
    @AttributeOverride(name = "note", column = @Column(name = "second_note", nullable = true, insertable = false))
    @AttributeOverride(name = "noteEnc__", column = @Column(name = "second_note_enc", nullable = true, length = 2048))
    public _SecretEmbeddable second;
}
