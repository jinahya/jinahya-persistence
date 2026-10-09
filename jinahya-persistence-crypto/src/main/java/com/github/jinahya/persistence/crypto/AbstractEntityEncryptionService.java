package com.github.jinahya.persistence.crypto;

import com.github.jinahya.persistence.metamodel.util.JinahyaAttributeUtils;
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
import jakarta.persistence.metamodel.MapAttribute;
import jakarta.persistence.metamodel.PluralAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.validation.Constraint;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.Nullable;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.Bytes_l;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.Characters_2l;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.big_decimal_;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.big_integer_;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.boolean_1;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.byte_1;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.char_2;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.chars_2l;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.double_8;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.enum_;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.float_4;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.instant_12;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.int_4;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.local_date_8;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.local_date_time_16;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.local_time_8;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.long_8;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.offset_date_time_20;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.offset_time_12;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.serializable_;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.short_2;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.sql_date_8;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.sql_time_8;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.sql_timestamp_12;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.string_;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.util_calendar_8;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.util_date_8;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.uuid_16;
import static com.github.jinahya.persistence.crypto.EntityEncryptionServiceUtils.year_4;

/**
 * An abstract service which encrypts and decrypts the {@link EncryptedAttribute annotated attributes} of an entity
 * instance, in place.
 * <p>
 * The service reads the entity's {@link ManagedType managedType} from the metamodel, and for each plaintext annotated
 * with {@link EncryptedAttribute @EncryptedAttribute} — a {@code BASIC} attribute (Mode A), or a
 * {@link jakarta.persistence.Transient @Transient} field (Mode B):
 * <ol>
 *   <li>converts the plaintext value to bytes, by its declared java type, prefixed with a header naming the format
 *       version and the codec;</li>
 *   <li>hands those bytes to the {@link EntityEncryptionManager encryptionManager}, along with the
 *       {@link EntityEncryptionManager#getEncryptionIdentifier(Object) encryption identifier} of the instance;</li>
 *   <li>stores the ciphertext in the paired {@code byte[]} attribute; in Mode A, it also clears the plaintext, which
 *       is mapped. In Mode B nothing mapped is ever cleared, and an unchanged plaintext is not re-encrypted.</li>
 * </ol>
 * {@link #decrypt(Object)} runs the same steps in reverse. {@code EMBEDDED} attributes are descended into, so that
 * attributes of an embeddable are covered as well.
 * <p>
 * The mapping of a type is validated in full before any instance of it is touched, and every mapping of the
 * persistence unit at {@link #onStartup(Startup) startup}, or by {@link #validateMappings()}. Each encrypt and decrypt
 * is all-or-nothing per instance: every value is computed before any is assigned.
 * <p>
 * The java types handled are those Jakarta Persistence calls basic types: the primitives and their wrappers,
 * {@link String}, {@link BigInteger}, {@link BigDecimal}, the {@code java.time} types, {@link java.util.Date},
 * {@link Calendar}, {@link UUID}, {@code byte[]}, {@code char[]} and their boxed forms, enums, and anything
 * {@link Serializable}. A mapping of any other type is rejected when it is validated.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see EntityEncryptionManager
 * @see EntityEncryptionListener
 * @see <a href="https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#a486">2.6. Basic
 *         Types</a> (Jakarta Persistence 3.2 Specification Document)
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class AbstractEntityEncryptionService {

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
     * @param entityManagerFactory    an entity manager factory; must not be {@code null}.
     * @param entityEncryptionManager the encryption manager; must not be {@code null}.
     */
    protected AbstractEntityEncryptionService(final EntityManagerFactory entityManagerFactory,
                                              final EntityEncryptionManager entityEncryptionManager) {
        super();
        this.entityManagerFactory = Objects.requireNonNull(entityManagerFactory, "entityManagerFactory is null");
        this.entityEncryptionManager =
                Objects.requireNonNull(entityEncryptionManager, "entityEncryptionManager is null");
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
        logger.log(System.Logger.Level.DEBUG, "encryptionManager: {0}", entityEncryptionManager.getClass().getName());
    }

    // https://stackoverflow.com/a/72628439/330457

    /**
     * Observes the CDI container {@link Startup} event.
     *
     * @param startup the observed event.
     * @implSpec The implementation of this class {@link #validateMappings() validates every mapping}, so that an
     *         invalid one fails the deployment rather than its first use. A subclass which overrides this method, and
     *         does not call {@code super}, opts out of that.
     */
    protected void onStartup(@Observes final Startup startup) {
        logger.log(System.Logger.Level.DEBUG, "onStartup({0})", startup);
        validateMappings();
    }

    /**
     * Validates, up front, the mapping of every entity of this service's persistence unit which is annotated with
     * {@link EncryptedEntity @EncryptedEntity}, or has an {@link EncryptedAttribute encrypted attribute}.
     * <p>
     * Without it, a mapping is validated on its first {@link #encrypt(Object) encrypt} or {@link #decrypt(Object)
     * decrypt}, so an entity on a rare code path fails only when that path runs, in production. Valid mappings are
     * cached, exactly as on first use.
     *
     * @throws RuntimeException when any mapping is invalid; its message lists every invalid entity, and each failure
     *                          is attached as {@link Throwable#getSuppressed() suppressed}.
     */
    public void validateMappings() {
        final var failures = new ArrayList<RuntimeException>();
        for (final var entityType : entityManagerFactory.getMetamodel().getEntities()) {
            final var type = entityType.getJavaType();
            if (type == null) {
                continue; // a dynamic entity has no java class to read annotations from
            }
            try {
                checkEncryptedEntity(type, getMapping(getManagedType(type)));
            } catch (final RuntimeException re) {
                failures.add(re);
            }
        }
        if (failures.isEmpty()) {
            return;
        }
        final var message = new StringBuilder("invalid encryption mapping(s): ").append(failures.size());
        for (final var failure : failures) {
            message.append(System.lineSeparator()).append("  - ").append(failure.getMessage());
        }
        final var exception = new RuntimeException(message.toString());
        failures.forEach(exception::addSuppressed);
        throw exception;
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
        return reject(rootType, embeddingPath, decryptedAttribute.getName(),
                      decryptedAttribute.getDeclaringType().getJavaType(), encryptedAttribute, reason);
    }

    /**
     * Returns a {@link RuntimeException} naming the attributes and the reason.
     *
     * @param rootType           the managed type the walk started from.
     * @param embeddingPath      the embedding attributes from the {@code rootType}.
     * @param decryptedName      the name of the attribute, or of the transient field, holding the plaintext.
     * @param declaringType      the class declaring the plaintext.
     * @param encryptedAttribute the attribute holding the ciphertext; may be {@code null}.
     * @param reason             why the mapping was rejected.
     * @return the exception to throw.
     */
    private static RuntimeException reject(final ManagedType<?> rootType, final List<Attribute<?, ?>> embeddingPath,
                                           final String decryptedName, final Class<?> declaringType,
                                           final @Nullable Attribute<?, ?> encryptedAttribute, final String reason) {
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
     * @implNote The rest of the validation walks the metamodel, so an annotated member the metamodel does not know
     *         is never visited: a {@code @Transient} member, an unmapped one, or one on the side which the access type
     *         does not read (a field under property access). Such a member used to be silently ignored, and a
     *         transient one was not even stored. Bridge and synthetic methods are skipped; {@code javac} copies a
     *         method's annotations onto its bridge.
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
            if (member instanceof java.lang.reflect.Field field && isTransientField(field)) {
                continue; // a transient plaintext: Mode B, validated by validateTransientPlaintexts(...)
            }
            final var path = new StringBuilder(rootType.getJavaType().getName());
            for (final var embedding : embeddingPath) {
                path.append('.').append(embedding.getName());
            }
            throw new RuntimeException(
                    "an @EncryptedAttribute member is neither a persistent attribute (Mode A) nor a transient field"
                    + " (Mode B): unmapped, a transient getter, or on the side the access type does not read; it would"
                    + " never be encrypted"
                    + "; member: " + member.getDeclaringClass().getName() + '.' + member.getName()
                    + "; managed type: " + managedType.getJavaType().getName()
                    + "; path: " + path
            );
        }
    }

    /**
     * Returns the declared members of the specified class, and of its superclasses, which this module reads
     * annotations from: every field, and every method which is neither a bridge nor synthetic.
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
    private @Nullable Member findEncryptedMember(final ManagedType<?> managedType,
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
                        entityManagerFactory.getMetamodel().managedType(attribute.getJavaType()), visited);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    /**
     * Returns a Bean Validation constraint declared on the specified attribute: on its java member, or on the field or
     * getter of the same property, directly or inside a repeatable container such as {@code @Size.List}.
     *
     * @param attribute the attribute.
     * @return a constraint annotation; {@code null} when there is none.
     */
    private static @Nullable Annotation findConstraint(final Attribute<?, ?> attribute) {
        final var name = attribute.getName();
        final var capitalized = Character.toUpperCase(name.charAt(0)) + name.substring(1);
        for (final var member : declaredMembers(attribute.getDeclaringType().getJavaType())) {
            final var matches = member instanceof java.lang.reflect.Field
                                ? member.getName().equals(name)
                                : member.getName().equals("get" + capitalized) || member.getName().equals("is" + capitalized);
            if (!matches) {
                continue;
            }
            for (final var annotation : ((AnnotatedElement) member).getAnnotations()) {
                if (annotation.annotationType().isAnnotationPresent(Constraint.class)) {
                    return annotation;
                }
                // a repeatable container, e.g. @Size.List, holds its constraints in value()
                try {
                    final var value = annotation.annotationType().getMethod("value");
                    if (value.getReturnType().isArray()
                        && value.getReturnType().getComponentType().isAnnotationPresent(Constraint.class)) {
                        final var contained = (Annotation[]) value.invoke(annotation);
                        if (contained.length > 0) {
                            return contained[0];
                        }
                    }
                } catch (final NoSuchMethodException nsme) {
                    // not a container
                } catch (final ReflectiveOperationException roe) {
                    throw new RuntimeException("failed to read " + annotation, roe);
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
     * @implNote The declared member, not {@link Attribute#getJavaType()}, decides the codec: providers disagree on the
     *         latter. A member declared as a type variable of a generic superclass, though, is resolved here by
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

    /**
     * Validates every {@link EncryptedAttribute annotated attribute} of the specified managed type, and returns the
     * pairs, and the embedded attributes, to walk.
     *
     * @param rootType      the managed type the walk started from.
     * @param embeddingPath the embedding attributes from the {@code rootType} down to the {@code managedType}.
     * @param managedType   the managed type to validate.
     * @param visiting      the managed types on the current path, for detecting a cycle.
     * @return the validated mapping of the {@code managedType}.
     * @throws RuntimeException when any annotated attribute of the {@code managedType} is mapped inconsistently.
     * @implNote The validation runs once per {@code (rootType, embeddingPath)}, and descends through the whole
     *         reachable embeddable graph to completion before any instance is touched, so that an inconsistent
     *         mapping cannot leave an instance half-encrypted.
     */
    private Mapping validate(final ManagedType<?> rootType, final List<Attribute<?, ?>> embeddingPath,
                             final ManagedType<?> managedType, final Set<ManagedType<?>> visiting) {
        if (!visiting.add(managedType)) {
            throw new RuntimeException("embeddable cycle through " + managedType.getJavaType().getName());
        }
        final var attributes = getAttributes(managedType);
        rejectUnmappedAnnotatedMembers(rootType, embeddingPath, managedType, attributes.values());
        final var pairs = new ArrayList<Pair>();
        final var embeddeds = new ArrayList<Embedded>();
        final var paired = new HashSet<Attribute<?, ?>>();
        for (final var decryptedAttribute : attributes.values()) {
            final var persistentAttributeType = decryptedAttribute.getPersistentAttributeType();
            final var annotation =
                    JinahyaAttributeUtils.getJavaMemberAnnotation(decryptedAttribute, EncryptedAttribute.class);
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
            if (persistentAttributeType == Attribute.PersistentAttributeType.ELEMENT_COLLECTION
                && decryptedAttribute instanceof PluralAttribute<?, ?, ?> plural) {
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
                        throw reject(rootType, embeddingPath, decryptedAttribute, null,
                                     "an embeddable reached through an @ElementCollection cannot hold an encrypted"
                                     + " attribute; it would be persisted in the clear"
                                     + "; encrypted member: " + encrypted.getDeclaringClass().getName() + '.'
                                     + encrypted.getName());
                    }
                }
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
            // the declared java member decides the codec, in both directions; know it is one before any value moves.
            // A member inherited from a generic superclass is declared as a type variable: resolve it against the
            // concrete class, or Base<String> would be encoded as its erasure, Object (#67)
            final var javaType = resolveJavaType(managedType.getJavaType(), decryptedAttribute);
            final var codec = EntityEncryptionServiceUtils.codecOf(javaType);
            if (codec == null) {
                throw reject(rootType, embeddingPath, decryptedAttribute, null,
                             "no codec for the declared java type " + javaType.getName());
            }
            // Jakarta Persistence validates after @PrePersist/@PreUpdate, by which time encrypting has nulled the
            // plaintext: a constraint on it is evaluated against null, failing every write or passing vacuously
            final var constraint = findConstraint(decryptedAttribute);
            if (constraint != null) {
                throw reject(rootType, embeddingPath, decryptedAttribute, null,
                             "a Bean Validation constraint on an encrypted attribute is evaluated against null"
                             + " (validation runs after encrypting has cleared the plaintext); found @"
                             + constraint.annotationType().getName()
                             + "; validate the value before persisting, or use the transient-plaintext mode (#82)");
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
            // a legacy row (plaintext in this column, no ciphertext) is migrated by the UPDATE which stores its
            // ciphertext, and only that UPDATE nulls this column; a non-updatable one keeps the legacy plaintext forever
            if (rules.updatable() != MappingFlag.YES) {
                throw reject(rootType, embeddingPath, decryptedAttribute, null,
                             (rules.updatable() == MappingFlag.UNKNOWN
                                     ? "cannot establish that a decrypted attribute's column is updatable"
                                     : "a decrypted attribute's column has to be updatable")
                             + "; otherwise a migrated legacy row keeps its plaintext (from " + rules.source() + ")");
            }
            final var encryptedAttribute = checkEncryptedAttribute(
                    rootType, embeddingPath, decryptedAttribute.getName(),
                    decryptedAttribute.getDeclaringType().getJavaType(), decryptedAttribute,
                    annotation.encryptedAttribute(), attributes, paired);
            pairs.add(new Pair(decryptedAttribute, null, encryptedAttribute, javaType));
        }
        validateTransientPlaintexts(rootType, embeddingPath, managedType, attributes, paired, pairs);
        visiting.remove(managedType);
        return new Mapping(List.copyOf(pairs), List.copyOf(embeddeds));
    }

    /**
     * Returns whether the specified field is transient to Jakarta Persistence: annotated with
     * {@link jakarta.persistence.Transient @Transient}, or declared {@code transient}.
     *
     * @param field the field.
     * @return {@code true} when the {@code field} is not persistent.
     */
    private static boolean isTransientField(final java.lang.reflect.Field field) {
        return field.isAnnotationPresent(jakarta.persistence.Transient.class)
               || java.lang.reflect.Modifier.isTransient(field.getModifiers());
    }

    /**
     * Validates every transient plaintext field (Mode B) of the specified managed type, and adds its pair.
     *
     * @param rootType      the managed type the walk started from.
     * @param embeddingPath the embedding attributes from the {@code rootType}.
     * @param managedType   the managed type whose java class, and superclasses, are searched.
     * @param attributes    the attributes of the {@code managedType}, by name.
     * @param paired        the attributes already paired.
     * @param pairs         the list to which each validated pair is added.
     * @implNote A transient field is not in the metamodel, so it is found by reflection. It has no column, so no
     *         column rule applies to it; and since nothing ever nulls it, a Bean Validation constraint on it sees the
     *         real value, and is allowed. What it needs instead is a setter which also nulls the ciphertext, the
     *         provider dirty-checking only mapped attributes: {@link #checkInvalidatingSetter} proves one.
     */
    private void validateTransientPlaintexts(final ManagedType<?> rootType,
                                             final List<Attribute<?, ?>> embeddingPath,
                                             final ManagedType<?> managedType,
                                             final Map<String, Attribute<?, ?>> attributes,
                                             final Set<Attribute<?, ?>> paired, final List<Pair> pairs) {
        final var concrete = managedType.getJavaType();
        for (final var member : declaredMembers(concrete)) {
            if (!(member instanceof java.lang.reflect.Field field) || !isTransientField(field)) {
                continue;
            }
            final var annotation = field.getAnnotation(EncryptedAttribute.class);
            if (annotation == null) {
                continue;
            }
            final var declaring = field.getDeclaringClass();
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                throw reject(rootType, embeddingPath, field.getName(), declaring, null,
                             "a transient plaintext cannot be a static field");
            }
            final var javaType = resolveType(concrete, field.getGenericType());
            if (javaType.isPrimitive()) {
                throw reject(rootType, embeddingPath, field.getName(), declaring, null,
                             "a decrypted attribute cannot be of a primitive type; it has no value when cleared");
            }
            if (EntityEncryptionServiceUtils.codecOf(javaType) == null) {
                throw reject(rootType, embeddingPath, field.getName(), declaring, null,
                             "no codec for the declared java type " + javaType.getName());
            }
            final var encryptedAttribute = checkEncryptedAttribute(
                    rootType, embeddingPath, field.getName(), declaring, null, annotation.encryptedAttribute(),
                    attributes, paired);
            checkInvalidatingSetter(rootType, embeddingPath, concrete, field, javaType, encryptedAttribute);
            field.setAccessible(true);
            pairs.add(new Pair(null, field, encryptedAttribute, javaType));
        }
    }

    /**
     * Checks that the specified transient plaintext has a setter which also nulls the ciphertext, by calling it on a
     * throwaway instance.
     *
     * @param rootType           the managed type the walk started from.
     * @param embeddingPath      the embedding attributes from the {@code rootType}.
     * @param concrete           the entity, or embeddable, class.
     * @param field              the transient plaintext field.
     * @param javaType           the resolved type of the {@code field}.
     * @param encryptedAttribute the attribute holding the ciphertext.
     * @implNote Without such a setter, changing only the plaintext of a loaded instance changes nothing the provider
     *         dirty-checks: no {@code @PreUpdate} runs, and the edit is lost. The check sets the ciphertext of a new
     *         instance (Jakarta Persistence requires a no-arg constructor) to a dummy value, calls the setter with
     *         {@code null}, and expects the ciphertext to be {@code null}. An abstract class is not instantiated;
     *         its concrete subclasses are checked on their own.
     */
    private static void checkInvalidatingSetter(final ManagedType<?> rootType,
                                                final List<Attribute<?, ?>> embeddingPath, final Class<?> concrete,
                                                final java.lang.reflect.Field field, final Class<?> javaType,
                                                final Attribute<?, ?> encryptedAttribute) {
        final var name = "set" + Character.toUpperCase(field.getName().charAt(0)) + field.getName().substring(1);
        java.lang.reflect.Method setter = null;
        search:
        for (var c = concrete; c != null && c != Object.class; c = c.getSuperclass()) {
            for (final var method : c.getDeclaredMethods()) {
                if (method.getName().equals(name) && method.getParameterCount() == 1 && !method.isBridge()
                    && !java.lang.reflect.Modifier.isStatic(method.getModifiers())
                    && method.getParameterTypes()[0].isAssignableFrom(javaType)) {
                    setter = method;
                    break search;
                }
            }
        }
        final var expectation = name + "(" + javaType.getSimpleName() + "), which sets the field and also sets "
                                + encryptedAttribute.getName() + " to null, so that the provider sees the change";
        if (setter == null) {
            throw reject(rootType, embeddingPath, field.getName(), field.getDeclaringClass(), encryptedAttribute,
                         "a transient plaintext needs a setter, " + expectation);
        }
        if (java.lang.reflect.Modifier.isAbstract(concrete.getModifiers())) {
            return;
        }
        try {
            final var constructor = concrete.getDeclaredConstructor();
            constructor.setAccessible(true);
            final var instance = constructor.newInstance();
            JinahyaAttributeUtils.setAttributeValue(instance, encryptedAttribute, new byte[]{0});
            setter.setAccessible(true);
            setter.invoke(instance, (Object) null);
            if (JinahyaAttributeUtils.getAttributeValue(instance, encryptedAttribute) != null) {
                throw reject(rootType, embeddingPath, field.getName(), field.getDeclaringClass(), encryptedAttribute,
                             "the setter of a transient plaintext does not null the ciphertext; it has to be "
                             + expectation + " (a generated setter is not enough)");
            }
        } catch (final ReflectiveOperationException roe) {
            final var cause = roe instanceof java.lang.reflect.InvocationTargetException ite && ite.getCause() != null
                    ? ite.getCause() : roe;
            throw reject(rootType, embeddingPath, field.getName(), field.getDeclaringClass(), encryptedAttribute,
                         "cannot verify the setter of a transient plaintext, which is called once with null on a"
                         + " new instance (" + cause + "); it has to be " + expectation);
        }
    }

    /**
     * Validates the attribute holding the ciphertext of a plaintext, and returns it.
     *
     * @param rootType           the managed type the walk started from.
     * @param embeddingPath      the embedding attributes from the {@code rootType}.
     * @param decryptedName      the name of the plaintext attribute, or transient field.
     * @param declaringType      the class declaring the plaintext.
     * @param decryptedAttribute the plaintext attribute; {@code null} for a transient plaintext.
     * @param named              the name given by {@link EncryptedAttribute#encryptedAttribute()}; may be blank.
     * @param attributes         the attributes of the managed type, by name.
     * @param paired             the attributes already paired, to which the result is added.
     * @return the attribute holding the ciphertext.
     */
    private Attribute<?, ?> checkEncryptedAttribute(final ManagedType<?> rootType,
                                                    final List<Attribute<?, ?>> embeddingPath,
                                                    final String decryptedName, final Class<?> declaringType,
                                                    final @Nullable Attribute<?, ?> decryptedAttribute,
                                                    final String named,
                                                    final Map<String, Attribute<?, ?>> attributes,
                                                    final Set<Attribute<?, ?>> paired) {
        final var name = named.isBlank()
                ? decryptedName + EncryptedAttributeConstants.DEFAULT_ENCRYPTED_ATTRIBUTE_POSTFIX
                : named;
        final var encryptedAttribute = attributes.get(name);
        if (encryptedAttribute == null) {
            throw reject(rootType, embeddingPath, decryptedName, declaringType, null,
                         "no encrypted attribute named '" + name + "'");
        }
        if (decryptedAttribute != null && encryptedAttribute == decryptedAttribute) {
            throw reject(rootType, embeddingPath, decryptedName, declaringType, encryptedAttribute,
                         "an encrypted attribute cannot be the decrypted attribute itself");
        }
        if (encryptedAttribute.getPersistentAttributeType() != Attribute.PersistentAttributeType.BASIC) {
            throw reject(rootType, embeddingPath, decryptedName, declaringType, encryptedAttribute,
                         "an encrypted attribute has to be BASIC");
        }
        if (encryptedAttribute.getJavaType() != byte[].class) {
            throw reject(rootType, embeddingPath, decryptedName, declaringType, encryptedAttribute,
                         "an encrypted attribute has to be typed byte[]");
        }
        if (isIdOrVersion(encryptedAttribute)) {
            throw reject(rootType, embeddingPath, decryptedName, declaringType, encryptedAttribute,
                         "an identifier, or a version, attribute cannot hold the ciphertext");
        }
        if (!isOptional(encryptedAttribute)) {
            throw reject(rootType, embeddingPath, decryptedName, declaringType, encryptedAttribute,
                         "an encrypted attribute has to be optional");
        }
        if (JinahyaAttributeUtils.getJavaMemberAnnotation(encryptedAttribute, EncryptedAttribute.class) != null) {
            throw reject(rootType, embeddingPath, decryptedName, declaringType, encryptedAttribute,
                         "an encrypted attribute cannot itself be annotated with @EncryptedAttribute");
        }
        final var encryptedRules = resolveColumnRules(rootType, embeddingPath, encryptedAttribute);
        if (encryptedRules.insertable() != MappingFlag.YES || encryptedRules.updatable() != MappingFlag.YES) {
            final var unknown = encryptedRules.insertable() == MappingFlag.UNKNOWN
                                || encryptedRules.updatable() == MappingFlag.UNKNOWN;
            throw reject(rootType, embeddingPath, decryptedName, declaringType, encryptedAttribute,
                         (unknown
                                 ? "cannot establish that an encrypted attribute's column is insertable and updatable"
                                 : "an encrypted attribute's column has to be insertable and updatable")
                         + "; otherwise the ciphertext cannot be stored (from " + encryptedRules.source() + ")");
        }
        if (encryptedRules.nullable() != MappingFlag.YES) {
            throw reject(rootType, embeddingPath, decryptedName, declaringType, encryptedAttribute,
                         (encryptedRules.nullable() == MappingFlag.UNKNOWN
                                 ? "cannot establish that an encrypted attribute's column is nullable"
                                 : "an encrypted attribute's column has to be nullable")
                         + "; decrypting nulls it (from " + encryptedRules.source() + ")");
        }
        if (!paired.add(encryptedAttribute)) {
            throw reject(rootType, embeddingPath, decryptedName, declaringType, encryptedAttribute,
                         "an encrypted attribute is paired more than once");
        }
        return encryptedAttribute;
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
     * A validated pair of a plaintext and the attribute which holds its ciphertext.
     * <p>
     * The plaintext is either a persistent attribute (Mode A, {@code decrypted}), or a transient field (Mode B,
     * {@code plaintextField}); exactly one of the two is set.
     *
     * @param decrypted      the attribute holding the plaintext, in Mode A; {@code null} in Mode B.
     * @param plaintextField the transient field holding the plaintext, in Mode B; {@code null} in Mode A.
     * @param encrypted      the attribute holding the ciphertext.
     * @param javaType       the declared java type of the plaintext, resolved against the concrete class.
     */
    private record Pair(@Nullable Attribute<?, ?> decrypted, java.lang.reflect.@Nullable Field plaintextField,
                        Attribute<?, ?> encrypted, Class<?> javaType) {

        Pair {
            assert (decrypted == null) != (plaintextField == null);
        }

        /**
         * Returns whether the plaintext is a transient field: Mode B.
         */
        boolean isTransient() {
            return plaintextField != null;
        }

        /**
         * Returns the name of the plaintext attribute, or field.
         */
        String name() {
            return plaintextField != null ? plaintextField.getName() : Objects.requireNonNull(decrypted).getName();
        }

        @Nullable Object getPlaintext(final Object target) {
            if (plaintextField != null) {
                try {
                    return plaintextField.get(target);
                } catch (final IllegalAccessException iae) {
                    throw new RuntimeException("failed to read " + plaintextField, iae);
                }
            }
            return JinahyaAttributeUtils.getAttributeValue(target, Objects.requireNonNull(decrypted));
        }

        Assignment setPlaintext(final Object target, final @Nullable Object value) {
            final var field = plaintextField;
            if (field != null) {
                return new Assignment(() -> {
                    try {
                        field.set(target, value);
                    } catch (final IllegalAccessException iae) {
                        throw new RuntimeException("failed to write " + field, iae);
                    }
                });
            }
            final var attribute = Objects.requireNonNull(decrypted);
            return new Assignment(() -> JinahyaAttributeUtils.setAttributeValue(target, attribute, value));
        }

        byte @Nullable [] getCiphertext(final Object target) {
            return (byte[]) JinahyaAttributeUtils.getAttributeValue(target, encrypted);
        }

        Assignment setCiphertext(final Object target, final byte @Nullable [] value) {
            return new Assignment(() -> JinahyaAttributeUtils.setAttributeValue(target, encrypted, value));
        }
    }

    /**
     * An assignment to an attribute, or a field, of an entity, or embeddable, instance, deferred until every
     * assignment of a transform has been computed.
     *
     * @param action the assignment.
     */
    private record Assignment(Runnable action) {

        void apply() {
            action.run();
        }
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
        final var mapping = getMapping(getManagedType(resolveClass(object)));
        // computes every ciphertext before assigning any, so that a failure part-way leaves the instance as it was
        final var assignments = new ArrayList<Assignment>();
        encrypt(encryptionIdentifier, object, mapping, assignments);
        assignments.forEach(Assignment::apply);
    }

    /**
     * Encrypts the specified object against the mapping already validated for it.
     *
     * @param encryptionIdentifier the identifier the encryption keys are selected by.
     * @param object               the entity, or embeddable, instance to encrypt, in place.
     * @param mapping              the validated mapping of the {@code object}.
     * @param assignments          the list to which every attribute assignment is added, rather than made.
     * @implNote The mapping is passed down rather than looked up again: an embeddable reached through an
     *         {@link AttributeOverride @AttributeOverride} would otherwise be re-resolved without that override in
     *         scope. Nothing is assigned here; the caller applies the {@code assignments} once all of them have been
     *         computed.
     */
    private void encrypt(final String encryptionIdentifier, final Object object, final Mapping mapping,
                         final List<Assignment> assignments) {
        for (final var embedded : mapping.embeddeds()) {
            final var embeddedValue = JinahyaAttributeUtils.getAttributeValue(object, embedded.attribute());
            if (embeddedValue != null) {
                encrypt(encryptionIdentifier, embeddedValue, embedded.mapping(), assignments);
            }
        }
        for (final var pair : mapping.pairs()) {
            final var plaintext = pair.getPlaintext(object);
            final var ciphertext = pair.getCiphertext(object);
            if (pair.isTransient()) {
                // Mode B: the plaintext is never mapped, so nothing mapped is ever nulled
                if (plaintext == null) {
                    if (ciphertext != null) {
                        assignments.add(pair.setCiphertext(object, null)); // cleared
                    }
                    continue;
                }
                final var framed = encode(pair, plaintext);
                if (ciphertext != null) {
                    // the invalidating setter nulls the ciphertext on a change; a ciphertext still present means the
                    // plaintext is unchanged -- unless it was written directly, which this compares for, so that the
                    // write is kept rather than lost, and an unchanged value is never re-encrypted with a new IV
                    final var current = entityEncryptionManager.decrypt(encryptionIdentifier, ciphertext.clone());
                    if (current != null && Arrays.equals(current, framed)) {
                        continue;
                    }
                }
                assignments.add(pair.setCiphertext(object, encryptFramed(encryptionIdentifier, pair, framed)));
                continue;
            }
            // Mode A: the plaintext is mapped, so it is nulled to keep it out of the row
            if (plaintext == null) {
                if (ciphertext != null) {
                    // already encrypted
                    continue;
                }
                assignments.add(pair.setCiphertext(object, null));
                continue;
            }
            assignments.add(pair.setCiphertext(
                    object, encryptFramed(encryptionIdentifier, pair, encode(pair, plaintext))));
            assignments.add(pair.setPlaintext(object, null));
        }
    }

    /**
     * Returns the specified plaintext value of the specified pair, encoded by its declared java type and framed with
     * the payload header.
     *
     * @param pair           the pair.
     * @param decryptedValue the plaintext value.
     * @return the framed encoding of the {@code decryptedValue}.
     */
    private static byte[] encode(final Pair pair, final Object decryptedValue) {
        final byte[] decryptedBytes;
        final var javaType = pair.javaType();
        // The DECLARED type decides the encoding, exactly as it decides the decoding below. Dispatching on
        // the runtime class here instead let the two ladders pick different codecs for one attribute -- a
        // java.util.Date holding a java.sql.Timestamp was written as epoch seconds and read back as epoch
        // millis, silently. Keep this chain in the same order as the one in decode(...).
        if (!javaType.isInstance(decryptedValue)) {
            throw new RuntimeException(
                    "the value is not an instance of the attribute's declared java type" +
                    "; decrypted attribute: " + pair.name() +
                    "; java type: " + javaType.getName() +
                    "; value type: " + decryptedValue.getClass().getName()
            );
        }
        try {
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
                decryptedBytes = sql_timestamp_12((java.sql.Timestamp) decryptedValue);
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
        } catch (final IllegalArgumentException iae) {
            // a value the codec refuses to encode, because it could not be read back as it is: a string with an
            // unpaired surrogate, an oversized serializable, ... -- reported here while the caller still holds it.
            // The message never carries the value.
            throw new RuntimeException(
                    "cannot encode the value (" + iae.getMessage() + ")" +
                    "; decrypted attribute: " + pair.name() +
                    "; java type: " + javaType.getName(),
                    iae
            );
        }
        // the header names the format version and the codec, so that a reader can tell what wrote the bytes
        final var codec = EntityEncryptionServiceUtils.codecOf(javaType);
        assert codec != null : "the ladder above handled a type codecOf does not know: " + javaType;
        return EntityEncryptionServiceUtils.frame(codec, decryptedBytes);
    }

    private byte[] encryptFramed(final String encryptionIdentifier, final Pair pair, final byte[] framed) {
        final var encrypted = entityEncryptionManager.encrypt(encryptionIdentifier, framed);
        if (encrypted == null) {
            throw new RuntimeException("encryptionManager returned null; decrypted attribute: " + pair.name());
        }
        return encrypted;
    }

    /**
     * Returns the number of rows of the specified entity which still hold a plaintext, and no ciphertext, in a Mode A
     * attribute: the rows which are not migrated yet.
     * <p>
     * An entity is ready to be switched to Mode B, its plaintext made {@link jakarta.persistence.Transient
     * @Transient}, when this returns {@code 0}. Run it while the entity is still in Mode A: a Mode B plaintext has no
     * mapping left to count by.
     *
     * @param entityClass the entity class.
     * @return the number of rows not migrated yet; {@code 0} when every row has its ciphertext.
     * @throws IllegalArgumentException when the {@code entityClass} is not an entity of this service's persistence
     *                                  unit.
     */
    public long countUnmigrated(final Class<?> entityClass) {
        Objects.requireNonNull(entityClass, "entityClass is null");
        if (!(getManagedType(entityClass) instanceof jakarta.persistence.metamodel.EntityType<?> entityType)) {
            throw new IllegalArgumentException("not an entity: " + entityClass);
        }
        final var conditions = new ArrayList<String>();
        collectUnmigratedConditions(getMapping(entityType), "e", conditions);
        if (conditions.isEmpty()) {
            return 0L;
        }
        try (var entityManager = entityManagerFactory.createEntityManager()) {
            return entityManager.createQuery(
                    "SELECT COUNT(e) FROM " + entityType.getName() + " e WHERE " + String.join(" OR ", conditions),
                    Long.class).getSingleResult();
        }
    }

    private static void collectUnmigratedConditions(final Mapping mapping, final String path,
                                                    final List<String> conditions) {
        for (final var pair : mapping.pairs()) {
            final var decrypted = pair.decrypted();
            if (decrypted != null) { // Mode A only
                conditions.add("(" + path + "." + decrypted.getName() + " IS NOT NULL AND "
                               + path + "." + pair.encrypted().getName() + " IS NULL)");
            }
        }
        for (final var embedded : mapping.embeddeds()) {
            collectUnmigratedConditions(embedded.mapping(), path + "." + embedded.attribute().getName(), conditions);
        }
    }

    /**
     * Returns whether the specified entity instance is to be transformed: whether its class is annotated with
     * {@link EncryptedEntity @EncryptedEntity}, directly or by inheritance.
     *
     * @param object the entity instance.
     * @return {@code true} when the {@code object} is to be transformed; {@code false} when it passes through.
     * @throws RuntimeException when the {@code object}'s class is not annotated, yet has encrypted attributes.
     * @implNote The gate lives here, not only in the callers, so that a direct caller of the public entry points gets
     *         the same guarantee. A class which is not annotated is validated all the same: an encrypted attribute on
     *         it is a forgotten annotation, which used to be skipped in silence, writing the plaintext.
     */
    private boolean isEncryptedEntity(final Object object) {
        final var type = resolveClass(object);
        return type.isAnnotationPresent(EncryptedEntity.class)
               || checkEncryptedEntity(type, getMapping(getManagedType(type)));
    }

    /**
     * Checks that the specified entity class is annotated with {@link EncryptedEntity @EncryptedEntity} when its
     * validated mapping has an encrypted attribute.
     *
     * @param type    the entity class.
     * @param mapping the validated mapping of the {@code type}.
     * @return whether the {@code type} is annotated, directly or by inheritance.
     * @throws RuntimeException when the {@code type} is not annotated, yet has encrypted attributes.
     */
    private static boolean checkEncryptedEntity(final Class<?> type, final Mapping mapping) {
        if (type.isAnnotationPresent(EncryptedEntity.class)) {
            return true;
        }
        if (hasPairs(mapping)) {
            throw new RuntimeException(
                    "an entity with encrypted attributes is not annotated with @EncryptedEntity"
                    + "; it would never be encrypted; entity: " + type.getName());
        }
        return false;
    }

    private static boolean hasPairs(final Mapping mapping) {
        if (!mapping.pairs().isEmpty()) {
            return true;
        }
        for (final var embedded : mapping.embeddeds()) {
            if (hasPairs(embedded.mapping())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Encrypts the annotated attributes of the specified object, with the identifier the
     * {@link EntityEncryptionManager encryptionManager} derives from it.
     *
     * @param object the entity instance to encrypt, in place; an instance of a class which is not annotated with
     *               {@link EncryptedEntity @EncryptedEntity}, and has no encrypted attribute, passes through untouched.
     * @throws RuntimeException when an attribute pair is inconsistent, when an attribute has a java type which
     *                          cannot be turned into bytes, or when the {@code object}'s class has encrypted
     *                          attributes but is not annotated with {@link EncryptedEntity @EncryptedEntity}.
     * @see EntityEncryptionManager#getEncryptionIdentifier(Object)
     */
    public void encrypt(final @Valid @NotNull Object object) {
        Objects.requireNonNull(object, "object is null");
        if (!isEncryptedEntity(object)) {
            return;
        }
        final var encryptionIdentifier = entityEncryptionManager.getEncryptionIdentifier(object);
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
        final var mapping = getMapping(getManagedType(resolveClass(object)));
        // decodes every value before assigning any, so that a failure part-way leaves the instance as it was
        final var assignments = new ArrayList<Assignment>();
        decrypt(encryptionIdentifier, object, mapping, assignments);
        assignments.forEach(Assignment::apply);
    }

    /**
     * Decrypts the specified object against the mapping already validated for it.
     *
     * @param encryptionIdentifier the identifier the encryption keys are selected by.
     * @param object               the entity, or embeddable, instance to decrypt, in place.
     * @param mapping              the validated mapping of the {@code object}.
     * @param assignments          the list to which every attribute assignment is added, rather than made.
     * @implNote The mapping is passed down rather than looked up again: an embeddable reached through an
     *         {@link AttributeOverride @AttributeOverride} would otherwise be re-resolved without that override in
     *         scope. Nothing is assigned here; the caller applies the {@code assignments} once all of them have been
     *         computed.
     */
    private void decrypt(final String encryptionIdentifier, final Object object, final Mapping mapping,
                         final List<Assignment> assignments) {
        for (final var embedded : mapping.embeddeds()) {
            final var embeddedValue = JinahyaAttributeUtils.getAttributeValue(object, embedded.attribute());
            if (embeddedValue != null) {
                decrypt(encryptionIdentifier, embeddedValue, embedded.mapping(), assignments);
            }
        }
        for (final var pair : mapping.pairs()) {
            final var ciphertext = pair.getCiphertext(object);
            if (ciphertext == null) {
                if (pair.isTransient()) {
                    // Mode B: no ciphertext is no value
                    assignments.add(pair.setPlaintext(object, null));
                    continue;
                }
                // Mode A: a legacy row keeps its plaintext; it is migrated when it is next written
                if (pair.getPlaintext(object) == null) {
                    assignments.add(pair.setPlaintext(object, null));
                }
                continue;
            }
            final var framedBytes = entityEncryptionManager.decrypt(encryptionIdentifier, ciphertext.clone());
            if (framedBytes == null) {
                throw new RuntimeException("encryptionManager returned null; decrypted attribute: " + pair.name());
            }
            assignments.add(pair.setPlaintext(object, decode(pair, framedBytes)));
            if (!pair.isTransient()) {
                assignments.add(pair.setCiphertext(object, null)); // Mode A only; Mode B leaves it, unchanged
            }
        }
    }

    /**
     * Returns the plaintext value of the specified pair, decoded from the specified framed bytes.
     *
     * @param pair        the pair.
     * @param framedBytes the decrypted, framed, bytes.
     * @return the decoded value.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Object decode(final Pair pair, final byte[] framedBytes) {
        final Object decryptedValue;
        // the declared java type, for the same reason as in encode(...): the two ladders must agree on the type
        final var javaType = pair.javaType();
        final var codec = EntityEncryptionServiceUtils.codecOf(javaType);
        if (codec == null) {
            throw new RuntimeException("unsupported java type: " + javaType);
        }
        try {
            // checks the format version, and that the codec which wrote the bytes is the one which reads them
            final var decryptedBytes = EntityEncryptionServiceUtils.unframe(codec, framedBytes);
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
                decryptedValue = sql_timestamp_12(decryptedBytes);
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
            //   AssertionError       an assert in EntityEncryptionServiceUtils, when assertions are enabled;
            //                        the payload lengths themselves are checked unconditionally
            //   IllegalArgumentException  chars_2l on an odd-length payload, Enum.valueOf on a name
            //                        which is not a constant of the attribute's enum, and a payload header
            //                        naming another format version or another codec
            // Without the last one, those two surfaced bare, with no clue which attribute they came from.
            throw new RuntimeException(
                    "cannot reconstruct the value from the decrypted bytes" +
                    (e.getMessage() == null ? "" : " (" + e.getMessage() + ")") +
                    "; decrypted attribute: " + pair.name() +
                    "; java type: " + javaType.getName() +
                    "; decrypted bytes: " + framedBytes.length,
                    e
            );
        }
        return decryptedValue;
    }

    /**
     * Decrypts the annotated attributes of the specified object, with the identifier the
     * {@link EntityEncryptionManager encryptionManager} derives from it.
     *
     * @param object the entity instance to decrypt, in place; an instance of a class which is not annotated with
     *               {@link EncryptedEntity @EncryptedEntity}, and has no encrypted attribute, passes through untouched.
     * @throws RuntimeException when an attribute pair is inconsistent, when an attribute has a java type which
     *                          cannot be reconstructed from bytes, or when the {@code object}'s class has encrypted
     *                          attributes but is not annotated with {@link EncryptedEntity @EncryptedEntity}.
     * @see EntityEncryptionManager#getEncryptionIdentifier(Object)
     */
    public void decrypt(final @Valid @NotNull Object object) {
        Objects.requireNonNull(object, "object is null");
        if (!isEncryptedEntity(object)) {
            return;
        }
        final var encryptionIdentifier = entityEncryptionManager.getEncryptionIdentifier(object);
        decrypt(encryptionIdentifier, object);
    }

    // ----------------------------------------------------------------------------------------------------- entityTypes

    // ---------------------------------------------------------------------------------------------------- managedTypes
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

    /**
     * Returns the metamodel {@link ManagedType} of the specified class.
     *
     * @param entityClass the class whose managed type is looked up.
     * @return the managed type of the {@code entityClass}.
     * @throws IllegalArgumentException when the {@code entityClass} is not a managed type of the persistence unit.
     * @implNote The lookup is cached per class, so that a listener consulting the metamodel on every lifecycle
     *         callback does not go back to the {@link jakarta.persistence.metamodel.Metamodel Metamodel} for each one.
     */
    protected ManagedType<?> getManagedType(final Class<?> entityClass) {
        return managedTypes.computeIfAbsent(
                Objects.requireNonNull(entityClass, "entityClass is null"),
                k -> entityManagerFactory.getMetamodel().managedType(k)
        );
    }

    // -------------------------------------------------------------------------------------------- entityManagerFactory

    // ----------------------------------------------------------------------------------------- entityEncryptionManager

    // -----------------------------------------------------------------------------------------------------------------
    private final Map<Class<?>, ManagedType<?>> managedTypes = new ConcurrentHashMap<>();

    private final Map<ManagedType<?>, Map<String, Attribute<?, ?>>> managedTypesAndAttributes =
            new ConcurrentHashMap<>();

    private final Map<MappingKey, Mapping> mappings = new ConcurrentHashMap<>();

    // -----------------------------------------------------------------------------------------------------------------
    private final EntityManagerFactory entityManagerFactory;

    private final EntityEncryptionManager entityEncryptionManager;
}
