package com.github.jinahya.persistence.more.converter;

import jakarta.persistence.Converter;

/**
 * Converters of {@link _Size} alone, and of {@link _ISize} with both {@link _Size} and {@link _ExtendedSize}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public final class _SizeConverters {

    /**
     * A converter of {@link _Size} alone.
     */
    @Converter(autoApply = false)
    public static class OfSize extends AttributeEnumConverter3.OfString<_Size> {

        public OfSize() {
            super(_Size.class);
        }
    }

    /**
     * A converter of {@link _ISize}, with both {@link _Size} and {@link _ExtendedSize}.
     */
    @Converter(autoApply = false)
    public static class OfISize extends AttributeEnumConverter3.OfString<_ISize<?>> {

        public OfISize() {
            super(_Size.class, _ExtendedSize.class);
        }
    }

    private _SizeConverters() {
        throw new AssertionError("instantiation is not allowed");
    }
}
