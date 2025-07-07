package com.github.jinahya.persistence.more;

import jakarta.annotation.Nonnull;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.io.Serial;
import java.util.Objects;
import java.util.function.DoubleFunction;

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
            @Nonnull final DoubleFunction< // h
                    ? extends DoubleFunction< // s
                            ? extends DoubleFunction< // l
                                    ? extends DoubleFunction< // a
                                            ? extends R>>>> function) {
        Objects.requireNonNull(function, "function is null");
        return function
                .apply(getHue())
                .apply(getSaturation())
                .apply(getLightness())
                .apply(getAlpha());
    }

    public <R> R applyNormalizedComponents(
            @Nonnull final DoubleFunction< // h
                    ? extends DoubleFunction< // s
                            ? extends DoubleFunction< // l
                                    ? extends DoubleFunction< // a
                                            ? extends R>>>> function) {
        Objects.requireNonNull(function, "function is null");
        return function
                .apply(getNormalizedHue())
                .apply(getNormalizedSaturation())
                .apply(getNormalizedLightness())
                .apply(getNormalizedAlpha());
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
    public float getHue() {
        return getComponent(COMPONENT_INDEX_H);
    }

    /**
     * Replaces current value of {@code hue} component with the specified value.
     *
     * @param hue new value for the {@code hue} component between {@value __MappedHsl#MIN_HUE} and
     *            {@value __MappedHsl#MAX_HUE}, both inclusive.
     */
    public void setHue(final float hue) {
        if (hue < MIN_HUE || hue > MAX_HUE) {
            throw new IllegalArgumentException("invalid hue: " + hue);
        }
        setComponent(
                COMPONENT_INDEX_H,
                hue
        );
    }

    @SuppressWarnings("unchecked")
    public SELF hue(final float hue) {
        setHue(hue);
        return (SELF) this;
    }

    @DecimalMax(___MappedColorConstants.DECIMAL_MAX_COMPONENT_NORMALIZED)
    @DecimalMin(___MappedColorConstants.DECIMAL_MIN_COMPONENT_NORMALIZED)
    @Transient
    public float getNormalizedHue() {
        return getHue() / MAX_HUE;
    }

    public void setNormalizedHue(final float normalizedHue) {
        if (normalizedHue < ___MappedColorConstants.MIN_COMPONENT_NORMALIZED ||
            normalizedHue > ___MappedColorConstants.MAX_COMPONENT_NORMALIZED) {
            throw new IllegalArgumentException("invalid hue: " + normalizedHue);
        }
        setHue(normalizedHue * MAX_HUE);
    }

    @SuppressWarnings("unchecked")
    public SELF normalizedHue(final float normalizedHue) {
        setNormalizedHue(normalizedHue);
        return (SELF) this;
    }

    // ------------------------------------------------------------------------------------------------------ saturation
    @Max(MAX_SATURATION)
    @Min(MIN_SATURATION)
    @Transient
    public float getSaturation() {
        return getComponent(COMPONENT_INDEX_S);
    }

    public void setSaturation(final float saturation) {
        if (saturation < MIN_SATURATION ||
            saturation > MAX_SATURATION) {
            throw new IllegalArgumentException("invalid saturation: " + saturation);
        }
        setComponent(
                COMPONENT_INDEX_S,
                saturation
        );
    }

    @SuppressWarnings("unchecked")
    public SELF saturation(final float saturation) {
        setSaturation(saturation);
        return (SELF) this;
    }

    @DecimalMax(___MappedColorConstants.DECIMAL_MAX_COMPONENT_NORMALIZED)
    @DecimalMin(___MappedColorConstants.DECIMAL_MIN_COMPONENT_NORMALIZED)
    @Transient
    public float getNormalizedSaturation() {
        return getSaturation() / MAX_SATURATION;
    }

    public void setNormalizedSaturation(final float normalizedSaturation) {
        if (normalizedSaturation < ___MappedColorConstants.MIN_COMPONENT_NORMALIZED ||
            normalizedSaturation > ___MappedColorConstants.MAX_COMPONENT_NORMALIZED) {
            throw new IllegalArgumentException("invalid normalized saturation: " + normalizedSaturation);
        }
        setSaturation(normalizedSaturation * MAX_SATURATION);
    }

    @SuppressWarnings("unchecked")
    public SELF normalizedSaturation(final float normalizedSaturation) {
        setNormalizedSaturation(normalizedSaturation);
        return (SELF) this;
    }

    // ------------------------------------------------------------------------------------------------------- lightness
    @Max(MAX_LIGHTNESS)
    @Min(MIN_LIGHTNESS)
    @Transient
    public float getLightness() {
        return getComponent(COMPONENT_INDEX_L);
    }

    public void setLightness(final float lightness) {
        if (lightness < MIN_LIGHTNESS ||
            lightness > MAX_LIGHTNESS) {
            throw new IllegalArgumentException("invalid lightness: " + lightness);
        }
        setComponent(
                COMPONENT_INDEX_L,
                lightness
        );
    }

    @SuppressWarnings("unchecked")
    public SELF lightness(final float lightness) {
        setLightness(lightness);
        return (SELF) this;
    }

    @DecimalMax(___MappedColorConstants.DECIMAL_MAX_COMPONENT_NORMALIZED)
    @DecimalMin(___MappedColorConstants.DECIMAL_MIN_COMPONENT_NORMALIZED)
    @Transient
    public float getNormalizedLightness() {
        return getLightness() / MAX_LIGHTNESS;
    }

    public void setNormalizedLightness(final float normalizedLightness) {
        if (normalizedLightness < ___MappedColorConstants.MIN_COMPONENT_NORMALIZED ||
            normalizedLightness > ___MappedColorConstants.MAX_COMPONENT_NORMALIZED) {
            throw new IllegalArgumentException("invalid normalized lightness: " + normalizedLightness);
        }
        setLightness(
                normalizedLightness * MAX_LIGHTNESS
        );
    }

    @SuppressWarnings("unchecked")
    public SELF normalizedLightness(final float normalizedLightness) {
        setNormalizedLightness(normalizedLightness);
        return (SELF) this;
    }

    // ----------------------------------------------------------------------------------------------------------- alpha
    @Max(MAX_ALPHA)
    @Min(MIN_ALPHA)
    @Transient
    public float getAlpha() {
        return getComponent(COMPONENT_INDEX_A);
    }

    public void setAlpha(final float alpha) {
        if (alpha < MIN_ALPHA || alpha > MAX_ALPHA) {
            throw new IllegalArgumentException("invalid alpha: " + alpha);
        }
        setComponent(COMPONENT_INDEX_A, alpha);
    }

    @SuppressWarnings("unchecked")
    public SELF alpha(final int alpha) {
        setAlpha(alpha);
        return (SELF) this;
    }

    @DecimalMax(___MappedColorConstants.DECIMAL_MAX_COMPONENT_NORMALIZED)
    @DecimalMin(___MappedColorConstants.DECIMAL_MIN_COMPONENT_NORMALIZED)
    @Transient
    public float getNormalizedAlpha() {
        return getAlpha() / MAX_ALPHA;
    }

    public void setNormalizedAlpha(final float normalizedAlpha) {
        if (normalizedAlpha < ___MappedColorConstants.MIN_COMPONENT_NORMALIZED ||
            normalizedAlpha > ___MappedColorConstants.MAX_COMPONENT_NORMALIZED) {
            throw new IllegalArgumentException("invalid normalized alpha: " + normalizedAlpha);
        }
        setAlpha(normalizedAlpha * MAX_ALPHA);
    }

    @SuppressWarnings("unchecked")
    public SELF normalizedAlpha(final float normalizedAlpha) {
        setNormalizedAlpha(normalizedAlpha);
        return (SELF) this;
    }
}
