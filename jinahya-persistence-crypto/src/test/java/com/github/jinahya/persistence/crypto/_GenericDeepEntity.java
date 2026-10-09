package com.github.jinahya.persistence.crypto;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * An entity binding the encrypted attribute of a generic mapped superclass to {@code LocalDate} (#67).
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@EncryptedEntity
@Access(AccessType.FIELD)
@Entity
@Table(name = "generic_deep_entity")
public class _GenericDeepEntity extends _GenericSecretMiddle<java.time.LocalDate> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
}
