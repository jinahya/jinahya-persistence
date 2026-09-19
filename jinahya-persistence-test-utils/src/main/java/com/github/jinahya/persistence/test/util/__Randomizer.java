package com.github.jinahya.persistence.test.util;

import com.navercorp.fixturemonkey.ArbitraryBuilder;
import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector;
import com.navercorp.fixturemonkey.api.property.DefaultPropertyGenerator;
import org.instancio.Instancio;
import org.instancio.InstancioObjectApi;
import org.instancio.Select;
import org.instancio.settings.Settings;
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
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * An abstract class for randomizing a specific class.
 * <p>
 * This class itself is engine-agnostic; use one of the nested subclasses to pick an engine, or extend this class
 * directly and implement {@link #get() get()} to populate instances by hand. All engines are declared as
 * {@code provided} dependencies, so a consumer brings only the one it uses.
 * <p>
 * Fields named in {@link #excludedFields} are left at whatever value the instance already carries, which is how a
 * generated identifier, a version, or an auditing column is kept out of the way of the persistence provider.
 *
 * @param <T> the type of the instances to randomize.
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ___OfPodam
 * @see ___OfEasyRandom
 * @see ___OfInstancio
 * @see ___OfFixtureMonkey
 * @see __RandomizerUtils#newRandomizedInstanceOf(Class)
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
     * unpopulated</em>, silently and without an error. Use {@link ___OfEasyRandom}, {@link ___OfInstancio}, or
     * {@link ___OfFixtureMonkey} for such a class — bearing in mind that, of those, only {@link ___OfInstancio} uses
     * {@link #newTargetInstance()} — or extend {@link __Randomizer} directly and populate the instance by hand.
     * <p>
     * This is the only flavor which honors {@code jakarta.validation.constraints} out of the box; PODAM reads them
     * through its {@link uk.co.jemos.podam.common.BeanValidationStrategy}. {@link ___OfInstancio} and
     * {@link ___OfFixtureMonkey} honor them once explicitly configured to, and {@link ___OfEasyRandom} can not honor
     * them at all.
     *
     * @param <T> the type of the instances to randomize.
     * @implNote Constraint support is partial, and is keyed to the <em>field</em>: an annotation declared on a
     *         getter, or on a setter, is not seen, even though the value is written through the setter. Of those
     *         verified against PODAM 8.0.2, {@code @Size}, {@code @Email}, {@code @Past}, and a {@code @Min}/
     *         {@code @Max} pair are honored, while a lone {@code @Max} is ignored and {@code @Pattern} yields
     *         {@code null}. Do not take a randomized instance to be a valid one without validating it.
     * @see <a href="https://mtedone.github.io/podam/">PODAM</a>
     * @see ___OfEasyRandom
     * @see ___OfInstancio
     * @see ___OfFixtureMonkey
     * @see uk.co.jemos.podam.common.BeanValidationStrategy
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
     * <a href="https://github.com/j-easy/easy-random">Easy Random</a>.
     * <p>
     * This flavor assigns fields reflectively, and so populates a class which declares no accessors at all — which is
     * the ordinary shape of a JPA entity mapped with field access, and precisely the shape {@link ___OfPodam} leaves
     * untouched. Pick it for such a class, bearing in mind that it does not use {@link #newTargetInstance()}.
     * <p>
     * <strong>The constructor does run, when there is one to run.</strong> Easy Random asks the target class for a
     * declared no-argument constructor and invokes it, making it accessible where required, and falls back to Objenesis
     * — which runs no constructor and no field initializer — only when that constructor is missing or throws. A JPA
     * entity always declares one, so a collection initialized at its declaration, and anything else the constructor
     * assigns, survives the randomization. What does <em>not</em> survive is whatever only the located
     * {@link __Instantiator} would have assigned: this flavor never calls {@link #newTargetInstance()}, so the
     * constructor Easy Random invokes is the target class's own, and the instantiator is not consulted at all. Use
     * {@link ___OfPodam}, or {@link ___OfInstancio}, when the instantiator has to be honored.
     * <p>
     * <strong>Bean validation constraints are not honored, and can not be.</strong> Easy Random 6 removed its
     * constraint support outright: the {@code easy-random-bean-validation} artifact, and the
     * {@code org.jeasy.random.validation} package it carried, are gone. A class annotated with
     * {@code jakarta.validation.constraints} is randomized as if it declared no constraints at all — silently, and
     * without an error. Use {@link ___OfPodam}, which honors them by default, or {@link ___OfInstancio} or
     * {@link ___OfFixtureMonkey}, each of which honors them once configured to.
     *
     * @param <T> the type of the instances to randomize.
     * @implNote Easy Random 6 is a single artifact — {@code org.jeasy:easy-random} — replacing the 5.x
     *         {@code easy-random-core}, {@code easy-random-bean-validation}, and {@code easy-random-randomizers} trio;
     *         its constraint support, which bound {@code javax.validation.constraints} and hence honored nothing on
     *         this platform anyway, was dropped along with it. That also takes {@code javax.validation:validation-api}
     *         off the classpath for good.
     * @see <a href="https://github.com/j-easy/easy-random">Easy Random</a>
     * @see ___OfPodam
     * @see ___OfInstancio
     * @see ___OfFixtureMonkey
     */
    @SuppressWarnings({
            "java:S101" // Class names should comply with a naming convention
    })
    public abstract static class ___OfEasyRandom<T> extends __Randomizer<T> {

        /**
         * Creates a new instance for initializing a randomized instance of the specified class.
         *
         * @param targetClass    the class to be randomized.
         * @param excludedFields fields to be excluded from randomization.
         */
        public ___OfEasyRandom(final Class<T> targetClass,
                               final Iterable<String> excludedFields) {
            super(targetClass, excludedFields);
        }

//SEP8//

        /**
         * Returns the seed for the engine of the {@link #getEasyRandom() easyRandom} method.
         *
         * @return the seed.
         * @implSpec The default implementation returns a fresh value, from
         *         {@link ThreadLocalRandom#nextLong()}, on every invocation; override it, returning a constant, for a
         *         reproducible sequence.
         * @implNote An explicit seed is required here. {@link EasyRandomParameters#EasyRandomParameters()}
         *         starts at {@link EasyRandomParameters#DEFAULT_SEED}, which is a constant, and {@link #get()} builds a
         *         new engine on every invocation; left alone, every instance this randomizer ever produces would carry
         *         identical values, and a second one could not be persisted alongside the first under a unique
         *         constraint.
         * @see EasyRandomParameters#seed(long)
         */
        protected long getSeed() {
            return ThreadLocalRandom.current().nextLong();
        }

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
         * @see #getSeed()
         */
        protected EasyRandomParameters getEasyRandomParameters() {
            final var parameters = new EasyRandomParameters().seed(getSeed());
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
         * @implNote Unlike {@link ___OfPodam} and {@link ___OfInstancio}, this class does not use the
         *         {@link #newTargetInstance()} method; Easy Random instantiates the {@link #targetClass} itself,
         *         through the {@link org.jeasy.random.ObjenesisObjectFactory} which
         *         {@link EasyRandomParameters#EasyRandomParameters()} installs by default. That factory, despite its
         *         name, tries {@link Class#getDeclaredConstructor(Class[])} first, and reaches for Objenesis only when
         *         that constructor is absent or throws — verified against Easy Random 6.0.1. A value which the target
         *         class's own no-argument constructor assigns therefore survives; a value which only the located
         *         {@link __Instantiator} would assign does not. Use {@link ___OfPodam}, or {@link ___OfInstancio}, when
         *         that matters.
         * @see EasyRandom#nextObject(Class)
         */
        @Override
        public T get() {
            return getEasyRandom().nextObject(targetClass);
        }
    }

    /**
     * An abstract randomizer which randomizes instances using <a href="https://www.instancio.org">Instancio</a>.
     * <p>
     * This flavor assigns fields reflectively, and so populates a class which declares no accessors at all, yet, unlike
     * {@link ___OfEasyRandom} and {@link ___OfFixtureMonkey}, it <em>fills an instance it is handed</em>, from
     * {@link #newTargetInstance()}, and hence honors the {@link __Instantiator} located for the {@link #targetClass}.
     * It is, of the flavors which do not require accessors, the one to pick when the target class must be constructed a
     * particular way.
     * <p>
     * <strong>A value already assigned is kept.</strong> Only a {@code null} field, and a primitive still at its
     * default, is filled; whatever a constructor, or the located {@link __Instantiator}, has assigned survives. A
     * generated identifier deliberately initialized to a sentinel value is therefore preserved even when it is not
     * named in {@link #excludedFields}.
     * <p>
     * <strong>Bean validation constraints are honored only when asked for.</strong> Instancio reads
     * {@code jakarta.validation.constraints} — the Jakarta annotations, not the {@code javax} ones — but only with
     * {@link org.instancio.settings.Keys#BEAN_VALIDATION_ENABLED} set, which it is not by default. Override
     * {@link #getInstancioSettings()} to turn it on, together with {@link org.instancio.settings.Keys#JPA_ENABLED} to
     * have {@code jakarta.persistence.Column#length()} respected:
     * <pre>{@code
     * @Override
     * protected Settings getInstancioSettings() {
     *     return super.getInstancioSettings()
     *             .set(Keys.BEAN_VALIDATION_ENABLED, true)
     *             .set(Keys.JPA_ENABLED, true);
     * }
     * }</pre>
     *
     * @param <T> the type of the instances to randomize.
     * @implNote {@link Instancio#ofObject(Object)}, and the {@link InstancioObjectApi#fill() fill()} which
     *         terminates it, are marked {@link org.instancio.documentation.ExperimentalApi} as of Instancio 6.0.0; an
     *         immutable target class, whose fields a constructor assigns once, is not a fit for this flavor at all.
     *         Constraint support is keyed to the <em>field</em> by default
     *         ({@link org.instancio.settings.Keys#BEAN_VALIDATION_TARGET}), which is the right default for an entity
     *         mapped with field access.
     * @see <a href="https://www.instancio.org">Instancio</a>
     * @see ___OfPodam
     * @see ___OfEasyRandom
     * @see ___OfFixtureMonkey
     * @see org.instancio.settings.Keys#BEAN_VALIDATION_ENABLED
     * @see org.instancio.settings.Keys#JPA_ENABLED
     */
    @SuppressWarnings({
            "java:S101" // Class names should comply with a naming convention
    })
    public abstract static class ___OfInstancio<T> extends __Randomizer<T> {

        /**
         * Creates a new instance for initializing a randomized instance of the specified class.
         *
         * @param targetClass    the class to be randomized.
         * @param excludedFields fields to be excluded from randomization.
         */
        public ___OfInstancio(final Class<T> targetClass, final Iterable<String> excludedFields) {
            super(targetClass, excludedFields);
        }

//SEP8//

        /**
         * Returns the settings for the population of each instance.
         *
         * @return the settings.
         * @implSpec The default implementation returns {@link Settings#create()}, whose defaults leave both
         *         {@link org.instancio.settings.Keys#BEAN_VALIDATION_ENABLED bean validation} and
         *         {@link org.instancio.settings.Keys#JPA_ENABLED JPA} support off; override it to turn either on.
         * @see Settings#create()
         */
        protected Settings getInstancioSettings() {
            return Settings.create();
        }

        /**
         * Creates a population API for the specified instance, with {@link #excludedFields} already ignored.
         *
         * @param instance the instance to be populated.
         * @return a population API for the {@code instance}.
         * @implNote Each exclusion is narrowed to the fields which the <em>runtime</em> class of the
         *         {@code instance} declares or inherits — the located {@link __Instantiator} is explicitly allowed to
         *         return a subclass of the {@link #targetClass} — so that a field of a same name, declared on an
         *         unrelated type reached through an association, is still populated. As with {@link ___OfEasyRandom},
         *         the predicate identifies a field <em>declaration</em>, so a field which the target class and an
         *         associated type both inherit from a common supertype is ignored on both. The selector is made
         *         {@link org.instancio.LenientSelector#lenient() lenient}, since a name which matches nothing is a
         *         legitimate exclusion here — a subclass commonly passes a superset of names — while Instancio, in
         *         strict mode, would fail on an unused selector.
         * @see Instancio#ofObject(Object)
         * @see #getInstancioSettings()
         */
        protected InstancioObjectApi<T> getInstancio(final T instance) {
            return Instancio.ofObject(instance)
                    .withSettings(getInstancioSettings())
                    .ignore(Select.fields(f -> excludedFields.contains(f.getName())
                                               && f.getDeclaringClass().isAssignableFrom(instance.getClass()))
                                    .lenient());
        }

        /**
         * {@inheritDoc}
         *
         * @return {@inheritDoc}
         * @implSpec Fills the instance from {@link #newTargetInstance()}, through the
         *         {@link #getInstancio(Object) instancio} API, and returns that very instance.
         * @see InstancioObjectApi#fill()
         */
        @Override
        public T get() {
            final T instance = newTargetInstance();
            getInstancio(instance).fill();
            return instance;
        }
    }

    /**
     * An abstract randomizer which randomizes instances using
     * <a href="https://naver.github.io/fixture-monkey">Fixture Monkey</a>.
     * <p>
     * This flavor assigns fields reflectively, through the
     * {@link FieldReflectionArbitraryIntrospector field-reflection introspector}, and so populates a class which
     * declares no accessors at all. Like {@link ___OfEasyRandom}, and unlike {@link ___OfInstancio}, it does not use
     * {@link #newTargetInstance()}: the engine constructs the instance itself, so the located {@link __Instantiator}
     * has no say. It does, however, go through a <em>no-argument constructor</em> — where {@link ___OfEasyRandom}
     * bypasses constructors entirely — so a value which the no-argument constructor, or a field initializer, assigns is
     * in place before the fields are written, and an excluded field keeps it.
     * <p>
     * It is also the flavor whose builder a subclass can drive declaratively: override {@link #getArbitraryBuilder()}
     * and set, fix, or post-condition a property by name before it is sampled.
     * <p>
     * <strong>Bean validation constraints are honored only when asked for.</strong> Constraint support lives in a
     * separate plugin: add {@code com.navercorp.fixturemonkey:fixture-monkey-jakarta-validation} and register its
     * {@code JakartaValidationPlugin} by overriding {@link #getFixtureMonkey()}. The {@code javax} counterpart,
     * {@code fixture-monkey-javax-validation}, is the wrong one for this platform.
     *
     * @param <T> the type of the instances to randomize.
     * @implNote A no-argument constructor is <em>required</em>: the introspector resolves one reflectively and
     *         rethrows, unchecked, when the target class declares none. A field it can not write is logged as a warning
     *         and left alone, rather than raising an error, so — as with every flavor here — do not take a randomized
     *         instance to be a complete one without asserting on it.
     * @see <a href="https://naver.github.io/fixture-monkey">Fixture Monkey</a>
     * @see ___OfPodam
     * @see ___OfEasyRandom
     * @see ___OfInstancio
     * @see FieldReflectionArbitraryIntrospector
     */
    @SuppressWarnings({
            "java:S101" // Class names should comply with a naming convention
    })
    public abstract static class ___OfFixtureMonkey<T> extends __Randomizer<T> {

        /**
         * Creates a new instance for initializing a randomized instance of the specified class.
         *
         * @param targetClass    the class to be randomized.
         * @param excludedFields fields to be excluded from randomization.
         */
        public ___OfFixtureMonkey(final Class<T> targetClass, final Iterable<String> excludedFields) {
            super(targetClass, excludedFields);
        }

//SEP8//

        /**
         * Creates a new engine which writes fields reflectively and which generates no property named in
         * {@link #excludedFields}.
         *
         * @return a new engine.
         * @implNote The exclusions are applied by replacing the property generator for the
         *         {@link #targetClass}, and for any subclass of it
         *         ({@link com.navercorp.fixturemonkey.FixtureMonkeyBuilder#pushAssignableTypePropertyGenerator(Class,
         *         com.navercorp.fixturemonkey.api.property.PropertyGenerator) pushAssignableTypePropertyGenerator}),
         *         which drops the excluded properties before anything is generated — leaving each at whatever the
         *         no-argument constructor assigned, a primitive default included. Unlike the predicate the other
         *         flavors use, this is scoped by <em>type</em> rather than by field declaration, so a field of a same
         *         name on an unrelated associated type is unaffected, while an inherited field, which the generator
         *         enumerates among the target's properties, is excluded. Properties are enumerated with
         *         {@link DefaultPropertyGenerator#FIELD_METHOD_PROPERTY_GENERATOR}, which is what the engine would use
         *         anyway, and
         *         {@link com.navercorp.fixturemonkey.FixtureMonkeyBuilder#defaultNotNull(boolean) defaultNotNull} keeps
         *         an association from being sampled as {@code null}.
         * @see FixtureMonkey#builder()
         */
        protected FixtureMonkey getFixtureMonkey() {
            return FixtureMonkey.builder()
                    .objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE)
                    .defaultNotNull(true)
                    .pushAssignableTypePropertyGenerator(targetClass,
                                                         property -> DefaultPropertyGenerator.FIELD_METHOD_PROPERTY_GENERATOR
                                                                 .generateChildProperties(property).stream()
                                                                 .filter(child -> !excludedFields.contains(
                                                                         child.getName()))
                                                                 .toList())
                    .build();
        }

        /**
         * Creates a new builder, for the {@link #targetClass}, from the engine of the
         * {@link #getFixtureMonkey() fixtureMonkey} method.
         *
         * @return a new builder for the {@link #targetClass}.
         * @implSpec Override this method to customize an instance before it is sampled — say,
         *         {@code super.getArbitraryBuilder().set("name", "...")}.
         * @see FixtureMonkey#giveMeBuilder(Class)
         * @see #getFixtureMonkey()
         */
        protected ArbitraryBuilder<T> getArbitraryBuilder() {
            return getFixtureMonkey().giveMeBuilder(targetClass);
        }

        /**
         * {@inheritDoc}
         *
         * @return {@inheritDoc}
         * @throws NullPointerException when the engine samples {@code null}, which it does for a type it can not
         *                              introspect.
         * @implSpec Samples a new instance from the {@link #getArbitraryBuilder() arbitraryBuilder}.
         * @see ArbitraryBuilder#sample()
         */
        @Override
        public T get() {
            return Objects.requireNonNull(getArbitraryBuilder().sample(), "Fixture Monkey returned null");
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
