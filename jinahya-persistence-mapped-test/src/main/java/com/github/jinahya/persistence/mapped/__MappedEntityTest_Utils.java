package com.github.jinahya.persistence.mapped;

import com.github.jinahya.persistence.mapped.util.JavaLangReflectUtils;

import java.util.Objects;
import java.util.stream.Stream;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S125", // Sections of code should not be commented out
})
public abstract class __MappedEntityTest_Utils {

    @SuppressWarnings({
            "java:S119" // Type parameter names should comply with a naming convention
    })
    public static <ENTITY extends __MappedEntity<ENTITY, ?>>
    Stream<ENTITY> getEntityInstanceStream(final Class<ENTITY> entityClass) {
        Objects.requireNonNull(entityClass, "entityClass is null");
        return Stream.of(
                __MappedEntityInitializerUtils.newInitializedInstanceOf(entityClass)
                        .orElseGet(() -> JavaLangReflectUtils.newInstanceOf(entityClass)),
                __MappedEntityRandomizerUtils.newRandomizedInstanceOf(entityClass)
                        .orElse(null)
        ).filter(Objects::nonNull);
    }

    // -----------------------------------------------------------------------------------------------------------------
    protected __MappedEntityTest_Utils() {
        super();
    }
}