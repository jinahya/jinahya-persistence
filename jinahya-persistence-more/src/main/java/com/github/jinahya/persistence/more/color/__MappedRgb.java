package com.github.jinahya.persistence.more.color;

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
public abstract class __MappedRgb extends  __Mapped {

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
    public static final int MAX_COMPONENT = 255;

    public static final int MIN_COMPONENT = 0;

    public static final String DECIMAL_MAX_COMPONENT_NORMALIZED = "1.0";

    public static final String DECIMAL_MIN_COMPONENT_NORMALIZED = "0.0";

    // -----------------------------------------------------------------------------------------------------------------
    static final String HEX_FORMAT = "%1$02x";

    // -----------------------------------------------------------------------------------------------------------------

    // -----------------------------------------------------------------------------------------------------------------
    protected __MappedRgb() {
        super();
    }

    protected __MappedRgb(final __MappedRgbBuilder<?, ?> builder) {
        super();
//        super(builder);
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

    /**
     * Returns a string representation of the object in hexadecimal notation as {@code rrggbb}.
     *
     * @return a string representation of the object in hexadecimal notation as {@code rrggbb}.
     */
    public String toHexNotation() {
        return Optional.ofNullable(getRedAsInteger())
                .flatMap(r -> Optional.ofNullable(getGreenAsInteger())
                        .flatMap(g -> Optional.ofNullable(getBlueAsInteger())
                                .map(b -> String.format(HEX_FORMAT, r) +
                                          String.format(HEX_FORMAT, g) +
                                          String.format(HEX_FORMAT, g))
                        )
                )
                .orElseThrow();
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns current value of {@code red} component as a normalized value between
     * {@value DECIMAL_MIN_COMPONENT_NORMALIZED} and {@value DECIMAL_MAX_COMPONENT_NORMALIZED}, both inclusive.
     *
     * @return current value of {@code red} component.
     */
    @Nonnull
    public Double getRed() {
        return red;
    }

    /**
     * Replaces current value of {@code red} component with specified value.
     *
     * @param red new value for the {@code red} component, as a normalized value between
     *            {@value DECIMAL_MIN_COMPONENT_NORMALIZED} and {@value DECIMAL_MAX_COMPONENT_NORMALIZED}, both
     *            inclusive.
     */
    public void setRed(@Nonnull final Double red) {
        this.red = red;
    }

    /**
     * Returns current value of {@code red} component as a denormalized value between {@value MIN_COMPONENT} and
     * {@value MAX_COMPONENT}, both inclusive.
     *
     * @return current value of {@code red} component.
     */
    public Integer getRedAsInteger() {
        return Optional.ofNullable(getRed())
                .map(v -> v * MAX_COMPONENT)
                .map(Double::intValue)
                .orElse(null);
    }

    /**
     * Replaces current value of {@code red} component with specified value.
     *
     * @param red new value for the {@code red} component, as a denormalized value between {@value MIN_COMPONENT} and
     *            {@value MAX_COMPONENT}, both inclusive.
     */
    @Transient
    public void setRedFromInteger(final Integer red) {
        setRed(
                Optional.ofNullable(red)
                        .map(v -> (double) v * MAX_COMPONENT)
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

    public Integer getGreenAsInteger() {
        return Optional.ofNullable(getGreen())
                .map(v -> v * MAX_COMPONENT)
                .map(Double::intValue)
                .orElse(null);
    }

    @Transient
    public void setGreenFromInteger(final Integer green) {
        setGreen(
                Optional.ofNullable(green)
                        .map(v -> (double) v * MAX_COMPONENT)
                        .orElse(null)
        );
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    public Double getBlue() {
        return blue;
    }

    public void setBlue(@Nonnull final Double blue) {
        this.blue = blue;
    }

    public Integer getBlueAsInteger() {
        return Optional.ofNullable(getBlue())
                .map(v -> v * MAX_COMPONENT)
                .map(Double::intValue)
                .orElse(null);
    }

    @Transient
    public void setBlueFromInteger(final Integer blue) {
        setBlue(
                Optional.ofNullable(blue)
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
    @Column(name = COLUMN_NAME_RED, nullable = false, insertable = true, updatable = true)
    private Double red;

    @Nonnull
    @DecimalMax(DECIMAL_MAX_COMPONENT_NORMALIZED)
    @DecimalMin(DECIMAL_MIN_COMPONENT_NORMALIZED)
    @NotNull
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_GREEN, nullable = false, insertable = true, updatable = true)
    private Double green;

    @Nonnull
    @DecimalMax(DECIMAL_MAX_COMPONENT_NORMALIZED)
    @DecimalMin(DECIMAL_MIN_COMPONENT_NORMALIZED)
    @NotNull
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_BLUE, nullable = false, insertable = true, updatable = true)
    private Double blue;
}
