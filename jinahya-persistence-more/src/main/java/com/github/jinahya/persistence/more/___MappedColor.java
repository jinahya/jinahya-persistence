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
@SuppressWarnings({
        "java:S100", // Method names should comply with a naming convention
        "java:S101", // Class names should comply with a naming convention
        "java:S115", // Constant names should comply with a naming convention
        "java:S116", // Field names should comply with a naming convention
        "java:S117", // Local variable and method parameter names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public abstract class ___MappedColor<SELF extends ___MappedColor<SELF>> implements Serializable {

    @Serial
    private static final long serialVersionUID = 7382750834467647159L;

    // -------------------------------------------------------------------------------------------------------- value___
    protected static final String COLUMN_NAME_VALUE___ = "value___";

    protected static final int NUMBER_OF_COMPONENTS___ = 5;

    protected static final int COMPONENT_LENGTH___ = Double.BYTES;

    protected static final int COLUMN_LENGTH_VALUE___ = NUMBER_OF_COMPONENTS___ * COMPONENT_LENGTH___;

    protected static final String ATTRIBUTE_NAME_VALUE___ = COLUMN_NAME_VALUE___;

    protected static final int SIZE_MIN_VALUE___ = COLUMN_LENGTH_VALUE___;

    protected static final int SIZE_MAX_VALUE___ = SIZE_MIN_VALUE___;

    protected static final int MIN_NORMALIZED_INDEX___ = 0;

    protected static final int MAX_NORMALIZED_INDEX___ = 5;

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected ___MappedColor() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object
    @Override
    public String toString() {
        return super.toString() + '{' +
               "value_=" +
               Optional.ofNullable(value___)
                       .map(v -> HexFormat.of().formatHex(v))
                       .orElse(null) +
               '}';
    }

    // --------------------------------------------------------------------------------------------------------- value__

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_VALUE___} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_VALUE___} attribute.
     */
    @Nullable
    protected byte[] getValue___() {
        return value___;
    }

    protected void setValue___(@Nullable final byte[] value__) {
        this.value___ = Optional.ofNullable(value__)
                .map(v -> v.length == COLUMN_LENGTH_VALUE___ ? v : Arrays.copyOf(v, COLUMN_LENGTH_VALUE___))
                .orElse(null);
        buffer___ = null;
    }

    @SuppressWarnings({"unchecked"})
    protected SELF value__(@Nullable final byte[] value__) {
        setValue___(value__);
        return (SELF) this;
    }

    /**
     * Sets {@value #ATTRIBUTE_NAME_VALUE___} attribute to {@code null}.
     *
     * @return this object.
     */
    @Nonnull
    public SELF resetValue__() {
        return value__(null);
    }

    // --------------------------------------------------------------------------------------------------------- buffer_
    @Nonnull
    ByteBuffer buffer_() {
        return Optional.ofNullable(buffer___)
                .orElseGet(() -> ByteBuffer.wrap(
                        Optional.ofNullable(value___)
                                .orElseGet(() -> value__(new byte[COLUMN_LENGTH_VALUE___]).getValue___())
                ));
    }

    // -----------------------------------------------------------------------------------------------------------------
    protected int offset___(final int index___) {
        if (index___ < MIN_NORMALIZED_INDEX___ || index___ > MAX_NORMALIZED_INDEX___) {
            throw new IllegalArgumentException("invalid index___: " + index___);
        }
        return index___ * COMPONENT_LENGTH___;
    }

    protected long getComponent___(final int index___) {
        return buffer_().getLong(offset___(index___));
    }

    protected void setComponent___(final int index___, final long component___) {
        if (index___ < MIN_NORMALIZED_INDEX___ || index___ > MAX_NORMALIZED_INDEX___) {
            throw new IllegalArgumentException("invalid index___: " + index___);
        }
        if (component___ < 0L) {
            throw new IllegalArgumentException("negative component: " + component___);
        }
        buffer_().putLong(offset___(index___), component___);
    }

    protected SELF component___(final int index___, final long component___) {
        setNormalizedComponent___(index___, component___);
        return (SELF) this;
    }

    // -----------------------------------------------------------------------------------------------------------------
    protected double getNormalizedComponent___(final int index___) {
        return buffer_().getDouble(offset___(index___));
    }

    protected void setNormalizedComponent___(final int index_, final double normalizedComponent___) {
        if (normalizedComponent___ < ___MappedColorConstants.MIN_COMPONENT_NORMALIZED ||
            normalizedComponent___ > ___MappedColorConstants.MAX_COMPONENT_NORMALIZED) {
            throw new IllegalArgumentException("invalid normalized: " + normalizedComponent___);
        }
        buffer_().putDouble(offset___(index_), normalizedComponent___);
    }

    protected SELF normalizedComponents___(final int index_, final double normalizedComponents___) {
        setNormalizedComponent___(index_, normalizedComponents___);
        return (SELF) this;
    }

//    // -----------------------------------------------------------------------------------------------------------------
//    @Nonnull
//    @Transient
//    public double[] getNormalizedComponents___() {
//        final var components = new double[NUMBER_OF_COMPONENTS___];
//        for (int i = 0; i < components.length; i++) {
//            components[i] = (float) getNormalizedComponent___(i);
//        }
//        return components;
//    }
//
//    protected void setNormalizedComponents___(@Nonnull final double[] normalizedComponents___) {
//        Objects.requireNonNull(normalizedComponents___, "normalizedComponents___ is null");
//        final var l = Math.min(NUMBER_OF_COMPONENTS___, normalizedComponents___.length);
//        for (int i = 0; i < l; i++) {
//            setNormalizedComponent___(i, normalizedComponents___[i]);
//        }
////        for (int i = l; i < NUMBER_OF_COMPONENTS_; i++) {
////            setComponent_(i, 0);
////        }
//    }
//
//    @Nonnull
//    public double[] getNormalizedComponents___(@Nonnull final double[] normalizedComponents_) {
//        Objects.requireNonNull(normalizedComponents_, "normalizedComponents_ is null");
//        System.arraycopy(
//                getNormalizedComponents___(),
//                0,
//                normalizedComponents_,
//                0,
//                normalizedComponents_.length
//        );
//        return normalizedComponents_;
//    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nullable
    @Size(min = SIZE_MIN_VALUE___, max = SIZE_MAX_VALUE___)
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_VALUE___, nullable = true, insertable = true, updatable = true)
    private byte[] value___;

    @Transient
    private transient ByteBuffer buffer___;
}
