package com.github.jinahya.persistence.more.converter;

/**
 * Sizes a downstream adds to {@link _Size}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
enum _ExtendedSize implements _ISize<_ExtendedSize> {

    EXTRA_LARGE("Extra-Large");

    _ExtendedSize(final String attributeValue) {
        this.attributeValue = attributeValue;
    }

    @Override
    public String attributeValue() {
        return attributeValue;
    }

    private final String attributeValue;
}
