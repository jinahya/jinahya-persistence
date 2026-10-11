package com.github.jinahya.persistence.crypto.examples;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * An entity binding the encrypted attribute of a generic mapped superclass to {@code Integer} (#67).
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Access(AccessType.FIELD)
@Entity
@Table(name = "generic_integer_entity")
public class GenericIntegerEntity extends GenericSecretBase<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
}
