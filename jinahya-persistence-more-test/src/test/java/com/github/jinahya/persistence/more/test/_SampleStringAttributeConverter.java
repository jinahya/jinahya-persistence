package com.github.jinahya.persistence.more.test;

import com.github.jinahya.persistence.more.converter.__StringAttributeConverter;

class _SampleStringAttributeConverter
        implements __StringAttributeConverter<String> {

    @Override
    public String convertToDatabaseColumn(final String attribute) {
        return attribute;
    }

    @Override
    public String convertToEntityAttribute(final String dbData) {
        return dbData;
    }
}
