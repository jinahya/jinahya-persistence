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
public abstract class __MappedHsl<SELF extends __MappedHsl<SELF>> extends ___MappedColor<SELF> {

    @Serial
    private static final long serialVersionUID = 4172783566899888893L;

    // -----------------------------------------------------------------------------------------------------------------
    public static final int MIN_HUE = 0;

    public static final int MAX_HUE = 360;

    // -----------------------------------------------------------------------------------------------------------------
    public static final int MIN_SATURATION = 0;

    public static final int MAX_SATURATION = 100;

    // -----------------------------------------------------------------------------------------------------------------
    public static final int MIN_LIGHTNESS = 0;

    public static final int MAX_LIGHTNESS = 100;

    // -----------------------------------------------------------------------------------------------------------------
    public static final int MIN_ALPHA = 0;

    public static final int MAX_ALPHA = 100;

    // -----------------------------------------------------------------------------------------------------------------
    static final int COMPONENT_INDEX_H = 0;

    static final int COMPONENT_INDEX_S = 1;

    static final int COMPONENT_INDEX_L = 2;

    static final int COMPONENT_INDEX_A = 3;

    // ------------------------------------------------------------------------------------------ STATIC_FACTORY_METHODS

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected __MappedHsl() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    // -----------------------------------------------------------------------------------------------------------------
    public <R> R applyComponents(
            final IntFunction< // h
                    ? extends IntFunction< // s
                            ? extends IntFunction< // l
                                    ? extends IntFunction< // a
                                            ? extends R>>>> function) {
        Objects.requireNonNull(function, "function is null");
        return function
                .apply(getHue())
                .apply(getSaturation())
                .apply(getLightness())
                .apply(getAlpha());
    }

    public <R> R applyNormalizedComponents(
            final DoubleFunction< // h
                    ? extends DoubleFunction< // s
                            ? extends DoubleFunction< // l
                                    ? extends DoubleFunction<? extends R>>>> function) {
        Objects.requireNonNull(function, "function is null");
        return function
                .apply(getNormalizedHue())
                .apply(getNormalizedSaturation())
                .apply(getNormalizedLightness())
                .apply(getNormalizedAlpha());
    }

    // -----------------------------------------------------------------------------------------------------------------
    public <R> R toRgb(
            final DoubleFunction<
                    ? extends DoubleFunction<
                            ? extends DoubleFunction<
                                    ? extends R>>> function) {
        Objects.requireNonNull(function, "function is null");
        return applyComponents(
                h -> s -> l -> a -> __MappedHslUtils.hslToRgb(h, s, l, function)
        );
    }

    // ------------------------------------------------------------------------------------------------------------- hue

    /**
     * Returns current value of {@code hue} component.
     *
     * @return current value of {@code hue} component.
     */
    @Max(MAX_HUE)
    @Min(MIN_HUE)
    @Transient
    public int getHue() {
        return getComponent(COMPONENT_INDEX_H);
    }

    /**
     * Replaces current value of {@code hue} component with the specified value.
     *
     * @param hue new value for the {@code hue} component between {@value __MappedHsl#MIN_HUE} and
     *            {@value __MappedHsl#MAX_HUE}, both inclusive.
     */
    public void setHue(final int hue) {
        if (hue < MIN_HUE || hue > MAX_HUE) {
            throw new IllegalArgumentException("invalid hue: " + hue);
        }
        setComponent(
                COMPONENT_INDEX_H,
                hue
        );
    }

    public SELF hue(final int hue) {
        setHue(hue);
        return (SELF) this;
    }

    @DecimalMax(___MappedColorConstants.DECIMAL_MAX_COMPONENT_NORMALIZED)
    @DecimalMin(___MappedColorConstants.DECIMAL_MIN_COMPONENT_NORMALIZED)
    @Transient
    public double getNormalizedHue() {
        return getHue() / (double) MAX_HUE;
    }

    public void setNormalizedHue(final double normalizedHue) {
        if (normalizedHue < ___MappedColorConstants.MIN_COMPONENT_NORMALIZED ||
            normalizedHue > ___MappedColorConstants.MAX_COMPONENT_NORMALIZED) {
            throw new IllegalArgumentException("invalid hue: " + normalizedHue);
        }
        setHue(
                (int) (normalizedHue * MAX_HUE)
        );
    }

    public SELF normalizedHue(final double normalizedHue) {
        setNormalizedHue(normalizedHue);
        return (SELF) this;
    }

    // ------------------------------------------------------------------------------------------------------ saturation
    @Max(MAX_SATURATION)
    @Min(MIN_SATURATION)
    @Transient
    public int getSaturation() {
        return getComponent(COMPONENT_INDEX_S);
    }

    public void setSaturation(final int saturation) {
        if (saturation < MIN_SATURATION ||
            saturation > MAX_SATURATION) {
            throw new IllegalArgumentException("invalid saturation: " + saturation);
        }
        setComponent(
                COMPONENT_INDEX_S,
                saturation
        );
    }

    public SELF saturation(final int saturation) {
        setSaturation(saturation);
        return (SELF) this;
    }

    @DecimalMax(___MappedColorConstants.DECIMAL_MAX_COMPONENT_NORMALIZED)
    @DecimalMin(___MappedColorConstants.DECIMAL_MIN_COMPONENT_NORMALIZED)
    @Transient
    public double getNormalizedSaturation() {
        return getSaturation() / (double) MAX_SATURATION;
    }

    public void setNormalizedSaturation(final double normalizedSaturation) {
        if (normalizedSaturation < ___MappedColorConstants.MIN_COMPONENT_NORMALIZED ||
            normalizedSaturation > ___MappedColorConstants.MAX_COMPONENT_NORMALIZED) {
            throw new IllegalArgumentException("invalid normalized saturation: " + normalizedSaturation);
        }
        setSaturation((int) (normalizedSaturation * MAX_SATURATION));
    }

    public SELF normalizedSaturation(final double normalizedSaturation) {
        setNormalizedSaturation(normalizedSaturation);
        return (SELF) this;
    }

    // ------------------------------------------------------------------------------------------------------ lightness
    @Max(MAX_LIGHTNESS)
    @Min(MIN_LIGHTNESS)
    @Transient
    public int getLightness() {
        return getComponent(COMPONENT_INDEX_L);
    }

    public void setLightness(final int lightness) {
        if (lightness < MIN_LIGHTNESS ||
            lightness > MAX_LIGHTNESS) {
            throw new IllegalArgumentException("invalid lightness: " + lightness);
        }
        setComponent(
                COMPONENT_INDEX_L,
                lightness
        );
    }

    public SELF lightness(final int lightness) {
        setLightness(lightness);
        return (SELF) this;
    }

    @DecimalMax(___MappedColorConstants.DECIMAL_MAX_COMPONENT_NORMALIZED)
    @DecimalMin(___MappedColorConstants.DECIMAL_MIN_COMPONENT_NORMALIZED)
    @Transient
    public double getNormalizedLightness() {
        return getLightness() / (double) MAX_LIGHTNESS;
    }

    public void setNormalizedLightness(final double normalizedLightness) {
        if (normalizedLightness < ___MappedColorConstants.MIN_COMPONENT_NORMALIZED ||
            normalizedLightness > ___MappedColorConstants.MAX_COMPONENT_NORMALIZED) {
            throw new IllegalArgumentException("invalid normalized lightness: " + normalizedLightness);
        }
        setLightness(
                (int) (normalizedLightness * MAX_LIGHTNESS)
        );
    }

    public SELF normalizedLightness(final double normalizedLightness) {
        setNormalizedLightness(normalizedLightness);
        return (SELF) this;
    }

    // ----------------------------------------------------------------------------------------------------------- alpha
    @Max(MAX_ALPHA)
    @Min(MIN_ALPHA)
    @Transient
    public int getAlpha() {
        return getComponent(COMPONENT_INDEX_A);
    }

    public void setAlpha(final int alpha) {
        if (alpha < MIN_ALPHA ||
            alpha > MAX_ALPHA) {
            throw new IllegalArgumentException("invalid alpha: " + alpha);
        }
        setComponent(COMPONENT_INDEX_A, alpha);
    }

    public SELF alpha(final int alpha) {
        setAlpha(alpha);
        return (SELF) this;
    }

    @DecimalMax(___MappedColorConstants.DECIMAL_MAX_COMPONENT_NORMALIZED)
    @DecimalMin(___MappedColorConstants.DECIMAL_MIN_COMPONENT_NORMALIZED)
    @Transient
    public double getNormalizedAlpha() {
        return getAlpha() / (double) MAX_ALPHA;
    }

    public void setNormalizedAlpha(final double normalizedAlpha) {
        if (normalizedAlpha < ___MappedColorConstants.MIN_COMPONENT_NORMALIZED ||
            normalizedAlpha > ___MappedColorConstants.MAX_COMPONENT_NORMALIZED) {
            throw new IllegalArgumentException("invalid normalized alpha: " + normalizedAlpha);
        }
        setAlpha((int) (normalizedAlpha * MAX_ALPHA));
    }

    public SELF normalizedAlpha(final double normalizedAlpha) {
        setNormalizedAlpha(normalizedAlpha);
        return (SELF) this;
    }
}
