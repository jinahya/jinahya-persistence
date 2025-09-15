package com.github.jinahya.persistence.more;

import com.github.jinahya.persistence.mapped.__MappedBuilder;

@SuppressWarnings({
        "unchecked",
        "java:S119" // Type parameter names should comply with a naming convention
})
public abstract class __MappedRgbBuilder<SELF extends __MappedRgbBuilder<SELF, TARGET>, TARGET extends __MappedRgb>
        extends __MappedBuilder<SELF, TARGET> {

    // -----------------------------------------------------------------------------------------------------------------
    protected __MappedRgbBuilder(final Class<TARGET> targetClass) {
        super(targetClass);
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    // ------------------------------------------------------------------------------------------------------------- red
    public Double red() {
        return red;
    }

    public SELF red(final Double red) {
        this.red = red;
        return (SELF) this;
    }

    // ----------------------------------------------------------------------------------------------------------- green
    public Double green() {
        return green;
    }

    public SELF green(final Double green) {
        this.green = green;
        return (SELF) this;
    }

    // ------------------------------------------------------------------------------------------------------------ blue
    public Double blue() {
        return blue;
    }

    public SELF blue(final Double blue) {
        this.blue = blue;
        return (SELF) this;
    }

    // -----------------------------------------------------------------------------------------------------------------
    private Double red;

    private Double green;

    private Double blue;
}
