package com.github.jinahya.persistence.test.util.spec;

import java.time.LocalDate;
import java.util.List;

/**
 * Constants shared by every randomizer of {@link _Employee}, whichever engine it uses.
 * <p>
 * The exclusions live here, rather than on {@link _Employee_Randomizer}, so that the three flavors are configured from
 * one place and can be compared on equal terms: a difference between them is then a difference in the engine, never a
 * difference in what each was told to leave alone. The {@link #HIRE_DATE} is here for the same reason: it is what a
 * flavor which populates the instance from {@code newTargetInstance()} assigns before the engine runs.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see _Employee_Randomized_Verifier
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class _Employee_Randomizer_Constants {

    /**
     * The hire date a randomizer assigns in {@code newTargetInstance()}; a constant, so that a test can assert it.
     */
    public static final LocalDate HIRE_DATE = LocalDate.of(2026, 1, 1);

    /**
     * Assigns, to the specified instance, what no engine is responsible for.
     *
     * @param instance a newly constructed instance.
     * @return the {@code instance}.
     * @apiNote This is what an overriding
     *         {@link com.github.jinahya.object.randomizer.AbstractObjectRandomizer#newTargetInstance()
     *         newTargetInstance()} calls. The {@code hireDate} is excluded from randomization, so the value assigned
     *         here is the value which reaches the database; keeping it out of the engines also keeps {@code java.time}
     *         out of them, which they support unevenly.
     */
    public static _Employee hired(final _Employee instance) {
        instance.setHireDate(HIRE_DATE);
        return instance;
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The attributes every randomizer of {@link _Employee} leaves alone, each for a different owner: {@code id} and
     * {@code version} belong to the provider, {@code hireDate} to {@link #hired(_Employee)}, and {@code department} and
     * {@code manager} to the {@link _Employee_Persister}, which has to persist an association before it can be
     * referenced.
     */
    public static final List<String> EXCLUDED_FIELDS = List.of(
            _Employee.ATTRIBUTE_NAME_ID,
            _Employee.ATTRIBUTE_NAME_VERSION,
            _Employee.ATTRIBUTE_NAME_HIRE_DATE,
            _Employee.ATTRIBUTE_NAME_DEPARTMENT,
            _Employee.ATTRIBUTE_NAME_MANAGER
    );

    // -----------------------------------------------------------------------------------------------------------------
    private _Employee_Randomizer_Constants() {
        throw new AssertionError("instantiation is not allowed");
    }
}
