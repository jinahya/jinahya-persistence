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
            final IntFunction< // h
                    ? extends IntFunction< // s
                            ? extends IntFunction< // l
                                    ? extends IntFunction< // a
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

    // -----------------------------------------------------------------------------------------------------------------
    public <R> R toRgb(
            final DoubleFunction<
                    ? extends DoubleFunction<
                            ? extends DoubleFunction<
                                    ? extends R>>> function) {
        Objects.requireNonNull(function, "function is null");
        return applyComponents(
                h -> s -> l -> a -> __MappedHwbUtils.hwbToRgb(h, s, l, function)
        );
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
    public int getHue() {
        return getComponent(COMPONENT_INDEX_H);
    }

    /**
     * Replaces current value of {@code hue} component with the specified value.
     *
     * @param hue new value for the {@code hue} component between {@value __MappedHwb#HWB_MIN_HUE} and
     *            {@value __MappedHwb#HWB_MAX_HUE}, both inclusive.
     */
    public void setHue(final int hue) {
        if (hue < HWB_MIN_HUE || hue > HWB_MAX_HUE) {
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
        return getHue() / (double) HWB_MAX_HUE;
    }

    public void setNormalizedHue(final double normalizedHue) {
        if (normalizedHue < ___MappedColorConstants.MIN_COMPONENT_NORMALIZED ||
            normalizedHue > ___MappedColorConstants.MAX_COMPONENT_NORMALIZED) {
            throw new IllegalArgumentException("invalid hue: " + normalizedHue);
        }
        setHue(
                (int) (normalizedHue * HWB_MAX_HUE)
        );
    }

    public SELF normalizedHue(final double normalizedHue) {
        setNormalizedHue(normalizedHue);
        return (SELF) this;
    }

    // ------------------------------------------------------------------------------------------------------ whiteness
    @Max(HWB_MAX_WHITENESS)
    @Min(HWB_MIN_WHITENESS)
    @Transient
    public int getWhiteness() {
        return getComponent(COMPONENT_INDEX_S);
    }

    public void setWhiteness(final int whiteness) {
        if (whiteness < HWB_MIN_WHITENESS ||
            whiteness > HWB_MAX_WHITENESS) {
            throw new IllegalArgumentException("invalid whiteness: " + whiteness);
        }
        setComponent(
                COMPONENT_INDEX_S,
                whiteness
        );
    }

    public SELF whiteness(final int whiteness) {
        setWhiteness(whiteness);
        return (SELF) this;
    }

    @DecimalMax(___MappedColorConstants.DECIMAL_MAX_COMPONENT_NORMALIZED)
    @DecimalMin(___MappedColorConstants.DECIMAL_MIN_COMPONENT_NORMALIZED)
    @Transient
    public double getNormalizedWhiteness() {
        return getWhiteness() / (double) HWB_MAX_WHITENESS;
    }

    public void setNormalizedWhiteness(final double normalizedWhiteness) {
        if (normalizedWhiteness < ___MappedColorConstants.MIN_COMPONENT_NORMALIZED ||
            normalizedWhiteness > ___MappedColorConstants.MAX_COMPONENT_NORMALIZED) {
            throw new IllegalArgumentException("invalid normalized whiteness: " + normalizedWhiteness);
        }
        setWhiteness((int) (normalizedWhiteness * HWB_MAX_WHITENESS));
    }

    public SELF normalizedWhiteness(final double normalizedWhiteness) {
        setNormalizedWhiteness(normalizedWhiteness);
        return (SELF) this;
    }

    // ------------------------------------------------------------------------------------------------------ blackness
    @Max(HWB_MAX_BLACKNESS)
    @Min(HWB_MIN_BLACKNESS)
    @Transient
    public int getBlackness() {
        return getComponent(COMPONENT_INDEX_L);
    }

    public void setBlackness(final int blackness) {
        if (blackness < HWB_MIN_BLACKNESS ||
            blackness > HWB_MAX_BLACKNESS) {
            throw new IllegalArgumentException("invalid blackness: " + blackness);
        }
        setComponent(
                COMPONENT_INDEX_L,
                blackness
        );
    }

    public SELF blackness(final int blackness) {
        setBlackness(blackness);
        return (SELF) this;
    }

    @DecimalMax(___MappedColorConstants.DECIMAL_MAX_COMPONENT_NORMALIZED)
    @DecimalMin(___MappedColorConstants.DECIMAL_MIN_COMPONENT_NORMALIZED)
    @Transient
    public double getNormalizedBlackness() {
        return getBlackness() / (double) HWB_MAX_BLACKNESS;
    }

    public void setNormalizedBlackness(final double normalizedBlackness) {
        if (normalizedBlackness < ___MappedColorConstants.MIN_COMPONENT_NORMALIZED ||
            normalizedBlackness > ___MappedColorConstants.MAX_COMPONENT_NORMALIZED) {
            throw new IllegalArgumentException("invalid normalized blackness: " + normalizedBlackness);
        }
        setBlackness(
                (int) (normalizedBlackness * HWB_MAX_BLACKNESS)
        );
    }

    public SELF normalizedBlackness(final double normalizedBlackness) {
        setNormalizedBlackness(normalizedBlackness);
        return (SELF) this;
    }

    // ----------------------------------------------------------------------------------------------------------- alpha
    @Max(HWB_MAX_ALPHA)
    @Min(HWB_MIN_ALPHA)
    @Transient
    public int getAlpha() {
        return getComponent(COMPONENT_INDEX_A);
    }

    public void setAlpha(final int alpha) {
        if (alpha < HWB_MIN_ALPHA ||
            alpha > HWB_MAX_ALPHA) {
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
        return getAlpha() / (double) HWB_MAX_ALPHA;
    }

    public void setNormalizedAlpha(final double normalizedAlpha) {
        if (normalizedAlpha < ___MappedColorConstants.MIN_COMPONENT_NORMALIZED ||
            normalizedAlpha > ___MappedColorConstants.MAX_COMPONENT_NORMALIZED) {
            throw new IllegalArgumentException("invalid normalized alpha: " + normalizedAlpha);
        }
        setAlpha((int) (normalizedAlpha * HWB_MAX_ALPHA));
    }

    public SELF normalizedAlpha(final double normalizedAlpha) {
        setNormalizedAlpha(normalizedAlpha);
        return (SELF) this;
    }
}
