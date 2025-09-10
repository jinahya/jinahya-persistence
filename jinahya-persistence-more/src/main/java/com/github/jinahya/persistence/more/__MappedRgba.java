package com.github.jinahya.persistence.more;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __MappedRgba extends __MappedRgb {

    // -----------------------------------------------------------------------------------------------------------------
    public static final String COLUMN_NAME_ALPHA = "alpha";

    public static final String ATTRIBUTE_NAME_ALPHA = "alpha";

    // -----------------------------------------------------------------------------------------------------------------

    // -----------------------------------------------------------------------------------------------------------------
    protected __MappedRgba() {
        super();
    }

    protected __MappedRgba(final __MappedRgbaBuilder<?, ?> builder) {
        super(builder);
        alpha = builder.alpha();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object
    @Override
    public String toString() {
        return super.toString() + '{' +
               "alpha=" + alpha +
               '}';
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof __MappedRgba that)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        return Objects.equals(alpha, that.alpha);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), alpha);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    public Double getAlpha() {
        return alpha;
    }

    public void setAlpha(@Nonnull final Double alpha) {
        this.alpha = alpha;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    @DecimalMax(DECIMAL_MAX_COMPONENT)
    @DecimalMin(DECIMAL_MIN_COMPONENT)
    @NotNull
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_ALPHA, nullable = false, insertable = true, updatable = true)
    private Double alpha;
}
