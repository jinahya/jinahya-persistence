package com.github.jinahya.persistence.encryption.hibernate.examples;

import com.github.jinahya.persistence.encryption.hibernate.Encrypted;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

/**
 * An entity whose version is {@code @Encrypted}, which the type refuses: the optimistic lock compares the column with
 * a fresh ciphertext, and never matches. In a persistence unit of its own.
 */
@Entity
@Table(name = "bad_version_entity")
public class BadVersionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long id;

    @Encrypted
    @Version
    @Column(name = "version")
    public Integer version;
}
