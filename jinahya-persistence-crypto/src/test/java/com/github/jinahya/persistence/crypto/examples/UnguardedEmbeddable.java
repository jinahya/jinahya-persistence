package com.github.jinahya.persistence.crypto.examples;

import com.github.jinahya.persistence.crypto.EncryptedAttribute;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * An embeddable whose plaintext column is insertable, and which {@code EntityEncryptor} therefore has to reject.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Embeddable
public class UnguardedEmbeddable {

    public UnguardedEmbeddable() {
        super();
    }

    // no insertable = false, so this pair is invalid
    @Basic(optional = true)
    @Column(name = "leak", nullable = true)
    public String leak;

    @EncryptedAttribute("leak")
    @Basic(optional = true)
    @Column(name = "leak_enc", nullable = true, length = 2048)
    public byte[] leakEnc__;
}
