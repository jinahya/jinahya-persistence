package com.github.jinahya.persistence.crypto;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * An embeddable whose plaintext column is insertable, and which {@link __EncryptionService} therefore has to reject.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Embeddable
public class _UnguardedEmbeddable {

    public _UnguardedEmbeddable() {
        super();
    }

    // no insertable = false, so this pair is invalid
    @__EncryptedAttribute
    @Basic(optional = true)
    @Column(name = "leak", nullable = true)
    public String leak;

    @Basic(optional = true)
    @Column(name = "leak_enc", nullable = true, length = 2048)
    public byte[] leakEnc__;
}
