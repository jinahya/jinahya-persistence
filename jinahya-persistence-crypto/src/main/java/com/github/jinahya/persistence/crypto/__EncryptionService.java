package com.github.jinahya.persistence.crypto;

import com.github.jinahya.persistence.metamodel.JinahyaAttributeUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.Shutdown;
import jakarta.enterprise.event.Startup;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.ManagedType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.Nullable;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.AnnotatedElement;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.Bytes_l;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.Characters_2l;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.big_decimal_;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.big_integer_;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.boolean_1;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.byte_1;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.char_2;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.chars_2l;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.double_8;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.enum_;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.float_4;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.instant_12;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.int_4;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.local_date_8;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.local_date_time_16;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.local_time_8;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.long_8;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.offset_date_time_20;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.offset_time_12;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.serializable_;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.short_2;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.sql_date_8;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.sql_time_8;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.sql_timestamp_16;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.string_;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.util_calendar_8;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.util_date_8;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.uuid_16;
import static com.github.jinahya.persistence.crypto.__EncryptionServiceUtils.year_4;

/**
 * An abstract service which encrypts and decrypts the {@link __EncryptedAttribute annotated attributes} of an entity
 * instance, in place.
 * <p>
 * The service reads the entity's {@link ManagedType managedType} from the metamodel, and for each {@code BASIC}
 * attribute annotated with {@link __EncryptedAttribute @__EncryptedAttribute}:
 * <ol>
 *   <li>converts the plaintext value to bytes, by its java type;</li>
 *   <li>hands those bytes to the {@link __EncryptionManager encryptionManager}, along with the
 *       {@link __EncryptionManager#getEncryptionIdentifier(Object) encryption identifier} of the instance;</li>
 *   <li>stores the ciphertext in the paired {@code byte[]} attribute, and clears the plaintext one.</li>
 * </ol>
 * {@link #decrypt(Object)} runs the same steps in reverse. {@code EMBEDDED} attributes are descended into, so that
 * attributes of an embeddable are covered as well.
 * <p>
 * The java types handled are those Jakarta Persistence calls basic types: the primitives and their wrappers,
 * {@link String}, {@link BigInteger}, {@link BigDecimal}, the {@code java.time} types, {@link java.util.Date},
 * {@link Calendar}, {@link UUID}, {@code byte[]}, {@code char[]} and their boxed forms, enums, and anything
 * {@link Serializable}. Any other type is rejected with a {@link RuntimeException}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __EncryptionManager
 * @see __EncryptionListener
 * @see <a href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#a486">2.6. Basic
 *         Types</a> (Jakarta Persistence 3.2 Specification Document)
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __EncryptionService {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns an unmodifiable map of attribute names and attributes of the specified managed type, caching it against
     * the {@code managedType}.
     *
     * @param managedType the managed type whose attributes are returned.
     * @return an unmodifiable map of attribute names and attributes of the {@code managedType}.
     * @apiNote this method is thread-safe.
     * @implNote The cache belongs to this instance. A cache shared across the JVM would hand one persistence
     *         unit's metadata to another which happens to manage a class of the same name.
     */
    protected Map<String, Attribute<?, ?>> getAttributes(final ManagedType<?> managedType) {
        Objects.requireNonNull(managedType, "managedType is null");
        return managedTypesAndAttributes.computeIfAbsent(
                managedType,
                k -> k.getAttributes().stream()
                        .collect(Collectors.toUnmodifiableMap(Attribute::getName, Function.identity()))
        );
    }

    // -------------------------------------------------------------------------------------------------------- BUILDERS

    // ------------------------------------------------------------------------------------------ STATIC_FACTORY_METHODS

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     *
     * @param entityManagerFactory an entity manager factory; must not be {@code null}.
     * @param encryptionManager    the encryption manager; must not be {@code null}.
     */
    protected __EncryptionService(final EntityManagerFactory entityManagerFactory,
                                  final __EncryptionManager encryptionManager) {
        super();
        this.entityManagerFactory = Objects.requireNonNull(entityManagerFactory, "entityManagerFactory is null");
        this.encryptionManager = Objects.requireNonNull(encryptionManager, "encryptionManager is null");
    }

    // -----------------------------------------------------------------------------------------------------------------

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Called after this instance has been constructed and its injection points have been satisfied.
     *
     * @implSpec The implementation of this class only logs.
     */
    @PostConstruct
    protected void onPostConstruct() {
        logger.log(System.Logger.Level.DEBUG, "onPostConstruct()");
        logger.log(System.Logger.Level.DEBUG, "encryptionManager: {0}", encryptionManager.getClass().getName());
    }

    // https://stackoverflow.com/a/72628439/330457

    /**
     * Observes the CDI container {@link Startup} event.
     *
     * @param startup the observed event.
     * @implSpec The implementation of this class only logs.
     */
    protected void onStartup(@Observes final Startup startup) {
        logger.log(System.Logger.Level.DEBUG, "onStartup({0})", startup);
    }

    /**
     * Called before this instance is destroyed.
     *
     * @implSpec The implementation of this class only logs.
     */
    @PreDestroy
    protected void onPreDestroy() {
        logger.log(System.Logger.Level.DEBUG, "onPreDestroy()");
    }

    // https://stackoverflow.com/a/72628439/330457

    /**
     * Observes the CDI container {@link Shutdown} event.
     *
     * @param shutdown the observed event.
     * @implSpec The implementation of this class only logs.
     */
    protected void onShutdown(@Observes final Shutdown shutdown) {
        logger.log(System.Logger.Level.DEBUG, "onShutdown({0})", shutdown);
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
                                    final @Nullable Attribute<?, ?> encryptedAttribute, final String reason) {
        final var path = new StringBuilder(rootType.getJavaType().getName());
        for (final var embedding : embeddingPath) {
            path.append('.').append(embedding.getName());
        }
        return new RuntimeException(
                reason +
                "; decrypted attribute: " + decryptedAttribute.getName() +
                (encryptedAttribute == null ? "" : "; encrypted attribute: " + encryptedAttribute.getName()) +
                "; managed type: " + decryptedAttribute.getDeclaringType().getJavaType().getName() +
                "; path: " + path
        );
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
     * Validates every {@link __EncryptedAttribute annotated attribute} of the specified managed type, and returns the
     * pairs, and the embedded attributes, to walk.
     *
     * @param managedType the managed type to validate.
     * @return the validated mapping of the {@code managedType}.
     * @throws RuntimeException when any annotated attribute of the {@code managedType} is mapped inconsistently.
     * @implNote The validation runs once per {@code (rootType, embeddingPath)}, and descends through the whole
     * reachable embeddable graph to completion before any instance is touched, so that an inconsistent mapping
     * cannot leave an instance half-encrypted.
     */
    /**
     * Whether a mapping fact holds, does not hold, or could not be established.
     */
    protected enum MappingFlag {

        /**
         * The fact holds.
         */
        YES,

        /**
         * The fact does not hold.
         */
        NO,

        /**
         * The fact could not be established from the metadata available.
         */
        UNKNOWN
    }

    /**
     * The column rules of one attribute.
     *
     * @param insertable whether the provider may write the column in an {@code INSERT}.
     * @param updatable  whether the provider may write the column in an {@code UPDATE}.
     * @param nullable   whether the column accepts {@code null}.
     * @param source     where the facts came from, for diagnostics; never a value.
     */
    protected record ColumnRules(MappingFlag insertable, MappingFlag updatable, MappingFlag nullable, String source) {

        /**
         * Creates a new instance.
         *
         * @param insertable whether the provider may write the column in an {@code INSERT}.
         * @param updatable  whether the provider may write the column in an {@code UPDATE}.
         * @param nullable   whether the column accepts {@code null}.
         * @param source     where the facts came from, for diagnostics; never a value.
         * @implNote The canonical constructor is {@code public} on purpose. A subclass in another package
         *         inherits visibility of this nested type, but not access to a {@code protected} constructor of it, so
         *         the hook would otherwise be impossible to implement outside this package — which is the whole point
         *         of it.
         */
        public ColumnRules {
            Objects.requireNonNull(insertable, "insertable is null");
            Objects.requireNonNull(updatable, "updatable is null");
            Objects.requireNonNull(nullable, "nullable is null");
            Objects.requireNonNull(source, "source is null");
        }
    }

    /**
     * Returns the column rules of the specified attribute.
     *
     * @param rootType      the managed type the walk started from.
     * @param embeddingPath the embedding attributes from the {@code rootType} down to the {@code attribute}'s owner;
     *                      empty when the {@code attribute} belongs to the {@code rootType} itself.
     * @param attribute     the attribute whose rules are returned.
     * @return the rules of the {@code attribute}.
     * @implSpec The implementation of this class describes the <em>annotation</em> mapping: the applicable
     *         {@link AttributeOverride @AttributeOverride}, if any, and otherwise the attribute's own
     *         {@link Column @Column}, honouring the annotation defaults when neither is present. It never returns
     *         {@link MappingFlag#UNKNOWN UNKNOWN}.
     * @implNote The result is cached against the {@code (rootType, embeddingPath)} it was resolved for, so an
     *         implementation has to be thread-safe, and has to return the same facts for the lifetime of this service:
     *         two threads may both miss the cache and call this method, and a later change of mind is never
     *         reconsidered. An implementation is trusted — returning {@link MappingFlag#NO NO} for a column which is in
     *         fact insertable authorizes a mapping which writes the plaintext.
     *         <p>
     *         Jakarta Persistence exposes no portable accessor for effective per-column metadata, so an application
     *         which maps in {@code orm.xml} has to override this method and state the facts its configuration
     *         establishes, delegating everything else to {@code super}. A subclass which cannot establish a fact
     *         returns {@link MappingFlag#UNKNOWN UNKNOWN}, and the mapping is rejected rather than assumed safe.
     */
    protected ColumnRules resolveColumnRules(final ManagedType<?> rootType,
                                             final List<Attribute<?, ?>> embeddingPath,
                                             final Attribute<?, ?> attribute) {
        Objects.requireNonNull(rootType, "rootType is null");
        Objects.requireNonNull(embeddingPath, "embeddingPath is null");
        Objects.requireNonNull(attribute, "attribute is null");
        final var override = findAttributeOverride(rootType, embeddingPath, attribute);
        if (override != null) {
            // an override supplies its own @Column, with its own defaults; it replaces the member's, never merges
            return rulesOf(override.column(), "@AttributeOverride(\"" + override.name() + "\")");
        }
        final var column = JinahyaAttributeUtils.getJavaMemberAnnotation(attribute, Column.class);
        return column == null
                ? new ColumnRules(MappingFlag.YES, MappingFlag.YES, MappingFlag.YES, "@Column defaults")
                : rulesOf(column, "@Column");
    }

    private static ColumnRules rulesOf(final Column column, final String source) {
        return new ColumnRules(
                column.insertable() ? MappingFlag.YES : MappingFlag.NO,
                column.updatable() ? MappingFlag.YES : MappingFlag.NO,
                column.nullable() ? MappingFlag.YES : MappingFlag.NO,
                source
        );
    }

    private static AttributeOverride[] overridesOn(final AnnotatedElement element) {
        return element.getAnnotationsByType(AttributeOverride.class);
    }

    /**
     * Returns the {@link AttributeOverride @AttributeOverride} which applies to the specified attribute, the outermost
     * one winning.
     */
    private static @Nullable AttributeOverride findAttributeOverride(final ManagedType<?> rootType,
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

    private Mapping validate(final ManagedType<?> rootType, final List<Attribute<?, ?>> embeddingPath,
                             final ManagedType<?> managedType, final Set<ManagedType<?>> visiting) {
        if (!visiting.add(managedType)) {
            throw new RuntimeException("embeddable cycle through " + managedType.getJavaType().getName());
        }
        final var attributes = getAttributes(managedType);
        final var pairs = new ArrayList<Pair>();
        final var embeddeds = new ArrayList<Embedded>();
        final var paired = new HashSet<Attribute<?, ?>>();
        for (final var decryptedAttribute : attributes.values()) {
            final var persistentAttributeType = decryptedAttribute.getPersistentAttributeType();
            final var annotation =
                    JinahyaAttributeUtils.getJavaMemberAnnotation(decryptedAttribute, __EncryptedAttribute.class);
            if (persistentAttributeType == Attribute.PersistentAttributeType.EMBEDDED) {
                if (annotation != null) {
                    throw reject(rootType, embeddingPath, decryptedAttribute, null,
                                 "an @Embedded attribute cannot itself be encrypted"
                                 + "; annotate the attributes inside the embeddable");
                }
                // descend now, so that an invalid embeddable cannot surface only once a sibling has been transformed,
                // and keep the child mapping: it was resolved with this path's overrides in scope
                final var childPath = new ArrayList<>(embeddingPath);
                childPath.add(decryptedAttribute);
                final var childMapping = resolve(
                        rootType,
                        List.copyOf(childPath),
                        entityManagerFactory.getMetamodel().managedType(decryptedAttribute.getJavaType()),
                        visiting
                );
                embeddeds.add(new Embedded(decryptedAttribute, childMapping));
                continue;
            }
            if (annotation == null) {
                continue;
            }
            // an annotated attribute which cannot be handled is an error, never something to skip past
            if (persistentAttributeType != Attribute.PersistentAttributeType.BASIC) {
                throw reject(rootType, embeddingPath, decryptedAttribute, null,
                             "only a BASIC attribute can be encrypted; found " + persistentAttributeType);
            }
            if (decryptedAttribute.getJavaType().isPrimitive()) {
                throw reject(rootType, embeddingPath, decryptedAttribute, null,
                             "a decrypted attribute cannot be of a primitive type; encrypting nulls it");
            }
            if (isIdOrVersion(decryptedAttribute)) {
                throw reject(rootType, embeddingPath, decryptedAttribute, null,
                             "an identifier, or a version, attribute cannot be encrypted");
            }
            if (!isOptional(decryptedAttribute)) {
                throw reject(rootType, embeddingPath, decryptedAttribute, null,
                             "a decrypted attribute has to be optional");
            }
            // the plaintext column must not be writable: @PrePersist runs at persist(), not when the INSERT is built,
            // so a provider which builds one statement at commit would otherwise carry a late assignment in the clear
            final var rules = resolveColumnRules(rootType, embeddingPath, decryptedAttribute);
            if (rules.insertable() != MappingFlag.NO) {
                throw reject(rootType, embeddingPath, decryptedAttribute, null,
                             (rules.insertable() == MappingFlag.UNKNOWN
                                     ? "cannot establish that a decrypted attribute's column is not insertable"
                                     : "a decrypted attribute's column has to be non-insertable")
                             + "; otherwise an INSERT can carry the plaintext (from " + rules.source() + ")");
            }
            if (rules.nullable() != MappingFlag.YES) {
                throw reject(rootType, embeddingPath, decryptedAttribute, null,
                             (rules.nullable() == MappingFlag.UNKNOWN
                                     ? "cannot establish that a decrypted attribute's column is nullable"
                                     : "a decrypted attribute's column has to be nullable")
                             + "; encrypting nulls it (from " + rules.source() + ")");
            }
            final var name = annotation.encryptedAttribute().isBlank()
                    ? __EncryptedAttributeUtils.getDefaultEncryptedAttributeName(decryptedAttribute)
                    : annotation.encryptedAttribute();
            final var encryptedAttribute = attributes.get(name);
            if (encryptedAttribute == null) {
                throw reject(rootType, embeddingPath, decryptedAttribute, null,
                             "no encrypted attribute named '" + name + "'");
            }
            if (encryptedAttribute == decryptedAttribute) {
                throw reject(rootType, embeddingPath, decryptedAttribute, encryptedAttribute,
                             "an encrypted attribute cannot be the decrypted attribute itself");
            }
            if (encryptedAttribute.getPersistentAttributeType() != Attribute.PersistentAttributeType.BASIC) {
                throw reject(rootType, embeddingPath, decryptedAttribute, encryptedAttribute,
                             "an encrypted attribute has to be BASIC");
            }
            if (encryptedAttribute.getJavaType() != byte[].class) {
                throw reject(rootType, embeddingPath, decryptedAttribute, encryptedAttribute,
                             "an encrypted attribute has to be typed byte[]");
            }
            if (isIdOrVersion(encryptedAttribute)) {
                throw reject(rootType, embeddingPath, decryptedAttribute, encryptedAttribute,
                             "an identifier, or a version, attribute cannot hold the ciphertext");
            }
            if (!isOptional(encryptedAttribute)) {
                throw reject(rootType, embeddingPath, decryptedAttribute, encryptedAttribute,
                             "an encrypted attribute has to be optional");
            }
            if (JinahyaAttributeUtils.getJavaMemberAnnotation(encryptedAttribute, __EncryptedAttribute.class) != null) {
                throw reject(rootType, embeddingPath, decryptedAttribute, encryptedAttribute,
                             "an encrypted attribute cannot itself be annotated with @__EncryptedAttribute");
            }
            final var encryptedRules = resolveColumnRules(rootType, embeddingPath, encryptedAttribute);
            if (encryptedRules.insertable() != MappingFlag.YES || encryptedRules.updatable() != MappingFlag.YES) {
                final var unknown = encryptedRules.insertable() == MappingFlag.UNKNOWN
                                    || encryptedRules.updatable() == MappingFlag.UNKNOWN;
                throw reject(rootType, embeddingPath, decryptedAttribute, encryptedAttribute,
                             (unknown
                                     ? "cannot establish that an encrypted attribute's column is insertable and updatable"
                                     : "an encrypted attribute's column has to be insertable and updatable")
                             + "; otherwise the ciphertext cannot be stored (from " + encryptedRules.source() + ")");
            }
            if (encryptedRules.nullable() != MappingFlag.YES) {
                throw reject(rootType, embeddingPath, decryptedAttribute, encryptedAttribute,
                             (encryptedRules.nullable() == MappingFlag.UNKNOWN
                                     ? "cannot establish that an encrypted attribute's column is nullable"
                                     : "an encrypted attribute's column has to be nullable")
                             + "; decrypting nulls it (from " + encryptedRules.source() + ")");
            }
            if (!paired.add(encryptedAttribute)) {
                throw reject(rootType, embeddingPath, decryptedAttribute, encryptedAttribute,
                             "an encrypted attribute is paired more than once");
            }
            pairs.add(new Pair(decryptedAttribute, encryptedAttribute));
        }
        visiting.remove(managedType);
        return new Mapping(List.copyOf(pairs), List.copyOf(embeddeds));
    }

    /**
     * Returns the validated mapping of the specified managed type, caching it against the {@code managedType}.
     *
     * @param managedType the managed type whose mapping is returned.
     * @return the validated mapping of the {@code managedType}.
     */
    private Mapping getMapping(final ManagedType<?> managedType) {
        return resolve(managedType, List.of(), managedType, new HashSet<>());
    }

    /**
     * Returns the validated mapping of the specified managed type, validating it, and everything its embedded
     * attributes reach, when it is not already cached.
     *
     * @param managedType the managed type whose mapping is returned.
     * @param visiting    the managed types on the current path, for detecting a cycle.
     * @return the validated mapping of the {@code managedType}.
     * @implNote The cache is not populated with {@code computeIfAbsent}: validation recurses, and a nested
     *         {@code computeIfAbsent} on the same map is not permitted.
     */
    private Mapping resolve(final ManagedType<?> rootType, final List<Attribute<?, ?>> embeddingPath,
                            final ManagedType<?> managedType, final Set<ManagedType<?>> visiting) {
        final var key = new MappingKey(rootType, embeddingPath);
        final var cached = mappings.get(key);
        if (cached != null) {
            return cached;
        }
        final var validated = validate(rootType, embeddingPath, managedType, visiting);
        final var previous = mappings.putIfAbsent(key, validated);
        return previous != null ? previous : validated;
    }

    /**
     * The key a validated mapping is cached under. The same embeddable reached through two different paths can carry
     * two different sets of overrides, so the managed type alone is not enough.
     *
     * @param rootType      the managed type the walk started from.
     * @param embeddingPath the embedding attributes from the {@code rootType}.
     */
    private record MappingKey(ManagedType<?> rootType, List<Attribute<?, ?>> embeddingPath) {

    }

    /**
     * A validated pair of a decrypted attribute and the attribute which holds its ciphertext.
     *
     * @param decrypted the attribute holding the plaintext.
     * @param encrypted the attribute holding the ciphertext.
     */
    private record Pair(Attribute<?, ?> decrypted, Attribute<?, ?> encrypted) {

    }

    /**
     * The validated mapping of a managed type.
     *
     * @param pairs     the encrypted pairs to transform.
     * @param embeddeds the embedded attributes to descend into.
     */
    private record Mapping(List<Pair> pairs, List<Embedded> embeddeds) {

    }

    /**
     * An embedded attribute, with the mapping validated for the path it was reached by.
     *
     * @param attribute the embedded attribute.
     * @param mapping   the validated mapping of the embeddable, in this attribute's override scope.
     */
    private record Embedded(Attribute<?, ?> attribute, Mapping mapping) {

    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Encrypts the annotated attributes of the specified object with the specified encryption identifier.
     *
     * @param encryptionIdentifier the identifier the encryption keys are selected by.
     * @param object               the entity, or embeddable, instance to encrypt, in place.
     * @throws RuntimeException when an attribute pair is inconsistent, or when an attribute has a java type which
     *                          cannot be turned into bytes.
     * @see #encrypt(Object)
     */
    protected void encrypt(final @NotBlank String encryptionIdentifier, final @Valid @NotNull Object object) {
        Objects.requireNonNull(object, "object is null");
        // validates the whole reachable graph before anything is touched
        encrypt(encryptionIdentifier, object, getMapping(getManagedType(resolveClass(object))));
    }

    /**
     * Encrypts the specified object against the mapping already validated for it.
     *
     * @param encryptionIdentifier the identifier the encryption keys are selected by.
     * @param object               the entity, or embeddable, instance to encrypt, in place.
     * @param mapping              the validated mapping of the {@code object}.
     * @implNote The mapping is passed down rather than looked up again: an embeddable reached through an
     *         {@link AttributeOverride @AttributeOverride} would otherwise be re-resolved without that override in
     *         scope.
     */
    private void encrypt(final String encryptionIdentifier, final Object object, final Mapping mapping) {
        for (final var embedded : mapping.embeddeds()) {
            final var embeddedValue = JinahyaAttributeUtils.getAttributeValue(object, embedded.attribute());
            if (embeddedValue != null) {
                encrypt(encryptionIdentifier, embeddedValue, embedded.mapping());
            }
        }
        for (final var pair : mapping.pairs()) {
            final var decryptedAttribute = pair.decrypted();
            final var encryptedAttribute = pair.encrypted();
            final var decryptedValue = JinahyaAttributeUtils.getAttributeValue(object, decryptedAttribute);
            if (decryptedValue == null) {
                final var encryptedValue = JinahyaAttributeUtils.getAttributeValue(object, encryptedAttribute);
                if (encryptedValue != null) {
                    // already encrypted
                    continue;
                }
                JinahyaAttributeUtils.setAttributeValue(object, encryptedAttribute, null);
                continue;
            }
            final byte[] decryptedBytes;
            final var javaType = decryptedAttribute.getJavaType();
            // The DECLARED type decides the encoding, exactly as it decides the decoding below. Dispatching on
            // the runtime class here instead let the two ladders pick different codecs for one attribute -- a
            // java.util.Date holding a java.sql.Timestamp was written as epoch seconds and read back as epoch
            // millis, silently. Keep this chain in the same order as the one in decrypt(...).
            if (!javaType.isInstance(decryptedValue)) {
                throw new RuntimeException(
                        "the value is not an instance of the attribute's declared java type" +
                        "; decrypted attribute: " + decryptedAttribute.getName() +
                        "; java type: " + javaType.getName() +
                        "; value type: " + decryptedValue.getClass().getName()
                );
            }
            if (javaType == Boolean.class) {
                decryptedBytes = boolean_1((Boolean) decryptedValue);
            } else if (javaType == Byte.class) {
                decryptedBytes = byte_1((Byte) decryptedValue);
            } else if (javaType == Short.class) {
                decryptedBytes = short_2((Short) decryptedValue);
            } else if (javaType == Integer.class) {
                decryptedBytes = int_4((Integer) decryptedValue);
            } else if (javaType == Long.class) {
                decryptedBytes = long_8((Long) decryptedValue);
            } else if (javaType == Character.class) {
                decryptedBytes = char_2((Character) decryptedValue);
            } else if (javaType == Float.class) {
                decryptedBytes = float_4((Float) decryptedValue);
            } else if (javaType == Double.class) {
                decryptedBytes = double_8((Double) decryptedValue);
            } else if (javaType == String.class) {
                decryptedBytes = string_((String) decryptedValue);
            } else if (javaType == UUID.class) {
                decryptedBytes = uuid_16((UUID) decryptedValue);
            } else if (javaType == BigInteger.class) {
                decryptedBytes = big_integer_((BigInteger) decryptedValue);
            } else if (javaType == BigDecimal.class) {
                decryptedBytes = big_decimal_((BigDecimal) decryptedValue);
            } else if (javaType == LocalDate.class) {
                decryptedBytes = local_date_8((LocalDate) decryptedValue);
            } else if (javaType == LocalTime.class) {
                decryptedBytes = local_time_8((LocalTime) decryptedValue);
            } else if (javaType == LocalDateTime.class) {
                decryptedBytes = local_date_time_16((LocalDateTime) decryptedValue);
            } else if (javaType == OffsetTime.class) {
                decryptedBytes = offset_time_12((OffsetTime) decryptedValue);
            } else if (javaType == OffsetDateTime.class) {
                decryptedBytes = offset_date_time_20((OffsetDateTime) decryptedValue);
            } else if (javaType == Instant.class) {
                decryptedBytes = instant_12((Instant) decryptedValue);
            } else if (javaType == Year.class) {
                decryptedBytes = year_4((Year) decryptedValue);
            } else if (javaType == java.sql.Timestamp.class) { // before java.util.Date; keeps the nanos
                decryptedBytes = sql_timestamp_16((java.sql.Timestamp) decryptedValue);
            } else if (javaType == java.sql.Date.class) {      // before java.util.Date
                decryptedBytes = sql_date_8((java.sql.Date) decryptedValue);
            } else if (javaType == java.sql.Time.class) {      // before java.util.Date
                decryptedBytes = sql_time_8((java.sql.Time) decryptedValue);
            } else if (Calendar.class.isAssignableFrom(javaType)) {
                decryptedBytes = util_calendar_8((Calendar) decryptedValue);
            } else if (java.util.Date.class.isAssignableFrom(javaType)) {
                // one branch feeds both of decrypt's generic Date branches -- the exact `== java.util.Date` one
                // and the reflective (long) constructor one -- because both read long_8 millis
                decryptedBytes = util_date_8((java.util.Date) decryptedValue);
            } else if (javaType == byte[].class) {
                decryptedBytes =
                        ((byte[]) decryptedValue).clone(); // the manager must not be handed the instance's own array
            } else if (javaType == Byte[].class) {
                logger.log(System.Logger.Level.WARNING, "Byte[] is not encouraged; use byte[]");
                decryptedBytes = Bytes_l((Byte[]) decryptedValue);
            } else if (javaType == char[].class) {
                decryptedBytes = chars_2l((char[]) decryptedValue);
            } else if (javaType == Character[].class) {
                logger.log(System.Logger.Level.WARNING, "Character[] is not encouraged; use char[]");
                decryptedBytes = Characters_2l((Character[]) decryptedValue);
            } else if (javaType.isEnum()) {
                decryptedBytes = enum_((Enum<?>) decryptedValue);
            } else if (Serializable.class.isAssignableFrom(javaType)) {
                decryptedBytes = serializable_((Serializable) decryptedValue);
            } else {
                throw new RuntimeException("unsupported java type: " + javaType);
            }
            final var encrypted = encryptionManager.encrypt(encryptionIdentifier, decryptedBytes);
            if (encrypted == null) {
                throw new RuntimeException(
                        "encryptionManager returned null; decrypted attribute: " + decryptedAttribute.getName());
            }
            JinahyaAttributeUtils.setAttributeValue(object, encryptedAttribute, encrypted);
            JinahyaAttributeUtils.setAttributeValue(object, decryptedAttribute, null);
        }
    }

    /**
     * Encrypts the annotated attributes of the specified object, with the identifier the
     * {@link __EncryptionManager encryptionManager} derives from it.
     *
     * @param object the entity instance to encrypt, in place.
     * @throws RuntimeException when an attribute pair is inconsistent, or when an attribute has a java type which
     *                          cannot be turned into bytes.
     * @see __EncryptionManager#getEncryptionIdentifier(Object)
     */
    public void encrypt(final @Valid @NotNull Object object) {
        Objects.requireNonNull(object, "object is null");
        final var encryptionIdentifier = encryptionManager.getEncryptionIdentifier(object);
        encrypt(encryptionIdentifier, object);
    }

    /**
     * Decrypts the annotated attributes of the specified object with the specified encryption identifier.
     *
     * @param encryptionIdentifier the identifier the encryption keys are selected by.
     * @param object               the entity, or embeddable, instance to decrypt, in place.
     * @throws RuntimeException when an attribute pair is inconsistent, or when an attribute has a java type which
     *                          cannot be reconstructed from bytes.
     * @see #decrypt(Object)
     */
    protected void decrypt(final @NotBlank String encryptionIdentifier, final @Valid @NotNull Object object) {
        Objects.requireNonNull(object, "object is null");
        // validates the whole reachable graph before anything is touched
        decrypt(encryptionIdentifier, object, getMapping(getManagedType(resolveClass(object))));
    }

    /**
     * Decrypts the specified object against the mapping already validated for it.
     *
     * @param encryptionIdentifier the identifier the encryption keys are selected by.
     * @param object               the entity, or embeddable, instance to decrypt, in place.
     * @param mapping              the validated mapping of the {@code object}.
     * @implNote The mapping is passed down rather than looked up again: an embeddable reached through an
     *         {@link AttributeOverride @AttributeOverride} would otherwise be re-resolved without that override in
     *         scope.
     */
    private void decrypt(final String encryptionIdentifier, final Object object, final Mapping mapping) {
        for (final var embedded : mapping.embeddeds()) {
            final var embeddedValue = JinahyaAttributeUtils.getAttributeValue(object, embedded.attribute());
            if (embeddedValue != null) {
                decrypt(encryptionIdentifier, embeddedValue, embedded.mapping());
            }
        }
        for (final var pair : mapping.pairs()) {
            final var decryptedAttribute = pair.decrypted();
            final var encryptedAttribute = pair.encrypted();
            final var encryptedBytes = (byte[]) JinahyaAttributeUtils.getAttributeValue(object, encryptedAttribute);
            if (encryptedBytes == null) {
                final var decryptedValue = JinahyaAttributeUtils.getAttributeValue(object, decryptedAttribute);
                if (decryptedValue != null) {
                    // the encrypted column may be defined later
                    continue;
                }
                JinahyaAttributeUtils.setAttributeValue(object, decryptedAttribute, null);
                continue;
            }
            final var decryptedBytes = encryptionManager.decrypt(encryptionIdentifier, encryptedBytes.clone());
            if (decryptedBytes == null) {
                throw new RuntimeException(
                        "encryptionManager returned null; decrypted attribute: " + decryptedAttribute.getName());
            }
            final Object decryptedValue;
            final var javaType = decryptedAttribute.getJavaType();
            try {
                if (javaType == boolean.class || javaType == Boolean.class) {
                    decryptedValue = boolean_1(decryptedBytes);
                } else if (javaType == byte.class || javaType == Byte.class) {
                    decryptedValue = byte_1(decryptedBytes);
                } else if (javaType == short.class || javaType == Short.class) {
                    decryptedValue = short_2(decryptedBytes);
                } else if (javaType == int.class || javaType == Integer.class) {
                    decryptedValue = int_4(decryptedBytes);
                } else if (javaType == long.class || javaType == Long.class) {
                    decryptedValue = long_8(decryptedBytes);
                } else if (javaType == char.class || javaType == Character.class) {
                    decryptedValue = char_2(decryptedBytes);
                } else if (javaType == float.class || javaType == Float.class) {
                    decryptedValue = float_4(decryptedBytes);
                } else if (javaType == double.class || javaType == Double.class) {
                    decryptedValue = double_8(decryptedBytes);
                } else if (javaType == String.class) {
                    decryptedValue = string_(decryptedBytes);
                } else if (javaType == UUID.class) {
                    decryptedValue = uuid_16(decryptedBytes);
                } else if (javaType == BigInteger.class) {
                    decryptedValue = big_integer_(decryptedBytes);
                } else if (javaType == BigDecimal.class) {
                    decryptedValue = big_decimal_(decryptedBytes);
                } else if (javaType == LocalDate.class) {
                    decryptedValue = local_date_8(decryptedBytes);
                } else if (javaType == LocalTime.class) {
                    decryptedValue = local_time_8(decryptedBytes);
                } else if (javaType == LocalDateTime.class) {
                    decryptedValue = local_date_time_16(decryptedBytes);
                } else if (javaType == OffsetTime.class) {
                    decryptedValue = offset_time_12(decryptedBytes);
                } else if (javaType == OffsetDateTime.class) {
                    decryptedValue = offset_date_time_20(decryptedBytes);
                } else if (javaType == Instant.class) {
                    decryptedValue = instant_12(decryptedBytes);
                } else if (javaType == Year.class) {
                    decryptedValue = year_4(decryptedBytes);
                } else if (javaType == java.sql.Timestamp.class) { // before java.util.Date; keeps the nanos
                    decryptedValue = sql_timestamp_16(decryptedBytes);
                } else if (javaType == java.sql.Date.class) {      // before java.util.Date
                    decryptedValue = sql_date_8(decryptedBytes);
                } else if (javaType == java.sql.Time.class) {      // before java.util.Date
                    decryptedValue = sql_time_8(decryptedBytes);
                } else if (Calendar.class.isAssignableFrom(javaType)) {
                    decryptedValue = util_calendar_8(decryptedBytes);
                } else if (javaType == java.util.Date.class) {
                    decryptedValue = util_date_8(decryptedBytes);
                } else if (java.util.Date.class.isAssignableFrom(javaType)) {
                    final var time = long_8(decryptedBytes);
                    try {
                        decryptedValue = javaType.getConstructor(long.class).newInstance(time);
                    } catch (final ReflectiveOperationException roe) {
                        throw new RuntimeException("failed to construct " + javaType + " with " + time, roe);
                    }
                } else if (javaType == byte[].class) {
                    decryptedValue = decryptedBytes;
                } else if (javaType == Byte[].class) {
                    logger.log(System.Logger.Level.WARNING, "Byte[] is not encouraged; use byte[]");
                    decryptedValue = Bytes_l(decryptedBytes);
                } else if (javaType == char[].class) {
                    decryptedValue = chars_2l(decryptedBytes);
                } else if (javaType == Character[].class) {
                    logger.log(System.Logger.Level.WARNING, "Character[] is not encouraged; use char[]");
                    decryptedValue = Characters_2l(decryptedBytes);
                } else if (javaType.isEnum()) {
                    decryptedValue = enum_(decryptedBytes, (Class) javaType);
                } else if (Serializable.class.isAssignableFrom(javaType)) {
                    decryptedValue = javaType.cast(serializable_(decryptedBytes, javaType));
                } else {
                    throw new RuntimeException("unsupported java type: " + javaType);
                }
            } catch (final IndexOutOfBoundsException | IllegalArgumentException | AssertionError e) {
                // Everything the decode ladder can raise for bytes it cannot turn back into a value:
                //   IndexOutOfBounds     a payload shorter than the fixed-width codec expects
                //   AssertionError       the same, when assertions are enabled -- __EncryptionServiceUtils
                //                        validates its lengths with assert, so a short payload reads the same
                //                        way whether or not -ea is on
                //   IllegalArgumentException  chars_2l on an odd-length payload, and Enum.valueOf on a name
                //                        which is not a constant of the attribute's enum
                // Without the last one, those two surfaced bare, with no clue which attribute they came from.
                throw new RuntimeException(
                        "cannot reconstruct the value from the decrypted bytes" +
                        "; decrypted attribute: " + decryptedAttribute.getName() +
                        "; java type: " + javaType.getName() +
                        "; decrypted bytes: " + decryptedBytes.length,
                        e
                );
            }
            JinahyaAttributeUtils.setAttributeValue(object, decryptedAttribute, decryptedValue);
            JinahyaAttributeUtils.setAttributeValue(object, encryptedAttribute, null);
        }
    }

    /**
     * Decrypts the annotated attributes of the specified object, with the identifier the
     * {@link __EncryptionManager encryptionManager} derives from it.
     *
     * @param object the entity instance to decrypt, in place.
     * @throws RuntimeException when an attribute pair is inconsistent, or when an attribute has a java type which
     *                          cannot be reconstructed from bytes.
     * @see __EncryptionManager#getEncryptionIdentifier(Object)
     */
    public void decrypt(final @Valid @NotNull Object object) {
        Objects.requireNonNull(object, "object is null");
        final var encryptionIdentifier = encryptionManager.getEncryptionIdentifier(object);
        decrypt(encryptionIdentifier, object);
    }

    // ----------------------------------------------------------------------------------------------------- entityTypes

    // ---------------------------------------------------------------------------------------------------- managedTypes
    /**
     * Returns the {@link ManagedType} of the specified class, from this service's entity manager factory, caching it
     * against the {@code entityClass}.
     *
     * @param entityClass the class whose managed type is returned.
     * @return the managed type of the {@code entityClass}.
     * @throws IllegalArgumentException when the entity manager factory does not manage the {@code entityClass}.
     */
    /**
     * Returns the class the metamodel knows the specified object by.
     *
     * @param object the entity, or embeddable, instance.
     * @return the class to look the {@code object} up by.
     * @implNote {@link Object#getClass() getClass()} is not it: a lazily-loaded reference is an instance of a
     *         provider-generated subclass, which the metamodel has never heard of, so asking it directly failed with
     *         {@code Not a managed type: ..._SecretEntity$HibernateProxy}. Jakarta Persistence 3.2 added
     *         {@link jakarta.persistence.PersistenceUnitUtil#getClass(Object)} for exactly this. It is defined for
     *         <em>entity</em> instances, so an embeddable &mdash; which a subclass may pass to the {@code protected}
     *         entry points &mdash; falls back to its own class.
     */
    private Class<?> resolveClass(final Object object) {
        try {
            return entityManagerFactory.getPersistenceUnitUtil().getClass(object);
        } catch (final RuntimeException re) {
            logger.log(System.Logger.Level.TRACE,
                       () -> "not an entity instance; using its own class: " + object.getClass().getName());
            return object.getClass();
        }
    }

    protected ManagedType<?> getManagedType(final Class<?> entityClass) {
        return managedTypes.computeIfAbsent(
                Objects.requireNonNull(entityClass, "entityClass is null"),
                k -> entityManagerFactory.getMetamodel().managedType(k)
        );
    }

    // -------------------------------------------------------------------------------------------- entityManagerFactory

    // ----------------------------------------------------------------------------------------------- encryptionManager

    // -----------------------------------------------------------------------------------------------------------------
    private final Map<Class<?>, ManagedType<?>> managedTypes = new ConcurrentHashMap<>();

    private final Map<ManagedType<?>, Map<String, Attribute<?, ?>>> managedTypesAndAttributes =
            new ConcurrentHashMap<>();

    private final Map<MappingKey, Mapping> mappings = new ConcurrentHashMap<>();

    // -----------------------------------------------------------------------------------------------------------------
    private final EntityManagerFactory entityManagerFactory;

    private final __EncryptionManager encryptionManager;
}
