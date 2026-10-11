package com.github.jinahya.persistence.encryption.examples;

import jakarta.persistence.MappedSuperclass;

/**
 * A generic mapped superclass which passes its own type variable on, for verifying multi-level binding (#67).
 *
 * @param <U> the type of the encrypted attribute, bound by a subclass.
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@MappedSuperclass
public abstract class GenericSecretMiddle<U> extends GenericSecretBase<U> {

}
