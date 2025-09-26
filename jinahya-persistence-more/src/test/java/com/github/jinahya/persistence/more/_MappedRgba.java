package com.github.jinahya.persistence.more;

import com.github.jinahya.persistence.more.color.__MappedRgba;

import java.util.Objects;

class _MappedRgba extends __MappedRgba {

    protected _MappedRgba() {
        super();
    }

    _MappedRgba(final _MappedRgbaBuilder builder) {
        super(builder);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public final boolean equals(final Object obj) {
        if (true) {
            return super.equals(obj);
        }
        if (!(obj instanceof _MappedRgba that)) {
            return false;
        }
        return Objects.equals(getRed(), that.getRed())
               && Objects.equals(getGreen(), that.getGreen())
               && Objects.equals(getBlue(), that.getBlue())
               && Objects.equals(getAlpha(), that.getAlpha());
//        return super.equals(obj)
//               && Objects.equals(getAlpha(), that.getAlpha());
    }

    @Override
    public final int hashCode() {
        if (true) {
            return super.hashCode();
        }
//        return Objects.hash(getRed(), getGreen(), getBlue(), getAlpha());
        return Objects.hash(super.hashCode(), getAlpha());
    }
}
