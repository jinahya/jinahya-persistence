/**
 * Interfaces and classes for testing classes/interfaces created with {@code jinahya-persistence}.
 * <p>
 * A target class is instantiated, randomized, and persisted, respectively, by an
 * {@link com.github.jinahya.persistence.test.util.__Instantiator instantiator}, a
 * {@link com.github.jinahya.persistence.test.util.__Randomizer randomizer}, and a
 * {@link com.github.jinahya.persistence.test.util.__Persister persister}, each of which is located, for the target class, by
 * a corresponding locator; see {@link com.github.jinahya.persistence.test.util.__InstantiatorLocator#STANDARD},
 * {@link com.github.jinahya.persistence.test.util.__RandomizerLocator#STANDARD}, and
 * {@link com.github.jinahya.persistence.test.util.__PersisterLocator#STANDARD} for the naming conventions applied, and
 * specify a custom locator for classes which do not follow them.
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
 *       {@link com.github.jinahya.persistence.test.util.__Randomizer.___OfPodam} to keep the instantiator in play, or
 *       {@link com.github.jinahya.persistence.test.util.__Randomizer.___OfEasyRandomBean} to honor bean validation
 *       constraints.</dd>
 *   <dt>{@link com.github.jinahya.persistence.test.util.__Persister}</dt>
 *   <dd>Persists an instance with an {@link jakarta.persistence.EntityManager}. Override it for an entity whose
 *       required associations have to be persisted first.</dd>
 * </dl>
 *
 * <h2>Conventions</h2>
 * For a target class {@code Foo}, each standard locator probes {@code FooRandomizer} and {@code Foo_Randomizer}, as a
 * sibling of {@code Foo}, then {@code Foo$FooRandomizer} and {@code Foo$Foo_Randomizer}, nested inside {@code Foo};
 * likewise for {@code Instantiator} and {@code Persister}. A randomizer may also be named by a
 * {@link com.github.jinahya.persistence.test.util.__RandomizerClass @__RandomizerClass} annotation, which takes precedence
 * over the convention.
 *
 * <h2>Example</h2>
 * Given an entity class {@code Foo}, declare, in the test source set:
 * <pre>{@code
 * class FooRandomizer extends __Randomizer.___OfEasyRandomBean<Foo> {
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
//@org.jspecify.annotations.NullMarked
package com.github.jinahya.persistence.test.util;
