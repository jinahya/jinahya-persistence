package com.example.downstream;

import com.github.jinahya.persistence.more.__AttributeEnum;

/** An enum a downstream persists by its attribute value. */
public enum DownstreamStatus implements __AttributeEnum.__OfString<DownstreamStatus> {

    OPEN("O"),

    CLOSED("C");

    DownstreamStatus(final String attributeValue) {
        this.attributeValue = attributeValue;
    }

    @Override
    public String attributeValue() {
        return attributeValue;
    }

    private final String attributeValue;
}
