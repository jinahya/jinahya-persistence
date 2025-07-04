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
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = __RgbaEmbedding.TABLE_NAME)
class __RgbaEmbedding implements Serializable {

    @Serial
    private static final long serialVersionUID = 6808370949221505641L;

    // -----------------------------------------------------------------------------------------------------------------
    static final String TABLE_NAME = "__rgba_embedding";

    // -------------------------------------------------------------------------------------------------------------- id
    static final String COLUMN_NAME_ID = "id";

    // ------------------------------------------------------------------------------------------------------ foreground
    static final String COLUMN_NAME_FOREGROUND = "foreground";

    // ------------------------------------------------------------------------------------------------------ background

    static final String COLUMN_NAME_BACKGROUND = "background";

    // ------------------------------------------------------------------------------------------ STATIC_FACTORY_METHODS

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    protected __RgbaEmbedding() {
        super();
    }

    // -------------------------------------------------------------------------------------------------------------- id
    public Long getId() {
        return id;
    }

    protected void setId(final Long id) {
        this.id = id;
    }

    // ------------------------------------------------------------------------------------------------------ foreground
    @Nonnull
    public __EmbeddableRgba getForeground() {
        if (foreground == null) {
            foreground = new __EmbeddableRgba();
        }
        return foreground;
    }

    public void setForeground(@Nonnull final __EmbeddableRgba foreground) {
        this.foreground = foreground;
    }

    // ------------------------------------------------------------------------------------------------------ background
    @Nonnull
    public __EmbeddableRgba getBackground() {
        if (foreground == null) {
            foreground = new __EmbeddableRgba();
        }
        return background;
    }

    public void setBackground(@Nonnull final __EmbeddableRgba background) {
        this.background = background;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COLUMN_NAME_ID, nullable = false, insertable = false, updatable = false)
    private Long id;

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    @Valid
    @NotNull
    @AttributeOverride(
            name = __MappedRgba.ATTRIBUTE_NAME_VALUE__,
            column = @Column(name = COLUMN_NAME_FOREGROUND, nullable = true, insertable = true, updatable = true)
    )
    @Embedded
    private __EmbeddableRgba foreground;

    @Nonnull
    @Valid
    @NotNull
    @AttributeOverride(
            name = __MappedRgba.ATTRIBUTE_NAME_VALUE__,
            column = @Column(name = COLUMN_NAME_BACKGROUND, nullable = true, insertable = true, updatable = true)
    )
    @Embedded
    private __EmbeddableRgba background;
}
