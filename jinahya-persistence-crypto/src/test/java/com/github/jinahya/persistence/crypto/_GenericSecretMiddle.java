package com.github.jinahya.persistence.crypto;

import jakarta.persistence.MappedSuperclass;

/**
 * A generic mapped superclass which passes its own type variable on, for verifying multi-level binding (#67).
 *
 * @param <U> the type of the encrypted attribute, bound by a subclass.
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@MappedSuperclass
public abstract class _GenericSecretMiddle<U> extends _GenericSecretBase<U> {

}
