package com.github.jinahya.persistence.test.util.spec;

/**
 * Verifies that a {@link _Department} is <em>usable</em> through this library: instantiated, randomized and persisted
 * by the classes the naming convention locates for it.
 * <p>
 * This class declares no test of its own, deliberately. The one test it runs is
 * {@link __Spec_PersistenceTest#__persist()}, inherited, which is the whole of what a downstream project does with an
 * entity: hand the class over, and get a row. Everything else about a department -- what a randomizer fills and what it
 * leaves alone, whether a written row reads back, whether two names collide -- is either this library's own behavior,
 * covered where that behavior is implemented, or the provider's. Neither is this class's to re-assert.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see _Department_Randomizer
 * @see _Department_Persister
 * @see _Department_Podam_Randomizer_Test
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class _Department_PersistenceTest extends __Spec_PersistenceTest<_Department> {

    _Department_PersistenceTest() {
        super(_Department.class);
    }
}
