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
        _EncryptionListener.class
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
    public Byte getByte1() {
        return byte1;
    }

    public void setByte1(Byte b2) {
        this.byte1 = b2;
    }

    // -----------------------------------------------------------------------------------------------------------------
    public Short getShort1() {
        return short1;
    }

    public void setShort1(Short s2) {
        this.short1 = s2;
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
    @__Encrypted("byte1Enc")
    @Basic(optional = true)
    @Column(name = "b2", nullable = true, insertable = true, updatable = true)
    Byte byte1;

    @Basic(optional = true)
    @Column(name = "byte1_enc", nullable = true, insertable = true, updatable = true)
    byte[] byte1Enc;

    // -----------------------------------------------------------------------------------------------------------------
    @__Encrypted("short1Enc")
    @Basic(optional = true)
    @Column(name = "s2", nullable = true, insertable = true, updatable = true)
    Short short1;

    @Basic(optional = true)
    @Column(name = "short1_enc", nullable = true, insertable = true, updatable = true)
    byte[] short1Enc;

    // -----------------------------------------------------------------------------------------------------------------
}
