package com.github.jinahya.persistence.crypto;

import com.github.jinahya.persistence.metamodel.JinahyaAttributeUtils;
import com.github.jinahya.persistence.util.JinahyaMetamodelUtils;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.EntityType;
import jakarta.validation.constraints.NotNull;

import java.lang.invoke.MethodHandles;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * .
 *
 * @see <a href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#a486">2.6. Basic
 *         Types</a> (Jakarta Persistence 3.2 Specification Document)
 */
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
    protected void encrypt(final @Nonnull Object entity,
                           final @Nonnull Attribute<?, ?> attribute,
                           final @Nonnull String cryptoIdentifier) {
        Objects.requireNonNull(entity, "entity is null");
        Objects.requireNonNull(attribute, "attribute is null");
        final var value = JinahyaAttributeUtils.getAttributeValue(entity, attribute);
        final var type = attribute.getJavaType();
        {
            final var annotation =
                    JinahyaMetamodelUtils.getJavaMemberAnnotation(attribute, __EncryptedAttributeOfSelf.class);
            if (annotation != null) {
                if (type == String.class) {
                    if (value == null) {
                        return;
                    }
                    final var decrypted = ((String) value).getBytes(StandardCharsets.UTF_8);
                    final var encrypted = manager.encrypt(cryptoIdentifier, decrypted);
                    JinahyaAttributeUtils.setAttributeValue(
                            entity,
                            attribute,
                            Base64.getEncoder().encodeToString(encrypted)
                    );
                }
            }
        }
    }

    protected void encrypt(@Nonnull @NotNull Object entity) {
        Objects.requireNonNull(entity, "entity is null");
        final var cryptoIdentifier = manager.getCryptoIdentifier(entity);
        final var entityClass = entity.getClass();
        final var entityType = getEntityType(entityClass);
        final var attributes = new HashMap<>(getAttributes(entityType));
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
            {
                final var annotation =
                        JinahyaMetamodelUtils.getJavaMemberAnnotation(attribute, __EncryptedAttributeOfSelf.class);
                if (annotation != null) {
                    if (value == null) {
                        continue;
                    }
                    if (javaType == String.class) {
                        final var decrypted = ((String) value).getBytes(StandardCharsets.UTF_8);
                        final var encrypted = manager.encrypt(cryptoIdentifier, decrypted);
                        JinahyaAttributeUtils.setAttributeValue(
                                entity,
                                attribute,
                                Base64.getEncoder().encodeToString(encrypted)
                        );
                        continue;
                    }
                    if (javaType == byte[].class) {
                        final var encrypted = manager.encrypt(cryptoIdentifier, (byte[]) value);
                        JinahyaAttributeUtils.setAttributeValue(
                                entity,
                                attribute,
                                encrypted
                        );
                        continue;
                    }
                    throw new RuntimeException("unsupported type: " + javaType);
                }
            }
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

    // ----------------------------------------------------------------------------------------------------- entityTypes

    // ------------------------------------------------------------------------------------ entityManagerFactoryInstance

    // --------------------------------------------------------------------------------------------------------- manager

    // ------------------------------------------------------------------------------------------------------ encryptors
    private __PersistenceCryptoProcessor<?> encryptor(@Nonnull final EntityType<?> entityType) {
        Objects.requireNonNull(entityType, "entityType is null");
        return encryptionProcessors.computeIfAbsent(
                entityType,
                k -> new __PersistenceCryptoEncryptor<>(k, manager)
        );
    }

    // ------------------------------------------------------------------------------------------------------ decryptors
    private __PersistenceCryptoProcessor<?> decryptor(@Nonnull final EntityType<?> entityType) {
        Objects.requireNonNull(entityType, "entityType is null");
        return decryptionProcessors.computeIfAbsent(
                entityType,
                k -> new __PersistenceCryptoDecryptor<>(k, manager)
        );
    }

    // -----------------------------------------------------------------------------------------------------------------
    private volatile Map<Class<?>, EntityType<?>> entityTypes;

    @Inject
    private Instance<EntityManagerFactory> entityManagerFactoryInstance;

    // -----------------------------------------------------------------------------------------------------------------
    @Inject
    private __PersistenceCryptoManager manager;

    // -----------------------------------------------------------------------------------------------------------------
    private final Map<Class<?>, __PersistenceCryptoProcessor<?>> encryptionProcessors = new ConcurrentHashMap<>();

    private final Map<Class<?>, __PersistenceCryptoProcessor<?>> decryptionProcessors = new ConcurrentHashMap<>();
}
