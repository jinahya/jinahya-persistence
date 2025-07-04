package com.github.jinahya.persistence.mapped.tests;

import com.github.jinahya.persistence.mapped.__MappedEntity;
import com.github.jinahya.persistence.mapped.___Mapped;
import com.github.jinahya.persistence.mapped.tests.util.__JavaLangReflectUtils;
import jakarta.annotation.Nonnull;

import java.lang.reflect.ParameterizedType;
import java.util.Objects;
import java.util.stream.Stream;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S125", // Sections of code should not be commented out
})
public abstract class ___MappedTestUtils {

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
    public static <T extends ___Mapped<T>> Stream<T> getTypeInstanceStream(@Nonnull final Class<T> typeClass) {
        Objects.requireNonNull(typeClass, "typeClass is null");
        return Stream.of(
                ___MappedInstantiatorUtils.newInitializedInstanceOf(typeClass)
                        .orElseGet(() -> __JavaLangReflectUtils.newInstanceOf(typeClass)),
                ___MappedRandomizerUtils.newRandomizedInstanceOf(typeClass)
                        .orElse(null)
        ).filter(Objects::nonNull);
    }

    // -----------------------------------------------------------------------------------------------------------------
    protected ___MappedTestUtils() {
        super();
    }
}
