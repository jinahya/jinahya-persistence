package com.github.jinahya.persistence.test.util.spec;

import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifications of a <em>randomized</em> {@link _Employee} -- of the value, that is, not of whatever produced it.
 * <p>
 * The contract splits in two, and the split is the interesting part of this entity. Everything in
 * {@link #verify(_Employee)} holds for all four flavors. The {@code hireDate} does not: only a flavor which populates
 * the instance from
 * {@link com.github.jinahya.persistence.test.util.__Randomizer#newTargetInstance() newTargetInstance()} sees what
 * {@link _Employee_Instantiator} assigned, and the other two, which construct the instance themselves, leave it
 * {@code null}. That is what {@link #verifyInstantiated(_Employee)} is for, and why only two of the four tests call
 * it.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see _Employee_Randomizer_Constants
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class _Employee_Randomized_Verifier {

    /**
     * The number of instances {@link #verifyVaries(Supplier)} draws.
     */
    private static final long DRAWS = 10L;

    /**
     * Verifies what every flavor owes: the excluded attributes are untouched, and the rest are filled.
     *
     * @param value the randomized instance to verify.
     * @return the {@code value}, so that a caller may go on asserting something of its own.
     */
    public static _Employee verify(final _Employee value) {
        assertThat(value).isNotNull();
        assertThat(value.getName()).as("excluded from nothing, and so filled").isNotNull();
        assertThat(value.getSalary()).as("excluded from nothing, and so filled").isNotNull();
        assertThat(value.getAddress())
                .as("an embedded value is reached through the attribute which holds it, and is filled too")
                .isNotNull()
                .satisfies(a -> assertThat(a.getStreet()).isNotNull());
        assertThat(value.getId()).as("excluded; the provider generates it").isNull();
        assertThat(value.getVersion()).as("excluded; the provider assigns it on insert").isNull();
        assertThat(value.getDepartment()).as("excluded; the persister persists one and assigns it").isNull();
        assertThat(value.getManager())
                .as("excluded; being of this very type, it is where an engine would recurse")
                .isNull();
        return value;
    }

    /**
     * Verifies, in addition to {@link #verify(_Employee)}, that the value carries what the instantiator assigned.
     *
     * @param value the randomized instance to verify.
     * @return the {@code value}.
     * @apiNote Only for a flavor which populates the instance from {@code newTargetInstance()}; a flavor which
     *         constructs its own leaves the {@code hireDate} {@code null}, and is not wrong to.
     */
    public static _Employee verifyInstantiated(final _Employee value) {
        verify(value);
        assertThat(value.getHireDate())
                .as("excluded from randomization, so what _Employee_Instantiator assigned survives")
                .isEqualTo(_Employee_Instantiator.HIRE_DATE);
        return value;
    }

    /**
     * Verifies that values drawn from specified source do not all carry the same name.
     *
     * @param source a source of randomized instances; {@link #DRAWS} of them are drawn.
     */
    public static void verifyVaries(final Supplier<? extends _Employee> source) {
        final var names = Stream.generate(source::get)
                .limit(DRAWS)
                .map(_Employee::getName)
                .collect(Collectors.toSet());
        assertThat(names).as("a constant name would make every row identical").hasSizeGreaterThan(1);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private _Employee_Randomized_Verifier() {
        throw new AssertionError("instantiation is not allowed");
    }
}
