package com.github.jinahya.persistence.mapped.test;

import com.github.jinahya.persistence.mapped.__MappedEntity;
import com.github.jinahya.persistence.mapped.__MappedEntityBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;

import java.beans.Introspector;
import java.lang.invoke.MethodHandles;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings({
        "java:S100", // Method names should comply with a naming convention
        "java:S101", // Class names should comply with a naming convention
//        "java:S117", // Local variable and method parameter names should comply with a naming convention
        "java:S119", // Type parameter names should comply with a naming convention
        "java:S3011" // Reflection should not be used to increase accessibility of classes, methods, or fields

})
public abstract class __MappedEntityBuilderTest<
        BUILDER extends __MappedEntityBuilder<BUILDER, ENTITY>,
        ENTITY extends __MappedEntity<ID>,
        ID
        > {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    protected __MappedEntityBuilderTest(final Class<BUILDER> builderClass, final Class<ENTITY> entityClass,
                                        final Class<ID> idClass) {
        super();
        this.builderClass = Objects.requireNonNull(builderClass, "builderClass is null");
        this.entityClass = Objects.requireNonNull(entityClass, "entityClass is null");
        this.idClass = Objects.requireNonNull(idClass, "idClass is null");
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("build() should return non-null ENTITY")
    @Test
    protected void build_NotNull_() {
        newRandomizedBuilderInstance().ifPresent(b -> {
            final var built = b.build();
            logger.log(System.Logger.Level.DEBUG, "built: {0}", built);
            assertThat(built).isNotNull();
        });
    }

    // ---------------------------------------------------------------------------------------------------- builderClass
    protected BUILDER newBuilderInstance() {
//        return ___JavaLangReflectTestUtils.newInstanceOf(builderClass);
        return ReflectionUtils.newInstance(builderClass);
    }

    @SuppressWarnings({
    })
    protected Optional<BUILDER> newRandomizedBuilderInstance() {
        final var builderInstance = newBuilderInstance();
        return newRandomizedEntityInstance()
                .map(entityInstance -> {
                    try {
                        final var beanInfo = Introspector.getBeanInfo(entityClass);
                        final var propertyDescriptors = beanInfo.getPropertyDescriptors();
                        for (var propertyDescriptor : propertyDescriptors) {
                            final var reader = propertyDescriptor.getReadMethod();
                            if (reader == null || !__MappedEntity.class.isAssignableFrom(reader.getDeclaringClass())) {
                                continue;
                            }
                            if (!reader.canAccess(entityInstance)) {
                                reader.setAccessible(true);
                            }
                            final var value = propertyDescriptor.getReadMethod().invoke(entityInstance);
                            final var name = propertyDescriptor.getName();
                            final var type = propertyDescriptor.getPropertyType();
                            final var writer = ReflectionUtils.findMethod(builderClass, name, type).orElse(null);
                            if (writer == null) {
                                logger.log(System.Logger.Level.DEBUG, "no writer method: {0}({1})", name, type);
                                continue;
                            }
                            if (!__MappedEntityBuilder.class.isAssignableFrom(writer.getDeclaringClass())) {
                                continue;
                            }
                            if (!writer.canAccess(builderInstance)) {
                                writer.setAccessible(true);
                            }
                            writer.invoke(builderInstance, value);
                        }
                        return builderInstance;
                    } catch (final Exception e) {
                        throw new RuntimeException(
                                "failed to randomize " + builderInstance + " from " + entityInstance,
                                e
                        );
                    }
                });
    }

    // ----------------------------------------------------------------------------------------------------- entityClass
    protected ENTITY newEntityInstance() {
//        return ___JavaLangReflectTestUtils.newInstanceOf(entityClass);
        return ReflectionUtils.newInstance(entityClass);
    }

    protected Optional<ENTITY> newRandomizedEntityInstance() {
        return __MappedEntityRandomizerUtils.newRandomizedInstanceOf(entityClass);
    }

    // -----------------------------------------------------------------------------------------------------------------
    protected final Class<BUILDER> builderClass;

    protected final Class<ENTITY> entityClass;

    protected final Class<ID> idClass;
}
