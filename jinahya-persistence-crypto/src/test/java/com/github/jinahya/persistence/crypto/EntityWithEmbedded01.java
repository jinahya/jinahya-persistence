package com.github.jinahya.persistence.crypto;

import com.github.jinahya.persistence.mapped.__MappedEntity;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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
@Table(name = "entity_with_embeddable01")
@Setter
@Getter
@ToString
@Slf4j
class EntityWithEmbedded01 extends __MappedEntity<Long> {

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    protected EntityWithEmbedded01() {
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
        if (!(obj instanceof EntityWithEmbedded01 that)) {
            return false;
        }
        return Objects.equals(getId(), that.getId());
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
    String __encryptionIdentifier;

    // -----------------------------------------------------------------------------------------------------------------
    @Embedded
    private Embeddable01 embeddable01;
}
