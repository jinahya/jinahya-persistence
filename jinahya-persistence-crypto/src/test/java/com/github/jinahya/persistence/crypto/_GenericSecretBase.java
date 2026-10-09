package com.github.jinahya.persistence.crypto;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

/**
 * A generic mapped superclass whose encrypted attribute is declared as a type variable, for verifying that the codec
 * follows the type each concrete entity binds it to (#67).
 *
 * @param <T> the type of the encrypted attribute.
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Access(AccessType.FIELD)
@MappedSuperclass
public abstract class _GenericSecretBase<T> {

    @EncryptedAttribute
    @Basic(optional = true)
    @Column(name = "secret", nullable = true, insertable = false)
    public T secret;

    @Basic(optional = true)
    @Column(name = "secret_enc", nullable = true, length = 2048)
    public byte[] secretEnc__;
}
