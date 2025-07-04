package com.github.jinahya.persistence.more;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.io.Serial;
import java.util.Objects;
import java.util.function.DoubleFunction;
import java.util.function.IntFunction;

@MappedSuperclass
public abstract class __MappedHsla<SELF extends __MappedHsla<SELF>> extends ___MappedColor<SELF> {

    @Serial
    private static final long serialVersionUID = 4172783566899888893L;

    // -----------------------------------------------------------------------------------------------------------------
    private static final int VALUE_OFFSET_H = 0;

    private static final int VALUE_OFFSET_S = 1;

    private static final int VALUE_OFFSET_L = 2;

    private static final int VALUE_OFFSET_A = 3;

    // ------------------------------------------------------------------------------------------ STATIC_FACTORY_METHODS

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected __MappedHsla() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

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

    @DecimalMax(___MappedColorConstants.DECIMAL_MAX_COMPONENT_NORMALIZED)
    @DecimalMin(___MappedColorConstants.DECIMAL_MIN_COMPONENT_NORMALIZED)
    @Transient
    public double getNormalizedHue() {
        return getComponent_(VALUE_OFFSET_H);
    }

    public void setNormalizedHue(final double normalizedHue) {
        if (normalizedHue < ___MappedColorConstants.MIN_COMPONENT_NORMALIZED ||
            normalizedHue > ___MappedColorConstants.MAX_COMPONENT_NORMALIZED) {
            throw new IllegalArgumentException("invalid hue: " + normalizedHue);
        }
        setComponent_(VALUE_OFFSET_H, normalizedHue);
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

    @DecimalMax(___MappedColorConstants.DECIMAL_MAX_COMPONENT_NORMALIZED)
    @DecimalMin(___MappedColorConstants.DECIMAL_MIN_COMPONENT_NORMALIZED)
    @Transient
    public double getNormalizedSaturation() {
        return getComponent_(VALUE_OFFSET_S);
    }

    public void setNormalizedSaturation(final double normalizedSaturation) {
        if (normalizedSaturation < ___MappedColorConstants.MIN_COMPONENT_NORMALIZED ||
            normalizedSaturation > ___MappedColorConstants.MAX_COMPONENT_NORMALIZED) {
            throw new IllegalArgumentException("invalid normalized saturation: " + normalizedSaturation);
        }
        setComponent_(VALUE_OFFSET_S, normalizedSaturation);
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

    @DecimalMax(___MappedColorConstants.DECIMAL_MAX_COMPONENT_NORMALIZED)
    @DecimalMin(___MappedColorConstants.DECIMAL_MIN_COMPONENT_NORMALIZED)
    @Transient
    public double getNormalizedLightness() {
        return getComponent_(VALUE_OFFSET_L);
    }

    public void setNormalizedLightness(final double normalizedLightness) {
        if (normalizedLightness < ___MappedColorConstants.MIN_COMPONENT_NORMALIZED ||
            normalizedLightness > ___MappedColorConstants.MAX_COMPONENT_NORMALIZED) {
            throw new IllegalArgumentException("invalid normalized lightness: " + normalizedLightness);
        }
        setComponent_(VALUE_OFFSET_L, normalizedLightness);
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
    @DecimalMax(___MappedColorConstants.DECIMAL_MAX_COMPONENT_NORMALIZED)
    @DecimalMin(___MappedColorConstants.DECIMAL_MIN_COMPONENT_NORMALIZED)
    @Transient
    public double getNormalizedAlpha() {
        return getComponent_(VALUE_OFFSET_A);
    }

    /**
     * Replaces the current value of this color's <span style="color:grey; -webkit-text-stroke: .5px
     * black;">normalizedAlpha</span> component with the specified value.
     *
     * @param normalizedAlpha new value for the <span style="color:grey; -webkit-text-stroke: .5px
     *                        black;">normalizedAlpha</span> component.
     */
    public void setNormalizedAlpha(final double normalizedAlpha) {
        if (normalizedAlpha < ___MappedColorConstants.MIN_COMPONENT_NORMALIZED ||
            normalizedAlpha > ___MappedColorConstants.MAX_COMPONENT_NORMALIZED) {
            throw new IllegalArgumentException("invalid normalized alpha: " + normalizedAlpha);
        }
        setComponent_(VALUE_OFFSET_A, normalizedAlpha);
    }

    public SELF normalizedAlpha(final double normalizedAlpha) {
        setNormalizedAlpha(normalizedAlpha);
        return (SELF) this;
    }
}
