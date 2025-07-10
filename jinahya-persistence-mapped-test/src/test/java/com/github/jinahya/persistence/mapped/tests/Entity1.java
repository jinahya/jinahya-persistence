package com.github.jinahya.persistence.mapped.tests;

import com.github.jinahya.persistence.mapped.__MappedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = Entity1.TABLE_NAME)
public class Entity1 extends __MappedEntity<Entity1, Long> {

    public static final String TABLE_NAME = "entity1";

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public final boolean equals(final Object obj) {
        return super.equals(obj);
    }

    @Override
    public final int hashCode() {
        return super.hashCode();
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    protected final Long getId__() {
        return getId();
    }

    @Override
    protected final void setId__(final Long id__) {
        setId(id__);
    }

    // -----------------------------------------------------------------------------------------------------------------
    public Long getId() {
        return id;
    }

    protected void setId(final Long id) {
        this.id = id;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
}
