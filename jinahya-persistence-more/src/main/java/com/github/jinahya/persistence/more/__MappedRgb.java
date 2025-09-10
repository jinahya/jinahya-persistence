package com.github.jinahya.persistence.more;

import com.github.jinahya.persistence.mapped.__Mapped;
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
public abstract class __MappedRgb extends __Mapped {

    // -----------------------------------------------------------------------------------------------------------------
    public static final String COLUMN_NAME_RED = "red";

    public static final String ATTRIBUTE_NAME_RED = "red";

    // -----------------------------------------------------------------------------------------------------------------
    public static final String COLUMN_NAME_GREEN = "green";

    public static final String ATTRIBUTE_NAME_GREEN = "green";

    // -----------------------------------------------------------------------------------------------------------------
    public static final String COLUMN_NAME_BLUE = "blue";

    public static final String ATTRIBUTE_NAME_BLUE = "blue";

    // -----------------------------------------------------------------------------------------------------------------
    public static final String DECIMAL_MAX_COMPONENT = "255";

    public static final String DECIMAL_MIN_COMPONENT = "0";
    // -----------------------------------------------------------------------------------------------------------------

    // -----------------------------------------------------------------------------------------------------------------
    protected __MappedRgb() {
        super();
    }

    protected __MappedRgb(final __MappedRgbBuilder<?, ?> builder) {
        super(builder);
        red = builder.red();
        green = builder.green();
        blue = builder.blue();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object
    @Override
    public String toString() {
        return super.toString() + '{'
               + "red=" + red
               + ",green=" + green
               + ",blue=" + blue
               + '}';
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof __MappedRgb that)) return false;
        return Objects.equals(red, that.red)
               && Objects.equals(green, that.green)
               && Objects.equals(blue, that.blue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(red, green, blue);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    public Double getRed() {
        return red;
    }

    public void setRed(@Nonnull final Double red) {
        this.red = red;
    }

    public Integer getRedAsInteger() {
        return Optional.ofNullable(getRed())
                .map(Double::intValue)
                .orElse(null);
    }

    @Transient
    public void setRedFromInteger(@Nonnull final Integer red) {
        setRed(
                Optional.ofNullable(red)
                        .map(v -> (double) v)
                        .orElse(null)
        );
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    public Double getGreen() {
        return green;
    }

    public void setGreen(@Nonnull final Double green) {
        this.green = green;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    public Double getBlue() {
        return blue;
    }

    public void setBlue(@Nonnull final Double blue) {
        this.blue = blue;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    @DecimalMax(DECIMAL_MAX_COMPONENT)
    @DecimalMin(DECIMAL_MIN_COMPONENT)
    @NotNull
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_RED, nullable = false, insertable = true, updatable = true)
    private Double red;

    @Nonnull
    @DecimalMax(DECIMAL_MAX_COMPONENT)
    @DecimalMin(DECIMAL_MIN_COMPONENT)
    @NotNull
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_GREEN, nullable = false, insertable = true, updatable = true)
    private Double green;

    @Nonnull
    @DecimalMax(DECIMAL_MAX_COMPONENT)
    @DecimalMin(DECIMAL_MIN_COMPONENT)
    @NotNull
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_BLUE, nullable = false, insertable = true, updatable = true)
    private Double blue;
}
