package com.github.jinahya.persistence.test.util.spec;

import com.github.jinahya.persistence.test.util.__Randomizer;

/**
 * The randomizer located, by the naming convention, for {@link _Employee}.
 * <p>
 * The PODAM flavor is chosen so that the {@link _Employee_Instantiator} is honored: it populates the instance that
 * {@code newTargetInstance()} returns, where the Easy Random flavor would build its own, bypassing the constructor and
 * the instantiator alike, and so lose the hire date this model depends on.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __Randomizer.___OfPodam
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public class _Employee_Randomizer extends __Randomizer.___OfPodam<_Employee> {

    /**
     * Creates a new instance.
     *
     * @apiNote The no-argument constructor is what {@code __RandomizerUtils} instantiates a located randomizer
     *         by.
     */
    public _Employee_Randomizer() {
        super(_Employee.class, _Employee_Randomizer_Constants.EXCLUDED_FIELDS);
    }
}
