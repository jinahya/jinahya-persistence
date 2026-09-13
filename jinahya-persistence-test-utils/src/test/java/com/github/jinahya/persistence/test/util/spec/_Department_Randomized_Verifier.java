package com.github.jinahya.persistence.test.util.spec;

import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifications of a <em>randomized</em> {@link _Department} -- of the value, that is, not of whatever produced it.
 * <p>
 * All four flavors are told to exclude the same {@link _Department_Randomizer_Constants#EXCLUDED_FIELDS}, so the values
 * they produce all owe the same contract; asserting it in one place is what lets each test say only which engine it is
 * about. A flavor whose values ever need their own expectation is a finding, not a reason to fork this class.
 * <p>
 * Nothing here takes a randomizer. {@link #verifyVaries(Supplier)} takes a {@link Supplier} because it needs more than
 * one value, and a supplier is the plainest way to ask for them; what it asserts is still a property of the values.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see _Department_Randomizer_Constants
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class _Department_Randomized_Verifier {

    /**
     * The number of instances {@link #verifyVaries(Supplier)} draws.
     */
    private static final long DRAWS = 10L;

    /**
     * Verifies a randomized instance: what is excluded is untouched, and what is not is filled.
     *
     * @param value the randomized instance to verify.
     * @return the {@code value}, so that a caller may go on asserting something of its own.
     */
    public static _Department verify(final _Department value) {
        assertThat(value).isNotNull();
        assertThat(value.getName())
                .as("'%s' is excluded from nothing, and so is filled",
                    _Department_Randomizer_Constants.ATTRIBUTE_NAME_NAME)
                .isNotNull();
        assertThat(value.getId())
                .as("'%s' is excluded; the provider generates it", _Department_Randomizer_Constants.ATTRIBUTE_NAME_ID)
                .isNull();
        assertThat(value.getEmployees())
                .as("'%s' is excluded, and keeps the empty collection the no-argument constructor assigned",
                    _Department_Randomizer_Constants.ATTRIBUTE_NAME_EMPLOYEES)
                .isNotNull()
                .isEmpty();
        return value;
    }

    /**
     * Verifies that values drawn from specified source do not all carry the same name.
     *
     * @param source a source of randomized instances; {@link #DRAWS} of them are drawn.
     * @implNote Asserts that the names are not all the same, rather than that they are all distinct: an engine
     *         may draw a very short string -- Easy Random readily draws a single character -- and two of those may
     *         legitimately collide. What would actually break the unique constraint on the column is a
     *         <em>constant</em>, and that is what this rules out.
     */
    public static void verifyVaries(final Supplier<? extends _Department> source) {
        final var names = Stream.generate(source::get)
                .limit(DRAWS)
                .map(_Department::getName)
                .collect(Collectors.toSet());
        assertThat(names).as("a constant name would fail on the second insert").hasSizeGreaterThan(1);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private _Department_Randomized_Verifier() {
        throw new AssertionError("instantiation is not allowed");
    }
}
