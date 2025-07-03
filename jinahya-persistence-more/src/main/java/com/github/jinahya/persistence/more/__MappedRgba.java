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
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.Objects;
import java.util.Optional;
import java.util.function.DoubleFunction;
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

    public static final String DECIMAL_MIN_COMPONENT_AS_DOUBLE = "0.0";

    public static final String DECIMAL_MAX_COMPONENT_AS_DOUBLE = "1.0";

    public static final double MIN_COMPONENT_AS_DOUBLE = new BigDecimal(DECIMAL_MIN_COMPONENT_AS_DOUBLE).doubleValue();

    public static final double MAX_COMPONENT_AS_DOUBLE = new BigDecimal(DECIMAL_MAX_COMPONENT_AS_DOUBLE).doubleValue();

    // ---------------------------------------------------------------------------------------------------------- value_
    public static final String COLUMN_NAME_VALUE_ = "value_";

    public static final int COLUMN_LENGTH_VALUE_ = 32; // 8 + 8 + 8 + 8

    public static final String ATTRIBUTE_NAME_VALUE_ = COLUMN_NAME_VALUE_;

    public static final int SIZE_MIN_VALUE_ = COLUMN_LENGTH_VALUE_;

    public static final int SIZE_MAX_VALUE_ = SIZE_MIN_VALUE_;

    protected static final int COMPONENT_LENGTH = Double.BYTES;

    protected static final int VALUE_OFFSET_R = 0;

    protected static final int VALUE_OFFSET_G = VALUE_OFFSET_R + COMPONENT_LENGTH;

    protected static final int VALUE_OFFSET_B = VALUE_OFFSET_G + COMPONENT_LENGTH;

    protected static final int VALUE_OFFSET_A = VALUE_OFFSET_B + COMPONENT_LENGTH;

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
            final IntFunction< // r
                    ? extends IntFunction< // g
                            ? extends IntFunction< // b
                                    ? extends IntFunction< // a
                                            ? extends R>>>> function) {
        Objects.requireNonNull(function, "function is null");
        return function
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
        Objects.requireNonNull(function, "function is null");
        return function
                .apply(getNormalizedRed())
                .apply(getNormalizedGreen())
                .apply(getNormalizedBlue())
                .apply(getNormalizedAlpha());
    }

    // ------------------------------------------------------------------------------------------------------------- red

    // -----------------------------------------------------------------------------------------------------------------
    private final class Component {

        private Component(final int offset) {
            super();
            this.offset = offset;
        }

        // -------------------------------------------------------------------------------------------------------------
        private int getValue() {
            return (int) (getNormalizedValue() * ___MappedColorConstants.RGB_MAX_COMPONENT);
        }

        private void setValue(final int value) {
            if (value < ___MappedColorConstants.RGB_MIN_COMPONENT ||
                value > ___MappedColorConstants.RGB_MAX_COMPONENT) {
                throw new IllegalArgumentException("invalid value: " + value);
            }
            setNormalizedValue((double) value / ___MappedColorConstants.RGB_MAX_COMPONENT);
        }

        private double getNormalizedValue() {
            return ByteBuffer.wrap(
                    Optional.ofNullable(getValue_()).orElseGet(
                            () -> value_(new byte[COLUMN_LENGTH_VALUE_]).getValue_()),
                    offset,
                    COMPONENT_LENGTH
            ).getDouble();
        }

        private void setNormalizedValue(final double normalizedValue) {
            if (normalizedValue < ___MappedColorConstants.RGB_MIN_COMPONENT_NORMALIZED ||
                normalizedValue > ___MappedColorConstants.RGB_MAX_COMPONENT_NORMALIZED) {
                throw new IllegalArgumentException("invalid normalized value: " + normalizedValue);
            }
            ByteBuffer.wrap(
                    Optional.ofNullable(getValue_()).orElseGet(
                            () -> value_(new byte[COLUMN_LENGTH_VALUE_]).getValue_()),
                    offset,
                    COMPONENT_LENGTH
            ).putDouble(normalizedValue);
        }

        // -------------------------------------------------------------------------------------------------------------
        private final int offset;
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns the current value of this color's <span style="color:red; -webkit-text-stroke: .5px black;">red</span>
     * component.
     *
     * @return the current value of the <span style="color:red; -webkit-text-stroke: .5px black;">red</span> component
     */
    @Max(___MappedColorConstants.RGB_MAX_COMPONENT)
    @Min(___MappedColorConstants.RGB_MIN_COMPONENT)
    @Transient
    public int getRed() {
        if (true) {
            return r.getValue();
        }
        return (int) (getNormalizedRed() * ___MappedColorConstants.RGB_MAX_COMPONENT);
    }

    /**
     * Replaces the current value of this color's <span style="color:red; -webkit-text-stroke: .5px black;">red</span>
     * component with the specified value.
     *
     * @param red new value for the <span style="color:red; -webkit-text-stroke: .5px black;">red</span> component.
     */
    public void setRed(final int red) {
        if (true) {
            r.setValue(red);
            return;
        }
        if (red < ___MappedColorConstants.RGB_MIN_COMPONENT || red > ___MappedColorConstants.RGB_MAX_COMPONENT) {
            throw new IllegalArgumentException("invalid red: " + red);
        }
        setNormalizedRed((double) red / ___MappedColorConstants.RGB_MAX_COMPONENT);
    }

    @SuppressWarnings({"unchecked"})
    public SELF red(final int red) {
        setRed(red);
        return (SELF) this;
    }

    @DecimalMax(DECIMAL_MAX_COMPONENT_AS_DOUBLE)
    @DecimalMin(DECIMAL_MIN_COMPONENT_AS_DOUBLE)
    public double getNormalizedRed() {
        if (true) {
            return r.getNormalizedValue();
        }
        return ByteBuffer.wrap(
                Optional.ofNullable(getValue_()).orElseGet(() -> value_(new byte[COLUMN_LENGTH_VALUE_]).getValue_()),
                VALUE_OFFSET_R,
                COMPONENT_LENGTH
        ).getDouble();
    }

    public void setNormalizedRed(final double normalizedRed) {
        if (true) {
            r.setNormalizedValue(normalizedRed);
            return;
        }
        if (normalizedRed < MIN_COMPONENT_AS_DOUBLE || MAX_COMPONENT_AS_DOUBLE < normalizedRed) {
            throw new IllegalArgumentException("invalid normalizedRed: " + normalizedRed);
        }
        ByteBuffer.wrap(
                Optional.ofNullable(getValue_()).orElseGet(() -> value_(new byte[COLUMN_LENGTH_VALUE_]).getValue_()),
                VALUE_OFFSET_R,
                COMPONENT_LENGTH
        ).putDouble(normalizedRed);
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
        if (true) {
            return g.getValue();
        }
        return (int) (getNormalizedGreen() * ___MappedColorConstants.RGB_MAX_COMPONENT);
    }

    public void setGreen(final int green) {
        if (true) {
            g.setValue(green);
            return;
        }
        if (green < ___MappedColorConstants.RGB_MIN_COMPONENT || green > ___MappedColorConstants.RGB_MAX_COMPONENT) {
            throw new IllegalArgumentException("invalid green: " + green);
        }
        setNormalizedGreen((double) green / ___MappedColorConstants.RGB_MAX_COMPONENT);
    }

    @SuppressWarnings({"unchecked"})
    public SELF green(final int green) {
        setGreen(green);
        return (SELF) this;
    }

    @DecimalMax(DECIMAL_MAX_COMPONENT_AS_DOUBLE)
    @DecimalMin(DECIMAL_MIN_COMPONENT_AS_DOUBLE)
    public double getNormalizedGreen() {
        if (true) {
            return g.getNormalizedValue();
        }
        return ByteBuffer.wrap(
                Optional.ofNullable(getValue_()).orElseGet(() -> value_(new byte[COLUMN_LENGTH_VALUE_]).getValue_()),
                VALUE_OFFSET_G,
                COMPONENT_LENGTH
        ).getDouble();
    }

    public void setNormalizedGreen(final double normalizedGreen) {
        if (true) {
            g.setNormalizedValue(normalizedGreen);
            return;
        }
        if (normalizedGreen < MIN_COMPONENT_AS_DOUBLE || MAX_COMPONENT_AS_DOUBLE < normalizedGreen) {
            throw new IllegalArgumentException("invalid normalizedGreen: " + normalizedGreen);
        }
        ByteBuffer.wrap(
                Optional.ofNullable(getValue_()).orElseGet(() -> value_(new byte[COLUMN_LENGTH_VALUE_]).getValue_()),
                VALUE_OFFSET_G,
                COMPONENT_LENGTH
        ).putDouble(normalizedGreen);
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
        if (true) {
            return b.getValue();
        }
        return (int) (getNormalizedBlue() * ___MappedColorConstants.RGB_MAX_COMPONENT);
    }

    public void setBlue(final int blue) {
        if (true) {
            b.setValue(blue);
            return;
        }
        if (blue < ___MappedColorConstants.RGB_MIN_COMPONENT || blue > ___MappedColorConstants.RGB_MAX_COMPONENT) {
            throw new IllegalArgumentException("invalid blue: " + blue);
        }
        setNormalizedBlue((double) blue / ___MappedColorConstants.RGB_MAX_COMPONENT);
    }

    @SuppressWarnings({"unchecked"})
    public SELF blue(final int blue) {
        setBlue(blue);
        return (SELF) this;
    }

    @DecimalMax(DECIMAL_MAX_COMPONENT_AS_DOUBLE)
    @DecimalMin(DECIMAL_MIN_COMPONENT_AS_DOUBLE)
    public double getNormalizedBlue() {
        if (true) {
            return b.getNormalizedValue();
        }
        return ByteBuffer.wrap(
                Optional.ofNullable(getValue_()).orElseGet(() -> value_(new byte[COLUMN_LENGTH_VALUE_]).getValue_()),
                VALUE_OFFSET_B,
                COMPONENT_LENGTH
        ).getDouble();
    }

    public void setNormalizedBlue(final double normalizedBlue) {
        if (true) {
            b.setNormalizedValue(normalizedBlue);
            return;
        }
        if (normalizedBlue < MIN_COMPONENT_AS_DOUBLE || MAX_COMPONENT_AS_DOUBLE < normalizedBlue) {
            throw new IllegalArgumentException("invalid normalizedBlue: " + normalizedBlue);
        }
        ByteBuffer.wrap(
                Optional.ofNullable(getValue_()).orElseGet(() -> value_(new byte[COLUMN_LENGTH_VALUE_]).getValue_()),
                VALUE_OFFSET_B,
                COMPONENT_LENGTH
        ).putDouble(normalizedBlue);
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
        if (true) {
            return a.getValue();
        }
        return (int) (getNormalizedAlpha() * ___MappedColorConstants.RGB_MAX_COMPONENT);
    }

    public void setAlpha(final int alpha) {
        if (true) {
            a.setValue(alpha);
            return;
        }
        if (alpha < ___MappedColorConstants.RGB_MIN_COMPONENT || alpha > ___MappedColorConstants.RGB_MAX_COMPONENT) {
            throw new IllegalArgumentException("invalid alpha: " + alpha);
        }
        setNormalizedAlpha((double) alpha / ___MappedColorConstants.RGB_MAX_COMPONENT);
    }

    @SuppressWarnings({"unchecked"})
    public SELF alpha(final int alpha) {
        setAlpha(alpha);
        return (SELF) this;
    }

    @DecimalMax(DECIMAL_MAX_COMPONENT_AS_DOUBLE)
    @DecimalMin(DECIMAL_MIN_COMPONENT_AS_DOUBLE)
    public double getNormalizedAlpha() {
        if (true) {
            return a.getNormalizedValue();
        }
        return ByteBuffer.wrap(
                Optional.ofNullable(getValue_()).orElseGet(() -> value_(new byte[COLUMN_LENGTH_VALUE_]).getValue_()),
                VALUE_OFFSET_A,
                COMPONENT_LENGTH
        ).getDouble();
    }

    public void setNormalizedAlpha(final double normalizedAlpha) {
        if (true) {
            a.setNormalizedValue(normalizedAlpha);
            return;
        }
        if (normalizedAlpha < MIN_COMPONENT_AS_DOUBLE || MAX_COMPONENT_AS_DOUBLE < normalizedAlpha) {
            throw new IllegalArgumentException("invalid normalizedAlpha: " + normalizedAlpha);
        }
        ByteBuffer.wrap(
                Optional.ofNullable(getValue_()).orElseGet(() -> value_(new byte[COLUMN_LENGTH_VALUE_]).getValue_()),
                VALUE_OFFSET_A,
                COMPONENT_LENGTH
        ).putDouble(normalizedAlpha);
    }

    @SuppressWarnings({"unchecked"})
    public SELF normalizedAlpha(final double normalizedAlpha) {
        setNormalizedAlpha(normalizedAlpha);
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

    // -----------------------------------------------------------------------------------------------------------------
    @Transient
    private final Component r = new Component(VALUE_OFFSET_R);

    @Transient
    private final Component g = new Component(VALUE_OFFSET_G);

    @Transient
    private final Component b = new Component(VALUE_OFFSET_B);

    @Transient
    private final Component a = new Component(VALUE_OFFSET_A);
}