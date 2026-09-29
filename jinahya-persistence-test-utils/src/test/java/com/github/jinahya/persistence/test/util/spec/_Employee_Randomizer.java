package com.github.jinahya.persistence.test.util.spec;

import com.github.jinahya.object.randomizer.PodamObjectRandomizer;

/**
 * The randomizer located, by the naming convention, for {@link _Employee}.
 * <p>
 * The PODAM flavor is chosen so that {@link #newTargetInstance()} is honored: it populates the instance that method
 * returns, where the Fixture Monkey flavor would let its engine construct one, never calling the override, and so lose
 * the hire date this model depends on.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see PodamObjectRandomizer
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public class _Employee_Randomizer extends PodamObjectRandomizer<_Employee> {

    /**
     * Creates a new instance.
     *
     * @apiNote The no-argument constructor is what {@code ObjectRandomizerUtils} instantiates a located
     *         randomizer by.
     */
    public _Employee_Randomizer() {
        super(_Employee.class, _Employee_Randomizer_Constants.EXCLUDED_FIELDS);
    }

    /**
     * {@inheritDoc}
     *
     * @return a new {@link _Employee} whose hire date is {@link _Employee_Randomizer_Constants#HIRE_DATE}.
     * @implSpec Takes the instance from {@code super.newTargetInstance()}, which uses the no-argument
     *         constructor, and assigns the hire date to it; the attribute is excluded from randomization, so that value
     *         is the one which reaches the database.
     */
    @Override
    protected _Employee newTargetInstance() {
        return _Employee_Randomizer_Constants.hired(super.newTargetInstance());
    }
}
