package com.github.jinahya.persistence.test.util.spec;

import com.github.jinahya.persistence.test.util.__Randomizer;

/**
 * A PODAM randomizer of {@link _Department}.
 * <p>
 * This class is <strong>not</strong> named by the convention that
 * {@link com.github.jinahya.persistence.test.util.__RandomizerUtils#locateStandard(Class) locateStandard} probes --
 * which is only {@code _DepartmentRandomizer} and {@code _Department_Randomizer} -- so it is never located, and never
 * competes with {@link _Department_Randomizer}. It stands on its own, beside the three other flavors, so that each
 * engine has exactly one class here and they can be read, and tested, as a set. That {@link _Department_Randomizer}
 * happens to use this same engine is a fact about that class, not a reason for this one to be absent.
 * <p>
 * The instance comes from {@link __Randomizer#newTargetInstance()}, and is populated through its setters; PODAM never
 * assigns a field.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see _Department_Randomizer
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public class _Department_Randomizer_Podam extends __Randomizer.___OfPodam<_Department> {

    /**
     * Creates a new instance.
     */
    public _Department_Randomizer_Podam() {
        super(_Department.class, _Department_Randomizer_Constants.EXCLUDED_FIELDS);
    }
}
