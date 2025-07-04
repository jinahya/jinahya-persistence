package com.github.jinahya.persistence.more;

import jakarta.annotation.Nonnull;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = __RgbaEmbedded.TABLE_NAME)
class __RgbaEmbedded implements Serializable {

    @Serial
    private static final long serialVersionUID = 6808370949221505641L;

    // -----------------------------------------------------------------------------------------------------------------
    static final String TABLE_NAME = "__rgba_embedded";

    // -----------------------------------------------------------------------------------------------------------------
    static final String COLUMN_NAME_ID = "id";

    static final String COLUMN_NAME_FOREGROUND = "foreground";

    static final String COLUMN_NAME_BACKGROUND = "background";

    // -----------------------------------------------------------------------------------------------------------------
    protected __RgbaEmbedded() {
        super();
    }

    // -----------------------------------------------------------------------------------------------------------------
    public Long getId() {
        return id;
    }

    protected void setId(final Long id) {
        this.id = id;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    public __Rgba getForeground() {
        if (foreground == null) {
            foreground = new __Rgba();
        }
        return foreground;
    }

    public void setForeground(final __Rgba foreground) {
        this.foreground = foreground;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    public __Rgba getBackground() {
        if (foreground == null) {
            foreground = new __Rgba();
        }
        return background;
    }

    public void setBackground(final __Rgba background) {
        this.background = background;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COLUMN_NAME_ID, nullable = false, insertable = false, updatable = false)
    private Long id;

    // -----------------------------------------------------------------------------------------------------------------
    @AttributeOverride(
            name = __MappedRgba.ATTRIBUTE_NAME_VALUE_,
            column = @Column(name = COLUMN_NAME_FOREGROUND, nullable = true, insertable = true, updatable = true)
    )
    @Embedded
    private __Rgba foreground;

    @AttributeOverride(
            name = __MappedRgba.ATTRIBUTE_NAME_VALUE_,
            column = @Column(name = COLUMN_NAME_BACKGROUND, nullable = true, insertable = true, updatable = true)
    )
    @Embedded
    private __Rgba background;
}
