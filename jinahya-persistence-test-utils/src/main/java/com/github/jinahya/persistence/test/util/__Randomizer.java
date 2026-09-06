package com.github.jinahya.persistence.test.util;

import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.FieldPredicates;
import uk.co.jemos.podam.api.AbstractClassInfoStrategy;
import uk.co.jemos.podam.api.ClassInfo;
import uk.co.jemos.podam.api.ClassInfoStrategy;
import uk.co.jemos.podam.api.DataProviderStrategy;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;
import uk.co.jemos.podam.api.RandomDataProviderStrategyImpl;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * An abstract class for randomizing a specific class.
 * <p>
 * This class itself is engine-agnostic; use one of the nested subclasses to pick an engine, or extend this class
 * directly and implement {@link #get() get()} to populate instances by hand. Both engines are declared as
 * {@code provided} dependencies, so a consumer brings only the one it uses.
 * <p>
 * Fields named in {@link #excludedFields} are left at whatever value the instance already carries, which is how a
 * generated identifier, a version, or an auditing column is kept out of the way of the persistence provider.
 *
 * @param <T> the type of the instances to randomize.
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ___OfPodam
 * @see ___OfEasyRandomBean
 * @see __RandomizerUtils#newRandomizedInstanceOf(Class)
 * @see __RandomizerClass
 * @see __Instantiator
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __Randomizer<T> implements Supplier<T> {

    /**
     * An abstract randomizer which randomizes instances using <a href="https://mtedone.github.io/podam/">PODAM</a>.
     * <p>
     * This flavor populates an instance obtained from {@link #newTargetInstance()}, and, hence, honors the
     * {@link __Instantiator} located for the {@link #targetClass}; pick it when the target class must be constructed a
     * particular way.
     * <p>
     * <strong>The target class has to expose accessors.</strong> PODAM writes a property through its setter, and
     * recurses into one through its getter; it never assigns a field reflectively. A class which declares only fields,
     * which is the ordinary shape of a JPA entity mapped with field access, is therefore left <em>entirely
     * unpopulated</em>, silently and without an error. Use {@link ___OfEasyRandomBean} for such a class, bearing in
     * mind that it does not use {@link #newTargetInstance()}, or extend {@link __Randomizer} directly and populate the
     * instance by hand.
     *
     * @param <T> the type of the instances to randomize.
     * @see <a href="https://mtedone.github.io/podam/">PODAM</a>
     * @see ___OfEasyRandomBean
     */
    @SuppressWarnings({
            "java:S101" // Class names should comply with a naming convention
    })
    public abstract static class ___OfPodam<T> extends __Randomizer<T> {

        /**
         * Creates a new instance for initializing a randomized instance of the specified class.
         *
         * @param targetClass    the class to be randomized.
         * @param excludedFields fields to be excluded from randomization.
         */
        public ___OfPodam(final Class<T> targetClass, final Iterable<String> excludedFields) {
            super(targetClass, excludedFields);
        }

//SEP8//

        /**
         * Creates a new data provider strategy.
         *
         * @return a new data provider strategy.
         * @see RandomDataProviderStrategyImpl#RandomDataProviderStrategyImpl()
         */
        protected DataProviderStrategy getDataProviderStrategy() {
            return new RandomDataProviderStrategyImpl();
        }

        /**
         * Creates a new class info strategy which excludes {@link #excludedFields} from the {@link #targetClass}, and
         * from any subclass of it.
         *
         * @return a new class info strategy which excludes {@link #excludedFields}
         * @implNote A new instance, rather than
         *         {@link uk.co.jemos.podam.api.DefaultClassInfoStrategy#getInstance()}, whose excluded fields, being
         *         held by a singleton and never removed, would leak into every other randomizer of a same target
         *         class.
         *         <p>
         *         The exclusions are applied by overriding {@link AbstractClassInfoStrategy#getClassInfo(Class)},
         *         rather than by {@link AbstractClassInfoStrategy#addExcludedField(Class, String) registering} them:
         *         PODAM introspects the <em>runtime</em> class of the instance being populated, and looks its
         *         exclusions up by exact class, so a registration made for the {@link #targetClass} is silently ignored
         *         whenever {@link #newTargetInstance()} yields a subclass — which the located {@link __Instantiator} is
         *         explicitly allowed to do. Registrations made by a subclass, through {@code addExcludedField}, are
         *         merged in, so that customization keeps working.
         */
        protected ClassInfoStrategy getClassInfoStrategy() {
            return new AbstractClassInfoStrategy() {
                @Override
                public ClassInfo getClassInfo(final Class<?> pojoClass) {
                    final var excluded = new HashSet<String>();
                    // exclusions registered through addExcludedField(Class, String), which are keyed by exact class
                    final var registered = getExcludedFields(pojoClass);
                    if (registered != null) {
                        excluded.addAll(registered);
                    }
                    if (targetClass.isAssignableFrom(pojoClass)) {
                        excluded.addAll(excludedFields);
                    }
                    // getExtraMethods(Class) is a raw map lookup, and may be null; the ClassInfo constructor adds the
                    // argument to a collection without a null check
                    final var extraMethods = getExtraMethods(pojoClass);
                    return getClassInfo(
                            pojoClass,
                            getExcludedAnnotations(),
                            excluded,
                            this,
                            extraMethods == null ? List.of() : extraMethods
                    );
                }
            };
        }

        /**
         * Creates a new factory created with a data provider strategy from the
         * {@link #getDataProviderStrategy() dataProviderStrategy} method, and set with a class info strategy from the
         * {@link #getClassInfoStrategy() classInfoStrategy} method.
         *
         * @return a new factory.
         * @see PodamFactoryImpl#PodamFactoryImpl(DataProviderStrategy)
         * @see #getClassInfoStrategy()
         * @see PodamFactory#setClassStrategy(ClassInfoStrategy)
         */
        protected PodamFactory getPodamFactory() {
            final var factory = new PodamFactoryImpl(getDataProviderStrategy());
            factory.setClassStrategy(getClassInfoStrategy());
            return factory;
        }

        /**
         * {@inheritDoc}
         *
         * @return {@inheritDoc}
         * @implSpec Populates a {@link #newTargetInstance() new instance} of the {@link #targetClass}, using a
         *         factory from the {@link #getPodamFactory() podamFactory} method.
         * @see PodamFactory#populatePojo(Object, java.lang.reflect.Type...)
         */
        @Override
        public T get() {
            return getPodamFactory().populatePojo(newTargetInstance());
        }
    }

    /**
     * An abstract randomizer which randomizes instances using
     * <a href="https://github.com/j-easy/easy-random">Easy Random</a>, with bean validation constraints honored.
     * <p>
     * Pick this flavor when generated values should satisfy the constraints already declared on the target class, so
     * that a randomized instance is persistable without a separate validation pass.
     *
     * @param <T> the type of the instances to randomize.
     * @implNote Constraints are honored only when {@code easy-random-bean-validation} is on the runtime
     *         classpath; Easy Random discovers its {@link org.jeasy.random.validation.BeanValidationRandomizerRegistry}
     *         through the {@link java.util.ServiceLoader}, and silently generates unconstrained values when the
     *         artifact is absent. Both Easy Random artifacts are {@code provided} dependencies of this module.
     * @see <a href="https://github.com/j-easy/easy-random">Easy Random</a>
     * @see org.jeasy.random.validation.BeanValidationRandomizerRegistry
     */
    @SuppressWarnings({
            "java:S101" // Class names should comply with a naming convention
    })
    public abstract static class ___OfEasyRandomBean<T> extends __Randomizer<T> {

        /**
         * Creates a new instance for initializing a randomized instance of the specified class.
         *
         * @param targetClass    the class to be randomized.
         * @param excludedFields fields to be excluded from randomization.
         */
        public ___OfEasyRandomBean(final Class<T> targetClass,
                                   final Iterable<String> excludedFields) {
            super(targetClass, excludedFields);
        }

//SEP8//

        /**
         * Creates new parameters which exclude {@link #excludedFields} declared on the {@link #targetClass}, or on any
         * of its supertypes.
         *
         * @return new parameters which exclude {@link #excludedFields}.
         * @implNote Each exclusion is narrowed to the fields which the {@link #targetClass} actually declares
         *         or inherits, so that a field of a same name, declared on an unrelated type reached through an
         *         association, is still randomized. Note that the predicate identifies a field <em>declaration</em>,
         *         and that Easy Random evaluates it everywhere in the graph: when the {@link #targetClass} and an
         *         associated type inherit the excluded field from a common supertype, it is excluded on both. The
         *         {@link ___OfPodam} flavor, which scopes exclusions by the runtime class being populated, excludes it
         *         only on the {@link #targetClass} and its subclasses. The declaring class is matched with
         *         {@link Class#isAssignableFrom(Class)}, rather than with
         *         {@link FieldPredicates#inClass(Class) FieldPredicates.inClass}, whose exact
         *         {@link Class#equals(Object) equality} would leave a field inherited from a
         *         {@code jakarta.persistence.MappedSuperclass}, or from an abstract entity class, randomized; a
         *         generated identifier, a version, and an auditing column are usually declared exactly there. Names are
         *         matched by {@link String#equals(Object) equality}, rather than by
         *         {@link FieldPredicates#named(String) FieldPredicates.named}, which treats its argument as a regular
         *         expression, so that both flavors read an exclusion the same way.
         * @see EasyRandomParameters#excludeField(java.util.function.Predicate)
         */
        protected EasyRandomParameters getEasyRandomParameters() {
            final var parameters = new EasyRandomParameters();
            excludedFields.forEach(v -> parameters.excludeField(
                    f -> f.getName().equals(v) && f.getDeclaringClass().isAssignableFrom(targetClass)
            ));
            return parameters;
        }

        /**
         * Creates a new randomizer created with parameters from the
         * {@link #getEasyRandomParameters() easyRandomParameters} method.
         *
         * @return a new randomizer.
         * @see EasyRandom#EasyRandom(EasyRandomParameters)
         * @see #getEasyRandomParameters()
         */
        protected EasyRandom getEasyRandom() {
            return new EasyRandom(getEasyRandomParameters());
        }

        /**
         * {@inheritDoc}
         *
         * @return {@inheritDoc}
         * @implSpec Returns a new object of the {@link #targetClass}, from a randomizer of the
         *         {@link #getEasyRandom() easyRandom} method.
         * @implNote Unlike {@link ___OfPodam}, this class does not use the {@link #newTargetInstance()} method;
         *         Easy Random instantiates the {@link #targetClass} by itself, bypassing its constructors, so any value
         *         which only a constructor would assign is absent. Use {@link ___OfPodam} when that matters.
         * @see EasyRandom#nextObject(Class)
         */
        @Override
        public T get() {
            return getEasyRandom().nextObject(targetClass);
        }
    }

//SEP:CONSTRUCTORS

    /**
     * Creates a new instance for initializing a randomized instance of the specified class.
     *
     * @param targetClass    the class to be randomized.
     * @param excludedFields fields to be excluded from randomization; {@code null}, and blank, elements are dropped,
     *                       and the rest are stripped and deduplicated.
     * @throws NullPointerException when either argument is {@code null}.
     * @apiNote A subclass is expected to declare a no-argument constructor which supplies both arguments, for
     *         that is how a located randomizer class is instantiated.
     * @see __RandomizerUtils#moreExcludedFields(Iterable, Iterable)
     */
    protected __Randomizer(final Class<T> targetClass, final Iterable<String> excludedFields) {
        super();
        Objects.requireNonNull(targetClass, "targetClass is null");
        Objects.requireNonNull(excludedFields, "excludedFields is null");
        this.targetClass = targetClass;
        this.excludedFields = StreamSupport.stream(excludedFields.spliterator(), false)
                .filter(Objects::nonNull)
                .map(String::strip)
                .filter(v -> !v.isBlank())
                .collect(Collectors.toUnmodifiableSet());
    }

// ---------------------------------------------------------------------------------------------------------------------

    /**
     * Returns a randomized instance of the {@link #targetClass}.
     *
     * @return a randomized instance of the {@link #targetClass}.
     */
    @Override
    public abstract T get();

    /**
     * Returns a new, yet to be randomized, instance of the {@link #targetClass}.
     *
     * @return a new instance of the {@link #targetClass}.
     * @implSpec Delegates to {@link __InstantiatorUtils#newInstantiatedInstanceOf(Class)}, so the
     *         {@link __Instantiator} located for the {@link #targetClass}, if any, decides how the instance is
     *         constructed.
     * @see __InstantiatorUtils#newInstantiatedInstanceOf(Class)
     */
    protected T newTargetInstance() {
        return __InstantiatorUtils.newInstantiatedInstanceOf(targetClass);
    }

// ---------------------------------------------------------------------------------------------------------------------

    /**
     * The target type to randomize.
     */
    protected final Class<T> targetClass;

    /**
     * An unmodifiable set of field names to exclude from the randomization.
     */
    protected final Set<String> excludedFields;
}
