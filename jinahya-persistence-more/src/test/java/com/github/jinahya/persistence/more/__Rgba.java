package com.github.jinahya.persistence.more;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serial;

@AttributeOverride(
        name = __MappedRgba.ATTRIBUTE_NAME_VALUE__,
        column = @Column(
                name = __Rgba.COLUMN_NAME_VALUE___,
                nullable = true,
                insertable = true,
                length = __MappedRgba.COLUMN_LENGTH_VALUE__
        )
)
@Entity
@Table(name = __Rgba.TABLE_NAME)
class __Rgba extends __MappedRgba<__Rgba> {

    @Serial
    private static final long serialVersionUID = 6264342671001204962L;

    // -----------------------------------------------------------------------------------------------------------------
    static final String TABLE_NAME = "__rgba";

    // -----------------------------------------------------------------------------------------------------------------
    static final String COLUMN_NAME_ID = "id";

    static final String COLUMN_NAME_VALUE___ = "value___";

    // -----------------------------------------------------------------------------------------------------------------
    protected __Rgba() {
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
