package com.github.jinahya.persistence.more;

import jakarta.annotation.Nonnull;
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

    // ---------------------------------------------------------------------------------------------------------- value_
    public static final String COLUMN_NAME_VALUE_ = "value_";

    public static final int COLUMN_LENGTH_VALUE_ = 32; // h(8) + s(8) + l(8) + a(8)

    public static final String ATTRIBUTE_NAME_VALUE_ = "value_";

    public static final int SIZE_MIN_VALUE_ = COLUMN_LENGTH_VALUE_;

    public static final int SIZE_MAX_VALUE_ = SIZE_MIN_VALUE_;

    // -----------------------------------------------------------------------------------------------------------------
    protected static final int COMPONENT_LENGTH = Double.BYTES;

    // -----------------------------------------------------------------------------------------------------------------
    private static final int VALUE_OFFSET_H = 0;

    private static final int VALUE_OFFSET_S = VALUE_OFFSET_H + COMPONENT_LENGTH;

    private static final int VALUE_OFFSET_L = VALUE_OFFSET_S + COMPONENT_LENGTH;

    private static final int VALUE_OFFSET_A = VALUE_OFFSET_L + COMPONENT_LENGTH;

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
            final IntFunction< // h
                    ? extends IntFunction< // s
                            ? extends IntFunction< // l
                                    ? extends DoubleFunction< // alpha
                                            ? extends R>>>> function) {
        Objects.requireNonNull(function, "function is null");
        return function
                .apply(getHue())
                .apply(getSaturation())
                .apply(getLightness())
                .apply(getNormalizedAlpha());
    }

    public <R> R applyNormalized(
            final DoubleFunction<? extends DoubleFunction<? extends DoubleFunction<? extends DoubleFunction<? extends R>>>> function) {
        Objects.requireNonNull(function, "function is null");
        return function
                .apply(getNormalizedHue())
                .apply(getNormalizedSaturation())
                .apply(getNormalizedLightness())
                .apply(getNormalizedAlpha());
    }

    // -----------------------------------------------------------------------------------------------------------------
    public <T extends __MappedRgba<T>> T toRgb(final T rgb) {
        Objects.requireNonNull(rgb, "rgb is null");
        return applyNormalized(
                r -> g -> b -> a -> rgb.normalizedRed(r).normalizedBlue(g).normalizedGreen(b)
        );
    }

    public <T extends __MappedRgba<T>> T toRgba(final T rgba) {
        return toRgb(rgba).normalizedAlpha(getNormalizedAlpha());
    }

    // -----------------------------------------------------------------------------------------------------------------
    public <T extends __MappedRgba<T>> SELF fromRgb(final T rgb) {
        Objects.requireNonNull(rgb, "rgb is null");
        return rgb.applyNormalized(r -> g -> b -> a -> {
            return ___MappedColorUtils.rgbToHsl(
                    r, g, b,
                    h -> s -> l -> {
                        return hue(h).normalizedSaturation(s).normalizedLightness(l);
                    });
        });
    }

    public <T extends __MappedRgba<T>> SELF fromRgba(final T rgba) {
        return fromRgb(rgba).normalizedAlpha(rgba.getNormalizedAlpha());
    }

    // ------------------------------------------------------------------------------------------------------------- hue

    /**
     * Returns current value of {@code hue} component.
     *
     * @return current value of {@code hue} component.
     */
    @Max(___MappedColorConstants.HSL_MAX_HUE)
    @Min(___MappedColorConstants.HSL_MIN_HUE)
    @Transient
    public int getHue() {
        return (int) (getNormalizedHue() * ___MappedColorConstants.HSL_MAX_HUE);
    }

    /**
     * Replaces current value of {@code hue} component with the specified value.
     *
     * @param hue new value for the {@code hue} component between {@value ___MappedColorConstants#HSL_MIN_HUE} and
     *            {@value ___MappedColorConstants#HSL_MAX_HUE}, both inclusive.
     */
    public void setHue(final int hue) {
        if (hue < ___MappedColorConstants.HSL_MIN_HUE || hue > ___MappedColorConstants.HSL_MAX_HUE) {
            throw new IllegalArgumentException("invalid hue: " + hue);
        }
        setNormalizedHue(hue / (double) ___MappedColorConstants.HSL_MAX_HUE);
    }

    public SELF hue(final int hue) {
        setHue(hue);
        return (SELF) this;
    }

    @DecimalMax(___MappedColorConstants.HSL_DECIMAL_MAX_HUE_NORMALIZED)
    @DecimalMin(___MappedColorConstants.HSL_DECIMAL_MIN_HUE_NORMALIZED)
    @Transient
    public double getNormalizedHue() {
        return ByteBuffer.wrap(
                Optional.ofNullable(getValue_()).orElseGet(() -> value_(new byte[COLUMN_LENGTH_VALUE_]).getValue_()),
                VALUE_OFFSET_H,
                COMPONENT_LENGTH
        ).getDouble();
    }

    public void setNormalizedHue(final double normalizedHue) {
        if (normalizedHue < ___MappedColorConstants.HSL_MIN_HUE_NORMALIZED ||
            normalizedHue > ___MappedColorConstants.HSL_MAX_HUE_NORMALIZED) {
            throw new IllegalArgumentException("invalid hue: " + normalizedHue);
        }
        ByteBuffer.wrap(
                Optional.ofNullable(getValue_()).orElseGet(() -> value_(new byte[COLUMN_LENGTH_VALUE_]).getValue_()),
                VALUE_OFFSET_H,
                COMPONENT_LENGTH
        ).putDouble(normalizedHue);
    }

    public SELF normalizedHue(final double normalizedHue) {
        setNormalizedHue(normalizedHue);
        return (SELF) this;
    }

    // ------------------------------------------------------------------------------------------------------ saturation
    @Max(___MappedColorConstants.HSL_MAX_SATURATION)
    @Min(___MappedColorConstants.HSL_MIN_SATURATION)
    @Transient
    public int getSaturation() {
        return (int) (getNormalizedSaturation() * ___MappedColorConstants.HSL_MAX_SATURATION);
    }

    public void setSaturation(final int saturation) {
        if (saturation < ___MappedColorConstants.HSL_MIN_SATURATION ||
            saturation > ___MappedColorConstants.HSL_MAX_SATURATION) {
            throw new IllegalArgumentException("invalid saturation: " + saturation);
        }
        setNormalizedSaturation(saturation / (double) ___MappedColorConstants.HSL_MAX_SATURATION);
    }

    public SELF saturation(final int saturation) {
        setSaturation(saturation);
        return (SELF) this;
    }

    @DecimalMax(___MappedColorConstants.HSL_DECIMAL_MAX_SATURATION_NORMALIZED)
    @DecimalMin(___MappedColorConstants.HSL_DECIMAL_MIN_SATURATION_NORMALIZED)
    @Transient
    public double getNormalizedSaturation() {
        return ByteBuffer.wrap(
                Optional.ofNullable(getValue_()).orElseGet(() -> value_(new byte[COLUMN_LENGTH_VALUE_]).getValue_()),
                VALUE_OFFSET_S,
                COMPONENT_LENGTH
        ).getDouble();
    }

    public void setNormalizedSaturation(final double normalizedSaturation) {
        if (normalizedSaturation < ___MappedColorConstants.HSL_MIN_SATURATION_NORMALIZED ||
            normalizedSaturation > ___MappedColorConstants.HSL_MAX_SATURATION_NORMALIZED) {
            throw new IllegalArgumentException("invalid normalized saturation: " + normalizedSaturation);
        }
        ByteBuffer.wrap(
                Optional.ofNullable(getValue_()).orElseGet(() -> value_(new byte[COLUMN_LENGTH_VALUE_]).getValue_()),
                VALUE_OFFSET_S,
                COMPONENT_LENGTH
        ).putDouble(normalizedSaturation);
    }

    public SELF normalizedSaturation(final double normalizedSaturation) {
        setNormalizedSaturation(normalizedSaturation);
        return (SELF) this;
    }

    // ------------------------------------------------------------------------------------------------------ lightness
    @Max(___MappedColorConstants.HSL_MAX_LIGHTNESS)
    @Min(___MappedColorConstants.HSL_MIN_LIGHTNESS)
    @Transient
    public int getLightness() {
        return (int) (getNormalizedLightness() * ___MappedColorConstants.HSL_MAX_LIGHTNESS);
    }

    public void setLightness(final int lightness) {
        if (lightness < ___MappedColorConstants.HSL_MIN_LIGHTNESS ||
            lightness > ___MappedColorConstants.HSL_MAX_LIGHTNESS) {
            throw new IllegalArgumentException("invalid lightness: " + lightness);
        }
        setNormalizedLightness(lightness / (double) ___MappedColorConstants.HSL_MAX_LIGHTNESS);
    }

    public SELF lightness(final int lightness) {
        setLightness(lightness);
        return (SELF) this;
    }

    @DecimalMax(___MappedColorConstants.HSL_DECIMAL_MAX_LIGHTNESS_NORMALIZED)
    @DecimalMin(___MappedColorConstants.HSL_DECIMAL_MIN_LIGHTNESS_NORMALIZED)
    @Transient
    public double getNormalizedLightness() {
        return ByteBuffer.wrap(
                Optional.ofNullable(getValue_()).orElseGet(() -> value_(new byte[COLUMN_LENGTH_VALUE_]).getValue_()),
                VALUE_OFFSET_L,
                COMPONENT_LENGTH
        ).getDouble();
    }

    public void setNormalizedLightness(final double normalizedLightness) {
        if (normalizedLightness < ___MappedColorConstants.HSL_MIN_LIGHTNESS_NORMALIZED ||
            normalizedLightness > ___MappedColorConstants.HSL_MAX_LIGHTNESS_NORMALIZED) {
            throw new IllegalArgumentException("invalid normalized lightness: " + normalizedLightness);
        }
        ByteBuffer.wrap(
                Optional.ofNullable(getValue_()).orElseGet(() -> value_(new byte[COLUMN_LENGTH_VALUE_]).getValue_()),
                VALUE_OFFSET_L,
                COMPONENT_LENGTH
        ).putDouble(normalizedLightness);
    }

    public SELF normalizedLightness(final double normalizedLightness) {
        setNormalizedLightness(normalizedLightness);
        return (SELF) this;
    }

    // ----------------------------------------------------------------------------------------------------------- alpha

    /**
     * Returns the current value of this color's <span style="color:grey; -webkit-text-stroke: .5px black;">alpha</span>
     * component.
     *
     * @return the current value of the <span style="color:grey; -webkit-text-stroke: .5px black;">alpha</span>
     *         component.
     */
    @DecimalMax(___MappedColorConstants.HSL_DECIMAL_MAX_ALPHA_NORMALIZED)
    @DecimalMin(___MappedColorConstants.HSL_DECIMAL_MIN_ALPHA_NORMALIZED)
    @Transient
    public double getNormalizedAlpha() {
        return ByteBuffer.wrap(
                Optional.ofNullable(getValue_()).orElseGet(() -> value_(new byte[COLUMN_LENGTH_VALUE_]).getValue_()),
                VALUE_OFFSET_A,
                COMPONENT_LENGTH
        ).getDouble();
    }

    /**
     * Replaces the current value of this color's <span style="color:grey; -webkit-text-stroke: .5px
     * black;">normalizedAlpha</span> component with the specified value.
     *
     * @param normalizedAlpha new value for the <span style="color:grey; -webkit-text-stroke: .5px
     *                        black;">normalizedAlpha</span> component.
     */
    public void setNormalizedAlpha(final double normalizedAlpha) {
        if (normalizedAlpha < ___MappedColorConstants.HSL_MIN_ALPHA_NORMALIZED ||
            normalizedAlpha > ___MappedColorConstants.HSL_MAX_ALPHA_NORMALIZED) {
            throw new IllegalArgumentException("invalid normalized alpha: " + normalizedAlpha);
        }
        ByteBuffer.wrap(
                Optional.ofNullable(getValue_()).orElseGet(() -> value_(new byte[COLUMN_LENGTH_VALUE_]).getValue_()),
                VALUE_OFFSET_A,
                COMPONENT_LENGTH
        ).putDouble(normalizedAlpha);
    }

    public SELF normalizedAlpha(final double normalizedAlpha) {
        setNormalizedAlpha(normalizedAlpha);
        return (SELF) this;
    }

    // ---------------------------------------------------------------------------------------------------------- value_
    @Nullable
    protected byte[] getValue_() {
        return value_;
    }

    protected void setValue_(@Nonnull final byte[] value_) {
        this.value_ = Optional.ofNullable(value_)
                .map(v -> v.length == COLUMN_LENGTH_VALUE_ ? v : Arrays.copyOf(v, COLUMN_LENGTH_VALUE_))
                .orElse(null);
    }

    @Nonnull
    protected SELF value_(@Nullable final byte[] value_) {
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