package com.github.jinahya.persistence.more.converter.test;

import com.github.jinahya.persistence.more.converter.__AttributeEnumConverter;
import com.github.jinahya.persistence.more.test._SampleLongAttributeEnum;

class _SampleLongAttributeEnumConverter
        extends __AttributeEnumConverter.__OfLong<_SampleLongAttributeEnum> {

    _SampleLongAttributeEnumConverter() {
        super(_SampleLongAttributeEnum.class);
    }
}
