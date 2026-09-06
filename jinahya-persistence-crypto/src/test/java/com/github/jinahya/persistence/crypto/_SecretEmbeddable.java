package com.github.jinahya.persistence.crypto;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * An embeddable which holds an {@link __EncryptedAttribute encrypted attribute} of its own, for verifying that
 * {@link __EncryptionService} descends into {@code EMBEDDED} attributes.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Embeddable
public class _SecretEmbeddable {

    public _SecretEmbeddable() {
        super();
    }

    public _SecretEmbeddable(final String note) {
        this();
        this.note = note;
    }

    public String getNote() {
        return note;
    }

    public byte[] getNoteEnc__() {
        return noteEnc__;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @__EncryptedAttribute
    @Basic(optional = true)
    @Column(name = "note", nullable = true, insertable = false)
    private String note;

    @Basic(optional = true)
    @Column(name = "note_enc", nullable = true, length = 2048)
    private byte[] noteEnc__;
}
