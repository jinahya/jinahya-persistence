package com.github.jinahya.persistence.test.util.spec;

import java.util.List;

/**
 * Constants shared by every randomizer of {@link _Department}, whichever engine it uses.
 * <p>
 * The exclusions live here, rather than on {@link _Department_Randomizer}, so that the four flavors are configured from
 * one place and can be compared on equal terms: a difference between them is then a difference in the engine, never a
 * difference in what each was told to leave alone.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see _Department_Randomized_Verifier
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class _Department_Randomizer_Constants {

    /**
     * The name of the identifier attribute; the provider's to generate, so no randomizer may touch it.
     */
    public static final String ATTRIBUTE_NAME_ID = "id";

    /**
     * The name of the inverse collection attribute; written from the other side, or not at all.
     */
    public static final String ATTRIBUTE_NAME_EMPLOYEES = "employees";

    /**
     * The name of the one attribute a randomizer is expected to fill.
     */
    public static final String ATTRIBUTE_NAME_NAME = "name";

    /**
     * The attributes every randomizer of {@link _Department} leaves alone.
     */
    public static final List<String> EXCLUDED_FIELDS = List.of(ATTRIBUTE_NAME_ID, ATTRIBUTE_NAME_EMPLOYEES);

    // -----------------------------------------------------------------------------------------------------------------
    private _Department_Randomizer_Constants() {
        throw new AssertionError("instantiation is not allowed");
    }
}
