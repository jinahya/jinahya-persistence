package com.github.jinahya.persistence.more.tests;

import com.github.jinahya.persistence.more.__AttributeEnum;

interface __SampleStringAttributeEnum<E extends Enum<E> & __SampleStringAttributeEnum<E>>
        extends __AttributeEnum.__OfString<E> {

}
