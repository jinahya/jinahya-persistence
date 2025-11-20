package com.github.jinahya.persistence.more.color;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;
import java.util.Optional;

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

    /**
     * Returns a string representation of the object in hexadecimal notation as {@code rrggbbaa}.
     *
     * @return a string representation of the object in hexadecimal notation as {@code rrggbbaa}.
     */
    @Override
    public String toHexNotation() {
        return super.toHexNotation() +
               Optional.ofNullable(getAlphaAsInteger())
                       .map(a -> String.format(HEX_FORMAT, a))
                       .orElseThrow();
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    public Double getAlpha() {
        return alpha;
    }

    public void setAlpha(final @Nonnull Double alpha) {
        this.alpha = alpha;
    }

    public Integer getAlphaAsInteger() {
        return Optional.ofNullable(getAlpha())
                .map(v -> v * MAX_COMPONENT)
                .map(Double::intValue)
                .orElse(null);
    }

    @Transient
    public void setAlphaFromInteger(final Integer alpha) {
        setAlpha(
                Optional.ofNullable(alpha)
                        .map(v -> (double) v * MAX_COMPONENT)
                        .orElse(null)
        );
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    @DecimalMax(DECIMAL_MAX_COMPONENT_NORMALIZED)
    @DecimalMin(DECIMAL_MIN_COMPONENT_NORMALIZED)
    @NotNull
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_ALPHA, nullable = false, insertable = true, updatable = true)
    private Double alpha;
}
