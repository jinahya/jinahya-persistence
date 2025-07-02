package com.github.jinahya.persistence.more;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.Optional;

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

    // ------------------------------------------------------------------------------------------------------------- red

    /**
     * Returns the current value of this color's <span style="color:red">red</span> component.
     *
     * @return the current value of the <span style="color:red">red</span> component
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
     * Replaces the current value of this color's <span style="color:red">red</span> component with the specified
     * value.
     *
     * @param red new value for the <span style="color:red">red</span> component.
     */
    public void setRed(final int red) {
        Optional.ofNullable(getValue_())
                .orElseGet(
                        () -> value_(new byte[COLUMN_LENGTH_VALUE_]).getValue_()
                )[COMPONENT_INDEX_R] = (byte) (red & 0xFF);
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

    // ---------------------------------------------------------------------------------------------------------- value_
    public byte[] getValue_() {
        return value_;
    }

    public void setValue_(final byte[] value_) {
        this.value_ = Optional.ofNullable(value_)
                .map(v -> v.length == COLUMN_LENGTH_VALUE_ ? v : Arrays.copyOf(v, COLUMN_LENGTH_VALUE_))
                .orElse(null);
    }

    public SELF value_(final byte[] value_) {
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