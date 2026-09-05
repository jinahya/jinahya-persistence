package com.github.jinahya.persistence.test.util;

import jakarta.annotation.Nonnull;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.FieldPredicates;
import uk.co.jemos.podam.api.AbstractClassInfoStrategy;
import uk.co.jemos.podam.api.ClassInfoStrategy;
import uk.co.jemos.podam.api.DataProviderStrategy;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;
import uk.co.jemos.podam.api.RandomDataProviderStrategyImpl;

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
     *
     * @param <T> the type of the instances to randomize.
     * @see <a href="https://mtedone.github.io/podam/">PODAM</a>
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
        public ___OfPodam(final @Nonnull Class<T> targetClass, final @Nonnull Iterable<String> excludedFields) {
            super(targetClass, excludedFields);
        }

//SEP8//

        /**
         * Creates a new data provider strategy.
         *
         * @return a new data provider strategy.
         * @see RandomDataProviderStrategyImpl#RandomDataProviderStrategyImpl()
         */
        @Nonnull
        protected DataProviderStrategy getDataProviderStrategy() {
            return new RandomDataProviderStrategyImpl();
        }

        /**
         * Creates a new class info strategy which excludes {@link #excludedFields}.
         *
         * @return a new class info strategy which excludes {@link #excludedFields}
         * @implNote A new instance, rather than
         *         {@link uk.co.jemos.podam.api.DefaultClassInfoStrategy#getInstance()}, whose excluded fields, being
         *         held by a singleton and never removed, would leak into every other randomizer of a same target
         *         class.
         */
        @Nonnull
        protected ClassInfoStrategy getClassInfoStrategy() {
            final var classInfoStrategy = new AbstractClassInfoStrategy() {
            };
            excludedFields.forEach(v -> classInfoStrategy.addExcludedField(targetClass, v));
            return classInfoStrategy;
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
        @Nonnull
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
        @Nonnull
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
        public ___OfEasyRandomBean(final @Nonnull Class<T> targetClass,
                                   final @Nonnull Iterable<String> excludedFields) {
            super(targetClass, excludedFields);
        }

//SEP8//

        /**
         * Creates new parameters which exclude {@link #excludedFields} declared on the {@link #targetClass}.
         *
         * @return new parameters which exclude {@link #excludedFields}.
         * @implNote Each exclusion is narrowed to the {@link #targetClass} itself, so that a field of a same
         *         name, declared on an unrelated type reached through an association, is still randomized.
         * @see EasyRandomParameters#excludeField(java.util.function.Predicate)
         * @see FieldPredicates#named(String)
         * @see FieldPredicates#inClass(Class)
         */
        @Nonnull
        protected EasyRandomParameters getEasyRandomParameters() {
            final var parameters = new EasyRandomParameters();
            excludedFields.forEach(v -> parameters.excludeField(
                    FieldPredicates.named(v).and(FieldPredicates.inClass(targetClass))
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
        @Nonnull
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
        @Nonnull
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
    protected __Randomizer(final @Nonnull Class<T> targetClass, final @Nonnull Iterable<String> excludedFields) {
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
    @Nonnull
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
    @Nonnull
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
