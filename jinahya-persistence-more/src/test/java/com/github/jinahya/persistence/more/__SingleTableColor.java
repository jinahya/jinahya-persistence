package com.github.jinahya.persistence.more;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serial;

@AttributeOverride(
        name = __MappedSingleTableColor.ATTRIBUTE_NAME_VALUE___,
        column = @Column(
                name = __Rgba.COLUMN_NAME_VALUE___,
                nullable = true,
                insertable = true,
                length = ___MappedColor.COLUMN_LENGTH_VALUE___
        )
)
@DiscriminatorColumn
@Entity
@Table(name = __SingleTableColor.TABLE_NAME)
abstract class __SingleTableColor<SELF extends __SingleTableColor<SELF>>
        extends __MappedSingleTableColor<SELF> {

    @Serial
    private static final long serialVersionUID = -1501644535823083731L;

    // -----------------------------------------------------------------------------------------------------------------
    static final String TABLE_NAME = "__single_table_color";

    // -----------------------------------------------------------------------------------------------------------------
    static final String COLUMN_NAME_ID = "id";

    static final String COLUMN_NAME_VALUE___ = "value___";

    // -----------------------------------------------------------------------------------------------------------------
    protected __SingleTableColor() {
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
