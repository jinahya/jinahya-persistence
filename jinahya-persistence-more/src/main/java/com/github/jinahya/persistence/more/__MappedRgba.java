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
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.Objects;
import java.util.Optional;
import java.util.function.IntFunction;

/**
 * .
 *
 * @param <SELF> .
 * @see <a href="https://www.w3.org/TR/css-color-4/">CSS Color Module Level 4</a>
 */
@Embeddable
@MappedSuperclass
public abstract class __MappedRgba<SELF extends __MappedRgba<SELF>> extends ___MappedColor<SELF> {

    @Serial
    private static final long serialVersionUID = -5905278228337798453L;

    // -----------------------------------------------------------------------------------------------------------------
    public static final int MIN_COMPONENT = 0x00;

    public static final int MAX_COMPONENT = 0xFF;

    public static final String DECIMAL_MIN_COMPONENT_AS_DOUBLE = "0.0d";

    public static final String DECIMAL_MAX_COMPONENT_AS_DOUBLE = "1.0d";

    public static final double MIN_COMPONENT_AS_DOUBLE = new BigInteger(DECIMAL_MIN_COMPONENT_AS_DOUBLE).doubleValue();

    public static final double MAX_COMPONENT_AS_DOUBLE = new BigInteger(DECIMAL_MAX_COMPONENT_AS_DOUBLE).doubleValue();

    // ---------------------------------------------------------------------------------------------------------- value_
    public static final String COLUMN_NAME_VALUE_ = "value_";

    public static final int COLUMN_LENGTH_VALUE_ = 4;

    public static final String ATTRIBUTE_NAME_VALUE_ = COLUMN_NAME_VALUE_;

    public static final int SIZE_MIN_VALUE_ = COLUMN_LENGTH_VALUE_;

    public static final int SIZE_MAX_VALUE_ = SIZE_MIN_VALUE_;

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

    @Override
    public String toString() {
        return super.toString() + '{' +
               "value_=" +
               Optional.ofNullable(getValue_()).map(v -> HexFormat.of().withPrefix("0x").formatHex(v)).orElse(null) +
               '}';
    }

    // -----------------------------------------------------------------------------------------------------------------
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
            final IntFunction<
                    ? extends IntFunction<
                            ? extends IntFunction<
                                    ? extends IntFunction<
                                            ? extends R>>>> function) {
        Objects.requireNonNull(function, "function is null");
        return function
                .apply(getRed())
                .apply(getGreen())
                .apply(getBlue())
                .apply(getAlpha());
    }

    // ------------------------------------------------------------------------------------------------------------- red

    /**
     * Returns the current value of this color's <span style="color:red; -webkit-text-stroke: .5px black;">red</span>
     * component.
     *
     * @return the current value of the <span style="color:red; -webkit-text-stroke: .5px black;">red</span> component
     */
    @Max(MAX_COMPONENT)
    @Min(MIN_COMPONENT)
    @Transient
    public int getRed() {
        return Optional.ofNullable(getValue_())
                       .orElseGet(
                               () -> value_(new byte[COLUMN_LENGTH_VALUE_]).getValue_()
                       )[COMPONENT_INDEX_R] & 0xFF;
    }

    /**
     * Replaces the current value of this color's <span style="color:red; -webkit-text-stroke: .5px black;">red</span>
     * component with the specified value.
     *
     * @param red new value for the <span style="color:red; -webkit-text-stroke: .5px black;">red</span> component.
     */
    public void setRed(final int red) {
        Optional.ofNullable(getValue_())
                .orElseGet(
                        () -> value_(new byte[COLUMN_LENGTH_VALUE_]).getValue_()
                )[COMPONENT_INDEX_R] = (byte) (red & 0xFF);
    }

    @SuppressWarnings({"unchecked"})
    public SELF red(final int red) {
        setRed(red);
        return (SELF) this;
    }

    @DecimalMax(DECIMAL_MAX_COMPONENT_AS_DOUBLE)
    @DecimalMin(DECIMAL_MIN_COMPONENT_AS_DOUBLE)
    public double getRedAsDoubleAsDouble() {
        return ((double) getRed()) / MAX_COMPONENT;
    }

    public void setRedAsDouble(final double redAsDouble) {
        if (redAsDouble < MIN_COMPONENT_AS_DOUBLE || MAX_COMPONENT_AS_DOUBLE < redAsDouble) {
            throw new IllegalArgumentException("invalid redAsDouble: " + redAsDouble);
        }
        setRed((int) (redAsDouble * MAX_COMPONENT));
    }

    @SuppressWarnings({"unchecked"})
    public SELF redAsDouble(final double redAsDouble) {
        setRedAsDouble(redAsDouble);
        return (SELF) this;
    }

    // ----------------------------------------------------------------------------------------------------------- green
    @Max(MAX_COMPONENT)
    @Min(MIN_COMPONENT)
    @Transient
    public int getGreen() {
        return Optional.ofNullable(getValue_())
                       .orElseGet(
                               () -> value_(new byte[COLUMN_LENGTH_VALUE_]).getValue_()
                       )[COMPONENT_INDEX_G] & 0xFF;
    }

    public void setGreen(final int green) {
        Optional.ofNullable(getValue_())
                .orElseGet(
                        () -> value_(new byte[COLUMN_LENGTH_VALUE_]).getValue_()
                )[COMPONENT_INDEX_G] = (byte) (green & 0xFF);
    }

    @DecimalMax(DECIMAL_MAX_COMPONENT_AS_DOUBLE)
    @DecimalMin(DECIMAL_MIN_COMPONENT_AS_DOUBLE)
    public double getGreenAsDoubleAsDouble() {
        return ((double) getGreen()) / MAX_COMPONENT;
    }

    public void setGreenAsDouble(final double greenAsDouble) {
        if (greenAsDouble < MIN_COMPONENT_AS_DOUBLE || MAX_COMPONENT_AS_DOUBLE < greenAsDouble) {
            throw new IllegalArgumentException("invalid greenAsDouble: " + greenAsDouble);
        }
        setGreen((int) (greenAsDouble * MAX_COMPONENT));
    }

    @SuppressWarnings({"unchecked"})
    public SELF greenAsDouble(final double greenAsDouble) {
        setGreenAsDouble(greenAsDouble);
        return (SELF) this;
    }

    // ------------------------------------------------------------------------------------------------------------ blue
    @Max(MAX_COMPONENT)
    @Min(MIN_COMPONENT)
    @Transient
    public int getBlue() {
        return Optional.ofNullable(getValue_())
                       .orElseGet(
                               () -> value_(new byte[COLUMN_LENGTH_VALUE_]).getValue_()
                       )[COMPONENT_INDEX_B] & 0xFF;
    }

    public void setBlue(final int blue) {
        Optional.ofNullable(getValue_())
                .orElseGet(
                        () -> value_(new byte[COLUMN_LENGTH_VALUE_]).getValue_()
                )[COMPONENT_INDEX_B] = (byte) (blue & 0xFF);
    }

    @DecimalMax(DECIMAL_MAX_COMPONENT_AS_DOUBLE)
    @DecimalMin(DECIMAL_MIN_COMPONENT_AS_DOUBLE)
    public double getBlueAsDoubleAsDouble() {
        return ((double) getBlue()) / MAX_COMPONENT;
    }

    public void setBlueAsDouble(final double blueAsDouble) {
        if (blueAsDouble < MIN_COMPONENT_AS_DOUBLE || MAX_COMPONENT_AS_DOUBLE < blueAsDouble) {
            throw new IllegalArgumentException("invalid blueAsDouble: " + blueAsDouble);
        }
        setBlue((int) (blueAsDouble * MAX_COMPONENT));
    }

    @SuppressWarnings({"unchecked"})
    public SELF blueAsDouble(final double blueAsDouble) {
        setBlueAsDouble(blueAsDouble);
        return (SELF) this;
    }

    // ----------------------------------------------------------------------------------------------------------- alpha
    @Max(MAX_COMPONENT)
    @Min(MIN_COMPONENT)
    @Transient
    public int getAlpha() {
        return Optional.ofNullable(getValue_())
                       .orElseGet(
                               () -> value_(new byte[COLUMN_LENGTH_VALUE_]).getValue_()
                       )[COMPONENT_INDEX_A] & 0xFF;
    }

    public void setAlpha(final int alpha) {
        Optional.ofNullable(getValue_())
                .orElseGet(
                        () -> value_(new byte[COLUMN_LENGTH_VALUE_]).getValue_()
                )[COMPONENT_INDEX_A] = (byte) (alpha & 0xFF);
    }

    public SELF alpha(final int alpha) {
        setAlpha(alpha);
        return (SELF) this;
    }

    @DecimalMax(DECIMAL_MAX_COMPONENT_AS_DOUBLE)
    @DecimalMin(DECIMAL_MIN_COMPONENT_AS_DOUBLE)
    public double getAlphaAsDoubleAsDouble() {
        return ((double) getAlpha()) / MAX_COMPONENT;
    }

    public void setAlphaAsDouble(final double alphaAsDouble) {
        if (alphaAsDouble < MIN_COMPONENT_AS_DOUBLE || MAX_COMPONENT_AS_DOUBLE < alphaAsDouble) {
            throw new IllegalArgumentException("invalid alphaAsDouble: " + alphaAsDouble);
        }
        setAlpha((int) (alphaAsDouble * MAX_COMPONENT));
    }

    @SuppressWarnings({"unchecked"})
    public SELF alphaAsDouble(final double alphaAsDouble) {
        setAlphaAsDouble(alphaAsDouble);
        return (SELF) this;
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
    @Column(name = "value_", nullable = true, insertable = true, updatable = true)
    private byte[] value_;
}