package com.github.jinahya.persistence.more.tests;

import com.github.jinahya.persistence.more.__AttributeEnum;

@SuppressWarnings({
        "java:S114", // Interface names should comply with a naming convention
        "java:S119" // Type parameter names should comply with a naming convention
})
interface __SampleStringAttributeEnum<ENUM extends Enum<ENUM> & __SampleStringAttributeEnum<ENUM>>
        extends __AttributeEnum.__OfString<ENUM> {

}
