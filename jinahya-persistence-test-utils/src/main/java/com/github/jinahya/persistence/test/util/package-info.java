/**
 * Interfaces and classes for testing classes/interfaces created with {@code jinahya-persistence}.
 * <p>
 * A target class is randomized, and then persisted, by an
 * {@link com.github.jinahya.object.randomizer.ObjectRandomizer randomizer} and an
 * {@link EntityPersister persister}, each of which is located, for the target class, by a naming convention; see
 * {@link com.github.jinahya.object.randomizer.ObjectRandomizerUtils the randomizer convention} and
 * {@link EntityPersisterUtils the persister convention} for the conventions applied.
 *
 * <h2>The two roles</h2>
 * <dl>
 *   <dt>{@link com.github.jinahya.object.randomizer.ObjectRandomizer}</dt>
 *   <dd>Fills an instance with random values, excluding the fields it is told to leave alone. Required by
 *       {@link EntityPersisterUtils}, and supplied by
 *       {@code jinahya-object-randomizer}, which declares the three engine flavors of
 *       {@link com.github.jinahya.object.randomizer.AbstractObjectRandomizer}: pick
 *       {@link com.github.jinahya.object.randomizer.PodamObjectRandomizer PodamObjectRandomizer} for an entity which
 *       exposes accessors, or
 *       {@link com.github.jinahya.object.randomizer.InstancioObjectRandomizer InstancioObjectRandomizer} for one which
 *       declares no accessors, which PODAM would leave entirely unpopulated. Those two populate the instance returned
 *       by {@code newTargetInstance()}, which an entity needing state assigned before it is randomized overrides; the
 *       third, {@link com.github.jinahya.object.randomizer.FixtureMonkeyObjectRandomizer
 *       FixtureMonkeyObjectRandomizer}, lets its engine construct the instance, and so never calls it. All three honor
 *       {@code jakarta.validation.constraints} with nothing overridden, Fixture Monkey once
 *       {@code fixture-monkey-jakarta-validation} is on the classpath, which this module declares.</dd>
 *   <dt>{@link EntityPersister}</dt>
 *   <dd>Persists an instance with an {@link jakarta.persistence.EntityManager}. Extend
 *       {@link AbstractEntityPersister} for the plain {@code persist} it brings, and override its
 *       {@code apply(entityManager, entityInstance)} for an entity whose required associations have to be persisted
 *       first; implement the interface directly for a persister which owes nothing to that machinery.</dd>
 * </dl>
 *
 * <h2>Conventions</h2>
 * For a target class {@code Foo}, the convention probes {@code FooRandomizer} and then {@code Foo_Randomizer}, both
 * declared beside {@code Foo}; likewise for {@code Persister}. That is the whole rule, and it is the same for both
 * roles: a counterpart is a sibling of its target class. A class nested inside another, such as an
 * {@code @Embeddable} identifier declared inside its entity, therefore has to be declared as a top-level class to
 * have a counterpart of its own.
 *
 * <h2>Example</h2>
 * Given an entity class {@code Foo}, declare, in the test source set:
 * <pre>{@code
 * class FooRandomizer extends PodamObjectRandomizer<Foo> {
 *     FooRandomizer() {
 *         super(Foo.class, List.of("id"));  // leave the generated identifier alone
 *     }
 * }
 *
 * class FooPersister extends AbstractEntityPersister<Foo> {
 *     FooPersister() {
 *         super(Foo.class);
 *     }
 * }
 * }</pre>
 * and a test then obtains a randomized, persisted instance with a single call:
 * <pre>{@code
 * final var foo = EntityPersisterUtils.newPersistedInstanceOf(entityManager, Foo.class);
 * }</pre>
 * Note that each located class is instantiated reflectively, and so has to declare an accessible no-argument
 * constructor which supplies the target class to its superclass, exactly as above.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@org.jspecify.annotations.NullMarked
package com.github.jinahya.persistence.test.util;
