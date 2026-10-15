package com.github.jinahya.persistence.encryption;

import com.github.jinahya.persistence.encryption.EncryptionMapping.ColumnRules;
import com.github.jinahya.persistence.encryption.EncryptionMapping.Embedded;
import com.github.jinahya.persistence.encryption.EncryptionMapping.Pair;
import com.github.jinahya.persistence.metamodel.util.JinahyaAttributeUtils;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.ManagedType;
import jakarta.persistence.metamodel.MapAttribute;
import jakarta.persistence.metamodel.PluralAttribute;
import jakarta.persistence.metamodel.SingularAttribute;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.github.jinahya.persistence.encryption.EntityEncryptorUtils.decode;
import static com.github.jinahya.persistence.encryption.EntityEncryptorUtils.encode;

/**
 * Encrypts and decrypts the {@link EncryptedAttribute encrypted attributes} of entity instances, in place, through an
 * {@link EntityEncryptionService}: the mechanism behind {@link EntityEncryptionListener}, and behind the persistence
 * provider integrations which replace it.
 * <p>
 * It decides nothing about keys, cryptography or the container: the persistence unit, the identifier and the bytes all
 * come from the {@link EntityEncryptionService}. It reads the entity's {@link ManagedType managedType} from the
 * metamodel, and for each {@code byte[]} attribute annotated with {@link EncryptedAttribute @EncryptedAttribute}, and
 * the {@code BASIC} attribute holding the plaintext it names:
 * <ol>
 *   <li>converts the plaintext value to bytes, by its declared java type; no type information is stored with
 *       them;</li>
 *   <li>hands those bytes to the {@link EntityEncryptionService encryption service}, along with the
 *       {@link EntityEncryptionContext context} of the instance, which the service is
 *       {@link EntityEncryptionService#init(EntityEncryptionContext) initialized} with first;</li>
 *   <li>stores the ciphertext in the annotated attribute; before an {@code UPDATE}, also sets the plaintext to
 *       {@code null} when its column is mapped nullable and it is not primitive, so that its column is never written
 *       with a value, and restores it once the {@code UPDATE} is done. A primitive plaintext is never zeroed. The
 *       plaintext column is non-insertable, so an {@code INSERT} needs no clearing.</li>
 * </ol>
 * {@link #decrypt(Object)} runs the same steps in reverse. {@code EMBEDDED} attributes are descended into, so that
 * attributes of an embeddable are covered as well.
 * <p>
 * The mapping of an entity class is validated in full before any instance of it is touched. Each encrypt and decrypt
 * is all-or-nothing per instance: every value is computed before any is assigned.
 * <p>
 * The java types handled are those of the Jakarta Persistence basic types which have a clean, secure encoding: the
 * primitives and their wrappers, {@link String}, {@link BigInteger}, {@link BigDecimal}, the {@code java.time} types,
 * {@link java.util.Date} and the {@code java.sql} temporal types, {@link UUID}, {@code byte[]}, {@code char[]} and
 * their boxed forms, and enums. {@code java.util.Calendar} and {@code java.io.Serializable} are not (#99). A mapping
 * of any other type is rejected when it is validated.
 * <p>
 * The public methods are for a persistence provider integration: an engine which hooks into the provider's own write
 * and read paths, instead of the entity life cycle callbacks, so that a plaintext is never nulled in the entity, and a
 * read never writes. Such an engine encrypts into the entity with a snapshot ({@link #encrypt(Object, Snapshot)}),
 * writes {@link #clearPlaintexts(Class, String, Object) cleared} copies of the
 * {@link #getPlaintextAttributeNames(Class) plaintext attributes} to the row, and {@link #decrypt(Object) decrypts}
 * after a load.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see EntityEncryptionService
 * @see EntityEncryptionListener
 * @see <a href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#a486">2.6. Basic
 *         Types</a> (Jakarta Persistence 3.2 Specification Document)
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public class EntityEncryptor {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    /**
     * The name of the persistence unit property a persistence provider integration reads its
     * {@link EntityEncryptionService encryption service} from. The value is an {@link EntityEncryptionService}
     * instance, its {@link Class}, or the name of its class; a class has to have a public no-arg constructor.
     *
     * @see #getEncryptionService(Object)
     */
    public static final String PROPERTY_ENCRYPTION_SERVICE = "jinahya.persistence.encryption.service";

    /**
     * Returns the encryption service the specified value of {@link #PROPERTY_ENCRYPTION_SERVICE} stands for.
     *
     * @param value the value of the property: an {@link EntityEncryptionService}, its {@link Class}, or the name of
     *              its class.
     * @return the encryption service; a new instance when the {@code value} is a class, or a class name.
     * @throws IllegalArgumentException when the {@code value} is {@code null}, or none of the above.
     */
    public static EntityEncryptionService getEncryptionService(final @org.jspecify.annotations.Nullable Object value) {
        if (value instanceof EntityEncryptionService service) {
            return service;
        }
        final Class<?> type;
        if (value instanceof Class<?> c) {
            type = c;
        } else if (value instanceof String name && !name.isBlank()) {
            try {
                type = Class.forName(name.strip(), true, Thread.currentThread().getContextClassLoader());
            } catch (final ClassNotFoundException cnfe) {
                throw new IllegalArgumentException("no class named " + name + " for " + PROPERTY_ENCRYPTION_SERVICE,
                                                   cnfe);
            }
        } else {
            throw new IllegalArgumentException(
                    PROPERTY_ENCRYPTION_SERVICE + " has to be an EntityEncryptionService, its class, or the name of its"
                    + " class; found " + (value == null ? "nothing" : value.getClass().getName()));
        }
        if (!EntityEncryptionService.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException(type.getName() + " is not an EntityEncryptionService");
        }
        try {
            return (EntityEncryptionService) type.getConstructor().newInstance();
        } catch (final ReflectiveOperationException roe) {
            throw new IllegalArgumentException("failed to instantiate " + type.getName(), roe);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns the attributes of the specified managed type, by name.
     *
     * @param managedType the managed type whose attributes are returned.
     * @return an unmodifiable map of attribute names and attributes of the {@code managedType}.
     */
    private static Map<String, Attribute<?, ?>> getAttributes(final ManagedType<?> managedType) {
        return managedType.getAttributes().stream()
                .collect(Collectors.toUnmodifiableMap(Attribute::getName, Function.identity()));
    }

    // -------------------------------------------------------------------------------------------------------- BUILDERS

    // ------------------------------------------------------------------------------------------ STATIC_FACTORY_METHODS

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     *
     * @param encryptionService the encryption service everything comes from; must not be {@code null}.
     * @implNote The entity manager factory is not resolved here, but on first use: the listener creating this
     *         instance may itself be created while the factory is still being built. The metadata of that factory is
     *         then cached, and never evicted: an instance is meant to live exactly as long as its factory.
     */
    public EntityEncryptor(final EntityEncryptionService encryptionService) {
        super();
        Objects.requireNonNull(encryptionService, "encryptionService is null");
        this.encryptionServiceSupplier = () -> encryptionService;
    }

    /**
     * Creates a new instance for the specified entity manager factory, whose metamodel is read in place of the one of
     * the {@link EntityEncryptionService#getEntityManagerFactory() encryption service's}.
     *
     * @param entityManagerFactory      the entity manager factory whose entities are encrypted; must not be
     *                                  {@code null}.
     * @param encryptionServiceSupplier a supplier of the encryption service, asked on the first use which needs one,
     *                                  never for an entity with no encrypted attribute; must not be {@code null}.
     */
    public EntityEncryptor(final EntityManagerFactory entityManagerFactory,
                           final Supplier<? extends EntityEncryptionService> encryptionServiceSupplier) {
        super();
        this.entityManagerFactory = Objects.requireNonNull(entityManagerFactory, "entityManagerFactory is null");
        this.encryptionServiceSupplier = Objects.requireNonNull(encryptionServiceSupplier,
                                                                "encryptionServiceSupplier is null");
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns the entity manager factory of the {@link EntityEncryptionService encryption service}, asking for it on
     * the first call only.
     *
     * @return the entity manager factory.
     * @throws RuntimeException when the encryption service returns {@code null}.
     * @implNote The service promises the same factory on every call, so it is asked once: what resolving it
     *         costs — a container lookup through a {@code Provider}, say — is the application's, and unknown here. Two
     *         threads may both ask on first use; they get the same factory.
     */
    private EntityManagerFactory entityManagerFactory() {
        var result = entityManagerFactory;
        if (result == null) {
            final var service = encryptionService();
            result = service.getEntityManagerFactory();
            if (result == null) {
                throw new RuntimeException(
                        "encryption service returned a null entity manager factory; service: "
                        + service.getClass().getName());
            }
            entityManagerFactory = result;
        }
        return result;
    }

    /**
     * Returns the encryption service, asking the supplier on the first call only.
     *
     * @return the encryption service.
     * @throws RuntimeException when the supplier returns {@code null}.
     */
    private EntityEncryptionService encryptionService() {
        var result = encryptionService;
        if (result == null) {
            result = encryptionServiceSupplier.get();
            if (result == null) {
                throw new RuntimeException("no encryption service supplied");
            }
            encryptionService = result;
        }
        return result;
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Throws a {@link RuntimeException} naming the attributes and the reason.
     *
     * @param decryptedAttribute the attribute holding the plaintext.
     * @param encryptedAttribute the attribute holding the ciphertext; may be {@code null}.
     * @param reason             why the mapping was rejected.
     * @return the exception to throw.
     */
    private RuntimeException reject(final ManagedType<?> rootType, final List<Attribute<?, ?>> embeddingPath,
                                    final Attribute<?, ?> decryptedAttribute,
                                    final @org.jspecify.annotations.Nullable Attribute<?, ?> encryptedAttribute,
                                    final String reason) {
        return reject(rootType, embeddingPath, decryptedAttribute.getName(),
                      decryptedAttribute.getDeclaringType().getJavaType(), encryptedAttribute, reason);
    }

    /**
     * Returns a {@link RuntimeException} naming the attributes and the reason.
     *
     * @param rootType           the managed type the walk started from.
     * @param embeddingPath      the embedding attributes from the {@code rootType}.
     * @param decryptedName      the name of the attribute holding the plaintext.
     * @param declaringType      the class declaring the plaintext.
     * @param encryptedAttribute the attribute holding the ciphertext; may be {@code null}.
     * @param reason             why the mapping was rejected.
     * @return the exception to throw.
     */
    private static RuntimeException reject(final ManagedType<?> rootType, final List<Attribute<?, ?>> embeddingPath,
                                           final String decryptedName, final Class<?> declaringType,
                                           final @org.jspecify.annotations.Nullable Attribute<?, ?> encryptedAttribute,
                                           final String reason) {
        final var path = new StringBuilder(rootType.getJavaType().getName());
        for (final var embedding : embeddingPath) {
            path.append('.').append(embedding.getName());
        }
        return new RuntimeException(
                reason +
                "; decrypted attribute: " + decryptedName +
                (encryptedAttribute == null ? "" : "; encrypted attribute: " + encryptedAttribute.getName()) +
                "; managed type: " + declaringType.getName() +
                "; path: " + path
        );
    }

    /**
     * Rejects any member of the specified managed type, or of its superclasses, which is annotated with
     * {@link EncryptedAttribute} but is not the java member of one of the specified attributes.
     *
     * @param rootType      the managed type the walk started from.
     * @param embeddingPath the embedding attributes from the {@code rootType} down to the {@code managedType}.
     * @param managedType   the managed type whose members are checked.
     * @param attributes    the attributes of the {@code managedType}.
     * @implNote The rest of the validation walks the metamodel, so an annotated member the metamodel does not
     *         know is never visited: a {@code @Transient} member, an unmapped one, or one on the side which the access
     *         type does not read (a field under property access). Such a member would be silently ignored, and the
     *         plaintext it names would never be encrypted. Bridge and synthetic methods are skipped; {@code javac}
     *         copies a method's annotations onto its bridge.
     */
    private static void rejectUnmappedAnnotatedMembers(final ManagedType<?> rootType,
                                                       final List<Attribute<?, ?>> embeddingPath,
                                                       final ManagedType<?> managedType,
                                                       final Collection<Attribute<?, ?>> attributes) {
        final var mapped = new HashSet<Member>();
        for (final var attribute : attributes) {
            final var member = attribute.getJavaMember();
            if (member != null) {
                mapped.add(member);
            }
        }
        for (final var member : declaredMembers(managedType.getJavaType())) {
            if (!((AnnotatedElement) member).isAnnotationPresent(EncryptedAttribute.class) || mapped.contains(member)) {
                continue;
            }
            final var path = new StringBuilder(rootType.getJavaType().getName());
            for (final var embedding : embeddingPath) {
                path.append('.').append(embedding.getName());
            }
            throw new RuntimeException(
                    "an @EncryptedAttribute member is not a persistent attribute: the annotation sits on the persistent"
                    + " byte[] attribute holding the ciphertext, and names the plaintext; this one is unmapped,"
                    + " transient, or on the side the access type does not read"
                    + "; member: " + member.getDeclaringClass().getName() + '.' + member.getName()
                    + "; managed type: " + managedType.getJavaType().getName()
                    + "; path: " + path
            );
        }
    }

    /**
     * Returns the declared members of the specified class, and of its superclasses, which this module reads annotations
     * from: every field, and every method which is neither a bridge nor synthetic.
     *
     * @param type the class whose members are returned.
     * @return the members of the {@code type} and of its superclasses.
     */
    private static List<Member> declaredMembers(final Class<?> type) {
        final var members = new ArrayList<Member>();
        for (var c = type; c != null && c != Object.class; c = c.getSuperclass()) {
            members.addAll(Arrays.asList(c.getDeclaredFields()));
            for (final var method : c.getDeclaredMethods()) {
                if (!method.isBridge() && !method.isSynthetic()) {
                    members.add(method);
                }
            }
        }
        return members;
    }

    /**
     * Returns a member annotated with {@link EncryptedAttribute} anywhere in the specified managed type, or in an
     * embeddable it embeds, at any depth.
     *
     * @param managedType the managed type to search.
     * @param visited     the managed types already searched, for stopping at a cycle.
     * @return an annotated member; {@code null} when there is none.
     */
    private @org.jspecify.annotations.Nullable Member findEncryptedMember(final ManagedType<?> managedType,
                                                                          final Set<ManagedType<?>> visited) {
        if (!visited.add(managedType)) {
            return null;
        }
        for (final var member : declaredMembers(managedType.getJavaType())) {
            if (((AnnotatedElement) member).isAnnotationPresent(EncryptedAttribute.class)) {
                return member;
            }
        }
        for (final var attribute : getAttributes(managedType).values()) {
            if (attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.EMBEDDED) {
                final var found = findEncryptedMember(
                        entityManagerFactory().getMetamodel().managedType(attribute.getJavaType()), visited);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    /**
     * Returns the declared java type of the specified attribute's member, resolved against the specified concrete
     * class.
     *
     * @param concrete  the class whose instances hold the attribute: the entity, or embeddable, class being validated.
     * @param attribute the attribute.
     * @return the resolved type; the raw type of a parameterized one; the erasure of a type variable which cannot be
     *         resolved.
     * @implNote The declared member, not {@link Attribute#getJavaType()}, decides the codec: providers disagree
     *         on the latter. A member declared as a type variable of a generic superclass, though, is resolved here by
     *         walking the generic superclasses from the {@code concrete} class, binding each level's type arguments.
     */
    private static Class<?> resolveJavaType(final Class<?> concrete, final Attribute<?, ?> attribute) {
        final var member = attribute.getJavaMember();
        if (member instanceof java.lang.reflect.Field field) {
            return resolveType(concrete, field.getGenericType());
        } else if (member instanceof java.lang.reflect.Method method) {
            return resolveType(concrete, method.getGenericReturnType());
        }
        return JinahyaAttributeUtils.getJavaMemberType(attribute);
    }

    /**
     * Returns the specified declared type, resolved against the specified concrete class.
     *
     * @param concrete the class whose instances hold the member.
     * @param type     the generic type of the member.
     * @return the resolved type.
     */
    private static Class<?> resolveType(final Class<?> concrete, final java.lang.reflect.Type type) {
        if (type instanceof Class<?> c) {
            return c;
        }
        // bind every type variable of every generic superclass, from the concrete class up
        final var bindings = new java.util.HashMap<java.lang.reflect.TypeVariable<?>, java.lang.reflect.Type>();
        for (var c = concrete; c != null && c != Object.class; c = c.getSuperclass()) {
            if (c.getGenericSuperclass() instanceof java.lang.reflect.ParameterizedType parameterized
                && parameterized.getRawType() instanceof Class<?> raw) {
                final var variables = raw.getTypeParameters();
                final var arguments = parameterized.getActualTypeArguments();
                for (int i = 0; i < variables.length; i++) {
                    // an argument may itself be a variable of the subclass, bound one level down
                    bindings.put(variables[i], bindings.getOrDefault(arguments[i], arguments[i]));
                }
            }
        }
        var resolved = type;
        while (resolved instanceof java.lang.reflect.TypeVariable<?> variable && bindings.containsKey(variable)) {
            resolved = bindings.get(variable);
        }
        return erase(resolved);
    }

    private static Class<?> erase(final java.lang.reflect.Type type) {
        if (type instanceof Class<?> c) {
            return c;
        }
        if (type instanceof java.lang.reflect.ParameterizedType parameterized) {
            return erase(parameterized.getRawType());
        }
        if (type instanceof java.lang.reflect.GenericArrayType array) {
            return erase(array.getGenericComponentType()).arrayType();
        }
        if (type instanceof java.lang.reflect.TypeVariable<?> variable) {
            return erase(variable.getBounds()[0]);
        }
        if (type instanceof java.lang.reflect.WildcardType wildcard) {
            return erase(wildcard.getUpperBounds()[0]);
        }
        return Object.class;
    }

    /**
     * Returns whether the specified attribute is optional.
     *
     * @param attribute the attribute.
     * @return {@code true} when the {@code attribute} is optional.
     * @implNote {@link SingularAttribute#isOptional()} is the provider's own answer, so unlike a column's write
     *         flags this fact is already effective — a mapping expressed in XML is reflected here.
     */
    private static boolean isOptional(final Attribute<?, ?> attribute) {
        return !(attribute instanceof SingularAttribute<?, ?> singular) || singular.isOptional();
    }

    private static boolean isIdOrVersion(final Attribute<?, ?> attribute) {
        return attribute instanceof SingularAttribute<?, ?> singular
               && (singular.isId() || singular.isVersion());
    }

    /**
     * Returns the column rules of the specified attribute, as its annotations describe them: the applicable
     * {@link AttributeOverride @AttributeOverride}, if any, and otherwise the attribute's own {@link Column @Column},
     * honouring the annotation defaults when neither is present.
     *
     * @param rootType      the managed type the walk started from.
     * @param embeddingPath the embedding attributes from the {@code rootType} down to the {@code attribute}'s owner;
     *                      empty when the {@code attribute} belongs to the {@code rootType} itself.
     * @param attribute     the attribute whose rules are returned.
     * @return the rules of the {@code attribute}.
     * @implNote Jakarta Persistence exposes no portable accessor for effective per-column metadata, so a
     *         mapping expressed in {@code orm.xml} is not visible here.
     */
    private static ColumnRules resolveColumnRules(final ManagedType<?> rootType,
                                                  final List<Attribute<?, ?>> embeddingPath,
                                                  final Attribute<?, ?> attribute) {
        final var override = findAttributeOverride(rootType, embeddingPath, attribute);
        if (override != null) {
            // an override supplies its own @Column, with its own defaults; it replaces the member's, never merges
            return rulesOf(override.column(), "@AttributeOverride(\"" + override.name() + "\")");
        }
        final var column = JinahyaAttributeUtils.getJavaMemberAnnotation(attribute, Column.class);
        return column == null
                ? new ColumnRules(true, true, true, "@Column defaults")
                : rulesOf(column, "@Column");
    }

    private static ColumnRules rulesOf(final Column column, final String source) {
        return new ColumnRules(column.insertable(), column.updatable(), column.nullable(), source);
    }

    private static AttributeOverride[] overridesOn(final AnnotatedElement element) {
        return element.getAnnotationsByType(AttributeOverride.class);
    }

    /**
     * Returns the {@link AttributeOverride @AttributeOverride} which applies to the specified attribute, the outermost
     * one winning.
     */
    private static @org.jspecify.annotations.Nullable AttributeOverride findAttributeOverride(
            final ManagedType<?> rootType,
            final List<Attribute<?, ?>> embeddingPath,
            final Attribute<?, ?> attribute) {
        final var names = new ArrayList<String>();
        for (final var embedding : embeddingPath) {
            names.add(embedding.getName());
        }
        names.add(attribute.getName());
        // the root type may override by the whole dotted path, and each embedding attribute by its own suffix
        for (int i = 0; i <= embeddingPath.size(); i++) {
            final var suffix = String.join(".", names.subList(i, names.size()));
            if (i == 0) {
                // the root class, and anything it inherits a mapping from, may override by the whole dotted path
                for (var c = rootType.getJavaType(); c != null && c != Object.class; c = c.getSuperclass()) {
                    for (final var override : overridesOn(c)) {
                        if (override.name().equals(suffix)) {
                            return override;
                        }
                    }
                }
                continue;
            }
            final var member = embeddingPath.get(i - 1).getJavaMember();
            if (!(member instanceof AnnotatedElement annotated)) {
                continue;
            }
            for (final var override : overridesOn(annotated)) {
                if (override.name().equals(suffix)) {
                    return override;
                }
            }
        }
        return null;
    }

    /**
     * Validates every {@link EncryptedAttribute annotated attribute} of the specified managed type — each holding the
     * ciphertext of the plaintext it names — and returns the pairs, and the embedded attributes, to walk.
     *
     * @param rootType      the managed type the walk started from.
     * @param embeddingPath the embedding attributes from the {@code rootType} down to the {@code managedType}.
     * @param managedType   the managed type to validate.
     * @param visiting      the managed types on the current path, for detecting a cycle.
     * @return the validated mapping of the {@code managedType}.
     * @throws RuntimeException when any annotated attribute of the {@code managedType} is mapped inconsistently.
     * @implNote Runs once per entity class, and descends through the whole reachable embeddable graph to
     *         completion before any instance is touched, so that an inconsistent mapping cannot leave an instance
     *         half-encrypted. An embeddable is validated anew for each path it is reached by: an override on one path
     *         can make it safe, or unsafe, where it is not on another.
     */
    private EncryptionMapping validate(final ManagedType<?> rootType, final List<Attribute<?, ?>> embeddingPath,
                                       final ManagedType<?> managedType, final Set<ManagedType<?>> visiting) {
        if (!visiting.add(managedType)) {
            throw new RuntimeException("embeddable cycle through " + managedType.getJavaType().getName());
        }
        final var attributes = getAttributes(managedType);
        rejectUnmappedAnnotatedMembers(rootType, embeddingPath, managedType, attributes.values());
        final var pairs = new ArrayList<Pair>();
        final var embeddeds = new ArrayList<Embedded>();
        final var paired = new HashSet<String>(); // the names of the plaintexts already paired
        for (final var attribute : attributes.values()) {
            final var persistentAttributeType = attribute.getPersistentAttributeType();
            final var annotation = JinahyaAttributeUtils.getJavaMemberAnnotation(attribute, EncryptedAttribute.class);
            if (persistentAttributeType == Attribute.PersistentAttributeType.EMBEDDED) {
                if (annotation != null) {
                    throw reject(rootType, embeddingPath, annotation.value(), declaringClassOf(attribute), attribute,
                                 "an @Embedded attribute cannot itself be annotated"
                                 + "; annotate the attributes holding the ciphertexts inside the embeddable");
                }
                // descend now, so that an invalid embeddable cannot surface only once a sibling has been transformed,
                // and keep the child mapping: it was resolved with this path's overrides in scope
                final var childPath = new ArrayList<>(embeddingPath);
                childPath.add(attribute);
                final var childMapping = validate(
                        rootType,
                        List.copyOf(childPath),
                        entityManagerFactory().getMetamodel().managedType(attribute.getJavaType()),
                        visiting
                );
                embeddeds.add(new Embedded(attribute, childMapping));
                continue;
            }
            if (persistentAttributeType == Attribute.PersistentAttributeType.ELEMENT_COLLECTION
                && attribute instanceof PluralAttribute<?, ?, ?> plural) {
                // an embeddable reached through a collection is never walked, so anything encrypted inside it would
                // be persisted in the clear to the collection table; that is an error, not something to skip past
                final var reached = new ArrayList<ManagedType<?>>();
                if (plural.getElementType() instanceof ManagedType<?> element) {
                    reached.add(element);
                }
                if (plural instanceof MapAttribute<?, ?, ?> map && map.getKeyType() instanceof ManagedType<?> key) {
                    reached.add(key);
                }
                for (final var embeddable : reached) {
                    final var encrypted = findEncryptedMember(embeddable, new HashSet<>());
                    if (encrypted != null) {
                        throw reject(rootType, embeddingPath, attribute.getName(), declaringClassOf(attribute), null,
                                     "an embeddable reached through an @ElementCollection cannot hold an encrypted"
                                     + " attribute; its plaintext would be persisted in the clear"
                                     + "; encrypted member: " + encrypted.getDeclaringClass().getName() + '.'
                                     + encrypted.getName());
                    }
                }
            }
            if (annotation == null) {
                continue;
            }
            // the annotated attribute holds the ciphertext; the plaintext is the attribute it names
            final var plaintextName = annotation.value();
            final var encryptedAttribute = checkEncryptedAttribute(rootType, embeddingPath, attribute, plaintextName);
            if (!paired.add(plaintextName)) {
                throw reject(rootType, embeddingPath, plaintextName, declaringClassOf(encryptedAttribute),
                             encryptedAttribute, "a plaintext is named by more than one encrypted attribute");
            }
            final var decryptedAttribute = attributes.get(plaintextName);
            if (decryptedAttribute == null) {
                throw reject(rootType, embeddingPath, plaintextName, declaringClassOf(encryptedAttribute),
                             encryptedAttribute,
                             "no plaintext named '" + plaintextName + "': it has to be a persistent attribute of the"
                             + " same entity, or embeddable");
            }
            pairs.add(checkMappedPlaintext(rootType, embeddingPath, managedType, decryptedAttribute,
                                           encryptedAttribute));
        }
        visiting.remove(managedType);
        return new EncryptionMapping(List.copyOf(pairs), List.copyOf(embeddeds));
    }

    private static Class<?> declaringClassOf(final Attribute<?, ?> attribute) {
        return attribute.getDeclaringType().getJavaType();
    }

    /**
     * Validates a persistent plaintext attribute, and returns its pair.
     *
     * @param rootType           the managed type the walk started from.
     * @param embeddingPath      the embedding attributes from the {@code rootType}.
     * @param managedType        the managed type declaring, or inheriting, both attributes.
     * @param decryptedAttribute the attribute holding the plaintext.
     * @param encryptedAttribute the attribute holding the ciphertext, already validated.
     * @return the validated pair.
     */
    private Pair checkMappedPlaintext(final ManagedType<?> rootType, final List<Attribute<?, ?>> embeddingPath,
                                      final ManagedType<?> managedType, final Attribute<?, ?> decryptedAttribute,
                                      final Attribute<?, ?> encryptedAttribute) {
        if (JinahyaAttributeUtils.getJavaMemberAnnotation(decryptedAttribute, EncryptedAttribute.class) != null) {
            throw reject(rootType, embeddingPath, decryptedAttribute, encryptedAttribute,
                         "a decrypted attribute cannot itself be annotated with @EncryptedAttribute");
        }
        final var persistentAttributeType = decryptedAttribute.getPersistentAttributeType();
        if (persistentAttributeType != Attribute.PersistentAttributeType.BASIC) {
            throw reject(rootType, embeddingPath, decryptedAttribute, encryptedAttribute,
                         "only a BASIC attribute can be encrypted; found " + persistentAttributeType);
        }
        if (isIdOrVersion(decryptedAttribute)) {
            throw reject(rootType, embeddingPath, decryptedAttribute, encryptedAttribute,
                         "an identifier, or a version, attribute cannot be encrypted");
        }
        // the declared java member decides the codec, in both directions; know it is one before any value moves.
        // A member inherited from a generic superclass is declared as a type variable: resolve it against the
        // concrete class, or Base<String> would be encoded as its erasure, Object (#67)
        final var javaType = resolveJavaType(managedType.getJavaType(), decryptedAttribute);
        if (!EntityEncryptorUtils.supports(javaType)) {
            throw reject(rootType, embeddingPath, decryptedAttribute, encryptedAttribute,
                         "no codec for the declared java type " + javaType.getName());
        }
        // the plaintext column must not be writable: @PrePersist runs at persist(), not when the INSERT is built,
        // so a provider which builds one statement at commit would otherwise carry a late assignment in the clear
        final var rules = resolveColumnRules(rootType, embeddingPath, decryptedAttribute);
        if (rules.insertable()) {
            throw reject(rootType, embeddingPath, decryptedAttribute, encryptedAttribute,
                         "a decrypted attribute's column has to be non-insertable"
                         + "; otherwise an INSERT can carry the plaintext (from " + rules.source() + ")");
        }
        // the mapping, not the database, decides: the plaintext is nulled before an UPDATE only when its column is
        // mapped nullable; a primitive can't be nulled. One which is not nulled goes out with the UPDATE as it is,
        // unless its column is mapped non-updatable
        final var nulled = !javaType.isPrimitive() && rules.nullable();
        if (nulled && !isOptional(decryptedAttribute)) {
            throw reject(rootType, embeddingPath, decryptedAttribute, encryptedAttribute,
                         "a decrypted attribute which is nulled has to be optional");
        }
        // a legacy row (plaintext in this column, no ciphertext) is migrated by the UPDATE which stores its
        // ciphertext, and only that UPDATE nulls this column; a non-updatable one keeps the legacy plaintext forever
        if (nulled && !rules.updatable()) {
            throw reject(rootType, embeddingPath, decryptedAttribute, encryptedAttribute,
                         "a nulled decrypted attribute's column has to be updatable"
                         + "; otherwise a migrated legacy row keeps its plaintext (from " + rules.source() + ")");
        }
        return new Pair(decryptedAttribute, encryptedAttribute, javaType, nulled);
    }

    /**
     * Validates an attribute annotated with {@link EncryptedAttribute}, which holds the ciphertext, and returns it.
     *
     * @param rootType           the managed type the walk started from.
     * @param embeddingPath      the embedding attributes from the {@code rootType}.
     * @param encryptedAttribute the annotated attribute.
     * @param decryptedName      the name of the plaintext, as given by {@link EncryptedAttribute#value()}.
     * @return the {@code encryptedAttribute}.
     */
    private Attribute<?, ?> checkEncryptedAttribute(final ManagedType<?> rootType,
                                                    final List<Attribute<?, ?>> embeddingPath,
                                                    final Attribute<?, ?> encryptedAttribute,
                                                    final String decryptedName) {
        final var declaringType = declaringClassOf(encryptedAttribute);
        if (decryptedName.isBlank()) {
            throw reject(rootType, embeddingPath, decryptedName, declaringType, encryptedAttribute,
                         "@EncryptedAttribute has to name the attribute, or field, holding the plaintext");
        }
        if (decryptedName.equals(encryptedAttribute.getName())) {
            throw reject(rootType, embeddingPath, decryptedName, declaringType, encryptedAttribute,
                         "an encrypted attribute cannot name itself as its plaintext");
        }
        if (encryptedAttribute.getPersistentAttributeType() != Attribute.PersistentAttributeType.BASIC) {
            throw reject(rootType, embeddingPath, decryptedName, declaringType, encryptedAttribute,
                         "an encrypted attribute has to be BASIC");
        }
        if (encryptedAttribute.getJavaType() != byte[].class) {
            throw reject(rootType, embeddingPath, decryptedName, declaringType, encryptedAttribute,
                         "an encrypted attribute has to be typed byte[]; @EncryptedAttribute sits on the attribute"
                         + " holding the ciphertext, and names the plaintext");
        }
        if (isIdOrVersion(encryptedAttribute)) {
            throw reject(rootType, embeddingPath, decryptedName, declaringType, encryptedAttribute,
                         "an identifier, or a version, attribute cannot hold the ciphertext");
        }
        if (!isOptional(encryptedAttribute)) {
            throw reject(rootType, embeddingPath, decryptedName, declaringType, encryptedAttribute,
                         "an encrypted attribute has to be optional");
        }
        final var encryptedRules = resolveColumnRules(rootType, embeddingPath, encryptedAttribute);
        if (!encryptedRules.insertable() || !encryptedRules.updatable()) {
            throw reject(rootType, embeddingPath, decryptedName, declaringType, encryptedAttribute,
                         "an encrypted attribute's column has to be insertable and updatable"
                         + "; otherwise the ciphertext cannot be stored (from " + encryptedRules.source() + ")");
        }
        if (!encryptedRules.nullable()) {
            throw reject(rootType, embeddingPath, decryptedName, declaringType, encryptedAttribute,
                         "an encrypted attribute's column has to be nullable"
                         + "; decrypting nulls it (from " + encryptedRules.source() + ")");
        }
        return encryptedAttribute;
    }

    /**
     * Returns the validated mapping of the specified entity class: its pairs, and the mapping of every embeddable it
     * reaches, each built for its own embedding path. Validated in full on first use, then cached.
     *
     * @param entityClass the entity class.
     * @return the validated mapping of the {@code entityClass}.
     * @throws RuntimeException when the mapping is invalid; it is not cached, so every later use fails the same way.
     */
    private EncryptionMapping getMapping(final Class<?> entityClass) {
        return mappings.computeIfAbsent(entityClass, k -> {
            final var managedType = entityManagerFactory().getMetamodel().managedType(k);
            return validate(managedType, List.of(), managedType, new HashSet<>());
        });
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Encrypts the annotated attributes of the specified object within the specified context.
     *
     * @param context              the context of the entity instance being encrypted, or decrypted.
     * @param object               the entity, or embeddable, instance to encrypt, in place.
     * @param clearPlaintext       whether to null each plaintext which is to be nulled once encrypted.
     * @throws RuntimeException when an attribute pair is inconsistent, or when an attribute has a java type which
     *                          cannot be turned into bytes.
     * @see #encrypt(Object, boolean)
     */
    private void encrypt(final Context context, final Object object,
                         final boolean clearPlaintext) {
        Objects.requireNonNull(object, "object is null");
        // validates the whole reachable graph before anything is touched
        final var mapping = getMapping(resolveClass(object));
        // computes every ciphertext before assigning any, so that a failure part-way leaves the instance as it was
        final var assignments = new ArrayList<Runnable>();
        encrypt(context, object, mapping, clearPlaintext, null, assignments);
        assignments.forEach(Runnable::run);
    }

    /**
     * Encrypts the specified object against the mapping already validated for it.
     *
     * @param context              the context of the entity instance being encrypted, or decrypted.
     * @param object               the entity, or embeddable, instance to encrypt, in place.
     * @param mapping              the validated mapping of the {@code object}.
     * @param clearPlaintext       whether to null each plaintext which is to be nulled once encrypted.
     * @param snapshot             the values the {@code object}'s attributes had when it was last read or written,
     *                             by attribute; {@code null} when there is none, and every plaintext is encrypted.
     * @param assignments          the list to which every attribute assignment is added, rather than made.
     * @implNote The mapping is passed down rather than looked up again: an embeddable reached through an
     *         {@link AttributeOverride @AttributeOverride} would otherwise be re-resolved without that override in
     *         scope. Nothing is assigned here; the caller applies the {@code assignments} once all of them have been
     *         computed.
     */
    private void encrypt(final Context context, final Object object,
                         final EncryptionMapping mapping, final boolean clearPlaintext,
                         final @org.jspecify.annotations.Nullable AttributeSnapshot snapshot,
                         final List<Runnable> assignments) {
        for (final var embedded : mapping.embeddeds()) {
            final var embeddedValue = JinahyaAttributeUtils.getAttributeValue(object, embedded.attribute());
            if (embeddedValue != null) {
                // an embeddable with no snapshot value was null, or is new: everything in it is encrypted
                final var embeddedAttributeSnapshot = snapshot == null ? null : snapshot.get(embedded.attribute());
                encrypt(context, embeddedValue, embedded.mapping(), clearPlaintext,
                        embeddedAttributeSnapshot == null
                                ? null
                                : a -> JinahyaAttributeUtils.getAttributeValue(embeddedAttributeSnapshot, a),
                        assignments);
            }
        }
        for (final var pair : mapping.pairs()) {
            // the plaintext is the truth: no plaintext is no value
            final var plaintext = pair.getPlaintext(object);
            if (plaintext == null) {
                assignments.add(pair.setCiphertext(object, null));
                continue;
            }
            if (snapshot != null && isUnchanged(pair, object, plaintext, snapshot)) {
                // the ciphertext already stored is that of this very plaintext; encrypting again would only make the
                // row differ, and write
                continue;
            }
            assignments.add(pair.setCiphertext(
                    object, encryptEncoded(context, pair, encode(pair, plaintext))));
            if (clearPlaintext && pair.nulled()) {
                // the plaintext column is mapped nullable: an UPDATE would carry the value, so it goes out as null;
                // @PostUpdate restores it
                assignments.add(pair.setPlaintext(object, null));
            }
        }
    }

    /**
     * Returns whether the specified plaintext, and the ciphertext the object holds, are those of the snapshot.
     *
     * @param pair      the pair.
     * @param object    the entity, or embeddable, instance.
     * @param plaintext the plaintext the {@code object} holds; not {@code null}.
     * @param snapshot  the snapshot of the {@code object}.
     * @return {@code true} when the ciphertext the {@code object} holds is the one the snapshot holds, and the
     *         {@code plaintext} encodes to the same bytes as the snapshot's plaintext.
     * @implNote The encodings are compared, never the values: they are what is encrypted, and a codec's
     *         equality is exact where a type's {@code equals} may not be ({@code java.sql.Timestamp}, arrays).
     */
    private static boolean isUnchanged(final Pair pair, final Object object, final Object plaintext,
                                       final AttributeSnapshot snapshot) {
        final var ciphertext = pair.getCiphertext(object);
        if (ciphertext == null || !(snapshot.get(pair.encrypted()) instanceof byte[] previousCiphertext)
            || !Arrays.equals(ciphertext, previousCiphertext)) {
            // a legacy row, a new value, or a ciphertext assigned by hand
            return false;
        }
        final var previousPlaintext = snapshot.get(pair.decrypted());
        return previousPlaintext != null && Arrays.equals(encode(pair, plaintext), encode(pair, previousPlaintext));
    }

    private byte[] encryptEncoded(final Context context, final Pair pair,
                                  final byte[] encoded) {
        final var encryptionService = encryptionService();
        final var encrypted = encryptionService.encrypt(initialized(context), encoded);
        if (encrypted == null) {
            throw new RuntimeException("encryption service returned null; decrypted attribute: " + pair.name());
        }
        // a copy: the entity must not share an array the service may keep, or reuse
        return encrypted.clone();
    }

    /**
     * Returns whether the specified entity instance is to be transformed: whether its validated mapping has an
     * {@link EncryptedAttribute encrypted attribute}, directly or inside an embeddable.
     *
     * @param object the entity instance.
     * @return {@code true} when the {@code object} is to be transformed; {@code false} when it passes through.
     * @throws RuntimeException when the mapping of the {@code object}'s class is invalid.
     */
    private boolean hasEncryptedAttributes(final Object object) {
        return getMapping(resolveClass(object)).hasPairs();
    }

    /**
     * Encrypts the annotated attributes of the specified object, with the identifier the
     * {@link EntityEncryptionService encryption service} derives from it.
     *
     * @param object         the entity instance to encrypt, in place; an instance of a class which has no encrypted
     *                       attribute passes through untouched.
     * @param clearPlaintext whether to null each plaintext which is to be nulled once encrypted: {@code true} before an
     *                       {@code UPDATE}, which would carry the updatable plaintext column; {@code false} before an
     *                       {@code INSERT}, which never carries it, the column being non-insertable.
     * @throws RuntimeException when an attribute pair is inconsistent, or when an attribute has a java type which
     *                          cannot be turned into bytes.
     * @see EntityEncryptionService#init(EntityEncryptionContext)
     */
    void encrypt(final Object object, final boolean clearPlaintext) {
        Objects.requireNonNull(object, "object is null");
        if (!hasEncryptedAttributes(object)) {
            return;
        }
        encrypt(new Context(object), object, clearPlaintext);
    }

    /**
     * Encrypts, in place, each plaintext of the specified entity instance which differs from the specified snapshot,
     * storing its ciphertext; a plaintext is never cleared.
     * <p>
     * A plaintext is encrypted again only when it encodes differently from the snapshot's, or when the ciphertext the
     * instance holds is not the snapshot's, which includes a legacy row, whose ciphertext is {@code null}. A
     * {@code null} plaintext always yields a {@code null} ciphertext.
     *
     * @param entity   the entity instance to encrypt, in place; an instance of a class which has no encrypted
     *                 attribute passes through untouched.
     * @param snapshot the values the instance's attributes had when it was last read or written, by the name of an
     *                 attribute of the entity class; an embedded attribute maps to an instance of its embeddable, read
     *                 the same way. {@code null} when there is none, as before an {@code INSERT}: every plaintext is
     *                 then encrypted.
     * @throws RuntimeException when the mapping is invalid, or when a plaintext cannot be encoded.
     */
    public void encrypt(final Object entity,
                        final @org.jspecify.annotations.Nullable Snapshot snapshot) {
        Objects.requireNonNull(entity, "entity is null");
        if (!hasEncryptedAttributes(entity)) {
            return;
        }
        final var context = new Context(entity);
        final var mapping = getMapping(resolveClass(entity));
        final var assignments = new ArrayList<Runnable>();
        encrypt(context, entity, mapping, false,
                snapshot == null ? null : a -> snapshot.get(a.getName()), assignments);
        assignments.forEach(Runnable::run);
    }

    /**
     * Returns whether the specified entity class has an {@link EncryptedAttribute encrypted attribute}, directly or
     * inside an embeddable.
     *
     * @param entityClass the entity class.
     * @return {@code true} when instances of the {@code entityClass} are encrypted.
     * @throws RuntimeException when the mapping of the {@code entityClass} is invalid.
     */
    public boolean hasEncryptedAttributes(final Class<?> entityClass) {
        return getMapping(entityClass).hasPairs();
    }

    /**
     * Returns the names of the attributes of the specified entity class which hold a plaintext: a plaintext attribute
     * of the class itself, or an embedded attribute whose embeddable holds one, at any depth.
     *
     * @param entityClass the entity class.
     * @return an unmodifiable set of attribute names; empty when the {@code entityClass} is not encrypted.
     */
    public Set<String> getPlaintextAttributeNames(final Class<?> entityClass) {
        final var mapping = getMapping(entityClass);
        final var names = new java.util.LinkedHashSet<String>();
        mapping.pairs().forEach(p -> names.add(p.name()));
        mapping.embeddeds().stream()
                .filter(e -> e.mapping().hasPairs())
                .forEach(e -> names.add(e.attribute().getName()));
        return Set.copyOf(names);
    }

    /**
     * Returns the names of the attributes of the specified entity class itself which hold a ciphertext. Those inside
     * an embeddable are reached through its {@link #getPlaintextAttributeNames(Class) embedded attribute}.
     *
     * @param entityClass the entity class.
     * @return an unmodifiable set of attribute names; empty when the {@code entityClass} has none of its own.
     */
    public Set<String> getCiphertextAttributeNames(final Class<?> entityClass) {
        return getMapping(entityClass).pairs().stream()
                .map(p -> p.encrypted().getName())
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Returns the paths, from the specified entity class, of the plaintexts which are nulled: not primitive, and
     * their columns mapped nullable. A path is the names of the embedded attributes leading to the plaintext, if any,
     * followed by the plaintext's own name.
     *
     * @param entityClass the entity class.
     * @return an unmodifiable list of unmodifiable paths; empty when the {@code entityClass} has none.
     */
    public List<List<String>> getNulledPlaintextPaths(final Class<?> entityClass) {
        final var paths = new ArrayList<List<String>>();
        collectNulledPlaintextPaths(getMapping(entityClass), List.of(), paths);
        return List.copyOf(paths);
    }

    private static void collectNulledPlaintextPaths(final EncryptionMapping mapping, final List<String> prefix,
                                                    final List<List<String>> paths) {
        for (final var pair : mapping.pairs()) {
            if (pair.nulled()) {
                final var path = new ArrayList<>(prefix);
                path.add(pair.name());
                paths.add(List.copyOf(path));
            }
        }
        for (final var embedded : mapping.embeddeds()) {
            final var path = new ArrayList<>(prefix);
            path.add(embedded.attribute().getName());
            collectNulledPlaintextPaths(embedded.mapping(), path, paths);
        }
    }

    /**
     * Returns the value to write for the specified attribute of the specified entity class: {@code null} for a
     * plaintext which is nulled — not primitive, and its column mapped nullable — and, for an embedded attribute, the
     * specified value with each such plaintext inside it, at any depth, set to {@code null}. Any other value is
     * returned as it is.
     *
     * @param entityClass   the entity class.
     * @param attributeName the name of an attribute of the {@code entityClass}.
     * @param value         the value of the attribute; for an embedded attribute, a <em>copy</em> of the
     *                      instance's, which is modified.
     * @return the value to write.
     */
    public @org.jspecify.annotations.Nullable Object clearPlaintexts(
            final Class<?> entityClass, final String attributeName,
            final @org.jspecify.annotations.Nullable Object value) {
        final var mapping = getMapping(entityClass);
        for (final var pair : mapping.pairs()) {
            if (pair.name().equals(attributeName)) {
                return pair.nulled() ? null : value;
            }
        }
        if (value != null) {
            for (final var embedded : mapping.embeddeds()) {
                if (embedded.attribute().getName().equals(attributeName)) {
                    clearPlaintexts(value, embedded.mapping());
                    break;
                }
            }
        }
        return value;
    }

    private static void clearPlaintexts(final Object object, final EncryptionMapping mapping) {
        for (final var embedded : mapping.embeddeds()) {
            final var embeddedValue = JinahyaAttributeUtils.getAttributeValue(object, embedded.attribute());
            if (embeddedValue != null) {
                clearPlaintexts(embeddedValue, embedded.mapping());
            }
        }
        for (final var pair : mapping.pairs()) {
            if (pair.nulled()) {
                pair.setPlaintext(object, null).run();
            }
        }
    }

    /**
     * Encrypts the annotated attributes of the specified object, and clears each plaintext, as before an
     * {@code UPDATE}.
     *
     * @param object the entity instance to encrypt, in place.
     * @see #encrypt(Object, boolean)
     */
    void encrypt(final Object object) {
        encrypt(object, true);
    }

    /**
     * Decrypts the annotated attributes of the specified object within the specified context.
     *
     * @param context              the context of the entity instance being encrypted, or decrypted.
     * @param object               the entity, or embeddable, instance to decrypt, in place.
     * @throws RuntimeException when an attribute pair is inconsistent, or when an attribute has a java type which
     *                          cannot be reconstructed from bytes.
     * @see #decrypt(Object)
     */
    private void decrypt(final Context context, final Object object) {
        Objects.requireNonNull(object, "object is null");
        // validates the whole reachable graph before anything is touched
        final var mapping = getMapping(resolveClass(object));
        // decodes every value before assigning any, so that a failure part-way leaves the instance as it was
        final var assignments = new ArrayList<Runnable>();
        decrypt(context, object, mapping, assignments);
        assignments.forEach(Runnable::run);
    }

    /**
     * Decrypts the specified object against the mapping already validated for it.
     *
     * @param context              the context of the entity instance being encrypted, or decrypted.
     * @param object               the entity, or embeddable, instance to decrypt, in place.
     * @param mapping              the validated mapping of the {@code object}.
     * @param assignments          the list to which every attribute assignment is added, rather than made.
     * @implNote The mapping is passed down rather than looked up again: an embeddable reached through an
     *         {@link AttributeOverride @AttributeOverride} would otherwise be re-resolved without that override in
     *         scope. Nothing is assigned here; the caller applies the {@code assignments} once all of them have been
     *         computed.
     */
    private void decrypt(final Context context, final Object object,
                         final EncryptionMapping mapping, final List<Runnable> assignments) {
        for (final var embedded : mapping.embeddeds()) {
            final var embeddedValue = JinahyaAttributeUtils.getAttributeValue(object, embedded.attribute());
            if (embeddedValue != null) {
                decrypt(context, embeddedValue, embedded.mapping(), assignments);
            }
        }
        for (final var pair : mapping.pairs()) {
            final var ciphertext = pair.getCiphertext(object);
            if (ciphertext == null) {
                // a legacy row keeps its plaintext; it is migrated when it is next written
                if (pair.getPlaintext(object) == null) {
                    assignments.add(pair.setPlaintext(object, null));
                }
                continue;
            }
            final var returned = encryptionService().decrypt(initialized(context), ciphertext.clone());
            if (returned == null) {
                throw new RuntimeException("encryption service returned null; decrypted attribute: " + pair.name());
            }
            // a copy: the payload is decoded from an array the service no longer holds
            final var decryptedBytes = returned.clone();
            assignments.add(pair.setPlaintext(object, decode(pair, decryptedBytes)));
        }
    }

    /**
     * Decrypts the annotated attributes of the specified object, with the identifier the
     * {@link EntityEncryptionService encryption service} derives from it.
     *
     * @param object the entity instance to decrypt, in place; an instance of a class which has no encrypted attribute
     *               passes through untouched.
     * @throws RuntimeException when an attribute pair is inconsistent, or when an attribute has a java type which
     *                          cannot be reconstructed from bytes.
     * @see EntityEncryptionService#init(EntityEncryptionContext)
     */
    public void decrypt(final Object object) {
        Objects.requireNonNull(object, "object is null");
        if (!hasEncryptedAttributes(object)) {
            return;
        }
        decrypt(new Context(object), object);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns the class the metamodel knows the specified object by.
     *
     * @param object the entity, or embeddable, instance.
     * @return the class to look the {@code object} up by.
     * @implNote {@link Object#getClass() getClass()} is not it: a lazily-loaded reference is an instance of a
     *         provider-generated subclass, which the metamodel has never heard of, so asking it directly failed with
     *         {@code Not a managed type: ..._SecretEntity$HibernateProxy}. Jakarta Persistence 3.2 added
     *         {@link jakarta.persistence.PersistenceUnitUtil#getClass(Object)} for exactly this. It is defined for
     *         <em>entity</em> instances, so anything else falls back to its own class.
     */
    private Class<?> resolveClass(final Object object) {
        try {
            return entityManagerFactory().getPersistenceUnitUtil().getClass(object);
        } catch (final RuntimeException re) {
            logger.log(System.Logger.Level.TRACE,
                       () -> "not an entity instance; using its own class: " + object.getClass().getName());
            return object.getClass();
        }
    }

    /**
     * The context of one entity instance, for one encryption, or one decryption, of it.
     *
     * @implNote Created for each operation, and confined to its thread; the encryption service is
     *         {@link EntityEncryptionService#init(EntityEncryptionContext) initialized} with it on the first attribute
     *         which needs the service, and never when none does.
     */
    private static final class Context implements EntityEncryptionContext {

        private Context(final Object entityInstance) {
            super();
            this.entityInstance = Objects.requireNonNull(entityInstance, "entityInstance is null");
        }

        @Override
        public Object getEntityInstance() {
            return entityInstance;
        }

        @Override
        public @org.jspecify.annotations.Nullable Object getAttachment() {
            return attachment;
        }

        @Override
        public void setAttachment(final @org.jspecify.annotations.Nullable Object attachment) {
            this.attachment = attachment;
        }

        private final Object entityInstance;

        private @org.jspecify.annotations.Nullable Object attachment;

        // whether the encryption service has been initialized with this context
        private boolean initialized;
    }

    /**
     * Returns the specified context, initializing the encryption service with it first, on its first use.
     *
     * @param context the context.
     * @return the {@code context}.
     */
    private EntityEncryptionContext initialized(final Context context) {
        if (!context.initialized) {
            encryptionService().init(context);
            context.initialized = true;
        }
        return context;
    }

    /**
     * The values an entity instance's attributes had when it was last read or written, as the persistence provider
     * keeps them for checking the instance for changes.
     *
     * @see #encrypt(Object, Snapshot)
     */
    @FunctionalInterface
    public interface Snapshot {

        /**
         * Returns the value the specified attribute had.
         *
         * @param attributeName the name of an attribute of the entity class; for an embedded attribute, the value is
         *                      an instance of its embeddable.
         * @return the value; may be {@code null}.
         */
        @org.jspecify.annotations.Nullable
        Object get(String attributeName);
    }

    /**
     * The values an instance's attributes had when it was last read or written.
     */
    @FunctionalInterface
    private interface AttributeSnapshot {

        /**
         * Returns the value the specified attribute had.
         *
         * @param attribute the attribute.
         * @return the value; may be {@code null}.
         */
        @org.jspecify.annotations.Nullable
        Object get(Attribute<?, ?> attribute);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final Supplier<? extends EntityEncryptionService> encryptionServiceSupplier;

    // the encryption service, asked for on first use
    private volatile @org.jspecify.annotations.Nullable EntityEncryptionService encryptionService;

    // the encryption service's entity manager factory, asked for on first use
    private volatile @org.jspecify.annotations.Nullable EntityManagerFactory entityManagerFactory;

    // -----------------------------------------------------------------------------------------------------------------
    // the validated mapping of each entity class; never evicted: it describes the encryption service's entity manager
    // factory, and lives exactly as long as it does
    private final Map<Class<?>, EncryptionMapping> mappings = new ConcurrentHashMap<>();
}
