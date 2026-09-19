package com.github.jinahya.persistence.test.util.spec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Verifies that the Easy Random flavor of {@link com.github.jinahya.persistence.test.util.__Randomizer} is wired up,
 * and honors {@link _Employee_Randomizer_Constants#EXCLUDED_FIELDS}, against a real entity.
 * <p>
 * Nothing is persisted here; what the contract is, and how it is checked, belong to
 * {@link _Employee_Randomized_Verifier}, so that this class says only which engine it is about.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class _Employee_Randomizer_EasyRandom_Test {

    @DisplayName("get() -> populated, with every excluded attribute left to its owner")
    @Test
    void get_ExcludedFieldsLeftAlone_() {
        _Employee_Randomized_Verifier.verify(new _Employee_Randomizer_EasyRandom().get());
    }

    @DisplayName("get() -> a varying name on every call")
    @Test
    void get_Varies_() {
        _Employee_Randomized_Verifier.verifyVaries(new _Employee_Randomizer_EasyRandom());
    }
}
