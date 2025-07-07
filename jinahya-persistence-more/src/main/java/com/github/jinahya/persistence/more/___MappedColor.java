package com.github.jinahya.persistence.more;

import jakarta.annotation.Nullable;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HexFormat;
import java.util.Optional;

@MappedSuperclass
@SuppressWarnings({
        "java:S100", // Method names should comply with a naming convention
        "java:S115", // Constant names should comply with a naming convention
        "java:S116"  // Field names should comply with a naming convention
})
public abstract class ___MappedColor<SELF extends ___MappedColor<SELF>> implements Serializable {

    @Serial
    private static final long serialVersionUID = 7382750834467647159L;

    // ---------------------------------------------------------------------------------------------------------- value_
    public static final String COLUMN_NAME_VALUE_ = "value_";

    protected static final int NUMBER_OF_COMPONENTS_ = 5;

    protected static final int COMPONENT_LENGTH_ = Float.BYTES;

    public static final int COLUMN_LENGTH_VALUE_ = NUMBER_OF_COMPONENTS_ * COMPONENT_LENGTH_;

    // -----------------------------------------------------------------------------------------------------------------
    public static final String ATTRIBUTE_NAME_VALUE_ = COLUMN_NAME_VALUE_;

    public static final int SIZE_MIN_VALUE_ = COLUMN_LENGTH_VALUE_;

    public static final int SIZE_MAX_VALUE_ = SIZE_MIN_VALUE_;

    // -----------------------------------------------------------------------------------------------------------------
    protected static final int MIN_COMPONENT_INDEX_ = 0;

    protected static final int MAX_COMPONENT_INDEX_ = 5;

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
               Optional.ofNullable(value_)
                       .map(v -> HexFormat.of().formatHex(v))
                       .orElse(null) +
               '}';
    }

    // -------------------------------------------------------------------------------------------------------- value___
    @SuppressWarnings({"unchecked"})
    public SELF resetValue_() {
        value_ = null;
        buffer_ = null;
        return (SELF) this;
    }

    // -----------------------------------------------------------------------------------------------------------------
    protected float getComponent(final int index) {
        if (index < MIN_COMPONENT_INDEX_ || index > MAX_COMPONENT_INDEX_) {
            throw new IllegalArgumentException("invalid index: " + index);
        }
        return Optional.ofNullable(buffer_)
                .map(b -> b.get(index))
                .map(Float::intBitsToFloat)
                .orElse(.0f);
    }

    protected void setComponent(final int index, final float value) {
        if (index < MIN_COMPONENT_INDEX_ || index > MAX_COMPONENT_INDEX_) {
            throw new IllegalArgumentException("invalid index: " + index);
        }
        Optional.ofNullable(buffer_)
                .orElseGet(() -> ByteBuffer
                        .wrap(Optional.ofNullable(value_).orElseGet(() -> new byte[COLUMN_LENGTH_VALUE_]))
                        .asIntBuffer()
                )
                .put(index, Float.floatToRawIntBits(value));
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nullable
    @Size(min = SIZE_MIN_VALUE_, max = SIZE_MAX_VALUE_)
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_VALUE_, nullable = true, insertable = true, updatable = true)
    private byte[] value_;

    @Transient
    private transient IntBuffer buffer_;
}
