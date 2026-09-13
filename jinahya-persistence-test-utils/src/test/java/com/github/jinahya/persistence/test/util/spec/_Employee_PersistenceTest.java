package com.github.jinahya.persistence.test.util.spec;

/**
 * Verifies that an {@link _Employee} is <em>usable</em> through this library: instantiated, randomized and persisted by
 * the classes the naming convention locates for it.
 * <p>
 * This class declares no test of its own, deliberately. The one test it runs is
 * {@link __Spec_PersistenceTest#__persist()}, inherited, which is the whole of what a downstream project does with an
 * entity. It happens to be a demanding case: an {@link _Employee} may not be written without a {@link _Department}, and
 * the randomizer leaves that association alone on purpose, so the row only appears if the {@link _Employee_Persister}
 * really did persist a department first. Nothing else needs to be asserted here for that to be proven.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see _Employee_Instantiator
 * @see _Employee_Randomizer
 * @see _Employee_Persister
 * @see _Employee_Randomizer_Podam_Test
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class _Employee_PersistenceTest extends __Spec_PersistenceTest<_Employee> {

    _Employee_PersistenceTest() {
        super(_Employee.class);
    }
}
