package com.github.jinahya.persistence.more;

import jakarta.annotation.Nonnull;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

import java.io.Serial;
import java.util.Objects;
import java.util.function.DoubleFunction;

/**
 * .
 *
 * @param <SELF> .
 * @see <a href="https://www.w3.org/TR/css-color-4/">CSS Color Module Level 4</a>
 */
@MappedSuperclass
@SuppressWarnings({
        "java:S100", // Method names should comply with a naming convention
        "java:S101", // Class names should comply with a naming convention
        "java:S115", // Constant names should comply with a naming convention
        "java:S116", // Field names should comply with a naming convention
        "java:S117",  // Local variable and method parameter names should comply with a naming convention
        "java:S119" // Type parameter names should comply with a naming convention
})
public abstract class __MappedRgb<SELF extends __MappedRgb<SELF>> extends ___MappedColor<SELF> {

    @Serial
    private static final long serialVersionUID = -5905278228337798453L;

    // -----------------------------------------------------------------------------------------------------------------
    public static final String DECIMAL_MIN_COMPONENT = "0.0";

    public static final String DECIMAL_MAX_COMPONENT = "255.0";

    public static final float MIN_COMPONENT = 0.0f;

    public static final float MAX_COMPONENT = 255.0f;

    // -----------------------------------------------------------------------------------------------------------------
    public static final String DECIMAL_MIN_NORMALIZED_COMPONENT = "0.0";

    public static final String DECIMAL_MAX_NORMALIZED_COMPONENT = "1.0";

    public static final float MIN_NORMALIZED_COMPONENT = 0.0f;

    public static final float MAX_NORMALIZED_COMPONENT = 1.0f;

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
    protected __MappedRgb() {
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
        return String.format("%1$x%2$x%3$x", ((int) getRed()) >> 4, ((int) getGreen()) >> 4, ((int) getBlue()) >> 4);
    }

    @Nonnull
    public String toHexadecimalNotation4() {
        return String.format("%1$s%2$x", toHexadecimalNotation3(), ((int) getAlpha()) >> 4);
    }

    @Nonnull
    public String toHexadecimalNotation6() {
        return String.format("%1$02x%2$02x%3$02x", (int) getRed(), (int) getGreen(), (int) getBlue());
    }

    @Nonnull
    public String toHexadecimalNotation8() {
        return String.format("%1$s%2$02x", toHexadecimalNotation6(), (int) getAlpha());
    }

    // -----------------------------------------------------------------------------------------------------------------

    public <R> R applyComponents(
            final DoubleFunction< // r
                    ? extends DoubleFunction< // g
                            ? extends DoubleFunction< // b
                                    ? extends DoubleFunction< // a
                                            ? extends R>>>> function) {
        return Objects.requireNonNull(function, "function is null")
                .apply(getRed())
                .apply(getGreen())
                .apply(getBlue())
                .apply(getAlpha());
    }

    public <R> R applyNormalizedComponents(
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
    @DecimalMax(DECIMAL_MAX_COMPONENT)
    @DecimalMin(DECIMAL_MIN_COMPONENT)
    @Transient
    public float getRed() {
        return getComponent(COMPONENT_INDEX_R);
    }

    public void setRed(final float red) {
        if (red < MIN_COMPONENT || red > MAX_COMPONENT) {
            throw new IllegalArgumentException("invalid red: " + red);
        }
        setComponent(COMPONENT_INDEX_R, red);
    }

    @SuppressWarnings({"unchecked"})
    public SELF red(final float red) {
        setRed(red);
        return (SELF) this;
    }

    @DecimalMax(DECIMAL_MAX_NORMALIZED_COMPONENT)
    @DecimalMin(DECIMAL_MIN_NORMALIZED_COMPONENT)
    public float getNormalizedRed() {
        return getRed() / MAX_COMPONENT;
    }

    public void setNormalizedRed(final float normalizedRed) {
        if (normalizedRed < MIN_NORMALIZED_COMPONENT || normalizedRed > MAX_NORMALIZED_COMPONENT) {
            throw new IllegalArgumentException("invalid normalized red: " + normalizedRed);
        }
        setRed(normalizedRed * MAX_COMPONENT);
    }

    @SuppressWarnings({"unchecked"})
    public SELF normalizedRed(final float normalizedRed) {
        setNormalizedRed(normalizedRed);
        return (SELF) this;
    }

    // ----------------------------------------------------------------------------------------------------------- green
    @DecimalMax(DECIMAL_MAX_COMPONENT)
    @DecimalMin(DECIMAL_MIN_COMPONENT)
    @Transient
    public float getGreen() {
        return getComponent(COMPONENT_INDEX_G);
    }

    public void setGreen(final float green) {
        if (green < MIN_COMPONENT || green > MAX_COMPONENT) {
            throw new IllegalArgumentException("invalid green: " + green);
        }
        setComponent(COMPONENT_INDEX_G, green);
    }

    @SuppressWarnings({"unchecked"})
    public SELF green(final float green) {
        setGreen(green);
        return (SELF) this;
    }

    @DecimalMax(DECIMAL_MAX_NORMALIZED_COMPONENT)
    @DecimalMin(DECIMAL_MIN_NORMALIZED_COMPONENT)
    public float getNormalizedGreen() {
        return getGreen() / MAX_COMPONENT;
    }

    public void setNormalizedGreen(final float normalizedGreen) {
        if (normalizedGreen < MIN_NORMALIZED_COMPONENT || normalizedGreen > MAX_NORMALIZED_COMPONENT) {
            throw new IllegalArgumentException("invalid normalized green: " + normalizedGreen);
        }
        setGreen(normalizedGreen * MAX_COMPONENT);
    }

    @SuppressWarnings({"unchecked"})
    public SELF normalizedGreen(final float normalizedGreen) {
        setNormalizedGreen(normalizedGreen);
        return (SELF) this;
    }

    // ------------------------------------------------------------------------------------------------------------ blue
    @DecimalMax(DECIMAL_MAX_COMPONENT)
    @DecimalMin(DECIMAL_MIN_COMPONENT)
    @Transient
    public float getBlue() {
        return getComponent(COMPONENT_INDEX_B);
    }

    public void setBlue(final float blue) {
        if (blue < MIN_COMPONENT || blue > MAX_COMPONENT) {
            throw new IllegalArgumentException("invalid blue: " + blue);
        }
        setComponent(COMPONENT_INDEX_B, blue);
    }

    @SuppressWarnings({"unchecked"})
    public SELF blue(final float blue) {
        setBlue(blue);
        return (SELF) this;
    }

    @DecimalMax(DECIMAL_MAX_NORMALIZED_COMPONENT)
    @DecimalMin(DECIMAL_MIN_NORMALIZED_COMPONENT)
    public float getNormalizedBlue() {
        return getBlue() / MAX_COMPONENT;
    }

    public void setNormalizedBlue(final float normalizedBlue) {
        if (normalizedBlue < MIN_NORMALIZED_COMPONENT || normalizedBlue > MAX_NORMALIZED_COMPONENT) {
            throw new IllegalArgumentException("invalid normalized blue: " + normalizedBlue);
        }
        setBlue(normalizedBlue * MAX_COMPONENT);
    }

    @SuppressWarnings({"unchecked"})
    public SELF normalizedBlue(final float normalizedBlue) {
        setNormalizedBlue(normalizedBlue);
        return (SELF) this;
    }

    // ----------------------------------------------------------------------------------------------------------- alpha
    @DecimalMax(DECIMAL_MAX_COMPONENT)
    @DecimalMin(DECIMAL_MIN_COMPONENT)
    @Transient
    public float getAlpha() {
        return getComponent(COMPONENT_INDEX_A);
    }

    public void setAlpha(final float alpha) {
        if (alpha < MIN_COMPONENT || alpha > MAX_COMPONENT) {
            throw new IllegalArgumentException("invalid alpha: " + alpha);
        }
        setComponent(COMPONENT_INDEX_A, alpha);
    }

    @SuppressWarnings({"unchecked"})
    public SELF alpha(final float alpha) {
        setAlpha(alpha);
        return (SELF) this;
    }

    @DecimalMax(DECIMAL_MAX_NORMALIZED_COMPONENT)
    @DecimalMin(DECIMAL_MIN_NORMALIZED_COMPONENT)
    public float getNormalizedAlpha() {
        return getAlpha() / MAX_COMPONENT;
    }

    public void setNormalizedAlpha(final float normalizedAlpha) {
        if (normalizedAlpha < MIN_NORMALIZED_COMPONENT || normalizedAlpha > MAX_NORMALIZED_COMPONENT) {
            throw new IllegalArgumentException("invalid normalized alpha: " + normalizedAlpha);
        }
        setAlpha(normalizedAlpha * MAX_COMPONENT);
    }

    @SuppressWarnings({"unchecked"})
    public SELF normalizedAlpha(final float normalizedAlpha) {
        setNormalizedAlpha(normalizedAlpha);
        return (SELF) this;
    }
}
