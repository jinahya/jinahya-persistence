package com.github.jinahya.persistence.more.converter;

/**
 * Sizes a library defines.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
enum _Size implements _ISize<_Size> {

    SMALL("Small"),

    MEDIUM("Medium"),

    LARGE("Large");

    _Size(final String attributeValue) {
        this.attributeValue = attributeValue;
    }

    @Override
    public String attributeValue() {
        return attributeValue;
    }

    private final String attributeValue;
}
