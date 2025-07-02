package com.github.jinahya.persistence.more;

import jakarta.annotation.Nullable;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.Optional;

@Embeddable
@MappedSuperclass
public abstract class __MappedCmyk<SELF extends __MappedCmyk<SELF>> extends ___MappedColor<SELF> {

    @Serial
    private static final long serialVersionUID = -5905278228337798453L;

    // -----------------------------------------------------------------------------------------------------------------
    public static final String DECIMAL_MIN_COMPONENT = "0.0d";

    public static final String DECIMAL_MAX_COMPONENT = "1.0d";

    // ---------------------------------------------------------------------------------------------------------- value_
    public static final String COLUMN_NAME_VALUE_ = "value_";

    public static final int COLUMN_LENGTH_VALUE_ = 40;

    public static final String ATTRIBUTE_NAME_VALUE_ = COLUMN_NAME_VALUE_;

    public static final int SIZE_MIN_VALUE_ = COLUMN_LENGTH_VALUE_;

    public static final int SIZE_MAX_VALUE_ = SIZE_MIN_VALUE_;

    protected static final int COMPONENT_LENGTH = Double.BYTES;

//    protected static final int COMPONENT_INDEX_C = 0;
//
//    protected static final int COMPONENT_INDEX_M = 1;
//
//    protected static final int COMPONENT_INDEX_Y = 2;
//
//    protected static final int COMPONENT_INDEX_K = 3;
//
//    protected static final int COMPONENT_INDEX_A = 4;

    protected static final int VALUE_OFFSET_C = 0;

    protected static final int VALUE_OFFSET_M = VALUE_OFFSET_C + COMPONENT_LENGTH;

    protected static final int VALUE_OFFSET_Y = VALUE_OFFSET_M + COMPONENT_LENGTH;

    protected static final int VALUE_OFFSET_K = VALUE_OFFSET_Y + COMPONENT_LENGTH;

    protected static final int VALUE_OFFSET_A = VALUE_OFFSET_K + COMPONENT_LENGTH;

    // ------------------------------------------------------------------------------------------ STATIC_FACTORY_METHODS

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected __MappedCmyk() {
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

    // ------------------------------------------------------------------------------------------------------------- cyan

    /**
     * Returns the current value of this color's <span style="color:cyan; -webkit-text-stroke: .5px black;">cyan</span>
     * component.
     *
     * @return the current value of the <span style="color:cyan; -webkit-text-stroke: .5px black;">cyan</span> component
     */
    @DecimalMax(DECIMAL_MAX_COMPONENT)
    @DecimalMin(DECIMAL_MIN_COMPONENT)
    @Transient
    public double getCyan() {
        return ByteBuffer.wrap(
                Optional.ofNullable(getValue_()).orElseGet(() -> value_(new byte[COLUMN_LENGTH_VALUE_]).getValue_()),
                VALUE_OFFSET_C,
                COMPONENT_LENGTH
        ).getDouble();
    }

    /**
     * Replaces the current value of this color's <span style="color:cyan; -webkit-text-stroke: .5px black;">cyan</span>
     * component with the specified value.
     *
     * @param cyan new value for the <span style="color:cyan; -webkit-text-stroke: .5px black;">cyan</span> component.
     */
    public void setCyan(final double cyan) {
        ByteBuffer.wrap(
                Optional.ofNullable(getValue_()).orElseGet(() -> value_(new byte[COLUMN_LENGTH_VALUE_]).getValue_()),
                VALUE_OFFSET_C,
                COMPONENT_LENGTH
        ).putDouble(cyan);
    }

    // --------------------------------------------------------------------------------------------------------- magenta

    /**
     * Returns the current value of this color's <span style="color:magenta; -webkit-text-stroke: .5px
     * black;">magenta</span> component.
     *
     * @return the current value of the <span style="color:magenta; -webkit-text-stroke: .5px black;">magenta</span>
     * component
     */
    @DecimalMax(DECIMAL_MAX_COMPONENT)
    @DecimalMin(DECIMAL_MIN_COMPONENT)
    @Transient
    public double getMagenta() {
        return ByteBuffer.wrap(
                Optional.ofNullable(getValue_()).orElseGet(() -> value_(new byte[COLUMN_LENGTH_VALUE_]).getValue_()),
                VALUE_OFFSET_M,
                COMPONENT_LENGTH
        ).getDouble();
    }

    /**
     * Replaces the current value of this color's <span style="color:magenta; -webkit-text-stroke: .5px
     * black;">magenta</span> component with the specified value.
     *
     * @param magenta new value for the <span style="color:magenta; -webkit-text-stroke: .5px black;">magenta</span>
     *                component.
     */
    public void setMagenta(final double magenta) {
        ByteBuffer.wrap(
                Optional.ofNullable(getValue_()).orElseGet(() -> value_(new byte[COLUMN_LENGTH_VALUE_]).getValue_()),
                VALUE_OFFSET_M,
                COMPONENT_LENGTH
        ).putDouble(magenta);
    }

    // ---------------------------------------------------------------------------------------------------------- yellow

    /**
     * Returns the current value of this color's <span style="color:yellow; -webkit-text-stroke: .5px
     * black;">yellow</span> component.
     *
     * @return the current value of the <span style="color:yellow; -webkit-text-stroke: .5px black;">yellow</span>
     * component
     */
    @DecimalMax(DECIMAL_MAX_COMPONENT)
    @DecimalMin(DECIMAL_MIN_COMPONENT)
    @Transient
    public double getYellow() {
        return ByteBuffer.wrap(
                Optional.ofNullable(getValue_()).orElseGet(() -> value_(new byte[COLUMN_LENGTH_VALUE_]).getValue_()),
                VALUE_OFFSET_Y,
                COMPONENT_LENGTH
        ).getDouble();
    }

    /**
     * Replaces the current value of this color's <span style="color:yellow; -webkit-text-stroke: .5px
     * black;">yellow</span> component with the specified value.
     *
     * @param yellow new value for the <span style="color:yellow; -webkit-text-stroke: .5px black;">yellow</span>
     *               component.
     */
    public void setYellow(final double yellow) {
        ByteBuffer.wrap(
                Optional.ofNullable(getValue_()).orElseGet(() -> value_(new byte[COLUMN_LENGTH_VALUE_]).getValue_()),
                VALUE_OFFSET_Y,
                COMPONENT_LENGTH
        ).putDouble(yellow);
    }

    // ------------------------------------------------------------------------------------------------------------- key

    /**
     * Returns the current value of this color's <span style="color:black; -webkit-text-stroke: .5px grey;">key</span>
     * component.
     *
     * @return the current value of the <span style="color:black; -webkit-text-stroke: .5px grey;">key</span> component
     */
    @DecimalMax(DECIMAL_MAX_COMPONENT)
    @DecimalMin(DECIMAL_MIN_COMPONENT)
    @Transient
    public double getKey() {
        return ByteBuffer.wrap(
                Optional.ofNullable(getValue_()).orElseGet(() -> value_(new byte[COLUMN_LENGTH_VALUE_]).getValue_()),
                VALUE_OFFSET_K,
                COMPONENT_LENGTH
        ).getDouble();
    }

    /**
     * Replaces the current value of this color's <span style="color:black; -webkit-text-stroke: .5px grey;">key</span>
     * component with the specified value.
     *
     * @param key new value for the <span style="color:black; -webkit-text-stroke: .5px grey;">key</span> component.
     */
    public void setKey(final double key) {
        ByteBuffer.wrap(
                Optional.ofNullable(getValue_()).orElseGet(() -> value_(new byte[COLUMN_LENGTH_VALUE_]).getValue_()),
                VALUE_OFFSET_K,
                COMPONENT_LENGTH
        ).putDouble(key);
    }
    // ------------------------------------------------------------------------------------------------------------- alpha

    /**
     * Returns the current value of this color's <span style="color:grey; -webkit-text-stroke: .5px black;">alpha</span>
     * component.
     *
     * @return the current value of the <span style="color:grey; -webkit-text-stroke: .5px black;">alpha</span>
     * component
     */
    @DecimalMax(DECIMAL_MAX_COMPONENT)
    @DecimalMin(DECIMAL_MIN_COMPONENT)
    @Transient
    public double getAlpha() {
        return ByteBuffer.wrap(
                Optional.ofNullable(getValue_()).orElseGet(() -> value_(new byte[COLUMN_LENGTH_VALUE_]).getValue_()),
                VALUE_OFFSET_A,
                COMPONENT_LENGTH
        ).getDouble();
    }

    /**
     * Replaces the current value of this color's <span style="color:grey; -webkit-text-stroke: .5px
     * black;">alpha</span> component with the specified value.
     *
     * @param alpha new value for the <span style="color:grey; -webkit-text-stroke: .5px black;">alpha</span>
     *              component.
     */
    public void setAlpha(final double alpha) {
        ByteBuffer.wrap(
                Optional.ofNullable(getValue_()).orElseGet(() -> value_(new byte[COLUMN_LENGTH_VALUE_]).getValue_()),
                VALUE_OFFSET_A,
                COMPONENT_LENGTH
        ).putDouble(alpha);
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
    @Column(name = "value_", nullable = true, insertable = true, updatable = true, length = COLUMN_LENGTH_VALUE_)
    private byte[] value_;
}