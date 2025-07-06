package com.github.jinahya.persistence.more;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@AttributeOverride(
        name = __MappedRgb.ATTRIBUTE_NAME_VALUE_,
        column = @Column(
                name = __Hsla.COLUMN_NAME_VALUE___,
                nullable = true,
                insertable = true,
                length = __MappedHsl.COLUMN_LENGTH_VALUE_
        )
)
@Entity
@Table(name = __Hsla.TABLE_NAME)
class __Hsla extends __MappedHsl<__Hsla> {

    // -----------------------------------------------------------------------------------------------------------------
    static final String TABLE_NAME = "__hsla";

    // -----------------------------------------------------------------------------------------------------------------
    static final String COLUMN_NAME_ID = "id";

    static final String COLUMN_NAME_VALUE___ = "value___";

    // -----------------------------------------------------------------------------------------------------------------
    protected __Hsla() {
        super();
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
    @Column(name = COLUMN_NAME_ID, nullable = false, insertable = false, updatable = false)
    private Long id;
}
