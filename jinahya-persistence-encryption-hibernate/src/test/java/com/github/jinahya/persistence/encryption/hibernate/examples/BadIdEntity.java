package com.github.jinahya.persistence.encryption.hibernate.examples;

import com.github.jinahya.persistence.encryption.hibernate.Encrypted;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * An entity whose identifier is {@code @Encrypted}, which the type refuses: a randomized ciphertext never matches the
 * key it is looked up by. In a persistence unit of its own, so that refusing it fails nothing else.
 */
@Entity
@Table(name = "bad_id_entity")
public class BadIdEntity {

    @Encrypted
    @Id
    @Column(name = "id")
    public Long id;
}
