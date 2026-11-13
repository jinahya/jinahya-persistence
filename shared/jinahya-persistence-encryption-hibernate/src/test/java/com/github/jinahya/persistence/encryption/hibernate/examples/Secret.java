package com.github.jinahya.persistence.encryption.hibernate.examples;

import com.github.jinahya.persistence.encryption.hibernate.Encrypted;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * An embeddable with one encrypted attribute, embedded in {@link SecretEntity}: the mapping is the same as on an
 * entity's own attribute, and so is the column.
 */
@Embeddable
public class Secret {

    @Encrypted
    @Basic(optional = true)
    @Column(name = "code_enc", nullable = true, length = 2048)
    public String code;
}
