package com.github.jinahya.persistence.crypto;

import com.github.jinahya.persistence.mapped.__MappedEntity;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@EntityListeners({
        __PersistenceCryptoListener.class
})
@Entity
@Table(name = "entity01")
class Entity01 extends __MappedEntity<Long> {

    protected Entity01() {
        super();
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, insertable = false, updatable = false)
    private Long id;

    // -----------------------------------------------------------------------------------------------------------------
    @Basic(optional = false)
    @Column(name = "b1", nullable = false, insertable = true, updatable = true)
    private byte b1;

    @Basic(optional = false)
    @Column(name = "b2", nullable = true, insertable = true, updatable = true)
    private Byte b2;
}
