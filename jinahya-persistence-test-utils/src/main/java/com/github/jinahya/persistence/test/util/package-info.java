/**
 * Interfaces and classes for testing classes/interfaces created with {@code jinahya-persistence}.
 * <p>
 * A target class is instantiated, randomized, and persisted, respectively, by an
 * {@link com.github.jinahya.persistence.test.util.__Instantiator instantiator}, a
 * {@link com.github.jinahya.persistence.test.util.__Randomizer randomizer}, and a
 * {@link com.github.jinahya.persistence.test.util.__Persister persister}, each of which is located, for the target
 * class, by a naming convention; see
 * {@link com.github.jinahya.persistence.test.util.__InstantiatorUtils#locateStandard(java.lang.Class)},
 * {@link com.github.jinahya.persistence.test.util.__RandomizerUtils#locateStandard(java.lang.Class)}, and
 * {@link com.github.jinahya.persistence.test.util.__PersisterUtils#locateStandard(java.lang.Class)} for the conventions
 * applied.
 *
 * <h2>The three roles</h2>
 * <dl>
 *   <dt>{@link com.github.jinahya.persistence.test.util.__Instantiator}</dt>
 *   <dd>Creates a bare instance. Optional; when none is located, the target class is instantiated through its
 *       no-argument constructor, which may be {@code private}. Declare one for a class which has no no-argument
 *       constructor, or which needs constructor arguments to be usable.</dd>
 *   <dt>{@link com.github.jinahya.persistence.test.util.__Randomizer}</dt>
 *   <dd>Fills an instance with random values, excluding the fields it is told to leave alone. Required by
 *       {@link com.github.jinahya.persistence.test.util.__PersisterUtils}; pick
 *       {@link com.github.jinahya.persistence.test.util.__Randomizer.___OfPodam} to keep the instantiator in play, and
 *       for the {@code jakarta.validation.constraints} it honors out of the box, or
 *       {@link com.github.jinahya.persistence.test.util.__Randomizer.___OfInstancio} to keep the instantiator in play
 *       for a class which declares no accessors, which PODAM would leave entirely unpopulated. The other two flavors,
 *       {@link com.github.jinahya.persistence.test.util.__Randomizer.___OfEasyRandom} and
 *       {@link com.github.jinahya.persistence.test.util.__Randomizer.___OfFixtureMonkey}, construct the instance
 *       themselves, and so ignore the instantiator. Of the four, Easy Random 6 is the one which can honor no Jakarta
 *       constraint at all.</dd>
 *   <dt>{@link com.github.jinahya.persistence.test.util.__Persister}</dt>
 *   <dd>Persists an instance with an {@link jakarta.persistence.EntityManager}. Override it for an entity whose
 *       required associations have to be persisted first.</dd>
 * </dl>
 *
 * <h2>Conventions</h2>
 * For a target class {@code Foo}, the convention probes {@code FooRandomizer} and then {@code Foo_Randomizer}, both
 * declared beside {@code Foo}; likewise for {@code Instantiator} and {@code Persister}. That is the whole rule, and
 * it is the same for all three roles: a counterpart is a sibling of its target class. A class nested inside another,
 * such as an {@code @Embeddable} identifier declared inside its entity, therefore has to be declared as a top-level
 * class to have a counterpart of its own.
 *
 * <h2>Example</h2>
 * Given an entity class {@code Foo}, declare, in the test source set:
 * <pre>{@code
 * class FooRandomizer extends __Randomizer.___OfEasyRandom<Foo> {
 *     FooRandomizer() {
 *         super(Foo.class, List.of("id"));  // leave the generated identifier alone
 *     }
 * }
 *
 * class FooPersister extends __Persister<Foo> {
 *     FooPersister() {
 *         super(Foo.class);
 *     }
 * }
 * }</pre>
 * and a test then obtains a randomized, persisted instance with a single call:
 * <pre>{@code
 * final var foo = __PersisterUtils.newPersistedInstanceOf(entityManager, Foo.class);
 * }</pre>
 * Note that each located class is instantiated reflectively, and so has to declare an accessible no-argument
 * constructor which supplies the target class to its superclass, exactly as above.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@org.jspecify.annotations.NullMarked
package com.github.jinahya.persistence.test.util;
