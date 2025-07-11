package com.github.jinahya.persistence.mapped.tests;

import com.github.jinahya.persistence.mapped.__MappedEntity;
import com.github.jinahya.persistence.mapped.tests.util.__JavaLangReflectUtils;
import jakarta.annotation.Nonnull;
import jakarta.persistence.Table;

import java.util.Objects;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S125", // Sections of code should not be commented out
})
public class __MappedEntityTestUtils {

    public static <ENTITY extends __MappedEntity<ENTITY, ?>, R>
    Table getTableAnnotation(@Nonnull final Class<ENTITY> entityClass) {
        Objects.requireNonNull(entityClass, "entityClass is null");
        return __JavaLangReflectUtils.findAnnotation(entityClass, Table.class).orElseThrow(
                () -> new IllegalArgumentException("unable to get @Table from " + entityClass)
        );
    }

    // -----------------------------------------------------------------------------------------------------------------
    private __MappedEntityTestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
