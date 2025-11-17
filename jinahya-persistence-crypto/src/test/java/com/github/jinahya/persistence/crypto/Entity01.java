package com.github.jinahya.persistence.crypto;

import com.github.jinahya.persistence.mapped.__MappedEntity;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Table;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@EntityListeners({
        _PersistenceCryptoListener.class
})
@Entity
@Table(name = "entity01")
@ToString
@Slf4j
class Entity01 extends __MappedEntity<Long> {

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    protected Entity01() {
        super();
    }

    // -----------------------------------------------------------------------------------------------------------------
    @PostLoad
    private void onPostLoad() {
        log.debug("onPostLoad()");
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object
    @Override
    public final boolean equals(final Object obj) {
        if (!(obj instanceof Entity01 entity01)) {
            return false;
        }
        return Objects.equals(getId(), entity01.getId());
    }

    @Override
    public final int hashCode() {
        return Objects.hashCode(getId());
    }

    // -----------------------------------------------------------------------------------------------------------------
    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    // -----------------------------------------------------------------------------------------------------------------
    public byte getB1() {
        return b1;
    }

    public void setB1(final byte b1) {
        this.b1 = b1;
    }

    // -----------------------------------------------------------------------------------------------------------------
    public Byte getB2() {
        return b2;
    }

    public void setB2(Byte b2) {
        this.b2 = b2;
    }

    public short getS1() {
        return s1;
    }

    public void setS1(short s1) {
        this.s1 = s1;
    }

    public Short getS2() {
        return s2;
    }

    public void setS2(Short s2) {
        this.s2 = s2;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, insertable = false, updatable = false)
    private Long id;

    // -----------------------------------------------------------------------------------------------------------------
    @Basic(optional = true)
    @Column(name = "crypto_identifier", nullable = true, insertable = true, updatable = true)
    String cryptoIdentifier;

    // -----------------------------------------------------------------------------------------------------------------
    @__EncryptedAttribute("b1Enc")
    @Basic(optional = false)
    @Column(name = "b1", nullable = false, insertable = true, updatable = true)
    byte b1;

    byte[] b1Enc;

    // -----------------------------------------------------------------------------------------------------------------
    @__EncryptedAttribute("b2Enc")
    @Basic(optional = true)
    @Column(name = "b2", nullable = true, insertable = true, updatable = true)
    Byte b2;

    byte[] b2Enc;

    // -----------------------------------------------------------------------------------------------------------------
    @__EncryptedAttribute("s1Enc")
    @Basic(optional = false)
    @Column(name = "s1", nullable = false, insertable = true, updatable = true)
    short s1;

    byte[] s1Enc;

    // -----------------------------------------------------------------------------------------------------------------
    @__EncryptedAttribute("s2Enc")
    @Basic(optional = true)
    @Column(name = "s2", nullable = true, insertable = true, updatable = true)
    Short s2;

    @Basic(optional = true)
    @Column(name = "s2_enc", nullable = true, insertable = true, updatable = true)
    byte[] s2Enc;

    // -----------------------------------------------------------------------------------------------------------------
}
