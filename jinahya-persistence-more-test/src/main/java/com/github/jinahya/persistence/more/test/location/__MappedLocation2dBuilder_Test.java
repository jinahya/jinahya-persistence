package com.github.jinahya.persistence.more.test.location;

import com.github.jinahya.persistence.mapped.test.__MappedBuilder_Test;
import com.github.jinahya.persistence.more.location.__MappedLocation2d;
import com.github.jinahya.persistence.more.location.__MappedLocation2dBuilder;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public abstract class __MappedLocation2dBuilder_Test<
        BUILDER extends __MappedLocation2dBuilder<BUILDER, MAPPED>,
        MAPPED extends __MappedLocation2d
        >
        extends __MappedBuilder_Test<BUILDER, MAPPED> {

    protected __MappedLocation2dBuilder_Test(final Class<BUILDER> builderClass, final Class<MAPPED> mappedClass) {
        super(builderClass, mappedClass);
    }
}
