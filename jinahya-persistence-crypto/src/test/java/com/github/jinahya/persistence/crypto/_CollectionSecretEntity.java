package com.github.jinahya.persistence.crypto;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * An entity reaching an embeddable which holds an {@link EncryptedAttribute encrypted attribute} through an
 * {@link ElementCollection}, which this module does not walk; for verifying that such a mapping is rejected rather
 * than persisted in the clear.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@EncryptedEntity
@Access(AccessType.FIELD)
@Entity
@Table(name = "collection_secret_entity")
public class _CollectionSecretEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ElementCollection
    @CollectionTable(name = "collection_secret_entity_secret",
                     joinColumns = @JoinColumn(name = "collection_secret_entity_id"))
    public List<_SecretEmbeddable> secrets = new ArrayList<>();
}
