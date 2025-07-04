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
import java.util.function.IntFunction;

/**
 * .
 *
 * @param <SELF> .
 * @see <a href="https://www.w3.org/TR/css-color-4/">CSS Color Module Level 4</a>
 */
@MappedSuperclass
@SuppressWarnings({
        "java:S100", // Method names should comply with a naming convention
        "java:S115", // Constant names should comply with a naming convention
        "java:S116", // Field names should comply with a naming convention
        "java:S117"  // Local variable and method parameter names should comply with a naming convention
})
public abstract class __MappedRgba<SELF extends __MappedRgba<SELF>> extends ___MappedColor2<SELF> {

    @Serial
    private static final long serialVersionUID = -5905278228337798453L;

    // -----------------------------------------------------------------------------------------------------------------
    protected static final int COMPONENT_INDEX_R = 0;

    protected static final int COMPONENT_INDEX_G = 1;

    protected static final int COMPONENT_INDEX_B = 2;

    protected static final int COMPONENT_INDEX_A = 3;

    // ------------------------------------------------------------------------------------------ STATIC_FACTORY_METHODS

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected __MappedRgba() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    // -------------------------------------------------------------------------------------------- hexadecimalNotations
    @Nonnull
    public String toHexadecimalNotation(final int digits) {
        return switch (digits) {
            case 3 -> toHexadecimalNotation3();
            case 4 -> toHexadecimalNotation4();
            case 6 -> toHexadecimalNotation6();
            case 8 -> toHexadecimalNotation8();
            default -> throw new IllegalArgumentException("unknown digits: " + digits);
        };
    }

    @Nonnull
    public String toHexadecimalNotation3() {
        return String.format("%1$x%2$x%3$x", getRed() >> 4, getGreen() >> 4, getBlue() >> 4);
    }

    @Nonnull
    public String toHexadecimalNotation4() {
        return String.format("%1$s%2$x", toHexadecimalNotation3(), getAlpha() >> 4);
    }

    @Nonnull
    public String toHexadecimalNotation6() {
        return String.format("%1$02x%2$02x%3$02x", getRed(), getGreen(), getBlue());
    }

    @Nonnull
    public String toHexadecimalNotation8() {
        return String.format("%1$s%2$02x", toHexadecimalNotation6(), getAlpha());
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    public <T extends __MappedHsla<T>> T toHsl(@Nonnull final T hsl) {
        Objects.requireNonNull(hsl, "hsl is null");
        return ___MappedColorUtils.rgbToHsl(
                getNormalizedRed(),
                getNormalizedGreen(),
                getNormalizedBlue(),
                h -> s -> l -> {
                    return hsl.hue(h).normalizedSaturation(s).normalizedLightness(l);
                }
        );
    }

    @Nonnull
    public <T extends __MappedHsla<T>> T toHsla(@Nonnull final T hsla) {
        return toHsla(hsla).normalizedAlpha(getNormalizedAlpha());
    }

    @Nonnull
    public <T extends __MappedHsla<T>> SELF fromHsl(@Nonnull final T hsl) {
        Objects.requireNonNull(hsl, "hsl is null");
        return ___MappedColorUtils.hslToRgb(
                hsl.getHue(),
                hsl.getNormalizedSaturation(),
                hsl.getNormalizedLightness(),
                r -> g -> b -> {
                    return normalizedRed(r)
                            .normalizedGreen(g)
                            .normalizedBlue(b);
                }
        );
    }

    @Nonnull
    public <T extends __MappedHsla<T>> SELF fromHsla(@Nonnull final T hsla) {
        return fromHsl(hsla).normalizedAlpha(hsla.getNormalizedAlpha());
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Applies all components to the specified function, and returns the result.
     * <p>
     * {@snippet lang = "java":
     * final var color = entity.apply(r -> g -> b -> a -> new java.awt.Color(r, g, b, a));
     *}
     *
     * @param function the function
     * @param <R>      result type parameter
     * @return the result of the {@code function}.
     */
    public <R> R apply(
            final IntFunction< // r
                    ? extends IntFunction< // g
                            ? extends IntFunction< // b
                                    ? extends IntFunction< // a
                                            ? extends R>>>> function) {
        return Objects.requireNonNull(function, "function is null")
                .apply(getRed())
                .apply(getGreen())
                .apply(getBlue())
                .apply(getAlpha());
    }

    public <R> R applyNormalized(
            final DoubleFunction< // r
                    ? extends DoubleFunction< // g
                            ? extends DoubleFunction< // b
                                    ? extends DoubleFunction< // a
                                            ? extends R>>>> function) {
        return Objects.requireNonNull(function, "function is null")
                .apply(getNormalizedRed())
                .apply(getNormalizedGreen())
                .apply(getNormalizedBlue())
                .apply(getNormalizedAlpha());
    }

    // ------------------------------------------------------------------------------------------------------------- red
    @Max(___MappedColorConstants.RGB_MAX_COMPONENT)
    @Min(___MappedColorConstants.RGB_MIN_COMPONENT)
    @Transient
    public int getRed() {
        return (int) (getNormalizedRed() * ___MappedColorConstants.RGB_MAX_COMPONENT);
    }

    public void setRed(final int red) {
        if (red < ___MappedColorConstants.RGB_MIN_COMPONENT ||
            red > ___MappedColorConstants.RGB_MAX_COMPONENT) {
            throw new IllegalArgumentException("invalid red: " + red);
        }
        setNormalizedRed(red / (double) ___MappedColorConstants.RGB_MAX_COMPONENT);
    }

    @SuppressWarnings({"unchecked"})
    public SELF red(final int red) {
        setRed(red);
        return (SELF) this;
    }

    @DecimalMax(___MappedColorConstants.DECIMAL_MAX_COMPONENT_NORMALIZED)
    @DecimalMin(___MappedColorConstants.DECIMAL_MIN_COMPONENT_NORMALIZED)
    public double getNormalizedRed() {
        return getComponent_(COMPONENT_INDEX_R);
    }

    public void setNormalizedRed(final double normalizedRed) {
        if (normalizedRed < ___MappedColorConstants.MIN_COMPONENT_NORMALIZED ||
            normalizedRed > ___MappedColorConstants.MAX_COMPONENT_NORMALIZED) {
            throw new IllegalArgumentException("invalid normalized red: " + normalizedRed);
        }
        setComponent_(COMPONENT_INDEX_R, normalizedRed);
    }

    @SuppressWarnings({"unchecked"})
    public SELF normalizedRed(final double normalizedRed) {
        setNormalizedRed(normalizedRed);
        return (SELF) this;
    }

    // ----------------------------------------------------------------------------------------------------------- green
    @Max(___MappedColorConstants.RGB_MAX_COMPONENT)
    @Min(___MappedColorConstants.RGB_MIN_COMPONENT)
    @Transient
    public int getGreen() {
        return (int) (getNormalizedGreen() * ___MappedColorConstants.RGB_MAX_COMPONENT);
    }

    public void setGreen(final int green) {
        if (green < ___MappedColorConstants.RGB_MIN_COMPONENT ||
            green > ___MappedColorConstants.RGB_MAX_COMPONENT) {
            throw new IllegalArgumentException("invalid green: " + green);
        }
        setNormalizedGreen(green / (double) ___MappedColorConstants.RGB_MAX_COMPONENT);
    }

    @SuppressWarnings({"unchecked"})
    public SELF green(final int green) {
        setGreen(green);
        return (SELF) this;
    }

    @DecimalMax(___MappedColorConstants.DECIMAL_MAX_COMPONENT_NORMALIZED)
    @DecimalMin(___MappedColorConstants.DECIMAL_MIN_COMPONENT_NORMALIZED)
    public double getNormalizedGreen() {
        return getComponent_(COMPONENT_INDEX_G);
    }

    public void setNormalizedGreen(final double normalizedGreen) {
        if (normalizedGreen < ___MappedColorConstants.MIN_COMPONENT_NORMALIZED ||
            normalizedGreen > ___MappedColorConstants.MAX_COMPONENT_NORMALIZED) {
            throw new IllegalArgumentException("invalid normalized green: " + normalizedGreen);
        }
        setComponent_(COMPONENT_INDEX_G, normalizedGreen);
    }

    @SuppressWarnings({"unchecked"})
    public SELF normalizedGreen(final double normalizedGreen) {
        setNormalizedGreen(normalizedGreen);
        return (SELF) this;
    }

    // ------------------------------------------------------------------------------------------------------------ blue
    @Max(___MappedColorConstants.RGB_MAX_COMPONENT)
    @Min(___MappedColorConstants.RGB_MIN_COMPONENT)
    @Transient
    public int getBlue() {
        return (int) (getNormalizedBlue() * ___MappedColorConstants.RGB_MAX_COMPONENT);
    }

    public void setBlue(final int blue) {
        if (blue < ___MappedColorConstants.RGB_MIN_COMPONENT ||
            blue > ___MappedColorConstants.RGB_MAX_COMPONENT) {
            throw new IllegalArgumentException("invalid blue: " + blue);
        }
        setNormalizedBlue(blue / (double) ___MappedColorConstants.RGB_MAX_COMPONENT);
    }

    @SuppressWarnings({"unchecked"})
    public SELF blue(final int blue) {
        setBlue(blue);
        return (SELF) this;
    }

    @DecimalMax(___MappedColorConstants.DECIMAL_MAX_COMPONENT_NORMALIZED)
    @DecimalMin(___MappedColorConstants.DECIMAL_MIN_COMPONENT_NORMALIZED)
    public double getNormalizedBlue() {
        return getComponent_(COMPONENT_INDEX_B);
    }

    public void setNormalizedBlue(final double normalizedBlue) {
        if (normalizedBlue < ___MappedColorConstants.MIN_COMPONENT_NORMALIZED ||
            normalizedBlue > ___MappedColorConstants.MAX_COMPONENT_NORMALIZED) {
            throw new IllegalArgumentException("invalid normalized blue: " + normalizedBlue);
        }
        setComponent_(COMPONENT_INDEX_B, normalizedBlue);
    }

    @SuppressWarnings({"unchecked"})
    public SELF normalizedBlue(final double normalizedBlue) {
        setNormalizedBlue(normalizedBlue);
        return (SELF) this;
    }

    // ----------------------------------------------------------------------------------------------------------- alpha
    @Max(___MappedColorConstants.RGB_MAX_COMPONENT)
    @Min(___MappedColorConstants.RGB_MIN_COMPONENT)
    @Transient
    public int getAlpha() {
        return (int) (getNormalizedAlpha() * ___MappedColorConstants.RGB_MAX_COMPONENT);
    }

    public void setAlpha(final int alpha) {
        if (alpha < ___MappedColorConstants.RGB_MIN_COMPONENT ||
            alpha > ___MappedColorConstants.RGB_MAX_COMPONENT) {
            throw new IllegalArgumentException("invalid alpha: " + alpha);
        }
        setNormalizedAlpha(alpha / (double) ___MappedColorConstants.RGB_MAX_COMPONENT);
    }

    @SuppressWarnings({"unchecked"})
    public SELF alpha(final int alpha) {
        setAlpha(alpha);
        return (SELF) this;
    }

    @DecimalMax(___MappedColorConstants.DECIMAL_MAX_COMPONENT_NORMALIZED)
    @DecimalMin(___MappedColorConstants.DECIMAL_MIN_COMPONENT_NORMALIZED)
    public double getNormalizedAlpha() {
        return getComponent_(COMPONENT_INDEX_A);
    }

    public void setNormalizedAlpha(final double normalizedAlpha) {
        if (normalizedAlpha < ___MappedColorConstants.MIN_COMPONENT_NORMALIZED ||
            normalizedAlpha > ___MappedColorConstants.MAX_COMPONENT_NORMALIZED) {
            throw new IllegalArgumentException("invalid normalized alpha: " + normalizedAlpha);
        }
        setComponent_(COMPONENT_INDEX_A, normalizedAlpha);
    }

    @SuppressWarnings({"unchecked"})
    public SELF normalizedAlpha(final double normalizedAlpha) {
        setNormalizedAlpha(normalizedAlpha);
        return (SELF) this;
    }
}
