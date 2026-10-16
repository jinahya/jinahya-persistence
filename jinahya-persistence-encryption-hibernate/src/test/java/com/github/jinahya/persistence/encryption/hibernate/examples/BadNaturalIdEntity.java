package com.github.jinahya.persistence.encryption.hibernate.examples;

import com.github.jinahya.persistence.encryption.hibernate.Encrypted;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.NaturalId;

/**
 * An entity whose natural id is {@code @Encrypted}, which the type refuses: a lookup by it compares the column with a
 * fresh ciphertext, and never matches. In a persistence unit of its own.
 */
@Entity
@Table(name = "bad_natural_id_entity")
public class BadNaturalIdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long id;

    @Encrypted
    @NaturalId
    @Column(name = "code_enc", length = 2048)
    public String code;
}
