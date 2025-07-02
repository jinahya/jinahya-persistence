package com.github.jinahya.persistence.more;

import jakarta.annotation.Nullable;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.Objects;
import java.util.Optional;
import java.util.function.DoubleFunction;
import java.util.function.IntFunction;

@Embeddable
@MappedSuperclass
public abstract class __MappedHsla<SELF extends __MappedHsla<SELF>> extends ___MappedColor<SELF> {

    @Serial
    private static final long serialVersionUID = 4172783566899888893L;

    // -----------------------------------------------------------------------------------------------------------------
    public static final int MIN_HUE = 0;

    public static final int MAX_HUE = 360;

    public static final String DECIMAL_MIN_HUE_AS_DOUBLE = "0.0d";

    public static final String DECIMAL_MAX_HUE_AS_DOUBLE = "1.0d";

    private static final double MIN_HUE_AS_DOUBLE = new BigDecimal(DECIMAL_MIN_HUE_AS_DOUBLE).doubleValue();

    private static final double MAX_HUE_AS_DOUBLE = new BigDecimal(DECIMAL_MAX_HUE_AS_DOUBLE).doubleValue();

    // ------------------------------------------------------------------------------------------------------ saturation
    public static final int MIN_SATURATION = 0;

    public static final int MAX_SATURATION = 100;

    public static final String DECIMAL_MIN_SATURATION_AS_DOUBLE = "0.0d";

    public static final String DECIMAL_MAX_SATURATION_AS_DOUBLE = "1.0d";

    private static final double MIN_SATURATION_AS_DOUBLE =
            new BigDecimal(DECIMAL_MIN_SATURATION_AS_DOUBLE).doubleValue();

    private static final double MAX_SATURATION_AS_DOUBLE =
            new BigDecimal(DECIMAL_MAX_SATURATION_AS_DOUBLE).doubleValue();

    // ------------------------------------------------------------------------------------------------------- lightless
    public static final int MIN_LIGHTLESS = 0;

    public static final int MAX_LIGHTLESS = 100;

    public static final String DECIMAL_MIN_LIGHTLESS_AS_DOUBLE = "0.0d";

    public static final String DECIMAL_MAX_LIGHTLESS_AS_DOUBLE = "1.0d";

    private static final double MIN_LIGHTLESS_AS_DOUBLE =
            new BigDecimal(DECIMAL_MIN_LIGHTLESS_AS_DOUBLE).doubleValue();

    private static final double MAX_LIGHTLESS_AS_DOUBLE =
            new BigDecimal(DECIMAL_MAX_LIGHTLESS_AS_DOUBLE).doubleValue();

    // -----------------------------------------------------------------------------------------------------------------
    public static final String DECIMAL_MIN_ALPHA_AS_DOUBLE = "0.0d";

    public static final String DECIMAL_MAX_ALPHA_AS_DOUBLE = "1.0d";

    private static final double MIN_ALPHA_AS_DOUBLE = new BigDecimal(DECIMAL_MIN_ALPHA_AS_DOUBLE).doubleValue();

    private static final double MAX_ALPHA_AS_DOUBLE = new BigDecimal(DECIMAL_MAX_ALPHA_AS_DOUBLE).doubleValue();

    // ---------------------------------------------------------------------------------------------------------- value_
    public static final String COLUMN_NAME_VALUE_ = "value_";

    public static final int COLUMN_LENGTH_VALUE_ = 32;

    public static final String ATTRIBUTE_NAME_VALUE_ = COLUMN_NAME_VALUE_;

    public static final int SIZE_MIN_VALUE_ = COLUMN_LENGTH_VALUE_;

    public static final int SIZE_MAX_VALUE_ = SIZE_MIN_VALUE_;

    protected static final int COMPONENT_LENGTH = Double.BYTES;

//    protected static final int COMPONENT_INDEX_H = 0;
//
//    protected static final int COMPONENT_INDEX_S = 1;
//
//    protected static final int COMPONENT_INDEX_L = 2;
//
//    protected static final int COMPONENT_INDEX_A = 3;

    protected static final int VALUE_OFFSET_H = 0;

    protected static final int VALUE_OFFSET_S = VALUE_OFFSET_H + COMPONENT_LENGTH;

    protected static final int VALUE_OFFSET_L = VALUE_OFFSET_S + COMPONENT_LENGTH;

    protected static final int VALUE_OFFSET_A = VALUE_OFFSET_L + COMPONENT_LENGTH;

    // ------------------------------------------------------------------------------------------ STATIC_FACTORY_METHODS

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected __MappedHsla() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    @Override
    public String toString() {
        return super.toString() + '{' +
               "value_=" +
               Optional.ofNullable(getValue_()).map(v -> HexFormat.of().withPrefix("0x").formatHex(v)).orElse(null) +
               '}';
    }

    // -----------------------------------------------------------------------------------------------------------------
    public <R> R apply(
            final IntFunction<
                    ? extends IntFunction<
                            ? extends IntFunction<
                                    ? extends DoubleFunction<
                                            ? extends R>>>> function) {
        Objects.requireNonNull(function, "function is null");
        return function
                .apply(getHue())
                .apply(getSaturation())
                .apply(getLightless())
                .apply(getAlpha());
    }

    // ------------------------------------------------------------------------------------------------------------- hue
    @Max(MAX_HUE)
    @Min(MIN_HUE)
    @Transient
    public int getHue() {
        return ((int) (getHueAsDouble() * MAX_HUE)) % MAX_HUE;
    }

    public void setHue(final int hue) {
        if (hue < MIN_HUE || hue > MAX_HUE) {
            throw new IllegalArgumentException("invalid hue: " + hue);
        }
        setHueAsDouble((hue % MAX_HUE) / (double) MAX_HUE);
    }

    @DecimalMax(DECIMAL_MAX_HUE_AS_DOUBLE)
    @DecimalMin(DECIMAL_MIN_HUE_AS_DOUBLE)
    @Transient
    public double getHueAsDouble() {
        return ByteBuffer.wrap(
                Optional.ofNullable(getValue_()).orElseGet(() -> value_(new byte[COLUMN_LENGTH_VALUE_]).getValue_()),
                VALUE_OFFSET_H,
                COMPONENT_LENGTH
        ).getDouble();
    }

    public void setHueAsDouble(final double hueAsDouble) {
        if (hueAsDouble < MIN_HUE_AS_DOUBLE || hueAsDouble > MAX_HUE_AS_DOUBLE) {
            throw new IllegalArgumentException("invalid hueAsDouble: " + hueAsDouble);
        }
        ByteBuffer.wrap(
                Optional.ofNullable(getValue_()).orElseGet(() -> value_(new byte[COLUMN_LENGTH_VALUE_]).getValue_()),
                VALUE_OFFSET_H,
                COMPONENT_LENGTH
        ).putDouble(hueAsDouble);
    }

    // ------------------------------------------------------------------------------------------------------ saturation
    @Max(MAX_SATURATION)
    @Min(MIN_SATURATION)
    @Transient
    public int getSaturation() {
        return (int) (getSaturationAsDouble() * MAX_SATURATION);
    }

    public void setSaturation(final int saturation) {
        if (saturation < MIN_SATURATION || saturation > MAX_SATURATION) {
            throw new IllegalArgumentException("invalid saturation: " + saturation);
        }
        setSaturationAsDouble(saturation / (double) MAX_SATURATION);
    }

    @DecimalMax(DECIMAL_MAX_SATURATION_AS_DOUBLE)
    @DecimalMin(DECIMAL_MIN_SATURATION_AS_DOUBLE)
    @Transient
    public double getSaturationAsDouble() {
        return ByteBuffer.wrap(
                Optional.ofNullable(getValue_()).orElseGet(() -> value_(new byte[COLUMN_LENGTH_VALUE_]).getValue_()),
                VALUE_OFFSET_S,
                COMPONENT_LENGTH
        ).getDouble();
    }

    public void setSaturationAsDouble(final double saturationAsDouble) {
        if (saturationAsDouble < MIN_SATURATION_AS_DOUBLE || saturationAsDouble > MAX_SATURATION_AS_DOUBLE) {
            throw new IllegalArgumentException("invalid saturationAsDouble: " + saturationAsDouble);
        }
        ByteBuffer.wrap(
                Optional.ofNullable(getValue_()).orElseGet(() -> value_(new byte[COLUMN_LENGTH_VALUE_]).getValue_()),
                VALUE_OFFSET_S,
                COMPONENT_LENGTH
        ).putDouble(saturationAsDouble);
    }

    // ------------------------------------------------------------------------------------------------------- lightless
    @Max(MAX_LIGHTLESS)
    @Min(MIN_LIGHTLESS)
    @Transient
    public int getLightless() {
        return (int) (getLightlessAsDouble() * MAX_LIGHTLESS);
    }

    public void setLightless(@Max(MAX_LIGHTLESS) @Min(MIN_LIGHTLESS) final int lightless) {
        setLightlessAsDouble(lightless / (double) MAX_LIGHTLESS);
    }

    @DecimalMax(DECIMAL_MAX_LIGHTLESS_AS_DOUBLE)
    @DecimalMin(DECIMAL_MIN_LIGHTLESS_AS_DOUBLE)
    @Transient
    public double getLightlessAsDouble() {
        return ByteBuffer.wrap(
                Optional.ofNullable(getValue_()).orElseGet(() -> value_(new byte[COLUMN_LENGTH_VALUE_]).getValue_()),
                VALUE_OFFSET_S,
                COMPONENT_LENGTH
        ).getDouble();
    }

    public void setLightlessAsDouble(final double lightlessAsDouble) {
        if (lightlessAsDouble < MIN_LIGHTLESS_AS_DOUBLE || lightlessAsDouble > MAX_LIGHTLESS_AS_DOUBLE) {
            throw new IllegalArgumentException("invalid lightlessAsDouble: " + lightlessAsDouble);
        }
        ByteBuffer.wrap(
                Optional.ofNullable(getValue_()).orElseGet(() -> value_(new byte[COLUMN_LENGTH_VALUE_]).getValue_()),
                VALUE_OFFSET_S,
                COMPONENT_LENGTH
        ).putDouble(lightlessAsDouble);
    }

    // ------------------------------------------------------------------------------------------------------------- alpha

    /**
     * Returns the current value of this color's <span style="color:grey; -webkit-text-stroke: .5px black;">alpha</span>
     * component.
     *
     * @return the current value of the <span style="color:grey; -webkit-text-stroke: .5px black;">alpha</span>
     * component
     */
    @DecimalMax(DECIMAL_MAX_ALPHA_AS_DOUBLE)
    @DecimalMin(DECIMAL_MIN_ALPHA_AS_DOUBLE)
    @Transient
    public double getAlpha() {
        return ByteBuffer.wrap(
                Optional.ofNullable(getValue_()).orElseGet(() -> value_(new byte[COLUMN_LENGTH_VALUE_]).getValue_()),
                VALUE_OFFSET_A,
                COMPONENT_LENGTH
        ).getDouble();
    }

    /**
     * Replaces the current value of this color's <span style="color:grey; -webkit-text-stroke: .5px
     * black;">alpha</span> component with the specified value.
     *
     * @param alpha new value for the <span style="color:grey; -webkit-text-stroke: .5px black;">alpha</span>
     *              component.
     */
    public void setAlpha(final double alpha) {
        if (alpha < MIN_ALPHA_AS_DOUBLE || alpha > MAX_ALPHA_AS_DOUBLE) {
            throw new IllegalArgumentException("invalid alpha: " + alpha);
        }
        ByteBuffer.wrap(
                Optional.ofNullable(getValue_()).orElseGet(() -> value_(new byte[COLUMN_LENGTH_VALUE_]).getValue_()),
                VALUE_OFFSET_A,
                COMPONENT_LENGTH
        ).putDouble(alpha);
    }

    // ---------------------------------------------------------------------------------------------------------- value_
    protected byte[] getValue_() {
        return value_;
    }

    protected void setValue_(final byte[] value_) {
        this.value_ = Optional.ofNullable(value_)
                .map(v -> v.length == COLUMN_LENGTH_VALUE_ ? v : Arrays.copyOf(v, COLUMN_LENGTH_VALUE_))
                .orElse(null);
    }

    protected SELF value_(final byte[] value_) {
        setValue_(value_);
        return (SELF) this;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nullable
    @Size(min = SIZE_MIN_VALUE_, max = SIZE_MAX_VALUE_)
    @Basic(optional = true)
    @Column(name = "value_", nullable = true, insertable = true, updatable = true, length = COLUMN_LENGTH_VALUE_)
    private byte[] value_;
}