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
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.HexFormat;
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

    protected static final int COMPONENT_LENGTH___ = Integer.BYTES;

    protected static final int COLUMN_LENGTH_VALUE___ = NUMBER_OF_COMPONENTS___ * COMPONENT_LENGTH___;

    protected static final String ATTRIBUTE_NAME_VALUE___ = COLUMN_NAME_VALUE___;

    protected static final int SIZE_MIN_VALUE___ = COLUMN_LENGTH_VALUE___;

    protected static final int SIZE_MAX_VALUE___ = SIZE_MIN_VALUE___;

    // -----------------------------------------------------------------------------------------------------------------
    protected static final int MIN_COMPONENT_INDEX___ = 0;

    protected static final int MAX_COMPONENT_INDEX___ = 5;

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

    // -------------------------------------------------------------------------------------------------------- value___

    /**
     * Returns current value of {@value #ATTRIBUTE_NAME_VALUE___} attribute.
     *
     * @return current value of {@value #ATTRIBUTE_NAME_VALUE___} attribute.
     */
    @Nullable
    protected byte[] getValue___() {
        return value___;
    }

    protected void setValue___(@Nullable final byte[] value___) {
        this.value___ = Optional.ofNullable(value___)
                .map(v -> v.length == COLUMN_LENGTH_VALUE___ ? v : Arrays.copyOf(v, COLUMN_LENGTH_VALUE___))
                .orElse(null);
        buffer___ = null;
    }

    @SuppressWarnings({"unchecked"})
    protected SELF value__(@Nullable final byte[] value___) {
        setValue___(value___);
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

    // ------------------------------------------------------------------------------------------------------- buffer___
    @Nonnull
    private IntBuffer buffer_() {
        return Optional.ofNullable(buffer___).orElseGet(
                () -> ByteBuffer.wrap(
                        Optional.ofNullable(value___)
                                .orElseGet(() -> value__(new byte[COLUMN_LENGTH_VALUE___]).getValue___())
                ).asIntBuffer()
        );
    }

    // -----------------------------------------------------------------------------------------------------------------
    private int requireValidIndex___(final int index___) {
        if (index___ < MIN_COMPONENT_INDEX___ || index___ > MAX_COMPONENT_INDEX___) {
            throw new IllegalArgumentException("invalid index___: " + index___);
        }
        return index___;
    }

    protected int getComponent___(final int index___) {
        return buffer_().get(
                requireValidIndex___(index___)
        );
    }

    protected void setComponent___(final int index___, final int component___) {
        buffer_().put(
                requireValidIndex___(index___),
                component___
        );
    }

    protected SELF component___(final int index___, final int component___) {
        setComponent___(index___, component___);
        return (SELF) this;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nullable
    @Size(min = SIZE_MIN_VALUE___, max = SIZE_MAX_VALUE___)
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_VALUE___, nullable = true, insertable = true, updatable = true)
    private byte[] value___;

    @Transient
    private transient IntBuffer buffer___;
}
