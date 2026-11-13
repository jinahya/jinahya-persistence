package com.github.jinahya.persistence.test.util.spec;

import com.github.jinahya.object.randomizer.AbstractObjectRandomizer;
import com.github.jinahya.object.randomizer.PodamObjectRandomizer;

/**
 * A PODAM randomizer of {@link _Employee}.
 * <p>
 * This class is <strong>not</strong> named by the convention that
 * {@link com.github.jinahya.object.randomizer.ObjectRandomizerUtils ObjectRandomizerUtils} probes -- which is only
 * {@code _EmployeeRandomizer} and {@code _Employee_Randomizer} -- so it is never located, and never competes with
 * {@link _Employee_Randomizer}, the one the persistence test uses. It stands beside the three other flavors so that
 * each engine has exactly one class here, and they can be read, and tested, as a set.
 * <p>
 * The instance comes from {@link AbstractObjectRandomizer#newTargetInstance()}, and is populated through its setters;
 * PODAM never assigns a field.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see _Employee_Randomizer
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public class _Employee_Randomizer_Podam extends PodamObjectRandomizer<_Employee> {

    /**
     * Creates a new instance.
     */
    public _Employee_Randomizer_Podam() {
        super(_Employee.class, _Employee_Randomizer_Constants.EXCLUDED_FIELDS);
    }

    @Override
    protected _Employee newTargetInstance() {
        return _Employee_Randomizer_Constants.hired(super.newTargetInstance());
    }
}
