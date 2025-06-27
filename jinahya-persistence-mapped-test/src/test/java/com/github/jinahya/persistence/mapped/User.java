package com.github.jinahya.persistence.mapped;

import jakarta.persistence.*;
import lombok.ToString;

@Entity
@Table(name = MappedUser.TABLE_NAME)
@ToString(callSuper = true)
class User extends MappedUser<User> {

    // -----------------------------------------------------------------------------------------------------------------
    protected User() {
        super();
    }

    // ------------------------------------------------------------------------------------------------------ super.id__
    @Override
    protected final Long getId__() {
        return getId();
    }

    @Override
    protected final void setId__(final Long id__) {
        setId(id__);
    }

    // -------------------------------------------------------------------------------------------------------------- id
    public Long getId() {
        return id;
    }

    protected void setId(final Long id) {
        this.id = id;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false, insertable = false, updatable = false)
    private Long id;
}