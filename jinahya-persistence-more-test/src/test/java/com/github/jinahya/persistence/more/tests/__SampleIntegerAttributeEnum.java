package com.github.jinahya.persistence.more.tests;

import com.github.jinahya.persistence.more.__AttributeEnum;

@SuppressWarnings({
        "java:S114" // Interface names should comply with a naming convention
})
interface __SampleIntegerAttributeEnum<ENUM extends Enum<ENUM> & __SampleIntegerAttributeEnum<ENUM>>
        extends __AttributeEnum<ENUM, Integer> {

}
