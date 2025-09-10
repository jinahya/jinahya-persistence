package com.github.jinahya.persistence.more;

@SuppressWarnings({
        "unchecked",
        "java:S119" // Type parameter names should comply with a naming convention
})
public abstract class __MappedRgbaBuilder<SELF extends __MappedRgbaBuilder<SELF, TARGET>, TARGET extends __MappedRgba>
        extends __MappedRgbBuilder<SELF, TARGET> {

    // -----------------------------------------------------------------------------------------------------------------
    protected __MappedRgbaBuilder(final Class<TARGET> targetClass) {
        super(targetClass);
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    // ----------------------------------------------------------------------------------------------------------- alpha
    public Double alpha() {
        return alpha;
    }

    public SELF alpha(final Double alpha) {
        this.alpha = alpha;
        return (SELF) this;
    }

    // -----------------------------------------------------------------------------------------------------------------
    private Double alpha;
}
