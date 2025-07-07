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

@MappedSuperclass
public abstract class __MappedHwb<SELF extends __MappedHwb<SELF>> extends ___MappedColor<SELF> {

    @Serial
    private static final long serialVersionUID = 4172783566899888893L;

    // -----------------------------------------------------------------------------------------------------------------
    public static final int HWB_MIN_HUE = 0;

    public static final int HWB_MAX_HUE = 359;

    // -----------------------------------------------------------------------------------------------------------------
    public static final int HWB_MIN_WHITENESS = 0;

    public static final int HWB_MAX_WHITENESS = 100;

    public static final String DECIMAL_MIN_NORMALIZED_WHITENESS = "0.0";

    public static final String DECIMAL_MAX_NORMALIZED_WHITENESS = "1.0";

    public static final double MIN_NORMALIZED_WHITENESS = 0.0d;

    public static final double MAX_NORMALIZED_WHITENESS = 1.0;

    // -----------------------------------------------------------------------------------------------------------------
    public static final int HWB_MIN_BLACKNESS = 0;

    public static final int HWB_MAX_BLACKNESS = 100;

    public static final String DECIMAL_MIN_NORMALIZED_BLACKNESS = "0.0";

    public static final String DECIMAL_MAX_NORMALIZED_BLACKNESS = "1.0";

    public static final double MIN_NORMALIZED_BLACKNESS = 0.0d;

    public static final double MAX_NORMALIZED_BLACKNESS = 1.0;

    // -----------------------------------------------------------------------------------------------------------------
    public static final int HWB_MIN_ALPHA = 0;

    public static final int HWB_MAX_ALPHA = 100;

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
    protected __MappedHwb() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    // -----------------------------------------------------------------------------------------------------------------
    public <R> R applyComponents(
            final DoubleFunction< // h
                    ? extends DoubleFunction< // s
                            ? extends DoubleFunction< // l
                                    ? extends DoubleFunction< // a
                                            ? extends R>>>> function) {
        Objects.requireNonNull(function, "function is null");
        return function
                .apply(getHue())
                .apply(getWhiteness())
                .apply(getBlackness())
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
                .apply(getNormalizedWhiteness())
                .apply(getNormalizedBlackness())
                .apply(getNormalizedAlpha());
    }

    // ------------------------------------------------------------------------------------------------------------- hue

    /**
     * Returns current value of {@code hue} component.
     *
     * @return current value of {@code hue} component.
     */
    @Max(HWB_MAX_HUE)
    @Min(HWB_MIN_HUE)
    @Transient
    public float getHue() {
        return getComponent(COMPONENT_INDEX_H);
    }

    /**
     * Replaces current value of {@code hue} component with the specified value.
     *
     * @param hue new value for the {@code hue} component between {@value __MappedHwb#HWB_MIN_HUE} and
     *            {@value __MappedHwb#HWB_MAX_HUE}, both inclusive.
     */
    public void setHue(final float hue) {
        if (hue < HWB_MIN_HUE || hue > HWB_MAX_HUE) {
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
        return getHue() / HWB_MAX_HUE;
    }

    public void setNormalizedHue(final float normalizedHue) {
        if (normalizedHue < ___MappedColorConstants.MIN_COMPONENT_NORMALIZED ||
            normalizedHue > ___MappedColorConstants.MAX_COMPONENT_NORMALIZED) {
            throw new IllegalArgumentException("invalid hue: " + normalizedHue);
        }
        setHue(normalizedHue * HWB_MAX_HUE);
    }

    @SuppressWarnings("unchecked")
    public SELF normalizedHue(final float normalizedHue) {
        setNormalizedHue(normalizedHue);
        return (SELF) this;
    }

    // ------------------------------------------------------------------------------------------------------- whiteness
    @Max(HWB_MAX_WHITENESS)
    @Min(HWB_MIN_WHITENESS)
    @Transient
    public float getWhiteness() {
        return getComponent(COMPONENT_INDEX_S);
    }

    public void setWhiteness(final float whiteness) {
        if (whiteness < HWB_MIN_WHITENESS ||
            whiteness > HWB_MAX_WHITENESS) {
            throw new IllegalArgumentException("invalid whiteness: " + whiteness);
        }
        setComponent(COMPONENT_INDEX_S, whiteness);
    }

    @SuppressWarnings("unchecked")
    public SELF whiteness(final int whiteness) {
        setWhiteness(whiteness);
        return (SELF) this;
    }

    @DecimalMax(___MappedColorConstants.DECIMAL_MAX_COMPONENT_NORMALIZED)
    @DecimalMin(___MappedColorConstants.DECIMAL_MIN_COMPONENT_NORMALIZED)
    @Transient
    public float getNormalizedWhiteness() {
        return getWhiteness() / HWB_MAX_WHITENESS;
    }

    public void setNormalizedWhiteness(final float normalizedWhiteness) {
        if (normalizedWhiteness < ___MappedColorConstants.MIN_COMPONENT_NORMALIZED ||
            normalizedWhiteness > ___MappedColorConstants.MAX_COMPONENT_NORMALIZED) {
            throw new IllegalArgumentException("invalid normalized whiteness: " + normalizedWhiteness);
        }
        setWhiteness(normalizedWhiteness * HWB_MAX_WHITENESS);
    }

    @SuppressWarnings("unchecked")
    public SELF normalizedWhiteness(final float normalizedWhiteness) {
        setNormalizedWhiteness(normalizedWhiteness);
        return (SELF) this;
    }

    // ------------------------------------------------------------------------------------------------------- blackness
    @Max(HWB_MAX_BLACKNESS)
    @Min(HWB_MIN_BLACKNESS)
    @Transient
    public float getBlackness() {
        return getComponent(COMPONENT_INDEX_L);
    }

    public void setBlackness(final float blackness) {
        if (blackness < HWB_MIN_BLACKNESS ||
            blackness > HWB_MAX_BLACKNESS) {
            throw new IllegalArgumentException("invalid blackness: " + blackness);
        }
        setComponent(COMPONENT_INDEX_L, blackness);
    }

    @SuppressWarnings("unchecked")
    public SELF blackness(final float blackness) {
        setBlackness(blackness);
        return (SELF) this;
    }

    @DecimalMax(___MappedColorConstants.DECIMAL_MAX_COMPONENT_NORMALIZED)
    @DecimalMin(___MappedColorConstants.DECIMAL_MIN_COMPONENT_NORMALIZED)
    @Transient
    public float getNormalizedBlackness() {
        return getBlackness() / HWB_MAX_BLACKNESS;
    }

    public void setNormalizedBlackness(final float normalizedBlackness) {
        if (normalizedBlackness < ___MappedColorConstants.MIN_COMPONENT_NORMALIZED ||
            normalizedBlackness > ___MappedColorConstants.MAX_COMPONENT_NORMALIZED) {
            throw new IllegalArgumentException("invalid normalized blackness: " + normalizedBlackness);
        }
        setBlackness(normalizedBlackness * HWB_MAX_BLACKNESS);
    }

    @SuppressWarnings("unchecked")
    public SELF normalizedBlackness(final float normalizedBlackness) {
        setNormalizedBlackness(normalizedBlackness);
        return (SELF) this;
    }

    // ----------------------------------------------------------------------------------------------------------- alpha
    @Max(HWB_MAX_ALPHA)
    @Min(HWB_MIN_ALPHA)
    @Transient
    public float getAlpha() {
        return getComponent(COMPONENT_INDEX_A);
    }

    public void setAlpha(final float alpha) {
        if (alpha < HWB_MIN_ALPHA || alpha > HWB_MAX_ALPHA) {
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
        return getAlpha() / HWB_MAX_ALPHA;
    }

    public void setNormalizedAlpha(final float normalizedAlpha) {
        if (normalizedAlpha < ___MappedColorConstants.MIN_COMPONENT_NORMALIZED ||
            normalizedAlpha > ___MappedColorConstants.MAX_COMPONENT_NORMALIZED) {
            throw new IllegalArgumentException("invalid normalized alpha: " + normalizedAlpha);
        }
        setAlpha((int) (normalizedAlpha * HWB_MAX_ALPHA));
    }

    @SuppressWarnings("unchecked")
    public SELF normalizedAlpha(final float normalizedAlpha) {
        setNormalizedAlpha(normalizedAlpha);
        return (SELF) this;
    }
}
