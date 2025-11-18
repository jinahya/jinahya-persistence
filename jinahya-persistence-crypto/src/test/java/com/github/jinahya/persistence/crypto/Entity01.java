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
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@EntityListeners({
        _EncryptionListener.class
})
@__EncryptedEntity(encryptionIdentifierAttribute = "__encryptionIdentifier")
@Entity
@Table(name = "entity01")
@Setter
@Getter
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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, insertable = false, updatable = false)
    private Long id;

    // -----------------------------------------------------------------------------------------------------------------
    @Basic(optional = true)
    @Column(name = "__encryption_identifier", nullable = true, insertable = true, updatable = true)
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    String __encryptionIdentifier;

    // -----------------------------------------------------------------------------------------------------------------
    @__EncryptedAttribute(encryptedAttribute = "byte1Enc")
    @Basic(optional = true)
    @Column(name = "b1", nullable = true, insertable = true, updatable = true)
    Byte byte1;

    @Basic(optional = true)
    @Column(name = "byte1_enc", nullable = true, insertable = true, updatable = true)
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    byte[] byte1Enc;

    // -----------------------------------------------------------------------------------------------------------------
    @__EncryptedAttribute(encryptedAttribute = "short1Enc")
    @Basic(optional = true)
    @Column(name = "s1", nullable = true, insertable = true, updatable = true)
    Short short1;

    @Basic(optional = true)
    @Column(name = "short1_enc", nullable = true, insertable = true, updatable = true)
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    byte[] short1Enc;

    // -----------------------------------------------------------------------------------------------------------------
    @__EncryptedAttribute(encryptedAttribute = "int1Enc")
    @Basic(optional = true)
    @Column(name = "i2", nullable = true, insertable = true, updatable = true)
    Integer int1;

    @Basic(optional = true)
    @Column(name = "int1_enc", nullable = true, insertable = true, updatable = true)
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    byte[] int1Enc;
}
