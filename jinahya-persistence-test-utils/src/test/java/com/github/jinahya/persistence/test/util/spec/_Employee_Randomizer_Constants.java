package com.github.jinahya.persistence.test.util.spec;

import java.util.List;

/**
 * Constants shared by every randomizer of {@link _Employee}, whichever engine it uses.
 * <p>
 * The exclusions live here, rather than on {@link _Employee_Randomizer}, so that the four flavors are configured from
 * one place and can be compared on equal terms: a difference between them is then a difference in the engine, never a
 * difference in what each was told to leave alone.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see _Employee_Randomized_Verifier
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class _Employee_Randomizer_Constants {

    /**
     * The attributes every randomizer of {@link _Employee} leaves alone, each for a different owner: {@code id} and
     * {@code version} belong to the provider, {@code hireDate} to the {@link _Employee_Instantiator}, and
     * {@code department} and {@code manager} to the {@link _Employee_Persister}, which has to persist an association
     * before it can be referenced.
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
