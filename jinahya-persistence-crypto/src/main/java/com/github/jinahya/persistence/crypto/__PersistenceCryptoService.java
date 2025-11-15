package com.github.jinahya.persistence.crypto;

import com.github.jinahya.persistence.util.JinahyaMetamodelUtils;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.validation.constraints.NotNull;

import java.lang.invoke.MethodHandles;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __PersistenceCryptoService {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // -----------------------------------------------------------------------------------------------------------------
    private static final Map<EntityType<?>, Map<String, Attribute<?, ?>>> ATTRIBUTE = new ConcurrentHashMap<>();

    protected static Map<String, Attribute<?, ?>> getAttributes(@Nonnull final EntityType<?> entityType) {
        Objects.requireNonNull(entityType, "entityType is null");
        return ATTRIBUTE.computeIfAbsent(
                entityType,
                k -> k.getAttributes().stream()
                        .collect(Collectors.toUnmodifiableMap(Attribute::getName, Function.identity()))
        );
    }

    // -------------------------------------------------------------------------------------------------------- BUILDERS

    // ------------------------------------------------------------------------------------------ STATIC_FACTORY_METHODS

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    // -----------------------------------------------------------------------------------------------------------------
    protected void encrypt(final @Nonnull Object entity, final @Nonnull SingularAttribute<?, ?> attribute) {
        Objects.requireNonNull(entity, "entity is null");
        Objects.requireNonNull(attribute, "attribute is null");
        final var value = JinahyaMetamodelUtils.<Object, Object>getAttributeValue(entity, attribute);
    }

    protected void encrypt(@Nonnull @NotNull Object entity) {
        Objects.requireNonNull(entity, "entity is null");
        final var cryptoIdentifier = manager.getCryptoIdentifier(entity);
        final var entityClass = entity.getClass();
        final var entityType = getEntityType(entityClass);
        final var attributes = getAttributes(entityType);
        final var byteBuffer = ByteBuffer.allocate(Long.BYTES);
        for (final var entry : attributes.entrySet()) {
            final var name = entry.getKey();
            final var attribute = entry.getValue();
            final var persistenceAttributeType = attribute.getPersistentAttributeType();
            if (persistenceAttributeType != Attribute.PersistentAttributeType.BASIC) {
                continue;
            }
            final var javaType = attribute.getJavaType();
            final var value = JinahyaMetamodelUtils.getAttributeValue(entity, attribute);
            final var singularAttribute = (SingularAttribute<?, ?>) attribute;
            {
                final var annotation =
                        JinahyaMetamodelUtils.getJavaMemberAnnotation(attribute, __EncryptedAttributeOfSelf.class);
            }
        }
    }

    protected abstract void decrypt(@Nonnull @NotNull Object entityInstance);

    // ----------------------------------------------------------------------------------------------------- entityTypes
    protected final @Nonnull Map<Class<?>, EntityType<?>> getEntityTypes() {
        var result = entityTypes;
        if (result == null) {
            result = entityTypes = new ConcurrentHashMap<>();
        }
        return result;
    }

    // ------------------------------------------------------------------------------------ entityManagerFactoryInstance
    protected final EntityType<?> getEntityType(@Nonnull final Class<?> entityClass) {
        Objects.requireNonNull(entityClass, "entityClass is null");
        return getEntityTypes().computeIfAbsent(
                entityClass,
                k -> entityManagerFactoryInstance.stream()
                        .map(emf -> {
                            try {
                                return emf.getMetamodel().entity(k);
                            } catch (final IllegalArgumentException iae) {
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("no entity type for " + k))
        );
    }

    // -----------------------------------------------------------------------------------------------------------------
//    private volatile Map<Class<?>, Metamodel> metamodels;

    private volatile Map<Class<?>, EntityType<?>> entityTypes;

    @Inject
    private Instance<EntityManagerFactory> entityManagerFactoryInstance;

    // -----------------------------------------------------------------------------------------------------------------
    @Inject
    private __PersistenceCryptoManager manager;
}
