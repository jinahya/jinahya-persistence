package com.github.jinahya.persistence.encryption.hibernate;

import com.github.jinahya.persistence.encryption.EncryptedAttribute;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class SecretEmbeddable {

    @Basic(optional = true)
    @Column(name = "code", nullable = true, insertable = false)
    public Integer code;

    @EncryptedAttribute("code")
    @Basic(optional = true)
    @Column(name = "code_enc", nullable = true, length = 2048)
    public byte[] codeEnc;
}
