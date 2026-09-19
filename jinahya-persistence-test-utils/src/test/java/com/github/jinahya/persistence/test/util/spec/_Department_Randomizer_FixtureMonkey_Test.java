package com.github.jinahya.persistence.test.util.spec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Verifies that the Fixture Monkey flavor of {@link com.github.jinahya.persistence.test.util.__Randomizer} is wired up,
 * and honors {@link _Department_Randomizer_Constants#EXCLUDED_FIELDS}, against a real entity.
 * <p>
 * Nothing is persisted here; what the contract is, and how it is checked, belong to
 * {@link _Department_Randomized_Verifier}, so that this class says only which engine it is about.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class _Department_Randomizer_FixtureMonkey_Test {

    @DisplayName("get() -> populated, with every excluded attribute left as the constructor left it")
    @Test
    void get_ExcludedFieldsLeftAlone_() {
        _Department_Randomized_Verifier.verify(new _Department_Randomizer_FixtureMonkey().get());
    }

    @DisplayName("get() -> a varying name on every call")
    @Test
    void get_Varies_() {
        _Department_Randomized_Verifier.verifyVaries(new _Department_Randomizer_FixtureMonkey());
    }
}
