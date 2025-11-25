package com.github.jinahya.persistence.crypto;

import com.github.jinahya.persistence.mapped.__MappedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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
@Table(name = "one_entity_to_one_entity01")
@Setter
@Getter
@ToString
@Slf4j
class OneEntityToOneEntity01 extends __MappedEntity<Long> {

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    protected OneEntityToOneEntity01() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object
    @Override
    public final boolean equals(final Object obj) {
        if (!(obj instanceof OneEntityToOneEntity01 that)) {
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
    @OneToOne(optional = false)
    @JoinColumn(name = "entity01_id", nullable = false, insertable = true, updatable = false)
    private Entity01 entity01;
}
