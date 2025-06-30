package com.github.jinahya.persistence.mapped.user_with_string_id;

import com.github.jinahya.persistence.mapped.__MappedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
@SuppressWarnings({
        "java:S119" // Type parameter names should comply with a naming convention
})
abstract class MappedUser<SELF extends MappedUser<SELF>> extends __MappedEntity<SELF, String> {

    static final String TABLE_NAME = "user";

    // -----------------------------------------------------------------------------------------------------------------
    static final String COLUMN_NAME_ID = "id";

    // ------------------------------------------------------------------------------------------------------ super.id__

    @Override
    protected final String getId__() {
        return id__;
    }

    @Override
    protected final void setId__(final String id__) {
        this.id__ = id__;
    }

    // -------------------------------------------------------------------------------------------------------------- id
    public String getId() {
        return getId__();
    }

    protected void setId(final String id) {
        setId__(id);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Id
    @Column(name = MappedUser.COLUMN_NAME_ID, nullable = false, insertable = false, updatable = false)
    private String id__;
}