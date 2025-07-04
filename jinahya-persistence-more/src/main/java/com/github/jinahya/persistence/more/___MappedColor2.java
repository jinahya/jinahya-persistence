package com.github.jinahya.persistence.more;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.Objects;
import java.util.Optional;

@MappedSuperclass
public abstract class ___MappedColor2<SELF extends ___MappedColor2<SELF>> implements Serializable {

    @Serial
    private static final long serialVersionUID = 7382750834467647159L;

    // --------------------------------------------------------------------------------------------------------- value__
    protected static final String COLUMN_NAME_VALUE__ = "value__";

    protected static final int NUMBER_OF_COMPONENTS__ = 5;

    protected static final int COMPONENT_LENGTH__ = Double.BYTES;

    //    protected static final int COLUMN_LENGTH_VALUE_ = 40; // 8 * 5
    protected static final int COLUMN_LENGTH_VALUE__ = NUMBER_OF_COMPONENTS__ * COMPONENT_LENGTH__;

    protected static final String ATTRIBUTE_NAME_VALUE__ = COLUMN_NAME_VALUE__;

    protected static final int SIZE_MIN_VALUE__ = COLUMN_LENGTH_VALUE__;

    protected static final int SIZE_MAX_VALUE__ = SIZE_MIN_VALUE__;

    protected static final int MIN_NORMALIZED_INDEX__ = 0;

    protected static final int MAX_NORMALIZED_INDEX__ = 5;

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected ___MappedColor2() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object
    @Override
    public String toString() {
        return super.toString() + '{' +
               "value_=" +
               Optional.ofNullable(value__)
                       .map(v -> HexFormat.of().formatHex(v))
                       .orElse(null) +
               '}';
    }

    // --------------------------------------------------------------------------------------------------------- value__

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_VALUE__} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_VALUE__} attribute.
     */
    @Nullable
    protected byte[] getValue__() {
        return value__;
    }

    protected void setValue__(@Nullable final byte[] value__) {
        this.value__ = Optional.ofNullable(value__)
                .map(v -> v.length == COLUMN_LENGTH_VALUE__ ? v : Arrays.copyOf(v, COLUMN_LENGTH_VALUE__))
                .orElse(null);
        buffer_ = null;
    }

    @SuppressWarnings({"unchecked"})
    protected SELF value__(@Nullable final byte[] value__) {
        setValue__(value__);
        return (SELF) this;
    }

    /**
     * Sets {@value #ATTRIBUTE_NAME_VALUE__} attribute to {@code null}.
     *
     * @return this object.
     */
    @Nonnull
    public SELF resetValue__() {
        return value__(null);
    }

    // --------------------------------------------------------------------------------------------------------- buffer_
    @Nonnull
    private ByteBuffer buffer_() {
        return Optional.ofNullable(buffer_)
                .orElseGet(() -> ByteBuffer.wrap(
                        Optional.ofNullable(value__)
                                .orElseGet(() -> value__(new byte[COLUMN_LENGTH_VALUE__]).getValue__())
                ));
    }

    protected double getComponent_(final int index_) {
        if (index_ < MIN_NORMALIZED_INDEX__ || index_ > MAX_NORMALIZED_INDEX__) {
            throw new IllegalArgumentException("invalid index_: " + index_);
        }
        return buffer_().getDouble(index_ * COMPONENT_LENGTH__);
    }

    protected void setComponent_(final int index_, final double component_) {
        if (index_ < MIN_NORMALIZED_INDEX__ || index_ > MAX_NORMALIZED_INDEX__) {
            throw new IllegalArgumentException("invalid index_: " + index_);
        }
        if (component_ < ___MappedColorConstants.MIN_COMPONENT_NORMALIZED ||
            component_ > ___MappedColorConstants.MAX_COMPONENT_NORMALIZED) {
            throw new IllegalArgumentException("invalid normalized: " + component_);
        }
        buffer_().putDouble(index_ * COMPONENT_LENGTH__, component_);
    }

    protected SELF component_(final int index_, final double component_) {
        setComponent_(index_, component_);
        return (SELF) this;
    }

    @Nonnull
    @Transient
    public double[] getComponents_() {
        final var components = new double[NUMBER_OF_COMPONENTS__];
        for (int i = 0; i < components.length; i++) {
            components[i] = (float) getComponent_(i);
        }
        return components;
    }

    protected void setComponents_(@Nonnull final double[] components_) {
        Objects.requireNonNull(components_, "components_ is null");
        final var l = Math.min(NUMBER_OF_COMPONENTS__, components_.length);
        for (int i = 0; i < l; i++) {
            setComponent_(i, components_[i]);
        }
//        for (int i = l; i < NUMBER_OF_COMPONENTS_; i++) {
//            setComponent_(i, 0);
//        }
    }

    @Nonnull
    public double[] getComponents_(@Nonnull final double[] components_) {
        Objects.requireNonNull(components_, "components_ is null");
        System.arraycopy(
                getComponents_(),
                0,
                components_,
                0,
                components_.length
        );
        return components_;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nullable
    @Size(min = SIZE_MIN_VALUE__, max = SIZE_MAX_VALUE__)
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_VALUE__, nullable = true, insertable = true, updatable = true)
    private byte[] value__;

    @Transient
    private transient ByteBuffer buffer_;
}
