package com.github.jinahya.persistence.test.util.spec;

import com.github.jinahya.persistence.test.util.__Randomizer;

/**
 * A Instancio randomizer of {@link _Employee}.
 * <p>
 * This class is <strong>not</strong> named by the convention that
 * {@link com.github.jinahya.persistence.test.util.__RandomizerUtils#locateStandard(Class) locateStandard} probes --
 * which is only {@code _EmployeeRandomizer} and {@code _Employee_Randomizer} -- so it is never located, and never
 * competes with {@link _Employee_Randomizer}, the one the persistence test uses. It stands beside the three other
 * flavors so that each engine has exactly one class here, and they can be read, and tested, as a set.
 * <p>
 * The instance comes from {@link __Randomizer#newTargetInstance()}, and is filled in place; whatever the instantiator
 * assigned is retained.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see _Employee_Randomizer
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public class _Employee_Randomizer_Instancio extends __Randomizer.___OfInstancio<_Employee> {

    /**
     * Creates a new instance.
     */
    public _Employee_Randomizer_Instancio() {
        super(_Employee.class, _Employee_Randomizer_Constants.EXCLUDED_FIELDS);
    }
}
