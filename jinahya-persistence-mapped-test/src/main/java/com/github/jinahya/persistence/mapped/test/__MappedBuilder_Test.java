package com.github.jinahya.persistence.mapped.test;

import com.github.jinahya.persistence.mapped.__Mapped;
import com.github.jinahya.persistence.mapped.__MappedBuilder;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119", // Type parameter names should comply with a naming convention
})
public abstract class __MappedBuilder_Test<
        BUILDER extends __MappedBuilder<BUILDER, MAPPED>,
        MAPPED extends __Mapped
        >
        extends __BuilderTest<BUILDER, MAPPED> {

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    protected __MappedBuilder_Test(final Class<BUILDER> builderClass, final Class<MAPPED> mappedClass) {
        super(builderClass, mappedClass);
    }
}
