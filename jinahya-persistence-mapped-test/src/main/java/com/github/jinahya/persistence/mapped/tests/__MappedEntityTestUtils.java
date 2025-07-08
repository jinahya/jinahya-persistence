package com.github.jinahya.persistence.mapped.tests;

import com.github.jinahya.persistence.mapped.__MappedEntity;
import com.github.jinahya.persistence.mapped.tests.util.__JavaLangReflectUtils;

import java.lang.reflect.ParameterizedType;
import java.util.Objects;
import java.util.stream.Stream;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S125", // Sections of code should not be commented out
})
public abstract class __MappedEntityTestUtils {

    @SuppressWarnings({
            "java:S119" // Type parameter names should comply with a naming convention
    })
    public static Class<? extends __MappedEntity<?, ?>> getEntityType(final Class<?> entityTestClass) {
        var genericSuperclass = entityTestClass.getGenericSuperclass();
        while (genericSuperclass instanceof ParameterizedType) {
            final var parameterizedType = (ParameterizedType) genericSuperclass;
            final var rawType = parameterizedType.getRawType();
            if (rawType.equals(__MappedEntityTest.class)) {
                final var actualTypeArguments = parameterizedType.getActualTypeArguments();
                return (Class<? extends __MappedEntity<?, ?>>) actualTypeArguments[0];
            }
            if (rawType instanceof Class) {
                genericSuperclass = ((Class<?>) rawType).getGenericSuperclass();
            } else {
                break;
            }
        }
        return null;
    }

    @SuppressWarnings({
            "java:S119" // Type parameter names should comply with a naming convention
    })
    public static <ENTITY extends __MappedEntity<ENTITY, ?>>
    Stream<ENTITY> getEntityInstanceStream(final Class<ENTITY> entityClass) {
        Objects.requireNonNull(entityClass, "entityClass is null");
        return Stream.of(
                __MappedEntityInstantiatorUtils.newInstanceOf(entityClass)
                        .orElseGet(() -> __JavaLangReflectUtils.newInstanceOf(entityClass)),
                __MappedEntityRandomizerUtils.newRandomizedInstanceOf(entityClass)
                        .orElse(null)
        ).filter(Objects::nonNull);
    }

    // -----------------------------------------------------------------------------------------------------------------
    protected __MappedEntityTestUtils() {
        super();
    }
}
