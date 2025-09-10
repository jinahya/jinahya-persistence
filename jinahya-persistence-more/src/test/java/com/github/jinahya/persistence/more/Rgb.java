package com.github.jinahya.persistence.more;

import java.util.Objects;

class Rgb extends __MappedRgb {

    protected Rgb() {
        super();
    }

    protected Rgb(final RgbBuilder builder) {
        super(builder);
    }

    @Override
    public final boolean equals(final Object obj) {
        if (true) {
            return super.equals(obj);
        }
        if (!(obj instanceof Rgb that)) {
            return false;
        }
        return Objects.equals(getRed(), that.getRed())
               && Objects.equals(getGreen(), that.getGreen())
               && Objects.equals(getBlue(), that.getBlue());
    }

    @Override
    public final int hashCode() {
        if (true) {
            return super.hashCode();
        }
        return Objects.hash(getRed(), getGreen(), getBlue());
    }
}
